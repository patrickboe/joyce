var shell = require("shelljs");

module.exports = function(grunt) {
  var webpath = function(path) {
    return "/" + path.split("/").slice(1).join("/");
  },
  paths = shell.ls(["dist/chapters/*.html","dist/notes/*.html","dist/info/*.html"]).map(webpath);

  console.log(paths);

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    pagespeed: {
        options: {
          key: process.env.PAGESPEED_API_KEY
          url: "http://05093a3aa61c2575bf27-bce33873b3e004c2e98272b24eb2f01a.r94.cf5.rackcdn.com"
        },
        paths: {
          options: {
            paths: paths,
            locale: "en_GB",
            strategy: "mobile",
            threshold: 80
          }
        }
    }
  });

  grunt.loadNpmTasks('grunt-pagespeed');
};
