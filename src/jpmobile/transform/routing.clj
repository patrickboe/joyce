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
(ns jpmobile.transform.routing
  (:require
    [clojure.string :as st]))

(defn path-parts [p]
  (st/split p #"/"))

(defn tokenized-filename [path]
  (st/split (last (path-parts path)) #"\."))

(defn extension [path]
  (st/lower-case (last (tokenized-filename path))))

(defn docname [path]
  (first (tokenized-filename path)))

(defn subdir-router [dir]
  (fn [target path]
    (str target "/" dir "/" (docname path) ".html")))

(def route-chapter (subdir-router "chapters"))

(def route-note (subdir-router "notes"))

(def route-info (subdir-router "info"))

(defn route-index [target] (str target "/index.html"))

(defn image? [path]
  (case (extension path) ("jpg" "png" "gif" "jpeg") true false))

(defn reroot-with [source target f]
  (let [source-pattern (re-pattern (str source "(.+)"))]
    (fn [path]
      (let [[_ inner] (re-find source-pattern path)]
        (if-let [newinner (f inner)] (str target "/" newinner))))))

(defn difmap [f xs]
  (map (fn [x] (list x (f x))) xs))

(defn rename-img-dir [d]
  (case d
    "images" "fullsize"
    "thumbs" "thumbnail"
    d))

(defn rewrite-img-url [url]
  (if-let [[_ ep img] (re-find #"episode_(\d+)_images/(.+)" url)]
    (let [ [d & r] (path-parts img)
          nudoc (st/join "/" (cons (rename-img-dir d) r)) ]
      (st/lower-case (str "images/for-chapter/" ep "/" nudoc)))))

(defn has-target? [[s t]] t)

(defn route-images [source target]
  (let [rt (reroot-with source target rewrite-img-url)]
    #(->> %
      (filter image?)
      (difmap rt)
      (filter has-target?))))

(def source-chapters #(str % "/chap/"))

(def source-notes #(str % "/notes/"))

(defn html? [{n :name c :content}]
  (re-matches #".+\.(html?|asp|php)" n))

(defn people? [{n :name c :content}] (= n "people.php"))

(defn times? [{n :name c :content}] (= n "times.php"))

(def source-images #(str % "/notes/"))

(def source-infos #(str % "/pages/"))

(def source-js #(str % "/scripts/swap/"))

(defn root-relative [url] (str "/" url))

(defn root-relative-from-subdir [subdir]
  (fn [url] (str "/" subdir "/" url)))

(defn rewrite-for [dir]
  (comp
    root-relative
    (fn [url]
      (case (extension url)
        "htm" (str dir url "l")
        ("jpg" "png" "gif" "jpeg") (rewrite-img-url url)
        (str dir url)))))

(defrecord Linker [link-chapter rewrite-url])

(defn linkers []
  (let [linker-from
        (fn [subdir]
          (comp (root-relative-from-subdir subdir)
                #(str % ".html"))) ]

    { :chapter->url (linker-from "chapters")
      :info->url (linker-from "info")
      :resource root-relative
      :rewrite-from-chapter (rewrite-for "")
      :rewrite-from-note (rewrite-for "notes/") }))
