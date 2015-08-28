(ns jpmobile.transform.coding)

(defn valid-coding-args? [[a b]]
  (not (= b "#000000")))

(def coding-re
  #"\s*document\.getElementById\(\'(\w+)\'\)\.style\.color\s*=\s*\'(#\w+)\';")

(def color-lookup
  {"#40B324" "ireland"
   "#F59627" "literature",
   "#9C632A" "dublin",
   "#AB59C2" "performances",
   "#CF2929" "bodily",
   "#307EE3" "artist"})

(defn generate-coding [id hex]
  [id (color-lookup hex)])

(defn script->codings [lines]
  (map (partial apply generate-coding)
       (filter valid-coding-args?
         (map (partial drop 1)
            (keep (partial re-matches coding-re) lines)))))

(defn code-table [js]
  (into {} (script->codings (mapcat clojure.string/split-lines js))))
