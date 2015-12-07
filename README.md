Running the Conversion Tool
===========================

You Need:
---------
- A bash prompt on a POSIX system
- Java
- nodejs & npm
- ImageMagick (mogrify)
- Python (required for Turbolift)
- [Turbolift](https://github.com/cloudnull/turbolift/archive/v2.1.3.zip)
  (install this by unzipping to a directory and running `python setup.py install`)

To Install:
-----------
1. `sudo npm install -g grunt-cli`
2. `npm install`
3. put a .joycerc in the project root directory which sets these
   variables:
   - `RACKSPACE_API_KEY` : Your API key for the rackspace account you
     use to host the site
   - `RACKSPACE_USER` : Your rackspace username
   - `RACKSPACE_STAGING_CONTAINER` : The name of the rackspace container
     to send the site to
   - `JOYCE_PROJECT_SOURCE` : The full path to the directory containing
     the source to the standard version of the joyceproject.com site

Commands:
---------
- `./stage.sh`: generate and stage site

Developing the Conversion Tool
==============================

You Need:
---------
- Leiningen

You May Want:
-------------
- Google Chrome and ChromeDriver - for selenium benchmarks
- LiveReload Chrome Extension - for interactive development

To Set Up a Dev Environment:
----------------------------
1. set up for running, as above
2. if you want to run the google pagespeed benchmarks from grunt, set
   an environment variable called `PAGESPEED_API_KEY`: Your google api
   key for use with the google pagespeed api

Commands:
---------
- `./dev.sh`: build the site from source, put a development version of it in the
`dist` directory, and host it in a livereload session on a lightweight webserver
- `./package.sh`: build and zip the files necessary to use the tool on a non-dev machine (produces a tarball in the target/ directory)
- `lein test`: run Selenium benchmarks on the staging location:
