define("wap/interface",[],function(){var d=1120;var p=function(){};function e(r,q){q=q||p;return $.ajax({url:"/web/login/checkNeedCaptcha",data:{username:r,client_id:d},cache:false,dataType:"json",success:function(s){if("string"===typeof s){try{s=JSON.parse(s)}catch(t){}}if(s&&s.data&&s.data.needCaptcha){return q(true)}else{return q(false)}},error:function(){return q(false)}})}function f(q){return"/captcha?token="+q+"&_"+(+new Date())}function m(s,r){var q={client_id:d,v:0,ru:"http://wap.sogou.com"};r=r||p;$.extend(q,s);return $.ajax({url:"/wap/login",type:"post",data:q,dataType:"json",error:function(){return r(false,{statusText:"登录失败"})},success:function(t){if(t&&!+t.status){return r(true,t.data)}else{return r(false,t)}}})}function i(s,r){var q={client_id:d,v:0,ru:"http://wap.sogou.com"};r=r||p;$.extend(q,s);return $.ajax({url:"/wap/smsCode/login",type:"post",data:q,dataType:"json",error:function(){return r(false,{statusText:"登录失败"})},success:function(t){if(t&&!+t.status){return r(true,t.data)}else{return r(false,t)}}})}function n(r,q){q=q||p;return $.ajax({url:"/web/account/checkusername",data:{username:r},dataType:"json",success:function(s){if("string"===typeof s){try{s=JSON.parse(s)}catch(t){s={status:1,statusText:"格式错误"}}}if(!!s&&!+s.status){return q(true)}else{if(!!s){return q(false,s)}}}})}function l(s,r){var q={client_id:d};$.extend(q,s);r=r||p;return $.ajax({url:"/web/sendsms",data:q,type:"post",dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true)}else{return r(false,t)}},error:function(){return r(false,{statusText:"发送失败"})}})}function a(s,r){var q={client_id:d,v:5,ru:"http://wap.sogou.com"};r=r||p;$.extend(q,s);$.ajax({url:"/wap/findpwd/checksms",type:"post",data:q,dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true,t.data)}else{return r(false,t)}},error:function(){return r(false,{statusText:"提交失败"})}})}function g(s,r){var q={client_id:d};$.extend(q,s);r=r||p;return $.ajax({url:"/wap/findpwd/sendsms",data:q,type:"post",dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true)}else{return r(false,t)}},error:function(){return r(false,{statusText:"发送失败"})}})}function j(s,r){var q={client_id:d};$.extend(q,s);r=r||p;return $.ajax({url:"/wap/smsCodeLogin/sendSms",data:q,type:"post",dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true)}else{return r(false,t)}},error:function(){return r(false,{statusText:"发送失败"})}})}function h(s,r){var q={client_id:d,v:5,ru:"http://wap.sogou.com"};r=r||p;$.extend(q,s);$.ajax({url:"/wap/findpwd/check",type:"post",data:q,dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true,t.data)}else{return r(false,t)}},error:function(){return r(false,{statusText:"提交失败"})}})}function b(s,r){var q={client_id:d,v:5,ru:"http://wap.sogou.com"};r=r||p;$.extend(q,s);$.ajax({url:"/wap/findpwd/sendemail",type:"post",data:q,dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true,t.data)}else{return r(false,t)}},error:function(){return r(false,{statusText:"提交失败"})}})}function k(s,r){var q={client_id:d,v:5,ru:"http://wap.sogou.com"};r=r||p;$.extend(q,s);$.ajax({url:"/wap/findpwd/reset",type:"post",data:q,dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true,t.data)}else{return r(false,t)}},error:function(){return r(false,{statusText:"提交失败"})}})}function o(s,r){var q={client_id:d,v:0,ru:"http://wap.sogou.com"};r=r||p;$.extend(q,s);$.ajax({url:"/wap/reguser",type:"post",data:q,dataType:"json",success:function(t){if("string"===typeof t){try{t=JSON.parse(t)}catch(v){t={status:1,statusText:"格式错误"}}}if(t&&!+t.status){return r(true,t.data)}else{return r(false,t)}},error:function(){return r(false,{statusText:"注册失败"})}})}return{client_id:d,checkNeedCaptcha:e,getCaptcha:f,login:m,smsCodeLogin:i,checkusername:n,sendsms:l,checksms:a,findpwdCheck:h,findpwdSendmail:b,register:o,reset:k,findpwdSendsms:g,smsCodeLoginSendSms:j}});define("lib/tpl",[],function(){return function(i){var f=/<%(.+?)%>/mg;var g,e;var h=0;var d=[];var a;var b='with(__data){var _tpl="";';i=i.replace(/[\r\n]/mg,"");while(g=f.exec(i)){b+='_tpl+="'+(i.slice(h,g.index)).replace(/"/mg,'\\"')+'";';h=g[0].length+g.index;e=g;switch(true){case /\s*for\s+(\w+)\s+in\s+(\w+)\s*/.test(g[1]):b+="for(var _i=0,"+RegExp.$1+",loop;_i<"+RegExp.$2+".length;++_i){"+RegExp.$1+"="+RegExp.$2+"[_i];loop={index:_i+1,index0:_i,first:(_i===0),last:(_i+1==="+RegExp.$2+".length)};";break;case /\s*(endfor|endif)\s*/.test(g[1]):b+="}";break;case /\s*if\s*([\w<>=\|\^&\*\(\)!\+\-\. ]+)/.test(g[1]):a=RegExp.$1.replace(/\band\b/ig,"&&");a=a.replace(/\bor\b/ig,"||");b+="if("+a+"){";break;case new RegExp("=([\\w\\.\\[\\]\"'\\-\\| ]+)").test(g[1]):b+="_tpl+="+RegExp.$1+";";break;case /\s*else\s*/.test(g[1]):b+="}else{";break;default:console.error(g[1])}}b+='_tpl+="'+(i.slice(e.index+e[0].length)).replace(/"/mg,'\\"')+'";';b+="return _tpl;}";return new Function("__data",b)}});define("wap/local",[],function(){var a={load:function(b){try{return JSON.parse(localStorage.getItem(b))||{}}catch(d){return{}}},save:function(d,f){var b=f;if("string"!==typeof f){b=JSON.stringify(f)}try{localStorage.setItem(d,b)}catch(g){}}};return a});define("lib/emitter",[],function(){function a(){var b={};this.on=function(d,f,e){if(!b[d]){b[d]=[]}b[d].push({thisArg:e,func:f,type:d})};this.emit=function(d,e){if(Array.isArray(b[d])){b[d].forEach(function(f){f.func.call(f.thisArg||null,f,e)})}}}return a});define("wap/utils",[],function(){var a=null;return{getUrlParams:function(){if(a){return a}var d=location.search.split("#")[0].split(/[?&]/g);var b;a={};d.forEach(function(e){if((b=e.match(/^([\w-]+)=([^&]+)/))&&b[1]&&b[2]){a[b[1]]=b[2]}});return a},getRu:function(){var d=this.getUrlParams();var b=d.ru;return b},getPassThroughParams:function(){var e=[];var d=this.getUrlParams(),b=this.getRu();if(d.client_id){e.push("client_id="+d.client_id)}if(d.v){e.push("v="+d.v)}if(d.skin){e.push("skin="+d.skin)}if(b){e.push("ru="+b)}if(d.display){e.push("display="+d.display)}return e.join("&")}}});define("wap/skin",["./utils"],function(a){var b={init:function(){var d=a.getUrlParams();if(d.skin){this.loadSkin(d.skin)}return this},loadSkin:function(d){switch(true){case /caipiao|cp|red|lottery/i.test(d):d="cp";break;case /orange/i.test(d):d="orange";break;case /cyan|x1/i.test(d):d="x1";break;case /semob|se/i.test(d):d="se";break;case /shenghuo|sh|/i.test(d):d="sh";break;default:d=null}if(d){$(document.body).addClass("skin_"+d)}}};return b.init()});var hexcase=0;function hex_md5(b){return rstr2hex(rstr_md5(str2rstr_utf8(b)))}function hex_hmac_md5(e,d){return rstr2hex(rstr_hmac_md5(str2rstr_utf8(e),str2rstr_utf8(d)))}function md5_vm_test(){return hex_md5("abc").toLowerCase()=="900150983cd24fb0d6963f7d28e17f72"}function rstr_md5(b){return binl2rstr(binl_md5(rstr2binl(b),b.length*8))}function rstr_hmac_md5(n,k){var l=rstr2binl(n);if(l.length>16){l=binl_md5(l,n.length*8)}var i=Array(16),m=Array(16);for(var h=0;h<16;h++){i[h]=l[h]^909522486;m[h]=l[h]^1549556828}var j=binl_md5(i.concat(rstr2binl(k)),512+k.length*8);return binl2rstr(binl_md5(m.concat(j),512+128))}function rstr2hex(l){try{hexcase}catch(i){hexcase=0}var j=hexcase?"0123456789ABCDEF":"0123456789abcdef";var e="";var h;for(var k=0;k<l.length;k++){h=l.charCodeAt(k);e+=j.charAt((h>>>4)&15)+j.charAt(h&15)}return e}function str2rstr_utf8(j){var f="";var i=-1;var g,h;while(++i<j.length){g=j.charCodeAt(i);h=i+1<j.length?j.charCodeAt(i+1):0;if(55296<=g&&g<=56319&&56320<=h&&h<=57343){g=65536+((g&1023)<<10)+(h&1023);i++}if(g<=127){f+=String.fromCharCode(g)}else{if(g<=2047){f+=String.fromCharCode(192|((g>>>6)&31),128|(g&63))}else{if(g<=65535){f+=String.fromCharCode(224|((g>>>12)&15),128|((g>>>6)&63),128|(g&63))}else{if(g<=2097151){f+=String.fromCharCode(240|((g>>>18)&7),128|((g>>>12)&63),128|((g>>>6)&63),128|(g&63))}}}}}return f}function rstr2binl(d){var e=Array(d.length>>2);for(var f=0;f<e.length;f++){e[f]=0}for(var f=0;f<d.length*8;f+=8){e[f>>5]|=(d.charCodeAt(f/8)&255)<<(f%32)}return e}function binl2rstr(d){var e="";for(var f=0;f<d.length*32;f+=8){e+=String.fromCharCode((d[f>>5]>>>(f%32))&255)}return e}function binl_md5(a,r){a[r>>5]|=128<<((r)%32);a[(((r+64)>>>9)<<4)+14]=r;var b=1732584193;var d=-271733879;var i=-1732584194;var q=271733878;for(var v=0;v<a.length;v+=16){var s=b;var t=d;var w=i;var x=q;b=md5_ff(b,d,i,q,a[v+0],7,-680876936);q=md5_ff(q,b,d,i,a[v+1],12,-389564586);i=md5_ff(i,q,b,d,a[v+2],17,606105819);d=md5_ff(d,i,q,b,a[v+3],22,-1044525330);b=md5_ff(b,d,i,q,a[v+4],7,-176418897);q=md5_ff(q,b,d,i,a[v+5],12,1200080426);i=md5_ff(i,q,b,d,a[v+6],17,-1473231341);d=md5_ff(d,i,q,b,a[v+7],22,-45705983);b=md5_ff(b,d,i,q,a[v+8],7,1770035416);q=md5_ff(q,b,d,i,a[v+9],12,-1958414417);i=md5_ff(i,q,b,d,a[v+10],17,-42063);d=md5_ff(d,i,q,b,a[v+11],22,-1990404162);b=md5_ff(b,d,i,q,a[v+12],7,1804603682);q=md5_ff(q,b,d,i,a[v+13],12,-40341101);i=md5_ff(i,q,b,d,a[v+14],17,-1502002290);d=md5_ff(d,i,q,b,a[v+15],22,1236535329);b=md5_gg(b,d,i,q,a[v+1],5,-165796510);q=md5_gg(q,b,d,i,a[v+6],9,-1069501632);i=md5_gg(i,q,b,d,a[v+11],14,643717713);d=md5_gg(d,i,q,b,a[v+0],20,-373897302);b=md5_gg(b,d,i,q,a[v+5],5,-701558691);q=md5_gg(q,b,d,i,a[v+10],9,38016083);i=md5_gg(i,q,b,d,a[v+15],14,-660478335);d=md5_gg(d,i,q,b,a[v+4],20,-405537848);b=md5_gg(b,d,i,q,a[v+9],5,568446438);q=md5_gg(q,b,d,i,a[v+14],9,-1019803690);i=md5_gg(i,q,b,d,a[v+3],14,-187363961);d=md5_gg(d,i,q,b,a[v+8],20,1163531501);b=md5_gg(b,d,i,q,a[v+13],5,-1444681467);q=md5_gg(q,b,d,i,a[v+2],9,-51403784);i=md5_gg(i,q,b,d,a[v+7],14,1735328473);d=md5_gg(d,i,q,b,a[v+12],20,-1926607734);b=md5_hh(b,d,i,q,a[v+5],4,-378558);q=md5_hh(q,b,d,i,a[v+8],11,-2022574463);i=md5_hh(i,q,b,d,a[v+11],16,1839030562);d=md5_hh(d,i,q,b,a[v+14],23,-35309556);b=md5_hh(b,d,i,q,a[v+1],4,-1530992060);q=md5_hh(q,b,d,i,a[v+4],11,1272893353);i=md5_hh(i,q,b,d,a[v+7],16,-155497632);d=md5_hh(d,i,q,b,a[v+10],23,-1094730640);b=md5_hh(b,d,i,q,a[v+13],4,681279174);q=md5_hh(q,b,d,i,a[v+0],11,-358537222);i=md5_hh(i,q,b,d,a[v+3],16,-722521979);d=md5_hh(d,i,q,b,a[v+6],23,76029189);b=md5_hh(b,d,i,q,a[v+9],4,-640364487);q=md5_hh(q,b,d,i,a[v+12],11,-421815835);i=md5_hh(i,q,b,d,a[v+15],16,530742520);d=md5_hh(d,i,q,b,a[v+2],23,-995338651);b=md5_ii(b,d,i,q,a[v+0],6,-198630844);q=md5_ii(q,b,d,i,a[v+7],10,1126891415);i=md5_ii(i,q,b,d,a[v+14],15,-1416354905);d=md5_ii(d,i,q,b,a[v+5],21,-57434055);b=md5_ii(b,d,i,q,a[v+12],6,1700485571);q=md5_ii(q,b,d,i,a[v+3],10,-1894986606);i=md5_ii(i,q,b,d,a[v+10],15,-1051523);d=md5_ii(d,i,q,b,a[v+1],21,-2054922799);b=md5_ii(b,d,i,q,a[v+8],6,1873313359);q=md5_ii(q,b,d,i,a[v+15],10,-30611744);i=md5_ii(i,q,b,d,a[v+6],15,-1560198380);d=md5_ii(d,i,q,b,a[v+13],21,1309151649);b=md5_ii(b,d,i,q,a[v+4],6,-145523070);q=md5_ii(q,b,d,i,a[v+11],10,-1120210379);i=md5_ii(i,q,b,d,a[v+2],15,718787259);d=md5_ii(d,i,q,b,a[v+9],21,-343485551);b=safe_add(b,s);d=safe_add(d,t);i=safe_add(i,w);q=safe_add(q,x)}return Array(b,d,i,q)}function md5_cmn(a,j,k,l,b,i){return safe_add(bit_rol(safe_add(safe_add(j,a),safe_add(l,i)),b),k)}function md5_ff(m,n,a,b,o,d,l){return md5_cmn((n&a)|((~n)&b),m,n,o,d,l)}function md5_gg(m,n,a,b,o,d,l){return md5_cmn((n&b)|(a&(~b)),m,n,o,d,l)}function md5_hh(m,n,a,b,o,d,l){return md5_cmn(n^a^b,m,n,o,d,l)}function md5_ii(m,n,a,b,o,d,l){return md5_cmn(a^(n|(~b)),m,n,o,d,l)}function safe_add(f,g){var h=(f&65535)+(g&65535);var e=(f>>16)+(g>>16)+(h>>16);return(e<<16)|(h&65535)}function bit_rol(e,d){return(e<<d)|(e>>>(32-d))}define("lib/md5-min",function(){});define("wap/login",["./interface","../lib/tpl","./local","../lib/emitter","./utils","./skin","../lib/md5-min"],function(h,o,j,l,k){var m="login-history";var d=k.getRu();var a=k.getPassThroughParams();var f=k.getUrlParams(),i=/phone|tel|/i.test(f.type||""),n="http://m.account.sogou.com/wap/smsCodeLogin/index?"+a;var b={phone:/^1[1-9][0-9]{9}$/};var g={$history:$(".history"),SELECTEVENT:"select-event",init:function(){var p=this;p.listFunc=o($("#history-tpl").html());this.$history.delegate(".rm","click",function(q){q.preventDefault();var r=$(this).attr("data-id");p.removeItem(r);$(this).parent("li").remove()}).delegate(".hisname,.check","click",function(r){var q=$(this).parent("li").find(".hisname").text().trim();p.emit(p.SELECTEVENT,q)}).html(this.listFunc({list:this.listItems()}))},removeItem:function(q){var p=j.load(m);if(Array.isArray(p)&&p.length){p=p.filter(function(r){return r!=q});j.save(m,p)}},addItem:function(q){var p=j.load(m);if(!Array.isArray(p)){p=[]}if(!!~p.indexOf(q)){return}p.push(q);j.save(m,p)},listItems:function(){var p=j.load(m);if(!Array.isArray(p)){p=[]}return p}};$.extend(g,new l());var e={$form:$("form"),$captchaWrapper:$("#captcha-wrapper"),$username:$("#username"),$password:$("#password"),$showpass:$("#showpass"),$captchaImg:$("#captcha-img"),$captcha:$("#captcha"),$msg:$(".msg"),__mLogining:false,init:function(){if(i){this.$username.prev().html("手机号：");this.$password.prev().html("密&nbsp;码：");this.$username.attr("placeholder","手机号")}g.init();g.on(g.SELECTEVENT,this.onHistorySelect,this);$(".backlink").click(function(q){q.preventDefault();history.back()});$(".trd-phone").attr("href",n);$(".trd-qq").attr("href","https://account.sogou.com/connect/login?provider=qq&type=wap&display=mobile&"+a);$(".reglink").attr("href","/wap/reg?"+a);$(".forgot").attr("href","/wap/findpwd?"+a);var p;if(p=f.phone){this.$username.val(p)}return this.initEvt()},initEvt:function(){var p=this;p.$username.blur(function(q){q.target.value&&h.checkNeedCaptcha(q.target.value,function(r){p.$captchaWrapper.toggleClass("hide",!r);r&&p.$captchaImg.attr("src",h.getCaptcha(token))})});p.$captchaImg.click(function(q){$(this).attr("src",h.getCaptcha(token))});p.$showpass.change(function(q){p.$password.attr("type",!this.checked?"password":"text");if(this.checked){$(this).siblings("label").find(".circle").removeClass("grey").addClass("blue")}else{$(this).siblings("label").find(".circle").removeClass("blue").addClass("grey")}});p.$form.submit(function(s){s.preventDefault();var q=$.trim(p.$username.val());var r=$.trim(p.$password.val());var t=$.trim(p.$captcha.val());if(!q||!r){return p.showMsg(i?"请输入手机号和密码":"请输入用户名或密码")}if(i&&!b.phone.test(q)){return p.showMsg("请输入正确的手机号")}if(r.length<6){return p.showMsg("密码至少6位")}if(p.__mLogining){return false}p.__mLogining=true;return h.login({token:token,captcha:t,username:q,password:hex_md5(r)},function(w,v){p.__mLogining=false;if(w){g.addItem(q);p.showMsg("登录成功",true);d=decodeURIComponent(d);if(d){d=d.split("#")[0];if(~d.indexOf("?")){if(d[d.length-1]!=="?"){d+="&"}}else{d+="?"}location.assign(d+"sgid="+v.sgid)}}else{p.showMsg(v.statusText);if(v.status=="20221"||v.status=="20257"){p.$captcha.empty().focus();p.showCaptcha()}else{p.$password.empty().focus();if(!p.$captchaWrapper.hasClass("hide")){p.showCaptcha()}}}})});return this},onHistorySelect:function(p,q){this.$username.val(q)},showCaptcha:function(){this.$captchaWrapper.removeClass("hide");this.$captchaImg.attr("src",h.getCaptcha(token))},showMsg:function(q,r,p){if(r){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").html(q);return this}};return e});define("wap/dialog",[],function(){var d=[];var b=function(){};function a(h,g){var f=this.opt=$.extend({$container:$(".dialog"),$mask:$(".mask"),init:b,onOk:b,onBeforeOk:function(){return true}},h||{}),e=this;f.$container.on("click",".x",function(i){i.preventDefault();if("function"===typeof f.onBeforeOk&&false===f.onBeforeOk.call(e)){return}e.hide();f.onOk.call(e)});f.$mask.click(function(){e.hide()});("function"===typeof f.init)&&f.init.call(this);$.extend(this,g||{});d.push(this)}a.prototype={show:function(){var e=this;d.forEach(function(f){(this!==e)&&f.hide()});this.opt.$mask.removeClass("hide");this.opt.$container.removeClass("hide");if("function"===typeof this.opt.onShow){this.opt.onShow.call(e)}return this},hide:function(){this.opt.$mask.addClass("hide");this.opt.$container.addClass("hide");return this}};return a});define("utils",[],function(){return{uuid:function(){function s4(){return Math.floor((1+Math.random())*65536).toString(16).substring(1)}return s4()+s4()+s4()+s4()+s4()+s4()+s4()+s4()},addZero:function(num,len){num=num.toString();while(num.length<len){num="0"+num}return num},parseResponse:function(data){if(typeof data=="string"){try{data=eval("("+data+")")}catch(e){data={status:-1,statusText:"服务器故障"}}}return data},addIframe:function(url,callback){var iframe=document.createElement("iframe");iframe.src=url;iframe.style.position="absolute";iframe.style.top="1px";iframe.style.left="1px";iframe.style.width="1px";iframe.style.height="1px";if(iframe.attachEvent){iframe.attachEvent("onload",function(){callback&&callback()})}else{iframe.onload=function(){callback&&callback()}}document.body.appendChild(iframe)},getScript:function(url,callback){var script=document.createElement("script");var head=document.head;script.async=true;script.src=url;script.onload=script.onreadystatechange=function(_,isAbort){if(isAbort||!script.readyState||/loaded|complete/.test(script.readyState)){script.onload=script.onreadystatechange=null;if(script.parentNode){script.parentNode.removeChild(script)}script=null;if(!isAbort){callback()}}};head.insertBefore(script,head.firstChild)},getUrlByMail:function(mail){mail=mail.split("@")[1];if(!mail){return false}var hash={"139.com":"mail.10086.cn","gmail.com":"mail.google.com","sina.com":"mail.sina.com.cn","yeah.net":"www.yeah.net","hotmail.com":"www.hotmail.com","live.com":"www.outlook.com","live.cn":"www.outlook.com","live.com.cn":"www.outlook.com","outlook.com":"www.outlook.com","yahoo.com.cn":"mail.cn.yahoo.com","yahoo.cn":"mail.cn.yahoo.com","ymail.com":"www.ymail.com","eyou.com":"www.eyou.com","188.com":"www.188.com","foxmail.com":"www.foxmail.com"};var url;if(mail in hash){url=hash[mail]}else{url="mail."+mail}return"http://"+url}}});define("wap/common",["./utils"],function(b){var a=b.getPassThroughParams();$(".backlink").click(function(d){d.preventDefault();history.back()});$("nav a").each(function(d,f){var e=$(f).attr("href");$(f).attr("href",e.indexOf("?")==-1?(e+"?"+a):(e+"&"+a))});return{}});define("wap/smsCodeLogin",["./interface","../lib/tpl","./local","../lib/emitter","./utils","./dialog","../utils","./skin","./common"],function(b,g,h,d,f,a,e){return{init:function(){var i=f.getRu();var k;var j=new a({$container:$("#captchaDialog"),onOk:function(){l.sendSms(this.$input.val())},init:function(){var m=this;this.$captchaImg=this.opt.$container.find(".captcha-img");this.$input=this.opt.$container.find("input");this.$captchaImg.click(function(){m.refreshCaptcha()})},onBeforeOk:function(){return/^\w+$/.test(this.$input.val())},onShow:function(){this.$input.val(null)}},{refreshCaptcha:function(){this.$captchaImg.attr("src",b.getCaptcha(k=e.uuid()))}});var l={$form:$("form"),$captchaWrapper:$("#captcha-wrapper"),$username:$("#username"),$sms:$("#sms"),$captchaImg:$("#captcha-img-check"),$captcha:$("#captcha"),$sendsms:$(".sendsms"),$msg:$(".msg"),__mLogining:false,__SendingSms:false,__smsSent:false,init:function(){return this.initEvt()},initEvt:function(){var m=this;m.$username.blur(function(n){n.target.value&&b.checkNeedCaptcha(n.target.value,function(o){m.$captchaWrapper.toggleClass("hide",!o);o&&m.$captchaImg.attr("src",b.getCaptcha(k))})});m.$captchaImg.click(function(){$(this).attr("src",b.getCaptcha(k))});m.$username.on("input",function(o){var n=o.target.value;if(/[^\d]/.test(n)){return m.showMsg("请输入11位手机号")}else{m.hideMsg()}if(!/^1\d{10}$/.test(n)){return}});m.$sendsms.click(function(n){n.preventDefault();m.sendSms()});m.$form.submit(function(p){p.preventDefault();var n=$.trim(m.$username.val());var q=$.trim(m.$captcha.val());var o=$.trim(m.$sms.val());if(!n||!o){return m.showMsg("请输入用户名/手机确认码")}if(!/^1\d{10}$/.test(n)){return m.showMsg("请输入11位手机号")}if(!m.__smsSent){return m.showMsg("您还没有获取手机确认码")}if(m.__mLogining){return false}m.__mLogining=true;return b.smsCodeLogin({token:k,captcha:q,mobile:n,smsCode:o},function(s,r){m.__mLogining=false;if(s){m.showMsg("登录成功",true);i=decodeURIComponent(i);if(i){i=i.split("#")[0];if(~i.indexOf("?")){if(i[i.length-1]!=="?"){i+="&"}}else{i+="?"}location.assign(i+"sgid="+r.sgid)}}else{m.showMsg(r.statusText);if(r.status=="20221"||r.status=="20257"||r.status=="21001"||r.status=="21002"||r.status=="21003"||r.status=="21004"){m.$captcha.empty().focus();m.showCaptcha()}else{m.$sms.empty().focus();if(!m.$captchaWrapper.hasClass("hide")){m.showCaptcha()}}}})});return this},showCaptcha:function(){this.$captchaWrapper.removeClass("hide");this.$captchaImg.attr("src",b.getCaptcha(k))},showMsg:function(m,n){if(n){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(m);return this},hideMsg:function(){this.$msg.find(".circle").addClass("hide");this.$msg.find(".info").empty()},sendSms:function(o){var n=this;var m=this.$username.val();if(!/^1\d{10}$/.test(m)){return n.showMsg("请输入11位手机号")}if(this.__SendingSms){return}this.__SendingSms=true;b.smsCodeLoginSendSms({mobile:m,token:k||"",captcha:o||"",client_id:f.getUrlParams()["client_id"]},function(s,r){n.showMsg(s?"验证码发送成功":r.statusText,s);if(s){n.__smsSent=true;n.$sendsms.addClass("disabled");var q=60,p=n.$sendsms.text();var t=setInterval(function(){n.$sendsms.text(q--+"秒后可重发");if(q<0){n.$sendsms.text(p);clearInterval(t);n.__SendingSms=false;n.$sendsms.removeClass("disabled")}},1000)}else{if(20257==r.status||20221==r.status){j.show().refreshCaptcha()}n.__SendingSms=false}})}};l.init()}}});define("wap/reg",["./interface","./utils","./local","./dialog","../utils","./skin"],function(b,e,f,a,d){return{init:function(){var g=e.getRu();var l=e.getPassThroughParams();var k=new a({$container:$("#alertDialog")});var i;var h=new a({$container:$("#captchaDialog"),onOk:function(){j.sendSms(this.$input.val())},init:function(){var m=this;this.$captchaImg=this.opt.$container.find(".captcha-img");this.$input=this.opt.$container.find("input");this.$captchaImg.click(function(){m.refreshCaptcha()})},onBeforeOk:function(){return/^\w+$/.test(this.$input.val())},onShow:function(){this.$input.val(null)}},{refreshCaptcha:function(){this.$captchaImg.attr("src",b.getCaptcha(i=d.uuid()))}});var j={$form:$("form"),$username:$("#username"),$password:$("#password"),$showpass:$("#showpass"),$sms:$("#sms"),$sendsms:$(".sendsms"),$msg:$(".msg"),__SendingSms:false,__smsSent:false,__mReging:false,init:function(){$(".backlink").click(function(m){m.preventDefault();history.back()});$(".tologin").attr("href","/wap/index?"+l);return this.initEvt()},initEvt:function(){var m=this;m.$username.on("input",function(o){var n=o.target.value;if(/[^\d]/.test(n)){return m.showMsg("请输入11位手机号")}else{m.hideMsg()}if(!/^1\d{10}$/.test(n)){return}b.checkusername(n,function(p){if(!p){$(".tologin").attr("href","/wap/index?"+l+"&phone="+n);k.show()}})});m.$showpass.change(function(n){m.$password.attr("type",!this.checked?"password":"text");if(this.checked){$(this).siblings("label").find(".circle").removeClass("grey").addClass("blue")}else{$(this).siblings("label").find(".circle").removeClass("blue").addClass("grey")}});m.$sendsms.click(function(n){n.preventDefault();m.sendSms()});m.$form.submit(function(q){q.preventDefault();var n=$.trim(m.$username.val());var o=$.trim(m.$password.val());var r=$.trim(m.$sms.val());if(!n||!o||!r){return m.showMsg("请输入用户名/密码/验证码")}if(o.length<6){return m.showMsg("密码至少6位")}if(!/^1\d{10}$/.test(n)){return m.showMsg("请输入11位手机号")}if(!m.__smsSent){return m.showMsg("您还没有获取短信验证码")}if(m.__mReging){return}m.__mReging=true;return b.register({captcha:r,username:n,password:o},function(s,p){m.__mReging=false;if(s){m.showMsg("注册成功",true);g=decodeURIComponent(g);if(g){g=g.split("#")[0];if(~g.indexOf("?")){if(g[g.length-1]!=="?"){g+="&"}}else{g+="?"}location.assign(g+"sgid="+p.sgid)}}else{m.showMsg(p.statusText);if(p.status=="20221"||p.status=="20216"){m.$sms.empty().focus();h.refreshCaptcha()}else{m.$password.empty().focus()}}})});return this},sendSms:function(o){var n=this;var m=n.$username.val();if(!/^1\d{10}$/.test(m)){return n.showMsg("请输入11位手机号")}if(n.__SendingSms){return}n.__SendingSms=true;b.sendsms({mobile:m,captcha:o,token:i},function(s,r){n.showMsg(s?"验证码发送成功":r.statusText,s);if(s){n.__smsSent=true;n.$sendsms.addClass("disabled");var q=60,p=n.$sendsms.text();var t=setInterval(function(){n.$sendsms.text(q--+"秒后可重发");if(q<0){n.$sendsms.text(p);clearInterval(t);n.__SendingSms=false;n.$sendsms.removeClass("disabled")}},1000)}else{if(20257==r.status||20221==r.status){h.show().refreshCaptcha()}n.__SendingSms=false}})},showMsg:function(m,n){if(n){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(m);return this},hideMsg:function(){this.$msg.find(".circle").addClass("hide");this.$msg.find(".info").empty()}};j.init()}}});define("wap/findpwd",["./interface","./utils","./dialog","../utils","./skin","./common"],function(b,e,a,d){return{init:function(){var f=e.getRu();var j=e.getPassThroughParams();var h;var g=new a({$container:$("#captchaDialog"),onOk:function(){i.sendSms(this.$input.val())},init:function(){var k=this;this.$captchaImg=this.opt.$container.find(".captcha-img");this.$input=this.opt.$container.find("input");this.$captchaImg.click(function(){k.refreshCaptcha()})},onBeforeOk:function(){return/^\w+$/.test(this.$input.val())},onShow:function(){this.$input.val(null)}},{refreshCaptcha:function(){this.$captchaImg.attr("src",b.getCaptcha(h=d.uuid()))}});var i={$form:$("form"),$username:$("#username"),$sms:$("#sms"),$sendsms:$(".sendsms"),$msg:$(".msg"),__SendingSms:false,__smsSent:false,__mFinding:false,init:function(){$(".links a").each(function(k,m){var l=$(m).attr("href");$(m).attr("href",l.indexOf("?")==-1?(l+"?"+j):(l+"&"+j))});return this.initEvt()},initEvt:function(){var k=this;k.$username.on("input",function(m){var l=m.target.value;if(/[^\d]/.test(l)){return k.showMsg("请输入11位手机号")}else{k.hideMsg()}if(!/^1\d{10}$/.test(l)){return}});k.$sendsms.click(function(l){l.preventDefault();k.sendSms()});k.$form.submit(function(m){m.preventDefault();var l=$.trim(k.$username.val());var n=$.trim(k.$sms.val());if(!l||!n){return k.showMsg("请输入用户名/验证码")}if(!/^1\d{10}$/.test(l)){return k.showMsg("请输入11位手机号")}if(!k.__smsSent){return k.showMsg("您还没有获取短信验证码")}if(k.__mFinding){return}k.__mFinding=true;return b.checksms({smscode:n,mobile:l,skin:e.getUrlParams()["skin"],ru:decodeURIComponent(f),display:e.getUrlParams()["display"],client_id:e.getUrlParams()["client_id"]},function(p,o){k.__mFinding=false;if(p){location.href=o.url}else{k.showMsg(o.statusText);if(o.status=="20221"||o.status=="20216"){k.$sms.empty().focus();g.refreshCaptcha()}else{k.$password.empty().focus()}}})});return this},showMsg:function(k,l){if(l){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(k);return this},hideMsg:function(){this.$msg.find(".circle").addClass("hide");this.$msg.find(".info").empty()},sendSms:function(m){var l=this;var k=this.$username.val();if(!/^1\d{10}$/.test(k)){return l.showMsg("请输入11位手机号")}if(this.__SendingSms){return}this.__SendingSms=true;b.findpwdSendsms({mobile:k,token:h||"",captcha:m||"",client_id:e.getUrlParams()["client_id"]},function(q,p){l.showMsg(q?"验证码发送成功":p.statusText,q);if(q){l.__smsSent=true;l.$sendsms.addClass("disabled");var o=60,n=l.$sendsms.text();var r=setInterval(function(){l.$sendsms.text(o--+"秒后可重发");if(o<0){l.$sendsms.text(n);clearInterval(r);l.__SendingSms=false;l.$sendsms.removeClass("disabled")}},1000)}else{if(20257==p.status||20221==p.status){g.show().refreshCaptcha()}l.__SendingSms=false}})}};i.init()}}});define("wap/findpwd_other",["./interface","../lib/tpl","./utils","./skin","./common"],function(b,g,f){var a=f.getRu();var e=f.getPassThroughParams();var d={$form:$("form"),$username:$("#username"),$sms:$("#sms"),$sendsms:$(".sendsms"),$captchaImg:$("#captcha-img"),$captcha:$("#captcha"),$confirmTpl:$("#FindpwdConfirm").html(),$msg:$(".msg"),__mFinding:false,__currentStep:1,showCaptcha:function(){this.$captchaImg.attr("src",b.getCaptcha(token))},init:function(){this.showCaptcha();$(".links a").each(function(h,j){var i=$(j).attr("href");$(j).attr("href",i.indexOf("?")==-1?(i+"?"+e):(i+"&"+e))});return this.initEvt()},initEvt:function(){var h=this;h.$captchaImg.click(function(i){h.showCaptcha()});h.$form.submit(function(j){j.preventDefault();if(h.__currentStep==1){var i=$.trim(h.$username.val());var k=$.trim(h.$captcha.val());if(!i||!k){return h.showMsg("请输入用户名/验证码")}if(h.__mFinding){return}h.__mFinding=true;return b.findpwdCheck({token:token,captcha:k,username:i,ru:decodeURIComponent(a)},function(m,l){h.__mFinding=false;if(m){if(l.url&&l.url.length){location.href=l.url}else{if(l.sec_process_email){h.$form.html(g(h.$confirmTpl)({email:l.sec_process_email}));h.__confirmData=l;h.__currentStep=2}else{location.href="/wap/findpwd/customer?"+e}}}else{h.showMsg(l.statusText);h.$captcha.empty();h.showCaptcha();if(l.status=="20221"){h.$captcha.focus()}}})}else{if(h.__currentStep==2){if(h.__mFinding){return}h.__mFinding=true;return b.findpwdSendmail({username:h.__confirmData.userid,scode:h.__confirmData.scode,email:h.__confirmData.sec_email,skin:f.getUrlParams()["skin"],ru:decodeURIComponent(a)},function(m,l){h.__mFinding=false;if(m){$("form").html('<p class="reset-notify">'+(l.statusText||"找回密码邮件已发送，请尽快查看邮件并修改密码")+"</p>");setTimeout(function(){location.href=decodeURIComponent(a)},2000)}else{h.showMsg(l.statusText);h.$captcha.empty();h.showCaptcha();if(l.status=="20221"){h.$captcha.focus()}}})}}});return this},showMsg:function(h,i){if(i){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(h);return this},hideMsg:function(){this.$msg.find(".circle").addClass("hide");this.$msg.find(".info").empty()}};return d});define("wap/resetpwd",["./interface","../lib/tpl","./utils","./skin","./common"],function(b,g,f){var a=f.getRu();var e=f.getPassThroughParams();var d={$form:$("form"),__mChanging:false,init:function(){this.__data={};try{this.__data=JSON.parse(window.data)}catch(h){}if(+this.__data.status===0){this.__data=this.__data.data;this.$form.html(g(this.$form.find("script").html())({userid:this.__data.userid}));this.$password=$("#password");this.$cpassowrd=$("#cpassword");this.$msg=$(".msg");return this.initEvt()}else{this.$form.html('<p class="reset-notify">'+(this.__data.statusText||"找回密码邮件已发送，请尽快查看邮件并修改密码")+"</p>");return}},initEvt:function(){var h=this;h.$form.submit(function(j){j.preventDefault();var i=$.trim(h.$password.val());var k=$.trim(h.$cpassowrd.val());if(!i||!k){return h.showMsg("请输入密码")}if(i!=k){return h.showMsg("确认密码不一致")}if(i.length<6){return h.showMsg("密码至少6位")}if(h.__mChanging){return}h.hideMsg();h.__mChanging=true;return b.reset({password:i,username:h.__data.userid,scode:h.__data.scode,ru:decodeURIComponent(a),client_id:f.getUrlParams()["client_id"]},function(m,l){h.__mChanging=false;if(m){h.showMsg("恭喜您，重置密码成功！",true);setTimeout(function(){location.assign(decodeURIComponent(a))},2000)}else{h.showMsg(l.statusText);if(l.status=="20221"||l.status=="20216"){h.$sms.empty().focus()}else{h.$password.empty().focus()}}})});return this},showMsg:function(h,i){if(i){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(h);this.$msg.removeClass("hide");return this},hideMsg:function(){this.$msg.find(".circle").addClass("hide");this.$msg.find(".info").empty();this.$msg.addClass("hide")}};return d});define("wap/findpwd_confirm",["./interface","./utils","./skin","./common"],function(b,f){var a=f.getRu();var e=f.getPassThroughParams();var d={$form:$("form"),__mFinding:false,init:function(){return this.initEvt()},initEvt:function(){var g=this;g.$form.submit(function(h){h.preventDefault();return b.check({captcha:c,username:u},function(j,i){g.__mFinding=false;if(j){g.showMsg("注册成功",true);a=decodeURIComponent(a);if(a){a=a.split("#")[0];if(~a.indexOf("?")){if(a[a.length-1]!=="?"){a+="&"}}else{a+="?"}location.assign(a+"sgid="+i.sgid)}}else{g.showMsg(i.statusText);if(i.status=="20221"||i.status=="20216"){g.$sms.empty().focus()}else{g.$password.empty().focus()}}})});return this},showMsg:function(g,h){if(h){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(g);return this},hideMsg:function(){this.$msg.find(".circle").addClass("hide");this.$msg.find(".info").empty()}};return d});define("wap",["./wap/login","./wap/smsCodeLogin","./wap/reg","./wap/findpwd","./wap/findpwd_other","./wap/resetpwd","./wap/findpwd_confirm"],function(h,g,b,a,e,f,d){return{index_touch:function(){h.init()},index_smscode_login_touch:function(){g.init()},regist_touch:function(){b.init()},findpwd_touch:function(){a.init()},findpwd_other_touch:function(){e.init()},findpwd_confirm_touch:function(){d.init()},resetpwd_touch:function(){f.init()}}});