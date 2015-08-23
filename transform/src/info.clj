(ns info
  (:use clojure.tools.trace)
  (:require [edits]
            [net.cgrand.enlive-html :as en]))

(defn is-tag? [t] (fn [n] (= t (:tag n))))

(en/deftemplate info-page "sample.html" [node])

(def simple-tfm
  (en/transformation [:h2] nil))

(def trim-nbsp
  (comp clojure.string/trim
        (partial apply str)
        (partial remove (partial = \u00A0))))

(defn every-other [xs] (map first (partition 1 2 xs)))

(defn content-to-definitions [n]
  (let [c (:content n)
        pairs (partition-by (is-tag? :a) c)
        symbols (map (en/wrap :dt) (every-other pairs))
        defs (map (en/wrap :dd) (every-other (drop 1 pairs)))
        dl-content (interleave symbols defs)]
    (assoc n :content dl-content)))

(defn nonempty-node? [n]
  (not (and (map? n) (empty? (:content n)))))

(defn whitespace-node? [n]
  (and (string? n) (re-matches #"\s*" n)))

(defn li->def [li]
  (let [c (drop-while whitespace-node? (:content li))
        ->dt
        (en/transformation
          [:b] en/unwrap

          #{[:strong] [:a]}
          (edits/change-tag :dt))
        dt (first (->dt (first c)))
        dd {:tag :dd, :attrs {}, :content (drop-while whitespace-node? (rest c))} ]
    (list dt dd)))

(def tfm-rich-info
  (en/transformation
    [:br] nil

    [#{:strong :p :h2} en/text-node] trim-nbsp

    [[:p (en/attr-starts :style "margin-left")]]
      (en/do->
        (en/add-class "author")
        (en/remove-attr  :style)
        (edits/change-tag :header))))

(def tfm-people
    (en/transformation

      [:a] (en/do->
             (en/remove-attr :target)
             (en/add-class "intro")
             (en/remove-class "gloss"))

      [:ul.gloss]
      (en/do->
        (edits/change-tag :dl)
        (en/transform-content [:li] li->def))

      [[:p (en/attr= :style "padding-left: 100px; text-indent: 0px;")]]
      (en/do->
        (en/add-class "key")
        (en/remove-attr :style)
        (edits/change-tag :dl)
        (en/transform-content [en/whitespace] nil)
        content-to-definitions)

      [:p.biblio]
        (en/do->
          (en/remove-class "biblio")
          (edits/change-tag :cite))))

(def p->nav
  (comp (en/transformation
          [#{:strong :p}] en/unwrap)
        (en/transformation [:p :> en/text-node] nil)))

(defn extract-nav [section]
  (let [paragraphs (en/select section [:p])
        [intro [_ _ & navs]] (split-with nonempty-node? paragraphs)]
    [
     ((en/transformation [:section] (en/content intro)) section)
     {:tag :nav, :attrs { :class "periods" }, :content (p->nav navs)}]))

(defn remove-nav [n]
  (assoc n :content (take-while nonempty-node? (:content n))))

(def transform-range-body
  (en/transformation
    [:ul]
      (en/do->
        remove-nav
        (edits/change-tag :dl)
        (en/transform-content [:li] li->def))))

(defn transform-period [[header body]]
  { :tag :section,
    :attrs { :class "period" },
    :content (concat header (transform-range-body body)) })

(def transform-intro
  (en/transformation
    [:ul] (en/do-> (en/remove-class "gloss") (edits/change-tag :section))
    [:li] (en/do-> (edits/change-tag :p) (en/remove-class "gloss"))))

(defn tfm-times [n]
  (let [[title intro & body] (partition-by (is-tag? :ul) n)
        biblio (last body)
        time-periods (map transform-period (partition 2 (butlast body)))
        [clean-intro nav] (extract-nav (transform-intro intro))]
    [(simple-tfm title) clean-intro nav time-periods biblio]))

(defn info-rewriter [tfm]
  (fn [db nav doc]
    (fn [node]
      (edits/host-content
        (first (en/select node [:h2 :> en/text-node]))
        nav
        (tfm (en/select node [:div.text :> :*]))))))

(def rewrite-info-page (info-rewriter simple-tfm))

(def rewrite-times (info-rewriter (comp tfm-times tfm-rich-info)))

(def rewrite-people (info-rewriter (comp tfm-people tfm-rich-info simple-tfm)))
