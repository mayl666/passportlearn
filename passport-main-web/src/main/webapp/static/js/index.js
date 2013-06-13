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

    return {
        init: function(){
            ui.checkbox('#RemChb');
            $('#Login').append('<input type="hidden" name="token" value="'+ utils.uuid()  +'" class="token"/>');

            $('#Login').on('submit' , function(){
                $.post($(this).attr('action'), $(this).serialize() , function(data){
                    data = utils.parseResponse(data);

                    if( !data.status ){
                        alert('登录成功');
                        data.ru && (window.location.href = data.ru);
                    }else if( data.needCaptcha ){
                        initVcode();
                    }
                });
                
                return false;
            });
        }
    };
});
