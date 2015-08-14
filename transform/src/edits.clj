(ns edits
  (:require [net.cgrand.enlive-html :as en]))

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

(en/deftemplate joyce-page "sample.html"
  [{:keys [title nav main]}]

  [:title]
  (en/append (str " : " title))

  [:nav]
  (en/substitute nav)

  [:h1]
  (en/content title)

  [:main]
  (en/content main))

(defn host-content [title nav main-nodes]
  (joyce-page {:title title, :nav nav, :main main-nodes}))
