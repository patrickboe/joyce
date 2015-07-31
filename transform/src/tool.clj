(ns tool
  (:require [net.cgrand.enlive-html :as en]
            [rendering :as render] [routing :as rt]
            [files] [nav] [note] [chapter] [info] [codes]))

(def source "/home/patrick/dev/proj/joyce/orig")

(def target "/home/patrick/dev/proj/joyce/dist")

(def linkers (rt/linkers (str "localhost" target)))

;;(def make-direction rewrite route
;;  (fn [t data nav]
;;    (let [rw (rewrite data nav)]
;;      (fn [{n :name, c :content, :as file}]
;;        (struct files/finfo
;;                (route t n)
;;                ((render/rerender (rw file)) c))))))
;;
;;(map make-direction
;;     [(note/rewrite-note (:rewrite-from-note linkers))
;;      (info/rewrite-info-page identity)
;;      (chapter/rewrite-chapter (:rewrite-from-chapter linkers))]
;;     [rt/route-note rt/route-info rt/route-chapter])

(defn direct-note [t nav]
  (let [rw (note/rewrite-note (:rewrite-from-note linkers) nav)]
    (fn [{n :name c :content}]
      (struct files/finfo
              (rt/route-note t n)
              ((render/rerender rw) c)))))

(defn direct-info [t nav]
  (let [rw (info/rewrite-info-page identity nav)]
   (fn [{n :name c :content}]
    (struct files/finfo
            (rt/route-info t n)
            ((render/rerender rw) c)))))

(defn direct-chapter [t nav]
  (let [rw (chapter/rewrite-chapter (:rewrite-from-chapter linkers) codes/site-data nav)]
    (fn [{n :name c :content}]
      (let [nm (rt/chapter-name n)]
        (struct files/finfo
          (rt/route-chapter t n)
          ((render/rerender (rw nm)) c))))))

(defn direct [[note-files chapter-files note-files]]
  (let [nav (nav/construct codes/site-data (:chapter->url linkers))
        direct-from-source
          (fn [dir source] (map (dir target nav) source)) ]
    (mapcat direct-from-source
      [direct-note direct-chapter direct-info]
      [note-files chapter-files (filter rt/info-file? note-files)])))

(def calc-sources
  (juxt rt/source-notes rt/source-chapters rt/source-infos))

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
