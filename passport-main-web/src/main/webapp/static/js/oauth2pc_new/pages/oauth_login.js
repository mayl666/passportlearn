/**
  * oauth_login.js
  *
  * changelog
  * 2013-11-26[20:58:40]:copied
  *
  * @info yinyong,osx-x64,Undefined,10.129.164.117,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/pages
  * @author yinyong#sogou-inc.com
  * @version 0.0.1
  * @since 0.0.1
  */

require( ['lib/base64'],function(){
    var logintype,result,sname,nick,accesstoken,refreshtoken,sid,passport
    if(splus.logintype)
        logintype=splus.logintype;
    if(splus.result)
        result = splus.result;
    if(splus.sname)
        sname =  splus.sname ;
    if(splus.nick)
        nick = splus.nick;
    if(splus.accesstoken)
        accesstoken = splus.accesstoken;
    if(splus.refreshtoken)
        refreshtoken = splus.refreshtoken ; 
    if(splus.sid)
        sid = splus.sid ; 
    if(splus.passport)
        passport = splus.passport ; 
    var msg = logintype+'|'+result+'|'+accesstoken+'|'+refreshtoken + '|'+sname+'|'+nick+ '|'+sid+'|'+passport+'|'+'1';     
    window.external.passport('result',msg);
});