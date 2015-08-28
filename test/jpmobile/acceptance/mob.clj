(ns jpmobile.acceptance.mob
  (:import (org.openqa.selenium.remote CapabilityType DesiredCapabilities)
           (org.openqa.selenium.chrome ChromeDriver)
           (net.lightbody.bmp.client ClientUtil)
           (net.lightbody.bmp BrowserMobProxyServer))
  (:require [clojure.test :refer :all]
            [clj-webdriver.driver :refer [init-driver]]
            [clojure.java.io :refer [file]]
            [clj-webdriver.taxi :refer :all]))

(def test-host "05093a3aa61c2575bf27-bce33873b3e004c2e98272b24eb2f01a.r94.cf5.rackcdn.com")

(def test-base-url (str "http://" test-host "/chapters/telem.html"))

(defn proxied-chrome-driver [bm-proxy]
  (let [prox (ClientUtil/createSeleniumProxy bm-proxy)
        caps (doto (new DesiredCapabilities)
                   (.setCapability (CapabilityType/PROXY) prox)) ]
    (new ChromeDriver caps)))

(deftest ^:acceptance homepage-greeting
  (let [bmproxy (new BrowserMobProxyServer)]
    (.start bmproxy)
    (try
      (set-driver! (init-driver (proxied-chrome-driver bmproxy)))
      (try
        (.newHar bmproxy "joyce-bench")
        (to test-base-url)
        (.writeTo (.getHar bmproxy) (file "joyce.har"))
        (finally (quit)))
      (finally (.stop bmproxy)))))
