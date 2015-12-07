#!/bin/bash
auto/dist.sh target/jpmobile-0.1.0-SNAPSHOT-standalone.jar ${JOYCE_PROJECT_SOURCE} && \
auto/push.sh ${RACKSPACE_USER} ${RACKSPACE_API_KEY} ${RACKSPACE_STAGING_CONTAINER}
