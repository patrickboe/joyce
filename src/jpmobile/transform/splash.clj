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
        (en/content (splash-content route) nav))
      (en/append {:tag :link :attrs {:rel "canonical" :href "http://www.joyceproject.com"}})))
