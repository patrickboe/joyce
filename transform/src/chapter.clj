(ns chapter
  (:require [net.cgrand.enlive-html :as en]
            [edits]
            [nav]
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
  (edits/transform-attr :href site))

(def categorize identity)

(defn lookup [k table db] ((db table) k))

(defn apply-link-category [database]
  (fn [n]
    (let [id (:id (:attrs n))]
      ((en/add-class (categorize (lookup id :notes database))) n))))

(en/defsnippet chapter-head "head.html" [:head] [context]
  [:title] (en/append (str " : " (:title context))))

(defn chapter-map [db]
  (into {} (:chapters db)))

(defn rewrite-chapter [linker database nav]
  (let [
        situate (situate-in (:rewrite-url linker))
        code-link (apply-link-category database)
        lookup-title (chapter-map database)
       ]
    (fn [docname]
      (let [title (lookup-title docname)]
        (en/transformation
          [:html]
          (comp (en/prepend
                  (chapter-head { :title title }))
                (en/prepend nav)
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
          cite-page)))))
