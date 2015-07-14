(ns edits
  (:require [net.cgrand.enlive-html :as en]))

(defn transform-attr [attr f]
  (fn [n]
    (assoc-in n [:attrs attr] (f (attr (:attrs n))))))
