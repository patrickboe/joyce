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
(ns jpmobile.transform.chapter
  (:require [net.cgrand.enlive-html :as en]
            [jpmobile.transform.edits :as edits]
            [clojure.tools.trace :as tr]
            [clojure.string :as st]))

(defn cite-page [n]
  (let [attrs (:attrs n)
        edition (:data-edition attrs)
        [_ year] (re-find #"ed(\d{4})" edition)
        page (:data-page attrs)
        title (str year " ed.")]
    { :tag :cite,
      :content [page],
      :attrs { :class "page",
               :title title}}))

(defn situate-in [site]
  (edits/transform-attr :href site))

(def categorize identity)

(defn lookup [k table db] ((db table) k))

(defn apply-link-category [database]
  (fn [n]
    (let [id (:id (:attrs n))]
      ((en/add-class (categorize (lookup id :notes database))) n))))

(defn chapter-map [db]
  (into {} (:chapters db)))

(defn remove-classes [cs]
  (apply comp (map en/remove-class cs)))

(defn rewrite-chapter [linker]
  (fn [host database docname]
    (let [situate (situate-in linker)
          code-link (apply-link-category database)
          lookup-title (chapter-map database)
          title (lookup-title docname)
          get-main #(en/select % [:body :> en/any-node])
          tfm
          (en/transformation
            #{[:a.box-media] [:a.box-images-med] [:a.box-images-short] [:a.box-images]}
            (comp situate
                  (en/remove-attr :id)
                  code-link
                  (en/add-class "note")
                  (remove-classes ["box-images" "box-images-med" "box-images-short" "box-media"]))

            [:p]
            (en/do-> (en/remove-attr :style) (en/remove-class "newchapter"))

            [[:span (en/has-class "page")]]
            cite-page)]
      (comp (partial host title) tfm get-main))))
