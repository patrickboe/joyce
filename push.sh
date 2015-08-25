#!/usr/bin/env bash
turbolift -u patrickboe -a ${RACKSPACE_API_KEY} \
  --os-auth-url "https://identity.api.rackspacecloud.com/v2.0/" \
  --os-region iad upload --sync -s dist -c joyceproject
