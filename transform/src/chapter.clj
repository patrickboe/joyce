(ns chapter
  (:use edits)
  (:require [net.cgrand.enlive-html :as en]
            [clojure.string :as st]))

(defn cite-page [n]
  (let [span-id (:id (:attrs n))
        [_ year page] (re-find #"ed(\d{4})pg(\d+)" span-id)
        title (str year " ed.")]
    { :tag :cite,
      :content [page],
      :attrs { :class "page",
               :title title}}))

(defn wrap-main [{c :content}]
    { :tag :body,
      :content [{:tag :main,
                 :content c}] })

(defn situate-in [site]
  (transform-attr :href site))

(def categorize identity)

(defn lookup [k table db] ((db table) k))

(defn apply-link-category [database]
  (fn [n]
    (let [id (:id (:attrs n))]
      ((en/add-class (categorize (lookup id :notes database))) n))))

(defn lookup-title [database docname]
  (lookup docname :chapters database))

(en/defsnippet chapter-head "head.html" [:head] [context]
  [:title] (en/append (str " : " (:title context))))

(defn rewrite-chapter [site database docname]
  (let [situate (situate-in site)
        code-link (apply-link-category database)
        title (lookup-title database docname)]

    (en/transformation
      [:html]
      (comp (en/prepend
              (chapter-head { :title title }))
            (en/set-attr :lang "en"))

      [:body]
      (comp (en/prepend {:tag :h1, :content title})
            wrap-main)

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
