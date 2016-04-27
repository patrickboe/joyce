(defproject jpmobile "0.2.2"
  :description "joyceproject.com responsive html redesign"
  :min-lein-version "2.0.0"
  :license {:name "GNU General Public License - Version 3"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "https://github.com/patrickboe/joyce"
  :plugins [[cider/cider-nrepl "0.9.1"]]
  :main jpmobile.transform.tool
  :aot [jpmobile.transform.tool]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version"
                   "leiningen.release/bump-version" "release"]
                  ["uberjar"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["change" "version" "leiningen.release/bump-version"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [commons-io/commons-io "2.4"]
                 [org.slf4j/slf4j-simple "1.7.12"]
                 [org.seleniumhq.selenium/selenium-server "2.44.0"]
                 [org.seleniumhq.webdriver/webdriver-selenium "0.9.7089"]
                 [net.lightbody.bmp/browsermob-core-littleproxy "2.1.0-beta-2"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [clj-webdriver "0.7.2"]
                 [org.clojure/tools.trace "0.7.8"]
                 [enlive "1.1.1"]])
