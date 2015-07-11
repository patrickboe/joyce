(ns files (:require [clojure.java.io :as io]))

(defstruct finfo :name :content)

(defn get-info [f] (struct finfo (.getName f) f))

(defn read-contents [dir]
  (map get-info (rest (file-seq (io/file dir)))))

(defn write-out [{n :name c :content}]
  (spit n c))
