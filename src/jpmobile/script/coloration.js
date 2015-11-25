var dom = require('./dom');

module.exports = function(form,main){
  var colorLinks = window.sessionStorage.getItem('colorLinks') == 'true',
      label = form.appendChild(dom.make('<label for="coloration">Link Color Coding:</label>')),
      button = form.appendChild(dom.make('<button id="coloration">Colorful</button>'));
      applyLinkColorPreference = function(){
        if(colorLinks) {
          main.classList.add('colorful');
          button.innerHTML = 'Colorful';
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
