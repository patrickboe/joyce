(ns tool
  (:require [net.cgrand.enlive-html :as en]
            [rendering :as render] [routing :as rt]
            [files] [nav] [note] [chapter] [info] [codes]))

(def source "/home/patrick/dev/proj/joyce/orig")

(def target "/home/patrick/dev/proj/joyce/dist")

(def linkers (rt/linkers (str "localhost" target)))

(defn make-direction [rewrite route]
  (fn [t data nav]
    (fn [{n :name, c :content, :as file}]
      (struct files/finfo
              (route t n)
              ((render/rerender (rewrite data nav file)) c)))))

(defn direct [[note-files info-files chapter-files]]
  (let [nav (nav/construct codes/site-data (:chapter->url linkers))

        directions
          (map make-direction
               [(note/rewrite-note (:rewrite-from-note linkers))
                (info/rewrite-info-page identity)
                (chapter/rewrite-chapter (:rewrite-from-chapter linkers))]
               [rt/route-note rt/route-info rt/route-chapter])

        sources
          [note-files (filter rt/info-file? info-files) chapter-files]

        direct-from-source
          (fn [dir source] (map (dir target codes/site-data nav) source)) ]

    (mapcat direct-from-source directions sources)))

(def calc-sources
  (juxt rt/source-notes rt/source-infos rt/source-chapters))

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
       ((rt/route-images source target))
       (map (partial apply files/cp))
       dorun))

(defn exec [] (migrate-text) (migrate-assets))
