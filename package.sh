#!/bin/bash

rm -rf target 2> /dev/null
lein uberjar && \
tar -zc -f target/jpmobile.tar.gz * --exclude 'target/jpmobile.tar.gz' --exclude 'orig'
