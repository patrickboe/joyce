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

(defn route-chapter [target path]
  (str target "/chapters/" (docname path) ".html")) 

(defn route-note [target path]
  (str target "/notes/" (docname path) ".html") )

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

(def source-images #(str % "/notes/"))

(defn chapter-name [path] (docname path))

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
  (let [chapter->url (comp (make-protocol-relative (str host "/chapters")) #(str % ".html"))]
   { :chapters
     (Linker. chapter->url (rewrite-for host ""))
     :notes
     (Linker. chapter->url (rewrite-for host "notes/")) }))
