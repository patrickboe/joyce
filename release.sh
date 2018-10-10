#!/bin/bash
set -e
rm -rf target 2> /dev/null
VERSION=$(jq -r .version package.json)
ZIP="target/jpmobile-${VERSION}.tar.gz"
echo "creating release"
lein release
echo "zipping it up"
tar -zc -f $ZIP *.sh *.txt *.md src target/*.jar lib/*
echo "updating target version for next time"
grunt version::patch
lein do vcs commit, vcs push
