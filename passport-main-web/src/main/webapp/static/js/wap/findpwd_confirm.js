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
define(['./interface', './utils',  './skin' , './common'], function(Form, Utils) {
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
