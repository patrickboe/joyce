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
