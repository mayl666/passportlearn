/**
 * local.js
 *
 * changelog
 * 2014-06-20[12:16:52]:created
 *
 * @info sogou-inc\yinyong,windows-x64,UTF-8,10.129.192.39,js,Y:\sogou-passport-front\static\js\wap
 * @author yanni4night@gmail.com
 * @version 0.0.1
 * @since 0.0.1
 */


define([], function() {
    var Local = {
        load: function(key) {
            try {
                return JSON.parse(localStorage.getItem(key)) || {};
            } catch (e) {
                return {};
            }
        },
        save: function(key, value) {
            var v = value;
            if ('string' !== typeof value) {
                v = JSON.stringify(value);
            }

            localStorage.setItem(key, v);
        }
    };

    return Local;
});