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
define([], function() {

    var client_id = 1024;
    var noop = function() {};

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
            success: function(data) {
                if ('string' === typeof data) {
                    try {
                        data = JSON.parse(data);
                    } catch (e) {}
                }

                if (data && data.data && data.data.needCaptcha) {
                    return callback(true);
                } else {
                    return callback(false);
                }
            },
            error: function() {
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
            error: function() {
                return callback(false, {
                    'statusText': '登录失败'
                });
            },
            success: function(data) {
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
            success: function(data) {
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
            success: function(data) {

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
            error: function() {
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
            success: function(data) {

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
            error: function() {
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
            success: function(data) {

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
            error: function() {
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
            success: function(data) {

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
            error: function() {
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
            success: function(data) {

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
            error: function() {
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
            success: function(data) {

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
            error: function() {
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
            success: function(data) {

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
            error: function() {
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
        checkusername: checkusername,
        sendsms: sendsms,
        checksms: checksms,
        findpwdCheck: findpwdCheck,
        findpwdSendmail: findpwdSendmail,
        register: register,
        reset: reset,
        findpwdSendsms: findpwdSendsms
    };
});