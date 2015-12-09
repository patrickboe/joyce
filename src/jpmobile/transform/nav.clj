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
(ns jpmobile.transform.nav
  (:require [net.cgrand.enlive-html :as en]))

(en/defsnippet link "jpmobile/template/sample.html"
  [:nav [:section en/first-of-type] :ul [:li en/first-of-type]]
  [[url title]]
  [:a] (en/do->
         (en/set-attr :href url)
         (en/content title)))

(en/defsnippet section "jpmobile/template/sample.html"
  [:nav [:section en/first-of-type]]
  [ [title link-models] ]

  [:header]
  (en/content title)

  [:ul]
  (en/content (map link link-models)))

(en/defsnippet nav "jpmobile/template/sample.html" [:nav] [section-models]
  [:nav]
  (en/content (map section section-models)))

(defn index [db linkers]
  (let [doc->link-model
        (fn [linker-sym]
          (let [linker (linker-sym linkers)]
            (fn [[docname title]] [(linker docname) title] )))

        d->c (doc->link-model :chapter->url)

        d->i (doc->link-model :info->url)

        chapter-section
        ["Chapters" (map d->c (:chapters db))]

        info-section
        (fn [[title sections]] [title (map d->i sections)]) ]

    (cons chapter-section (map info-section (:pages db)))))

(def construct (comp nav index))
