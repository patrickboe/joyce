#!/bin/bash
find /home/patrick/dev/proj/joyce/dist/images -type f -print0 | xargs -0 mogrify -quality 86 -strip
