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

module.exports = function(form,main){
  var colorLinks = window.sessionStorage.getItem('colorLinks') != 'false',
      label = form.appendChild(dom.make('<label for="coloration">Link Color Coding:</label>')),
      button = form.appendChild(dom.make('<button id="coloration"></button>')),
      colorText = '<span class="artist">C</span><span class="bodily">o</span><span class="dublin">l</span><span class="performances">o</span><span class="literature">r</span>' ;
      applyLinkColorPreference = function(){
        if(colorLinks) {
          main.classList.add('colorful');
          button.innerHTML = colorText;
        } else {
          main.classList.remove('colorful');
          button.innerHTML = 'Plain';
        }
      },
      toggleColors = function(){
        colorLinks = !colorLinks;
        window.sessionStorage.setItem('colorLinks', colorLinks);
        applyLinkColorPreference();
      };

  applyLinkColorPreference();
  button.addEventListener('click', function (e) {
    e.preventDefault();
    toggleColors.apply(this);
  });
};
