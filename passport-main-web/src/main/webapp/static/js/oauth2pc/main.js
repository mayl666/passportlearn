require.config({
	//baseUrl: 'http://s5.suc.itc.cn/ux_sogou_member/js/',
	baseUrl: '//s5.suc.itc.cn/ux_sogou_member/' + (window.splus && splus._timestamp? 'v' + splus._timestamp + '/': '') + 'js/',
	paths: {
		almond: 'lib/almond/js/almond'
	}
});

/**
 * Application
 */
require(['jquery', 'lib/es5-shim', 'lib/json2', 'lib/placeholder', 'almond'], function($){
    // global
    $(function(){

    });
});

