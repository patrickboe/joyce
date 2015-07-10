(ns tool
  (:use
    [chapter :only [rewrite-chapter]]
    [codes])
  (:require
    [clojure.java.io :as io]
    [net.cgrand.enlive-html :as en]))

(defn render [n] (apply str (en/emit* n)))

(def rewrite-mobile-chapter (rewrite-chapter "m.joyceproject.com" link-codes))

(defn chapters [root]
  (filter
    #(.endsWith (.getName %) ".php")
    (file-seq (io/file (str root "/chap/")))))

(def source "/home/patrick/dev/proj/joyceproject_archive")

(def rerender (comp render rewrite-mobile-chapter en/html-resource))

(def rewritten (map rerender (chapters source)))
