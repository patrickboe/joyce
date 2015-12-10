var shell = require("shelljs");

module.exports = function(grunt) {
  var webpath = function(path) {
    return "/" + path.split("/").slice(1).join("/");
  },
  oneHourInMs =1000 * 60 * 60,
  paths = shell.test('-d','target/dist/chapters') &&
    shell.ls(["target/dist/chapters/*.html","target/dist/notes/*.html","target/dist/info/*.html"]).map(webpath);

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    connect: {
      server: {
        options: {
          base: { path: 'target/dist', options: { maxAge: 0 } }
        }
      }
    },
    watch: {
      css: {
        tasks: ['less:dev'],
        files: ['src/jpmobile/style/*.less']
      },
      js: {
        tasks: ['browserify:dev'],
        files: ['src/jpmobile/script/*.js']
      },
      cp: {
        tasks: ['copy'],
        files: ['src/jpmobile/template/*','src/jpmobile/img/*']
      },
      livereload: {
        options: { livereload: true },
        files: ["target/dist/**/*"]
      }
    },
    copy: {
      dev: {
        files: [
          {expand: true, cwd: 'src/jpmobile/template/', src: '**', dest: 'target/dist/', filter: 'isFile'}
        ],
      },
      img: {
        files: [
          {expand: true, cwd: 'src/jpmobile/img/', src: '**', dest: 'target/dist/images/', filter: 'isFile'}
        ],
      }
    },
    cssmin: {
      dist: {
        files: {
          'target/dist/style/site.css' : ['target/dist-stage/style/site.css']
        }
      }
    },
    version: {
      default: { src: ['package.json'] }
    },
    less: {
      prod: {
        files: {
          "target/dist-stage/style/site.css" : ["src/jpmobile/style/site.less"]
        }
      },
      dev: {
        files: {
          "target/dist/style/site.css" : ["src/jpmobile/style/site.less"]
        }
      }
    },
    browserify: {
      prod: {
        files: {
          'target/dist-stage/script/site.js': ['src/jpmobile/script/site.js']
        }
      },
      dev: {
        files: {
          'target/dist/script/site.js': ['src/jpmobile/script/site.js']
        }
      }
    },
    uglify: {
      dist: {
        files: {
          'target/dist/script/site.js' : ['target/dist-stage/script/site.js']
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

  grunt.loadNpmTasks('grunt-version');

  grunt.registerTask('dev', ['less:dev', 'browserify:dev', 'copy', 'connect', 'watch']);

  grunt.registerTask('default', ['less:prod', 'browserify:prod', 'cssmin', 'uglify', 'copy:img']);
};
