/**
  * login.js
  *
  * changelog
  * 2013-11-21[15:11:34]:copied
  *
  * @info yinyong,osx-x64,UTF-8,10.129.173.11,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/app
  * @author yinyong#sogou-inc.com
  * @version 0.0.1
  * @since 0.0.1
  */

;
define(['lib/md5','lib/utils','lib/common', 'lib/placeholder', 'lib/base64'], function(md5,utils,common) {
    //user login and register
    var _g_client_id=(window.splus&&splus._client_id)||1044;
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
   // function Login() {};
    var Login/*.prototype*/ = {

        constructor: Login,

        submited: false,

        firstVisit: true,

        init: function() {

            this.exPassport("size", "604", "360")
            this.initEvents()
            this.initLoginHistory();
            this.initDropdownEvents();
            //yinyong@sogou-inc.com,2013-11-21[16:29:48]
            //init vcode img
            this.refreshVcode($('.chkPic img'));
            $('[name=account]').focus();
        },
        sogouBaseurl: location.origin,//'//account.sogou.com',
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
            //点击登录按钮进行登录操作
            /*.on('click', 'button.btn', function(e) {
                self.doLogin()
                return false
            })*/
            //鼠标点击输入框 所有错误提示及样式清除
            .on('focus', 'input', function(e) {
                var $input = $(e.target);
                $input.removeClass('error');
                $input.next('span.position-tips').html('');

                $('#out_error').empty().hide();

            })
            //检验是否需要验证码
            .on('blur', 'input[name=account]', function(e) {
                var $account = $(e.target);
                //如果用户未输入账号,不进行校验
                if (!$account.val()) return;
                //yinyong@sogou-inc.com,2013-11-21[20:58:12]
                if (!self.check($account, self.validObj.account)) {
                    self.submited = false;
                    return;
                }
                $.ajax({
                    //yinyong@sogou-inc.com,2013-11-21[15:53:52]
                    //fixed url
                    url: self.sogouBaseurl + self.interfaces.checkNeedCaptcha,// '/a/sogou/checkLoginName',
                    data: {
                        //yinyong@sogou-inc.com,2013-11-21[15:55:38]
                        //fixed param name
                        username/*loginName*/: $account.val(),
                        client_id: _g_client_id
                    },
                    type:'get'/* 'post'*/,//yinyong@sogou-inc.com,2013-11-21[15:56:02],fixed method
                    dataType: 'json'
                }).done(function(result) {

                    result=getJSON(result);
                    //yinyong@sogou-inc.com,2013-11-21[15:57:45]
                    //fixed validate method
                    if (result.data && result.data.needCaptcha/*result.code !== 0*/) {

                        $('div.vcode-area').show();
                    } else {
                        //TODO : In order to facilitate the test there used show-method which  should use hide-method
                        $('div.vcode-area').hide();
                    }

                })
            })
            //yinyong@sogou-inc.com,2013-11-22[11:04:43]
            //valid password on blur
            .on('blur','[name=password]',function(e){
                var $pwd=$(e.target);
                if(!$pwd.val())return;//ignore empty
                if(!self.check($pwd,self.validObj.password)){
                    self.submited=false;
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
            //yinyong@sogou-inc.com,2013-11-22[11:13:08]
            //error msg unclickable
            .on('click','.position-tips',function(e){
                $(e.target).hide().empty().prev('input').removeClass('error').focus();
            });
        //yinyong@sogou-inc.com,2013-11-21[16:24:21]
        $(document).click(function(e){
            $('i.outside-arrow').closest('div.selected').next('ul.outside-list').hide();
        });
        },
        initDropdownEvents : function(){
            var self = this,
                list = self.list,
                index = -1 ,
                //yinyong@sogou-inc.com,2013-11-21[15:59:09],
                //fixed url
                delUrl = self.sogouBaseurl + self.interfaces.deleteLoginHistory,//"/a/sogou/loginhistory/delete",
                $form = self.$form,
                $mail = $('input[name=account]',$form),
                $selectBox = $("div.ppselecter") ;

            $mail.on("blur", function(e) {

            })
            .on("keydown", function(e) {
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
            .on("click", function(e) {
                e.stopPropagation()
            }).on("input", function(e) {
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
                    $selectBox.hide()
                    return
                } else {
                    self.renderSelect($selectBox, _list)
                    $selectBox.show()
                }
            })

            //点击小箭头dropdown显示
            $form.on("click",".select-btn", function(e) {
                e.stopPropagation()
                $mail.focus()
                $selectBox.toggle()
            })
            //鼠标点击页面其他地方,dropdown消失
            $(window).on("click", function(e) {
                $selectBox.hide(function() {
                    $("ul>li", $selectBox).removeClass("hover")
                })
                e.stopPropagation()
            })

            $selectBox.on("mouseenter", "ul li", function(e) {
                $(this).addClass("hover")
                $(this).siblings().removeClass("hover")
            }).on("click",'ul', function(e) {
                e.stopPropagation()
                var $ele = $(e.target);
                    if ($ele.closest('[sid]').length > 0) {

                        var $sid = $ele.closest('[sid]'),
                            sid = $sid.attr("sid")
                            $.post(delUrl, {
                                sid: sid
                            }).done(function(result) {
                                var _tempList = []
                                $.each(list, function(index, val) {
                                    if (val.sid != sid) {
                                        _tempList.push(val)
                                    }
                                })
                                self.list = _tempList
                                $selectBox.hide(function() {
                                    $("ul>li", $selectBox).removeClass("hover")
                                })
                                $mail.focus()
                                self.getHistorySelect()
                            })
                    }
                if ($ele.closest('[sid]').length < 1 && $ele.closest('li').length > 0) {

                    var $li = $ele.closest('li'),
                        $password = $("input[name=password]", $form),
                        snameStr = $("p.id", $li).html(),

                        sname = snameStr.replace(/<b>(\S*)<\/b>/, "$1")
                    
                        $mail.val(sname)
                        $selectBox.hide(function() {
                            $("ul>li", $selectBox).removeClass("hover")
                        })
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
                // $btn = $('button.btn',$form),


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
            //yinyong@sogou-inc.com,2013-11-22[11:03:15]
            //check again
            if(!self.check($account,self.validObj.account)){
                this.submited=false;
                return false;
            }

            if (!self.checkEmpty($password, $pwdError)) {
                this.submited = false;
                return false
            }

            //yinyong@sogou-inc.com,2013-11-22[11:03:55]
            //check pwd
            if(!self.check($password,self.validObj.password)){
                this.submited=false;
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
                checked = $checked.is(':checked') ? 1 : 0;
            data = {
                loginname: $account.val(),
                pwd: password,
                rememberMe: 　checked,
                //yinyong@sogou-inc.com,2013-11-21[16:01:55]
                //add client_id
                client_id:_g_client_id,
                instanceid: instanceid,
                token:$img.attr('data-token')
            };
            if (hasVcode) {
                //yinyong@sogou-inc.com,2013-11-21[16:02:44]
                //fixed param name
                data./*vcode*/captcha = $vcode.val();
            }
            $.ajax({
                //yinyong@sogou-inc.com,2013-11-21[16:00:49]
                //fixed url
                url: self.sogouBaseurl + self.interfaces.login,//'/a/sogou/login?_input_encode=utf-8',
                data: data,
                type: 'post',
                dataType: 'json'
            }).done(function(result) {
                self.submited = false;
                result=getJSON(result);//yinyong@sogou-inc.com,2013-11-21[16:05:31]
                var code = result.status/*code*/,//yinyong@sogou-inc.com,2013-11-21[16:05:41]
                    data = result.data;
                switch (/*code*/true) {//yinyong@sogou-inc.com,2013-11-21[16:06:10]
                    case (0==code)://yinyong@sogou-inc.com,2013-11-21[16:06:23]
                        /*var sname = data.sname,
                            nick = data.nick,
                            sid = data.sid,
                            autoLogin = data.autologin,
                            // passport = Base64.encode(data.passport)
                            passport = data.passport
                        var msg = data.logintype + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + sname + '|' + nick + '|' + sid + '|' + passport + '|' + autoLogin
                        // console.log('logintype|result|accToken|refToken|sname|nick|是否公用电脑|是否自动登陆|是否保存\n ' + msg)
                        exPassport('result', msg)*/
                        var msg = data.logintype + '|' + data.result + '|' + data.accesstoken + '|' + data.refreshtoken + '|' + (data.sname||data.uniqname) + '|' + data.nick + '|' + data.sid + '|' + data.passport + '|' + (data.autologin||1)
                        console.log('logintype|result|accToken|refToken|sname|nick|是否公用电脑|是否自动登陆|是否保存\n ' + msg)
                        window.external && window.external.passport && window.external.passport('result', msg)
                        break;
                        //yinyong@sogou-inc.com,2013-11-21[16:07:11]
                        //use retStatus object&array
/*                    case 1:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val('')
                        }
                        $accError.html('登录名不存在')
                        $account.addClass('error')
                        break
                    case 2:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val('')
                        }
                        $bottomError.html('登录名和密码不匹配').show()

                        break
                    case 3:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val('')
                        }

                        $vcodeError.html('验证码错误')
                        $vcode.addClass('error')


                        break
                    case 4:
                        if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val('')
                        }

                        $bottomError.html('登录行为异常，请稍候尝试登录').show()
                        break*/
                    default:
                       /* if (hasVcode) {
                            self.refreshVcode($img)
                            $vcode.val('')
                        }

                        $bottomError.html('网络异常，请稍后尝试登录').show()*/
                        if (/(10009|20205)/.test(code))
                            $account.addClass('error').next('.position-tips').html(self.retStatus[code]).show();
                        else if (20206==code)
                            $password.addClass('error').next('.position-tips').text(self.retStatus.login[code]).show();
                        else if (20221==code)
                            $vcode.addClass('error').next('.position-tips').text(self.retStatus.login[code]).show();
                        else
                            $bottomError.html(self.retStatus.login[code] || result.statusText || "未知错误").show();
                         
                         $password.val('');
                         //yinyong@sogou-inc.com,2013-11-23[16:42:42]
                         if(result.data&&result.data.needCaptcha){
                                $('div.vcode-area').show();
                                $vcode.val('');
                                self.refreshVcode($img);
                         }
                        break;
                }


            }).fail(function(data) {
                $bottomError.html('网络异常，请稍后尝试注册').show();
                self.submited = false
            })
        },
        //校验是否为空
        checkEmpty: function($input, $error) {

            return $input.val() == '' ? ($input.addClass('error'), $error.html('不能为空').show(), false) : true

        },
        //刷新验证码
        refreshVcode: function($img) {
            //yinyong@sogou-inc.com,2013-11-21[16:09:05]
            //Fixed
           /* var self = this,
                ts = new Date().getTime(),
                url = self.sogouBaseurl + '/vcode/register/?nocache=' + ts
                $img.attr('src', url)*/
             var ts = +new Date,
                token = utils.uuid(),
                url = '/captcha?token=' + token + '&t=' + ts;
            $img.attr("src", url).attr("data-token", token);
        },
        initLoginHistory: function() {

            var login_arr = [],
                login_history = splus ? 　splus.login_history : null;

            this.formatHistoryData(login_history)
            this.getHistorySelect()
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
        },
        renderSelect: function($container, list) {
            var self = this
            $container.empty()
            var $ul = $("<ul></ul>")
            $.each(list, function(index, obj) {
                var liHtml = [
                    '<li index="' + index + '">',
                    '   <div class="photo">',
                    '       <img src="' + obj.img + '">',
                    '   </div>',
                    '   <div class="caption">',
                    '       <p class="id">' + obj.sname + '</p>',
                    '   </div>',
                    '   <a href="javascript:" class="del" sid="' + obj.sid + '" title="删除">×</a>',
                    '</li>'].join("")
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
            this.list = list
        }

    }
    $.extend(Login,common)
    return Login;
});