(ns edits
  (:require [net.cgrand.enlive-html :as en]))

(defn without-doctype [tf] (comp rest tf))

(defn change-tag [t]
  (fn [n] (assoc n :tag t)))

(defn transform-attr
  ([attr f]
   (transform-attr attr f (en/remove-attr attr)))
  ([attr f on-nil]
   (fn [n]
     (let [newval (f (attr (:attrs n)))]
       (if newval
         (assoc-in n [:attrs attr] newval)
         (on-nil n))))))

(def apply-html-standard
   (en/do-> (en/set-attr :lang "en")
            (en/remove-attr :xmlns)))

(en/defsnippet head-with "head.html" [:head] [d]
  [:title]
  (en/append (str " : " (:title d))))

(defn use-title-in-standard-head [n]
  (head-with { :title
               (first (en/select n [:title en/text-node])) }))
