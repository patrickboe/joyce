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
            [jpmobile.transform.splash :as splash]
            [jpmobile.transform.coding :as coding]
            [jpmobile.transform.data :as data]))

(defn deploy [sourcedir target]
  (let
    [make-direction
     (fn [rewrite route]
       (fn [t data master]
         (fn [{n :name, c :content}]
           (struct files/finfo
                   (route t n)
                   ((render/rerender (rewrite master data (rt/docname n))) c)))))

     categorize
     (fn [[note-files info-files chapter-files]]
       [
        note-files
        (filter rt/info? info-files)
        (filter rt/people? info-files)
        (filter rt/times? info-files)
        chapter-files
        ] )

     directions
     (fn [linkers]
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

     calc-sources
     (juxt rt/source-notes rt/source-infos rt/source-chapters)

     load-db
     (fn []
       (->> sourcedir
            rt/source-js
            files/read-contents
            (map (comp slurp :content))
            data/site-data))

     build-director
     (fn [db master]
       (fn [dir from] (map (dir target db master) from)))

     splash
     (fn [route nav]
       {:name (rt/route-index target)
        :content (apply str (splash/render-splash route nav))})

     migrate-text
     (fn []
       (let [linkers (rt/linkers)
             db (load-db)
             nav (nav/construct db linkers)
             master (partial master/joyce-page linkers nav)
             director (build-director db master)]
         (files/write-out (splash linkers nav))
         (->> sourcedir
              calc-sources
              (map files/read-contents)
              categorize
              (mapcat director (directions linkers))
              (map files/write-out)
              dorun)))

     migrate-assets
     (fn []
       (->> sourcedir
            rt/source-images
            files/list-contents
            ((rt/route-images sourcedir target))
            (map (partial apply files/cp))
            dorun))
     ]
    (migrate-text)
    (migrate-assets)))

(defn -main [& args]
  (apply deploy args))
