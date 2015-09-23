var life = require('./life');

var identity = function(x) {return x;};

module.exports =
  function (window, document) {
      life.ready(function(){

        var layout   = document.querySelector('body'),
            menu     = document.getElementById('menu'),
            hamburger = document.getElementById('hamburger');

        function toggleClass(element, className) {
            var classes = element.className.split(/\s+/).filter(identity),
                length = classes.length,
                i = 0;

            for(; i < length; i++) {
              if (classes[i] === className) {
                classes.splice(i, 1);
                break;
              }
            }
            // The className is not found
            if (length === classes.length) {
                classes.push(className);
            }

            element.className = classes.join(' ');
        }

        hamburger.onclick = function (e) {
            var active = 'active';

            e.preventDefault();
            toggleClass(layout, active);
            toggleClass(menu, active);
            toggleClass(hamburger, active);
        };

    });

  };
