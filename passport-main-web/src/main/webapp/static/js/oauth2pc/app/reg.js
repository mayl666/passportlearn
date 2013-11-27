/**
  * reg.js
  *
  * changelog
  * 2013-11-21[16:37:53]:copied
  *
  * @info yinyong,osx-x64,UTF-8,10.129.173.11,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/app
  * @author yinyong#sogou-inc.com
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
  //  function Reg() {};
    var Reg/*.prototype*/ = {

        constructor: Reg,

        submited: false,

        init: function() {

            this.exPassport("size", "604", "360")
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
                $input.removeClass('error').next('span.position-tips').html('');
                //验证码特殊处理
                if($input.attr('name')=='vcode'){
                    $input.next('span.chkbtn-wrap').find('span.chktext').empty()
                }
                //清除公共区域错误提示
                $('p.out-error', $form).empty().hide();
            })
            /*//点击提交按钮
            .on('click', 'button.btn', function(e) {
                $(e.delegateTarget).trigger('submit')
                return false
            })
            //阻止button按钮默认行为
            .on('click', 'button', function(e) {
                e.preventDefault();

            })*/
            //点击获取手机验证码按钮
            .on('click', 'button.chkbtn', function(e) {
                var $btn = $(this),
                    $form = $(e.delegateTarget),
                    $phone = $('input[name=account]', $form),
                    $vcode = $('input[name=vcode]', $form),
                    phoneValue = $phone.val(),
                    //yinyong@sogou-inc.com,2013-11-21[17:05:44]
                    //fixed url
                    vcodeUrl = self.sogouBaseurl +self.interfaces.sendSms,// '/a/sogou/sendsms',
                    phoneObj = self.validObj.phone;

                if ($btn.hasClass('chkbtn-disable'))  return 
            
                if (!self.check($phone, phoneObj)) return

                $.ajax({
                    url: vcodeUrl,
                    data: {
                        client_id:_g_client_id,
                        mobile:phoneValue
                        //yinyong@sogou-inc.com,2013-11-21[17:07:26]
                        //fixed params
                       /* smstype: 0,
                        phonenumber: phoneValue*/
                    },
                    type:'get' /*"post"*/,
                    dataType: "json"
                }).done(function(result) {
                    result=getJSON(result);
                    var code = +result.status/*code*/,
                        $error = $('.out-error', $form)
                        switch (code) {
                            case 0:
                                self.countdown($btn)
                                $error.empty().hide()
                                break
                                //yinyong@sogou-inc.com,2013-11-21[17:10:30]
/*                            case 3:
                                $phone.next('span.position-tips').html("手机号有误").show()
                                break
                            case 4:
                                $error.html("系统繁忙,请稍后发送").show()
                                break
                            case 5:
                                $error.html("请等待60秒后重新发送").show()
                                break*/
                            default:
                                $error.html(self.retStatus.sendSms[code] || result.statusText || "未知错误").show();
                                break
                        }
                }).fail(function(jqXHR,error){
                    $error.html('网络异常，请稍后重试').show();
                });

            })//yinyong@sogou-inc.com,2013-11-23[16:27:25]
            //error msg unclickable
            .on('click','.position-tips',function(e){
                $(e.target).hide().empty().prev('input').removeClass('error').focus();
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
                            //隐藏上次操作显示的验证码区域
                           /* if($vcodeArea.is(':visible')){
                                $vcodeArea.hide()
                            }*/
                            snameObj.legal = false;
                            return 
                        }
                        $.ajax({
                            //yinyong@sogou-inc.com,2013-11-21[17:11:17]
                            //fixed url
                            url: self.sogouBaseurl + self.interfaces.checkSname,//"/a/sogou/check/sname/" + $account.val(),
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
                                    $account.removeClass("error").next('span.position-tips').empty().hide();
                                    break;
                                default:
                                    $account.addClass("error").next('span.position-tips').show()
                                    //self.refreshVcode($img);
                                    .html(self.retStatus.checkSname[code] || result.statusText || "未知错误");;
                            }
                            
                          /*  if (result.regstatus !== 0) {
                                $vcodeArea.show()
                            }                               
                            //TODO  : vcodeArea not show here
                            // $vcodeArea.show()

                            switch(result.code){
                                case 0  :   $account.removeClass("error").next('span.position-tips').html('');
                                        snameObj.legal = true;
                                        break;
                                case 1  :   $account.addClass("error").next('span.position-tips').html('账号已被占用');
                                        snameObj.legal = false;
                                        break;
                                case 3  :   $account.addClass("error").next('span.position-tips').html('请用6-16位字符或"-"');
                                        snameObj.legal = false;
                                        break;
                                default :   $account.addClass("error").next('span.position-tips').html('账号不可用');
                                        snameObj.legal = false;
                                        break;
                            }*/
                        })
                    }
                    if(inputName == 'account'){
                        if(!self.check($account,phoneObj))  {
                            return phoneObj.legal = false;
                        }
                        $.ajax({
                            //yinyong@sogou-inc.com,2013-11-21[17:14:55]
                            //fixed url
                            url: self.sogouBaseurl + self.interfaces.checkMobile,//"/a/sogou/check/mobile/" + $account.val(),
                            type:/* "get",*/'post',
                            dataType: "json",
                            //2013-11-21[17:15:30],yinyong@sogou-inc.com
                            //add
                            data:{
                                username:$account.val()
                            }
                        }).done(function(result) {
                            result=getJSON(result);
                             var code = result.status;
                            if (code != 0) {
                                $account.addClass("error").next('span.position-tips').show().html(self.retStatus.checkSname[code] || result.statusText || "未知错误")
                            } else {
                                $account.removeClass("error").next('span.position-tips').empty().hide()
                            }
                            //yinyong@sogou-inc.com,2013-11-21[17:16:00]
                            //fixed
                           /* if (result.code !== 0) {
                                phoneObj.legal = false;
                                $account.addClass("error").next('span.position-tips').html('该手机已经注册过')
                            } else {
                                phoneObj.legal = true;
                                $account.removeClass("error").next('span.position-tips').html('')
                            }*/
                        });
                    }

                    if(inputName == 'password'){
                        if(!self.check($account,passwordObj))   {
                           // $account.addClass("error").next('span.position-tips').html('请用6-16位字符')
                            return passwordObj.legal = false;
                        }
                        else{
                            $account.removeClass("error").next('span.position-tips').html('')
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
            //yinyong@sogou-inc.com,2013-11-23[15:49:07]
            //removed simple check
            if (!this.check($password, passwordObj)/* || !this.isSimple($password, '密码过于简单')*/) {
                return this.submited = false;
            }
            var data,
            //yinyong@sogou-inc.com,2013-11-21[17:21:31]
            //fixed url
                url = this.sogouBaseurl + self.interfaces.register,//"/sogou/register",
                splus = window.splus;
                
            data = {
                /*sname*/username: $input.val(),
                /*pwd*/password: $password.val(),
                instance_id: splus ? splus.instanceid : "",
                client_id:_g_client_id,
                token:$img.attr('data-token')
            }
            if (hasVcode) {
                data./*vcode*/captcha = $vcode.val();
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
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登陆 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        break; 
                    default:
                    //yinyong@sogou-inc.com,2013-11-23[16:31:57]
                        if(/20221|20214/.test(code)){
                            $vcode.addClass('error');
                        }
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
                // console.log("error happens in http request : a/sogou/mobileregister")
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
            //yinyong@sogou-inc.com,2013-11-23[15:48:44]
            //removed simple check
            if (!this.check($password, passwordObj) /*|| !this.isSimple($password, "密码过于简单")*/) {
                return this.submited = false;
            }
            this.countdownOver($vcodeBtn);
            //yinyong@sogou-inc.com,2013-11-21[17:24:47]
            //fixed url
            var url = this.sogouBaseurl +this.interfaces.mobileRegister,// "/a/sogou/mobileregister",
                self = this,
                instanceid = splus ? splus.instanceid : ""

            $.ajax({
                url: url,
                type: "post",
                data: {
                    /*phonenumber*/username: $input.val(),
                    /*smscode*/captcha: $vcode.val(),
                    /*pwd*/password: $password.val(),
                    /*instanceid*/instance_id: instanceid,
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
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登陆 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        break;
/*                var code = result.code
                switch (code) {
                    case 0:
                        var data = result.data,
                            sname = data.sname,
                            nick = data.nick,
                            sid = data.sid,
                            passport = data.passport
                        var msg = data.logintype + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + sname + '|' + nick + '|' + sid + '|' + passport + '|1'
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登陆 | 是否保存\n ' + msg)
                        self.exPassport('result ', msg)
                        break
                    case 1:
                        $input.addClass('error').next('span.position-tips').html('该手机已注册')
                        break
                    case 3:
                        $vcode.addClass('error').next('span.chkbtn-wrap').find('>span.chktext').html('验证码错误')
                        break
                    case 4:
                        $error.show().html("参数错误")
                        break*/
                    default:
                        if (/20221|20214/.test(code)) {
                            $vcode.addClass('error').next('span.chkbtn-wrap').find('span.chktext').show().html(self.retStatus.register[code] || result.statusText || "未知错误");
                        }else
                            $error.show().html(self.retStatus.register[code] || result.statusText || "未知错误");
                        $vcode.val("");
                        $password.val("");
                        break
                }
                self.submited = false;
            }).fail(function() {
                $error.show().html('网络异常，请稍后尝试注册');
                // console.log("error happens in http request : a/sogou/mobileregister")
                self.submited = false;
            })
        },
        //校验是否为空
        checkEmpty: function($input, $error) {

            return $input.val() == '' ? ($input.addClass('error'), $error.html('不能为空'), false) : true

        },
        //刷新验证码
        refreshVcode: function($img) {
            //yinyong@sogou-inc.com,2013-11-21[17:28:32]
            //fixed url
             var ts = +new Date,
                token = utils.uuid(),
                url = '/captcha?token=' + token + '&t=' + ts;
            $img.attr("src", url).attr("data-token", token);
          /*  var self = this,
                ts = new Date().getTime(),
                url = self.sogouBaseurl + '/vcode/register/?nocache=' + ts
                $img.attr('src', url)*/
        },
        //yinyong@sogou-inc.com,2013-11-23[15:49:21]
        //removed
        //校验密码是否过于简单
/*        isSimple: function($input, msg) {
            var self = this,
                inputValue = $input.val(),
                flag = true,
                simpleMial = ['123456', '12345678', 'qwerty', 'qwaszx', 'qazwsx', 'password', 'abc123'];
            $.each(simpleMial, function(index, val) {
                if (inputValue == val) {
                    flag = false;
                }
            })
            if (flag) {
                $input.removeClass("error").next('span.position-tips').empty()

            } else {
                $input.addClass("error").next('span.position-tips').html(msg)
            }

            return flag
        },*/
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
        //校验规则及错误提示
/*        validObj: {
            account: {
                defaultMsg: "帐号/手机号/邮箱",
                errMsg: '请用6-16位字符或"-"',
                emptyMsg: '不能为空',
                nullable: false,
                legal : true,
                regStr: /^\S{6,50}$/
            },
            phone: {
                errMsg: '手机号有误',
                emptyMsg: '不能为空',
                nullable: false,
                legal : true,
                regStr: /^1\d{10,13}$/
            },
            email: {
                errMsg: '请正确输入邮箱（非必填）',
                emptyMsg: '',
                nullable: true
                // regStr: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/
            },
            password: {
                errMsg: '请用6-16位字符',
                emptyMsg: '不能为空',
                nullable: false,
                legal : true,
                regStr: /^\S{6,20}$/
            },
            vcode: {
                errMsg: '请正确输入验证码',
                emptyMsg: '不能为空',
                nullable: false,
                legal : true,
                regStr: /^\w*$/
            },
            regacc: {
                errMsg: '仅支持4-16位字母、数字及“-”',
                emptyMsg: '请填写帐号',
                nullable: false,
                legal : true,
                regStr: /^[a-zA-Z0-9-]{4,20}$/
                // regStr: /^[a-zA-Z0-9_\-\u4e00-\u9fa5]+$/
            }
        }*/
    //}
    $.extend(Reg,common);
    return Reg;
});