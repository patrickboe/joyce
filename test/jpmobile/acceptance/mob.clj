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

(def pages (map webpath (mapcat html-in ["dist/chapters" "dist/notes" "dist/info"])))

(defn proxied-chrome-driver [bm-proxy]
  (let [prox (ClientUtil/createSeleniumProxy bm-proxy)
        caps (doto (new DesiredCapabilities)
                   (.setCapability (CapabilityType/PROXY) prox)) ]
    (new ChromeDriver caps)))

(defn visit-from [bm-proxy]
  (fn [addy]
    (.newPage bm-proxy addy)
    (to addy)))

(defn load-time [har-page]
  (list (.getId har-page) (.getOnLoad (.getPageTimings har-page)) ))

(defn read-load-times [bm-proxy]
  (map load-time (drop 1 (.getPages (.getLog (.endHar bm-proxy))))))

(defn at-percentile [k f xs]
  (let [n (count xs)
        pos (int (Math/floor (/ (* (+ k 1) n) 100)))]
    (nth (sort-by f xs) pos)))

(def rates {:3G {:down-Bps 100000 :up-Bps 50000 :latency-ms 100}})

(def unstyled-site-90th-percentile-load-time 1881)

(defn make-3G-network-simulator []
  (let [rate (rates :3G)]
    (doto (new BrowserMobProxyServer)
      (.start)
      (.setReadBandwidthLimit (rate :down-Bps))
      (.setWriteBandwidthLimit (rate :up-Bps))
      (.setLatency (rate :latency-ms) java.util.concurrent.TimeUnit/MILLISECONDS))))

(defn pages-should-have-loaded-quickly [bmproxy]
  (let [load-time-90th-percentile (at-percentile 90 second (read-load-times bmproxy))
      threshold (long (Math/ceil (* 1.1 unstyled-site-90th-percentile-load-time)))]
  (is (< (second load-time-90th-percentile) threshold)
      (str "Load time threshold for 90th percentile page exceeded at " load-time-90th-percentile))))

(deftest ^:acceptance collect-full-har
  (let [bmproxy (make-3G-network-simulator)]
    (try
      (set-driver! (init-driver (proxied-chrome-driver bmproxy)))
      (try
        (.newHar bmproxy)
        (dorun (map (visit-from bmproxy) pages))
        (pages-should-have-loaded-quickly bmproxy)
        (finally (quit)))
      (finally (.stop bmproxy)))))
