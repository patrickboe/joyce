#!/bin/bash
TRANSFORM_JAR=$1
ORIG_DIR=$2

rm -rf dist 2> /dev/null
java -jar $TRANSFORM_JAR $ORIG_DIR ${PWD}/dist && \
auto/optimize-images.sh && \
grunt
