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
define(['./interface', '../lib/tpl' , './utils',  './skin' , './common'], function(Form,Tpl, Utils) {
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
