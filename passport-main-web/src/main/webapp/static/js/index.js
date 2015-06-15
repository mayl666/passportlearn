define('index' , ['./ui' , './utils' , './conf'] , function(ui , utils , conf){
    
    var vcodeInited = false;
    var initVcode = function(){
        var $el = $('#Login');
        $el.parent().parent().addClass('login-vcode');
        if( vcodeInited ) return;
        vcodeInited = true;

        $el.find('.vcode img,.vcode a').click(function(){
            refreshVcode();
            return false;
        });

        refreshVcode($el);
        return true;
    };

    var hideVcode = function(){
        var $el = $('#Login');
        $el.parent().parent().removeClass('login-vcode');
    };    

    var Module_Size = {
        renren:[880,620],
        sina:[780,640],
        qq:[500,300]
    };



    var refreshVcode = function(){
        if(vcodeInited){
            $('#Login').find('.vcode img').attr('src' , "/captcha?token="+ PassportSC.getToken() + '&t=' + +new Date() );
        }
    };

    var showVcodeError = function(text){
        $('#Login .vcode-error .text').html(text).parent().show();
    };

    var showUnameError = function(text){
        $('#Login .uname-error .text').html(text).parent().show();
    };

    return {
        init: function(){
            var login_data ={};
            try{
                login_data = $.evalJSON(server_data).data;
            }catch(e){window['console'] && console.log(e);}

            ui.checkbox('#RemChb');

            if( $.cookie('fe_uname') ){
                $('#Login .username input').val($.trim($.cookie('fe_uname')));
                $('#Login .username span').hide();
            }

            PassportSC.appid = conf.client_id;
            PassportSC.redirectUrl = location.protocol +  '//' + location.hostname + ( location.port ? (':' + location.port) :'' ) + conf.redirectUrl;
            $('#Login').on('submit' , function(){
                var $el = $('#Login');

                var passwordVal = $el.find('input[name="password"]').val();
                if( !$.trim($el.find('input[name="username"]').val()) || !passwordVal  ){
                    showUnameError('请输入用户名密码');
                    return false;;
                }

                if( passwordVal.length > 16 || passwordVal.length<6 ){
                    showUnameError('用户名密码输入错误');
                    return false;;
                }

                if( $('#Login').parent().parent().hasClass('login-vcode') && !$.trim( $el.find('input[name="captcha"]').val() ) ){
                    showVcodeError('请输入验证码');
                    $el.find('input[name="captcha"]').focus();
                    return false;
                }

                PassportSC.loginHandle( $el.find('input[name="username"]').val() , 
                                        $el.find('input[name="password"]').val() ,
                                        $el.find('input[name="captcha"]').val() , 
                                        $el.find('input[name="autoLogin"]').prop('checked')?1:0,
                                        document.getElementById('logdiv'),
                                        function(data){
                                            var refreshed = false;
                                            var captchaIpt = $el.find('input[name=captcha]');
                                            if( +data.needcaptcha ){
                                                if(initVcode()) {
                                                    refreshed = true;
                                                }
                                                $('#Login').parent().parent().addClass('login-vcode');
                                                //showVcodeError('请输入验证码');
                                                //captchaIpt.focus();
                                            }
                                            if( +data.status == 20221 ){//vcode
                                                var text = captchaIpt.val() ? '验证码错误':"请输入验证码";
                                                showVcodeError(text);
                                                !refreshed && refreshVcode();
                                                captchaIpt.focus();
                                            }else if( +data.status == 20230 ){
                                                showUnameError('登录异常，请1小时后再试');
                                                !refreshed && refreshVcode();
                                            }else{
                                                showUnameError('用户名或密码错误，请重新输入');
                                                !refreshed && refreshVcode();
                                            }
                                        } ,
                                        function(){
                                            $.cookie('fe_uname' , $('#Login .username input').val() , {
                                                path:'/',
                                                expires:365
                                            });

                                            var data ={};
                                            try{
                                                data = $.evalJSON(server_data).data;
                                            }catch(e){window['console'] && console.log(e);}
                                            if( data && data.ru ){
                                                location.href = data.ru;
                                            }else{
                                                location.href = "@protocol@://" + location.hostname;
                                            }
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

            var gets = location.search.split('#')[0].split(/[?&]/g),
                ru;
            for (var i = gets.length - 1; i >= 0; i--) {
                var kv = gets[i];
                if (/^ru=.+/.test(kv)) {
                    ru = kv.slice(3);
                    break;
                }
            };

            $('.login .third-login a').each(function(idx,item){
                var href ='@protocol@://account.sogou.com/connect/login?provider=' + $(item).html() 
                             + '&client_id=' + (login_data.client_id ? login_data.client_id : conf.client_id)
                             + '&ru=' + (ru||encodeURIComponent(location.href));
                $(item).attr('href' , href);
            });
            
            $('#Login .username input').change(function(){
                if( !$.trim($(this).val()) ) return;
                $.get('/web/login/checkNeedCaptcha' , {
                    username:$.trim($(this).val()),
                    client_id: conf.client_id,
                    t: +new Date()
                } , function(data){
                    data = utils.parseResponse(data);
                    if( data.data.needCaptcha ){
                        initVcode();
                    }else{
                        hideVcode();
                    }

                    if(0!=data.status){
                        showUnameError(data.statusText);
                    }
                });
            });
            


            var inputs = $('#Login .password input , #Login .username input , #Login .vcode input');

            var hideDescTm;
            if( /se 2.x/i.test(navigator.userAgent) || /compatible;/i.test(navigator.userAgent) ){
                hideDescTm = setInterval(function(){
                    inputs.each(function(idx,item){
                        if( $(item).val().length ){
                            $(item).prev().hide();
                        }
                    });
                },100);
            }
            inputs.focus(function(){
                $(this).prev().hide();
                $(this).parent().find('b').show();
                hideDescTm && clearInterval(hideDescTm);
            }).blur(function(){
                $(this).parent().find('b').hide();
                if( !$.trim($(this).val()) )
                    $(this).prev().show();
            });
            inputs.parent().click(function(){
                $(this).find('input').focus();
            });
            window.onload = function(){
                inputs.each(function(idx,item){
                    if( $(item).val() ){
                        $(item).prev().hide();
                    }
                });
            };


            if( login_data.ru ){
                $('.login a').each(function(idx , item){
                    if(/(^#|ru=|^javascript)/.test($(item).attr('href') ) )return;
                    $(item).attr('href' , $(item).attr('href') + '?ru=' + encodeURIComponent(login_data.ru));
                });
            }

            if( login_data.client_id ){
                $('.login a').each(function(idx , item){
                    if( $(item).attr('href') == '#' )return;
                    if( $(item).attr('href').indexOf('client_id') != -1 )return;
                    $(item).attr('href' , 
                                 $(item).attr('href') 
                                 + ($(item).attr('href').indexOf('?') == -1 ? '?' : '&')
                                 + 'client_id=' + login_data.client_id
                                );
                });
            }
            

        }


    };
});
