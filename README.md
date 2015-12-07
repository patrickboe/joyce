Running the Conversion Tool
===========================

You Need:
---------
- Java
- nodejs & npm
- ImageMagick
- Turbolift (https://github.com/cloudnull/turbolift)

To Install:
-----------
1. `npm install`
2. set these environment variables:
   - `RACKSPACE_API_KEY` : Your API key for the rackspace account you
     use to host the site
   - `RACKSPACE_USER` : Your rackspace username
   - `RACKSPACE_STAGING_CONTAINER` : The name of the rackspace container
     to send the site to
   - `JOYCE_PROJECT_SOURCE` : The directory containing the source to the
     standard version of the joyceproject.com site
   - `JOYCE_MOBILE_DEST` : The directory to put the files for the mobile
     joyceproject site into for analysis and staging

To Generate Site to the `dist` directory:
---------------------------------------
`auto/dist.sh [the path to the jpmobile.jar file]

To Generate and Stage Site:
---------------------------
`auto/stage.sh`

Additional Development Tasks
============================

You Need:
---------
- Leiningen
- Google Chrome
- ChromeDriver
- LiveReload Chrome Extension

To Set Up a Dev Environment:
----------------------------
1. set up for running, as above
2. set these additional environment variables:
   - `PAGESPEED_API_KEY` : Your google api key for use with the google
     pagespeed api

To Generate a new jar for executing the site transform:
-------------------------------------------------------
`lein uberjar`

To build the site from source, put a development version of it in the
`dist` directory, and host it in a livereload session on a lightweight
webserver:
--------------------------------------------------------------------
`auto/dev.sh`

To Run Benchmarks:
------------------
`lein test`
