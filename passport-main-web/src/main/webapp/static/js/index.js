
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
        addZero: function(num,len){
            num = num.toString();
            while( num.length < len ){
                num = '0'+ num;
            }
            return num;
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
        addIframe: function(url , callback){
            var iframe = document.createElement('iframe');
            iframe.src = url;
            
            if (iframe.attachEvent){
                iframe.attachEvent("onload", function(){
                    callback && callback();
                });
            } else {
                iframe.onload = function(){
                    callback && callback();
                };
            }

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
        },
        getUrlByMail:function(mail){
            mail = mail.split('@')[1];
            if( !mail ) return false;
            var hash = {
                "139.com":"mail.10086.cn",
                'gmail.com': 'mail.google.com', 
                'sina.com': 'mail.sina.com.cn', 
                'yeah.net': 'www.yeah.net', 
                'hotmail.com': 'www.hotmail.com', 
                'live.com': 'www.outlook.com', 
                'live.cn': 'www.outlook.com', 
                'live.com.cn': 'www.outlook.com', 
                'outlook.com': 'www.outlook.com', 
                'yahoo.com.cn': 'mail.cn.yahoo.com', 
                'yahoo.cn': 'mail.cn.yahoo.com', 
                'ymail.com': 'www.ymail.com', 
                'eyou.com': 'www.eyou.com', 
                '188.com': 'www.188.com', 
                'foxmail.com': 'www.foxmail.com' 
            };
            var url;
            if( mail in hash ){
                url= hash[mail];
            }else{
                url= 'mail.' + mail;
            }
            return 'http://' + url;
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
                if( !$.trim($el.find('input[name="username"]').val()) || !passwordVal ||
                  passwordVal.length > 16 || passwordVal.length<6  ){
                    showUnameError('请输入用户名密码');
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
                                        $el.find('input[name="autoLogin"]').val(),
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
                                                showUnameError('未知错误');
                                                !refreshed && refreshVcode();
                                            }else{
                                                showUnameError('用户名或密码错，请重新输入');
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
                                                location.href = "https://" + location.hostname;
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

            $('.login .third-login a').each(function(idx,item){
                $(item).attr('href' , 'https://account.sogou.com/connect/login?provider=' + $(item).html() 
                             + '&client_id=' + conf.client_id
                             + '&ru=' + encodeURIComponent(location.href)
                            );
            });
            
            $('#Login .username input').change(function(){
                if( !$.trim($(this).val()) ) return;
                $.get('/web/login/checkNeedCaptcha' , {
                    username:$.trim($(this).val()),
                    client_id: conf.client_id
                } , function(data){
                    data = utils.parseResponse(data);
                    if( data.data.needCaptcha ){
                        initVcode();
                    }else{
                        hideVcode();
                    }
                });
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
