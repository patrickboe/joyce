var life = require('./life'),
  dom = require('./dom'),
  lookMonitor = require('./lookMonitor'),
  navigation = require('./navigation'),
  pagination = require('./pagination');

module.exports =
  function (document,window) {

      prepareContentPage = function(body){
        var nav = body.querySelector('nav'),
            main = body.querySelector('main'),
            controlMarkup = '<div id="controls"></div>',
            controls = body.insertBefore(dom.make(controlMarkup),nav);

        navigation(body.classList,
                   window,
                   lookMonitor(window,main),
                   nav,
                   controls);

        pagination(window,controls,main);

      };

      life.ready(function(){
        var body = document.querySelector('body.content');
        if(body) prepareContentPage(body);
      });
  };
