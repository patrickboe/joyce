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
