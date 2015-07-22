#!/bin/bash
find /home/patrick/dev/proj/joyce/dist/images -type f -print0 | grep -vz /fullsize/ | xargs -0 mogrify -quality 86 -strip
