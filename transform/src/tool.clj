(ns tool
  (:require [ net.cgrand.enlive-html :as en])
  (:use
    [rendering]
    [routing]
    [files]
    [note :only [rewrite-note]]
    [chapter :only [rewrite-chapter]]
    [codes]))

(def source "/home/patrick/dev/proj/joyce/orig")

(def target "/home/patrick/dev/proj/joyce/dist")

(def tool-linker (linker (subs target 1)))

(defn direct-note [t]
  (fn [{n :name c :content}]
    (struct finfo
            (route-note t n)
            ((rerender (rewrite-note tool-linker)) c))))

(defn direct-chapter [t]
  (fn [{n :name c :content}]
      (let [title (chapter-name n)]
        (struct finfo
          (route-chapter t n)
          ((rerender
             (rewrite-chapter tool-linker site-data title))
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
