var life = require('./life');

var identity = function(x) {return x;};

module.exports =
  function (document) {
      var make = function(html) {
          var box = document.createElement('div');
          box.innerHTML = html;
          return box.firstChild;
      };

      life.ready(function(){

        var layout   = document.querySelector('body'),
            nav = document.querySelector('nav'),
            hamburger = layout.insertBefore(make('<a id="hamburger"><span></span></a>'),nav);

        hamburger.onclick = function (e) {
            e.preventDefault();
            layout.classList.toggle('navigating');
        };

    });

  };
