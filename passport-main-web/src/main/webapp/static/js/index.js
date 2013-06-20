
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
        },
        addIframe: function(url){
            var iframe = document.createElement('iframe');
            iframe.src = url;
            
            document.body.appendChild(iframe);
        },
        getScript: function(url , callback){
            var script = document.createElement("script");
            var head = document.head;
            script.async = true;
            script.src = url;
            script.onload = script.onreadystatechange = function( _, isAbort ) {
                if ( isAbort || !script.readyState || /loaded|complete/.test( script.readyState ) ) {
                    script.onload = script.onreadystatechange = null;
                    if ( script.parentNode ) {
                        script.parentNode.removeChild( script );
                    }
                    script = null;
                    if ( !isAbort ) {
                        callback( );
                    }
                };
            };

            head.insertBefore( script, head.firstChild );
        }
    };

});

define('conf',[],function(){


    return{
        client_id:"1120",
        redirectUrl: "/static/api/jump.htm",
        thirdRedirectUrl:"/static/api/tj.htm"
    };
});

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
        return true;
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

            ui.checkbox('#RemChb');

            PassportSC.appid = conf.client_id;
            PassportSC.redirectUrl = location.protocol +  '//' + location.hostname + ( location.port ? (':' + location.port) :'' ) + conf.redirectUrl;
            $('#Login').on('submit' , function(){
                var $el = $('#Login');

                if( !$.trim($el.find('input[name="username"]').val()) || !$.trim($el.find('input[name="password"]').val()) ){
                    showUnameError('请输入用户名密码');
                    return false;;
                }

                if( vcodeInited && !$.trim( $el.find('input[name="captcha"]').val() ) ){
                    showVcodeError('请输入验证码');
                    $el.find('input[name="captcha"]').focus();
                    return false;
                }

                PassportSC.loginHandle( $el.find('input[name="username"]').val() , 
                                        $el.find('input[name="password"]').val() ,
                                        $el.find('input[name="captcha"]').val() , 
                                        $el.find('input[name="autoLogin"]').val(),
                                        document.getElementById('logdiv'),
                                        function(data){
                                            var refreshed = false;
                                            var captchaIpt = $el.find('input[name=captcha]');
                                            if( +data.needcaptcha ){
                                                if(initVcode()) {
                                                    refreshed = true;
                                                }
                                                //showVcodeError('请输入验证码');
                                                //captchaIpt.focus();
                                            }
                                            if( +data.status == 20221 ){//vcode
                                                var text = captchaIpt.val() ? '验证码错误':"请输入验证码";
                                                showVcodeError(text);
                                                !refreshed && refreshVcode();
                                                captchaIpt.focus();
                                            }else if( +data.status == 20230 ){
                                                showUnameError('未知错误');
                                                !refreshed && refreshVcode();
                                            }else{
                                                showUnameError('用户名或密码错，请重新输入');
                                                !refreshed && refreshVcode();
                                            }
                                        } ,
                                        function(){
                                            location.href = "https://" + location.hostname;
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

            $('.login .third-login a').each(function(idx,item){
                $(item).attr('href' , 'https://account.sogou.com/connect/login?provider=' + $(item).html() 
                             + '&client_id=' + conf.client_id
                             + '&ru=' + encodeURIComponent(location.href)
                            );
            });
            var inputs = $('#Login .password input , #Login .username input , #Login .vcode input');
            inputs.focus(function(){
                $(this).prev().hide();
                $(this).parent().find('b').show();
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
        }
    };
});
