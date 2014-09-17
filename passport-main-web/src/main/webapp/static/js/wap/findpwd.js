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
define(['./interface', './utils', './dialog', '../utils', './skin', './common'], function(Form, Utils, Dialog, SuperUtils) {
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
							ru: decodeURIComponent(ru)
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
						captcha: captcha || ""
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