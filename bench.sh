#!/bin/bash
source lib/setupdist
lein run ${JOYCE_PROJECT_SOURCE} ${DIST_DIR} && \
source lib/optimize-images && \
exec 3< <(grunt run & echo $! > .gpid; wait)

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

runpid=$(cat .gpid)
rm .gpid
kill -2 $runpid
exec 3<&-
