/**
 * Copyright (C) 2014 yanni4night.com
 * skin.js
 *
 * changelog
 * 2014-06-23[16:44:23]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define(['./utils'], function(Utils) {
    var SkinManager = {
        init: function() {
            var params = Utils.getUrlParams();
            if (params.skin) {
                this.loadSkin(params.skin);
            }
            return this;
        },
        loadSkin: function(name) {
            switch (true) {
                case /caipiao|cp|red|lottery/i.test(name):
                    name = 'cp';
                    break;
                case /orange/i.test():
                    name = 'orange';
                    break;
                case /cyan|x1/i.test(name):
                    name = 'x1';
                    break;
                case /semob|se/i.test(name):
                    name = 'se';
                    break;
                default:
                    name = null;
            }

            if (name) {
                $(document.body).addClass('skin_' + name);
            }
        }
    };

    return SkinManager.init();
});