var shell = require("shelljs");

module.exports = function(grunt) {
  var webpath = function(path) {
    return "/" + path.split("/").slice(1).join("/");
  },
  paths = shell.ls(["dist/chapters/*.html","dist/notes/*.html","dist/info/*.html"]).map(webpath);

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    connect: {
      server: {
        options: {
          base: { path: 'dist', options: { maxAge: 1000 * 60 * 60 } }
        }
      }
    },
    watch: {
      css: {
        tasks: ['less:dev'],
        files: ['src/jpmobile/style/*.less']
      },
      js: {
        tasks: ['browserify'],
        files: ['src/jpmobile/script/*.js']
      },
      cp: {
        tasks: ['copy'],
        files: ['src/jpmobile/template/*']
      },
      livereload: {
        options: { livereload: true },
        files: ["dist/**/*"]
      }
    },
    copy: {
      dev: {
        files: [
          {expand: true, cwd: 'src/jpmobile/template/', src: '**', dest: 'dist/', filter: 'isFile'}
        ],
      },
    },
    cssmin: {
      dist: {
        files: {
          'dist/style/site.css' : ['stage/style/site.css']
        }
      }
    },
    less: {
      prod: {
        files: {
          "stage/style/site.css" : ["src/jpmobile/style/site.less"]
        }
      },
      dev: {
        files: {
          "dist/style/site.css" : ["src/jpmobile/style/site.less"]
        }
      }
    },
    browserify: {
      dist: {
        files: {
          'dist/script/site.js': ['src/jpmobile/script/site.js']
        }
      }
    },
    pagespeed: {
        options: {
          key: process.env.PAGESPEED_API_KEY,
          url: "http://05093a3aa61c2575bf27-bce33873b3e004c2e98272b24eb2f01a.r94.cf5.rackcdn.com"
        },
        paths: {
          options: {
            paths: paths,
            locale: "en_GB",
            strategy: "mobile",
            threshold: 90
          }
        }
    }
  });

  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

  grunt.registerTask('dev', ['connect', 'watch']);

  grunt.registerTask('default', ['less:prod', 'cssmin', 'browserify']);
};
