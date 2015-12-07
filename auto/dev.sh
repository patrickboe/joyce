#!/bin/bash

rm -rf dist 2> /dev/null
lein run ${JOYCE_PROJECT_SOURCE} ${PWD}/dist && \
grunt dev
