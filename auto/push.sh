#!/usr/bin/env bash

USER=$1
API_KEY=$2
CONTAINER=$3

turbolift -u $USER -a $API_KEY \
  --os-auth-url "https://identity.api.rackspacecloud.com/v2.0/" \
  --os-region iad upload --sync -s dist -c $CONTAINER
