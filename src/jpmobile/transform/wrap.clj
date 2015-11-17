(ns jpmobile.transform.wrap
  (:require
      [jpmobile.transform.edits :as ed]
      [net.cgrand.enlive-html :as en]))

(en/deftemplate joyce-wrap "jpmobile/template/wrap.html"
  [route title & body-content]

  [:title]
  (en/append (str " : " title))

  [:script]
  (ed/transform-attr :src (route :resource))

  [[:link en/last-of-type]]
  (ed/transform-attr :href (route :resource))

  [:body]
  (apply en/content body-content))
