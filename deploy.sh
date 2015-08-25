#!/bin/bash
rm -rf dist 2> /dev/null
java -jar transform/target/joyce-transform-0.1.0-standalone.jar s3.amazonaws.com/m.joyceproject.com && ./optimize-images.sh
