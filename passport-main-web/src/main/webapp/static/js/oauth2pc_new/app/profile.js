/**
  * profile.js
  *
  * changelog
  * 2013-11-22[13:43:19]:modified to sohu+2
  *
  * @info yinyong,osx-x64,UTF-8,10.129.173.11,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/app
  * @author yinyong#sogou-inc.com
  * @version 0.0.2
  * @since 0.0.1
  */
;
define([/*'jquery', */'lib/md5', 'app/dialog', 'lib/placeholder', 'lib/base64', 'lib/fileupload'], function(/*$,*/ md5, dialog, upload) {
    var _g_client_id=(window.splus&&splus.client_id)||1044;
    function Profile() {};
    Profile.prototype = {
        sogouBaseurl: "//account.sogou.com",
        interfaces:{
            updateNickName: "/web/userinfo/updatenickname",
            checkSname: "/a/sogou/check/sname/",
            checkNickName:"/web/userinfo/checknickname?nickname=" ,
            sendEmail:"/web/security/sendemail",
            sendSms:"/web/security/sendsmsnew",
            bindMobile: "/web/security/bindmobile",
            updatePwd:"/web/security/updatepwd",
            updateAvatar:'/web/userinfo/uploadavatar'
        },
        init: function() {
            this.initPageEvent();
            this.initBasicProfile();
            //搜狐帐号不能修改密码和绑定
            if(!window.isSohuAccount)
            {
                this.initAccountSecure();
                if (window.isUpdatepwdUsable) {
                    this.initUpdatePassword();
                } else {
                    $(".pwd").prop("disabled", true).css('background-color', '#ccc');
                }
                this.initAccountBind();
            }
            this.initHeadPortrait();
        },
        //页面整体控制
        initPageEvent: function() {
            $("input[type='password']").on("paste", function(e) {
                return false
            })
        },
        //基本资料部分
        initBasicProfile: function() {
            var self = this,
                $sname = $("#pro_sname"),
                $snameError = $sname.next("p"),
                $nick = $("#pro_nick"),
                $nickError = $nick.next("p"),
                $successArea = ($sname.parents("div.control-group")).siblings("div.state-wrapper"),
                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : ""


            $("#pro_btn_basic").on("click", function(e) {
                e.preventDefault()
                if ($nick.val() == $nick.attr("pro_default")) return
                //               var sC = self.checkSname($sname, $snameError, self.validObj.regacc),
                var nC = self.checkNick($nick, $nickError, self.validObj.nick)
                if (nC) {
                    $.when(nC).then(function() {
                        //                            var snameLegal = self.validObj.regacc.legal,
                        var nickLegal = self.validObj.nick.legal;
                        if (nickLegal) {
                            var url = self.sogouBaseurl +self.interfaces.updateNickName;
                            $.ajax({
                                url: url,
                                data: {
                                    nickname: $nick.val(),
                                    sname: $sname.val(),
                                    accesstoken: accesstoken
                                },
                                type: "post",
                                dataType: "json"
                            }).done(function(result) {
                                if(typeof result==='string'||(result&&result.constructor==String)){
                                    result=$.parseJSON(result);
                                }
                                var code = +result.status;
                                if (code == 0) {
                                    $successArea.fadeIn("fast", function() {

                                        setTimeout(function() {
                                            $successArea.fadeOut(500, function() {

                                                $sname.removeClass("error").attr("disabled", "disabled")
                                                $snameError.empty().hide()
                                                $nick.removeClass("error").attr("pro_default", $nick.val())
                                                $nickError.empty().hide()
                                                $(".sidebar>.uesr-name").html($nick.val())
                                                try {
                                                    window.external.passport("onProfileChange")
                                                } catch (e) {
                                                    console.log("your browser do not support the method onProfileChange,please user a high version ")
                                                }

                                            });
                                        }, 1200);
                                    })

                                }
                            })
                        } else {
                            return false;
                        }
                    })
                } else {
                    return false;
                }

            })
        },
        checkSname: function($sname, $snameError, validObj) {
            var self = this
            if (!self.check($sname, $snameError, validObj)) return false
            if ($sname.val() == $sname.attr("pro_default")) return true
            return $.ajax({
                url: self.sogouBaseurl +self.interfaces.checkSname+ $sname.val(),
                type: "get",
                dataType: "json"
            }).done(function(result) {
                if (result.code !== 0) {
                    $sname.addClass("error")
                    $snameError.show().html("该帐号太受欢迎，已有人抢注了")
                    validObj.legal = false
                }
                if (result.code === 0) {
                    $sname.removeClass("error")
                    $snameError.empty().hide()
                    validObj.legal = true
                }
            })
        },
        checkNick: function($nick, $nickError, validObj) {
            var self = this,
                val = encodeURIComponent($nick.val());
            if (!self.check($nick, $nickError, validObj)) return false

            return $.ajax({
                url: self.sogouBaseurl +self.interfaces.checkNickName  + val,
                type: "get",
                dataType: "json"
            }).done(function(result) {
                if(typeof result==='string'||(result&&result.constructor==String)){
                    result=$.parseJSON(result);
                }

                var code=+result.status;

                if (code !== 0) {
                    $nick.addClass("error");
                    $nickError.show().html("该用户名不可用");
                    validObj.legal = false;
                }else {
                    $nick.removeClass("error");
                    $nickError.empty().hide();
                    validObj.legal = true;
                }
            })
        },
        //帐号安全部分
        initAccountSecure: function() {
            var self = this,
                $phone = $("#pro_phone"),
                $phoneError = $phone.siblings("p"),
                $email = $("#pro_email"),
                $emailError = $email.siblings("p"),
                $phoneBtn = $phone.next("div"),
                $emailBtn = $email.next("div"),
                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : ""

                //adaptor:email&phone cannot be disbinded.
            if (window.isBindMobileUsable) {
                self.bindPhone();
            } else {
                $phone.prop('disabled', true).css("background-color","#ccc");
            }
            if (window.isBindEmailUsable ) {
                self.bindEmail();
            } else {
                $email.prop('disabled', true).css("background-color","#ccc");
            }
        },
        bindEmail: function() {
            var self = this,

                $unactivated = $("#pro_email_area_1"),
                $prompt = $("#pro_email_area_2"),
                $bind = $("#pro_email_area_3"),
                $email = $bind.find("input"),
                $emailError = $email.siblings("p"),
                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : "",
                instanceid = splus ? splus.instanceid ? splus.instanceid : "" : "",
                $emailPwdArea = $("#pro_email_pwd_area"),
                $emailPwd = $("#pro_email_pwd"),
                $emailPwdError = $emailPwd.siblings('p'),
                $confirmBtn = $emailPwdArea.find("div.btn-wrap>a:first"),
                $cancelBtn = $emailPwdArea.find("div.btn-wrap>a:last");


            $unactivated.on("click", "div>a", function(e) {
                $bind.show()
                $unactivated.hide()
            })

            $prompt.on("click", "a#pre_btn_back", function(e) {
                // var $thisArea = e.delegateTarget
                $prompt.hide()
                $bind.show()
            }).on("click", "a#pre_btn_retry", function(e) {
                var url = self.sogouBaseurl +self.interfaces.sendEmail ,
                    $a = $(e.target),
                    email = $a.attr("pro_default")
                    $.post(self.interfaces.sendEmail, {
                        email: email,
                        accesstoken: accesstoken,
                        instanceid: instanceid
                    })
            })
            //下拉
            $bind.on("click", "div>a", function() {
                var d = self.checkEmail($email, $emailError, self.validObj.email);
                if (d) $emailPwdArea.show();
            });

            $cancelBtn.click(function() {
                $emailPwdArea.hide();
            });

            /*$bind*/
            $confirmBtn.on("click", /*"div>a", */ function(e) {
                var
                em = self.checkEmail($email, $emailError, self.validObj.email),
                    pp = self.check($emailPwd, $emailPwdError, self.validObj.password),
                    url = self.sogouBaseurl +self.interfaces.sendEmail ;
                if (em && pp) /*em.done(function(data) */ {
                    $.ajax({
                        url: url,
                        type: "post",
                        dataType: 　"json",
                        data: {
                            new_email: $email.val(),
                            accesstoken: accesstoken,
                            //                            instanceid: instanceid,
                            client_id: _g_client_id,
                            password: $emailPwd.val(),
                            ru: window.location.href
                        }
                    }).done(function(result) {
                        if ((typeof result === 'string') || (result && result.constructor == String)) {
                            result = $.parseJSON(result);
                        }
                        var codes = {
                            "10001": " 未知错误 ",
                            "10002": "参数错误,请输入必填的参数或参数验证失败",
                            "20229": "帐号未登录，请先登录 ",
                            "20243": "SOHU域用户不允许此操作",
                            "20244": "第三方帐号不允许此操作",
                            "20295": "今日绑定次数超限，请明日再试",
                            "20210": "今日密码验证失败次数超过上限",
                            "20223": "当日邮件发送次数已达上限",
                            "20285": "旧绑定邮箱错误",
                            "20206": "用户名或密码不正确",
                            "20284": "申请邮件发送失败"
                        };
                        var code = result.status;
                        switch (true) {
                            case ("0"==code):
                                $("p:first", $prompt).html("确认邮件已发送到邮箱：" + $email.val());
                                $("a#pre_btn_retry", $prompt).attr("pro_default", $email.val());
                                $bind.hide();
                                $prompt.show();
                                $emailPwdArea.hide();
                                break;
                            case (/20229|20210|20206/.test(code)):
                                $emailPwd.addClass("error");
                                $emailPwdError.html(codes[code]||result.statusText||'未知错误').addClass('red').show();
                                break;
                            default:
                                $email.addClass("error");
                                $emailError.html(codes[code]||result.statusText||'未知错误').addClass('red').show();
                        }
                    })
                } //)
            })
        },
        checkEmail: function($email, $emailError, validObj) {
            var self = this
            if (!self.check($email, $emailError, validObj)) {
                validObj.legal = false
                $emailError.addClass("red")
                return false;
            } else {
                $emailError.removeClass("red")
            }
            return true;
            /*$.ajax({
                url: self.sogouBaseurl + "/a/sogou/check/email/" + $email.val() + "/",
                type: "get",
                dataType: "json"
            }).done(function(result) {
                if (result.code === 1) {
                    $email.addClass("error")
                    $emailError.show().html("该邮箱已绑定过，请选择其他邮箱进行绑定").addClass("red")
                    validObj.legal = false
                }
                if (result.code === 0) {
                    $email.removeClass("error")
                    $emailError.empty().removeClass("red").hide()
                    validObj.legal = true
                }
            })*/
        },
        countdown: function($waitArea) {
            var self = this,
                total = 60,
                interdown

                interdown = setInterval(function() {
                    if (total === 0) {
                        $waitArea.empty().html('<a href="javascript:">获取验证码</a>')
                        clearInterval(interdown)
                    } else {
                        $waitArea.empty().html('<span style="height:25px;line-height:25px"><span style="color:#000093;font-size:12px">&nbsp;' + (total--) + '</span> 秒后重新获取验证码</span>')
                    }
                }, 1000)
        },
        //phone bind
        bindPhone: function() {
            var self = this,

                $phone = $("#pro_phone"),
                $phoneError = $phone.siblings("p"),
                $phoneBtn = $phone.next("div"),

                $vcodeErea = $("#pro_vcode_area"),
                $vcodePwdErea = $("#pro_vcode_pwd_area"),
                $vcode = $("#pro_vcode"),
                $vcodePwd = $("#pro_pwd"),
                $vcodeError = $vcode.siblings("p"),
                $vcodePwdError = $vcodePwd.siblings("p"),
                $confirmBtn = $vcodeErea.find("div.btn-wrap>a:first"),
                $cancelBtn = $vcodeErea.find("div.btn-wrap>a:last"),

                $waitArea = $vcodeErea.find("div.code"),

                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : ""


            $phoneBtn.on("click", function(e) {
                var cP = self.checkPhone($phone, $phoneError, self.validObj.phone)
                if (cP) /*cP.done(function()*/ {

                    // if (!self.validObj.phone.legal) return
                    $vcodeErea.show();
                    $vcodePwdErea.show();
                } /*)*/

            })
            $("fieldset.bind").on("click", "div.code>a", function(e) {
                e.stopPropagation()
                var url = self.sogouBaseurl +self.interfaces.sendSms ;
                $.ajax({
                    url: url,
                    data: {
                        smstype: 1,
                        client_id: _g_client_id,
                        new_mobile: $phone.val()
                    },
                    type: "post",
                    dataType: "json"
                }).done(function(result) {
                    if (typeof result === 'string' || (result && result.constructor == String))
                        result = $.parseJSON(result);

                    var code = +result.status;
                    switch (code) {
                        case 0:
                            self.countdown($waitArea)
                            $vcodeError.html("手机可以用来找回密码以及登录").removeClass("red");
                            break;
                        case 10002:
                            $vcodeError.html("参数错误").show().addClass("red");
                            break;
                        case 20229:
                            $vcodeError.html("帐号未登录，请先登录").show().addClass("red");
                            break;
                        case 20243:
                            $vcodeError.html("SOHU域用户不允许此操作").show().addClass("red");
                            break;
                        case 20244:
                            $vcodeError.html("第三方帐号不允许此操作").show().addClass("red");
                            break;
                        case 20225:
                            $vcodeError.html("手机号已绑定其他帐号").show().addClass("red")
                            break
                        case 20209:
                            $vcodeError.html("今日验证码校验错误次数已超过上限").show().addClass("red")
                            break
                        case 20202:
                            $vcodeError.html("今日手机短信发送次数超过上限").show().addClass("red")
                            break
                        case 20204:
                            $vcodeError.html("一分钟内只能发一条短信").show().addClass("red")
                            break
                        case 20213:
                            $vcodeError.html("手机验证码发送失败").show().addClass("red")
                            break
                        default:
                            $vcodeError.html("未知错误").show().addClass("red")
                            break
                    }
                })
            });
            $cancelBtn.on("click", function(e) {
                $phone.val("")
                $vcode.val("")
                $vcodeErea.hide()
                $vcodeError.empty().removeClass("red").hide()
                $phoneError.empty().hide()
            });
            //confirm button
            $confirmBtn.on("click", function(e) {
                var cP = self.checkPhone($phone, $phoneError, self.validObj.phone),
                    vP = self.check($vcode, $vcodeError, self.validObj.vcode),
                    pP = self.check($vcodePwd, $vcodePwdError, self.validObj.password);
                if (vP) {
                    $vcodeError.removeClass("red")
                } else {
                    $vcodeError.addClass("red")
                    return
                }
                //check password
                if (pP) {
                    $vcodePwdError.removeClass("red")
                } else {
                    $vcodePwdError.addClass("red")
                    return
                }
                if (cP && pP) /*cP.done(function(data)*/ {
                    var url = self.sogouBaseurl +self.interfaces.bindMobile;
                    $.ajax({
                        url: url,
                        data: {
                            new_mobile: $phone.val(),
                            smscode: $vcode.val(),
                            client_id: _g_client_id,
                            password: $vcodePwd.val(),
                            accesstoken: accesstoken
                        },
                        dataType: "json",
                        type: "post"
                    }).done(function(result) {

                        var codes = {
                            "10001": " 未知错误  ",
                            "10002": " 参数错误,请输入必填的参数或参数验证失败 ",
                            "20208":"新手机验证码错误或已过期",
                            "20229": "帐号未登录，请先登录 ",
                            "20243": " SOHU域用户不允许此操作",
                            "20244": " 第三方帐号不允许此操作",
                            "20245": " 手机帐号不允许此操作",
                            "20295": " 今日绑定次数超限，请明日再试",
                            "20210": " 今日密码验证失败次数超过上限",
                            "20206": " 用户名或密码不正确",
                            "20225": " 手机号已绑定其他帐号",
                            "20209": " 今日验证码校验错误次数已超过上限",
                            "20216": " 验证码错误或已过期",
                            "20289": " 绑定手机失败"
                        };
                        if (typeof result === 'string' || (result && result.constructor == String))
                            {
                                result = $.parseJSON(result)||{};
                            }
                        var code = result.status;
                        switch (true) {
                            case (code == "0"):
                                window.location.reload();
                                break;
                            case (/(20210|20206)/.test(code)):
                                $vcodePwd.addClass("error");
                                $vcodePwdError.addClass('red').html(codes[code]).show();
                                break;
                            case (/(20209|20216)/.test(code)):
                                $vcode.addClass("error");
                                $vcodeError.addClass('red').html(codes[code]).show();
                                break;
                            default:
                                $phone.addClass("error");
                                $phoneError.addClass('red').html(codes[code] || "未知错误").show();;
                        }
                        /*                        if (result.code == 0) {
                            window.location.href = window.location.href
                        } else if (result.code == 1) {
                            $phone.addClass("error")
                            $phoneError.html("该号码已被他人绑定").show()
                        } else if (result.code == 13) {
                            $vcode.addClass("error")
                            $vcodeError.html("验证行为异常，请明天再来进行验证").show().addClass("red")
                        } else {
                            $vcode.addClass("error")
                            $vcodeError.html("验证码错误").show().addClass("red")
                        }*/
                    })
                } //)

            })
        },
        //check phone 
        checkPhone: function($phone, $phoneError, validObj) {
            var self = this
            if (!self.check($phone, $phoneError, validObj)) {
                $phoneError.addClass("red")
                validObj.legal = false
                return false
            } else {
                $phoneError.removeClass("red")
            }
            return true;
            /*$.ajax({
                url: self.sogouBaseurl + "/a/sogou/check/mobile/" + $phone.val(),
                type: "get",
                dataType: "json"
            }).done(function(result) {
                if (result.code !== 0) {
                    $phone.addClass("error")
                    $phoneError.addClass("red")
                    $phoneError.show().html("该手机号已绑定过,请选择其他手机号码")
                    validObj.legal = false
                }
                if (result.code === 0) {
                    validObj.legal = true
                    $phone.removeClass("error")
                    $phoneError.removeClass("red")
                    $phoneError.empty().hide()
                }
            })*/
        },
        // 密码修改部分
        initUpdatePassword: function() {
            var self = this,
                $oriPwd = $("#pro_ori_pass"),
                $newPwd = $("#pro_new_pass"),
                $conPwd = $("#pro_con_pass"),
                $btn = $("#pro_btn_pass"),
                $successArea = ($oriPwd.parents("div.control-group")).siblings("div.state-wrapper"),
                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : "",
                pwdObj = self.validObj.password;

                $newPwd.on('blur',function(e){
                   if(!self.check($(this),$(this).next('p'),pwdObj))
                   {
                    $(this).next('p').show().addClass('red');
                   }
                });   

                $conPwd.on('blur',function(e){
                    if($(this).val()!=$newPwd.val())
                    {
                        $(this).next('p').text("密码不一致").show().addClass('red');
                        return;
                    }
                   if(!self.check($(thisArea),$(this).next('p'),pwdObj))
                   {
                        $(this).next('p').show().addClass('red');
                   }
                });

                $btn.on("click", function(e) {
                    e.preventDefault()

                    self.checkPassword($oriPwd, $newPwd, $conPwd, pwdObj)
                    if (pwdObj.legal) {
                        $.ajax({
                            url: self.sogouBaseurl + self.interfaces.updatePwd,
                            type: "post",
                            dataType: "json",
                            data: {
                                password: $oriPwd.val(),
                                newpwd: $newPwd.val(),
                                client_id:_g_client_id,
                                accesstoken: accesstoken
                            }
                        }).done(function(result) {
                            if(typeof result==='string'||(result&&result.constructor==String))
                            {
                                result=$.parseJSON(result);
                            }
                        var code = result.status;
                        var codes = {
                            "10001": "未知错误 ",
                            "10002": "参数错误,请输入必填的参数或参数验证失败  ",
                            "10009": "帐号不存在或异常 ",
                            "10010":  "client_id不存在",
                            "20198": "当日用户原密码校验错误次数已达上限 ",
                            "20205": "帐号不存在 ",
                            "20206": "密码错误 ",
                            "20218": "重置密码失败 ",
                            "20221": "验证码验证失败 ",
                            "20222": "当日修改或重置密码次数已达上限 ",
                            "20230": "登录用户或者ip在黑名单中 ",
                            "20244": "第三方帐号不允许此操作 "
                        };
                            switch (true) {
                                case ("0"==code):
                                    $successArea.fadeIn("fast", function() {
                                        setTimeout(function() {
                                            $successArea.fadeOut(500);
                                        }, 1200);
                                    })
                                    $oriPwd.val("").removeClass("error")
                                    $newPwd.val("").removeClass("error")
                                    $conPwd.val("").removeClass("error")
                                    $oriPwd.next("p").empty().hide()
                                    $newPwd.next("p").empty().hide()
                                    $conPwd.next("p").empty().hide()
                                    break;
                                case (/20206/.test(code)):
                                    $oriPwd.next("p").html(codes[code]||"未知错误").show().addClass('red')
                                    break;
                                default:
                                    $conPwd.next("p").html(codes[code]||"未知错误").show().addClass('red')
                                    ;
                              /*  case 1:
                                    $oriPwd.val("")
                                    $newPwd.val("")
                                    $conPwd.val("")
                                    $conPwd.next("p").html("修改行为异常，请明天再来进行验证").show()
                                    break
                                case 2:
                                    $oriPwd.val("")
                                    $conPwd.val("")
                                    $newPwd.next("p").html("该密码不可用").show()
                                    $newPwd.addClass("error").focus().val("")
                                    break
                                case 3:
                                    $newPwd.val("")
                                    $conPwd.val("")
                                    $oriPwd.next("p").html("原密码填写错误").show()
                                    $oriPwd.addClass("error").focus().val("")
                                    break
                                case 4:
                                    $newPwd.val("")
                                    $conPwd.val("")
                                    $conPwd.next("p").html("修改密码失败,请重新填写").show()
                                    $oriPwd.focus().val("")
                                    break
                                default:
                                    break*/
                            }
                        })
                    }
                });

        },
        checkPassword: function($oriPwd, $newPwd, $conPwd, pwdObj) {
            var self = this,
                $oriError = $oriPwd.next("p"),
                $newError = $newPwd.next("p"),
                $conError = $conPwd.next("p")

                var oriObj = $.extend({}, pwdObj, {
                    emptyMsg: "请填写密码"
                }),
                newObj = $.extend({}, pwdObj, {
                    emptyMsg: "请填写新密码"
                }),
                conObj = $.extend({}, pwdObj, {
                    emptyMsg: "请填写确认密码"
                })

                if (!self.check($oriPwd, $oriError, oriObj)) {
                    pwdObj.legal = false
                    return false
                }
            if (!self.check($newPwd, $newError, newObj)) {
                pwdObj.legal = false
                return false
            }
            if (!self.check($conPwd, $conError, conObj)) {

                pwdObj.legal = false
                return false
            }
            if ($newPwd.val() !== $conPwd.val()) {
                $conPwd.addClass("error")
                $conError.html("两次密码输入不一致，请重新填写").show()
                pwdObj.legal = false
                return false
            } else {
                $newPwd.removeClass("error")
                $conError.empty().hide()
                pwdObj.legal = true
                return true
            }

        },
        //帐号解绑
        initAccountBind: function() {
            var self = this,
                $lis = $("#pro_bind_list>ul>li"),
                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : ""

            $lis.each(function(index, ele) {
                var $ele = $(ele),
                    $a = $ele.find("a")
                    if ($a.hasClass("unbind-btn")) {
                        var identify = $a.attr("identify")
                        $a.on("click", function(e) {

                            e.stopPropagation()
                            var identify = $(this).attr("identify"),
                                msg = "解除绑定后,将不能用该帐号进行登录,该帐号的信息也将消失,是否确定解除绑定?"
                            dialog.confirm(msg, function() {
                                $.ajax({
                                    url: self.sogouBaseurl + "/a/sogou/bind/remove/" + identify,
                                    data: {
                                        accesstoken: accesstoken
                                    },
                                    dataType: "json",
                                    type: "post"
                                }).done(function(result) {
                                    if (result.code == 0) {
                                        $a.removeClass("unbind-btn")
                                        var href = window.location.href
                                        window.location.href = href
                                    } else if (result.code == 2) {
                                        dialog.alert("当前帐号只绑定了一个社交帐号，无法解除绑定")
                                    } else if (result.code == 1) {
                                        dialog.alert("解绑失败")
                                    }
                                })
                            })
                        })
                    }

            })

            //帐号绑定时如果遇到失败则会弹窗提示
            if (splus && splus.bind_tipmsg) {
                dialog.alert(splus.bind_tipmsg)
            }
        },
        // isSupportXHR2: window.XMLHttpRequest && 'upload' in new XMLHttpRequest(),
        //修改头像
        initHeadPortrait: function() {
            var self = this,
                $imginput = $("#pro_file_input"),
                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : "",
                token = splus ? splus.avatar_token ? splus.avatar_token : "" : "",
                url = self.sogouBaseurl + self.interfaces.updateAvatar;
                $imginput.fileupload({
                    url: url,
                    dataType: "json",
                    maxFileSize: 1024 * 1024 * 5,
                    contentType: false,
                formData: {
                    client_id: _g_client_id
                },
                    acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
                    done: function(e,result) {
                        result=result.result;
                        if(typeof result==='string'||(result&&result.constructor==String))
                        {
                            result=$.parseJSON(result)||{};
                        }
                        var code=result.status;

                        if (0==code) {
                            $("aside.sidebar div.photo-error").empty().hide()
                            $("aside.sidebar div.uesr-photo").removeClass("uesr-photo-error")
                            try {
                                window.external.passport("onProfileChange")
                            } catch (e) {
                                console.log("your browser do not support the method onProfileChange,please user a high version ")
                            }
                            window.location.reload();
                        }else {

                            $("aside.sidebar div.photo-error").html("上传头像失败,仅支持小于3M的jpg、gif、png图片文件").show()
                            $("aside.sidebar div.uesr-photo").addClass("uesr-photo-error")
                        }
                    }

                });

        },
        isSimple: function($input, $error, msg) {
            var self = this,
                flag = true,
                simpleMial = ['123456', '12345678', 'qwerty', 'qwaszx', 'qazwsx', 'password', 'abc123']
                $.each(simpleMial, function(index, val) {
                    if ($input.val() == val) {
                        $error.show().html(msg)
                        $input.addClass("error").one("blur", function(e) {
                            self.isSimple($input, $error, msg)
                        }).one("focus", function(e) {
                            $input.removeClass("error")
                            $error.empty().hide()
                        })
                        flag = false
                        return false
                    }
                })
                if (flag) {
                    $input.removeClass("error")
                    $error.empty().hide()
                }
            return flag
        },
        check: function($input, $error, valid) {
            var self = this,
                nullable = valid.nullable,
                regrex = valid.regStr,
                emptyMsg = valid.emptyMsg,
                errMsg = valid.errMsg
            if (!nullable && $input.val() == "") {
                $input.addClass("error")
                // .val("")
                .one("focus", function() {
                    $error.empty().hide()
                    $input.removeClass("error")
                })
                //when id is pro_phone or pro_email ,do not need check when the onblur event happens
                if ($input.attr("id") != "pro_phone" && $input.attr("id") != "pro_email") {
                    $input.one("blur", function() {
                        self.check($input, $error, valid)
                    })
                }
                $error.show().html(emptyMsg).addClass('red');
                return false
            } else if (nullable && $input.val() == "") {
                $input.removeClass("error")
                $error.empty().hide()
                return true
            } else {
                if (!regrex.test($input.val())) {
                    $input.addClass("error")
                        .one("focus", function() {
                            $error.empty().hide()
                            $input.removeClass("error")
                        })
                    //when id is pro_phone or  pro_email ,do not need check when the onblur event happens
                    if ($input.attr("id") != "pro_phone" && $input.attr("id") != "pro_email") {
                        $input.one("blur", function() {
                            self.check($input, $error, valid)
                        })
                    }
                    $error.show().html(errMsg).addClass('red');
                    return false
                } else {
                    $input.removeClass("error");
                    $error.empty().hide().removeClass('red;');
                    return true
                }
            }

        },
        validObj: {
            phone: {
                errMsg: '请正确填写手机号',
                emptyMsg: '手机号不能为空',
                nullable: false,
                regStr: /^1\d{10}$/,
                legal: true
            },
            email: {
                errMsg: '请正确输入邮箱',
                emptyMsg: '请输入邮箱',
                nullable: false,
                regStr: /^(\w)+(\.\w+)*@([\w_\-])+((\.\w+)+)$/,
                legal: true
            },
            password: {
                errMsg: '密码位数为6-16位，仅限数字、字母和字符，字母区分大小写',
                emptyMsg: '请正确输入密码',
                nullable: false,
                regStr: /^\w{6,16}$/,
                legal: true
            },
            vcode: {
                errMsg: '请正确输入验证码',
                emptyMsg: '验证码不能为空',
                nullable: false,
                regStr: /^\w*$/,
                legal: true
            },
            regacc: {
                errMsg: '仅支持4-16位字母、数字及“-”',
                emptyMsg: '请填写帐号',
                nullable: false,
                regStr: /^[a-zA-Z0-9-]{4,16}$/,
                legal: true
            },
            nick: {
                errMsg: '用户名为2-12位中英文字符',
                emptyMsg: '请填写用户名',
                nullable: false,
                regStr: /^([a-zA-Z0-9]|[\u4e00-\u9fa5]){2,12}$/,
                legal: true
            }
        }


    }
    return Profile
});