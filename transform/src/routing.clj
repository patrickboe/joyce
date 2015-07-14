(ns routing
  (:require [clojure.string :as st]))

(defn docname [path]
  (let [filename (last (st/split path #"/"))]
    (first (st/split filename #"\."))))

(defn route-chapter [target path]
  (str target "/chapters/" (docname path) ".html"))

(defn source-chapters [s] (str s "/chap/"))

(defn chapter-name [path] (docname path))

(defn use-html-extension [href] (str href "l"))

(defn make-protocol-relative [host]
  (fn [href] (str "//" host "/" href)))

(defn linker [host]
  (comp use-html-extension (make-protocol-relative host)))
