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

module.exports = function(main) {
  var twistyAfter=function(el) {
      var twistyMarkup='<i class="material-icons twisty">expand_more</i>';
      return el.appendChild(dom.make(twistyMarkup));
    },
    applyTwistBehavior = function(block) {
      var header = block.querySelector('h2'),
      twisty = twistyAfter(header),
      toggleReadMore = function (e) {
        e.preventDefault();
        twisty.innerHTML =
          block.classList.toggle('open') ?
            "expand_less" :
            "expand_more";
      };
      header.addEventListener('click', toggleReadMore);
    },
    readMoreBlock = main.querySelector('.read-more');

  if(readMoreBlock) applyTwistBehavior(readMoreBlock);
};
