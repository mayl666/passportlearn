define('index' , ['./ui' , './utils' , './conf'] , function(ui , utils , conf){
    
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

            PassportSC.appid = conf.client_id;
            PassportSC.redirectUrl = location.protocol +  '//' + location.hostname + ( location.port ? (':' + location.port) :'' ) + conf.redirectUrl;
            $('#Login').on('submit' , function(){
                var $el = $('#Login');

                if( !$.trim($el.find('input[name="username"]').val()) || !$.trim($el.find('input[name="password"]').val()) ){
                    showUnameError('请输入用户名密码');
                    return false;;
                }

                PassportSC.loginHandle( $el.find('input[name="username"]').val() , 
                                        $el.find('input[name="password"]').val() ,
                                        $el.find('input[name="captcha"]').val() , 
                                        $el.find('input[name="autoLogin"]').val(),
                                        document.getElementById('logdiv'),
                                        function(data){
                                            if( data.needCaptcha ){
                                                initVcode();
                                                showVcodeError('请输入验证码');
                                            }
                                            if( +data.status == 20221 ){//vcode
                                                showVcodeError('请输入验证码');
                                            }else{
                                                showUnameError('用户名或密码错，请重新输入');
                                            }
                                        } ,
                                        function(){
                                            location.href = "@protocol@://" + location.hostname;
                                            return;
                                        }
                                      );
                
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
