(ns jpmobile.transform.master
  (:require
      [jpmobile.transform.edits :as ed]
      [net.cgrand.enlive-html :as en]))

(en/deftemplate joyce-page "jpmobile/template/sample.html"
    [route nav title main]

    [:title]
    (en/append (str " : " title))

    [:script]
    (ed/transform-attr :src (route :resource))

    [:link]
    (ed/transform-attr :href (route :resource))

    [:nav]
    (en/substitute nav)

    [:main]
    (en/content (cons {:tag :h1, :content title} main)))
