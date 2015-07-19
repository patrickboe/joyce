(ns edits
  (:require [net.cgrand.enlive-html :as en]))

(defn transform-attr
  ([attr f]
   (transform-attr attr f (en/remove-attr attr)))
  ([attr f on-nil]
   (fn [n]
     (let [newval (f (attr (:attrs n)))]
       (if newval
         (assoc-in n [:attrs attr] newval)
         (on-nil n))))))
