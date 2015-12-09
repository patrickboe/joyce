/*
Copyright 2015 Patrick Boe

This file is part of jpmobile.

jpmobile is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

jpmobile is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jpmobile.  If not, see <http://www.gnu.org/licenses/>.
*/
var dom = require('./dom');

module.exports = function(uiClasses,timers,looking,nav,controls,main) {
      var hamburger =
        controls.appendChild(dom.make('<a id="hamburger"><span></span></a>')),
      hamburgerSpan = hamburger.querySelector('span'),

      status = function(message){
        hamburgerSpan.innerHTML = message;
      },

      navigating = function(){
        var self = {
          transition : function(event){
            switch(event) {
              case "seeking text":
              case "user toggle":
                uiClasses.remove('navigating');
                return browsing();
              default : return self;
            }
          }
        };
        uiClasses.add('navigating');
        return self;
      },

      browsing = function(){
        var setIdle = function(){ processMenuEvent("idle") },
          dribbleMon = looking.up(function(){
            processMenuEvent("seeking menu");
          }),
          seekMon = looking.down(function(){
            processMenuEvent("seeking text");
          }),
          setIdleTimeout =
            function(){ return timers.setTimeout(setIdle, 6000); };
          idleTimeout = setIdleTimeout(),
          disposeMonitors = function(){
            seekMon.dispose();
            dribbleMon.dispose();
          },
          self = {
            transition : function(event){
              switch(event) {
                case "seeking menu":
                  timers.clearTimeout(idleTimeout);
                  idleTimeout = setIdleTimeout();
                  return self;
                case "user toggle":
                  disposeMonitors();
                  timers.clearTimeout(idleTimeout);
                  return navigating();
                case "idle" :
                  disposeMonitors();
                  return reading();
                case "seeking text":
                  disposeMonitors();
                  timers.clearTimeout(idleTimeout);
                  return reading();
                default : return self;
              }
            }
          };
        return self;
      },

      reading = function(){
          var seekMon = looking.up(function(){
            processMenuEvent("seeking menu")
          }),
          self = {
            transition : function(event){
              switch(event) {
                case "seeking menu" :
                  seekMon.dispose();
                  uiClasses.remove('reading');
                  return browsing();
                default : return self;
              }
            }
          };

        uiClasses.add('reading');
        return self;
      },

      processMenuEvent = (function(){
        var self = function(event){
          userState = userState.transition(event);
        },
        userState = browsing();
        return self;
      })();

  hamburger.addEventListener('click', function (e) {
      e.preventDefault();
      processMenuEvent("user toggle");
  });

  main.addEventListener('click', function(e) {
    processMenuEvent("seeking text");
  });

  controls.addEventListener('click', function (e) {
    processMenuEvent("seeking menu");
  });

};


