(ns tool
  (:require [net.cgrand.enlive-html :as en]
            [rendering :as render]
            [files]
            [nav]
            [note]
            [chapter]
            [routing :as rt]
            [codes]))

(def source "/home/patrick/dev/proj/joyce/orig")

(def target "/home/patrick/dev/proj/joyce/dist")

(def linkers (rt/linkers (str "localhost" target)))

(defn direct-note [t nav]
  (let [rw (note/rewrite-note (:rewrite-from-note linkers) nav)]
    (fn [{n :name c :content}]
      (struct files/finfo
              (rt/route-note t n)
              ((render/rerender rw) c)))))

(defn direct-chapter [t nav]
  (let [rw (chapter/rewrite-chapter (:rewrite-from-chapter linkers) codes/site-data nav)]
    (fn [{n :name c :content}]
      (let [nm (rt/chapter-name n)]
        (struct files/finfo
          (routing/route-chapter t n)
          ((render/rerender (rw nm)) c))))))

(defn direct [[note-files chapter-files]]
  (let [nav (nav/construct codes/site-data (:chapter->url linkers))]
    (concat
      (map (direct-note target nav) note-files)
      (map (direct-chapter target nav) chapter-files))))

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
