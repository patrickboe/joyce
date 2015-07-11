(ns routing
  (:require [clojure.string :as st]))

(defn route-chapter [target path]
  (let [filename (last (st/split path #"/"))
        docname (first (st/split filename #"\."))]
    (str target "/chapters/" docname ".html")))

(defn source-chapters [s] (str s "/chap/"))
