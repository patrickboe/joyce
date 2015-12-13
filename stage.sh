#!/bin/bash
set -e
source lib/setupdist

echo "Running transformation"
java -jar $(ls target/*standalone.jar) ${JOYCE_PROJECT_SOURCE} ${DIST_DIR}
source lib/optimize-images
echo "Setting up staging site"
grunt run
