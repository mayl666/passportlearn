
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
    };

    var Module_Size = {
        renren:[880,620],
        sina:[780,640],
        qq:[500,300]
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
                                            if( +data.needcaptcha ){
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
            var inputs = $('#Login .password input , #Login .username input');
            inputs.focus(function(){
                $(this).prev().hide();
            }).blur(function(){
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
