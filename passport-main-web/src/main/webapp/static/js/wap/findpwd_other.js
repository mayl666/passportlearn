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
define(['./interface','../lib/tpl', './utils',  './skin' , './common'], function(Form, Tpl, Utils) {
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
                                location.href = '/wap/index?' + passParamsStr;
                            },2000);
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
