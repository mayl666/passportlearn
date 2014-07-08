/**
 * local.js
 *
 * changelog
 * 2014-06-20[12:16:52]:created
 * 2014-07-02[10:40:04]:fixed localstorage crash on safari
 *
 * @info sogou-inc\yinyong,windows-x64,UTF-8,10.129.192.39,js,Y:\sogou-passport-front\static\js\wap
 * @author yanni4night@gmail.com
 * @version 0.0.2
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

            try{
                localStorage.setItem(key, v);
            }catch(e){}
        }
    };

    return Local;
});