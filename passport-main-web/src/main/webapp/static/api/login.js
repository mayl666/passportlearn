/*
 * Sogou Passport Login
 * @author zhengxin
 */


(function(){

    var utils = {
        tmpl:function(str, data){
            var fn = 
                    new Function("obj",
                                 "var p=[],print=function(){p.push.apply(p,arguments);};" +
                                 "with(obj){p.push('" +
                                 str
                                 .replace(/[\r\t\n]/g, " ")
                                 .split("<%").join("\t")
                                 .replace(/((^|%>)[^\t]*)'/g, "$1\r")
                                 .replace(/\t=(.*?)%>/g, "',$1,'")
                                 .split("\t").join("');")
                                 .split("%>").join("p.push('")
                                 .split("\r").join("\\'")
                                 + "');}return p.join('');");
            return data ? fn( data ) : fn;
        },
        uuid: function(){
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            };            
            return s4() + s4()  + s4()  + s4()  +
                s4() +  s4() + s4() + s4();
        }
    };




    
    var PassportSC = window['PassportSC'] || {};
    
    PassportSC._token = utils.uuid();
    
    PassportSC._passhtml = '<form method="post" action="https://account.sogou.com/web/login" target="_PassportIframe">'
        +'<input type="hidden" name="username" value="<%=username%>">'
        +'<input type="hidden" name="password" value="<%=password%>">'
        +'<input type="hidden" name="captcha" value="<%=vcode%>">'
        +'<input type="hidden" name="autoLogin" value="<%=isAutoLogin%>">'
        +'<input type="hidden" name="client_id" value="<%=appid%>">'
        +'<input type="hidden" name="xd" value="<%=redirectUrl%>">'
        +'<input type="hidden" name="token" value="<%=token%>">'
        +'</form>'
        +'<iframe id="_PassportIframe" name="_PassportIframe" src="about:blank" style="width：1px;height:1px;position:absolute;left:-1000px;"></iframe>';

    PassportSC._logincb = function(data){
        if( !+data.status ){
            PassportSC.onsuccess && PassportSC.onsuccess(data);
        }else{
            PassportSC.onfailure && PassportSC.onfailure(data);
        }
    };

    PassportSC.getToken = function(){
        return PassportSC._token;
    };

    PassportSC.loginHandle = function(username, password , vcode , isAutoLogin , container , onfailure, onsuccess){
        if( arguments.length < 7 ){
            onsuccess = onfailure;
            onfailure = container;
            container = isAutoLogin;
            vcode = '';
        }

        if( !PassportSC.redirectUrl ){
            window['console'] && console.error('Must specify redirect url.Exit!');
            return;
        }
        if( !PassportSC.appid ){
            window['console'] && console.error('Must specify appid.Exit!');
            return;
        }

        

        if(!container)
            return;

        PassportSC.onsuccess = onsuccess,
        PassportSC.onfailure = onfailure;
        container.innerHTML = utils.tmpl(PassportSC._passhtml , {
            username:username,
            password:password,
            vcode:vcode,
            isAutoLogin: isAutoLogin,
            appid: PassportSC.appid,
            redirectUrl:PassportSC.redirectUrl,
            token: PassportSC._token
        });
        container.getElementsByTagName('form')[0].submit();
    };





    window['PassportSC'] = PassportSC;



})();
