(ns tool
  (:require [net.cgrand.enlive-html :as en]
            [rendering :as render] [routing :as rt]
            [files] [nav] [note] [chapter] [info] [coding] [data]))

(def source "/home/patrick/dev/proj/joyce/orig")

(def target "/home/patrick/dev/proj/joyce/dist")

(def linkers (rt/linkers (str "localhost" target)))

(defn make-direction [rewrite route]
  (fn [t data nav]
    (fn [{n :name, c :content}]
      (struct files/finfo
              (route t n)
              ((render/rerender (rewrite data nav (rt/docname n))) c)))))

(defn categorize [[note-files info-files chapter-files]]
  [
    note-files
    (filter rt/info? info-files)
    (filter rt/people? info-files)
    (filter rt/times? info-files)
    chapter-files
   ] )

(def directions
  (map make-direction
    [ (note/rewrite-note (:rewrite-from-note linkers))
      info/rewrite-info-page
      info/rewrite-people
      info/rewrite-times
      (chapter/rewrite-chapter (:rewrite-from-chapter linkers) )
     ]
    [ rt/route-note
      rt/route-info
      rt/route-info
      rt/route-info
      rt/route-chapter
    ]) )

(def calc-sources
  (juxt rt/source-notes rt/source-infos rt/source-chapters))

(defn load-db []
  (->> source
       rt/source-js
       files/read-contents
       (map (comp slurp :content))
       data/site-data))

(defn migrate-text [db]
  (let [nav (nav/construct db linkers)
        direct-from-source
        (fn [dir source] (map (dir target db nav) source))]
    (->> source
         calc-sources
         (map files/read-contents)
         categorize
         (mapcat direct-from-source directions)
         (map files/write-out)
         dorun)))

(defn migrate-assets []
  (->> source
       rt/source-images
       files/list-contents
       ((rt/route-images source target))
       (map (partial apply files/cp))
       dorun))

(defn exec []
  (migrate-text (load-db))
  (migrate-assets))
