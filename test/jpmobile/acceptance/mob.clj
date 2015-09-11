(ns jpmobile.acceptance.mob
  (:import (org.openqa.selenium.remote CapabilityType DesiredCapabilities)
           (org.openqa.selenium.chrome ChromeDriver)
           (net.lightbody.bmp.client ClientUtil)
           (net.lightbody.bmp BrowserMobProxyServer))
  (:require [clojure.test :refer :all]
            [clojure.tools.trace :refer [trace]]
            [clojure.string :refer [split join]]
            [clj-webdriver.driver :refer [init-driver]]
            [clojure.java.io :refer [file]]
            [clj-webdriver.taxi :refer :all]))

(def test-host "05093a3aa61c2575bf27-bce33873b3e004c2e98272b24eb2f01a.r94.cf5.rackcdn.com")

(defn html-in [dir]
  (filter #(.endsWith % ".html") (map #(.getPath %) (file-seq (file dir)))))

(defn webpath [filepath] (str "http://" test-host "/" (join "/" (drop 1 (split filepath #"/")))))

(def pages (trace (map webpath (mapcat html-in ["dist/chapters" "dist/notes" "dist/info"]))))

(defn proxied-chrome-driver [bm-proxy]
  (let [prox (ClientUtil/createSeleniumProxy bm-proxy)
        caps (doto (new DesiredCapabilities)
                   (.setCapability (CapabilityType/PROXY) prox)) ]
    (new ChromeDriver caps)))

(defn visit-from [bm-proxy]
  (fn [addy]
    (.newPage bm-proxy addy)
    (to addy)))

(deftest ^:acceptance collect-full-har
  (let [bmproxy (new BrowserMobProxyServer)]
    (.start bmproxy)
    (try
      (set-driver! (init-driver (proxied-chrome-driver bmproxy)))
      (try
        (.newHar bmproxy "joyce-bench")
        (dorun (map (visit-from bmproxy) pages))
        (.writeTo (.getHar bmproxy) (file "joyce.har"))
        (finally (quit)))
      (finally (.stop bmproxy)))))
