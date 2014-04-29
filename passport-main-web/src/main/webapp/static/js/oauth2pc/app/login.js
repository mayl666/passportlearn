/**
 * login.js
 *
 * changelog
 * 2013-11-21[15:11:34]:copied
 * 2013-11-29[18:12:03]:what a mess....
 *
 * @adapt someone#sogou-inc.com
 * @version 0.0.2
 * @since 0.0.1
 */

;
define(['lib/md5', 'lib/utils', 'lib/common', 'lib/placeholder', 'lib/base64'], function(md5, utils, common) {
    //user login and register
    var _g_client_id = (window.splus && splus._client_id) || 1044;
    /**
     * Fixed JSON
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
    var Login= {
        constructor: Login,
        submited: false,
        firstVisit: true,

        init: function() {

            this.exPassport("size", "540", "345")
            this.initEvents()
            this.initLoginHistory();
            this.initDropdownEvents();
            this.refreshVcode($('.chkPic img'));
            $('[name=account]').focus();
        },
        sogouBaseurl: location.origin, 
        //事件代理
        initEvents: function() {
            var self = this,
                $form = $('div.login-con>form'),
                exPassport = self.exPassport;

            this.$form = $form;
            //禁止form默认提交
            $form.on('submit', function(e) {
                self.doLogin()
                return false
            })
            //鼠标点击输入框 所有错误提示及样式清除
            .on('focus', 'input', function(e) {
                var $input = $(e.target);
                $input.removeClass('error');
                $input.next('span.position-tips').hide();
                $('#out_error').empty().hide();
            })
            //检验是否需要验证码
            .on('blur', 'input[name=account]', function(e) {
                var $account = $(e.target);
                //如果用户未输入账号,不进行校验
                if (!$account.val()||$('.ppselecter').is(':visible')) return;
                if (!self.check($account, self.validObj.account)) {
                    self.submited = false;
                    return;
                }
                $.ajax({
                    url: self.sogouBaseurl + self.interfaces.checkNeedCaptcha, 
                    data: {
                        username : $account.val(),
                        client_id: _g_client_id
                    },
                    type: 'get',
                    dataType: 'json'
                }).done(function(result) {

                    result = getJSON(result);

                    if (result.data && result.data.needCaptcha) {

                        $('div.vcode-area').show();
                    } else {
                        $('div.vcode-area').hide();
                    }

                    switch(true){
                        case result.status==0:break;
                        case result.status!=0:
                            self.showTips($account,$account.next('.position-tips'),result.statusText);
                            break;
                        default:;
                    }

                })
            })
            //,2013-11-22[11:04:43]
            //valid password on blur
            .on('blur', '[name=password]', function(e) {
                var $pwd = $(e.target);
                if (!$pwd.val()) return; //ignore empty
                if (!self.check($pwd, self.validObj.password)) {
                    self.submited = false;
                    return;
                }
            })
            //点击验证码图片切换验证码  
            .on('click', 'div.chkPic>img', function(e) {
                var $img = $(e.target);
                self.refreshVcode($img);
            })
            //点击"换一张"切换验证码
            .on('click', 'div.chkPic>span>a', function(e) {
                $('div.chkPic>img', $form).trigger('click');
            })
            //第三方登录小箭头
            .on('click', 'i.outside-arrow', function(e) {
                e.stopPropagation();
                $(e.target).closest('div.selected').next('ul.outside-list').toggle();
            })
            //绑定第三方登录链接
            .on('click', '[oauth-type]', function(e) {
                var $this = $(this),
                    $i = $this.find('>i'),
                    type = $this.attr('oauth-type'),
                    url = $i.attr('oauth');

                switch (type) {
                    case "qq":
                        exPassport("size", "715", "385")
                        window.location.href = url
                        break
                    case "weibo":
                        exPassport("size", "650", "325")
                        window.location.href = url
                        break
                    case "renren":
                        exPassport("size", "500", "275")
                        window.location.href = url
                        break
                    case "taobao":
                        exPassport("size", "450", "475")
                        window.location.href = url
                        break
                    default:
                        break

                }
            })
            //error msg unclickable
            .on('click', '.position-tips', function(e) {
                 if($(e.target).is('span.x'))
                        $(this).prev('input').val('');
                $(this).hide().empty().prev('input').removeClass('error').focus();
            });

            $(document).click(function(e) {
                $('i.outside-arrow').closest('div.selected').next('ul.outside-list').hide();
            });
        },
        initDropdownEvents: function() {
            var self = this,
                list = self.list,
                index = -1,
                $form = self.$form,
                $mail = $('input[name=account]', $form),
                $selectBox = $("div.ppselecter");

                $mail.on("blur", function(e) {
    
                })
                .on("keydown", function(e) {
                    var $this = $(e.delegateTarget),

                        $lis = $selectBox.find("ul>li"),
                        $btn = $(".select-btn");

                        switch (e.keyCode) {
                            case 13:
                                e.preventDefault();
                            case 9:
                                if ($selectBox.is(":visible")) {
                                    $("ul>li.hover", $selectBox).trigger("click");
                                }
                                $selectBox.hide();
                                self.toggleArrDirection();
                                break
                            case 38:
                                e.preventDefault();

                                if ($selectBox.is(":visible")) {

                                    if (index <= 0) {
                                        $lis.removeClass("hover");
                                        index = $lis.length;
                                        $this.val("");
                                    } else {
                                        var $li = $("ul>li[index=" + (--index) + "]", $selectBox)
                                        $li.addClass("hover");
                                        $li.siblings().removeClass("hover");
                                        var sname = $li.find("div.caption>p.id").html();
                                        if (sname) {
                                            sname = sname.replace(/<b>(\S*)<\/b>/, "$1");
                                            $this.val(sname);
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
                                    var $li = $("ul>li[index=" + (++index) + "]", $selectBox);
                                    $li.addClass("hover");
                                    $li.siblings().removeClass("hover");

                                    var sname = $li.find("div.caption>p.id").html();
                                    if (sname) {
                                        sname = sname.replace(/<b>(\S*)<\/b>/, "$1");
                                        $this.val(sname);
                                    }
                                }
                                break;
                        }
                })
                .on("click", function(e) {
                    e.stopPropagation()
                }).on("input", function(e) {
                    e.stopPropagation()
                    var val = $.trim($(this).val());
                    /*if(val){
                        $('.select-btn').hide()
                    }else{
                        $('.select-btn').show();
                    }*/
                    var _list = $.map(self.list, function(obj) {
                        var temp,
                            sname = $.trim(obj).replace(/<b>(\S*)<\/b>/, "$1"),
                            index = sname.indexOf(val);
                        if (index > -1) {
                            return sname; 
                        }
                        return null;
                    })
                    if (_list.length < 1) {
                        $selectBox.hide();
                        return;
                    } else {
                        self.renderSelect($selectBox, _list);
                        $selectBox.show();
                        self.toggleArrDirection();
                    }
                })

            //点击小箭头dropdown显示
            $form.on("click", ".select-btn", function(e) {
                e.stopPropagation();
                $mail.focus();
                $selectBox.toggle();
                self.toggleArrDirection();
            })
            //鼠标点击页面其他地方,dropdown消失
            $(window).on("click", function(e) {
                $selectBox.hide(function() {
                    $("ul>li", $selectBox).removeClass("hover");
                })
                e.stopPropagation();
            })

            $selectBox.on("mouseenter", "ul li", function(e) {
                $(this).addClass("hover");
                $(this).siblings().removeClass("hover");
            }).on("click", 'ul', function(e) {
                e.stopPropagation();
                e.preventDefault();
                var $ele = $(e.target);
                if ($ele.is('.del')) {
                    var sid = $ele.attr("sname");
                    $ele.parents('li').remove();
                    self.delHistory(sid);
                    self.list= self.list.filter(function(v){return sid!=v});
                    if(!self.list.length){$selectBox.hide();$('.select-btn').hide()}
                    $mail.focus();
                    return;
                }else
                if ($ele.closest('[sname]').length < 1 && $ele.closest('li').length > 0) {

                    var $li = $ele.closest('li'),
                        $password = $("input[name=password]", $form),
                        snameStr = $("p.id", $li).html(),

                        sname = snameStr.replace(/<b>(\S*)<\/b>/, "$1");

                    $mail.val(sname);
                    $('.select-btn').hide();
                    $selectBox.hide(function() {
                        $("ul>li", $selectBox).removeClass("hover")
                    });
                    $mail.focus();
                }
            })
        },
        //登录逻辑
        doLogin: function() {
            if (this.submited) {
                return
            }
            this.submited = true
            var self = this,
                exPassport = self.exPassport,
                hasVcode = false,
                $form = self.$form,

                $account = $('input[name=account]', $form),
                $accError = $account.next('span.position-tips'),

                $password = $('input[name=password]', $form),
                $pwdError = $password.next('span.position-tips'),

                $vcode = $('input[name=vcode]', $form),
                $vcodeError = $vcode.next('span.position-tips'),

                $img = $('div.chkPic>img', $form),
                $checked = $('#forget_pw'),

                $bottomError = $('#out_error');

            if (!self.checkEmpty($account, $accError)) {
                this.submited = false;
                return false
            }
            //,2013-11-22[11:03:15]
            //check again
            if (!self.check($account, self.validObj.account)) {
                this.submited = false;
                return false;
            }

            if (!self.checkEmpty($password, $pwdError)) {
                this.submited = false;
                return false
            }

            //,2013-11-22[11:03:55]
            //check pwd
            if (!self.check($password, self.validObj.password)) {
                this.submited = false;
                return;
            }

            if ($vcode.is(':visible')) {
                hasVcode = true;
                if (!self.checkEmpty($vcode, $vcodeError)) {
                    this.submited = false;
                    return false
                }
            }

            var data,
                instanceid = splus ? splus.instanceid : '',
                password = md5($password.val()),
                checked = +$checked.is(':checked');
            data = {
                username: $account.val(),
                password: password,
                rememberMe: 　checked,
                client_id: _g_client_id,
                instanceid: instanceid,
                token: $img.attr('data-token')
            };
            if (hasVcode) {
                data. /*vcode*/ captcha = $vcode.val();
            }
            $.ajax({
                url: self.sogouBaseurl + self.interfaces.login, 
                data: data,
                type: 'post',
                dataType: 'json'
            }).done(function(result) {
                self.submited = false;
                result = getJSON(result); 
                var code = result.status, 
                    data = result.data;
                switch (true) { 
                    case (0 == code):
                        self.saveHistory($account.val()); 
                        var msg = data.logintype + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + (data.sname || data.uniqname) + '|' + data.nick + '|' + data.sid + '|' + data.passport + '|' + (data.autologin)
                        console.log('logintype|result|accToken|refToken|sname|nick|是否公用电脑|是否自动登陆|是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        break;
                    default:
                        if (result.data && result.data.needCaptcha) {
                            $('div.vcode-area').show();
                            $vcode.val('').next('.position-tips').hide();
                            self.refreshVcode($img);
                        }
                        
                        $password.val('').next('.position-tips').hide();

                        if (/(10009|20205)/.test(code))
                            self.showTips($account,$account.next('.position-tips'),self.retStatus.login[code]);
                       // else if (20206 == code)
                       //     self.showTips($password,$password.next('.position-tips'),self.retStatus.login[code]);
                        else if (20221 == code)
                            self.showTips($vcode,$vcode.next('.position-tips'),self.retStatus.login[code]);
                        else
                            $bottomError.html(self.retStatus.login[code] || result.statusText || "未知错误").show();

                        break;
                }


            }).fail(function(data) {
                $bottomError.html('网络异常，请稍后尝试注册').show();
                self.submited = false
            })
        },
        //校验是否为空
        checkEmpty: function($input, $error) {
            return $input.val() == '' ?this.showTips($input,$error,'不能为空')&&false : true
        },
        //刷新验证码
        refreshVcode: function($img) {
            var ts = +new Date,
                token = utils.uuid(),
                url = '/captcha?token=' + token + '&t=' + ts;
            $img.attr("src", url).attr("data-token", token);
        },
        toggleArrDirection:function(){
            var $btn=$('.select-btn'),$selectBox=$('.ppselecter'),self=this;
            if ($selectBox.is(':visible')) {
                $btn.addClass('up');
            } else {
                $btn.removeClass('up');
            }

            if($('[name=account]').next('.position-tips').is(':visible')){
                $btn.hide();
            }else if(self.list.length){
                $btn.show();
            }
        },
        initLoginHistory: function() {

            var login_history = "";
            try {
                login_history = localStorage.getItem('login_history')
            } catch (e) {}

            this.formatHistoryData(login_history);
            this.getHistorySelect();
        },
        getHistorySelect: function() {
            var $container = $("div.ppselecter"),
                self = this,
                list = this.list;
            if (list && list.length && list.length > 0) {
                $(".select-btn").show()
            } else {
                $(".select-btn").hide()
            }
            self.renderSelect($container, list)
        },
        renderSelect: function($container, list) {
            var self = this
            $container.empty()
            var $ul = $("<ul></ul>")
            $.each(list, function(index, obj) {
                var liHtml = [
                    '<li index="' + index + '">',
                    '   <div class="caption">',
                    '       <p class="id">' + obj + '</p>',
                    '   </div>',
                    '   <span class="del" sname="' + obj + '" title="删除">×</span>',
                    '</li>'
                ].join("")
                $ul.append(liHtml)
            })
            $container.append($ul)
        },
        //格式化登录历史数据
        formatHistoryData: function(login_history) {
            //sid sname nick avatar  type
            var list = []
            if (login_history) {　
                login_arr = login_history.split("|")
                if (login_arr.length > 0) {
                    list = login_arr.filter(function(k) {
                        return !!k;
                    });
                }
            }
            this.list = list
        }

    };
    $.extend(Login, common)
    return Login;
});