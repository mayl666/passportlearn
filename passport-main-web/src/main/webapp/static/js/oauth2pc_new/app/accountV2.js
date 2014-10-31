/**
 * @adapt yinyong#sogou-inc.com
 */
;
define(['lib/utils', 'lib/placeholder'], function(utils) {
    var _g_client_id=splus._client_id||1044;
    var _g_instance_id=splus.instanceid||"";
    //user login and register
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

    function Account() {}
    Account.prototype = {

        constructor: Account,

        submited: false,
        firstVisit: true,
        sogouBaseurl: "//account.sogou.com",
        interfaces: {
            sendSms: "/web/sendsms",
            checkNeedCaptcha: "/web/login/checkNeedCaptcha",
            checkSname: "/oauth2/checkregname",
            checkMobile: "/oauth2/checkregname",
            login: "/oauth2/login",
            register: "/oauth2/register",
            mobileRegister: "/oauth2/register",
            deleteLoginHistory: "/a/sogou/loginhistory/delete"
        },
        retStatus: {
            checkSname: {
                "10002": "必填参数错误",
                "20201": "此帐号已注册，请直接登录",
                "20217": "暂不支持邮箱注册",
                "20225": "该手机号已注册或已绑定，请直接登录",
                "20241": " 暂不支持sohu域内邮箱注册"
            },
            login: {
                "10001": "系统级错误 ",
                "10002": "参数错误,请输入必填的参数或参数验证失败",
                "10009": "帐号不存在",
                "20205": "帐号不存",
                "20221": " 验证码验证失败",
                "20231": "登陆帐号未激活",
                "20232": "登陆帐号被封杀",
                "20230": "当前帐号或者IP登陆操作存在异常",
                "20206": "帐号或密码错误",
                "20226": "用户登录失败",
                "20240": "生成cookie失败"
            },
            sendSms: {
                "10001": "未知错误",
                "10002": "参数错误,请输入必填的参数或参数验证失败",
                "20229": "号未登录，请先登",
                "20243": "SOHU域用户不允许此",
                "20244": "第三方帐号不允许此",
                "20225": "手机号已绑定其他",
                "20209": "今日验证码校验错误次数已超过",
                "20202": "今日手机短信发送次数超过上限",
                "20204": "一分钟内只能发一条",
                "20213": "手机验证码发送"
            },
            register: {
                "10002": "必填参数错误",
                "20199": "当前注册ip次数已达上限或该ip已在黑名单中",
                "20227": "密码必须为字母和数字且长度大于6位",
                "20224": "当日注册次数已达上",
                "20221": "验证码验证失败",
                "10001": "未知错误 ",
                "10010": "client_id不存在",
                "20214": "用户注册失败",
                "20201": "此帐号已注册，请直接登录",
                "20230": "前帐号或者IP操作存在异常 "
            }
        },
        init: function(classIds) {
            this.initPage(classIds);
            this.initEvents();
        },
        initPage: function(classIds) {
            var self=this;
            //外层容器
            this.$mainWrap = $("." + classIds.c_mainWrap)
            //登录相关容器
            this.$loginLeft = $("." + classIds.c_loginLeft)
            this.$loginDefault = $("." + classIds.c_loginRight + ":first")
            this.$loginAuto = $("." + classIds.c_loginRight + ":last")
            //注册相关容器
            this.$commonReg = $("." + classIds.c_commonReg)
            this.$phoneReg = $("." + classIds.c_phoneReg)
            this.$regBottom = $("." + classIds.c_regBottom)
            //forms
            this.$loginForm = $("#" + classIds.i_loginForm)
            this.$commonRegForm = $("#" + classIds.i_loginForm)
            this.$phoneRegForm = $("#" + classIds.i_phoneRegForm)
            //初始化验证码
            $.each($("img.captcha"), function(index, item) {
                var token = utils.uuid();
                $(item).attr('src', "/captcha?token=" + token).attr("data-token", token);
            });
            //设置刷新验证码按钮的响应事件
            $("a.refreshCapt").click(function(e) {
                var img = $("#" + $(this).attr('data-target'));
                self.refreshVcode(img);
            });

            $(".captcha").click(function(e){
                self.refreshVcode($(this));
            });

            if (location.hash == "#register") {
                this.showReg();
            } else {
                this.showLogin();
            }
            var login_arr = [],
                login_history = splus ? splus.login_history : null

                this.list = this.formatData(login_history);
            this.getHistorySelect();

        },

        initEvents: function() {
            var self = this
            $selectBox = $("div.ppselecter")
            $("ul>li>a", this.$loginAuto).on("click", function(e) {
                e.preventDefault()
                var $a = $(e.delegateTarget),
                    id = $a.attr("id"),
                    href = $a.attr("href");
                switch (id) {
                    case "oauth_qq_login":
                        window.external && window.external.passport && window.external.passport("size", "715", "385");
                        window.location.href = href;
                        break;
                    case "oauth_sina_login":
                        window.external && window.external.passport && window.external.passport("size", "650", "325");
                        window.location.href = href;
                        break;
                    case "oauth_renren_login":
                        window.external && window.external.passport && window.external.passport("size", "500", "275");
                        window.location.href = href;
                        break;
                    case "oauth_taobao_login":
                        window.external && window.external.passport && window.external.passport("size", "450", "475");
                        window.location.href = href;
                        break;
                    default:
                        break;

                }
            })


            $("a", this.$regBottom).off("click").on("click", function(e) {

                e.preventDefault()
                var $a = $(e.delegateTarget),
                    id = $a.attr("id"),
                    href = $a.attr("href")
                    switch (id) {
                        case "oauth_qq_reg":
                            window.external && window.external.passport && window.external.passport("size", "446", "385")
                            window.location.href = href;
                            break
                        case "oauth_sina_reg":
                            window.external && window.external.passport && window.external.passport("size", "650", "325")
                            window.location.href = href;
                            break
                        case "oauth_renren_reg":
                            window.external && window.external.passport && window.external.passport("size", "500", "275")
                            window.location.href = href;
                            break
                        case "oauth_taobao_reg":
                            window.external && window.external.passport && window.external.passport("size", "450", "475")
                            window.location.href = href;
                            break
                        default:
                            break
                    }
            })
            $(window).on("click", function(e) {
                $selectBox.hide(function() {
                    $("ul>li", $selectBox).removeClass("hover")
                })
                e.stopPropagation()
            })
            $('form').on("submit", function(e) {
                return false
            })


            .on('keydown', "input", function(e) {

                var $input = $(e.target),
                    $form = $(e.delegateTarget)
                    // $inputs = $("input",$form),
                    // length = $inputs.length,
                    // i = $inputs.index($input)

                    if (e.keyCode === 13) {

                        e.stopPropagation()
                        if ($input.attr("type") == "password") {

                            $("a.form-submit-a", $form).trigger('click')
                        } else if ($input.attr("type") == "text") {

                            if ($input.attr("id") == "user_acc") {
                                if ($selectBox.is(":visible")) {
                                    $("ul>li[class=hover]", $selectBox).trigger("click")
                                }
                            }
                            e.keyCode = 9
                        } else {
                            $("a.form-submit-a", $form).trigger('click')
                        }
                    }


            })
                .on("click", "a.form-submit-a", function(e) {

                    e.preventDefault()
                    var form = e.delegateTarget,
                        $form = $(form),
                        id = $form.attr("id")
                        switch (id) {
                            case "login_form":
                                self.doLogin(form)
                                break
                            case "common_reg":
                                self.doRegMail(form)
                                break
                            case "phone_reg":
                                self.doRegPhone(form)
                                break
                            default:
                                break
                        }
                    return false
                })
                .on("click", "a#get_vcode", function(e) {
                    e.preventDefault()
                    var form = e.delegateTarget,
                        $phone = $(form.reg_phone),
                        $error = $("div.error-tips", form),
                        flag = self.check($phone, $error, self.validObj.phone),
                        url = self.sogouBaseurl + self.interfaces.sendSms;

                    if (flag) {
                        $.ajax({
                            url: url,
                            data: {
                                client_id: _g_client_id,
                                mobile: $phone.val()
                            },
                            type: "post",
                            dataType: "json"
                        }).done(function(result) {
                            if (typeof result === 'string' || (result && result.constructor == String)) {
                                try {
                                    result = $.parseJSON(result)
                                } catch (e) {
                                    result = {}
                                }
                            }
                            var code = result.status,
                                $vcodeError = $("dic.error-tips", $("form#phone_reg")),
                                $waitArea = $("#phone_code_msg");

                            switch (true) {
                                case ("0" == code):
                                    self.countdown($waitArea)
                                    $error.html("手机可以用来找回密码以及登陆")
                                    break;
                                default:
                                    $error.html(self.retStatus.sendSms[code] || result.statusText || "未知错误").show();
                                    break;
                            }
                        });
                    }
                }).on("blur", "input", function(e) {
                    var form = e.delegateTarget,
                        $form = $(form),
                        $ele = $(e.target),
                        $error = $("div.error-tips", form),
                        id = $ele.attr("id");

                    if (id == "user_acc") {
                        var accountObj = self.validObj.account,
                            regrex = accountObj.regStr;

                        if (!regrex.test($ele.val())) {
                            return;
                        }

                        $.ajax({
                            url: self.sogouBaseurl + self.interfaces.checkNeedCaptcha,
                            data: {
                                username: $ele.val(),
                                client_id: _g_client_id
                            },
                            type: 'get',
                            dataType: 'json'
                        }).done(function(result) {
                            result = getJSON(result);
                            if (result.data && result.data.needCaptcha) {
                                $("div.vcode", form).show();
                                /*                         $("div.chkPic", form).on("click", function(e) {
                                var $img = $("img", e.delegateTarget)
                                self.refreshVcode($img)
                            });*/
                            } else {
                                $("div.vcode", form).hide();
                            }
                        });

                        /*                     $.ajax({
                                url: self.sogouBaseurl + self.interfaces.checkLoginName,
                                data: {
                                    username: $ele.val()
                                },
                                type: "get",
                                dataType: "json"
                            }).done(function(result) {
                                if (result.code !== 0) {
                                    $("div.vcode", form).show()
                                    $("div.chkPic", form).on("click", function(e) {
                                        var $img = $("img", e.delegateTarget)
                                        self.refreshVcode($img)
                                    })
                                } else {
                                    $("div.vcode", form).hide()
                                }

                            })*/
                    }
                    if (id == "reg_acc") {
                        var regaccObj = self.validObj.regacc
                        if (!self.check($ele, $error, regaccObj)) {
                            return
                        }
                        $.ajax({
                            url: self.sogouBaseurl + self.interfaces.checkSname,
                            type: "post",
                            data: {
                                username: $ele.val()
                            },
                            dataType: "json"
                        }).done(function(result) {

                            if (typeof result === 'string' || (result && result.constructor == String)) {
                                try {
                                    result = $.parseJSON(result);
                                } catch (e) {
                                    result = {};
                                }
                            }

                            var code = result.status;
                            switch (true) {
                                case (0 == code):
                                    $ele.removeClass("error")
                                    $error.empty().hide()
                                    break;
                                default:
                                    $ele.addClass("error");
                                    self.refreshVcode($img);
                                    $error.show().html(self.retStatus.checkSname[code] || result.statusText || "未知错误");;
                            }

                            /*                            if (result.regstatus !== 0) {
                                $("div.vcode", form).show()
                                $("div.chkPic", form).on("click", function(e) {
                                    var $img = $("img", e.delegateTarget)
                                    self.refreshVcode($img)
                                })
                            }
                            if (result.code !== 0) {
                                $ele.addClass("error")
                                $error.show().html("该帐号不可用")
                            }
                            if (result.code === 0) {
                                $ele.removeClass("error")
                                $error.empty().hide()
                            }*/
                        });
                    }
                    if (id == "reg_phone") {

                        var phoneObj = self.validObj.phone
                        if (!self.check($ele, $error, phoneObj)) {
                            return
                        }
                        $.ajax({
                            url: self.sogouBaseurl + self.interfaces.checkMobile,
                            type: "post",
                            data: {
                                username: $ele.val()
                            },
                            dataType: "json"
                        }).done(function(result) {
                            if (typeof result === 'string' || (result && result.constructor == String)) {
                                try {
                                    result = $.parseJSON(result);
                                } catch (e) {
                                    result = {};
                                }
                            }
                            var code = result.status;
                            if (code != 0) {
                                $ele.addClass("error")
                                $("div.error-tips", form).show().html(self.retStatus.checkSname[code] || result.statusText || "未知错误")
                            } else {
                                $ele.removeClass("error")
                                $("div.error-tips", form).empty().hide()
                            }
                        });
                    }
                })
            $("#to_reg").on("click", function(e) {
                e.preventDefault()
                self.showReg()
            })
            $("#to_login").on("click", function(e) {
                e.preventDefault()
                self.showLogin()
            })


            var index = -1

            $("#user_acc").on("blur", function(e) {

            }).on("keydown", function(e) {

                var $this = $(e.delegateTarget),

                    $lis = $selectBox.find("ul>li"),
                    $btn = $(".select-btn")

                    switch (e.keyCode) {
                        case 9:
                            if ($selectBox.is(":visible")) {
                                $("ul>li[class=hover]", $selectBox).trigger("click")
                            }
                            break
                        case 38:
                            e.preventDefault()

                            if ($selectBox.is(":visible")) {

                                if (index <= 0) {
                                    $lis.removeClass("hover")
                                    index = $lis.length
                                    $this.val("")
                                } else {
                                    var $li = $("ul>li[index=" + (--index) + "]", $selectBox)
                                    $li.addClass("hover")
                                    $li.siblings().removeClass("hover")
                                    var sname = $li.find("div.caption>p.id").html()
                                    if (sname) {
                                        sname = sname.replace(/<b>(\S*)<\/b>/, "$1")
                                        $this.val(sname)
                                    }
                                }
                            }
                            break
                        case 40:

                            e.preventDefault()
                            if (!$selectBox.is(":visible")) {
                                $selectBox.show()
                            }
                            if (index >= $lis.length - 1) {
                                $this.val("")
                                $lis.removeClass("hover")
                                index = -1
                            } else {
                                var $li = $("ul>li[index=" + (++index) + "]", $selectBox)
                                $li.addClass("hover")
                                $li.siblings().removeClass("hover")

                                var sname = $li.find("div.caption>p.id").html()
                                if (sname) {
                                    sname = sname.replace(/<b>(\S*)<\/b>/, "$1")
                                    $this.val(sname)
                                }
                            }
                            break
                    }
            })



        },

        doLogin: function(form) {
            if (this.submited) return
            var $mail = $(form.user_acc),
                $password = $(form.user_pwd),
                $forget = $(form.forget_pw),
                $error = $(".error-tips", form),
                $vcode,
                $img = $("div.chkPic>img", form),
                password = /*md5*/ ($password.val()),
                accountObj = this.validObj.account,
                passwordObj = this.validObj.password,
                checked = $forget.is(":checked") ? 1 : 0

                this.submited = true

            if (!this.check($mail, $error, accountObj)) {
                this.submited = false
                return
            }
            if (!this.check($password, $error, passwordObj)) {
                this.submited = false
                return
            }
            var self = this,
                instanceid = splus ? splus.instanceid : "",
                data = {
                    loginname: $mail.val(),
                    pwd: password,
                    rememberMe: checked,
                    instanceid: instanceid,
                    client_id: _g_client_id,
                    token: $img.attr("data-token")
                },
                hasVcode = $img.is(":visible"),
                $vcodeContainer = $("div.vcode", form);

            if ($vcodeContainer.is(":visible")) {
                var vcodeObj = this.validObj.vcode;
                $vcode = $(form.vcode)
                if (!this.check($vcode, $error, vcodeObj)) {
                    this.submited = false;
                    return;
                } else {
                    data.captcha = $vcode.val();
                }
            }
            $.ajax({
                url: self.sogouBaseurl + self.interfaces.login,
                data: data,
                type: "post",
                dataType: "json"
            }).done(function(result) {
                self.submited = false;
                result = getJSON(result);
                var code = result.status,
                    data = result.data;
                if (result.data.needCaptcha) {
                    $vcodeContainer.show();
                }else{
                    $vcodeContainer.hide();
                }
                switch (true) {
                    case (0 == code):
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        // var sname = Base64.encode(data.sname),
                        //     nick = Base64.encode(data.nick),
                        /*                        var sname = data.sname,
                            nick = data.nick,
                            sid = data.sid,
                            autoLogin = data.autologin,
                            // passport = Base64.encode(data.passport)
                            passport = data.passport*/
                        var msg = data.logintype + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + (data.sname||data.uniqname) + '|' + data.nick + '|' + data.sid + '|' + data.passport + '|' + (data.autologin||1)
                        console.log('logintype|result|accToken|refToken|sname|nick|是否公用电脑|是否自动登陆|是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        break
                        /*                    case 1:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $error.show().html("登录名不存在")
                        $mail.addClass("error").focus()
                        $password.val("")
                        break
                    case 2:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $mail.focus()
                        $error.show().html("登录名和密码不匹配")
                        $password.val("")
                        break
                    case 3:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $mail.focus()
                        $error.show().html("验证码有误,请重新输入")
                        $error.addClass("error")
                        $password.val("")

                        break
                    case 4:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $mail.focus()
                        $error.show().html("登录行为异常，请稍候尝试登录")
                        break*/
                    default:
                        $error.show().html(self.retStatus[code] || result.statusText || "未知错误");
                         self.refreshVcode($img);
                        break
                }


            }).fail(function(data) {
                //TODO:error handle
                self.submited = false
            })
        },
        checkMail: function($input, $error) {
            var self = this,
                flag = true
            if (/^[0-9]+$/.test($input.val())) {
                $error.show().html("不能全为数字")
                $input.addClass("error").one("blur", function(e) {
                    self.checkMail($input, $error)
                }).one("focus", function(e) {
                    $input.removeClass("error")
                    $error.empty().hide()
                })
                flag = false
            }
            if (flag) {
                $input.removeClass("error")
                $error.empty().hide()
            }
            return flag
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
        doRegMail: function(form) {
            if (this.submited) return
            var $input = $(form.reg_acc),
                $password = $(form.reg_pwd1),
                // $email = $(form.reg_email),
                $error = $(".error-tips", form),
                $vcode,
                $img = $("div.chkPic>img", form),
                regaccObj = this.validObj.regacc,
                passwordObj = this.validObj.password,
                emailObj = this.validObj.email

                this.submited = true

            if (!this.checkMail($input, $error) || !this.isSimple($input, $error, "登录名过于简单")) {
                this.submited = false
                return
            }
            if (!this.check($input, $error, regaccObj)) {
                this.submited = false
                return
            }
            if (!this.check($password, $error, passwordObj) || !this.isSimple($password, $error, "密码过于简单")) {
                this.submited = false
                return
            }
            /*if (!this.check($email, $error, emailObj)) {
                this.submited = false
                return
            }*/
            var $vcodeContainer = $("div.vcode", form)
            if ($vcodeContainer.is(":visible")) {
                var vcodeObj = this.validObj.vcode
                $vcode = $(form.vcode)
                if (!this.check($vcode, $error, vcodeObj)) {
                    this.submited = false
                    return
                }
            }
            var self = this,
                //email = $email.val(),
                hasVcode = $img.is(":visible"),
                instanceid = splus ? splus.instanceid : "",
                data = {
                    username: $input.val(),
                    password: $password.val(),
                    instance_id: instanceid,
                    client_id: _g_client_id,
                    token: $img.attr('data-token')
                }
                /*if (email != "") {
                data.email = email
            }*/
            if ($vcode) {
                data.captcha = $vcode.val();
            }
            $.ajax({
                url: self.sogouBaseurl + self.interfaces.register,
                type: "post",
                data: data,
                dataType: "json"
            }).done(function(result) {

                //result may be a string
                if (typeof result === 'string' || (result && result.constructor == String)) {
                    try {
                        result = $.parseJSON(result);
                    } catch (e) {
                        result = {};
                    }
                }

                var code = result.status;

                switch (true) {
                    case (0 == code):
                        var data = result.data;
                        var msg = (data.logintype || 'sogou') + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + /*sname*/ (data.sname||data.uniqname)+ '|' + /*nick*/ data.nick + '|' + data.sid + '|' + data.passport + '|' + '1';
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登陆 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        break;
                        /*                    case code < 1:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        var data = result.data,
                            sname = data.sname,
                            nick = data.nick,
                            sid = data.sid,
                            passport = data.passport
                        var msg = data.logintype + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + sname + '|' + nick + '|' + sid + '|' + passport + '|' + '1'
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登陆 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result ', msg)
                        break
                    case code == 1:
                        self.refreshVcode($img)
                        $error.show().html("验证码有误,请重新输入")
                        $vcode.addClass("error")
                        // $password.val("")
                        $vcode.val("")
                        break
                    case code == 2:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $error.show().html("注册失败,请稍后再试")
                        break
                    case code > 10 && code < 20:

                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $error.show().html("用户名不可用")
                        $input.addClass("error")
                        break
                        // $password.val("")
                    case code > 20 && code < 30:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $error.show().html("密码格式不正确")
                        $password.addClass("error")
                        $password.val("")
                        break
                    case code == 31:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $error.show().html("注册行为异常，请使用已有帐号进行登录")
                        $input.addClass("error")
                        break
                    case code > 31 && code < 40:

                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }
                        $error.show().html("用户名已存在或非法用户名")
                        $input.addClass("error")
                        // $password.val("")
                        break
                    case code > 40:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val("")
                        }

                        $error.show().html("邮箱已存在或非法邮箱")
                        $email.addClass("error")
                        // $password.val("")
                        break*/
                    default:
                        $error.show().html(self.retStatus.register[code] || result.statusText || "未知错误");
                        $vcode.val("");
                        if (hasVcode) {
                            self.refreshVcode($img)
                        }
                        break
                }
                self.submited = false
            }).fail(function(result) {
                self.submited = false
            })



        },
        doRegPhone: function(form) {
            var self=this;
            if (this.submited) return;
            var $input = $(form.reg_phone),
                $vcode = $(form.reg_vcode),
                $password = $(form.reg_pwd2),
                $error = $("div.error-tips", form),
                phoneObj = this.validObj.phone,
                passwordObj = this.validObj.password,
                vcodeObj = this.validObj.vcode

            if (!this.check($input, $error, phoneObj)) {
                this.submited = false
                return
            }
            if (!this.check($vcode, $error, vcodeObj)) {
                this.submited = false
                return
            }
            if (!this.check($password, $error, passwordObj) || !this.isSimple($password, $error, "密码过于简单")) {
                this.submited = false
                return
            }

            var url = this.sogouBaseurl + self.interfaces.mobileRegister,
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
                    client_id: _g_client_id,
                    token: $vcode.attr('data-token')
                },
                dataType: "json"
            }).done(function(result) {;
                if (typeof result === 'string' || (result && result.constructor == String)) {
                    try {
                        result = $.parseJSON(result);
                    } catch (e) {
                        result = {};
                    }
                }
                var code = result.status,data=result.data;
                switch (true) {
                    case (0 == code):
                        var msg = (data.logintype || 'sogou') + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + /*sname*/ (data.sname )+ '|' + /*nick*/ data.nick + '|' + data.sid + '|' + data.passport+ '|' + '1';
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登陆 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        break;
                        /*                    case 0:
                        var data = result.data,
                            sname = data.sname,
                            nick = data.nick,
                            sid = data.sid,
                            passport = data.passport
                        var msg = data.logintype + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + sname + '|' + nick + '|' + sid + '|' + passport + '|1'
                        console.log('logintype | result | accToken | refToken | sname | nick | 是否公用电脑 | 是否自动登陆 | 是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result ', msg)
                        break
                    case 1:
                        $error.show().html("手机已存在")
                        $input.addClass("error")
                        $password.val("")
                        break
                    case 3:
                        $error.show().html("校验码错误")
                        $vcode.addClass("error")
                        $password.val("")
                        break
                    case 4:
                        $error.show().html("参数错误")
                        break*/
                    default:
                        $error.show().html(self.retStatus.register[code] || result.statusText || "未知错误");
                        $vcode.addClass("error");
                        $password.val("");
                        break
                }
                this.submited = false
            }).fail(function() {
                window['console'] && console.log("error happens in http request :%s", self.interfaces.register)
                this.submited = false
            })
        },


        //显示登录区域
        showLogin: function() {

            window.external && window.external.passport && window.external.passport("size", "604", "360")
            var self = this,
                loginArea,
                regArea = self.$commonReg.add(self.$phoneReg).add(self.$regBottom)
                loginArea = self.logintype === "auto" ? (self.$loginDefault.hide(),
                    self.$loginLeft.add(self.$loginAuto)) : (self.$loginAuto.hide(), self.$loginLeft.add(self.$loginDefault))



                regArea.hide();
                loginArea.show();
                self.refreshVcode($("#loginCapt"));
                $("#user_acc").focus();
                self.reset();
        },
        //显示注册区域
        showReg: function() {
            window.external && window.external.passport && window.external.passport("size", "605", "360")
            var self = this,
                loginArea,
                regArea = self.$commonReg.add(self.$phoneReg).add(self.$regBottom)
                loginArea = self.logintype === "auto" ? (self.$loginDefault.hide(),
                    self.$loginLeft.add(self.$loginAuto)) : (self.$loginAuto.hide(), self.$loginLeft.add(self.$loginDefault))


                loginArea.hide()
                regArea.show()
                // $("#reg_acc").focus()

                //保持验证码最新
                self.refreshVcode($("#commonRegCapt"));

                self.reset()


        },
        reset: function() {
            var self = this,
                $forms = $("form")
                $.each($forms, function(index) {
                    if (!self.firstVisit) {
                        $forms[index].reset()
                        $("input", $forms).removeClass("error")
                        $("div.error-tips", $forms).empty().hide()
                    }

                })
                self.firstVisit = false
        },
        formatData: function(login_history) {
            //sid sname nick avatar  type
            var list = []
            if (login_history) {
                login_arr = login_history.split("|")
                if (login_arr.length > 0) {
                    $.each(login_arr, function(index, val) {
                        var _tempObj,
                            _arr = val.split(",")
                            for (var i = 0; i < _arr.length; i++) {
                                _tempObj = {
                                    img: _arr[3],
                                    sname: _arr[1],
                                    nick: _arr[2],
                                    type: _arr[4],
                                    sid: _arr[0]
                                }
                            }
                        list.push(_tempObj)
                    })
                }
            }
            return list
        },
        getHistorySelect: function() {
            var $container = $("div.ppselecter"),
                self = this,
                list = this.list
            if (list && list.length && list.length > 0) {
                $(".select-btn").show()
            } else {
                $(".select-btn").hide()
            }
            self.renderSelect($container, list)
            $(".select-btn").off("click").on("click", function(e) {
                e.stopPropagation()
                $("#user_acc").focus()
                $container.toggle()
            })
            $("#user_acc").off("click").on("click", function(e) {
                e.stopPropagation()
            }).off("input").on("input", function(e) {

                e.stopPropagation()


                var val = $.trim($(this).val())
                var _list = $.map(list, function(obj) {
                    var temp,
                        sname = $.trim(obj.sname).replace(/<b>(\S*)<\/b>/, "$1"),
                        index = sname.indexOf(val)
                        if (index > -1) {
                            return {
                                img: obj.img,
                                sname: sname.substr(0, index) + "<b>" + val + "</b>" + sname.substr(index + val.length),
                                nick: obj.nick,
                                type: obj.type,
                                sid: obj.sid
                            }

                        }
                    return null
                })
                if (_list.length < 1) {
                    $container.hide()
                    return
                } else {
                    self.renderSelect($container, _list)
                    $container.show()
                }

            })

        },
        renderSelect: function($container, list) {
            var self = this
            $container.empty()
            var $ul = $("<ul></ul>")
            $.each(list, function(index, obj) {
                var liHtml = ['<li index="' + index + '">',
                    '<div class="photo">',
                    ' <img src="' + obj.img + '">',
                    '</div>',
                    '<div class="caption">',
                    '<p class="id"  >' + obj.sname + '</p>',
                    '<p class="name">' + obj.nick + '</p>',
                    '</div>',
                    '<a href="javascript:" sid="' + obj.sid + '" class="del">×</a>',
                    '</li>'
                ].join("")
                $ul.append(liHtml)
            })

            $container.append($ul);

            var url = self.sogouBaseurl + self.interfaces.deleteLoginHistory;
            $ul.on("mouseenter", "li", function(e) {
                $(this).addClass("hover")
                $(this).siblings().removeClass("hover")
            }).on("click", function(e) {
                e.stopPropagation()
                var $ele = $(e.target),
                    $form = $("#login_form"),
                    $mail = $("#user_acc", $form)
                    if ($ele.closest('[sid]').length > 0) {

                        var $sid = $ele.closest('[sid]'),
                            sid = $sid.attr("sid")
                            $.post(url, {
                                sid: sid
                            }).done(function(result) {
                                var _tempList = []
                                $.each(list, function(index, val) {
                                    if (val.sid != sid) {
                                        _tempList.push(val)
                                    }
                                })
                                self.list = _tempList
                                $container.hide(function() {
                                    $("ul>li", $container).removeClass("hover")
                                })
                                $mail.focus()
                                self.getHistorySelect()
                            })
                    }
                if ($ele.closest('[sid]').length < 1 && $ele.closest('li').length > 0) {

                    var $li = $ele.closest('li'),
                        $password = $("#user_pwd", $form),
                        snameStr = $("p.id", $li).html(),

                        sname = snameStr.replace(/<b>(\S*)<\/b>/, "$1")

                        $mail.val(sname)
                        $container.hide(function() {
                            $("ul>li", $container).removeClass("hover")
                        })
                }
            })
        },
        countdown: function($waitArea) {
            var self = this,
                total = 60,
                interdown

                interdown = setInterval(function() {
                    if (total === 0) {
                        $waitArea.empty().html('<a href="javascript:" id="get_vcode">获取验证码</a>')
                        clearInterval(interdown)
                    } else {
                        $waitArea.empty().html('<span style="color:#000093;font-size:1.1em">' + (total--) + '</span> 秒后重新获取')
                    }
                }, 1000)
        },
        check: function($input, $error, valid) {
            var self = this,
                nullable = valid.nullable,
                regrex = valid.regStr,
                emptyMsg = valid.emptyMsg,
                errMsg = valid.errMsg
            if (!nullable && $input.val() == "") {
                // if($input.attr("id")!=="user_acc"){
                $input.addClass("error")
                $error.show().html(emptyMsg)
                // }
                $input.one("focus", function() {
                    $error.empty().hide()
                    $input.removeClass("error")
                })
                // .one("blur", function(e) {

                //     self.check($input, $error, valid)
                // })

                return false
            } else if (nullable && $input.val() == "") {
                $input.removeClass("error")
                $error.empty().hide()
                return true
            } else {
                if (!regrex.test($input.val())) {
                    // if($input.attr("id")!=="user_acc"){
                    $input.addClass("error")
                    $error.show().html(errMsg)
                    // }
                    $input.one("focus", function() {
                        $error.empty().hide()
                        $input.removeClass("error")
                    })

                    return false
                } else {
                    $input.removeClass("error")
                    $error.empty().hide()
                    return true
                }
            }

        },
        refreshVcode: function($img) {
            var ts = new Date().getTime(),
                token = utils.uuid(),
                url = '/captcha?token=' + token + '&t=' + ts;
            $img.attr("src", url).attr("data-token", token);
        },
        validObj: {
            account: {
                defaultMsg: "帐号/手机号/邮箱",
                errMsg: '该登录名不存在',
                emptyMsg: '请填写登录名',
                nullable: false,
                // regStr: /((^\d{11,13}$) |(^[\w_]{4,20}$))|([\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?)/,
                regStr: /^\S{4,50}$/
            },
            phone: {
                errMsg: '请正确填写手机号',
                emptyMsg: '手机号不能为空',
                nullable: false,
                // regStr: /((^\d{11,13}$) |(^[\w_]{4,20}$))|([\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?)/,
                regStr: /^1\d{10,13}$/
            },
            email: {
                errMsg: '请正确输入邮箱（非必填）',
                emptyMsg: '',
                nullable: true,
                regStr: /^(\w)+(\.\w+)*@([\w_\-])+((\.\w+)+)$/
            },
            password: {
                errMsg: '密码必须是6-16位字母、数字、下划线的组合',
                emptyMsg: '请输入6-16位密码',
                nullable: false,
                regStr: /^\w{6,16}$/
            },
            vcode: {
                errMsg: '请正确输入验证码',
                emptyMsg: '验证码不能为空',
                nullable: false,
                regStr: /^\w*$/
            },
            regacc: {
                errMsg: '必须是小写字母开头的4-16位字母、数字、“.”及“-”的组合',
                emptyMsg: '请填写帐号',
                nullable: false,
                regStr: /^[a-z][a-zA-Z0-9-\.]{3,15}$/
            },
            vcode: {
                errMsg: '验证码错误，请重新输入',
                emptyMsg: '请输入验证码',
                nullable: false
            }
        }
    }
    return Account
});