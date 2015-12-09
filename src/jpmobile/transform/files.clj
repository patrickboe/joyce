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
(ns jpmobile.transform.files
  (:import [org.apache.commons.io FileUtils])
  (:require [clojure.java.io :as io]))

(defstruct finfo :name :content)

(defn get-info [f] (struct finfo (.getName f) f))

(def only-files (partial filter #(.isFile %)))

(defn read-contents [dir]
  (map get-info
    (only-files
      (. (io/file dir) listFiles))))

(defn list-contents [dir]
  (map #(.getPath %)
       (only-files (file-seq (io/file dir)))))

(defn write-out [{n :name c :content}]
  (FileUtils/writeStringToFile (io/file n) c "UTF-8"))

(defn cp [from to]
  (FileUtils/copyFile (io/file from) (io/file to)))
