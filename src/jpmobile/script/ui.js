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
            main = document.querySelector('main'),
            hamburger = layout.insertBefore(make('<a id="hamburger"><span></span></a>'),nav),
            hamburgerSpan = hamburger.querySelector('span'),

            status = function(message){
              hamburgerSpan.innerHTML = message;
            },

            swipeDownMonitor = function(onSwipe){
              var lastY = 999999,
                  moveMon = function(e){
                    var touches=e.changedTouches,
                    y0 = lastY,
                    y = touches[0].clientY;
                    if(touches.length==1) {
                      if(y0 < y) onSwipe();
                      lastY = y;
                    }
                  },
                  startMon = function(e){
                    var touches = e.changedTouches;
                    if(touches.length==1) {
                      lastY=touches[0].clientY;
                    }
                  },
                  endMon = function(e) {
                    lastY=999999;
                  },
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

            scrollUpMonitor = function(onScroll){
              var scrollY = window.scrollY,

                scrollMonitor = function(){
                  var prevY = scrollY;
                  scrollY = window.scrollY;
                  if(scrollY < prevY) { onScroll(); }
                },

                dampenedScrollMonitor = dampen(scrollMonitor,30),

                self = {
                  dispose : function(){
                    window.removeEventListener("scroll",dampenedScrollMonitor.execute);
                    dampenedScrollMonitor.dispose();
                  }
                }
              window.addEventListener('scroll', dampenedScrollMonitor.execute);

              return self;
            },

            menuSeekingMonitor = function(onSeek){
                var swipeMon = swipeDownMonitor(onSeek),
                    //scrollMon = scrollUpMonitor(onSeek),

                self = {
                  dispose : function(){
                    swipeMon.dispose();
                    //scrollMon.dispose();
                  }
                };

                return self;
            },

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
                idleTimeout = window.setTimeout(setIdle, 3000),
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
                var seekMon = menuSeekingMonitor(function(){
                  processUIEvent("seeking menu")
                }),
                self = {
                  transition : function(event){
                    switch(event) {
                      case "seeking menu" :
                        seekMon.dispose();
                        layout.classList.remove('reading');
                        return browsing();
                      default : return self;
                    }
                  }
                };

              layout.classList.add('reading');
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
