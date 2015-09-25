#!/bin/bash
rm -rf dist 2> /dev/null
lein uberjar && \
java -jar target/jpmobile-0.1.0-SNAPSHOT-standalone.jar 05093a3aa61c2575bf27-bce33873b3e004c2e98272b24eb2f01a.r94.cf5.rackcdn.com && \
./optimize-images.sh && \
grunt
