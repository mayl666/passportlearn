define("utils",[],function(){return{uuid:function(){function s4(){return Math.floor((1+Math.random())*65536).toString(16).substring(1)}return s4()+s4()+s4()+s4()+s4()+s4()+s4()+s4()},addZero:function(num,len){num=num.toString();while(num.length<len){num="0"+num}return num},parseResponse:function(data){if(typeof data=="string"){try{data=eval("("+data+")")}catch(e){data={status:-1,statusText:"服务器故障"}}}return data},addIframe:function(url,callback){var iframe=document.createElement("iframe");iframe.src=url;iframe.style.position="absolute";iframe.style.top="1px";iframe.style.left="1px";iframe.style.width="1px";iframe.style.height="1px";if(iframe.attachEvent){iframe.attachEvent("onload",function(){callback&&callback()})}else{iframe.onload=function(){callback&&callback()}}document.body.appendChild(iframe)},getScript:function(url,callback){var script=document.createElement("script");var head=document.head;script.async=true;script.src=url;script.onload=script.onreadystatechange=function(_,isAbort){if(isAbort||!script.readyState||/loaded|complete/.test(script.readyState)){script.onload=script.onreadystatechange=null;if(script.parentNode){script.parentNode.removeChild(script)}script=null;if(!isAbort){callback()}}};head.insertBefore(script,head.firstChild)},getUrlByMail:function(mail){mail=mail.split("@")[1];if(!mail){return false}var hash={"139.com":"mail.10086.cn","gmail.com":"mail.google.com","sina.com":"mail.sina.com.cn","yeah.net":"www.yeah.net","hotmail.com":"www.hotmail.com","live.com":"www.outlook.com","live.cn":"www.outlook.com","live.com.cn":"www.outlook.com","outlook.com":"www.outlook.com","yahoo.com.cn":"mail.cn.yahoo.com","yahoo.cn":"mail.cn.yahoo.com","ymail.com":"www.ymail.com","eyou.com":"www.eyou.com","188.com":"www.188.com","foxmail.com":"www.foxmail.com"};var url;if(mail in hash){url=hash[mail]}else{url="mail."+mail}return"http://"+url}}});define("common",["./utils"],function(a){return{showBannerUnderLine:function(){$(".banner ul").show();var b=$(".banner ul li.current");if(b.length){$(".banner .underline").css("left",b.position().left).css("width",b.css("width"))}},parseHeader:function(b){$("#Header .username").html(b.username);if(b.username){$("#Header .info").show();$("#Header .logout a").click(function(c){a.addIframe($(this).attr("href"),function(){location.reload()});return false})}},bindJumpEmail:function(){$("#JumpToUrl").click(function(){if($("#JumpTarget")){window.open(a.getUrlByMail($("#JumpTarget").html()))}return false})}}});var __ssjs__=typeof exports=="undefined"?false:true;if(__ssjs__){var Ursa={varType:{},escapeType:{}}}(function(){if(!__ssjs__){if(typeof Ursa!="undefined"&&typeof Ursa.render!="undefined"){return}window.Ursa=window.Ursa||{varType:{},escapeType:{}}}var config={starter:"{",ender:"}",commentStarter:"#",commentEnder:"#",opStarter:"{",opEnder:"}",statementStarter:"%",statementEnder:"%"},starter=config.starter,ender=config.ender,commentStarter=config.commentStarter,commentEnder=config.commentEnder,opStarter=config.opStarter,opEnder=config.opEnder,statementStarter=config.statementStarter,statementEnder=config.statementEnder,endStartReg=new RegExp("["+opEnder+commentEnder+statementEnder+"]","g");function setConfig(conf){for(var i in conf){if(config[i]){config[i]=conf[i]}}starter=config.starter,ender=config.ender,commentStarter=config.commentStarter,commentEnder=config.commentEnder,opStarter=config.opStarter,opEnder=config.opEnder,statementStarter=config.statementStarter,statementEnder=config.statementEnder}function range(start,end,size){var res=[],size=size||1;if(start<=end){while(start<end){res.push(start);start+=size*1}}else{while(start>end){res.push(start);start=start-size}}return res}function each(rge,callback){if(rge instanceof Array){for(var i=0,len=rge.length;i<len;i++){callback&&callback(rge[i],i,i)}}else{if(rge instanceof Object){var index=0;for(var key in rge){if(typeof rge[key]!="function"){callback&&callback(rge[key],key,index);index++}}}}}function dumpError(code,tplString,pointer,matches){var msg;switch(code){case 1:msg="错误的使用了\\，行数:"+getLineNumber(tplString,pointer);break;case 2:msg='缺少结束符}"，行数:'+getLineNumber(tplString,pointer);break;case 3:msg='缺少"{","#"或者"%"，行数:'+getLineNumber(tplString,pointer);break;case 4:msg="未闭合的{，,行数:"+getLineNumber(tplString,pointer);break;case 5:msg="以下标签未闭合"+matches.join(",");break;case 6:msg="创建模板失败"+tplString;break;case 7:msg='缺少"'+matches.replace("end","")+",行数:"+getLineNumber(tplString,pointer);break;case 8:msg="缺少结束符}"+tplString;break;default:msg="出错了";break}throw new Error(msg)}var __undefinded;function cleanWhiteSpace(result){result=result.replace(/\t/g,"    ");result=result.replace(/\r\n/g,"\n");result=result.replace(/\r/g,"\n");result=result.replace(/^(\s*\S*(\s+\S+)*)\s*$/,"$1");return result}function _length(rge){if(!rge){return 0}if(rge instanceof Array){return rge.length}var length=0;each(rge,function(item,i,index){length=index+1});return length}function _jsIn(key,rge){if(!key||!rge){return false}if(rge instanceof Array){for(var i=0,len=rge.length;i<len;i++){if(key==rge[i]){return true}}}try{return rge.match(key)?true:false}catch(e){return false}}function _jsIs(vars,type,args3,args4){switch(type){case"odd":return vars%2==1;break;case"even":return vars%2==0;break;case"divisibleby":return vars%args3==0;break;case"defined":return typeof vars!="undefined";break;default:if(Ursa.varType&&Ursa.varType[type]){return Ursa.varType[type].apply(null,arguments)}else{return false}}}function _trim(str){return str?(str+"").replace(/(^\s*)|(\s*$)/g,""):""}function _default(vars){return vars}function _abs(vars){return Math.abs(vars)}function _format(vars){if(!vars){return""}var placeHolder=vars.split(/%s/g);var str="",arg=arguments;each(placeHolder,function(item,key,i){str+=item+(arg[i+1]?arg[i+1]:"")});return str}function _join(vars,div){if(!vars){return""}if(vars instanceof Array){return vars.join(typeof div!="undefined"?div:",")}return vars}function _replace(str,replacer){if(!str){return""}var str=str;each(replacer,function(value,key){str=str.replace(new RegExp(key,"g"),value)});return str}function _slice(arr,start,length){if(arr&&arr.slice){return arr.slice(start,start+length)}else{return arr}}function _sort(arr){if(arr&&arr.sort){arr.sort(function(a,b){return a-b})}return arr}function _escape(str,type){if(typeof str=="undefined"||str==null){return""}if(str&&(str.safe==1)){return str.str}var str=str.toString();if(type=="js"){return str.replace(/\'/g,"\\'").replace(/\"/g,'\\"')}if(type=="none"){return{str:str,safe:1}}if(Ursa.escapeType&&Ursa.escapeType[type]){return Ursa.escapeType[type](str)}return str.replace(/<|>/g,function(m){if(m=="<"){return"&lt;"}return"&gt;"})}function _raw(str){return{safe:1,str:str}}function _truncate(str,len,killwords,end){if(typeof str=="undefined"){return""}var str=new String(str);var killwords=killwords||false;var end=typeof end=="undefined"?"...":"";if(killwords){return(typeof len=="undefined"?str.substr(0,str.length):str.substr(0,len)+(str.length<=len?"":end))}return end}function _substring(str,start,end){if(typeof str=="undefined"){return""}var str=new String(str);var end=typeof end!="undefined"?end:str.length;return str.substring(start,end)}function _upper(str){if(typeof str=="undefined"){return""}return new String(str).toUpperCase()}function _lower(str){if(typeof str=="undefined"){return""}return new String(str).toLowerCase()}Ursa._tpl={};Ursa.render=function(tplName,data,tplString){if(!Ursa._tpl[tplName]){Ursa.compile(tplString,tplName)}return Ursa._tpl[tplName](data)};Ursa.compile=function(tplString,tplName){var str=SyntaxGetter(tplString);try{eval('Ursa._tpl["'+tplName+'"] = '+str)}catch(e){dumpError(6,e)}return Ursa._tpl[tplName]};var tags="^(for|endfor|if|elif|else|endif|set)";var tagsReplacer={"for":{validate:/for[\s]+[^\s]+\sin[\s]+[\S]+/g,pfixFunc:function(obj){var statement=obj.statement,args=statement.split(/[\s]+in[\s]+/g)[0],_args,_value=_args,_key=args,context=statement.replace(new RegExp("^"+args+"[\\s]+in[\\s]+","g"),"");if(args.indexOf(",")!=-1){args=args.split(",");if(args.length>2){dumpError('多余的","在'+args.join(","),"tpl")}_key=args[0];_value=args[1];_args=args.reverse().join(",")}else{_key="_key";_value=args;_args=args+",_key"}return"(function() {var loop = {index:0,index0:-1,length: _length("+context+")}; if(loop.length > 0) {each("+context+", function("+_args+") {loop.index ++;loop.index0 ++;loop.key = "+_key+";loop.value = "+_value+";loop.first = loop.index0 == 0;loop.last = loop.index == loop.length;"}},endfor:{pfixFunc:function(obj,hasElse){return(hasElse?"":"})")+"}})();"}},"if":{validate:/if[\s]+[^\s]+/g,pfixFunc:function(obj){var statement=obj.statement;var tests=compileOperator(statement);return"if("+tests},sfix:") {"},elif:{validate:/elif[\s]+[^\s]+/g,pfixFunc:function(obj){var statement=obj.statement;var tests=compileOperator(statement);return"} else if("+tests},sfix:") {"},"else":{pfixFunc:function(obj,start){if(start=="for"){return"})} else {"}return"} else {"}},endif:{pfix:"}"},set:{validate:/set[\s]+[^\s]+/g,pfixFunc:function(obj){var statement=obj.statement;var tests=compileOperator(statement);return"var "+tests},sfix:";"}};var operator="\\/\\/|\\*\\*|\\||in|is";var operatorReplacer={"//":{pfix:"parseInt(",sfix:")"},"**":{pfixFunc:function(){return"Math.pow("},sfix:")"},"|":{sfix:")"},"in":{pfixFunc:function(vars){return"_jsIn(((typeof "+vars+' != "undefined") ? '+vars+": __undefinded)"},sfix:")"},is:{pfixFunc:function(vars){return"_jsIs(typeof "+vars+' != "undefined" ? '+vars+" : __undefinded"},sfix:")"},and:{pfixFunc:function(obj){var statement=obj.statement;return statement.replace(/[\s]*and[\s]*/g," && ")}},or:{pfixFunc:function(obj){var statement=obj.statement;return statement.replace(/[\s]*or[\s]*/g," || ")}},not:{pfixFunc:function(obj){var statement=obj.statement;return statement.replace(/[\s]*not[\s]*/g,"!")}}};function merge(obj,opstatement,start){return(obj.pfixFunc&&obj.pfixFunc(opstatement,start)||obj.pfix||"")+(opstatement.sfix||obj.sfix||"")}function funcVars(str){var str=str.replace(/\([\s]*\)/g,"").replace(/[\s\(]+/g,",").replace(/\)$/g,"");var dot=str.indexOf(",");if(dot==-1){str+='"'}else{str=str.substring(0,dot)+'"'+str.substring(dot)}return str}function redoGetStrings(str,bark){each(bark,function(value,key){str=str.replace(new RegExp(key,"g"),value)});return str}function compileOperator(opstatement){var reg=new RegExp("(^(not)|[\\s]+(and|or|not))[\\s]+","g"),matches;opstatement=opstatement.replace(/[^\s\(\)]+[\s]+is[\s]+not[\s]+[^\s\(\)]+(\([^\)]*\))?/g,function(m){var vars=m.split(/[\s]+is[\s]+not/);var str="!"+operatorReplacer.is["pfixFunc"](vars[0]);vars.splice(0,1);vars=funcVars(_trim(vars.join("")));return str+(vars?', "'+vars+"":"")+operatorReplacer.is["sfix"]});opstatement=opstatement.replace(/[^\s\(\)]+[\s]+is[\s]+[^\s\(\)]+(\([^\)]*\))?/g,function(m){var vars=m.split(/[\s]+is[\s]+/);var str=operatorReplacer.is["pfixFunc"](vars[0]);vars.splice(0,1);vars=funcVars(_trim(vars.join("is")));return str+(vars?', "'+vars+"":"")+operatorReplacer.is["sfix"]});var vars=opstatement.match(/[^\s]+[\s]+in[\s]+[^\s]+/g);if(vars){for(var i=0,len=vars.length;i<len;i++){var varName=vars[i].split(/[\s]+/g);var rge=varName[varName.length-1];varName=varName[0];opstatement=opstatement.replace(vars[i],operatorReplacer["in"].pfixFunc(varName)+","+rge+operatorReplacer["in"].sfix)}}opstatement=opstatement.replace(reg,function(m){var m=_trim(m);if(m=="not"){return"!"}if(m=="and"){return"&&"}if(m=="or"){return"||"}});return opstatement}function output(source){source=source.split("|");var str=compileOperator(source[0]);for(var i=1,len=source.length;i<len;i++){var func="_"+_trim(source[i]);var fs=func.split("(");var fname=_trim(fs[0]);fs.splice(0,1);fs=_trim(fs.join("("));if(fname=="_default"){str=fname+"( typeof "+str+' == "undefined" ? '+fs.replace(/\)$/g,"")+" : "+str+")"}else{str=fname+"("+str+((!fs||fs==")")?")":","+fs)}}return"__output.push(_escape("+str+"));"}function getLineNumber(tplString,pointer){return tplString?(tplString.substr(0,pointer+1).match(/\n/g)||[]).length+1:0}function setKeyV(obj,value){var k=Math.random()*100000>>0;while(!obj["__`begin`__"+k+"__`end`__"]){k++;obj["__`begin`__"+k+"__`end`__"]=value}return"__`begin`__"+k+"__`end`__"}Ursa.ioStart=function(){return"function (__context) {var __output = [];with(__context) {"};Ursa.ioEnd=function(){return'};return __output.join("");}'};Ursa.ioHTML=function(ins){return'__output.push("'+_escape(ins,"js")+'");'};Ursa.ioOutput=function(ins){return output(ins)};Ursa.ioOP=function(ins){return compileOperator(ins)+";"};Ursa.ioMerge=function(matches,sourceObj,flag){return merge(tagsReplacer[matches],sourceObj,flag)};Ursa.set=function(key,value){Ursa[key]=value};function SyntaxGetter(tplString){var pointer=-1,tplString=cleanWhiteSpace(tplString),character,stack="",statement="",endType="",tree=[],oldType,result=Ursa.ioStart(),tagStack=[],tagStackPointer=[],strDic={},type=false;while((character=tplString.charAt(++pointer))!=""){id=tagStackPointer.length;if(type==3){if(character==commentEnder){character=tplString.charAt(++pointer);if(character==ender){type=false}}continue}if(type%3==1&&(character=="'"||character=='"')){var start=tplString.charAt(pointer),tmpStr=start;while((character=tplString.charAt(++pointer))&&(character!=start)){if(character=="\\"){tmpStr+="\\";character=tplString.charAt(++pointer)}tmpStr+=character}tmpStr+=start;stack+=setKeyV(strDic,tmpStr)}else{if(character=="\\"){type=2;stack+=character+character}else{if(character==starter){character=tplString.charAt(++pointer);oldType=type;switch(character){case commentStarter:type=3;break;case opStarter:type=4;break;case statementStarter:type=1;break;default:stack+=starter;if(character.match(/[\'\"]/g)){pointer--}else{stack+=character}continue;break}if(oldType==2){result+=Ursa.ioHTML(stack);stack=""}else{if(character==ender){}}}else{if(endType=character.match(endStartReg)){endType=endType[0];if(type!=2){character=tplString.charAt(++pointer);if(character==ender){if(endType==opEnder){result+=Ursa.ioOutput(_trim(stack))}else{var start=tagStackPointer[tagStackPointer.length-1],matches,flag=start&&start.type,source=_trim(stack),id=1;if((matches=source.match(tags))){matches=matches[0];if(matches.indexOf("end")==0){id=tagStackPointer.length;flag=tagStack.splice(start.p,tagStack.length-start.p).length>1;tagStackPointer.splice(tagStackPointer.length-1,1)}else{if(matches!="set"){tagStack.push(matches);if(matches=="if"||matches=="for"){tagStackPointer.push({p:tagStack.length-1,type:matches})}id=tagStackPointer.length}}result+=Ursa.ioMerge(matches,{statement:source.replace(new RegExp("^"+matches+"[\\s]*","g"),"")},flag)}else{result+=Ursa.ioOP(source)}}type=false;stack="";continue}else{if(character.match(endStartReg)){pointer--;stack+=endType;continue}else{stack+=endType+character}}}else{stack+=endType}}else{if(!type){type=2}stack+=character}}}}}if(stack){if(type==2){result+=Ursa.ioHTML(stack);stack=null}else{dumpError(8,stack)}}result+=Ursa.ioEnd();if(tagStack.length){dumpError(5,tplString,pointer,tagStack)}return redoGetStrings(result.replace(/\n/g,""),strDic)}Ursa.parse=SyntaxGetter;Ursa.setConfig=setConfig})();if(__ssjs__){exports.Ursa=Ursa}else{if(window.define){define("Ursa",[],function(){return Ursa})}}define("tpl",["./Ursa"],function(a){a.setConfig({starter:"<",ender:">"});return{render:function(b,c){return a.render(+new Date(),c,b)}}});define("conf",[],function(){return{client_id:"1120",redirectUrl:"/static/api/jump.htm",thirdRedirectUrl:"/static/api/tj.htm"}});(function(e){window.uuiJQuery=e;function i(m,l){for(var k in l){if(m[k]){continue}m[k]=l[k]}}var c={};function f(k){var p=k.match(/WebKit\/([\d.]+)/),m=k.match(/(Android)\s+([\d.]+)/),o=k.match(/(iPad).*OS\s([\d_]+)/),q=!o&&k.match(/(iPhone\sOS)\s([\d_]+)/),t=k.match(/(webOS|hpwOS)[\s\/]([\d.]+)/),r=t&&k.match(/TouchPad/),s=k.match(/Kindle\/([\d.]+)/),n=k.match(/Silk\/([\d._]+)/),l=k.match(/(BlackBerry).*Version\/([\d.]+)/);if(m){c.android=true,c.version=m[2]}if(q){c.ios=c.iphone=true,c.version=q[2].replace(/_/g,".")}if(o){c.ios=c.ipad=true,c.version=o[2].replace(/_/g,".")}if(t){c.webos=true,c.version=t[2]}if(r){c.touchpad=true}if(l){c.blackberry=true,c.version=l[2]}if(s){c.kindle=true,c.version=s[1]}}if(!e.os){f(navigator.userAgent)}else{c=e.os}var j;uiDict={};function b(){return{top:(document.body.scrollTop||document.documentElement.scrollTop),left:(document.body.scrollLeft||document.documentElement.scrollLeft)}}function a(l){var k=b();return{clientX:l.clientX,clientY:l.clientY,left:l.clientX+k.left,top:l.clientY+k.top}}function h(){return Math.floor(Math.random()*65536).toString(16)}var g=c&&c.version;if(g){var d=a;a=function(k){var k=k.originalEvent;if(k.touches){return d(k.touches[0])}return d(k)}}e.fn.getUUI=function(l){var k=[];var l=l||this.uiName;this.each(function(n,o){var m=e(o).data(l);if(m&&uiDict[m]){k.push(uiDict[m])}});return k};e.fn.excUUICMD=function(m,l){var k=this.getUUI(this.uiName);e.each(k,function(n,o){o.excUUICMD&&o.excUUICMD(m,l)});return this};e.UUIBase={ismobile:g,stopPropagation:function(k){k.stopPropagation&&k.stopPropagation();k.cancelBubble=true},preventDefault:function(k){if(k&&k.preventDefault){k.preventDefault()}else{window.event.returnValue=false}return false},getScroll:b,getEPos:a,guid:function(){return(h()+h()+h()+h()+h()+h()+h()+h())},getMousePos:function(){return j},empty:function(){if(document.selection&&document.selection.empty){document.selection.empty()}else{if(window.getSelection){window.getSelection().removeAllRanges()}}},offset:function(k){return e.extend({width:k.width(),height:k.height()},k.offset())},eventHash:{mousedown:g?"touchstart":"mousedown",mousemove:g?"touchmove":"mousemove",mouseover:g?"touchstart":"mouseover",mouseup:g?"touchend":"mouseup",click:"click"},baseClass:{excUUICMD:function(l,k){if(this[l]){this[l](k)}if(l=="destroy"){this._destroy(k)}},on:function(n,l,m,k){arguments[1]=this.eventName(l);this.eventList?this.eventList.push(arguments):this.eventList=[arguments];n.on.apply(n,Array.prototype.slice.call(arguments,1));return arguments},eventName:function(k){return e.UUIBase.eventHash[k]||k},off:function(l){var m=l[0],k=this;m.off.apply(m,Array.prototype.slice.call(l,1));e.each(this.eventList,function(o,n){if(l==n){k.eventList.splice(o,1)}})},_destroy:function(){if(!this.eventList){return}e.each(this.eventList,function(k,l){l[0].off.apply(l[0],Array.prototype.slice.call(l,1))})}},data:{},css:[],init:function(){if(e.UUIBase.css.length){var l=e.UUIBase.css.join("");if(l==""){return}var k=document.createElement("style");k.setAttribute("type","text/css");k.innerHTML=l;e("head").append(k);e.UUIBase.css=[];e.UUIBase.data=[]}},create:function(k,l){e[k]=l;i(e[k].prototype,e.UUIBase.baseClass);e.fn[k]=function(n){var m=n||{};this.uiName=k;this.each(function(p,q){var o=e(q).data(k);if(o){if(m.destroy){uiDict[o].excUUICMD("destroy",m);delete uiDict[o];e(q).removeData(k)}else{n&&uiDict[o].excUUICMD("update",m)}}else{if(!m.destroy){if(m.enable===undefined&&m.disable===undefined){m.enable=true}o=k+(+(new Date()));e(q).data(k,o);uiDict[o]=new e[k](e(q),m)}}});if(m.instance){return this.getUUI()}return this}}};e(function(){e(document).on(e.UUIBase.eventHash.mousemove,function(k){j=a(k)});g&&e(document).on(e.UUIBase.eventHash.mousedown,function(k){j=a(k)})})})(jQuery);define("uuibase",function(){});(function(c){var a={type:"submit",onsinglefail:function(){},onsinglesuccess:function(){},onfocus:function(){},onblur:function(){},onformfail:function(){return false},onformsuccess:function(){return false}};function b(g,e){var f=this;f._dom=g;var d=f.options=c.extend({},a);f.guid=c.UUIBase.guid();f.update(e||{});f._bindEvent()}b.prototype={update:function(d){this.options=c.extend(this.options,d)},_types:{num:function(d){return !d.length||/^\d+$/.test(d)},cellphone:function(d){return !d.length||/^1\d{10}$/.test(d)},require:function(d){return c.trim(d).length},email:function(d){return !d.length||/^(\w)+(\.\w+)*@([\w_\-])+((\.\w+)+)$/.test(d)},min:function(e,d){return !e.length||e.length>=d},max:function(e,d){return !e.length||e.length<=d},range:function(f,e,d){return !f.length||f.length<=d&&f.length>=e}},_bindEvent:function(){var e=this;var d=e._dom;this.on(d,"submit",function(){var g=c(this);var h=true;var f=g.find(":input");f.each(function(i,j){h=e._validate(c(j))&&h});return(h?e.options.onformsuccess(g):e.options.onformfail(g))});d.on("focus",":input",function(){e.options.onfocus(c(this))});d.on("blur",":input",function(){e.options.onblur(c(this))});if(e.options.type=="blur"){d.on("blur",":input",function(){return e._validate(c(this))})}},_validate:function(s){var r=s.attr("uui-type"),f=s.attr("uui-reg"),p=this;if(!r&&!f){return true}var h=[];if(f){h.push([function(i){return new RegExp(f,"g").test(i)},[]])}if(r){r=r.split(" ");for(var m=0,g=r.length;m<g;m++){var o=r[m];var k,j=[];var e=/^(\w+)\((.*)\)$/g,q;q=e.exec(o);if(q){k=p._types[q[1]];j=q[2].split(",")}else{k=p._types[o]}h.push([k,j,o])}}for(var m=0,g=h.length;m<g;m++){var k=h[m][0],j=h[m][1],d=h[m][2];j.unshift(s.val());if(!k){continue}var n=k.apply(null,j);n?p.options.onsinglesuccess(s,d):p.options.onsinglefail(s,d);if(!n){break}}return n}};b.addType=function(d,e){b.prototype._types[d]=e};c.UUIBase.create("uuiForm",b);c(c.UUIBase.init)})(jQuery);define("uuiForm",function(){});define("form",["./utils","./conf","./uuibase","./uuiForm"],function(i,h){$.uuiForm.addType("password",function(k){return k.length<=16&&k.length>=6});$.uuiForm.addType("vpasswd",function(m,n){var k=$("#"+n.slice(0,1).toUpperCase()+n.slice(1)+"Ipt");if(k&&k.length){var l=k.val();return l==m}return true});$.uuiForm.addType("nick",function(k){return/^[a-z]([a-zA-Z0-9_.]{3,15})$/.test(k)});var e={require:function(l){var k=l.parent().prev().html();return"请填写"+k.replace("：","")},email:function(){return"邮箱格式不正确"},password:function(){return"密码长度为6-16位"},cellphone:function(){return"请输入正确的手机号码"},vpasswd:function(){return"两次密码输入不一致"},range:function(k){return""},max:function(l,k){return"输入字符请少于"+k+"个字"},nick:function(k){if(k.val().length<3||k.val().length>16){return"个性帐号长度为6-16位"}return"小写字母开头的数字字母下划线或组合"}};var b={email:"请输入您作为帐号的邮箱名",password:"6-16位，字母(区分大小写)、数字、符号",nick:"小写字母开头的数字字母下划线或组合"};var f=function(l,k){if(!l.parent().parent().find("."+k).length){l.parent().parent().append('<span class="'+k+'"></span>')}};var j=function(l,k){return l.parent().parent().find("."+k)};var c=function(l){if(l.attr("data-desc")){return l.attr("data-desc")}var k=l.attr("uui-type");k=(k||"").split(" ");var m;_.forEach(k,function(n){if(n!="require"&&!m&&b[n]){m=n}});return m?(b[m]||""):""};var d=function(m,l,k){return e[l]&&e[l](m,k)||""};var g=function(l){var k=i.uuid();l.find(".token").val(k);l.find(".vpic img").attr("src","/captcha?token="+k+"&t="+ +new Date())};var a=function(k){k.find(".vpic img,.change-vpic").click(function(){k.find(".vpic img").attr("src","/captcha?token="+k.find(".token").val()+"&t="+ +new Date());return false});k.click(function(){k.find(".form-error,.form-success").hide()})};return{render:function(l,k){k=k||{};l.uuiForm({type:"blur",onfocus:function(m){m.parent().addClass("form-el-focus");j(m,"error").hide();var n=c(m);if(n&&n.length){f(m,"desc");j(m,"desc").show().html(n)}},onblur:function(m){m.parent().removeClass("form-el-focus");j(m,"desc").hide()},onsinglefail:function(o,n){var m=n.split("(")[1];n=n.split("(")[0];m=m?m.slice(0,-1).split(","):[];var p=d(o,n,m);if(p&&p.length){f(o,"error");j(o,"desc").hide();j(o,"error").show().html(p)}},onsinglesuccess:function(n,m){j(n,"error").hide()},onformsuccess:function(m){if(!k.onbeforesubmit||k.onbeforesubmit(m)){$.post(m.attr("action"),m.serialize(),function(o){o=i.parseResponse(o);if(!+o.status){m.find(".form-success").show().find("span").html(o.statusText?o.statusText:"提交成功");k.onsuccess&&k.onsuccess(m,o)}else{var n=o.statusText?o.statusText.split("|")[0]:"未知错误";m.find(".form-error").show().find("span").html(n);k.onfailure&&k.onfailure(m)}})}return false},onformfail:function(m){m.find(".desc").hide();k.onformfail&&k.onformfail();return false}});l.append('<input type="hidden" name="token" value="" class="token"/>');l.append('<input name="client_id" value="'+h.client_id+'" type="hidden"/>');l.find(".form-btn").before('<div class="form-error"><span></span></div>');l.find(".form-btn").before('<div class="form-success"><span></span></div>');g(l);a(l)},initTel:function(m){var l,p="秒后重新获取验证码",o,q=60,k=q,n;$(".tel-valid-btn").click(function(){if(n){return}$(".main-content .form form").find(".tel-valid-error").hide();var s=$('.main-content .form form input[name="'+(m?m:"username")+'"]');if(s&&s.length){var v=s.parent().find(".error");if(!$.trim(s.val()).length){s.blur();return}if(v.length&&v.css("display")!="none"){return}}n=true;var u=$(this);o=u.html();u.html(k+p);u.addClass("tel-valid-btn-disable");var r=u.attr("action")||"/mobile/sendsms";$.get(r,{mobile:s.val(),new_mobile:s.val(),client_id:h.client_id,t:+new Date()},function(w){w=i.parseResponse(w);if(+w.status){if(+w.status!=20201){$(".main-content .form form").find(".tel-valid-error").show().html(w.statusText?w.statusText:"系统错误")}t()}});function t(){u.html(o);clearInterval(l);n=false;k=q;u.removeClass("tel-valid-btn-disable")}l=setInterval(function(){if(!--k){t()}else{u.html(k+p)}},1000)})},showFormError:function(k){$(".main-content .form form").find(".form-error").show().find("span").html(k)}}});define("safe",["./common","./tpl","./form","./conf","./utils"],function(b,a,c,e,h){var f={common:function(j){b.parseHeader(j);if(j.actype=="phone"){$(".nav li.tel").hide()}},index:function(l){var j=$("#Target");var m=j.parent();var k=new Date(+l.last_login_time);l.time={year:k.getFullYear(),month:k.getMonth()+1,day:k.getDate()};if(+l.sec_score<25){l.sec_score_desc="极低，强烈建议设置密保措施"}else{if(+l.sec_score<50){l.sec_score_desc="较低，建议设置多个密保措施"}else{if(+l.sec_score<75){l.sec_score_desc="适中，可以设置多个密保措施"}else{l.sec_score_desc="较高，已设置多个密保措施"}}}m.html(a.render(j.html(),l));m.find(".level-status b").css("width",l.sec_score+"%")},password:function(){},email:function(k){var j=$("#Target");var l=j.parent();l.html(a.render(j.html(),k));$(".form .binded a").click(function(){l.html(a.render($("#Target2").html(),{}));c.render($(".main-content .form form"),{onsuccess:function(){d.email($(".main-content .form form"))}});return false})},emailsuccess:function(j){$(".binded .result").html(j.statusText||"未知错误");$(".binded .timing").show();$(".binded .long-btn").show();setInterval(function(){var k=$("#time");if(+k.html()<=1){location.href="/"}k.html(k.html()-1)},1000)},tel:function(k){var j=$("#Target");var l=j.parent();l.html(a.render(j.html(),k));c.initTel("new_mobile");$(".form .binded a").click(function(){l.html(a.render($("#Target2").html(),k));c.render(l.find("form"),{onsuccess:function(m,n){d&&d.telcheck(l,n)}});c.initTel("new_mobile");return false})},question:function(m){var k=$("#Target");var o=k.parent();o.html(a.render(k.html(),{}));var l=o.find("label[for=Question2Ipt]").parent();l.hide();var n=l.find("input");var j=n.attr("uui-type");n.attr("uui-type","");o.find("select").on("change",function(){if($(this).val()==0){l.show();n.attr("uui-type",j)}else{l.hide();n.attr("uui-type","")}})},history:function(k){var j=$("#Target");_.each(k.records,function(l){var m=new Date(l.time);l.time={year:m.getFullYear(),month:m.getMonth()+1,day:m.getDate(),hour:m.getHours(),minute:h.addZero(m.getMinutes(),2),second:h.addZero(m.getSeconds(),2)}});k.time={};j.parent().html(a.render(j.html(),k))}};var g={question:function(){var l=$(".main-content .form form");var k=$("#NewQues");var j=l.find("select");if(+j.val()){k.val(j.find("option[value="+j.val()+"]").html())}else{k.val(l.find('input[name="question2"]').val())}}};var d={common:function(){setTimeout(function(){location.reload()},1000)},email:function(k){var j=k.find('input[name="new_email"]');k.parent().html(a.render($("#Target3").html(),{sec_email:j.val()}));b.bindJumpEmail()},tel:function(j){var k=j.attr("action");d.common()},telcheck:function(j,k){j.html(a.render($("#Target3").html(),{scode:k.data.scode}));c.render(j.find("form"),{onsuccess:function(){setTimeout(function(){location.reload()},1000)}});c.initTel("new_mobile")}};var i=function(){var j=$(".main-content .nav li a");j.each(function(k,l){$(l).attr("href",$(l).attr("href")+"?client_id="+e.client_id)})};return{init:function(j){b.showBannerUnderLine();var k={};try{k=$.evalJSON(server_data).data}catch(l){window.console&&console.log(l)}k.actype=k.actype||"";f.common(k);f[j]&&f[j](k);$(".nav").show();c.render($(".main-content .form form"),{onbeforesubmit:function(){g[j]&&g[j]();return true},onsuccess:function(m){d[j]?d[j](m):d.common(m)}})}}});