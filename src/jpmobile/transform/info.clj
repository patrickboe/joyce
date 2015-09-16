(ns jpmobile.transform.info
  (:use clojure.tools.trace)
  (:require [jpmobile.transform.edits :as edits]
            [net.cgrand.enlive-html :as en]))

(defn is-tag? [t] (fn [n] (= t (:tag n))))

(en/deftemplate info-page "jpmobile/template/sample.html" [node])

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

(defn empty-node? [n]
  (and (map? n) (empty? (:content n))))

(def nonempty-node?
  (comp not empty-node?))

(defn node-matches? [re]
  (fn [n]
    (and (string? n) (re-matches re n))))

(defn citation-header? [n]
  (and (map? n) ((node-matches? #".*Works Cited.*") (first (:content n)))))

(def whitespace-node? (node-matches? #"\s*"))

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
    [:a] (en/remove-class "gloss")

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
             (en/add-class "intro"))

      [:ul.gloss]
      (en/do->
        (en/remove-class "gloss")
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
     {:tag :nav, :attrs { :class "eras" }, :content (p->nav navs)}]))

(defn canon? [n]
  (not (or (citation-header? n) (empty-node? n))) )

(defn remove-cruft [n]
  (assoc n :content (take-while canon? (:content n))))

(def normalize-body (en/transformation [:h2] nil))

(def transform-era-body
  (en/transformation
    [:ul]
      (en/do->
        remove-cruft
        (edits/change-tag :dl)
        (en/transform-content [:li] li->def))))

(def transform-era-content
  (en/transformation [:h2] (en/remove-class "era")))

(defn transform-norm-era [[header body]]
  [{ :tag :section,
     :attrs { :class "era" },
     :content (transform-era-content
                (concat header
                        (transform-era-body (normalize-body body)))) }])

(defn twelfth? [li]
  (first (en/select li [[:strong (en/has [(en/re-pred #"12.*")])]])))

(defn wrap-era [lis]
  (list {:tag :ul, :attrs {}, :content lis}))

(defn split-century [body]
  (let [[c1 c2] (split-with twelfth? (en/select body [:li]))]
    [(wrap-era c1) (wrap-era c2)]))

(defn split-era [h1 h2 body]
  (let [[b1 b2] (split-century body)] [[h1 b1] [h2 b2]]))

(defn transform-era [[header body :as era]]
  (if-let [bad-h (not-empty (en/select body [:h2]))]
    (let [[e1 e2] (split-era header bad-h (en/at body [:h2] nil))]
      (concat (transform-norm-era e1) (transform-norm-era e2)))
    (transform-norm-era era)))

(def transform-bibliography
  (en/transformation [:p] (edits/change-tag :cite)))

(def transform-intro
  (en/transformation
    [:ul] (en/do-> (en/remove-class "gloss") (edits/change-tag :section))
    [:li] (en/do-> (edits/change-tag :p) (en/remove-class "gloss"))))

(defn tfm-times [n]
  (let [[title intro & body-parts] (partition-by (is-tag? :ul) n)
        biblio (transform-bibliography (last body-parts))
        eras (mapcat transform-era (partition 2 (butlast body-parts)))
        [clean-intro nav] (extract-nav (transform-intro intro))]
    [(simple-tfm title) clean-intro nav eras biblio]))

(defn info-rewriter [tfm]
  (fn [host db doc]
    (fn [node]
      (host
        (first (en/select node [:h2 :> en/text-node]))
        (tfm (en/select node [:div.text :> :*]))))))

(def rewrite-info-page (info-rewriter simple-tfm))

(def rewrite-times (info-rewriter (comp tfm-times tfm-rich-info)))

(def rewrite-people (info-rewriter (comp tfm-people tfm-rich-info simple-tfm)))
