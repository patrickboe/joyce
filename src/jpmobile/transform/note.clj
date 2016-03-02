;;
;;Copyright 2015 Patrick Boe
;;
;;This file is part of jpmobile.
;;
;;jpmobile is free software: you can redistribute it and/or modify
;;it under the terms of the GNU General Public License as published by
;;the Free Software Foundation, either version 3 of the License, or
;;(at your option) any later version.
;;
;;jpmobile is distributed in the hope that it will be useful,
;;but WITHOUT ANY WARRANTY; without even the implied warranty of
;;MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;GNU General Public License for more details.
;;
;;You should have received a copy of the GNU General Public License
;;along with jpmobile.  If not, see <http://www.gnu.org/licenses/>.
;;
(ns jpmobile.transform.note
  (:require
    [jpmobile.transform.edits :as edits]
    [net.cgrand.enlive-html :as en]))

(defn situate-relative [site]
  (fn [href]
      (when-let [uri
                 (try (java.net.URI. href)
                      (catch java.net.URISyntaxException e nil))]
        (if (not (.isAbsolute uri)) (site href) href))))

(defn situate-in [site]
  (let [sit-rel (situate-relative site)]
    (edits/transform-attr :href sit-rel (comp first en/unwrap))))

(def to-caption
  (comp
    (edits/change-tag :figcaption)
    (en/remove-attr :class)))

(defn situate-image [site]
  (let [situate-img-link (situate-in site)
        situate-img-src (edits/transform-attr :src site)]
   (en/transformation
    [:a]
    situate-img-link

    [:img]
    situate-img-src)))

(defn rewrite-image-section [site]
  (let [situate (situate-image site)

        to-fig (fn [[{itag :tag :as img}
                     {ctag :tag cattrs :attrs :as caption}]]
           (if (and (or (= :a itag) (= :img itag)) (= :p ctag))
             [{ :tag :figure,
                    :content (list
                               (first (situate img))
                               (to-caption caption))}]
             [img caption]))

        to-figures (comp flatten #(map to-fig (partition 2 %)))]

    (fn [n] (to-figures
              (en/select
                n
                [:div :> (en/but en/text-node)])))))

(defn rewrite-note-text-for [site]
  (let [situate (situate-in site)]
    (en/transformation
      [:div#note]
      (comp
        (edits/change-tag :section)
        (en/prepend {:tag :h2 :content ["In Brief"]})
        (en/add-class "brief")
        (en/remove-attr :id))

      [:div#expandednote]
      (comp
        (edits/change-tag :section)
        (en/prepend {:tag :h2 :content ["Read More"]})
        (en/add-class "read-more")
        (en/remove-attr :id)
        (en/remove-attr :style))

      [:div#return]
        (comp
          (en/add-class "byline")
          (en/remove-attr :id)
          (edits/change-tag :span))

      [:a]
      situate)))

(defn rewrite-note [site]
   (let [rewrite-text (rewrite-note-text-for site)
         rewrite-images (rewrite-image-section site)]
     (fn [host db doc]
       (let
         [tfm (en/transformation
                [:body] en/unwrap

                [:div#button] nil

                [:div.note-container]
                (en/do-> rewrite-text en/unwrap)

                #{[:div#images] [:div#media]}
                rewrite-images)]
         (fn [node]
           (host
             (first (en/select node [:title en/text-node]))
             (tfm (en/select node [:body]))))))))
