(ns tool
  (:use
    [rendering]
    [routing]
    [files]
    [chapter :only [rewrite-chapter]]
    [codes]))

(def source "/home/patrick/dev/proj/joyceproject_archive")

(def target "/home/patrick/dev/proj/joyce/dist")

(def host "home/patrick/dev/proj/joyce/dist")

(defn direct-chapter [t]
  (fn [{n :name c :content}]
      (let [title (chapter-name n)]
        (struct finfo
          (route-chapter t n)
          ((rerender
             (rewrite-chapter (linker host) site-data title))
             c)))))

(def direct (partial map (direct-chapter target)))

(def write-all (partial map write-out))

(defn calc-sources [] (source-chapters source))

(def exec (comp write-all direct read-contents calc-sources))
