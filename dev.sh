#!/bin/bash
source lib/setupdist
lein run ${JOYCE_PROJECT_SOURCE} ${DIST_DIR} && \
grunt dev
