(ns info
  (:require [edits]
            [net.cgrand.enlive-html :as en]))

(defn rewrite-info-content [n]
  (let [title ((edits/change-tag :h1) (first (en/select n [:h2])))
        proto-main ((en/remove-class "text") ((edits/change-tag :main) n))
        main (first (en/at proto-main [:h2] nil))]
    [title main]))

(defn rewrite-info-page [site nav]
  (edits/without-doctype
    (en/transformation
      [:head]
      edits/use-title-in-standard-head

      [:body]
      (en/remove-attr :background)

      [:div.logo] nil

      [:div.text]
      rewrite-info-content

      [:html]
      edits/apply-html-standard)))

;;(def txt (first (en/select (en/html-resource (clojure.java.io/file "/home/patrick/dev/proj/joyce/orig/pages/aboutnotes.htm")) [:div.text] )))
