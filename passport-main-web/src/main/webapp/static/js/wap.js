define("wap/interface",[],function(){var h=1024;var e=function(){};function c(j,i){i=i||e;return $.ajax({url:"/web/login/checkNeedCaptcha",data:{username:j,client_id:h},cache:false,dataType:"json",success:function(k){if("string"===typeof k){try{k=JSON.parse(k)}catch(l){}}if(k&&k.data&&k.data.needCaptcha){return i(true)}else{return i(false)}},error:function(){return i(false)}})}function d(i){return"/captcha?token="+i+"&_"+(+new Date())}function b(k,j){var i={client_id:h,v:0,ru:"http://wap.sogou.com"};j=j||e;$.extend(i,k);return $.ajax({url:"/wap/login",type:"post",data:i,dataType:"json",error:function(){return j(false,{statusText:"登录失败"})},success:function(l){if(l&&!+l.status){return j(true,l.data)}else{return j(false,l)}}})}function g(j,i){i=i||e;return $.ajax({url:"/web/account/checkusername",data:{username:j},dataType:"json",success:function(k){if("string"===typeof k){try{k=JSON.parse(k)}catch(l){k={status:1,statusText:"格式错误"}}}if(!!k&&!+k.status){return i(true)}else{if(!!k){return i(false,k)}}}})}function f(i,j){j=j||e;return $.ajax({url:"/web/sendsms",data:{client_id:h,mobile:i},dataType:"json",success:function(k){if("string"===typeof k){try{k=JSON.parse(k)}catch(l){k={status:1,statusText:"格式错误"}}}if(k&&!+k.status){return j(true)}else{return j(false,k)}},error:function(){return j(false,{statusText:"发送失败"})}})}function a(k,j){var i={client_id:h,v:0,ru:"http://wap.sogou.com"};j=j||e;$.extend(i,k);$.ajax({url:"/wap/reguser",type:"post",data:i,dataType:"json",success:function(l){if("string"===typeof l){try{l=JSON.parse(l)}catch(m){l={status:1,statusText:"格式错误"}}}if(l&&!+l.status){return j(true,l.data)}else{return j(false,l)}},error:function(){return j(false,{statusText:"注册失败"})}})}return{client_id:h,checkNeedCaptcha:c,getCaptcha:d,login:b,checkusername:g,sendsms:f,register:a}});define("lib/tpl",[],function(){return function(h){var e=/<%(.+?)%>/mg;var f,d;var g=0;var c=[];var a;var b='with(__data){var _tpl="";';h=h.replace(/[\r\n]/mg,"");while(f=e.exec(h)){b+='_tpl+="'+(h.slice(g,f.index)).replace(/"/mg,'\\"')+'";';g=f[0].length+f.index;d=f;switch(true){case /\s*for\s+(\w+)\s+in\s+(\w+)\s*/.test(f[1]):b+="for(var _i=0,"+RegExp.$1+",loop;_i<"+RegExp.$2+".length;++_i){"+RegExp.$1+"="+RegExp.$2+"[_i];loop={index:_i+1,index0:_i,first:(_i===0),last:(_i+1==="+RegExp.$2+".length)};";break;case /\s*(endfor|endif)\s*/.test(f[1]):b+="}";break;case /\s*if\s*([\w<>=\|\^&\*\(\)!\+\-\. ]+)/.test(f[1]):a=RegExp.$1.replace(/\band\b/ig,"&&");a=a.replace(/\bor\b/ig,"||");b+="if("+a+"){";break;case new RegExp("=([\\w\\.\\[\\]\"'\\-\\| ]+)").test(f[1]):b+="_tpl+="+RegExp.$1+";";break;case /\s*else\s*/.test(f[1]):b+="}else{";break;default:console.error(f[1])}}b+='_tpl+="'+(h.slice(d.index+d[0].length)).replace(/"/mg,'\\"')+'";';b+="return _tpl;}";return new Function("__data",b)}});define("wap/local",[],function(){var a={load:function(b){try{return JSON.parse(localStorage.getItem(b))||{}}catch(c){return{}}},save:function(c,d){var b=d;if("string"!==typeof d){b=JSON.stringify(d)}localStorage.setItem(c,b)}};return a});define("lib/emitter",[],function(){function a(){var b={};this.on=function(c,e,d){if(!b[c]){b[c]=[]}b[c].push({thisArg:d,func:e,type:c})};this.emit=function(c,d){if(Array.isArray(b[c])){b[c].forEach(function(e){e.func.call(e.thisArg||null,e,d)})}}}return a});define("wap/utils",[],function(){var a=null;return{getUrlParams:function(){if(a){return a}var c=location.search.split("#")[0].split(/[?&]/g);var b;a={};c.forEach(function(d){if((b=d.match(/^([\w-]+)=([^&=\?#]+)/))&&b[1]&&b[2]){a[b[1]]=b[2]}});return a},getRu:function(){var b=this.getUrlParams();ru=b.ru;if(!/https?:\/\/([\w-]+\.)+sogou.com/.test(decodeURIComponent(ru))){ru=encodeURIComponent("http://wap.sogou.com")}return ru},getPassThroughParams:function(){var d=[];var c=this.getUrlParams(),b=this.getRu();if(c.client_id){d.push("client_id="+c.client_id)}if(c.v){d.push("v="+c.v)}if(c.skin){d.push("skin="+c.skin)}if(b){d.push("ru="+b)}return d.join("&")}}});define("wap/skin",["./utils"],function(a){var b={init:function(){var c=a.getUrlParams();if(c.skin){this.loadSkin(c.skin)}return this},loadSkin:function(c){switch(true){case /caipiao|cp|red|lottery/i.test(c):c="cp";break;default:c=null}if(c){$(document.body).addClass("skin_"+c)}}};return b.init()});var hexcase=0;function hex_md5(b){return rstr2hex(rstr_md5(str2rstr_utf8(b)))}function hex_hmac_md5(d,c){return rstr2hex(rstr_hmac_md5(str2rstr_utf8(d),str2rstr_utf8(c)))}function md5_vm_test(){return hex_md5("abc").toLowerCase()=="900150983cd24fb0d6963f7d28e17f72"}function rstr_md5(b){return binl2rstr(binl_md5(rstr2binl(b),b.length*8))}function rstr_hmac_md5(n,k){var l=rstr2binl(n);if(l.length>16){l=binl_md5(l,n.length*8)}var i=Array(16),m=Array(16);for(var h=0;h<16;h++){i[h]=l[h]^909522486;m[h]=l[h]^1549556828}var j=binl_md5(i.concat(rstr2binl(k)),512+k.length*8);return binl2rstr(binl_md5(m.concat(j),512+128))}function rstr2hex(l){try{hexcase}catch(i){hexcase=0}var j=hexcase?"0123456789ABCDEF":"0123456789abcdef";var e="";var h;for(var k=0;k<l.length;k++){h=l.charCodeAt(k);e+=j.charAt((h>>>4)&15)+j.charAt(h&15)}return e}function str2rstr_utf8(j){var f="";var i=-1;var g,h;while(++i<j.length){g=j.charCodeAt(i);h=i+1<j.length?j.charCodeAt(i+1):0;if(55296<=g&&g<=56319&&56320<=h&&h<=57343){g=65536+((g&1023)<<10)+(h&1023);i++}if(g<=127){f+=String.fromCharCode(g)}else{if(g<=2047){f+=String.fromCharCode(192|((g>>>6)&31),128|(g&63))}else{if(g<=65535){f+=String.fromCharCode(224|((g>>>12)&15),128|((g>>>6)&63),128|(g&63))}else{if(g<=2097151){f+=String.fromCharCode(240|((g>>>18)&7),128|((g>>>12)&63),128|((g>>>6)&63),128|(g&63))}}}}}return f}function rstr2binl(d){var e=Array(d.length>>2);for(var f=0;f<e.length;f++){e[f]=0}for(var f=0;f<d.length*8;f+=8){e[f>>5]|=(d.charCodeAt(f/8)&255)<<(f%32)}return e}function binl2rstr(d){var e="";for(var f=0;f<d.length*32;f+=8){e+=String.fromCharCode((d[f>>5]>>>(f%32))&255)}return e}function binl_md5(a,q){a[q>>5]|=128<<((q)%32);a[(((q+64)>>>9)<<4)+14]=q;var b=1732584193;var c=-271733879;var d=-1732584194;var i=271733878;for(var t=0;t<a.length;t+=16){var r=b;var s=c;var u=d;var v=i;b=md5_ff(b,c,d,i,a[t+0],7,-680876936);i=md5_ff(i,b,c,d,a[t+1],12,-389564586);d=md5_ff(d,i,b,c,a[t+2],17,606105819);c=md5_ff(c,d,i,b,a[t+3],22,-1044525330);b=md5_ff(b,c,d,i,a[t+4],7,-176418897);i=md5_ff(i,b,c,d,a[t+5],12,1200080426);d=md5_ff(d,i,b,c,a[t+6],17,-1473231341);c=md5_ff(c,d,i,b,a[t+7],22,-45705983);b=md5_ff(b,c,d,i,a[t+8],7,1770035416);i=md5_ff(i,b,c,d,a[t+9],12,-1958414417);d=md5_ff(d,i,b,c,a[t+10],17,-42063);c=md5_ff(c,d,i,b,a[t+11],22,-1990404162);b=md5_ff(b,c,d,i,a[t+12],7,1804603682);i=md5_ff(i,b,c,d,a[t+13],12,-40341101);d=md5_ff(d,i,b,c,a[t+14],17,-1502002290);c=md5_ff(c,d,i,b,a[t+15],22,1236535329);b=md5_gg(b,c,d,i,a[t+1],5,-165796510);i=md5_gg(i,b,c,d,a[t+6],9,-1069501632);d=md5_gg(d,i,b,c,a[t+11],14,643717713);c=md5_gg(c,d,i,b,a[t+0],20,-373897302);b=md5_gg(b,c,d,i,a[t+5],5,-701558691);i=md5_gg(i,b,c,d,a[t+10],9,38016083);d=md5_gg(d,i,b,c,a[t+15],14,-660478335);c=md5_gg(c,d,i,b,a[t+4],20,-405537848);b=md5_gg(b,c,d,i,a[t+9],5,568446438);i=md5_gg(i,b,c,d,a[t+14],9,-1019803690);d=md5_gg(d,i,b,c,a[t+3],14,-187363961);c=md5_gg(c,d,i,b,a[t+8],20,1163531501);b=md5_gg(b,c,d,i,a[t+13],5,-1444681467);i=md5_gg(i,b,c,d,a[t+2],9,-51403784);d=md5_gg(d,i,b,c,a[t+7],14,1735328473);c=md5_gg(c,d,i,b,a[t+12],20,-1926607734);b=md5_hh(b,c,d,i,a[t+5],4,-378558);i=md5_hh(i,b,c,d,a[t+8],11,-2022574463);d=md5_hh(d,i,b,c,a[t+11],16,1839030562);c=md5_hh(c,d,i,b,a[t+14],23,-35309556);b=md5_hh(b,c,d,i,a[t+1],4,-1530992060);i=md5_hh(i,b,c,d,a[t+4],11,1272893353);d=md5_hh(d,i,b,c,a[t+7],16,-155497632);c=md5_hh(c,d,i,b,a[t+10],23,-1094730640);b=md5_hh(b,c,d,i,a[t+13],4,681279174);i=md5_hh(i,b,c,d,a[t+0],11,-358537222);d=md5_hh(d,i,b,c,a[t+3],16,-722521979);c=md5_hh(c,d,i,b,a[t+6],23,76029189);b=md5_hh(b,c,d,i,a[t+9],4,-640364487);i=md5_hh(i,b,c,d,a[t+12],11,-421815835);d=md5_hh(d,i,b,c,a[t+15],16,530742520);c=md5_hh(c,d,i,b,a[t+2],23,-995338651);b=md5_ii(b,c,d,i,a[t+0],6,-198630844);i=md5_ii(i,b,c,d,a[t+7],10,1126891415);d=md5_ii(d,i,b,c,a[t+14],15,-1416354905);c=md5_ii(c,d,i,b,a[t+5],21,-57434055);b=md5_ii(b,c,d,i,a[t+12],6,1700485571);i=md5_ii(i,b,c,d,a[t+3],10,-1894986606);d=md5_ii(d,i,b,c,a[t+10],15,-1051523);c=md5_ii(c,d,i,b,a[t+1],21,-2054922799);b=md5_ii(b,c,d,i,a[t+8],6,1873313359);i=md5_ii(i,b,c,d,a[t+15],10,-30611744);d=md5_ii(d,i,b,c,a[t+6],15,-1560198380);c=md5_ii(c,d,i,b,a[t+13],21,1309151649);b=md5_ii(b,c,d,i,a[t+4],6,-145523070);i=md5_ii(i,b,c,d,a[t+11],10,-1120210379);d=md5_ii(d,i,b,c,a[t+2],15,718787259);c=md5_ii(c,d,i,b,a[t+9],21,-343485551);b=safe_add(b,r);c=safe_add(c,s);d=safe_add(d,u);i=safe_add(i,v)}return Array(b,c,d,i)}function md5_cmn(a,j,k,l,b,i){return safe_add(bit_rol(safe_add(safe_add(j,a),safe_add(l,i)),b),k)}function md5_ff(l,m,a,b,n,c,d){return md5_cmn((m&a)|((~m)&b),l,m,n,c,d)}function md5_gg(l,m,a,b,n,c,d){return md5_cmn((m&b)|(a&(~b)),l,m,n,c,d)}function md5_hh(l,m,a,b,n,c,d){return md5_cmn(m^a^b,l,m,n,c,d)}function md5_ii(l,m,a,b,n,c,d){return md5_cmn(a^(m|(~b)),l,m,n,c,d)}function safe_add(f,g){var h=(f&65535)+(g&65535);var e=(f>>16)+(g>>16)+(h>>16);return(e<<16)|(h&65535)}function bit_rol(d,c){return(d<<c)|(d>>>(32-c))}define("lib/md5-min",function(){});define("wap/login",["./interface","../lib/tpl","./local","../lib/emitter","./utils","./skin","../lib/md5-min"],function(e,j,f,h,g){var i="login-history";var b=g.getRu();var a=g.getPassThroughParams();var d={$history:$(".history"),SELECTEVENT:"select-event",init:function(){var k=this;k.listFunc=j($("#history-tpl").html());this.$history.delegate(".rm","click",function(l){l.preventDefault();var m=$(this).attr("data-id");k.removeItem(m);$(this).parent("li").remove()}).delegate(".hisname,.check","click",function(m){var l=$(this).parent("li").find(".hisname").text().trim();k.emit(k.SELECTEVENT,l)}).html(this.listFunc({list:this.listItems()}))},removeItem:function(l){var k=f.load(i);if(Array.isArray(k)&&k.length){k=k.filter(function(m){return m!=l});f.save(i,k)}},addItem:function(l){var k=f.load(i);if(!Array.isArray(k)){k=[]}if(!!~k.indexOf(l)){return}k.push(l);f.save(i,k)},listItems:function(){var k=f.load(i);if(!Array.isArray(k)){k=[]}return k}};$.extend(d,new h());var c={$form:$("form"),$captchaWrapper:$("#captcha-wrapper"),$username:$("#username"),$password:$("#password"),$showpass:$("#showpass"),$captchaImg:$("#captcha-img"),$captcha:$("#captcha"),$msg:$(".msg"),__mLogining:false,init:function(){d.init();d.on(d.SELECTEVENT,this.onHistorySelect,this);$(".backlink").click(function(l){l.preventDefault();history.back()});$(".trd-qq").attr("href","https://account.sogou.com/connect/login?provider=qq&type=wap&display=mobile&"+a);$(".reglink").attr("href","/wap/reg?"+a);$(".forgot").attr("href","/wap/findpwd?"+a);var k;if(k=g.getUrlParams().phone){this.$username.val(k)}return this.initEvt()},initEvt:function(){var k=this;k.$username.blur(function(l){l.target.value&&e.checkNeedCaptcha(l.target.value,function(m){k.$captchaWrapper.toggleClass("hide",!m);m&&k.$captchaImg.attr("src",e.getCaptcha(token))})});k.$captchaImg.click(function(l){$(this).attr("src",e.getCaptcha(token))});k.$showpass.change(function(l){k.$password.attr("type",!this.checked?"password":"text");if(this.checked){$(this).siblings("label").find(".circle").removeClass("grey").addClass("blue")}else{$(this).siblings("label").find(".circle").removeClass("blue").addClass("grey")}});k.$form.submit(function(n){n.preventDefault();var l=$.trim(k.$username.val());var m=$.trim(k.$password.val());var o=$.trim(k.$captcha.val());if(!l||!m){return k.showMsg("请输入用户名或密码")}if(m.length<6){return k.showMsg("密码至少6位")}if(k.__mLogining){return false}k.__mLogining=true;return e.login({token:token,captcha:o,username:l,password:hex_md5(m)},function(q,p){k.__mLogining=false;if(q){d.addItem(l);k.showMsg("登录成功",true);b=decodeURIComponent(b);if(b){b=b.split("#")[0];if(~b.indexOf("?")){if(b[b.length-1]!=="?"){b+="&"}}else{b+="?"}location.assign(b+"sgid="+p.sgid)}}else{k.showMsg(p.statusText);if(p.status=="20221"||p.status=="20257"){k.$captcha.empty().focus();k.showCaptcha()}else{k.$password.empty().focus();if(!k.$captchaWrapper.hasClass("hide")){k.showCaptcha()}}}})});return this},onHistorySelect:function(k,l){this.$username.val(l)},showCaptcha:function(){this.$captchaWrapper.removeClass("hide");this.$captchaImg.attr("src",e.getCaptcha(token))},showMsg:function(k,l){if(l){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(k);return this}};return c});define("wap/reg",["./interface","./utils","./local","./skin"],function(c,f,g){var a=f.getRu();var e=f.getPassThroughParams();var b={$container:$(".dialog"),$mask:$(".mask"),init:function(){var h=this;h.$container.on("click",".x",function(i){i.preventDefault();h.hide()});h.$mask.click(function(){h.hide()})},show:function(){this.$mask.removeClass("hide");this.$container.removeClass("hide")},hide:function(){this.$mask.addClass("hide");this.$container.addClass("hide")}};var d={$form:$("form"),$username:$("#username"),$password:$("#password"),$showpass:$("#showpass"),$sms:$("#sms"),$sendsms:$(".sendsms"),$msg:$(".msg"),__SendingSms:false,__smsSent:false,__mReging:false,init:function(){$(".backlink").click(function(h){h.preventDefault();history.back()});$(".tologin").attr("href","/wap/index?"+e);return this.initEvt()},initEvt:function(){var h=this;h.$username.on("input",function(j){var i=j.target.value;if(/[^\d]/.test(i)){return h.showMsg("请输入11位手机号")}else{h.hideMsg()}if(!/^1\d{10}$/.test(i)){return}c.checkusername(i,function(k){if(!k){$(".tologin").attr("href","/wap/index?"+e+"&phone="+i);b.show()}})});h.$showpass.change(function(i){h.$password.attr("type",!this.checked?"password":"text");if(this.checked){$(this).siblings("label").find(".circle").removeClass("grey").addClass("blue")}else{$(this).siblings("label").find(".circle").removeClass("blue").addClass("grey")}});h.$sendsms.click(function(j){j.preventDefault();var i=h.$username.val();if(!/^1\d{10}$/.test(i)){return h.showMsg("请输入11位手机号")}if(h.__SendingSms){return}h.__SendingSms=true;c.sendsms(i,function(n,m){h.showMsg(n?"验证码发送成功":m.statusText,n);if(n){h.__smsSent=true;h.$sendsms.addClass("disabled");var l=60,k=h.$sendsms.text();var o=setInterval(function(){h.$sendsms.text(l--+"秒后可重发");if(l<0){h.$sendsms.text(k);clearInterval(o);h.__SendingSms=false;h.$sendsms.removeClass("disabled")}},1000)}else{h.__SendingSms=false}})});h.$form.submit(function(k){k.preventDefault();var i=$.trim(h.$username.val());var j=$.trim(h.$password.val());var l=$.trim(h.$sms.val());if(!i||!j||!l){return h.showMsg("请输入用户名/密码/验证码")}if(j.length<6){return h.showMsg("密码至少6位")}if(!/^1\d{10}$/.test(i)){return h.showMsg("请输入11位手机号")}if(!h.__smsSent){return h.showMsg("您还没有获取短信验证码")}if(h.__mReging){return}h.__mReging=true;return c.register({captcha:l,username:i,password:j},function(n,m){h.__mReging=false;if(n){h.showMsg("注册成功",true);a=decodeURIComponent(a);if(a){a=a.split("#")[0];if(~a.indexOf("?")){if(a[a.length-1]!=="?"){a+="&"}}else{a+="?"}location.assign(a+"sgid="+m.sgid)}}else{h.showMsg(m.statusText);if(m.status=="20221"||m.status=="20216"){h.$sms.empty().focus()}else{h.$password.empty().focus()}}})});return this},showMsg:function(h,i){if(i){this.$msg.find(".circle").removeClass("hide red").addClass("green");this.$msg.find(".circle .sprite").removeClass("sprite-wrong").addClass("sprite-right")}else{this.$msg.find(".circle").removeClass("hide green").addClass("red");this.$msg.find(".circle .sprite").removeClass("sprite-right").addClass("sprite-wrong")}this.$msg.find(".info").text(h);return this},hideMsg:function(){this.$msg.find(".circle").addClass("hide");this.$msg.find(".info").empty()}};return{init:function(){b.init();d.init()}}});define("wap/findpwd",["./skin"],function(){return{init:function(){$(".backlink").click(function(a){a.preventDefault();history.back()})}}});define("wap",["./wap/login","./wap/reg","./wap/findpwd"],function(c,b,a){return{index_touch:function(){c.init()},regist_touch:function(){b.init()},findpwd_touch:function(){a.init()}}});