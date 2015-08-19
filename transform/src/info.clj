(ns info
  (:use clojure.tools.trace)
  (:require [edits]
            [net.cgrand.enlive-html :as en]))

(en/deftemplate info-page "sample.html" [node])

(def simple-tfm (en/transformation [:h2] nil))

(def trim-nbsp
  (comp clojure.string/trim
        (partial apply str)
        (partial remove (partial = \u00A0))))

(defn every-other [xs] (map first (partition 1 2 xs)))

(defn content-to-definitions [n]
  (let [c (:content n)
        anchor? #(= :a (:tag %))
        pairs (partition-by anchor? c)
        symbols (map (en/wrap :dt) (every-other pairs))
        defs (map (en/wrap :dd) (every-other (drop 1 pairs)))
        dl-content (interleave symbols defs)]
    (assoc n :content dl-content)))

(defn li->def [li]
  (let [c (:content li)
        dt (first ((en/transformation
                     [:b] en/unwrap
                     [:a] (edits/change-tag :dt)) (first c)))
        dd {:tag :dd, :attrs {}, :content (rest c)} ]
    (list dt dd)))

(def rich-tfm
    (en/transformation
      [:br] nil

      [:a] (en/do->
             (en/remove-attr :target)
             (en/remove-class "gloss"))

      [:ul.gloss]
      (en/do->
        (en/transform-content [en/whitespace] nil)
        (edits/change-tag :dl)
        (en/transform-content [:li] li->def))

      [[:p (en/attr= :style "padding-left: 100px; text-indent: 0px;")]]
      (en/do->
        (en/add-class "key")
        (en/remove-attr :style)
        (edits/change-tag :dl)
        (en/transform-content
          [en/whitespace] nil
          [en/text-node] trim-nbsp)
        content-to-definitions)

      [:p.biblio]
        (en/do->
          (en/remove-class "biblio")
          (edits/change-tag :cite))

      [[:p (en/attr= :style "margin-left: 220px;")]]
      (en/do->
        (en/add-class "author")
        (en/remove-attr  :style)
        (edits/change-tag :header))))

(defn info-rewriter [tfm]
  (fn [db nav doc]
    (fn [node]

      (edits/host-content
        (first (en/select node [:h2 :> en/text-node]))
        nav
        (tfm (en/select node [:div.text :> :*]))))))

(def rewrite-info-page (info-rewriter simple-tfm))

(def rewrite-rich-info (info-rewriter (comp simple-tfm rich-tfm)))

(def txt (en/select (en/html-resource (clojure.java.io/file "/home/patrick/dev/proj/joyce/orig/pages/people.php")) [:div.text] ))

;;((rewrite-info-page nil nil nil) txt)
