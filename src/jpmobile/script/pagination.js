var dom = require('./dom');

module.exports = function(window,controls,main){
  var paginationForm = controls.appendChild(dom.make('<form id="pagination-form"></form>')),
      states=["Unmarked","1922 ed.","1932 ed.","1961 ed.","1986 ed."],
      stateIndex = parseInt(window.sessionStorage.getItem('paginationState')) || 0;
      label = paginationForm.appendChild(dom.make('<label for="pagination">Pages:</label>')),
      pageButton = paginationForm.appendChild(dom.make('<button id="pagination"></button>')),
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
