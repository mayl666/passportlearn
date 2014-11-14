/**
  * reg.js
  *
  * changelog
  * 2013-11-21[16:37:53]:copied
  *
  * @info yinyong,osx-x64,UTF-8,10.129.173.11,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/app
  * @author sb
  * @version 0.0.1
  * @since 0.0.1
  */
define(['lib/md5','lib/utils','lib/common',  'lib/placeholder', 'lib/base64'], function(md5,utils,common) {
    //user login and register
    var _g_client_id=(window.splus&&splus.client_id)||1044;
    /**
     * Fix JSON
     * @param  {[type]} result [description]
     * @return {[type]}        [description]
     */
    function getJSON(result) {
        if (typeof result === 'string' || (result && result.constructor == String)) {
            try {
                result = $.parseJSON(result);
            } catch (e) {
                result = {};
            }
            return result;
        }
        return {};
    }
    var Reg = {
        constructor: Reg,
        submited: false,
        init: function() {

            this.exPassport("size", "376", "498")
            this.initEvents()

            this.refreshVcode($('.chkPic img'));
        },
        sogouBaseurl: location.origin,
        //事件代理
        initEvents: function() {
            var self = this;
            //表单提交时默认事件
            $('form').on('submit' ,function(e) {
                var $form = $(e.delegateTarget),
                    formId = $form.attr('id');
                if (formId == 'fast_reg_form') {
                    self.doFastReg($form);
                }
                if (formId == 'mobile_reg_form') {
                   self.doMobileReg($form);
                }
                e.preventDefault();
                return false;
            })
            //鼠标点击输入框 所有错误提示及样式清除
            .on('focus', 'input', function(e) {
                var $form = $(e.delegateTarget),
                    $input = $(e.target);
                //清除输入框后错误提示
                $input.removeClass('error').next('span.position-tips').hide();
                //验证码特殊处理
                if($input.attr('name')=='vcode'){
                    $input.next('span.chkbtn-wrap').find('span.chktext').hide()
                }
                //清除公共区域错误提示
                $('p.out-error', $form).empty().hide();
            })
            .on('click', 'button.chkbtn', function(e) {
                var $btn = $(this),
                    $form = $(e.delegateTarget),
                    $phone = $('input[name=account]', $form),
                    $vcode = $('input[name=vcode]', $form),
                    phoneValue = $phone.val(),
                    vcodeUrl = self.sogouBaseurl +self.interfaces.sendSms,
                    phoneObj = self.validObj.phone;

                if ($btn.hasClass('chkbtn-disable'))  return ;
            
                if (!self.check($phone, phoneObj)) return;

                $.ajax({
                    url: vcodeUrl,
                    data: {
                        client_id:_g_client_id,
                        mobile:phoneValue
                    },
                    type:'post',
                    dataType: "json"
                }).done(function(result) {
                    result=getJSON(result);
                    var code = +result.status,
                        $error = $('.out-error', $form)
                        switch (code) {
                            case 0:
                                self.countdown($btn)
                                break
                            default:
                                $error.html(self.retStatus.sendSms[code] || result.statusText || "未知错误").show();
                                break
                        }
                }).fail(function(jqXHR,error){
                    $error.html('网络异常，请稍后重试').show();
                });

            })
            .on('click','.position-tips',function(e){
                    if($(e.target).is('span.x'))
                        $(this).prev('input').val('');
                    $(this).hide().empty().prev('input').removeClass('error').focus();
            })
            //检验是否需要验证码
            .on('blur', 'input', function(e) {
                var $account = $(e.target),
                    $form = $(e.delegateTarget),
                    $vcodeArea = $('div.vcode-area',$form),

                    inputName = $account.attr('name'),
                    snameObj = self.validObj.regaccount,
                    passwordObj = self.validObj.password,
                    phoneObj = self.validObj.phone;
                
                    if(inputName == 'sname'){
                        if(!self.check($account,snameObj))  {
                            snameObj.legal = false;
                            return 
                        }
                        $.ajax({
                            url: self.sogouBaseurl + self.interfaces.checkSname,
                            type: 'post',/*"get",*/
                            dataType: "json",
                            data:{
                                username:$account.val()
                            }
                        }).done(function(result) {

                            result=getJSON(result);
                            var code = result.status;
                            switch (true) {
                                case (0 == code):
                                    break;
                                default:
                                self.showTips($account,$account.next('span.position-tips'),self.retStatus.checkSname[code] || result.statusText || "未知错误");
                            }

                        })
                    }
                    if(inputName == 'account'){
                        if(!self.check($account,phoneObj))  {
                            return phoneObj.legal = false;
                        }
                        $.ajax({
                            url: self.sogouBaseurl + self.interfaces.checkMobile,
                            type:'post',
                            dataType: "json",
                            data:{
                                username:$account.val()
                            }
                        }).done(function(result) {
                            result=getJSON(result);
                             var code = result.status;
                            if (code != 0) {
                                self.showTips($account,$account.next('span.position-tips'),self.retStatus.checkSname[code] || result.statusText || "未知错误");
                            } else {
                            }
                        });
                    }

                    if(inputName == 'password'){
                        if(!self.check($account,passwordObj))   {
                            return passwordObj.legal = false;
                        }
                        else{
                            passwordObj.legal = true;
                        }
                    }
            })
            //点击验证码图片切换验证码  
            .on('click', 'div.chkPic>img', function(e) {
                var $img = $(e.target);
                self.refreshVcode($img);
            })
            //点击"换一张"切换验证码
            .on('click', 'div.chkPic>span>a', function(e) {
                $('div.chkPic>img'/*, $form*/).trigger('click');
            })

        },
        //快速注册
        doFastReg: function($form) {
            if (this.submited) return
            var self = this,
                hasVcode,
                $input = $('input[name=sname]', $form),
                $password = $('input[name=password]', $form),
                $vcode = $('input[name=vcode]', $form),
                $error = $('.out-error', $form),
                $img = $('div.chkPic>img', $form),
                $vcodeError = $vcode.next('span.position-tips'),
                accountObj = this.validObj.regaccount,
                vcodeObj = this.validObj.vcodeObj,
                passwordObj = this.validObj.password;

            if (!this.check($input, accountObj) ) {
                return this.submited = false;

            }
            if ($vcode.is(':visible')) {
                hasVcode = true;
                if (!self.checkEmpty($vcode, $vcodeError)) {
                    return  this.submited = false;
                }
            }

            if (!this.check($password, passwordObj)/* || !this.isSimple($password, '密码过于简单')*/) {
                return this.submited = false;
            }
            var data,
                url = this.sogouBaseurl + self.interfaces.register,
                splus = window.splus;
                
            data = {
                username: $input.val(),
                password: $password.val(),
                instance_id: splus ? splus.instanceid : "",
                client_id:_g_client_id,
                token:$img.attr('data-token')
            }
            if (hasVcode) {
                data.captcha = $vcode.val();
            }

            $.ajax({
                url: url,
                type: "post",
                data: data,
                dataType: "json"
            }).done(function(result) {
                result=getJSON(result);
                var code = result.status;

                switch (true) {
                    case (0 == code):
                        var data = result.data;
                        var msg = (data.logintype || 'sogou') + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + /*sname*/ (data.sname||data.uniqname)+ '|' + /*nick*/ data.nick + '|' + data.sid + '|' + data.passport + '|' + '1';
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登录 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        self.saveHistory($input.val());
                        break; 
                    default:
                        if(/20221|20214/.test(code)){
                            //$vcode.addClass('error').;
                            self.showTips($vcode,$vcode.next('.position-tips'),self.retStatus.register[code]);
                        }else
                            $error.show().html(self.retStatus.register[code] || result.statusText || "未知错误");
                        $vcode.val("");
                        if (hasVcode) {
                            self.refreshVcode($img);
                        }
                        break;
                }
                self.submited = false;
            }).fail(function() {
                $error.show().html('网络异常，请稍后尝试注册');
                self.submited = false;
            })
        },
        //手机注册
        doMobileReg: function($form) {
            if (this.submited) return
            var $input = $('input[name=account]', $form),
                $vcode = $('input[name=vcode]', $form),
                $password = $('input[name=password]', $form),
                $error = $('.out-error', $form),
                $vcodeBtn = $('button.chkbtn',$form),
                phoneObj = this.validObj.phone,
                passwordObj = this.validObj.password,
                vcodeObj = this.validObj.vcode;

            if (!this.check($input, phoneObj)) {
                return this.submited = false;

            }
            if (!this.check($vcode, vcodeObj)) {
                return this.submited = false;

            }
            if (!this.check($password, passwordObj) /*|| !this.isSimple($password, "密码过于简单")*/) {
                return this.submited = false;
            }
            this.countdownOver($vcodeBtn);
            var url = this.sogouBaseurl +this.interfaces.mobileRegister,
                self = this,
                instanceid = splus ? splus.instanceid : ""

            $.ajax({
                url: url,
                type: "post",
                data: {
                    username: $input.val(),
                    captcha: $vcode.val(),
                    password: $password.val(),
                    instance_id: instanceid,
                    client_id:_g_client_id,
                    token:$vcode.attr('data-token')
                },
                dataType: "json"
            }).done(function(result) {
                result=getJSON(result);
                var code = result.status,data=result.data;
                switch (true) {
                    case (0 == code):
                        var msg = (data.logintype || 'sogou') + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + /*sname*/ (data.sname )+ '|' + /*nick*/ data.nick + '|' + data.sid + '|' + data.passport+ '|' + '1';
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登录 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg);
                        self.saveHistory($input.val());
                        break;
                    default:
                        if (/20221|20214/.test(code)) {
                            $vcode.addClass('error').next('span.chkbtn-wrap').find('span.chktext').show().html(self.retStatus.register[code] || result.statusText || "未知错误");
                        }else
                            $error.show().html(self.retStatus.register[code] || result.statusText || "未知错误");
                        $vcode.val("");
                        $password.val("").next('span.position-tips').hide();
                        break
                }
                self.submited = false;
            }).fail(function() {
                $error.show().html('网络异常，请稍后尝试注册');
                self.submited = false;
            })
        },
        //校验是否为空
        checkEmpty: function($input, $error) {
            var self=this;
            return $input.val() == '' ? self.showTips($input,$error,'不能为空')&&false/*($input.addClass('error'), $error.html('不能为空'), false)*/ : true;
        },
        //刷新验证码
        refreshVcode: function($img) {
            //yinyong@sogou-inc.com,2013-11-21[17:28:32]
            //fixed url
             var ts = +new Date,
                token = utils.uuid(),
                url = '/captcha?token=' + token + '&t=' + ts;
            $img.attr("src", url).attr("data-token", token);
        },
        //短信验证码倒计时
        countdown: function($btn) {
            var self = this,
                total = 60,
                interdown;

            self.interdown = setInterval(function() {
                if (total === 0) {
                    $btn.html('获取验证码').removeClass('chkbtn-disable')
                    clearInterval(self.interdown)
                } else {
                    $btn.html((total--) + '秒后重新获取').addClass('chkbtn-disable')
                }
            }, 1000)
        },
        countdownOver : function($btn){
            clearInterval(this.interdown)
            $btn.html('获取验证码').removeClass('chkbtn-disable')
        }

        }
    $.extend(Reg,common);
    return Reg;
});