/**
  * login.js
  *
  * changelog
  * 2013-11-21[15:10:20]:copied
  *
  * @info yinyong,osx-x64,UTF-8,10.129.173.11,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/pages
  * @author yinyong@sogou-inc.com
  * @version 0.0.1
  * @since 0.0.1
  */
require(['app/login'], function(Login) {
  if(/#register/.test(location.hash)){
    return location.assign('/sogou/fastreg?instanceid='+(window.splus&&window.splus.instanceid||'')+'&v='+window.v);
  }
  Login.init(); 
});
