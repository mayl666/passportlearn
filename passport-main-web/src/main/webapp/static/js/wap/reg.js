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
define(['./interface', './utils', './local', './dialog', '../utils', './skin'], function(Form, Utils, Local, Dialog, SuperUtils) {
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