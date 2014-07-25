(function(){ 
    window.PassportSC=window.PassportSC||{};
    var scr=document.createElement('script');
    var passhref = '';
    if( location.protocol.indexOf('https') != -1 ){
        passhref = 'https://account.sogou.com/static';
    }else{
        passhref = 'http://s.account.sogoucdn.com/u';
    }
    scr.src= passhref + '/api/sogou.js?t=2014072530';
    if(undefined !==window.__sogoujsStartLoading){
        window.__sogoujsStartLoading = + new Date;
    }
    document.body.appendChild(scr);
})();
