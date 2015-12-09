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
(ns jpmobile.transform.edits
  (:require [net.cgrand.enlive-html :as en]))

(defn change-tag [t]
  (fn [n] (assoc n :tag t)))

(defn transform-attr
  ([attr f]
   (transform-attr attr f (en/remove-attr attr)))
  ([attr f on-nil]
   (fn [n]
     (let [newval (f (attr (:attrs n)))]
       (if newval
         (assoc-in n [:attrs attr] newval)
         (on-nil n))))))
