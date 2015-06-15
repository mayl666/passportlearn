
/**
 * Copyright (C) 2014 yanni4night.com
 *
 * interface.js
 *
 * changelog
 * 2014-06-19[18:18:50]:authorized
 *
 * @info yinyong,osx-x64,UTF-8,10.129.161.40,js,/Volumes/yinyong/sogou-passport-front/static/js/wap
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/interface',[], function () {

    var client_id = 1024;
    var noop = function () {
    };

    function checkNeedCaptcha(username, callback) {

        callback = callback || noop;
        return $.ajax({
            url: '/web/login/checkNeedCaptcha',
            data: {
                username: username,
                client_id: client_id
            },
            cache: false,
            dataType: 'json',
            success: function (data) {
                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                    }
                }

                if (data && data.data && data.data.needCaptcha) {
                    return callback(true);
                } else {
                    return callback(false);
                }
            },
            error: function () {
                return callback(false);
            }
        });
    } //checkNeedCaptcha

    function getCaptcha(token) {
        return '/captcha?token=' + token + "&_" + (+new Date());
    }

    function login(params, callback) {
        var options = {
            client_id: client_id,
            v: 0,
            ru: 'http://wap.sogou.com'
        };

        callback = callback || noop;

        $.extend(options, params);

        return $.ajax({
            url: '/wap/login',
            type: 'post',
            data: options,
            dataType: 'json',
            error: function () {
                alert("come into a");
                return callback(false, {
                    'statusText': '登录失败'
                });
            },
            success: function (data) {
                if (data && !+data.status)
                    return callback(true, data.data);
                else
                    alert("come into b");
                    return callback(false, data);
            }
        });

    }


    /**
     * 短信登录
     * @param params
     * @param callback
     * @return {*}
     */
    function smsCodeLogin(params, callback) {
        var options = {
            client_id: client_id,
            v: 0,
            ru: 'http://wap.sogou.com'
        };

        callback = callback || noop;

        $.extend(options, params);

        return $.ajax({
            url: '/wap/smsCode/login',
            type: 'post',
            data: options,
            dataType: 'json',
            error: function () {
                return callback(false, {
                    'statusText': '登录失败'
                });
            },
            success: function (data) {
                if (data && !+data.status)
                    return callback(true, data.data);
                else
                    return callback(false, data);
            }
        });
    }


    /**
     * For register
     * @param  {[type]}   username [description]
     * @param  {Function} callback [description]
     * @return {[type]}            [description]
     */
    function checkusername(username, callback) {
        callback = callback || noop;

        return $.ajax({
            url: '/web/account/checkusername',
            data: {
                username: username
            },
            dataType: 'json',
            success: function (data) {
                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }

                if (!!data && !+data.status) {
                    return callback(true);
                } else if (!!data) {
                    return callback(false, data);
                }
            }
        });
    }

    function sendsms(params, callback) {
        var options = {
            client_id: client_id
        };

        $.extend(options, params);
        callback = callback || noop;

        return $.ajax({
            url: '/web/sendsms',
            data: options,
            type: 'post',
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }

                if (data && !+data.status) {
                    return callback(true);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '发送失败'
                });
            }
        });
    }


    function checksms(params, callback) {
        var options = {
            client_id: client_id,
            v: 5,
            ru: 'http://wap.sogou.com'
        };
        callback = callback || noop;
        $.extend(options, params);
        $.ajax({
            url: '/wap/findpwd/checksms',
            type: 'post',
            data: options,
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }
                if (data && !+data.status) {
                    return callback(true, data.data);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '提交失败'
                });
            }
        });
    }

    function findpwdSendsms(params, callback) {
        var options = {
            client_id: client_id
        };

        $.extend(options, params);

        callback = callback || noop;

        return $.ajax({
            url: '/wap/findpwd/sendsms',
            data: options,
            type: 'post',
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }

                if (data && !+data.status) {
                    return callback(true);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '发送失败'
                });
            }
        });
    }


    /**
     * 短信登录,发送验证码
     *
     * add by chengang & 2015-06-11
     *
     * @param params
     * @param callback
     */
    function smsCodeLoginSendSms(params, callback) {
        var options = {
            client_id: client_id
        };

        $.extend(options, params);

        callback = callback || noop;

        return $.ajax({
            url: '/wap/smsCodeLogin/sendSms',
            data: options,
            type: 'post',
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }

                if (data && !+data.status) {
                    return callback(true);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '发送失败'
                });
            }
        });
    }


    function findpwdCheck(params, callback) {
        var options = {
            client_id: client_id,
            v: 5,
            ru: 'http://wap.sogou.com'
        };
        callback = callback || noop;
        $.extend(options, params);
        $.ajax({
            url: '/wap/findpwd/check',
            type: 'post',
            data: options,
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }
                if (data && !+data.status) {
                    return callback(true, data.data);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '提交失败'
                });
            }
        });
    }

    function findpwdSendmail(params, callback) {
        var options = {
            client_id: client_id,
            v: 5,
            ru: 'http://wap.sogou.com'
        };
        callback = callback || noop;
        $.extend(options, params);
        $.ajax({
            url: '/wap/findpwd/sendemail',
            type: 'post',
            data: options,
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }
                if (data && !+data.status) {
                    return callback(true, data.data);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '提交失败'
                });
            }
        });
    }

    function reset(params, callback) {
        var options = {
            client_id: client_id,
            v: 5,
            ru: 'http://wap.sogou.com'
        };
        callback = callback || noop;
        $.extend(options, params);
        $.ajax({
            url: '/wap/findpwd/reset',
            type: 'post',
            data: options,
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }
                if (data && !+data.status) {
                    return callback(true, data.data);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '提交失败'
                });
            }
        });
    }

    function register(params, callback) {
        var options = {
            client_id: client_id,
            v: 0,
            ru: 'http://wap.sogou.com'
        };
        callback = callback || noop;
        $.extend(options, params);
        $.ajax({
            url: '/wap/reguser',
            type: 'post',
            data: options,
            dataType: 'json',
            success: function (data) {

                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {
                        data = {
                            status: 1,
                            statusText: '格式错误'
                        };
                    }
                }

                if (data && !+data.status) {
                    return callback(true, data.data);
                } else {
                    return callback(false, data);
                }
            },
            error: function () {
                return callback(false, {
                    'statusText': '注册失败'
                });
            }
        });
    }

    return {
        client_id: client_id,
        checkNeedCaptcha: checkNeedCaptcha,
        getCaptcha: getCaptcha,
        login: login,
        smsCodeLogin: smsCodeLogin,
        checkusername: checkusername,
        sendsms: sendsms,
        checksms: checksms,
        findpwdCheck: findpwdCheck,
        findpwdSendmail: findpwdSendmail,
        register: register,
        reset: reset,
        findpwdSendsms: findpwdSendsms,
        smsCodeLoginSendSms: smsCodeLoginSendSms
    };
});

/**
  * tpl.js
  *
  * changelog
  * 2014-06-20[14:04:23]:created
  *
  * @info sogou-inc\yinyong,windows-x64,UTF-8,10.129.192.39,js,Y:\sogou-passport-front\static\js\lib
  * @author yanni4night@gmail.com
  * @version 0.0.1
  * @since 0.0.1
  */
