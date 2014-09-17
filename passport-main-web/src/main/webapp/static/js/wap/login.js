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
define(['./interface','../lib/tpl' , './local','../lib/emitter','./utils','./skin','../lib/md5-min'], function(Form, resolve, Local,Emitter,Utils) {

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