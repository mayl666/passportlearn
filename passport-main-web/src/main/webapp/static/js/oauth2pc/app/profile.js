;
define(['jquery', 'lib/md5', 'app/dialog', 'lib/placeholder', 'lib/base64', 'lib/fileupload'], function($, md5, dialog, upload) {
    function Profile() {}
    Profile.prototype = {
        sogouBaseurl: "//plus.sohu.com",
        init: function() {
            this.initPageEvent()
            this.initBasicProfile()
            this.initAccountSecure()
            this.initUpdatePassword()
            this.initAccountBind()
            this.initHeadPortrait()
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
                if ($sname.val() == $sname.attr("pro_default") && $nick.val() == $nick.attr("pro_default")) return
                var sC = self.checkSname($sname, $snameError, self.validObj.regacc),
                    nC = self.checkNick($nick, $nickError, self.validObj.nick)
                    if (sC && nC) {
                        $.when(sC, nC).then(function() {
                            var snameLegal = self.validObj.regacc.legal,
                                nickLegal = self.validObj.nick.legal

                            if (snameLegal && nickLegal) {
                                var url = self.sogouBaseurl + "/a/sogou/profile/basic/update?_input_encode=utf-8"
                                $.ajax({
                                    url: url,
                                    data: {
                                        nick: $nick.val(),
                                        sname: $sname.val(),
                                        accesstoken: accesstoken
                                    },
                                    type: "post",
                                    dataType: "json"
                                }).done(function(result) {
                                    var code = result.code
                                    if (code == 0) {
                                        $successArea.fadeIn("fast", function() {
                                           
                                            setTimeout(function() {    
                                                $successArea.fadeOut(500, function() {

                                                    $sname.removeClass("error").attr("disabled", "disabled")
                                                    $snameError.empty().hide()
                                                    $nick.removeClass("error").attr("pro_default",$nick.val())
                                                    $nickError.empty().hide()
                                                    $(".sidebar>.uesr-name").html($nick.val())
                                                    try{
                                                        window.external.passport("onProfileChange")   
                                                    }catch(e){
                                                        console.log("your browser do not support the method onProfileChange,please user a high version ")
                                                    }
                                               
                                                });
                                            }, 1200);
                                        })

                                    }
                                })
                            } else {
                                return false
                            }
                        })
                    } else {
                        return false
                    }

            })
        },
        checkSname: function($sname, $snameError, validObj) {
            var self = this
            if (!self.check($sname, $snameError, validObj)) return false
            if ($sname.val() == $sname.attr("pro_default")) return true
            return $.ajax({
                url: self.sogouBaseurl + "/a/sogou/check/sname/" + $sname.val(),
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
                val = encodeURIComponent($nick.val())
                if (!self.check($nick, $nickError, validObj)) return false

            return $.ajax({
                url: self.sogouBaseurl + "/a/sogou/check/nick/" + val,
                type: "get",
                dataType: "json"
            }).done(function(result) {
                if (result.code !== 0) {
                    $nick.addClass("error")
                    $nickError.show().html("该用户名不可用")
                    validObj.legal = false
                }
                if (result.code === 0) {
                    $nick.removeClass("error")
                    $nickError.empty().hide()
                    validObj.legal = true
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

            if ($phone.is(":disabled")) {
                var phoneNum = $phone.val()
                $phoneBtn.on("click", function(e) {
                    e.stopPropagation()
                    var msg = "确认后将解除手机号" + phoneNum + "与该账号的绑定",
                        unbindPhoneUrl = self.sogouBaseurl + "/a/sogou/security/phone/remove"
                    dialog.confirm(msg, function() {
                        $.ajax({
                            url: unbindPhoneUrl,
                            data: {
                                phonenumber: phoneNum,
                                accesstoken: 　accesstoken
                            },
                            type: "post",
                            dataType: "json"
                        }).done(function(result) {
                            if (result.code == 0) {
                                window.location.href = window.location.href
                            }

                        })
                    })
                })
            } else {
                self.bindPhone()
            }
            self.bindEmail()
        },
        bindEmail: function() {
            var self = this,
                $unactivated = $("#pro_email_area_1"),
                $prompt = $("#pro_email_area_2"),
                $bind = $("#pro_email_area_3"),
                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : "",
                instanceid = splus ? splus.instanceid ? splus.instanceid : "" : ""



            $unactivated.on("click", "div>a", function(e) {
                $bind.show()
                $unactivated.hide()
            })

            $prompt.on("click", "a#pre_btn_back", function(e) {
                // var $thisArea = e.delegateTarget
                $prompt.hide()
                $bind.show()
            }).on("click", "a#pre_btn_retry", function(e) {
                var url = self.sogouBaseurl + "/a/sogou/security/email/send",
                    $a = $(e.target),
                    email = $a.attr("pro_default")
                    $.post("/a/sogou/security/email/send", {
                        email: email,
                        accesstoken: accesstoken,
                        instanceid: instanceid
                    })
            })


            $bind.on("click", "div>a", function(e) {
                var $email = $bind.find("input"),
                    $emailError = $email.siblings("p"),
                    validObj = self.validObj.email,
                    em = self.checkEmail($email, $emailError, validObj),
                    url = self.sogouBaseurl + "/a/sogou/security/email/modify"
                if (em) em.done(function(data) {
                    if (!validObj.legal) return
                    $.ajax({
                        url: url,
                        type: "post",
                        dataType: 　
                        "json",
                        data: {
                            email: $email.val(),
                            accesstoken: accesstoken,
                            instanceid: instanceid
                        }
                    }).done(function(result) {
                        var email = result.data.email
                        $("p:first", $prompt).html("确认邮件已发送到邮箱：" + email)
                        $("a#pre_btn_retry", $prompt).attr("pro_default", email)
                        $bind.hide()
                        $prompt.show()
                    })
                })
            })
        },
        checkEmail: function($email, $emailError, validObj) {
            var self = this
            if (!self.check($email, $emailError, validObj)) {
                validObj.legal = false
                $emailError.addClass("red")
                return
            } else {
                $emailError.removeClass("red")
            }
            return $.ajax({
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
            })
        },
        countdown: function($waitArea) {
            var self = this,
                total = 60,
                interdown 
    
                interdown = setInterval(function(){
                    if(total === 0){
                        $waitArea.empty().html('<a href="javascript:">获取验证码</a>')
                        clearInterval(interdown)
                    }else{
                        $waitArea.empty().html('<span style="height:25px;line-height:25px"><span style="color:#000093;font-size:12px">&nbsp;'+(total--)+'</span> 秒后重新获取验证码</span>')
                    }
                },1000)
        },
        //phone bind
        bindPhone: function() {
            var self = this,

                $phone = $("#pro_phone"),
                $phoneError = $phone.siblings("p"),
                $phoneBtn = $phone.next("div"),

                $vcodeErea = $("#pro_vcode_area"),
                $vcode = $("#pro_vcode"),
                $vcodeError = $vcode.siblings("p"),
                $confirmBtn = $vcodeErea.find("div.btn-wrap>a:first"),
                $cancelBtn = $vcodeErea.find("div.btn-wrap>a:last"),
                
                $waitArea = $vcodeErea.find("div.code"),

                accesstoken = splus ? splus.accesstoken ? splus.accesstoken : "" : ""


            $phoneBtn.on("click", function(e) {
                var cP = self.checkPhone($phone, $phoneError, self.validObj.phone)
                if (cP) cP.done(function() {

                    if (!self.validObj.phone.legal) return
                    $vcodeErea.show()
                })

            })
            $("fieldset.bind").on("click","div.code>a",function(e){
                e.stopPropagation()
                var url = self.sogouBaseurl + "/a/sogou/sendsms/"
                $.ajax({
                    url: url,
                    data: {
                        smstype: 1,
                        phonenumber: $phone.val()
                    },
                    type: "post",
                    dataType: "json"
                }).done(function(result) {
                    var code = result.code
                    switch (code) {
                        case 2:
                            $vcodeError.html("发送次数过多,请稍后再试").show().addClass("red")
                            break
                        case 0:
                            self.countdown($waitArea)
                            $vcodeError.html("手机可以用来找回密码以及登陆").removeClass("red")
                            break
                        case 3:
                            $vcodeError.html("该号码不支持帐号绑定").show().addClass("red")
                            break
                        case 4:
                            $vcodeError.html("该号码不支持帐号绑定").show().addClass("red")
                            break
                        case 5 :
                            $vcodeError.html("请等待60秒后重新发送").show().addClass("red")
                            break
                        default:
                            break
                    }
                })
            })
            // $vcodeBtn.off("click").on("click", function(e) {
            //     e.stopPropagation()
            //     var url = self.sogouBaseurl + "/a/sogou/sendsms/"
            //     $.ajax({
            //         url: url,
            //         data: {
            //             smstype: 1,
            //             phonenumber: $phone.val()
            //         },
            //         type: "post",
            //         dataType: "json"
            //     }).done(function(result) {
            //         var code = result.code
            //         switch (code) {
            //             case 2:
            //                 $vcodeError.html("发送次数过多,请稍后再试").show().addClass("red")
            //                 break
            //             case 0:
            //                 $vcodeError.html("手机可以用来找回密码以及登陆").removeClass("red")
            //                 break
            //             case 3:
            //                 $vcodeError.html("该号码不支持帐号绑定").show().addClass("red")
            //                 break
            //             case 4:
            //                 $vcodeError.html("该号码不支持帐号绑定").show().addClass("red")
            //                 break
            //             default:
            //                 break
            //         }
            //     })
            // })
            //cancel button
            $cancelBtn.on("click", function(e) {
                $phone.val("")
                $vcode.val("")
                $vcodeErea.hide()
                $vcodeError.empty().removeClass("red").hide()
                $phoneError.empty().hide()
            })
            //confirm button
            $confirmBtn.on("click", function(e) {
                var cP = self.checkPhone($phone, $phoneError, self.validObj.phone),
                    vP = self.check($vcode, $vcodeError, self.validObj.vcode)
                    if (vP) {
                        $vcodeError.removeClass("red")
                    } else {
                        $vcodeError.addClass("red")
                        return
                    }
                if (cP) cP.done(function(data) {
                    var url = self.sogouBaseurl + "/a/sogou/security/phone/modify"
                    $.ajax({
                        url: url,
                        data: {
                            phonenumber: $phone.val(),
                            smscode: $vcode.val(),
                            accesstoken: accesstoken
                        },
                        dataType: "json",
                        type: "post"
                    }).done(function(result) {
                        //TODO:handle when bind phone success
                        if (result.code == 0) {
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
                        }
                    })
                })

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
            return $.ajax({
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
            })
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
                pwdObj = self.validObj.password

                $btn.on("click", function(e) {
                    e.preventDefault()

                    self.checkPassword($oriPwd, $newPwd, $conPwd, pwdObj)
                    if (pwdObj.legal) {
                        $.ajax({
                            url: self.sogouBaseurl + "/a/sogou/security/pwd",
                            type: "post",
                            dataType: "json",
                            data: {
                                oldpwd: $oriPwd.val(),
                                newpwd: $newPwd.val(),
                                accesstoken: accesstoken
                            }
                        }).done(function(result) {
                            var code = result.code

                            switch (code) {
                                case 0:
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
                                    break
                                case 1:
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
                                    break
                            }
                        })
                    }
                })

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
            if (!self.check($newPwd, $newError, newObj) || !self.isSimple($newPwd, $newError, "密码过于简单")) {
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
                                    }else if(result.code == 2){
                                        dialog.alert("当前帐号只绑定了一个社交帐号，无法解除绑定")
                                    }else if(result.code == 1){
                                        dialog.alert("解绑失败")
                                    }
                                })
                            })
                        })
                    }

            })

            //账号绑定时如果遇到失败则会弹窗提示
            if(splus && splus.bind_tipmsg){
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
                url = self.sogouBaseurl + '/sogou/avatar/fileupload?_input_encode=utf8&token=' + splus.avatar_token + "&accesstoken=" + accesstoken
                $imginput.fileupload({
                    url: url,
                    dataType: "json",
                    maxFileSize: 5000000,
                    contentType : false,
                    acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i ,
                    done: function(e, data) {
                        var result = data.result

                        if (result.code == 0) {
                            $("aside.sidebar div.photo-error").empty().hide()
                            $("aside.sidebar div.uesr-photo").removeClass("uesr-photo-error")
                            try{
                                window.external.passport("onProfileChange")   
                            }catch(e){
                                console.log("your browser do not support the method onProfileChange,please user a high version ")
                            }
                           
                            window.location.href = window.location.href
                        }
                        if(result.code == 7){

                            $("aside.sidebar div.photo-error").html("上传头像失败,仅支持小于5M的jpg、gif、png图片文件").show()
                            $("aside.sidebar div.uesr-photo").addClass("uesr-photo-error")
                        }
                    }

                })

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
                $error.show().html(emptyMsg)
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
                    $error.show().html(errMsg)
                    return false
                } else {
                    $input.removeClass("error")
                    $error.empty().hide()
                    return true
                }
            }

        },
        validObj: {
            phone: {
                errMsg: '请正确填写手机号',
                emptyMsg: '手机号不能为空',
                nullable: false,
                // regStr: /((^\d{11,13}$) |(^[\w_]{4,20}$))|([\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?)/,
                regStr: /^1\d{10}$/,
                legal: true
            },
            email: {
                errMsg: '请正确输入邮箱',
                emptyMsg: '请输入邮箱',
                nullable: false,
                regStr: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
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
                // regStr: /^[a-zA-Z0-9_\-\u4e00-\u9fa5]+$/,
                legal: true
            },
            nick: {
                errMsg: '用户名为1-20位中英文字符，及“-”和“_”',
                emptyMsg: '请填写用户名',
                nullable: false,
                regStr: /^[a-zA-Z0-9-_\u4e00-\u9fa5]{1,20}$/,

                legal: true
            }
        }


    }
    return Profile
});
