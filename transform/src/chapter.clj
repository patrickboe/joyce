(ns chapter
  (:require [net.cgrand.enlive-html :as en]
            [clojure.java.io :as io]))

(def proteus (as-file "/home/patrick/dev/proj/joyce/prototype/simple/ulysses/telemachus.htm"))

(def proteus-node (en/html-resource proteus))

(defn cite-page [n]
  (let [span-id (:id (:attrs n))
        [_ year page] (re-find #"ed(\d{4})pg(\d+)" span-id)
        title (str year " ed.")]
    { :tag :cite,
      :content [page],
      :attrs { :class "page",
               :title title}}))

(defn make-protocol-relative [site]
  (fn [n]
    (let [href (:href (:attrs n))]
      (assoc-in n [:attrs :href] (str "//" site "/" href)))))

(defn rewrite-chapter [site]
  (let [situate (make-protocol-relative site)]
    (en/transformation

      [:a]
      (comp situate (en/remove-attr :id))

      [:a.box-images]
      (comp (en/remove-class "box-images") (en/set-attr :rel "sidebar"))

      [:p]
      (en/remove-class "newchapter")

      [[:span (en/attr? :id)]]
      cite-page)))

(defn render [n] (apply str (en/emit* n)))

(def rewrite-mobile-chapter (rewrite-chapter "m.joyceproject.com"))
