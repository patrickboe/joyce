(ns tool
  (:use
    [rendering]
    [routing]
    [files]
    [chapter :only [rewrite-chapter]]
    [codes]))

(def source "/home/patrick/dev/proj/joyceproject_archive")

(def target "/home/patrick/dev/proj/joyce/dist")

(def rerender-chapter
  (rerender (rewrite-chapter "m.joyceproject.com" link-codes)))

(defn direct-chapter [t]
  (fn [{n :name c :content}]
      (struct finfo (route-chapter t n) (rerender-chapter c))))

(def rewritten
  (map (direct-chapter target) (read-contents (source-chapters source))))

(def direct (partial map (direct-chapter target)))

(def write-all (partial map write-out))

(defn calc-sources [] (source-chapters source))

(def exec (comp write-all direct read-contents calc-sources))
