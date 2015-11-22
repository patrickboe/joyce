var toSingleTouchHandler = function(touchHandler) {
  return function(e) {
    if(e.changedTouches.length==1) touchHandler(e);
  };
},

constitutesASwipe = function(pixelsMoved){
  return pixelsMoved > 20
},

up = true,

down = !up,

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

module.exports = function(window,main) {

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
    };

    return {
      up : lookingMonitor(up),
      down : lookingMonitor(down)
    };
};
