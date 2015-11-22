var dom = require('./dom');

module.exports = function(window,controls,main){
  var label = controls.appendChild(dom.make('<label for="pagination">Pages:</label>')),
      pageButton = controls.appendChild(dom.make('<button id="pagination">Unpaginated</button>')),
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
        var states=["Unpaginated","1922 ed.","1932 ed.","1961 ed.","1986 ed."],
            i = parseInt(window.sessionStorage.getItem('paginationState')) || 0;
        processPagination(states[i]);

        return function(){
          unpaginateEdition(states[i]);
          i = (i + 1) % states.length;
          window.sessionStorage.setItem('paginationState',i);
          processPagination(states[i]);
        };
      }();


  pageButton.addEventListener('click', function (e) {
    e.preventDefault();
    cycleCitations.apply(this);
  });
};
