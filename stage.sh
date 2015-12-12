#!/bin/bash
set -e
source lib/setupdist

java -jar $(ls target/*standalone.jar) \
${JOYCE_PROJECT_SOURCE} ${DIST_DIR}
source lib/optimize-images
grunt
turbolift -u ${RACKSPACE_USER} -a ${RACKSPACE_API_KEY} \
  --os-auth-url "https://identity.api.rackspacecloud.com/v2.0/" \
  --os-region iad upload --sync -s ${DIST_DIR} -c ${RACKSPACE_STAGING_CONTAINER}
