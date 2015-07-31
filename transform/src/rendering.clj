(ns rendering (:require [net.cgrand.enlive-html :as en]))

(defn render [n] (apply str (cons "<!doctype html>\n" (en/emit* n))))

(defn rerender [f]
  (comp render f en/html-resource))