/*
 * form module script
 * @author zhengxin
*/
 



define(['./utils','./uuibase' , './uuiForm'] , function(utils){

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
        }
    };

    var NormalDesc = {
        email:"请输入您作为账号的邮箱名",
        password:"6-16位，字母(区分大小写)、数字、符号"
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
        var types = $el.attr('uui-type');
        types = (types || '').split(' ');
        var type;
        _.forEach(types , function(value){
            if( value!= 'require' && !type && NormalDesc[value] )
                type = value;
        });
        return type? ( NormalDesc[type] || '' ) : '';
    };
    var getError = function($el , name){
        return ErrorDesc[name] && ErrorDesc[name]($el) || '';
    };

    var initToken = function($el){
        var token = utils.uuid();
        $el.find('.token').val(token);
        $el.find('.vpic img').attr('src' , "https://account.sogou.com/captcha?token="+ token);
    };

    var checkUsername = function($el , cb){
        var ipt = $el.find('input[name="username"]');
        $.get('/web/checkusername' , {
            username: ipt.val()
        } , function(data){
            if( typeof data == 'string' ){
                try{
                    data = eval('('+data+')');
                }catch(e){
                    data = {status:-1,statusText:'服务器故障'};
                }
            }
            if( !+data.status ){//success
                cb && cb(0);
            }else{
                createSpan(ipt,'error');
                getSpan(ipt , 'error').show().html(data.statusText);
                cb && cb(1);
            }
        });
        
    };

    var bindReg = function($el){
        $el.find('input[name=username]').blur(function(){
            var errorspan = $(this).parent().parent().find('.error');
            if( !errorspan || !errorspan.length || errorspan.css('display') == 'none' ){
                checkUsername($el );
            }
        });
    };

    var bindOptEvent = function($el){
        $el.find('.vpic img,.change-vpic').click(function(){
            initToken($el);
            return false;
        });
        
        var action = $el.attr('action');
        if( action.indexOf('reguser') ){
            bindReg($el);
        }
    };

    return{
        render: function($el){
            $el.uuiForm({
                type:'blur',
                onfocus: function($el){
                    getSpan($el , 'error').hide();
                    var desc = getDesc($el);
                    if( desc && desc.length ){
                        createSpan($el,'desc');
                        getSpan($el , 'desc').show().html(desc);
                    }
                    
                },
                onblur: function($el){
                    getSpan($el , 'desc').hide();
                },
                onsinglefail: function($el , name){
                    name = name.split('(')[0];
                    var desc = getError($el , name);
                    if( desc && desc.length ){
                        createSpan($el,'error');
                        getSpan($el , 'error').show().html(desc);
                    }
                },
                onformsuccess: function($el){
                    
                    checkUsername($el,function(status){
                        if( !status ){
                            $.post($el.attr('action'), $el.serialize() , function(data){
                                
                            });
                        }
                        return false;
                    });
                    return false;
                }
            });
            $el.append('<input type="hidden" name="token" value="" class="token"/>');
            initToken($el);
            bindOptEvent($el);
        }
    };
});
