(ns chapter
  (:require [net.cgrand.enlive-html :as en]
            [edits]
            [nav]
            [clojure.tools.trace :as tr]
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

(defn chapter-map [db]
  (into {} (:chapters db)))

(defn rewrite-chapter [linker]
  (fn [database nav docname]
    (let [situate (situate-in linker)
          code-link (apply-link-category database)
          lookup-title (chapter-map database)
          title (lookup-title docname)
          get-main #(en/select % [:body :> en/any-node])
          tfm
          (en/transformation
            [:a.box-images]
            (comp situate
                  (en/remove-attr :id)
                  code-link
                  (en/remove-class "box-images")
                  (en/set-attr :rel "sidebar"))

            [:p]
            (en/remove-class "newchapter")

            [[:span (en/attr? :id)]]
            cite-page)]
      (comp (partial edits/host-content title nav) tfm get-main))))
