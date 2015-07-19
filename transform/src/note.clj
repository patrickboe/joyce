(ns note
  (:use edits)
  (:require [net.cgrand.enlive-html :as en]))

(defn relative? [url]
  (not (.isAbsolute (java.net.URI. url))))

(defn situate-in [site]
  (transform-attr
    :href
    (fn [href]
      (if (relative? href) (site href) href))))

(en/defsnippet head-with "head.html" [:head] [d]
  [:title]
  (en/append (str " : " (:title d))))

(defn use-title-in-standard-head [n]
  (head-with { :title
               (first (en/select n [:title en/text-node])) }))

(defn change-tag [t]
  (fn [n] (assoc n :tag t)))

(def to-caption
  (comp
    (change-tag :figcaption)
    (en/remove-attr :class)))

(defn wrap-content
 ([tag] (wrap-content tag nil))
 ([tag attrs]
   (fn [n]
     (assoc n :content
            (en/as-nodes (array-map
              :tag tag
              :attrs attrs
              :content (:content n)))))))

(defn situate-image [site]
  (let [situate-img-link (situate-in site)
        situate-img-src (transform-attr :src site)]
   (en/transformation
    [:a]
    situate-img-link

    [:img]
    situate-img-src)))

(defn rewrite-image-section [site]
  (let [situate (situate-image site)

        to-fig (fn [[img caption]]
             { :tag :figure,
               :content (list
                          (first (situate img))
                          (to-caption caption))})

        to-figures #(map to-fig (partition 2 %))]

    (fn [n] (to-figures
              (en/select
                n
                [:div :> (en/but en/text-node)])))))

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

(defn to-html5-doctype [[dt & r]]
  (cons (assoc dt :data ["html"]) r))

(defn rewrite-note [site]
  (let [rewrite-text (rewrite-note-text-for site)
        rewrite-images (rewrite-image-section site)]

    (comp

     rest

     (en/transformation
       [:html]
       (en/do-> (en/set-attr :lang "en")
                (en/remove-attr :xmlns))

       [:div#button] nil

       [:div.note-container]
       (en/do-> rewrite-text en/unwrap)

       [:div#images]
       rewrite-images

       [:body]
       (wrap-content :main)

       [:head]
       use-title-in-standard-head))))
