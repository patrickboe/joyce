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

module.exports = function(window,form,main){
  var states=["Unpaged","1922 ed.","1932 ed.","1939 ed.","1961 ed.","1986 ed."],
      stateIndex = parseInt(window.sessionStorage.getItem('paginationState')) || 0;
      label = form.appendChild(dom.make('<label for="pagination">Pages:</label>')),
      pageButton = form.appendChild(dom.make('<button id="pagination"></button>')),
      pageCitations = main.querySelectorAll('cite.page'),

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
        pageButton.innerHTML = ed;
      };

      cycleCitations = function(){
        processPagination(states[stateIndex]);

        return function(){
          unpaginateEdition(states[stateIndex]);
          stateIndex = (stateIndex + 1) % states.length;
          window.sessionStorage.setItem('paginationState',stateIndex);
          processPagination(states[stateIndex]);
        };
      }();


  pageButton.addEventListener('click', function (e) {
    e.preventDefault();
    cycleCitations.apply(this);
  });
};
