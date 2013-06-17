
/*
 * ui module script
 * @author zhengxin
*/
 



define('ui',[],function(){



    return{
        checkbox: function(el){
            el = $(el);
            var target = el.data('target');
            if( !target ) return;
            target = $('#' +target);
            var checkedClass = 'checkbox-checked';
            
            el.click( function(){
                var checked = el.prop('checked');
                checked ? target.addClass(checkedClass) :
                    target.removeClass(checkedClass);
            });
        }

    };
});

/*
 * form module script
 * @author zhengxin
*/
 



define('utils',[], function(){

    
    return {
        uuid: function(){
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            };            
            return s4() + s4()  + s4()  + s4()  +
                s4() +  s4() + s4() + s4();

        },
        parseResponse: function(data){
            if( typeof data == 'string' ){
                try{
                    data = eval('('+data+')');
                }catch(e){
                    data = {status:-1,statusText:'服务器故障'};
                }
            }
            return data;
        }
    };

});

define('index' , ['./ui' , './utils'] , function(ui , utils){
    
    var vcodeInited = false;
    var initVcode = function(){
        if( vcodeInited ) return;
        vcodeInited = true;
        var $el = $('#Login');
        $el.parent().parent().addClass('login-vcode');

        $el.find('.vcode img,.vcode a').click(function(){
            refreshVcode();
            return false;
        });

        refreshVcode($el);
    };

    var refreshVcode = function(){
        $('#Login').find('.vcode img').attr('src' , "/captcha?token="+ $('#Login').find('.token').val() + '&t=' + +new Date() );
    };

    var showVcodeError = function(text){
        $('#Login .vcode-error .text').html(text).parent().show();
    };

    var showUnameError = function(text){
        $('#Login .uname-error .text').html(text).parent().show();
    };

    return {
        init: function(){
            ui.checkbox('#RemChb');
            $('#Login').append('<input type="hidden" name="token" value="'+ utils.uuid()  +'" class="token"/>');

            $('#Login').on('submit' , function(){
                $.post($(this).attr('action'), $(this).serialize() , function(data){
                    data = utils.parseResponse(data);

                    if( !+data.status ){
                        alert('登录成功');
                        data.data && data.data.cookieUrl && (window.location.href = data.data.cookieUrl);
                        return;
                    }else if( data.data.needCaptcha ){
                        initVcode();
                        showVcodeError('请输入验证码');
                    }
                    if( +data.status == 20221 ){//vcode
                        showVcodeError('请输入验证码');
                    }else{
                        showUnameError('用户名或密码错，请重新输入');
                    }
                });
                
                return false;
            });

            $(document.body).click(function(){
                $('#Login .error').hide();
            });
            $('#Login .error a').click(function(){
                $('#Login .error').hide();
                return false;
            });
        }
    };
});
