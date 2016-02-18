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
(ns jpmobile.transform.wrap
  (:require
      [jpmobile.transform.edits :as ed]
      [net.cgrand.enlive-html :as en]))

(en/deftemplate joyce-wrap "jpmobile/template/wrap.html"
  [route title body-tfm head-tfm]

  [:title]
  (en/append (str " : " title))

  [:script]
  (ed/transform-attr :src (route :resource))

  [[:link (en/attr= :rel "stylesheet") (en/but (en/attr-starts :href "http"))]]
  (ed/transform-attr :href (route :resource))

  [:head]
  head-tfm

  [:body]
  body-tfm)
