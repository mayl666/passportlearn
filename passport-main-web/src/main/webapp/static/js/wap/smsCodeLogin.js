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
define(['./interface','../lib/tpl' , './local','../lib/emitter', './utils', './dialog', '../utils', './skin', './common'], function (Form, resolve, Local, Emitter, Utils, Dialog, SuperUtils) {
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
