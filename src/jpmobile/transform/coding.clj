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
(ns jpmobile.transform.coding)

(defn valid-coding-args? [[a b]]
  (not (= b "#000000")))

(def coding-re
  #"\s*document\.getElementById\(\'(\w+)\'\)\.style\.color\s*=\s*\'(#\w+)\';?\s*")

(def color-lookup
  {"#40B324" "ireland"
   "#F59627" "literature",
   "#9C632A" "dublin",
   "#AB59C2" "performances",
   "#CF2929" "bodily",
   "#307EE3" "artist"})

(defn generate-coding [id hex]
  [id (color-lookup (clojure.string/upper-case hex))])

(defn script->codings [lines]
  (map (partial apply generate-coding)
       (filter valid-coding-args?
         (map (partial drop 1)
            (keep (partial re-matches coding-re) lines)))))

(defn code-table [js]
  (into {} (script->codings (mapcat clojure.string/split-lines js))))
