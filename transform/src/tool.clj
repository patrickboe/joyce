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

(def linkers (linker (str "localhost" target)))

(defn direct-note [t]
  (fn [{n :name c :content}]
    (struct finfo
            (route-note t n)
            ((rerender (rewrite-note (:notes linkers))) c))))

(defn direct-chapter [t]
  (fn [{n :name c :content}]
      (let [title (chapter-name n)]
        (struct finfo
          (route-chapter t n)
          ((rerender
             (rewrite-chapter (:chapters linkers) site-data title))
             c)))))

(defn direct [[note-files chapter-files]]
  (concat
    (map (direct-note target) note-files)
    (map (direct-chapter target) chapter-files)))

(defn calc-sources [s]
  (list (source-notes s) (source-chapters s)))

(def router (route-images source target))

(defn migrate-text []
  (->> source
       calc-sources
       (map read-contents)
       direct
       (map write-out)
       dorun))

(defn migrate-assets []
  (->> source
       source-images
       list-contents
       router
       (map (partial apply cp))
       dorun))

(defn exec [] (migrate-text) (migrate-assets))
