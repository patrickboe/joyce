(defproject jpmobile "0.1.0-SNAPSHOT"
  :description "joyceproject.com responsive html redesign"
  :min-lein-version "2.0.0"
  :plugins [[cider/cider-nrepl "0.9.1"]]
  :main jpmobile.transform.tool
  :aot [jpmobile.transform.tool]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [commons-io/commons-io "2.4"]
                 [org.seleniumhq.selenium/selenium-server "2.44.0"]
                 [org.seleniumhq.webdriver/webdriver-selenium "0.9.7089"]
                 [clj-webdriver "0.7.2"]
                 [org.clojure/tools.trace "0.7.8"]
                 [enlive "1.1.1"]])
