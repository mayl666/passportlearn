(function(){ 
    window.PassportSC=window.PassportSC||{};
    var scr=document.createElement('script');
    var passhref = '';
    if( location.protocol.indexOf('https') != -1 ){
        passhref = 'https://account.sogou.com/static';
    }else{
        passhref = 'http://s.account.sogoucdn.com/u';
    }
<<<<<<< HEAD
    scr.src= passhref + '/api/sogou.js?t=2014121016';
=======

    scr.src= passhref + '/api/sogou.js?t=2014121018';
>>>>>>> aa73c84bc1b4dd4824579f066c45479bacc3e299
    if(undefined !==window.__sogoujsStartLoading){
        window.__sogoujsStartLoading = + new Date;
    }
    document.body.appendChild(scr);
})();
