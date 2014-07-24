/**
 * Copyright (C) 2014 yanni4night.com
 * utils.js
 *
 * changelog
 * 2014-06-23[14:31:22]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define([], function() {
    var gParams =null;
    return {
        getUrlParams: function() {
            if(gParams)return gParams;
            var params = location.search.split('#')[0].split(/[?&]/g);
            var matches;
            gParams= {};
            params.forEach(function(kv) {
                if ((matches = kv.match(/^([\w-]+)=([^&=\?#]+)/)) && matches[1] && matches[2]) {
                    gParams[matches[1]] = matches[2];
                }
            });

            return gParams;
        },
        getRu: function() {
            var params = this.getUrlParams();
            var ru = params['ru'];
            if (!/https?:\/\/([\w-]+\.)+sogou.com/.test(decodeURIComponent(ru))) {
                ru = encodeURIComponent('http://wap.sogou.com');
            }
            return ru;
        },
        getPassThroughParams:function(){
            var p = [];
            var allp = this.getUrlParams(),ru = this.getRu();
            if(allp.client_id){
                p.push('client_id='+allp.client_id);
            }
            if(allp.v){
                p.push('v='+allp.v);
            }
            if(allp.skin){
                p.push('skin='+allp.skin);
            }
            if(ru){
                p.push('ru='+ru);
            }

            return p.join('&');
        }
    };
});
