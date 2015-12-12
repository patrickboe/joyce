#!/bin/bash
set -e
source lib/setupdist
echo "running transformation"
lein run ${JOYCE_PROJECT_SOURCE} ${DIST_DIR}
echo "serving development website"
grunt dev