define('lib/tpl',[], function() {
    return function(tplStr) {
        var reg = /<%(.+?)%>/mg;
        var matches, lastMatches;
        var start = 0;
        var segs = [];
        var judge;
        var fnStr = 'with(__data){var _tpl="";';
        tplStr = tplStr.replace(/[\r\n]/mg, ''); //PRE elements not supported
        while (matches = reg.exec(tplStr)) {
            fnStr += '_tpl+="' + (tplStr.slice(start, matches.index)).replace(/"/mg, '\\"') + '";';
            start = matches[0].length + matches.index;
            lastMatches = matches;

            switch (true) {
                case /\s*for\s+(\w+)\s+in\s+(\w+)\s*/.test(matches[1]):
                    fnStr += 'for(var _i=0,' + RegExp.$1 + ',loop;_i<' + RegExp.$2 + '.length;++_i){' + RegExp.$1 + '=' + RegExp.$2 + '[_i];loop={index:_i+1,index0:_i,first:(_i===0),last:(_i+1===' + RegExp.$2 + '.length)};'
                    break;
                case /\s*(endfor|endif)\s*/.test(matches[1]):
                    fnStr += "}";
                    break;
                case /\s*if\s*([\w<>=\|\^&\*\(\)!\+\-\. ]+)/.test(matches[1]):
                    judge = RegExp.$1.replace(/\band\b/ig, '&&');
                    judge = judge.replace(/\bor\b/ig, '||');
                    fnStr += "if(" + judge + "){";
                    break;
                case new RegExp("=([\\w\\.\\[\\]\"'\\-\\| ]+)").test(matches[1]):
                    fnStr += '_tpl+=' + RegExp.$1 + ';';
                    break;
                case /\s*else\s*/.test(matches[1]):
                    fnStr += '}else{';
                    break;
                default:
                    console.error(matches[1]);
            }
        }
        fnStr += '_tpl+="' + (tplStr.slice(lastMatches.index + lastMatches[0].length)).replace(/"/mg, '\\"') + '";';
        fnStr += 'return _tpl;}';
        return new Function('__data', fnStr);
    };
});
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


define('wap/local',[], function() {
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
/**
  * emitter.js
  *
  * changelog
  * 2014-06-20[17:19:23]:created
  *
  * @info sogou-inc\yinyong,windows-x64,UTF-8,10.129.192.39,js,Y:\sogou-passport-front\static\js\lib
  * @author yanni4night@gmail.com
  * @version 0.0.1
  * @since 0.0.1
  */
define('lib/emitter',[],function(){
    function Emitter(){
        var listeners = {};

        this.on = function(evt,func,thisArg){
            if(!listeners[evt]){
                listeners[evt] = [];
            }

            listeners[evt].push({
                thisArg:thisArg,
                func:func,
                type:evt
            });

        };

        this.emit = function(evt,data){
            if(Array.isArray(listeners[evt])){
                listeners[evt].forEach(function(s){
                    s.func.call(s.thisArg||null,s,data);
                });
            }
        };
    };

    return Emitter;
});
/**
 * Copyright (C) 2014 yanni4night.com
 * utils.js
 *
 * changelog
 * 2014-06-23[14:31:22]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/utils',[], function() {
    var gParams = null;
    return {
        getUrlParams: function() {
            if (gParams) return gParams;
            var params = location.search.split('#')[0].split(/[?&]/g);
            var matches;
            gParams = {};
            params.forEach(function(kv) {
                if ((matches = kv.match(/^([\w-]+)=([^&]+)/)) && matches[1] && matches[2]) {
                    gParams[matches[1]] = matches[2];
                }
            });

            return gParams;
        },
        getRu: function() {
            var params = this.getUrlParams();
            var ru = params['ru'];
            // if (!/https?:\/\/([\w-]+\.)+sogou.com/.test(decodeURIComponent(ru))) {
            //     ru = encodeURIComponent('http://wap.sogou.com');
            // }
            return ru;
        },
        getPassThroughParams: function() {
            var p = [];
            var allp = this.getUrlParams(),
                ru = this.getRu();
            if (allp.client_id) {
                p.push('client_id=' + allp.client_id);
            }
            if (allp.v) {
                p.push('v=' + allp.v);
            }
            if (allp.skin) {
                p.push('skin=' + allp.skin);
            }
            if (ru) {
                p.push('ru=' + ru);
            }

            if (allp.display) {
                p.push('display=' + allp.display);
            }

            return p.join('&');
        }
    };
});
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
define('wap/skin',['./utils'], function(Utils) {
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
                case /orange/i.test(name):
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
/*
 * A JavaScript implementation of the RSA Data Security, Inc. MD5 Message
 * Digest Algorithm, as defined in RFC 1321.
 * Version 2.2 Copyright (C) Paul Johnston 1999 - 2009
 * Other contributors: Greg Holt, Andrew Kepert, Ydnar, Lostinet
 * Distributed under the BSD License
 * See http://pajhome.org.uk/crypt/md5 for more info.
 */
var hexcase=0;function hex_md5(a){return rstr2hex(rstr_md5(str2rstr_utf8(a)))}function hex_hmac_md5(a,b){return rstr2hex(rstr_hmac_md5(str2rstr_utf8(a),str2rstr_utf8(b)))}function md5_vm_test(){return hex_md5("abc").toLowerCase()=="900150983cd24fb0d6963f7d28e17f72"}function rstr_md5(a){return binl2rstr(binl_md5(rstr2binl(a),a.length*8))}function rstr_hmac_md5(c,f){var e=rstr2binl(c);if(e.length>16){e=binl_md5(e,c.length*8)}var a=Array(16),d=Array(16);for(var b=0;b<16;b++){a[b]=e[b]^909522486;d[b]=e[b]^1549556828}var g=binl_md5(a.concat(rstr2binl(f)),512+f.length*8);return binl2rstr(binl_md5(d.concat(g),512+128))}function rstr2hex(c){try{hexcase}catch(g){hexcase=0}var f=hexcase?"0123456789ABCDEF":"0123456789abcdef";var b="";var a;for(var d=0;d<c.length;d++){a=c.charCodeAt(d);b+=f.charAt((a>>>4)&15)+f.charAt(a&15)}return b}function str2rstr_utf8(c){var b="";var d=-1;var a,e;while(++d<c.length){a=c.charCodeAt(d);e=d+1<c.length?c.charCodeAt(d+1):0;if(55296<=a&&a<=56319&&56320<=e&&e<=57343){a=65536+((a&1023)<<10)+(e&1023);d++}if(a<=127){b+=String.fromCharCode(a)}else{if(a<=2047){b+=String.fromCharCode(192|((a>>>6)&31),128|(a&63))}else{if(a<=65535){b+=String.fromCharCode(224|((a>>>12)&15),128|((a>>>6)&63),128|(a&63))}else{if(a<=2097151){b+=String.fromCharCode(240|((a>>>18)&7),128|((a>>>12)&63),128|((a>>>6)&63),128|(a&63))}}}}}return b}function rstr2binl(b){var a=Array(b.length>>2);for(var c=0;c<a.length;c++){a[c]=0}for(var c=0;c<b.length*8;c+=8){a[c>>5]|=(b.charCodeAt(c/8)&255)<<(c%32)}return a}function binl2rstr(b){var a="";for(var c=0;c<b.length*32;c+=8){a+=String.fromCharCode((b[c>>5]>>>(c%32))&255)}return a}function binl_md5(p,k){p[k>>5]|=128<<((k)%32);p[(((k+64)>>>9)<<4)+14]=k;var o=1732584193;var n=-271733879;var m=-1732584194;var l=271733878;for(var g=0;g<p.length;g+=16){var j=o;var h=n;var f=m;var e=l;o=md5_ff(o,n,m,l,p[g+0],7,-680876936);l=md5_ff(l,o,n,m,p[g+1],12,-389564586);m=md5_ff(m,l,o,n,p[g+2],17,606105819);n=md5_ff(n,m,l,o,p[g+3],22,-1044525330);o=md5_ff(o,n,m,l,p[g+4],7,-176418897);l=md5_ff(l,o,n,m,p[g+5],12,1200080426);m=md5_ff(m,l,o,n,p[g+6],17,-1473231341);n=md5_ff(n,m,l,o,p[g+7],22,-45705983);o=md5_ff(o,n,m,l,p[g+8],7,1770035416);l=md5_ff(l,o,n,m,p[g+9],12,-1958414417);m=md5_ff(m,l,o,n,p[g+10],17,-42063);n=md5_ff(n,m,l,o,p[g+11],22,-1990404162);o=md5_ff(o,n,m,l,p[g+12],7,1804603682);l=md5_ff(l,o,n,m,p[g+13],12,-40341101);m=md5_ff(m,l,o,n,p[g+14],17,-1502002290);n=md5_ff(n,m,l,o,p[g+15],22,1236535329);o=md5_gg(o,n,m,l,p[g+1],5,-165796510);l=md5_gg(l,o,n,m,p[g+6],9,-1069501632);m=md5_gg(m,l,o,n,p[g+11],14,643717713);n=md5_gg(n,m,l,o,p[g+0],20,-373897302);o=md5_gg(o,n,m,l,p[g+5],5,-701558691);l=md5_gg(l,o,n,m,p[g+10],9,38016083);m=md5_gg(m,l,o,n,p[g+15],14,-660478335);n=md5_gg(n,m,l,o,p[g+4],20,-405537848);o=md5_gg(o,n,m,l,p[g+9],5,568446438);l=md5_gg(l,o,n,m,p[g+14],9,-1019803690);m=md5_gg(m,l,o,n,p[g+3],14,-187363961);n=md5_gg(n,m,l,o,p[g+8],20,1163531501);o=md5_gg(o,n,m,l,p[g+13],5,-1444681467);l=md5_gg(l,o,n,m,p[g+2],9,-51403784);m=md5_gg(m,l,o,n,p[g+7],14,1735328473);n=md5_gg(n,m,l,o,p[g+12],20,-1926607734);o=md5_hh(o,n,m,l,p[g+5],4,-378558);l=md5_hh(l,o,n,m,p[g+8],11,-2022574463);m=md5_hh(m,l,o,n,p[g+11],16,1839030562);n=md5_hh(n,m,l,o,p[g+14],23,-35309556);o=md5_hh(o,n,m,l,p[g+1],4,-1530992060);l=md5_hh(l,o,n,m,p[g+4],11,1272893353);m=md5_hh(m,l,o,n,p[g+7],16,-155497632);n=md5_hh(n,m,l,o,p[g+10],23,-1094730640);o=md5_hh(o,n,m,l,p[g+13],4,681279174);l=md5_hh(l,o,n,m,p[g+0],11,-358537222);m=md5_hh(m,l,o,n,p[g+3],16,-722521979);n=md5_hh(n,m,l,o,p[g+6],23,76029189);o=md5_hh(o,n,m,l,p[g+9],4,-640364487);l=md5_hh(l,o,n,m,p[g+12],11,-421815835);m=md5_hh(m,l,o,n,p[g+15],16,530742520);n=md5_hh(n,m,l,o,p[g+2],23,-995338651);o=md5_ii(o,n,m,l,p[g+0],6,-198630844);l=md5_ii(l,o,n,m,p[g+7],10,1126891415);m=md5_ii(m,l,o,n,p[g+14],15,-1416354905);n=md5_ii(n,m,l,o,p[g+5],21,-57434055);o=md5_ii(o,n,m,l,p[g+12],6,1700485571);l=md5_ii(l,o,n,m,p[g+3],10,-1894986606);m=md5_ii(m,l,o,n,p[g+10],15,-1051523);n=md5_ii(n,m,l,o,p[g+1],21,-2054922799);o=md5_ii(o,n,m,l,p[g+8],6,1873313359);l=md5_ii(l,o,n,m,p[g+15],10,-30611744);m=md5_ii(m,l,o,n,p[g+6],15,-1560198380);n=md5_ii(n,m,l,o,p[g+13],21,1309151649);o=md5_ii(o,n,m,l,p[g+4],6,-145523070);l=md5_ii(l,o,n,m,p[g+11],10,-1120210379);m=md5_ii(m,l,o,n,p[g+2],15,718787259);n=md5_ii(n,m,l,o,p[g+9],21,-343485551);o=safe_add(o,j);n=safe_add(n,h);m=safe_add(m,f);l=safe_add(l,e)}return Array(o,n,m,l)}function md5_cmn(h,e,d,c,g,f){return safe_add(bit_rol(safe_add(safe_add(e,h),safe_add(c,f)),g),d)}function md5_ff(g,f,k,j,e,i,h){return md5_cmn((f&k)|((~f)&j),g,f,e,i,h)}function md5_gg(g,f,k,j,e,i,h){return md5_cmn((f&j)|(k&(~j)),g,f,e,i,h)}function md5_hh(g,f,k,j,e,i,h){return md5_cmn(f^k^j,g,f,e,i,h)}function md5_ii(g,f,k,j,e,i,h){return md5_cmn(k^(f|(~j)),g,f,e,i,h)}function safe_add(a,d){var c=(a&65535)+(d&65535);var b=(a>>16)+(d>>16)+(c>>16);return(b<<16)|(c&65535)}function bit_rol(a,b){return(a<<b)|(a>>>(32-b))};
define("lib/md5-min", function(){});

/**
 * Copyright (C) 2014 yanni4night.com
 *
 * main.js
 *
 * Fuck,all stupid
 *
 * changelog
 * 2014-06-19[17:13:04]:authorized
 *
 * @info yinyong,osx-x64,UTF-8,10.129.161.40,js,/Volumes/yinyong/sogou-passport-front/static/js/wap
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/login',['./interface','../lib/tpl' , './local','../lib/emitter','./utils','./skin','../lib/md5-min'], function(Form, resolve, Local,Emitter,Utils) {

    var HISTORY_KEY = 'login-history';

    var ru = Utils.getRu();
    var passParamsStr = Utils.getPassThroughParams();

    //This class operate list of history.
    var LoginHistory = {
        $history:$('.history'),
        SELECTEVENT:'select-event',
        init:function(){
            var self = this;

            self.listFunc = resolve($('#history-tpl').html());

            this.$history.delegate('.rm','click',function(e){
                e.preventDefault();
                var id = $(this).attr('data-id');
                self.removeItem(id);
                $(this).parent('li').remove();
            }).delegate('.hisname,.check','click',function(e){
                //check
                var name = $(this).parent('li').find('.hisname').text().trim();
                self.emit(self.SELECTEVENT,name);
            }).html(this.listFunc({list:this.listItems()}));

        },
        removeItem: function(username) {
            var his = Local.load(HISTORY_KEY);
            if (Array.isArray(his) && his.length) {
                his = his.filter(function(i) {
                    return i != username;
                });
                Local.save(HISTORY_KEY, his);
            }
        },
        addItem: function(username) {
            var his = Local.load(HISTORY_KEY);
            if (!Array.isArray(his)) {
                his = [];
            }

            if (!!~his.indexOf(username)) {
                return;
            }
            his.push(username);
            Local.save(HISTORY_KEY, his);
        },
        listItems: function() {
            var his = Local.load(HISTORY_KEY);
            if (!Array.isArray(his)) {
                his = [];
            }
            return his;
        }
    };

    $.extend(LoginHistory,new Emitter());

    var App = {
        $form: $('form'),
        $captchaWrapper: $('#captcha-wrapper'),
        $username: $('#username'),
        $password: $('#password'),
        $showpass: $('#showpass'),
        $captchaImg: $('#captcha-img'),
        $captcha: $('#captcha'),
        $msg: $('.msg'),
        __mLogining:false,
        init: function() {
            LoginHistory.init();

            LoginHistory.on(LoginHistory.SELECTEVENT,this.onHistorySelect,this);
            $('.backlink').click(function(e){
                e.preventDefault();
                history.back();
            });
            $('.trd-qq').attr('href','https://account.sogou.com/connect/login?provider=qq&type=wap&display=mobile&' + passParamsStr );
            $('.reglink').attr('href','/wap/reg?'+passParamsStr);
            $('.forgot').attr('href', '/wap/findpwd?'+passParamsStr);

            var phone;
            if(phone=Utils.getUrlParams().phone){
                this.$username.val(phone);
            }

            return this.initEvt();
        },
        initEvt: function() {
            var self = this;

            //check need captcha
            self.$username.blur(function(e) {
                e.target.value&&Form.checkNeedCaptcha(e.target.value, function(need) {
                    self.$captchaWrapper.toggleClass('hide', !need);
                    need && self.$captchaImg.attr('src', Form.getCaptcha(token));
                });
            });

            //click refresh
            self.$captchaImg.click(function(e) {
                $(this).attr('src', Form.getCaptcha(token));
            });

            //show or hide the password
            self.$showpass.change(function(e) {
                self.$password.attr('type', !this.checked ? 'password' : 'text');

                if(this.checked){
                    $(this).siblings('label').find('.circle').removeClass('grey').addClass('blue');
                }else{
                    $(this).siblings('label').find('.circle').removeClass('blue').addClass('grey');
                }
            });


            //submit
            self.$form.submit(function(e) {
                e.preventDefault();
                
                var u = $.trim(self.$username.val());
                var p = $.trim(self.$password.val());
                var c = $.trim(self.$captcha.val());

                if (!u || !p) {
                    return self.showMsg('请输入用户名或密码');
                }

                if(p.length<6){
                    return self.showMsg('密码至少6位');
                }

                if(self.__mLogining){
                    return false;
                }
                self.__mLogining = true;
                return Form.login({
                    token: token,
                    captcha: c,
                    username: u,
                    password: hex_md5(p)
                }, function(result, data) {
                    self.__mLogining = false;
                    if (result) {
                        LoginHistory.addItem(u);
                        self.showMsg('登录成功',true);

                        ru = decodeURIComponent(ru);
                        if(ru){
                            ru = ru.split('#')[0];
                            if(~ru.indexOf('?')){
                                if(ru[ru.length-1]!=='?'){
                                    ru +='&';
                                }
                            }else{
                                ru+='?';
                            }
                            location.assign(ru+'sgid='+data.sgid);
                        }

                        //ru&&location.assign(decodeURIComponent(ru));
                    } else {
                        self.showMsg(data.statusText);
                        if (data.status == '20221' || data.status == '20257') {
                            self.$captcha.empty().focus();
                            self.showCaptcha();
                        } else {
                            self.$password.empty().focus();
                            if(!self.$captchaWrapper.hasClass('hide')){
                                self.showCaptcha();
                            }
                        }
                        
                    }
                });
            });


            return this;
        },
        onHistorySelect:function (evt,name) {
            this.$username.val(name);
        },
        showCaptcha: function() {
            this.$captchaWrapper.removeClass('hide');
            this.$captchaImg.attr('src', Form.getCaptcha(token));
        },
        showMsg: function(msg,normal) {
            if(normal){
                this.$msg.find('.circle').removeClass('hide red').addClass('green');
                this.$msg.find('.circle .sprite').removeClass('sprite-wrong').addClass('sprite-right');

            }else{                
                this.$msg.find('.circle').removeClass('hide green').addClass('red');
                this.$msg.find('.circle .sprite').removeClass('sprite-right').addClass('sprite-wrong');

            }
            this.$msg.find('.info').text(msg);
            return this;
        }
    };

    return App;
});
/**
 * Copyright (C) 2014 yanni4night.com
 * dialog.js
 *
 * changelog
 * 2014-08-05[09:51:16]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/dialog',[], function() {
    var gDialogs = [];
    var noop = function() {};

    function Dialog(options, inherts) {
        var opt = this.opt = $.extend({
                $container: $('.dialog'),
                $mask: $('.mask'),
                init: noop,
                onOk: noop,
                onBeforeOk: function() {
                    return true;
                }
            }, options || {}),
            self = this;

        opt.$container.on('click', '.x', function(e) {
            e.preventDefault();
            if ('function' === typeof opt.onBeforeOk && false === opt.onBeforeOk.call(self)) {
                return;
            }
            self.hide();
            opt.onOk.call(self)
        });
        opt.$mask.click(function() {
            self.hide();
        });

        ('function' === typeof opt.init) && opt.init.call(this);
        $.extend(this, inherts || {});
        gDialogs.push(this);
    }

    Dialog.prototype = {
        show: function() {
            var self = this;
            gDialogs.forEach(function(dialog) {
                (this!==self )&&dialog.hide();
            });

            this.opt.$mask.removeClass('hide');
            this.opt.$container.removeClass('hide');

            if ('function' === typeof this.opt.onShow) {
                this.opt.onShow.call(self);
            }

            return this;
        },
        hide: function() {

            this.opt.$mask.addClass('hide');
            this.opt.$container.addClass('hide');
            return this;
        }
    };

    return Dialog;
});
/*
 * form module script
 * @author zhengxin
*/
 



define( 'utils',[],function(){

    
    return {
        uuid: function(){
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            };            
            return s4() + s4()  + s4()  + s4()  +
                s4() +  s4() + s4() + s4();

        },
        addZero: function(num,len){
            num = num.toString();
            while( num.length < len ){
                num = '0'+ num;
            }
            return num;
        },
        parseResponse: function(data){
            if( typeof data == 'string' ){
                try{
                    data = eval('('+data+')');
                }catch(e){
                    data = {status:-1,statusText:'服务器故障'};
                }
            }
            return data;
        },
        addIframe: function(url , callback){
            var iframe = document.createElement('iframe');
            iframe.src = url;

            iframe.style.position = 'absolute';
            iframe.style.top = '1px';
            iframe.style.left = '1px';
            iframe.style.width = '1px';
            iframe.style.height = '1px';
            
            if (iframe.attachEvent){
                iframe.attachEvent("onload", function(){
                    callback && callback();
                });
            } else {
                iframe.onload = function(){
                    callback && callback();
                };
            }

            document.body.appendChild(iframe);
        },
        getScript: function(url , callback){
            var script = document.createElement("script");
            var head = document.head;
            script.async = true;
            script.src = url;
            script.onload = script.onreadystatechange = function( _, isAbort ) {
                if ( isAbort || !script.readyState || /loaded|complete/.test( script.readyState ) ) {
                    script.onload = script.onreadystatechange = null;
                    if ( script.parentNode ) {
                        script.parentNode.removeChild( script );
                    }
                    script = null;
                    if ( !isAbort ) {
                        callback( );
                    }
                };
            };

            head.insertBefore( script, head.firstChild );
        },
        getUrlByMail:function(mail){
            mail = mail.split('@')[1];
            if( !mail ) return false;
            var hash = {
                "139.com":"mail.10086.cn",
                'gmail.com': 'mail.google.com', 
                'sina.com': 'mail.sina.com.cn', 
                'yeah.net': 'www.yeah.net', 
                'hotmail.com': 'www.hotmail.com', 
                'live.com': 'www.outlook.com', 
                'live.cn': 'www.outlook.com', 
                'live.com.cn': 'www.outlook.com', 
                'outlook.com': 'www.outlook.com', 
                'yahoo.com.cn': 'mail.cn.yahoo.com', 
                'yahoo.cn': 'mail.cn.yahoo.com', 
                'ymail.com': 'www.ymail.com', 
                'eyou.com': 'www.eyou.com', 
                '188.com': 'www.188.com', 
                'foxmail.com': 'www.foxmail.com' 
            };
            var url;
            if( mail in hash ){
                url= hash[mail];
            }else{
                url= 'mail.' + mail;
            }
            return 'http://' + url;
        }
    };

});

define( 'wap/common',['./utils'], function(Utils) {

	var passParamsStr = Utils.getPassThroughParams();

    $('.backlink').click(function(e) {
        e.preventDefault();
        history.back();
    });

    $('nav a').each(function(idx,item){
        var chref = $(item).attr('href');
        $(item).attr('href', chref.indexOf('?') == -1 ? (chref + '?' + passParamsStr)
                     : (chref + '&' + passParamsStr));
    });
    
    return{};
});

/**
 * Copyright (C) 2015
 * smsCodeLogin.js
 *
 * changelog
 * 2015-06-11
 *
 * @author chengang@sogou-inc.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/smsCodeLogin',['./interface','../lib/tpl' , './local','../lib/emitter', './utils', './dialog', '../utils', './skin', './common'], function (Form, resolve, Local, Emitter, Utils, Dialog, SuperUtils) {
    return {
        init: function () {
            var ru = Utils.getRu();
            var passParamsStr = Utils.getPassThroughParams();
            var token;

            //发送手机短信需要的验证码
            var CaptchaDialog = new Dialog({
                $container: $('#captchaDialog'),
                onOk: function () {
                    App.sendSms(this.$input.val());
                },
                init: function () {
                    var self = this;
                    this.$captchaImg = this.opt.$container.find('.captcha-img');
                    this.$input = this.opt.$container.find('input');

                    this.$captchaImg.click(function () {
                        self.refreshCaptcha();
                    });
                },
                onBeforeOk: function () {
                    return /^\w+$/.test(this.$input.val());
                },
                onShow: function () {
                    this.$input.val(null);
                }
            }, {
                refreshCaptcha: function () {
                    this.$captchaImg.attr('src', Form.getCaptcha(token = SuperUtils.uuid()));
                }
            });


            var App = {
                $form: $('form'),
                $captchaWrapper: $('#captcha-wrapper'),
                $username: $('#username'),
                $sms: $('#sms'),
                $captchaImg: $('#captcha-img-check'),
                $captcha: $('#captcha'),
                $sendsms: $('.sendsms'),
                $msg: $('.msg'),
                __mLogining: false,
                __SendingSms: false,
                __smsSent: false,
                __mFinding: false,
                init: function () {

                    $('.links a').each(function (idx, item) {
                        var chref = $(item).attr('href');
                        $(item).attr('href', chref.indexOf('?') == -1 ? (chref + '?' + passParamsStr) : (chref + '&' + passParamsStr));

                    });

                    return this.initEvt();
                },
                initEvt: function () {
                    var self = this;

                    //check need captcha
                    self.$username.blur(function (e) {
                        e.target.value && Form.checkNeedCaptcha(e.target.value, function (need) {
                            self.$captchaWrapper.toggleClass('hide', !need);
                            need && self.$captchaImg.attr('src', Form.getCaptcha(token));
                        });
                    });

                    //click refresh
                    self.$captchaImg.click(function () {
                        $(this).attr('src', Form.getCaptcha(token));
                    });


                    //check need captcha
                    self.$username.on('input', function (e) {

                        var phone = e.target.value;
                        if (/[^\d]/.test(phone)) {
                            return self.showMsg('请输入11位手机号');
                        } else {
                            self.hideMsg();
                        }
                        if (!/^1\d{10}$/.test(phone)) {
                            return;
                        }

                    });

                    //send sms code
                    self.$sendsms.click(function (e) {
                        e.preventDefault();
                        self.sendSms();

                    });

                    //submit
                    self.$form.submit(function (e) {
                        e.preventDefault();
                        var u = $.trim(self.$username.val());
                        var c = $.trim(self.$captcha.val());
                        var s = $.trim(self.$sms.val());

                        if (!u || !s) {
                            return self.showMsg('请输入用户名/验证码');
                        }

                        if (!/^1\d{10}$/.test(u)) {
                            return self.showMsg('请输入11位手机号');
                        }

                        if (!self.__smsSent) {
                            return self.showMsg('您还没有获取短信验证码');
                        }

                        if (self.__mLogining) {
                            return false;
                        }
                        self.__mLogining = true;

                        //登录
                        return Form.smsCodeLogin({
                            token: token,
                            captcha: c,
                            mobile: u,
                            smsCode: s
                        }, function (result, data) {
                            self.__mLogining = false;
                            if (result) {
                                self.showMsg('登录成功', true);

                                ru = decodeURIComponent(ru);
                                if (ru) {
                                    ru = ru.split('#')[0];
                                    if (~ru.indexOf('?')) {
                                        if (ru[ru.length - 1] !== '?') {
                                            ru += '&';
                                        }
                                    } else {
                                        ru += '?';
                                    }
                                    location.assign(ru + 'sgid=' + data.sgid);
                                }
                            } else {
                                self.showMsg(data.statusText);
                                if (data.status == '20221' || data.status == '20257') {
                                    self.$captcha.empty().focus();
                                    self.showCaptcha();
                                } else {
                                    self.$sms.empty().focus();
                                    if (!self.$captchaWrapper.hasClass('hide')) {
                                        self.showCaptcha();
                                    }
                                }

                            }
                        });
                    });


                    return this;
                },
                onHistorySelect: function (evt, name) {
                    this.$username.val(name);
                },
                showCaptcha: function () {
                    this.$captchaWrapper.removeClass('hide');
                    this.$captchaImg.attr('src', Form.getCaptcha(token));
                },
                showMsg: function (msg, normal) {
                    if (normal) {
                        this.$msg.find('.circle').removeClass('hide red').addClass('green');
                        this.$msg.find('.circle .sprite').removeClass('sprite-wrong').addClass('sprite-right');

                    } else {
                        this.$msg.find('.circle').removeClass('hide green').addClass('red');
                        this.$msg.find('.circle .sprite').removeClass('sprite-right').addClass('sprite-wrong');

                    }
                    this.$msg.find('.info').text(msg);
                    return this;
                },
                sendSms: function (captcha) {
                    var self = this;
                    var phone = this.$username.val();

                    if (!/^1\d{10}$/.test(phone)) {
                        return self.showMsg('请输入11位手机号');
                    }

                    if (this.__SendingSms) {
                        return;
                    }

                    this.__SendingSms = true;

                    Form.smsCodeLoginSendSms({
                        mobile: phone,
                        token: token || "",
                        captcha: captcha || "",
                        client_id: Utils.getUrlParams()['client_id']
                    }, function (result, data) {
                        self.showMsg(result ? '验证码发送成功' : data.statusText, result);
                        if (result) {
                            self.__smsSent = true;
                            self.$sendsms.addClass('disabled');
                            var total = 60,
                                oriText = self.$sendsms.text();
                            var to = setInterval(function () {
                                self.$sendsms.text(total-- + "秒后可重发");
                                if (total < 0) {
                                    self.$sendsms.text(oriText);
                                    clearInterval(to);
                                    self.__SendingSms = false;
                                    self.$sendsms.removeClass('disabled');
                                }
                            }, 1e3);
                        } else {
                            if (20257 == data.status || 20221 == data.status) {
                                CaptchaDialog.show().refreshCaptcha();
                            }
                            self.__SendingSms = false;
                        }
                    });
                }

            };

            App.init();
        }
    };
});

/**
 * reg.js
 *
 * changelog
 * 2014-06-21[13:45:10]:created
 *
 * @info yinyong,osx-x64,UTF-8,192.168.1.100,js,/Volumes/yinyong/sogou-passport-front/static/js/wap
 * @author yanni4night@gmail.com
 * @version 0.0.1
 * @since 0.0.1
 */
define('wap/reg',['./interface', './utils', './local', './dialog', '../utils', './skin'], function(Form, Utils, Local, Dialog, SuperUtils) {
	return {
		init: function() {
			var ru = Utils.getRu();
			var passParamsStr = Utils.getPassThroughParams();

			var AlertDialog = new Dialog({
				$container: $('#alertDialog')
			});
			var token;
			//发送手机短信需要的验证码
			var CaptchaDialog = new Dialog({
				$container: $('#captchaDialog'),
				onOk: function() {
					App.sendSms(this.$input.val());
				},
				init: function() {
					var self = this;
					this.$captchaImg = this.opt.$container.find('.captcha-img');
					this.$input = this.opt.$container.find('input');

					this.$captchaImg.click(function() {
						self.refreshCaptcha();
					});
				},
				onBeforeOk: function() {
					return /^\w+$/.test(this.$input.val());
				},
				onShow: function() {
					this.$input.val(null);
				}
			}, {
				refreshCaptcha: function() {
					this.$captchaImg.attr('src', Form.getCaptcha(token = SuperUtils.uuid()));
				}
			});


			var App = {
				$form: $('form'),
				$username: $('#username'),
				$password: $('#password'),
				$showpass: $('#showpass'),
				$sms: $('#sms'),
				$sendsms: $('.sendsms'),
				$msg: $('.msg'),
				__SendingSms: false,
				__smsSent: false,
				__mReging: false,
				init: function() {
					$('.backlink').click(function(e) {
						e.preventDefault();
						history.back();
					});

					$('.tologin').attr('href', '/wap/index?' + passParamsStr);

					return this.initEvt();
				},
				initEvt: function() {
					var self = this;

					//check need captcha
					self.$username.on('input', function(e) {

						var phone = e.target.value;
						if (/[^\d]/.test(phone)) {
							return self.showMsg('请输入11位手机号');
						} else {
							self.hideMsg();
						}
						if (!/^1\d{10}$/.test(phone)) {
							return;
						}

						Form.checkusername(phone, function(noexist) {
							if (!noexist) {
								$('.tologin').attr('href', '/wap/index?' + passParamsStr + "&phone=" + phone);
								AlertDialog.show();
							}
						});
					});

					//show or hide the password
					self.$showpass.change(function(e) {
						self.$password.attr('type', !this.checked ? 'password' : 'text');

						//Stupid way,whatever,tired
						if (this.checked) {
							$(this).siblings('label').find('.circle').removeClass('grey').addClass('blue');
						} else {
							$(this).siblings('label').find('.circle').removeClass('blue').addClass('grey');
						}
					});

					//send sms
					self.$sendsms.click(function(e) {
						e.preventDefault();
						self.sendSms();
					});

					//submit
					self.$form.submit(function(e) {
						e.preventDefault();
						var u = $.trim(self.$username.val());
						var p = $.trim(self.$password.val());
						var c = $.trim(self.$sms.val());

						if (!u || !p || !c) {
							return self.showMsg('请输入用户名/密码/验证码');
						}

						if (p.length < 6) {
							return self.showMsg('密码至少6位');
						}

						if (!/^1\d{10}$/.test(u)) {
							return self.showMsg('请输入11位手机号');
						}

						if (!self.__smsSent) {
							return self.showMsg('您还没有获取短信验证码');
						}
						if (self.__mReging) {
							return;
						}
						self.__mReging = true;
						return Form.register({
							captcha: c,
							username: u,
							password: p
						}, function(result, data) {
							self.__mReging = false;
							if (result) {
								self.showMsg('注册成功', true);
								ru = decodeURIComponent(ru);
								if (ru) {
									ru = ru.split('#')[0];
									if (~ru.indexOf('?')) {
										if (ru[ru.length - 1] !== '?') {
											ru += '&';
										}
									} else {
										ru += '?';
									}
									location.assign(ru + 'sgid=' + data.sgid);
								}
							} else {
								self.showMsg(data.statusText);
								if (data.status == '20221' || data.status == '20216') {
									self.$sms.empty().focus();
									CaptchaDialog.refreshCaptcha();
								} else {
									self.$password.empty().focus();
								}
							}
						});
					});

					return this;
				},
				sendSms: function(captcha) {
					var self = this;
					var phone = self.$username.val();

					if (!/^1\d{10}$/.test(phone)) {
						return self.showMsg('请输入11位手机号');
					}

					if (self.__SendingSms) {
						return;
					}

					self.__SendingSms = true;

					Form.sendsms({
						mobile: phone,
						captcha: captcha,
						token: token
					}, function(result, data) {
						self.showMsg(result ? '验证码发送成功' : data.statusText, result);
						if (result) {
							self.__smsSent = true;
							self.$sendsms.addClass('disabled');
							var total = 60,
								oriText = self.$sendsms.text();
							var to = setInterval(function() {
								self.$sendsms.text(total--+"秒后可重发");
								if (total < 0) {
									self.$sendsms.text(oriText);
									clearInterval(to);
									self.__SendingSms = false;
									self.$sendsms.removeClass('disabled');
								}
							}, 1e3);
						} else {
							if (20257 == data.status || 20221 == data.status) {
								CaptchaDialog.show().refreshCaptcha();
							}
							self.__SendingSms = false;
						}
					});
				},
				showMsg: function(msg, normal) {
					if (normal) {
						this.$msg.find('.circle').removeClass('hide red').addClass('green');
						this.$msg.find('.circle .sprite').removeClass('sprite-wrong').addClass('sprite-right');
					} else {
						this.$msg.find('.circle').removeClass('hide green').addClass('red');
						this.$msg.find('.circle .sprite').removeClass('sprite-right').addClass('sprite-wrong');
					}
					this.$msg.find('.info').text(msg);
					return this;
				},
				hideMsg: function() {
					this.$msg.find('.circle').addClass('hide');
					this.$msg.find('.info').empty();
				}
			};


			App.init();
		}
	};
});

/**
 * Copyright (C) 2014 yanni4night.com
 * findpwd.js
 *
 * changelog
 * 2014-06-23[15:51:29]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/findpwd',['./interface', './utils', './dialog', '../utils', './skin', './common'], function(Form, Utils, Dialog, SuperUtils) {
	return {
		init: function() {
			var ru = Utils.getRu();
			var passParamsStr = Utils.getPassThroughParams();
			var token;
			//发送手机短信需要的验证码
			var CaptchaDialog = new Dialog({
				$container: $('#captchaDialog'),
				onOk: function() {
					App.sendSms(this.$input.val());
				},
				init: function() {
					var self = this;
					this.$captchaImg = this.opt.$container.find('.captcha-img');
					this.$input = this.opt.$container.find('input');

					this.$captchaImg.click(function() {
						self.refreshCaptcha();
					});
				},
				onBeforeOk: function() {
					return /^\w+$/.test(this.$input.val());
				},
				onShow: function() {
					this.$input.val(null);
				}
			}, {
				refreshCaptcha: function() {
					this.$captchaImg.attr('src', Form.getCaptcha(token = SuperUtils.uuid()));
				}
			});
			var App = {
				$form: $('form'),
				$username: $('#username'),
				$sms: $('#sms'),
				$sendsms: $('.sendsms'),
				$msg: $('.msg'),
				__SendingSms: false,
				__smsSent: false,
				__mFinding: false,
				init: function() {
					$('.links a').each(function(idx, item) {
						var chref = $(item).attr('href');
						$(item).attr('href', chref.indexOf('?') == -1 ? (chref + '?' + passParamsStr) : (chref + '&' + passParamsStr));

					});

					return this.initEvt();
				},
				initEvt: function() {
					var self = this;

					//check need captcha
					self.$username.on('input', function(e) {

						var phone = e.target.value;
						if (/[^\d]/.test(phone)) {
							return self.showMsg('请输入11位手机号');
						} else {
							self.hideMsg();
						}
						if (!/^1\d{10}$/.test(phone)) {
							return;
						}

					});

					//send sms
					self.$sendsms.click(function(e) {
						e.preventDefault();
						self.sendSms();

					});

					//submit
					self.$form.submit(function(e) {
						e.preventDefault();
						var u = $.trim(self.$username.val());
						var c = $.trim(self.$sms.val());

						if (!u || !c) {
							return self.showMsg('请输入用户名/验证码');
						}

						if (!/^1\d{10}$/.test(u)) {
							return self.showMsg('请输入11位手机号');
						}

						if (!self.__smsSent) {
							return self.showMsg('您还没有获取短信验证码');
						}
						if (self.__mFinding) {
							return;
						}
						self.__mFinding = true;
						return Form.checksms({
							smscode: c,
							mobile: u,
							skin: Utils.getUrlParams()['skin'],
							ru: decodeURIComponent(ru),
							display: Utils.getUrlParams()['display'],
							client_id: Utils.getUrlParams()['client_id']
						}, function(result, data) {
							self.__mFinding = false;
							if (result) {
								location.href = data.url;
							} else {
								self.showMsg(data.statusText);
								if (data.status == '20221' || data.status == '20216') {
									self.$sms.empty().focus();
									CaptchaDialog.refreshCaptcha();
								} else {
									self.$password.empty().focus();
								}
							}
						});
					});


					return this;
				},

				showMsg: function(msg, normal) {
					if (normal) {
						this.$msg.find('.circle').removeClass('hide red').addClass('green');
						this.$msg.find('.circle .sprite').removeClass('sprite-wrong').addClass('sprite-right');
					} else {
						this.$msg.find('.circle').removeClass('hide green').addClass('red');
						this.$msg.find('.circle .sprite').removeClass('sprite-right').addClass('sprite-wrong');
					}
					this.$msg.find('.info').text(msg);
					return this;
				},
				hideMsg: function() {
					this.$msg.find('.circle').addClass('hide');
					this.$msg.find('.info').empty();
				},
				sendSms: function(captcha) {
					var self = this;
					var phone = this.$username.val();

					if (!/^1\d{10}$/.test(phone)) {
						return self.showMsg('请输入11位手机号');
					}

					if (this.__SendingSms) {
						return;
					}

					this.__SendingSms = true;

					Form.findpwdSendsms({
						mobile: phone,
						token: token || "",
						captcha: captcha || "",
						client_id: Utils.getUrlParams()['client_id']
					}, function(result, data) {
						self.showMsg(result ? '验证码发送成功' : data.statusText, result);
						if (result) {
							self.__smsSent = true;
							self.$sendsms.addClass('disabled');
							var total = 60,
								oriText = self.$sendsms.text();
							var to = setInterval(function() {
								self.$sendsms.text(total--+"秒后可重发");
								if (total < 0) {
									self.$sendsms.text(oriText);
									clearInterval(to);
									self.__SendingSms = false;
									self.$sendsms.removeClass('disabled');
								}
							}, 1e3);
						} else {
							if (20257 == data.status || 20221 == data.status) {
								CaptchaDialog.show().refreshCaptcha();
							}
							self.__SendingSms = false;
						}
					});
				}
			};

			App.init();


		}
	};
});
/**
 * Copyright (C) 2014 yanni4night.com
 * findpwd.js
 *
 * changelog
 * 2014-06-23[15:51:29]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/findpwd_other',['./interface','../lib/tpl', './utils',  './skin' , './common'], function(Form, Tpl, Utils) {
	var ru = Utils.getRu();
	var passParamsStr = Utils.getPassThroughParams();

	var App = {
		$form: $('form'),
		$username: $('#username'),
		$sms: $('#sms'),
		$sendsms: $('.sendsms'),
        $captchaImg: $('#captcha-img'),
        $captcha: $('#captcha'),
        $confirmTpl : $('#FindpwdConfirm').html(),
		$msg: $('.msg'),
		__mFinding: false,
        __currentStep:1,
        showCaptcha: function() {
            this.$captchaImg.attr('src', Form.getCaptcha(token));
        },
		init: function() {
            this.showCaptcha();
            $('.links a').each(function(idx,item){
                var chref = $(item).attr('href');
                $(item).attr('href', chref.indexOf('?') == -1 ? (chref + '?' + passParamsStr)
                             : (chref + '&' + passParamsStr));
                
            });
			return this.initEvt();
		},
		initEvt: function() {
			var self = this;

            //click refresh
            self.$captchaImg.click(function(e) {
                self.showCaptcha();
            });

			//submit
			self.$form.submit(function(e) {
				e.preventDefault();
                if(self.__currentStep==1){
				    var u = $.trim(self.$username.val());
				    var c = $.trim(self.$captcha.val());

				    if (!u || !c) {
					    return self.showMsg('请输入用户名/验证码');
				    }

				    if (self.__mFinding) {
					    return;
				    }
				    self.__mFinding = true;
				    return Form.findpwdCheck({
                        token:token,
					    captcha: c,
					    username: u,
                        ru: decodeURIComponent(ru)
				    }, function(result, data) {
					    self.__mFinding = false;

					    if (result) {
                            if(data.url && data.url.length){
                                location.href = data.url;
                            }else if(data.sec_process_email){
                                self.$form.html(Tpl(self.$confirmTpl)({
                                    email: data.sec_process_email
                                }));
                                self.__confirmData = data;
                                self.__currentStep = 2;
                            }else {
                                location.href = '/wap/findpwd/customer?' + passParamsStr;
                            }
					    } else {
						    self.showMsg(data.statusText);
                            self.$captcha.empty();
                            self.showCaptcha();
						    if (data.status == '20221' ) {
                                self.$captcha.focus();
                            }

					    }
				    });
                }else if(self.__currentStep == 2){
				    if (self.__mFinding) {
					    return;
				    }
				    self.__mFinding = true;
				    return Form.findpwdSendmail({
					    username: self.__confirmData.userid,
                        scode:self.__confirmData.scode,
                        email:self.__confirmData.sec_email,
                        skin: Utils.getUrlParams()['skin'],
                        ru: decodeURIComponent(ru)
				    }, function(result, data) {
					    self.__mFinding = false;

					    if (result) {
                            $('form').html('<p class="reset-notify">'+ (data.statusText||'找回密码邮件已发送，请尽快查看邮件并修改密码') +'</p>');
                            setTimeout(function(){
                                location.href =  decodeURIComponent(ru);//'/wap/index?' + passParamsStr;
                            },2e3);
					    } else {
						    self.showMsg(data.statusText);
                            self.$captcha.empty();
                            self.showCaptcha();
						    if (data.status == '20221' ) {
                                self.$captcha.focus();
                            }

					    }
				    });
                    
                }
			});

			return this;
		},

		showMsg: function(msg, normal) {
			if (normal) {
				this.$msg.find('.circle').removeClass('hide red').addClass('green');
				this.$msg.find('.circle .sprite').removeClass('sprite-wrong').addClass('sprite-right');
			} else {
				this.$msg.find('.circle').removeClass('hide green').addClass('red');
				this.$msg.find('.circle .sprite').removeClass('sprite-right').addClass('sprite-wrong');
			}
			this.$msg.find('.info').text(msg);
			return this;
		},
		hideMsg: function() {
			this.$msg.find('.circle').addClass('hide');
			this.$msg.find('.info').empty();
		}
    };

    return App;
});

/**
 * Copyright (C) 2014 yanni4night.com
 * findpwd.js
 *
 * changelog
 * 2014-06-23[15:51:29]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/resetpwd',['./interface', '../lib/tpl' , './utils',  './skin' , './common'], function(Form,Tpl, Utils) {
	var ru = Utils.getRu();
	var passParamsStr = Utils.getPassThroughParams();

	var App = {
		$form: $('form'),
		__mChanging: false,
		init: function() {
            this.__data = {};
            try{
                this.__data = JSON.parse(window.data);
            }catch(e){}

            if( +this.__data.status === 0 ){
                this.__data = this.__data.data;
                this.$form.html(Tpl(this.$form.find('script').html())({
                    userid:this.__data.userid
                }));
		        this.$password= $('#password');
		        this.$cpassowrd= $('#cpassword');
		        this.$msg= $('.msg');
			    return this.initEvt();
            }else{
                this.$form.html('<p class="reset-notify">'+ (this.__data.statusText|| '找回密码邮件已发送，请尽快查看邮件并修改密码')+'</p>');
                return;
            }
		},
		initEvt: function() {
			var self = this;


			//submit
			self.$form.submit(function(e) {
				e.preventDefault();
				var u = $.trim(self.$password.val());
				var c = $.trim(self.$cpassowrd.val());

				if (!u || !c) {
					return self.showMsg('请输入密码');
				}

				if (u != c) {
					return self.showMsg('确认密码不一致');
				}
				if (u.length < 6) {
					return self.showMsg('密码至少6位');
				}
				if (self.__mChanging) {
					return;
				}
                self.hideMsg();
				self.__mChanging = true;
				return Form.reset({
					password: u,
                    username: self.__data.userid,
                    scode: self.__data.scode,
                    ru:decodeURIComponent(ru),
                    client_id: Utils.getUrlParams()['client_id']
				}, function(result, data) {
					self.__mChanging = false;
					if (result) {
						self.showMsg('恭喜您，重置密码成功！', true);
                        setTimeout(function(){
                           location.assign(decodeURIComponent(ru));
                           // location.href = '/wap/index?' + passParamsStr;
                        },2e3);
					} else {
						self.showMsg(data.statusText); 
						if (data.status == '20221' || data.status == '20216') {
							self.$sms.empty().focus();
						} else {
							self.$password.empty().focus();
						}
					}
				});
			});

			return this;
		},

		showMsg: function(msg, normal) {
			if (normal) {
				this.$msg.find('.circle').removeClass('hide red').addClass('green');
				this.$msg.find('.circle .sprite').removeClass('sprite-wrong').addClass('sprite-right');
			} else {
				this.$msg.find('.circle').removeClass('hide green').addClass('red');
				this.$msg.find('.circle .sprite').removeClass('sprite-right').addClass('sprite-wrong');
			}
			this.$msg.find('.info').text(msg);
			this.$msg.removeClass('hide');
			return this;
		},
		hideMsg: function() {
			this.$msg.find('.circle').addClass('hide');
			this.$msg.find('.info').empty();
			this.$msg.addClass('hide');
		}
    };

    return App;
});

/**
 * Copyright (C) 2014 yanni4night.com
 * findpwd.js
 *
 * changelog
 * 2014-06-23[15:51:29]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define('wap/findpwd_confirm',['./interface', './utils',  './skin' , './common'], function(Form, Utils) {
	var ru = Utils.getRu();
	var passParamsStr = Utils.getPassThroughParams();

	var App = {
		$form: $('form'),
		__mFinding: false,
		init: function() {

			return this.initEvt();
		},
		initEvt: function() {
			var self = this;

			//submit
			self.$form.submit(function(e) {
				e.preventDefault();

				return Form.check({
					captcha: c,
					username: u
				}, function(result, data) {
					self.__mFinding = false;
					if (result) {
						self.showMsg('注册成功', true);
						ru = decodeURIComponent(ru);
						if (ru) {
							ru = ru.split('#')[0];
							if (~ru.indexOf('?')) {
								if (ru[ru.length - 1] !== '?') {
									ru += '&';
								}
							} else {
								ru += '?';
							}
							location.assign(ru + 'sgid=' + data.sgid);
						}
					} else {
						self.showMsg(data.statusText);
						if (data.status == '20221' || data.status == '20216') {
							self.$sms.empty().focus();
						} else {
							self.$password.empty().focus();
						}
					}
				});
			});

			return this;
		},

		showMsg: function(msg, normal) {
			if (normal) {
				this.$msg.find('.circle').removeClass('hide red').addClass('green');
				this.$msg.find('.circle .sprite').removeClass('sprite-wrong').addClass('sprite-right');
			} else {
				this.$msg.find('.circle').removeClass('hide green').addClass('red');
				this.$msg.find('.circle .sprite').removeClass('sprite-right').addClass('sprite-wrong');
			}
			this.$msg.find('.info').text(msg);
			return this;
		},
		hideMsg: function() {
			this.$msg.find('.circle').addClass('hide');
			this.$msg.find('.info').empty();
		}
    };

    return App;
});

/**
  * Copyright (C) 2014 yanni4night.com
  *
  * wap.js
  *
  * changelog
  * 2014-06-19[19:02:53]:authorized
  *
  * @info yinyong,osx-x64,UTF-8,10.129.161.40,js,/Volumes/yinyong/sogou-passport-front/static/js
  * @author yanni4night@gmail.com
  * @version 0.1.0
  * @since 0.1.0
  */
define('wap',['./wap/login','./wap/smsCodeLogin','./wap/reg','./wap/findpwd' , './wap/findpwd_other', './wap/resetpwd', './wap/findpwd_confirm'],function(Login,SmsCodeLogin,Reg,Findpwd ,FindpwdOther, Resetpwd , FindpwdConfirm){
    return {
        index_touch:function(){
            Login.init();
        },
        smscode_index_touch:function(){
            SmsCodeLogin.init();
        },
        regist_touch:function () {
          Reg.init();
        },
        findpwd_touch:function(){
          Findpwd.init();
        },
        findpwd_other_touch: function(){
            FindpwdOther.init();
        },
        findpwd_confirm_touch: function(){
            FindpwdConfirm.init();
        },
        resetpwd_touch: function(){
            Resetpwd.init();
        }
    };
});
