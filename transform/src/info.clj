(ns info
  (:require [edits]
            [net.cgrand.enlive-html :as en]))

(en/deftemplate info-page "sample.html" [node])

(defn rewrite-info-page [db nav doc]
  (let [tfm  (en/transformation
               [:h2] nil)]
    (fn [node]
      (edits/host-content
        (first (en/select node [:h2]))
        nav
        (tfm (en/select node [:div.text]))))))

(defn rewrite-rich-info [db nav doc]
  info-page)

(def txt (en/select (en/html-resource (clojure.java.io/file "/home/patrick/dev/proj/joyce/orig/pages/people.php")) [:div.text] ))

;;((rewrite-info-page nil nil nil) txt)
