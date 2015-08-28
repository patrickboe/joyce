(ns jpmobile.transform.rendering (:require [net.cgrand.enlive-html :as en]))

(defn rerender [f]
  (comp (partial apply str) f en/html-resource))
