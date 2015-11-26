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
