module.exports = {
    make : function(html) {
        var box = document.createElement('div');
        box.innerHTML = html;
        return box.firstChild;
    }
};

