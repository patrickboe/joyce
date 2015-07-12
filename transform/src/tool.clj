(ns tool
  (:use
    [rendering]
    [routing]
    [files]
    [chapter :only [rewrite-chapter]]
    [codes]))

(def source "/home/patrick/dev/proj/joyceproject_archive")

(def target "/home/patrick/dev/proj/joyce/dist")

(defn direct-chapter [t]
  (fn [{n :name c :content}]
      (struct finfo
              (route-chapter t n)
              ((rerender
                 (rewrite-chapter "m.joyceproject.com" link-codes)) c))))

(def direct (partial map (direct-chapter target)))

(def write-all (partial map write-out))

(defn calc-sources [] (source-chapters source))

(def exec (comp write-all direct read-contents calc-sources))
