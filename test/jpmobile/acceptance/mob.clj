(ns jpmobile.acceptance.mob
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]))

(def test-host "05093a3aa61c2575bf27-bce33873b3e004c2e98272b24eb2f01a.r94.cf5.rackcdn.com")

(def test-base-url (str "http://" test-host "/chapters/telem.html"))

(defn start-browser []
  (set-driver! {:browser :chrome}))

(defn stop-browser []
  (quit))

(deftest ^:acceptance homepage-greeting
  (start-browser)
  (to test-base-url)
  (is (= (text "body") "Hello World"))
  (stop-browser))
