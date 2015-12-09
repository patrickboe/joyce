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
var life = require('./life'),
  dom = require('./dom'),
  lookMonitor = require('./lookMonitor'),
  navigation = require('./navigation'),
  pagination = require('./pagination'),
  coloration = require('./coloration');

module.exports =
  function (document,window) {

      prepareContentPage = function(body){
        var nav = body.querySelector('nav'),
            main = body.querySelector('main'),
            controlMarkup = '<div id="controls"></div>',
            controls = body.insertBefore(dom.make(controlMarkup),nav),
            form = controls.appendChild(dom.make('<form></form>'));

        navigation(body.classList,
                   window,
                   lookMonitor(window,main),
                   nav,
                   controls,
                   main);

        pagination(window,form,main);

        coloration(form, main);

      };

      life.ready(function(){
        var body = document.querySelector('body.content');
        if(body) prepareContentPage(body);
      });
  };
