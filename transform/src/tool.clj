(ns tool
  (:require [ net.cgrand.enlive-html :as en])
  (:use
    [rendering]
    [routing]
    [files]
    [note :only [rewrite-note]]
    [chapter :only [rewrite-chapter]]
    [codes]))

(def source "/home/patrick/dev/proj/joyceproject_archive")

(def target "/home/patrick/dev/proj/joyce/dist")

(def host "home/patrick/dev/proj/joyce/dist")

(defn direct-note [t]
  (fn [{n :name c :content}]
    (struct finfo
            (route-note t n)
            ((rerender (rewrite-note (linker host))) c))))

(defn direct-chapter [t]
  (fn [{n :name c :content}]
      (let [title (chapter-name n)]
        (struct finfo
          (route-chapter t n)
          ((rerender
             (rewrite-chapter (linker host) site-data title))
             c)))))

(defn direct [[note-files chapter-files]]
  (list
    (map (direct-note target) note-files)
    (map (direct-chapter target) chapter-files)))

(def write-all (partial map (partial map write-out)))

(defn calc-sources []
  (list (source-notes source) (source-chapters source)))

(def exec
  (comp write-all
        direct
        (partial map read-contents)
        calc-sources))

(def render-note (rerender (rewrite-note (linker host))))

(defn sample [n]
  (en/html-resource (clojure.java.io/as-file (str "/home/patrick/dev/proj/joyceproject_archive/notes/" n))))

(def dring (sample "030018dringdring.htm"))
