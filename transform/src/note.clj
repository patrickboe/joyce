(ns note
  (:use edits)
  (:require [net.cgrand.enlive-html :as en]))

(defn relative? [url]
  (not (.isAbsolute (java.net.URI. url))))

(defn situate-in [site]
  (transform-attr
    :href
    (fn [href]
      (if (relative? href) (site href) n))))

(en/defsnippet keep-only-title "head.html" [n]
  (let [note-title (en/select n [:title en/first-child] n)]
    [:title] (en/append (str " : " note-title))))

(defn change-tag [t]
  (fn [n] (assoc n :tag t)))

(def to-caption
  (comp
    (change-tag :figcaption)
    (remove-attr :class)))

(defn situate-image [site]
  (let [situate-img-link (situate-in site)
        situate-img-src (transform-attr :src site)]
   (en/transformation
    [:a]
    situate-img-link

    [:img]
    situate-img-src)))

(defn to-figure [[img caption]]
  { :tag :figure,
    :content [(situate-image img)
              (to-caption caption)]})

(defn replace-with-figures [n]
  (map to-figure (partition 2 n)))

(def rewrite-image-section
  (en/transformation
    [:div :> :*]
    replace-with-figures))

(defn rewrite-note-text-for [site]
  (let [situate (situate-in site)]
    (en/transformation
      [:div#note]
      (comp
        (change-tag :summary)
        (en/remove-attr :id))

      [:div#expandednote]
      (comp
        (change-tag :details)
        (en/remove-attr :style))

      [:div#return]
        (comp
          (en/add-class "byline")
          (en/remove-attr :id)
          (change-tag :span))

      [:a]
      situate)))

(defn rewrite-note [site database docname]
  (let [rewrite-note-text (rewrite-note-text-for site)]

    (en/transformation
      [:html]
      (en/remove-attr :xmlns)

      [:div#button]
      (en/substitute nil)

      [:body :> *]
      (wrap :main)

      [:div.note-container]
      (comp
        unwrap
        rewrite-note-text)

      [:div#images]
      (comp unwrap
            rewrite-image-section)

      [:head]
      keep-only-title)))
