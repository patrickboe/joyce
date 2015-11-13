var life = require('./life');

var identity = function(x) {return x;};

module.exports =
  function (document) {
      var make = function(html) {
          var box = document.createElement('div');
          box.innerHTML = html;
          return box.firstChild;
      },
      dampen = function(f,t){
        var called=false,
        poll = function(){
          if(called){
            called = false;
            f();
          }
        },
        iv = window.setInterval(poll,t);

        return {
          execute : function(){ called = true; },
          dispose : function() { window.clearInterval(iv); }
        };
      };

      life.ready(function(){

        var layout   = document.querySelector('body'),
            nav = document.querySelector('nav'),
            hamburger = layout.insertBefore(make('<a id="hamburger"><span></span></a>'),nav),

            navigating = function(){
              var self = {
                transition : function(event){
                  switch(event) {
                    case "user toggle" :
                      layout.classList.remove('navigating');
                      return browsing();
                    default : return self;
                  }
                }
              };
              layout.classList.add('navigating');
              return self;
            },

            browsing = function(){
              var setIdle = function(){ processUIEvent("idle") }
                idleTimeout = window.setTimeout(setIdle, 5000),
                self = {
                  transition : function(event){
                    switch(event) {
                      case "user toggle" :
                        window.clearTimeout(idleTimeout);
                        return navigating();
                      case "idle" : return reading();
                      default : return self;
                    }
                  }
                };
              return self;
            },

            reading = function(){
              var scrollY = window.scrollY;
                scrollMonitor = function(){
                  var prevY = scrollY;
                  scrollY = window.scrollY;
                  if(scrollY < prevY) { processUIEvent("scrolling up"); }
                },
                dampenedScrollMonitor = dampen(scrollMonitor,30),
                self = {
                  transition : function(event){
                    switch(event) {
                      case "scrolling up" :
                        window.removeEventListener("scroll",dampenedScrollMonitor.execute);
                        dampenedScrollMonitor.dispose();
                        layout.classList.remove('reading');
                        return browsing();
                      default : return self;
                    }
                  }
                };
              layout.classList.add('reading');
              window.addEventListener("scroll",dampenedScrollMonitor.execute);
              return self;
            },

            processUIEvent = function(){
              var self = function(event){
                userState = userState.transition(event);
              },
              userState = browsing();
              return self;
            }();

        hamburger.onclick = function (e) {
            e.preventDefault();
            processUIEvent("user toggle");
        };

    });

  };
