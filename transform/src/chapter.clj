(ns chapter
  (:require [net.cgrand.enlive-html :as en]
            [clojure.string :as st]
            [edits]
            [routing]))

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

(defn lookup-title [database docname]
  (lookup docname :chapters database))

(en/defsnippet chapter-head "head.html" [:head] [context]
  [:title] (en/append (str " : " (:title context))))

(en/defsnippet chapter-link "nav.html"
  [:nav :section#chapter-nav :ul :li]
  [{url :url title :title}]
  [:a] (en/do->
         (en/set-attr :href url)
         (en/content title)))

(en/defsnippet nav "nav.html" [:nav] [model]
  [:section#chapter-nav :ul]
  (en/content (map chapter-link (:chapters model))))

(defn nav-model [db linker]
  (let [chapter-nav-model (fn [[docname title]]
         { :url ((:link-chapter linker) docname)
           :title title })]
    {:chapters (map chapter-nav-model (:chapters db))}))

(defn rewrite-chapter [linker database docname]
  (let [
        situate (situate-in (:rewrite-url linker))
        code-link (apply-link-category database)
        title (lookup-title database docname)
        ]

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
