(defproject jpmobile "0.1.0"
  :description "joyceproject.com responsive html redesign"
  :plugins [[cider/cider-nrepl "0.9.1"]]
  :main jpmobile.transform.tool
  :aot [jpmobile.transform.tool]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [commons-io/commons-io "2.4"]
                 [org.clojure/tools.trace "0.7.8"]
                 [enlive "1.1.1"]])
