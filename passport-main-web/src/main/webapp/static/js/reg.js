/*
 * reg module script
 * @author zhengxin
 */



define('reg', ['./common', './form', './conf', './utils', './tpl', './ui', './lib/jquery.placeholder'], function(common, form, conf, utils, ursa, ui) {

    var reg_data = {};

    var createSpan = function($el, className) {
        if (!$el.parent().parent().find('.' + className).length) {
            $el.parent().parent().append('<span class="' + className + '"></span>');
        }
    };
    var getSpan = function($el, className) {
        return $el.parent().parent().find('.' + className);
    };
    var errorUnames = {};
    var checkUsername = function($el, cb) {
        var ipt = $el.find('input[name="username"]');
        if (!ipt || !ipt.length) {
            cb && cb(0);
            return;
        }
        if (!ipt.val().length) {
            cb && cb(-1);
            return;
        }
        var errSpan = getSpan(ipt, 'error');

        if (errSpan && errSpan.length && errSpan.css('display') != 'none') {
            cb && cb(-1);
            return;
        }

        function showError(text) {
            createSpan(ipt, 'error');
            getSpan(ipt, 'error').show().html(text);
            getSpan(ipt, 'desc').hide();
        }

        if (errorUnames[ipt.val()]) {
            showError(errorUnames[ipt.val()]);
            cb && cb(1);
            return;
        }
        $.get('/web/account/checkusername', {
            username: ipt.val(),
            t: +new Date()
        }, function(data) {
            data = utils.parseResponse(data);
            if (!+data.status) { //success
                cb && cb(0);
            } else {
                showError(data.statusText);
                errorUnames[ipt.val()] = data.statusText;
                cb && cb(1);
            }
        });

    };


    var bindFormEvent = function(type) {
        var $el = $('.main-content .form form');

        form.render($el, {
            onformfail: function() {
                checkUsername($el);
                return false;
            },
            onbeforesubmit: function() {
                checkUsername($el, function(status) {
                    if (!status) {

                        var isdescchecked = $el.find('.form-desc input').prop('checked');
                        if (!isdescchecked) {
                            form.showFormError('请阅读协议');
                            return;
                        }

                        $.post($el.attr('action'), $el.serialize(), function(data) {
                            data = utils.parseResponse(data);
                            if (!+data.status) {
                                formsuccess[type] && formsuccess[type]($el, data);
                            } else {
                                if (+data.status == 20221) {
                                    var token = $el.find('.token').val();
                                    $el.find('.vpic img').attr('src', "/captcha?token=" + token + '&t=' + (+new Date()));
                                }
                                form.showFormError(data.statusText);
                            }
                        });
                    }
                    return false;
                });
            }
        });
        $el.append('<input type="hidden" name="ru" value="' + (reg_data.ru || '') + '" class="ru"/>');

        //$el.find(".nick-holder").placeholder();

        $el.find('input[name=username]').blur(function() {
            var errorspan = $(this).parent().parent().find('.error');
            if (!errorspan || !errorspan.length || errorspan.css('display') == 'none') {
                setTimeout(function() {
                    checkUsername($el);
                }, 100);
            }
        });

    };


    var formsuccess = {
        common: function($el, data) {
            var ru = data.data.ru;
            location.href = ru;
            /*    window['rucallback'] = function(data){
                if( !+data.status ){
                    if( ru ){
                        location.href = ru;
                    }
                }else{
                    alert('系统错误');
                }
            };
            utils.addIframe(data.data.cookieUrl);*/

        },
        nick: function($el, data) {
            formsuccess.common($el, data);
        },
        tel: function($el, data) {
            formsuccess.common($el, data);
        },
        email: function($el) {
            $('.main-content .step').addClass('step2');
            $el.parent().html(ursa.render($('#Target3').html(), {
                sec_email: $el.find('input[name="username"]').val()
            }));
            common.bindJumpEmail();
        }
    };


    function initRemind(data) {
        $('#JumpTarget').html(data.email || '');
        common.bindJumpEmail();
        common.bindResendEmail(data);
    }
    /**
     * 依据状态码引导用户
     * @param  {[type]} data [description]
     * @return {[type]}      [description]
     */
    function initEmailFailure(data) {
        var code = +data.code,
            counter = 5;
        if (10002 === code || 10010 === code || 20205 === code) {
            $('#msg').html('如果您已经激活搜狗通行证，请立即<a href="/web/webLogin">登录</a>
如果您48小时内未激活，请重新<a href="/web/reg/email">注册</a>。');
        } else if (20219 === code) {
            setInterval(function() {
                $('#msg').text(counter + "秒后返回登录页");
                if (!counter--) {
                    location.href = '/web/webLogin';
                }
            }, 1000);
        } else if (20220 === code) {
            setInterval(function() {
                $('#msg').text(counter + "秒后返回邮箱注册页");
                if (!counter--) {
                    location.href = '/web/reg/email';
                }
            }, 1000);
        } else {
            $('#msg').text('激活失败，请重新<a href="/web/reg/email">注册</a>');
        }
    }

    return {
        init: function(type) {
            try {
                reg_data = $.evalJSON(server_data).data || {};
            } catch (e) {
                window['console'] && console.log(e);
            }


            common.addUrlCommon(reg_data);

            common.showBannerUnderLine();

            bindFormEvent(type);
            $('.nav').show();

            form.initTel();

            ui.checkbox('#ConfirmChb');

            if (type == 'remind') {
                initRemind(reg_data);
            } else if ('emailfailure' === type) {
                initEmailFailure(reg_data);
            }
        }
    };
});