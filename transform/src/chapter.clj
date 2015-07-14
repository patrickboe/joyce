(ns chapter
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

(defn make-protocol-relative [site]
  (fn [n]
    (let [href (:href (:attrs n))]
      (assoc-in n [:attrs :href] (str "//" site "/" href)))))

(def categorize identity)

(defn lookup [id db] (db id))

(defn apply-link-category [database]
  (fn [n]
    (let [id (:id (:attrs n))]
      ((en/add-class (categorize (lookup id database))) n))))

(en/defsnippet chapter-head "head.html" [:head] [context]
  [:title] (en/content (:title context))
  [[:meta (en/attr? :description)]] (en/set-attr :content (:description context)))

(defn rewrite-chapter [site database title]
  (let [situate (make-protocol-relative site)
        code-link (apply-link-category database)]
    (en/transformation
      [:html]
      (comp (en/prepend
              (chapter-head
                { :title title,
                  :description "some description" }))
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
