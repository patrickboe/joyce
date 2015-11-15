var life = require('./life');

module.exports =
  function (document,timers) {
      var make = function(html) {
          var box = document.createElement('div');
          box.innerHTML = html;
          return box.firstChild;
      };

      life.ready(function(){

        var body   = document.querySelector('body'),
            nav = document.querySelector('nav'),
            main = document.querySelector('main'),
            hamburger = body.insertBefore(make('<a id="hamburger"><span></span></a>'),nav),
            hamburgerSpan = hamburger.querySelector('span'),

            status = function(message){
              hamburgerSpan.innerHTML = message;
            },

            toSingleTouchHandler = function(touchHandler) {
              return function(e) {
                if(e.changedTouches.length==1) touchHandler(e);
              };
            },

            constitutesASwipe = function(pixelsMoved){
              return pixelsMoved > 30
            },

            swipeDownMonitor = function(onSwipe){
              var highestTouch = NaN,
                  moveMon = toSingleTouchHandler(function(e){
                    var y = e.changedTouches[0].clientY;
                    if(isNaN(highestTouch) || y < highestTouch) {
                      highestTouch=y;
                    } else if(constitutesASwipe(y - highestTouch)) {
                      onSwipe();
                    }
                  }),
                  startMon = toSingleTouchHandler(function(e){
                    highestTouch=e.changedTouches[0].clientY;
                  }),
                  endMon = toSingleTouchHandler(function(e) {
                    highestTouch=NaN;
                  }),
                  self = {
                    dispose: function(){
                      main.removeEventListener('touchmove', moveMon);
                      main.removeEventListener('touchstart', startMon);
                      main.removeEventListener('touchend', endMon);
                      main.removeEventListener('touchcancel', endMon);
                    }
                  };
              main.addEventListener('touchmove', moveMon);
              main.addEventListener('touchstart', startMon);
              main.addEventListener('touchend', endMon);
              main.addEventListener('touchcancel', endMon);
              return self;
            },

            navigating = function(){
              var self = {
                transition : function(event){
                  switch(event) {
                    case "seeking text":
                    case "user toggle":
                      body.classList.remove('navigating');
                      return browsing();
                    default : return self;
                  }
                }
              };
              body.classList.add('navigating');
              return self;
            },

            browsing = function(){
              var setIdle = function(){ processUIEvent("idle") }
                idleTimeout = timers.setTimeout(setIdle, 2500),
                self = {
                  transition : function(event){
                    switch(event) {
                      case "user toggle":
                        timers.clearTimeout(idleTimeout);
                        return navigating();
                      case "idle" : return reading();
                      default : return self;
                    }
                  }
                };
              return self;
            },

            reading = function(){
                var seekMon = swipeDownMonitor(function(){
                  processUIEvent("seeking menu")
                }),
                self = {
                  transition : function(event){
                    switch(event) {
                      case "seeking menu" :
                        seekMon.dispose();
                        body.classList.remove('reading');
                        return browsing();
                      default : return self;
                    }
                  }
                };

              body.classList.add('reading');
              return self;
            },

            processUIEvent = (function(){
              var self = function(event){
                userState = userState.transition(event);
              },
              userState = browsing();
              return self;
            })();

        hamburger.addEventListener('click', function (e) {
            e.preventDefault();
            processUIEvent("user toggle");
        });
        main.addEventListener('click', function(e) {
          processUIEvent("seeking text");
        });
    });

  };
