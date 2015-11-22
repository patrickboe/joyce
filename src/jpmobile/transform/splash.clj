(ns jpmobile.transform.splash
  (:require
    [jpmobile.transform.edits :as ed]
    [jpmobile.transform.wrap :as wrap]
    [net.cgrand.enlive-html :as en]))

(en/defsnippet splash-content "jpmobile/template/splash.html"
  [:main] [route]
  [:img] (ed/transform-attr :src (route :resource)))

(defn render-splash [route nav]
  (wrap/joyce-wrap route "Home"
      (en/do->
        (en/add-class "home")
        (en/content (splash-content route) nav))))
