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

(def source-chapters #(str % "/chap/"))

(def source-notes #(str % "/notes/"))

(defn chapter-name [path] (docname path))

(defn rename-img-dir [d]
  (case d
    "images" "fullsize"
    "thumbs" "thumbnail"
    d))

(defn rewrite-img-url [url]
  (let [ [_ ep img] (re-find #"episode_(\d+)_images/(.+)" url)
         [d & r] (path-parts img)
         nudoc (st/join "/" (cons (rename-img-dir d) r)) ]
    (str "images/for-chapter/" ep "/" nudoc)))

(defn rewrite-url [url]
  (case (extension url)
    "htm" (str url "l")
    ("jpg" "png" "gif" "jpeg") (rewrite-img-url url)
    url))

(defn make-protocol-relative [host]
  (fn [url] (str "//" host "/" url)))

(defn linker [host]
  (comp (make-protocol-relative host) rewrite-url))
