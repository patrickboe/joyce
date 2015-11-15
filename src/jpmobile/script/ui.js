var life = require('./life');

module.exports =
  function (document,window) {
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

            up = true,

            down = !up,

            scrollMonitor = function(direction) {
              var progressing =
                direction === up ?
                function(y0,y1) { return y0 > y1; } :
                function(y0,y1) { return y0 < y1; };

              return function(onScroll){
                var prevY = window.scrollY,

                  mon = function(){
                    if(progressing(prevY, window.scrollY)) { onScroll(); }
                    prevY = window.scrollY;
                  },

                  dampenedScrollMonitor = dampen(mon,30),

                  self = {
                    dispose : function(){
                      window.removeEventListener("scroll",dampenedScrollMonitor.execute);
                      dampenedScrollMonitor.dispose();
                    }
                  }
                window.addEventListener('scroll', dampenedScrollMonitor.execute);

                return self;
              };
            },

            swipeMonitor = function(direction) {
              var metrics =
                direction === up ?

                { isNewExtreme : function(extreme, cur) { return cur > extreme; },
                  magnitude : function(extreme, cur) { return extreme - cur; } } :

                { isNewExtreme : function(extreme, cur) { return cur < extreme; },
                  magnitude : function(extreme, cur) { return cur - extreme; } };

              return function(onSwipe){
                var extremeTouch = NaN,
                    moveMon = toSingleTouchHandler(function(e){
                      var y = e.changedTouches[0].clientY;
                      if(isNaN(extremeTouch) || metrics.isNewExtreme(extremeTouch, y)) {
                        extremeTouch=y;
                      } else if(constitutesASwipe(metrics.magnitude(extremeTouch,y))) {
                        onSwipe();
                      }
                    }),
                    startMon = toSingleTouchHandler(function(e){
                      extremeTouch=e.changedTouches[0].clientY;
                    }),
                    endMon = toSingleTouchHandler(function(e) {
                      extremeTouch=NaN;
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
              };
            },

            lookingMonitor = function(direction) {
              var makeSwipeMonitor = swipeMonitor(!direction),
                  makeScrollMonitor = scrollMonitor(direction);
              return function(onSeek){
                  var swipeMon = makeSwipeMonitor(onSeek),
                      scrollMon = makeScrollMonitor(onSeek),

                  self = {
                    dispose : function(){
                      swipeMon.dispose();
                      scrollMon.dispose();
                    }
                  };

                  return self;
              }
            },

            lookingDownMonitor = lookingMonitor(down),

            lookingUpMonitor = lookingMonitor(up),

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
              var setIdle = function(){ processUIEvent("idle") },
                seekMon = lookingDownMonitor(function(){
                  processUIEvent("seeking text")
                }),
                idleTimeout = window.setTimeout(setIdle, 2500),
                self = {
                  transition : function(event){
                    switch(event) {
                      case "user toggle":
                        seekMon.dispose();
                        window.clearTimeout(idleTimeout);
                        return navigating();
                      case "idle" :
                        seekMon.dispose();
                        return reading();
                      case "seeking text":
                        seekMon.dispose();
                        window.clearTimeout(idleTimeout);
                        return reading();
                      default : return self;
                    }
                  }
                };
              return self;
            },

            reading = function(){
                var seekMon = lookingUpMonitor(function(){
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
