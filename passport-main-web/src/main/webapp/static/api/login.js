/*
 * Sogou Passport Login
 * @author zhengxin
 */


(function(){

    var utils = {
        b64_423:function(E) {
            var D = new Array("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", "_");
            var F = new String();
            for (var C = 0; C < E.length; C++) {
                for (var A = 0; A < 64; A++) {
                    if (E.charAt(C) == D[A]) {
                        var B = A.toString(2);
                        F += ("000000" + B).substr(B.length);
                        break
                    }
                }
                if (A == 64) {
                    if (C == 2) {
                        return F.substr(0, 8);
                    } else {
                        return F.substr(0, 16);
                    }
                }
            }
            return F;
        },
        b2i:function(D) {
            var A = 0;
            var B = 128;
            for (var C = 0; C < 8; C++, B = B / 2) {
                if (D.charAt(C) == "1") {
                    A += B;
                }
            }
            return String.fromCharCode(A);
        },

        
        b64_decodex:function(D) {
            var B = new Array();
            var C;
            var A = "";
            for (C = 0; C < D.length; C += 4) {
                A += utils.b64_423(D.substr(C, 4));
            }
            for (C = 0; C < A.length; C += 8) {
                B += utils.b2i(A.substr(C, 8));
            }
            return B;
        },

        utf8to16: function(I) {
            var D, F, E, G, H, C, B, A, J;
            D = [];
            G = I.length;
            F = E = 0;
            while (F < G) {
                H = I.charCodeAt(F++);
                switch (H >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    D[E++] = I.charAt(F - 1);
                    break;
                case 12:
                case 13:
                    C = I.charCodeAt(F++);
                    D[E++] = String.fromCharCode(((H & 31) << 6) | (C & 63));
                    break;
                case 14:
                    C = I.charCodeAt(F++);
                    B = I.charCodeAt(F++);
                    D[E++] = String.fromCharCode(((H & 15) << 12) | ((C & 63) << 6) | (B & 63));
                    break;
                case 15:
                    switch (H & 15) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        C = I.charCodeAt(F++);
                        B = I.charCodeAt(F++);
                        A = I.charCodeAt(F++);
                        J = ((H & 7) << 18) | ((C & 63) << 12) | ((B & 63) << 6) | (A & 63) - 65536;
                        if (0 <= J && J <= 1048575) {
                            D[E] = String.fromCharCode(((J >>> 10) & 1023) | 55296, (J & 1023) | 56320);
                        } else {
                            D[E] = "?";
                        }
                        break;
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                        F += 4;
                        D[E] = "?";
                        break;
                    case 12:
                    case 13:
                        F += 5;
                        D[E] = "?";
                        break;
                    }
                }
                E++;
            }
            return D.join("");
        },

        tmpl:function(str, data){
            var fn = 
                    new Function("obj",
                                 "var p=[],print=function(){p.push.apply(p,arguments);};" +
                                 "with(obj){p.push('" +
                                 str
                                 .replace(/[\r\t\n]/g, " ")
                                 .split("<%").join("\t")
                                 .replace(/((^|%>)[^\t]*)'/g, "$1\r")
                                 .replace(/\t=(.*?)%>/g, "',$1,'")
                                 .split("\t").join("');")
                                 .split("%>").join("p.push('")
                                 .split("\r").join("\\'")
                                 + "');}return p.join('');");
            return data ? fn( data ) : fn;
        },
        addIframe: function(container ,url,callback){
            var iframe = document.createElement('iframe');
            iframe.style.height = '1px';
            iframe.style.width = '1px';
            iframe.style.visibility = 'hidden';
            iframe.src = url;
            
            if (iframe.attachEvent){
                iframe.attachEvent("onload", function(){
                    callback && callback();
                });
            } else {
                iframe.onload = function(){
                    callback && callback();
                };
            }

            container.appendChild(iframe);
            
        },
        uuid: function(){
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            };            
            return s4() + s4()  + s4()  + s4()  +
                s4() +  s4() + s4() + s4();
        }
    };




    
    var PassportSC = window['PassportSC'] || {};
    
    PassportSC._token = utils.uuid();
    
    PassportSC._passhtml = '<form method="post" action="https://account.sogou.com/web/login" target="_PassportIframe">'
        +'<input type="hidden" name="username" value="<%=username%>">'
        +'<input type="hidden" name="password" value="<%=password%>">'
        +'<input type="hidden" name="captcha" value="<%=vcode%>">'
        +'<input type="hidden" name="autoLogin" value="<%=isAutoLogin%>">'
        +'<input type="hidden" name="client_id" value="<%=appid%>">'
        +'<input type="hidden" name="xd" value="<%=redirectUrl%>">'
        +'<input type="hidden" name="token" value="<%=token%>">'
        +'</form>'
        +'<iframe id="_PassportIframe" name="_PassportIframe" src="about:blank" style="width：1px;height:1px;position:absolute;left:-1000px;"></iframe>';

    PassportSC._logincb = function(data){
        if( !+data.status ){
            PassportSC.onsuccess && PassportSC.onsuccess(data);
        }else if(+data.status == 20231){
            location.href = 'https://account.sogou.com/web/remindActivate?email=' + encodeURIComponent(PassportSC._currentUname)
                + '&client_id=' + PassportSC.appid
                + '&ru=' + encodeURIComponent(location.href);
        }else{
            PassportSC.onfailure && PassportSC.onfailure(data);
        }
    };

    PassportSC.getToken = function(){
        return PassportSC._token;
    };

    PassportSC._checkCommon = function(nore){
        if( !PassportSC.redirectUrl && !nore ){
            window['console'] && console.log('Must specify redirect url.Exit!');
            return;
        }
        if( !PassportSC.appid ){
            window['console'] && console.log('Must specify appid.Exit!');
            return;
        }
        return true;
    };

    PassportSC.loginHandle = function(username, password , vcode , isAutoLogin , container , onfailure, onsuccess){
        if( arguments.length < 7 ){
            onsuccess = onfailure;
            onfailure = container;
            container = isAutoLogin;
            isAutoLogin = vcode;
            vcode = '';
        }

        if( !PassportSC._checkCommon() ){
            return;
        }

        if(!container)
            return;

        PassportSC._currentUname = username;
        PassportSC.onsuccess = onsuccess,
        PassportSC.onfailure = onfailure;
        container.innerHTML = utils.tmpl(PassportSC._passhtml , {
            username:username,
            password:password,
            vcode:vcode,
            isAutoLogin: isAutoLogin,
            appid: PassportSC.appid,
            redirectUrl:PassportSC.redirectUrl,
            token: PassportSC._token
        });
        container.getElementsByTagName('form')[0].submit();
        return false;
    };

    PassportSC.logoutHandle = function( container , onfailure, onsuccess){
        if(!container)
            return;

        if( !PassportSC._checkCommon(true))
            return;

        var url = 'https://account.sogou.com/web/logout_js?client_id=' + PassportSC.appid;
        utils.addIframe(container , url , function(){
            onsuccess && onsuccess();
        });
    };

    PassportSC._parsePassportCookie =  function (F) {
        var J;
        var C;
        var D;
        this.cookie = new Object;
        J = 0;
        C = F.indexOf(":", J);
        while (C != -1) {
            var B;
            var A;
            var I;
            B = F.substring(J, C);
            var lenEnd_offset = F.indexOf(":", C + 1);
            if (lenEnd_offset == -1) {
                break;
            }
            A = parseInt(F.substring(C + 1, lenEnd_offset));
            I = F.substr(lenEnd_offset + 1, A);
            if (F.charAt(lenEnd_offset + 1 + A) != "|") {
                break;
            }
            this.cookie[B] = I;
            J = lenEnd_offset + 2 + A;
            C = F.indexOf(":", J);
        }
        var relation_userid = this._parserRelation();
        if (relation_userid != null && relation_userid.length > 0) {
            this.cookie[B] = relation_userid;
        }
        try {
            this.cookie.service = new Object;
            var H = this.cookie.service;
            H.mail = 0;
            H.alumni = 0;
            H.chinaren = 0;
            H.blog = 0;
            H.pp = 0;
            H.club = 0;
            H.crclub = 0;
            H.group = 0;
            H.say = 0;
            H.music = 0;
            H.focus = 0;
            H["17173"] = 0;
            H.vip = 0;
            H.rpggame = 0;
            H.pinyin = 0;
            H.relaxgame = 0;
            var G = this.cookie.serviceuse;
            if (G.charAt(0) == 1) {
                H.mail = "sohu";
            } else {
                if (G.charAt(2) == 1) {
                    H.mail = "sogou";
                } else {
                    if (this.cookie.userid.indexOf("@chinaren.com") > 0) {
                        H.mail = "chinaren";
                    }
                }
            } if (G.charAt(1) == 1) {
                H.alumni = 1;
            }
            if (G.charAt(3) == 1) {
                H.blog = 1;
            }
            if (G.charAt(4) == 1) {
                H.pp = 1;
            }
            if (G.charAt(5) == 1) {
                H.club = 1;
            }
            if (G.charAt(7) == 1) {
                H.crclub = 1;
            }
            if (G.charAt(8) == 1) {
                H.group = 1;
            }
            if (G.charAt(10) == 1) {
                H.music = 1;
            }
            if (G.charAt(11) == 1 || this.cookie.userid.lastIndexOf("@focus.cn") > 0) {
                H.focus = 1;
            }
            if (G.charAt(12) == 1 || this.cookie.userid.indexOf("@17173.com") > 0) {
                H["17173"] = 1;
            }
            if (G.charAt(13) == 1) {
                H.vip = 1;
            }
            if (G.charAt(14) == 1) {
                H.rpggame = 1;
            }
            if (G.charAt(15) == 1) {
                H.pinyin = 1;
            }
            if (G.charAt(16) == 1) {
                H.relaxgame = 1;
            }
        } catch (E) {}
    };


    PassportSC._parseCookie = function(){
        var cookie = document.cookie.split("; ");
        var result;
        for (var i = 0 , l=cookie.length; i < l; i++) {
            if (cookie[i].indexOf("ppinf=") == 0) {
                result = cookie[i].substr(6);
                break;
            }
            if (cookie[i].indexOf("ppinfo=") == 0) {
                result = cookie[i].substr(7);
                break;
            }
            if (cookie[i].indexOf("passport=") == 0) {
                result = cookie[i].substr(9);
                break;
            }
        }
        if ( !result ) {
            this.cookie = false;
            return;
        }
        try {
            result = unescape(result).split("|");
            if ( result[0] == "1" || result[0] == "2") {
                result = utils.utf8to16(utils.b64_decodex(result[3]));
                this._parsePassportCookie(result);
                return;
            }
        } catch (F) {}
        
    };

    PassportSC.cookieHandle = function(){
        this._parseCookie();

        if (this.cookie && this.cookie.userid != "") {
            return this.cookie.userid;
        } else {
            return "";
        }
        
    };

    PassportSC._authConfig = {
        size:{
            renren:[880,620],
            sina:[780,640],
            qq:[500,300]
        }
    };

    PassportSC.authHandle = function( provider , display ,  onfailure, onsuccess ){
        if( !PassportSC._checkCommon())
            return;
        if( !provider ){
            window['console'] && console.log('Must specify provider.Exit!');
            return;
        }
        display = display || 'page';
        if( display == 'page' && (typeof onfailure =='function' || typeof onsuccess == 'function') ){
            window['console'] && console.log('When display is page, onfailure & onsuccess must be url.Exit!');
            return;
        }

        var ru = display == 'popup'? PassportSC.redirectUrl : ( onsuccess || location.href );
        
        var authUrl = 'http://account.sogou.com/connect/login?client_id='
                + PassportSC.appid
                + '&provider=' + provider
                + '&ru='
                + encodeURIComponent(ru);//TODO PAGE
                //+ '&display=' + display;


        if( display == 'popup' ){
            var size = PassportSC._authConfig.size[provider];
            var left = (window.screen.availWidth-size[0])/2;
            window.open(authUrl , 'OPEN_LOGIN' , 'height='+ size[1] +',width='+ size[0] +',top=80,left='+left+',toolbar=no,menubar=no');
        }else if( display == 'page' ){
            location.href = authUrl;
        }
    };


    window['PassportSC'] = PassportSC;


    if( PassportSC.onApiLoaded && typeof PassportSC.onApiLoaded == 'function' ){
        PassportSC.onApiLoaded();
    }



})();
