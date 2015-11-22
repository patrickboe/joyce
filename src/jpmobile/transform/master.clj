(ns jpmobile.transform.master
  (:require
      [jpmobile.transform.edits :as ed]
      [jpmobile.transform.wrap :as wrap]
      [net.cgrand.enlive-html :as en]))

(defn joyce-page [route nav title main]
  (wrap/joyce-wrap route title
    (en/do->
      (en/add-class "content")
      (en/content
        nav
        ((en/wrap "main") (cons {:tag :h1 :content title} main))))))
