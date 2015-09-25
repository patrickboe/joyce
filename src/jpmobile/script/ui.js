var life = require('./life');

var identity = function(x) {return x;};

module.exports =
  function (document) {
      life.ready(function(){

        var layout   = document.querySelector('body'),
            hamburger = document.getElementById('hamburger');

        hamburger.onclick = function (e) {
            e.preventDefault();
            layout.classList.toggle('navigating');
        };

    });

  };
