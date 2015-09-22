(ns jpmobile.transform.tool
  (:gen-class)
  (:require [net.cgrand.enlive-html :as en]
            [jpmobile.transform.rendering :as render]
            [jpmobile.transform.routing :as rt]
            [jpmobile.transform.files :as files]
            [jpmobile.transform.nav :as nav]
            [jpmobile.transform.master :as master]
            [jpmobile.transform.note :as note]
            [jpmobile.transform.chapter :as chapter]
            [jpmobile.transform.info :as info]
            [jpmobile.transform.coding :as coding]
            [jpmobile.transform.data :as data]))

(def sourcedir "/home/patrick/dev/proj/joyce/orig")

(def target "/home/patrick/dev/proj/joyce/dist")

(defn make-direction [rewrite route]
  (fn [t data master]
    (fn [{n :name, c :content}]
      (struct files/finfo
              (route t n)
              ((render/rerender (rewrite master data (rt/docname n))) c)))))

(defn categorize [[note-files info-files chapter-files]]
  [
    note-files
    (filter rt/info? info-files)
    (filter rt/people? info-files)
    (filter rt/times? info-files)
    chapter-files
   ] )

(defn directions [linkers]
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
  (->> sourcedir
       rt/source-js
       files/read-contents
       (map (comp slurp :content))
       data/site-data))

(defn build-director [db master]
  (fn [dir from] (map (dir target db master) from)))

(defn migrate-text [hostname]
  (let [linkers (rt/linkers hostname)
        db (load-db)
        nav (nav/construct db linkers)
        master (partial master/joyce-page linkers nav)
        director (build-director db master)]
    (->> sourcedir
         calc-sources
         (map files/read-contents)
         categorize
         (mapcat director (directions linkers))
         (map files/write-out)
         dorun)))

(defn migrate-assets []
  (->> sourcedir
       rt/source-images
       files/list-contents
       ((rt/route-images sourcedir target))
       (map (partial apply files/cp))
       dorun))

(defn deploy [hostname]
  (migrate-text hostname)
  (migrate-assets))

(defn deploy->local []
  (deploy "localhost:8000"))

(defn -main [& args] (deploy (first args)))
