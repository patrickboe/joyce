(ns nav
  (:require [net.cgrand.enlive-html :as en]))

(en/defsnippet chapter-link "nav.html"
  [:nav :section#chapter-nav :ul :li]
  [{url :url title :title}]
  [:a] (en/do->
         (en/set-attr :href url)
         (en/content title)))

(en/defsnippet nav "nav.html" [:nav] [model]
  [:section#chapter-nav :ul]
  (en/content (map chapter-link (:chapters model))))

(defn index [db linker]
  (let [chapter-nav-model
        (fn [[docname title]]
           { :url ((:link-chapter linker) docname)
             :title title })]
    {:chapters (map chapter-nav-model (:chapters db))}))

(defn construct [db linker] (nav (index db linker)))
