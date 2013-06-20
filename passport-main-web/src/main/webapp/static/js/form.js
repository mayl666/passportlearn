/*
 * form module script
 * @author zhengxin
*/
 



define(['./utils','./conf','./uuibase' , './uuiForm'] , function(utils,conf){

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
        return /^[a-z]([a-zA-Z0-9_.]{3,15})$/.test(value);
    });

    var ErrorDesc = {
        require: function($el){
            var label= $el.parent().prev().html();
            return '请填写' + label.replace('：', '');
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
        nick: function(){
            return '非纯数字的字母数字下划线组合';
        }
    };

    var NormalDesc = {
        email:"请输入您作为账号的邮箱名",
        password:"6-16位，字母(区分大小写)、数字、符号",
        nick: "非纯数字的字母数字下划线组合"
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
        $el.find('.vpic img').attr('src' , "/captcha?token="+ token);
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
                onformsuccess: function($el){
                    if( !config.onbeforesubmit || config.onbeforesubmit($el) ){
                        $.post($el.attr('action'), $el.serialize() , function(data){
                            data = utils.parseResponse(data);
                            
                            if( !+data.status ){
                                $el.find('.form-success').show().find('span').html('提交成功');
                                config.onsuccess && config.onsuccess($el , data);
                            }else{
                                $el.find('.form-error').show().find('span').html(data.statusText? data.statusText : '未知错误');
                                config.onfailure && config.onfailure($el);
                            }
                        });
                    }
                    return false;
                }
            });
            $el.append('<input type="hidden" name="token" value="" class="token"/>');
            $el.append('<input name="client_id" value="'+ conf.client_id +'" type="hidden"/>');
            
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
                el.html(timeout + text);
                el.addClass('tel-valid-btn-disable');

                var url = el.attr('action') || '/mobile/sendsms';
                $.get(url , {
                    mobile: usernameIpt.val(),
                    new_mobile: usernameIpt.val(),
                    client_id: conf.client_id
                } , function(data){
                    data = utils.parseResponse(data);
                    if( +data.status ){
                        $('.main-content .form form').find('.tel-valid-error').show().html(data.statusText? data.statusText : '系统错误');;
                        resetBtn();
                    }
                        
                });

                function resetBtn(){
                    el.html(oldText);
                    clearInterval(tm);
                    status = false;
                    timeout = oldtimeout;
                    el.removeClass('tel-valid-btn-disable');
                }
                tm=setInterval(function(){
                    if( !--timeout  ){
                        resetBtn();
                    }else{
                        el.html(timeout + text);

                    }
                    
                } , 1000);
            });

        },
        showFormError: function(text){
            $('.main-content .form form').find('.form-error').show().find('span').html(text);;
        }
    };
});
