(ns jpmobile.transform.nav
  (:require [net.cgrand.enlive-html :as en]))

(en/defsnippet link "jpmobile/template/sample.html"
  [:nav [:section en/first-of-type] :ul :li]
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
