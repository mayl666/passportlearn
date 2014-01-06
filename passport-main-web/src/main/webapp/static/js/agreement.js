
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

            iframe.style.position = 'absolute';
            iframe.style.top = '1px';
            iframe.style.left = '1px';
            iframe.style.width = '1px';
            iframe.style.height = '1px';
            
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

/*
 * common module script
 * @author zhengxin
*/
 



define('common',['./utils'],function(utils){


    return{
        addUrlCommon: function(data){
            if( data.ru ){
                $('.main-content .nav li a,.banner li a').each(function(idx , item){
                    $(item).attr('href' , $(item).attr('href') + '?ru=' + encodeURIComponent(data.ru));
                });
            }

            if( data.client_id ){
                $('.main-content .nav li a,.banner li a').each(function(idx , item){
                    $(item).attr('href' , 
                                 $(item).attr('href') 
                                 + ($(item).attr('href').indexOf('?') == -1 ? '?' : '&')
                                 + 'client_id=' + data.client_id
                                );
                });
            }

        },
        showBannerUnderLine: function(){
            $('.banner ul').show();
            var currentBanner = $('.banner ul li.current');
            if( currentBanner.length ){
                $('.banner .underline').css('left' , currentBanner.position().left)
                    .css('width' , currentBanner.css('width'));
            }
        },
        parseHeader: function(data){
            $('#Header .username').html(decodeURIComponent(data.uniqname||data.username));
            if( data.username||data.uniqname ){
                $('#Header .info').show();
            }
        },
        bindJumpEmail: function(){
            $('#JumpToUrl').click(function(){
                if( $('#JumpTarget') ){
                    window.open( utils.getUrlByMail($('#JumpTarget').html()) );
                }
                return false;
            });
        },
        /**
         * [bindResendEmail description]
         * @author yinyong
         * @version 0.1
         */
        bindResendEmail:function(data){
            var self=this;
            var count=60;
            var inter=null;
            $('#ResendEmail').click(function(e){
                if(!inter){
                    var btn=this;
                    var time=count;
                
                    $.ajax({
                        url:"/web/resendActiveMail",
                        data:{
                            client_id:1120,
                            username:data.email
                        },
                        type:"post",
                        error:function(xhr,error){
                            alert("通信错误");
                        },success:function(){
                            inter = setInterval(function() {
                                $(btn).text(time--+"秒后重发");
                                if (!time) {
                                    clearInterval(inter);
                                    inter = null;
                                    $(btn).text("重发验证邮件");
                                }
                            }, 1000);
                        }
                    });
                }
                e.preventDefault();
            });
        }//bindResendEmail
    };
});

/*
 * feedback module script
 * @author zhengxin
*/
 



define('agreement',['./common'] , function(common ){



    return{
        init: function(){
            common.showBannerUnderLine();

        }
    };
});
