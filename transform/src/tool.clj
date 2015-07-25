(ns tool
  (:require [net.cgrand.enlive-html :as en]
            [rendering :as render]
            [files]
            [note]
            [chapter]
            [routing :as rt]
            [codes]))

(def source "/home/patrick/dev/proj/joyce/orig")

(def target "/home/patrick/dev/proj/joyce/dist")

(def linkers (rt/linkers (str "localhost" target)))

(defn direct-note [t]
  (fn [{n :name c :content}]
    (struct files/finfo
            (rt/route-note t n)
            ((render/rerender (note/rewrite-note (:notes linkers))) c))))

(defn direct-chapter [t]
  (fn [{n :name c :content}]
      (let [title (rt/chapter-name n)]
        (struct files/finfo
          (routing/route-chapter t n)
          ((render/rerender
             (chapter/rewrite-chapter (:chapters linkers) codes/site-data title))
             c)))))

(defn direct [[note-files chapter-files]]
  (concat
    (map (direct-note target) note-files)
    (map (direct-chapter target) chapter-files)))

(defn calc-sources [s]
  (list (routing/source-notes s) (routing/source-chapters s)))

(def imgrouter (routing/route-images source target))

(defn migrate-text []
  (->> source
       calc-sources
       (map files/read-contents)
       direct
       (map files/write-out)
       dorun))

(defn migrate-assets []
  (->> source
       rt/source-images
       files/list-contents
       imgrouter
       (map (partial apply files/cp))
       dorun))

(defn exec [] (migrate-text) (migrate-assets))
