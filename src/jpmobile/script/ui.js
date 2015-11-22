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
      },

      prepareContentPage = function(body){
        var nav = document.querySelector('nav'),
            main = document.querySelector('main'),
            controlMarkup = '<div id="controls"><a id="hamburger"><span></span></a><label for="pagination">Pages:</label><button id="pagination">Unpaginated</button></div>';
            controls = body.insertBefore(make(controlMarkup),nav),
            hamburger = controls.querySelector('#hamburger'),
            pagination = controls.querySelector('#pagination'),
            hamburgerSpan = hamburger.querySelector('span'),
            pageCitations = main.querySelectorAll('cite.page'),

            status = function(message){
              hamburgerSpan.innerHTML = message;
            },

            toSingleTouchHandler = function(touchHandler) {
              return function(e) {
                if(e.changedTouches.length==1) touchHandler(e);
              };
            },

            constitutesASwipe = function(pixelsMoved){
              return pixelsMoved > 20
            },

            up = true,

            down = !up,

            couldBeInABrowserUIEffect = function(){
              return window.scrollY < 60;
            },

            scrollMonitor = function(direction) {
              var progressing =
                direction === up ?
                function(y0,y1) { return y0 > y1; } :
                function(y0,y1) { return y0 < y1 && !(couldBeInABrowserUIEffect()) };

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
                dribbleMon = lookingUpMonitor(function(){
                  processUIEvent("seeking menu");
                }),
                seekMon = lookingDownMonitor(function(){
                  processUIEvent("seeking text");
                }),
                setIdleTimeout =
                  function(){ return window.setTimeout(setIdle, 6000); };
                idleTimeout = setIdleTimeout(),
                disposeMonitors = function(){
                  seekMon.dispose();
                  dribbleMon.dispose();
                },
                self = {
                  transition : function(event){
                    switch(event) {
                      case "seeking menu":
                        window.clearTimeout(idleTimeout);
                        idleTimeout = setIdleTimeout();
                        return self;
                      case "user toggle":
                        disposeMonitors();
                        window.clearTimeout(idleTimeout);
                        return navigating();
                      case "idle" :
                        disposeMonitors();
                        return reading();
                      case "seeking text":
                        disposeMonitors();
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

            isEdition = function(ed) {
              return function(node){
                return node.title==ed;
              };
            },

            onEditionClasses = function(classOp){
              return function(ed){
                var filter = Array.prototype.filter,
                cs = filter.call(pageCitations,isEdition(ed)),
                i = 0;
                for(i=0;i<cs.length;i++)
                  classOp(cs[i].classList);
              }
            },

            unpaginateEdition = onEditionClasses(function(cl) { cl.remove("chosen"); }),

            paginateEdition = onEditionClasses(function(cl) { cl.add("chosen"); }),

            processPagination = function(ed) {
              paginateEdition(ed);
              pagination.innerHTML = ed;
            };

            cycleCitations = function(){
              var states=["Unpaginated","1922 ed.","1932 ed.","1961 ed.","1986 ed."],
                  i = parseInt(window.sessionStorage.getItem('paginationState')) || 0;
              processPagination(states[i]);

              return function(){
                unpaginateEdition(states[i]);
                i = (i + 1) % states.length;
                window.sessionStorage.setItem('paginationState',i);
                processPagination(states[i]);
              };
            }(),

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
        pagination.addEventListener('click', function (e) {
          e.preventDefault();
          cycleCitations.apply(this);
        });

      };

      life.ready(function(){
        var body = document.querySelector('body.content');
        if(body) prepareContentPage(body);
      });
  };
