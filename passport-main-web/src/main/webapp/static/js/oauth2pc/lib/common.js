/**
 * NetworkInterfaces.js=>common.js
 *
 * changelog
 * 2013-11-21[17:56:05]:created
 * 2013-11-22[10:50:07]:rename to lib/common.js
 *
 * @info yinyong,osx-x64,UTF-8,10.129.173.11,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/lib
 * @author yinyong@sogou-inc.com
 * @version 0.0.1
 * @since 0.0.1
 */
define([], function() {
    // yinyong@sogou-inc.com,2013-11-21[20:00:43]
    // fixed location.origin
    if (!location.origin) {
        location.origin = location.protocol + '//' + location.host;
    }
    return {
        //yinyong@sogou-inc.com,2013-11-21[17:04:29]
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
               // "10002": "必填参数错误",
                "20201": "此帐号已注册，请直接登录",
                "20217": "暂不支持邮箱注册",
                "20225": "该手机已注册",
                "20241": " 暂不支持sohu域内邮箱注册 "
            },
            login: {
                "10001": "系统级错误",
                "10002": "参数错误,请输入必填的参数或参数验证失败",
                "10009": "帐号不存在",
                "20205": "账号不存在",
                "20221": " 验证码错误",
                "20231": "登陆账号未激活",
                "20232": "登陆账号被封杀",
                "20230": "当前账号或者IP登陆操作存在异常",
                "20206": "账号或密码错误",
                "20226": "用户登录失败",
                "20240": "生成cookie失败"
            },
            sendSms: {
                "10001": "未知错误",
                "10002": "参数错误,请输入必填的参数或参数验证失",
                "20229": "号未登录，请先登",
                "20243": "SOHU域用户不允许此",
                "20244": "第三方账号不允许此",
                "20225": "手机号已绑定其他",
                "20209": "今日验证码校验错误次数已超过",
                "20202": "今日手机短信发送次数超过",
                "20204": "一分钟内只能发一条",
                "20213": "手机验证码发送"
            },
            register: {
               // "10002": "必填参数错误",
                "20199": "当前注册ip次数已达上限或该ip已在黑名单中",
                "20227": "密码必须为字母和数字且长度大于6位 ",
                "20224": "当日注册次数已达上",
                "20221": "验证码错误",
                "10001": "未知错误",
                "10010": "client_id不存在 ",
                "20214": "验证码错误或已过期",
                "20201": "此帐号已注册，请直接登录",
                "20230": "前账号或者IP操作存在异常"
            }
        },
        //yinyong@sogou-inc.com,2013-11-21[20:48:18]
        //added
        validObj: {
            account: {
                defaultMsg: "帐号/手机号/邮箱",
                errMsg: '该登录名不存在',
                emptyMsg: '请填写登录名',
                nullable: false,
                regStr: /^\S{4,50}$/
            },
            regaccount:{
                defaultMsg: "帐号/手机号/邮箱",
                errMsg: '请用4-16位字母、数字或"-"'/*function(val){
                    if(!val)return this.emptyMsg;
                    else if(val.length<4||val.length>50)return "长度必须为4-50个字符";
                    else return "注册用户名非法";
                }*/,
                emptyMsg: '请填写注册用户名',
                nullable: false,
                regStr:/^[a-z]([a-zA-Z0-9_.]{3,15})$/
            },
            phone: {
                errMsg: '请正确填写手机号',
                emptyMsg: '手机号不能为空',
                nullable: false,
                regStr: /^1\d{10,13}$/
            },
            email: {
                errMsg: '请正确输入邮箱（非必填）',
                emptyMsg: '',
                nullable: true,
                regStr: /^(\w)+(\.\w+)*@([\w_\-])+((\.\w+)+)$/
            },
            password: {
                errMsg:function(val){
                    if(!val)return this.emptyMsg;
                    if(val.length<6||val.length>16)return "长度必须为6-16位";
                    else return '密码必须字母、数字、下划线的组合';
                },
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
        },
        //初始化搜狗浏览器接口
        exPassport: (function() {
            if (window.external && window.external.passport) {
                return window.external.passport
            } else {
                return function() {
                    var _noop = $.noop;
                    if (window.console && console.log) {
                        console.log('the method \'' + arguments[0] + '\' is not supported in your browser ,make sure you using sogou-browser when see this page')
                    }
                    _noop();
                };
            }
        })(),
        //按照规则校验输入是否合理
        check: function($input, valid) {
            var self = this,
                $error = $input.next('span.position-tips'),
                inputValue = $input.val(),
                nullable = valid.nullable,
                regrex = valid.regStr,
                emptyMsg = valid.emptyMsg,
                errMsg = valid.errMsg;
            if ($input.attr('name') == 'vcode') {
                $error = $input.next('span.chkbtn-wrap').find('>span.chktext')
            }

            if (!nullable && inputValue == "") {
                $input.addClass("error");
                $error.html(emptyMsg).show();
                return false;
            } else if (nullable && inputValue == "") {
                $input.removeClass("error");
                $error.html('').hide();
                return true;
            } else {
                if (!regrex.test(inputValue)) {
                    $input.addClass("error");
                    $error.html(typeof errMsg==='function'?errMsg.call(valid,inputValue):errMsg).show();
                    return false;
                } else {
                    $input.removeClass("error");
                    $error.html('').hide();
                    return true;
                }
            }
        } //check

    }
});