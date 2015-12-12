#!/bin/bash
set -e
source lib/setupdist

echo "Running transformation"
lein run ${JOYCE_PROJECT_SOURCE} ${DIST_DIR}

source lib/optimize-images

echo "Serving website"
exec 3< <(grunt run & echo $! > .gpid; wait)

read n1 <&3
runpid=$(cat .gpid)
rm .gpid
while read line; do
   echo $line
   case "$line" in
   *localhost:8000*)
      break
      ;;
   *)
      ;;
   esac
done <&3

echo "Starting benchmarks."
lein test

kill -2 $runpid
exec 3<&-
