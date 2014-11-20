/*require.config({
	//baseUrl: 'http://s5.suc.itc.cn/ux_sogou_member/js/',
	baseUrl: 'static/js/oauth2pc/',
	paths: {
		almond: 'lib/almond/js/almond'
	}
});*/

/**
 * Application
 */
require(['lib/es5-shim', 'lib/json2', 'lib/placeholder'/*, 'almond'*/], function(){
    // global
    $(function(){
        var ava=$('#avatar');
        !ava.attr('src')&&ava.attr('src',ava.attr('default-src'));
    });
});

