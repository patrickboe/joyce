(ns routing
  (:require [clojure.string :as st]))

(defn path-parts [p]
  (st/split p #"/"))

(defn tokenized-filename [path]
  (st/split (last (path-parts path)) #"\."))

(defn extension [path]
  (second (tokenized-filename path)))

(defn docname [path]
  (first (tokenized-filename path)))

(defn subdir-router [dir]
  (fn [target path]
    (str target "/" dir "/" (docname path) ".html")))

(def route-chapter (subdir-router "chapters"))

(def route-note (subdir-router "notes"))

(def route-info (subdir-router "info"))

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
      (str "images/for-chapter/" ep "/" nudoc))))

(defn has-target? [[s t]] t)

(defn route-images [source target]
  (let [rt (reroot-with source target rewrite-img-url)]
    (fn [paths]
      (filter has-target? (difmap rt (filter image? paths))))))

(def source-chapters #(str % "/chap/"))

(def source-notes #(str % "/notes/"))

(defn info? [{n :name c :content}] (.endsWith n ".htm"))

(defn people? [{n :name c :content}] (= n "people.php"))

(defn times? [{n :name c :content}] (= n "times.php"))

(def source-images #(str % "/notes/"))

(def source-infos #(str % "/pages/"))

(def source-js #(str % "/swap/"))

(defn make-protocol-relative [host]
  (fn [url] (str "//" host "/" url)))

(defn rewrite-for [host dir]
  (comp
    (make-protocol-relative host)
    (fn [url]
      (case (extension url)
        "htm" (str dir url "l")
        ("jpg" "png" "gif" "jpeg") (rewrite-img-url url)
        (str dir url)))))

(defrecord Linker [link-chapter rewrite-url])

(defn linkers [host]
  (let [linker-from
        (fn [subdir]
          (comp (make-protocol-relative (str host "/" subdir))
                #(str % ".html"))) ]

    { :chapter->url (linker-from "chapters")
      :info->url (linker-from "info")
      :rewrite-from-chapter (rewrite-for host "")
      :rewrite-from-note (rewrite-for host "notes/") }))
