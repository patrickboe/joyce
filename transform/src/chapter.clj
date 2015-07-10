(ns chapter
  (:require [net.cgrand.enlive-html :as en]
            [clojure.java.io :as io]
            [clojure.string :as st]))

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

(def categorize identity)

(defn lookup [id db]
  "ireland")

(defn apply-link-category [database]
  (fn [n]
    (let [id (:id (:attrs n))]
      ((en/add-class (categorize (lookup id database))) n))))

(defn rewrite-chapter [site database]
  (let [situate (make-protocol-relative site)
        code-link (apply-link-category database)]
    (en/transformation

      [:a.box-images]
      (comp situate
            (en/remove-attr :id)
            code-link
            (en/remove-class "box-images")
            (en/set-attr :rel "sidebar"))

      [:p]
      (en/remove-class "newchapter")

      [[:span (en/attr? :id)]]
      cite-page)))

(defn render [n] (apply str (en/emit* n)))

(def rewrite-mobile-chapter (rewrite-chapter "m.joyceproject.com" nil))

(def proteus (as-file "/home/patrick/dev/proj/joyce/prototype/simple/ulysses/telemachus.htm"))

(def proteus-node (en/html-resource proteus))

