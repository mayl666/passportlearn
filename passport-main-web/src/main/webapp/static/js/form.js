/*
 * form module script
 * @author zhengxin
*/

define(['./utils','./conf','./uuibase' , './uuiForm'] , function(utils,conf){

    /**
     * Just accept "CJK Unified Ideographs"
     * @see http://zh.wikipedia.org/wiki/Unicode%E4%B8%AD%E6%97%A5%E9%9F%93%E7%B5%B1%E4%B8%80%E8%A1%A8%E6%84%8F%E6%96%87%E5%AD%97%E5%88%97%E8%A1%A8
     */
    
    var nicknameReg=/^([a-zA-Z0-9]|[\u4e00-\u9fa5]){2,12}$/;
    var invalidNicknameKey=['搜狐','搜狗','搜狐微博','sohu','souhu','sogou','sougou'];

    if(!Array.indexOf)
    {
        Array.prototype.indexOf = function(item) {
            for (var i = 0; i < this.length; ++i) {
                if (this[i] == item)
                    return i;
            }
            return -1;
        };
    }

    $.uuiForm.addType('password' , function(value){
        return value.length<=16 && value.length>=6;
    });
    $.uuiForm.addType('vpasswd' , function(value , target){
        var targetIpt = $( '#' + target.slice(0,1).toUpperCase() + target.slice(1) + 'Ipt' );
        if( targetIpt && targetIpt.length){
            var vvalue = targetIpt.val();
            return vvalue == value;
        }
        return true;
    });
    $.uuiForm.addType('nick' , function(value){
        return /^[a-zA-Z]([a-zA-Z0-9_.]{3,15})$/.test(value);
    }); 

    $.uuiForm.addType('new_answer' , function(value){
        return value&&value.replace(/[^\x00-\xff]/g,'xx').length<=50;
    });    

    $.uuiForm.addType('nickname' , function(value){
        return  nicknameReg.test(value)&&(invalidNicknameKey.indexOf(value)<0);
    });

    $.uuiForm.addType('uniqname' , function(value){
        return  nicknameReg.test(value)&&(invalidNicknameKey.indexOf(value)<0);
    });    

    $.uuiForm.addType('username' , function(value){
        return   value.length<=50&&(value==''||/^[a-z\u4e00-\u9fa5]+$/i.test(value));
    });

    $.uuiForm.addType('personalid', function(value) {
        return (value == "") || idTester.valid(value);
    });
    $.uuiForm.addType('nonempty', function(value) {
        return !!$.trim(value);
    });
    
    //yinyong#sogou-inc.com:Copied from Internet.
    var idTester = {
        aCity: {
            11: "北京",
            12: "天津",
            13: "河北",
            14: "山西",
            15: "内蒙古",
            21: "辽宁",
            22: "吉林",
            23: "黑龙江",
            31: "上海",
            32: "江苏",
            33: "浙江",
            34: "安徽",
            35: "福建",
            36: "江西",
            37: "山东",
            41: "河南",
            42: "湖北",
            43: "湖南",
            44: "广东",
            45: "广西",
            46: "海南",
            50: "重庆",
            51: "四川",
            52: "贵州",
            53: "云南",
            54: "西藏",
            61: "陕西",
            62: "甘肃",
            63: "青海",
            64: "宁夏",
            65: "新疆",
            71: "台湾",
            81: "香港",
            82: "澳门",
            91: "国外"
        },
        valid: function(sId) {
            var iSum = 0;
            var info = "";
            if (!/^\d{17}(\d|x)$/i.test(sId)) return false;
            sId = sId.replace(/x$/i, "a");
            if (this.aCity[parseInt(sId.substr(0, 2))] == null) return false;//"Error:非法地区";
            var sBirthday = sId.substr(6, 4) + "-" + Number(sId.substr(10, 2)) + "-" + Number(sId.substr(12, 2));
            var d = new Date(sBirthday.replace(/-/g, "/"))
            if (sBirthday != (d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate())) return false;//"Error:非法生日";
            for (var i = 17; i >= 0; i--) iSum += (Math.pow(2, i) % 11) * parseInt(sId.charAt(17 - i), 11)
            if (iSum % 11 != 1) return false;//"Error:非法证号";
            return true;//aCity[parseInt(sId.substr(0, 2))] + "," + sBirthday + "," + (sId.substr(16, 1) % 2 ? "男" : "女")
        }
    };

    var ErrorDesc = {
        require: function($el){
            var label= $el.parent().prev().html();
            return '请填写' + label.replace('：', '');
        },
        nonempty: function($el){
            return '不能为空';
        },
        email: function(){
            return '邮箱格式不正确';
        },
        password: function(){
            return '密码长度为6-16位';
        },
        cellphone: function(){
            return '请输入正确的手机号码';
        },
        vpasswd: function(){
            return '两次密码输入不一致';
        },
        range: function($el){
            return "";
        },
        max: function($el , max){
            return '输入字符请少于' + max + '个字';
        },
        nickname: function($el){
            if($el.val().length <2 || $el.val().length>12 ){
                return '昵称长度为2-12位';
            }else if(/[^\u4e00-\u9fa5a-zA-Z0-9]/.test($el.val())){
                return "只能使用中文、字母、数字";
            }else if(invalidNicknameKey.indexOf($el.val())>-1){
                return "含有非法关键字"
            }
            return '昵称不合法';
        }, 
        uniqname: function($el){
            if($el.val().length <2 || $el.val().length>12 ){
                return '昵称长度为2-12位';
            }else if(/[^\u4e00-\u9fa5a-zA-Z0-9]/.test($el.val())){
                return "只能使用中文、字母、数字";
            }else if(invalidNicknameKey.indexOf($el.val())>-1){
                return "含有非法关键字"
            }
            return '昵称不合法';
        },
        username:function($el){
            if ($el.val().length > 50)
                return "不能超过50个字符"
            else return "真实姓名仅允许输入英文字母和汉字";
        },  
        new_answer:function($el){
             return "不能超过50个英文字母或25个汉字"
        },
        nick: function($el){
            if( $el.val().length <4 || $el.val().length>16 ){
                return '个性帐号长度为4-16位';
            }
            return '字母开头的数字、字母、下划线或组合';
        },
        personalid:function(){
            return "请输入18位有效的身份证号码";
        }
    };

    var NormalDesc = {
        email:"请输入您作为帐号的邮箱名",
        password:"6-16位，字母(区分大小写)、数字、符号",
        nick: "字母开头的数字、字母、下划线或组合"
    };

    var createSpan= function($el , className){
        if( !$el.parent().parent().find('.'+className).length ){
            $el.parent().parent().append('<span class="'+ className +'"></span>');
        }
    };
    var getSpan=function( $el, className ){
        return $el.parent().parent().find('.' + className);
    };
    var getDesc= function($el){
        if( $el.attr('data-desc') )
            return $el.attr('data-desc');
        var types = $el.attr('uui-type');
        types = (types || '').split(' ');
        var type;
        _.forEach(types , function(value){
            if( value!= 'require' && !type && NormalDesc[value] )
                type = value;
        });
        return type? ( NormalDesc[type] || '' ) : '';
    };
    var getError = function($el , name , args){
        return ErrorDesc[name] && ErrorDesc[name]($el , args) || '';
    };

    var initToken = function($el){
        var token = utils.uuid();
        $el.find('.token').val(token);
        $el.find('.vpic img').attr('src' , "/captcha?token="+ token + '&t=' + +new Date());
    };


    var bindOptEvent = function($el){
        $el.find('.vpic img,.change-vpic').click(function(){
            $el.find('.vpic img').attr('src' , "/captcha?token="+ $el.find('.token').val() + '&t=' + +new Date());
            return false;
        });
        $el.click(function(){
            $el.find('.form-error,.form-success').hide();;
        });
    };

    return{
        render: function($el , config){
            config = config || {};
            $el.uuiForm({
                type:'blur',
                onfocus: function($el){
                    $el.parent().addClass('form-el-focus');
                    getSpan($el , 'error').hide();
                    var desc = getDesc($el);
                    if( desc && desc.length ){
                        createSpan($el,'desc');
                        getSpan($el , 'desc').show().html(desc);
                    }
                    
                },
                onblur: function($el){
                    $el.parent().removeClass('form-el-focus');
                    getSpan($el , 'desc').hide();
                },
                onsinglefail: function($el , name){
                    var args = name.split('(')[1];
                    name = name.split('(')[0];
                    args = args ? args.slice(0,-1).split(','):[];
                    var desc = getError($el , name , args);
                    if( desc && desc.length ){
                        createSpan($el,'error');
                        getSpan($el , 'desc').hide();
                        getSpan($el , 'error').show().html(desc);
                    }
                },
                onsinglesuccess: function($el,name){
                    getSpan($el,'error').hide();
                },
                onformsuccess: function($el){
                    if( !config.onbeforesubmit || config.onbeforesubmit($el) ){
                        $.post($el.attr('action'), $el.serialize() , function(data){
                            data = utils.parseResponse(data);
                            
                            if( !+data.status ){
                                $el.find('.form-success').show().find('span').html( data.statusText? data.statusText: '提交成功');
                                config.onsuccess && config.onsuccess($el , data);
                            }else{
                                var errorText = data.statusText ? data.statusText.split('|')[0]: '未知错误';
                                $el.find('.form-error').show().find('span').html(errorText);
                                config.onfailure && config.onfailure($el);
                            }
                        });
                    }
                    return false;
                },
                onformfail: function($el){
                    $el.find('.desc').hide();
                    config.onformfail && config.onformfail();
                    return false;
                }
            });
            $el.append('<input type="hidden" name="token" value="" class="token"/>');

            var data = {};
            try{
                data = $.evalJSON(server_data).data || {};
            }catch(e){window['console'] && console.log(e);}
            

            $el.append('<input name="client_id" value="'+ ( data.client_id? data.client_id : conf.client_id) +'" type="hidden"/>');
            
            $el.find('.form-btn').before('<div class="form-error"><span></span></div>');
            $el.find('.form-btn').before('<div class="form-success"><span></span></div>');
            
            initToken($el);
            bindOptEvent($el);
        },
        initTel: function(iptname){
            var tm,
                text = '秒后重新获取验证码',
                oldText,
                oldtimeout = 60,
                timeout = oldtimeout,
                status;

            $('.tel-valid-btn').click(function(){
                if(status)return;
                $('.main-content .form form').find('.tel-valid-error').hide();

                var usernameIpt = $('.main-content .form form input[name="'+ ( iptname?iptname: 'username' ) +'"]');
                if( usernameIpt && usernameIpt.length ){
                    var errorSpan = usernameIpt.parent().find('.error');
                    if( !$.trim(usernameIpt.val()).length ){
                        usernameIpt.blur();
                        return;
                    }
                    if( errorSpan.length && errorSpan.css('display') != 'none' )
                        return;
                }
                status = true;
                var el = $(this);
                oldText = el.html();
                //el.html(timeout + text);
                //el.addClass('tel-valid-btn-disable');

                var url = el.attr('action') || '/web/sendsms';
                $.get(url , {
                    mobile: usernameIpt.val(),
                    new_mobile: usernameIpt.val(),
                    client_id: conf.client_id,
                    t: +new Date()
                } , function(data){
                    data = utils.parseResponse(data);
                    if( +data.status ){
                        if( +data.status != 20201 ){
                            $('.main-content .form form').find('.tel-valid-error').show().html(data.statusText? data.statusText : '系统错误');;
                        }
                        resetBtn();
                    }else{
                        //Fixed by yinyong
                        el.addClass('tel-valid-btn-disable');
                        tm = setInterval(function() {
                            if (!--timeout) {
                                resetBtn();
                            } else {
                                el.html(timeout + text);

                            }

                        }, 1000);
                    }
                        
                });

                function resetBtn(){
                    el.html(oldText);
                    clearInterval(tm);
                    status = false;
                    timeout = oldtimeout;
                    el.removeClass('tel-valid-btn-disable');
                }

            });

        },
        showFormError: function(text){
            $('.main-content .form form').find('.form-error').show().find('span').html(text);;
        },
        freshToken:function($el){
            initToken($el);
        }
    };
});
