(ns tool
  (:use
    [chapter :only [rewrite-chapter]]
    [codes])
  (:require
    [clojure.java.io :as io]
    [net.cgrand.enlive-html :as en]))

;;rendering

(defn render [n] (apply str (en/emit* n)))

(defn rerender [f]
  (comp render f en/html-resource))

;;routing

(defn route-chapter [x] "something")

(defn source-chapters [s] (str s "/chap/"))

;;io

(defstruct finfo :name :content)

(defn get-info [f] (struct finfo (.getName f) f))

(defn read-contents [dir]
  (map get-info (rest (file-seq (io/file dir)))))

;;composition

(def source "/home/patrick/dev/proj/joyceproject_archive")

(def rerender-chapter
  (rerender (rewrite-chapter "m.joyceproject.com" link-codes)))

(defn divert [{n :name c :content}]
  (struct finfo (route-chapter n) (rerender-chapter c)))

(def rewritten
  (map divert (read-contents (source-chapters source))))
