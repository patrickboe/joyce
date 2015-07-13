(ns routing
  (:require [clojure.string :as st]))

(defn docname [path]
  (let [filename (last (st/split path #"/"))]
    (first (st/split filename #"\."))))

(defn route-chapter [target path]
  (str target "/chapters/" (docname path) ".html"))

(defn source-chapters [s] (str s "/chap/"))

(defn chapter-name [path] (docname path))
