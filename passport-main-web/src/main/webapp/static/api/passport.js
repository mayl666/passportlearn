(function(){ 
    var passhref = '';
    if( location.protocol.indexOf('https') != -1 ){
        passhref = 'https://account.sogou.com/static';
    }else{
        passhref = '';
    }

document.write('<script type="text/javascript" src="https://account.sogou.com/static/api/sogou.js?t=2013081225"></script>');
})();
