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

(def rewrite-chapter
  (en/transformation
    [:p]
    (en/remove-class "newchapter")

    [[:span (en/attr? :id)]]
    cite-page))

(defn render [n] (apply str (en/emit* n)))
