/**
 * PCLOGIN for Sogou browser(http://ie.sogou.com).
 * Copyright(C) 2013 Sogou.com
 *
 * @Author:yinyong(yinyong@sogou-inc.com)
 * @Version:0.0.1
 * @Date:Thu Aug 22 2013 14:43:33 GMT+0800 (CST)
 */

var chrsz = 8,
    hexcase = 0;

function safe_add(a, d) {
    var c = (a & 65535) + (d & 65535);
    var b = (a >> 16) + (d >> 16) + (c >> 16);
    return (b << 16) | (c & 65535)
}

function bit_rol(a, b) {
    return (a << b) | (a >>> (32 - b))
}

function binl2hex(c) {
    var b = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
    var d = "";
    for (var a = 0; a < c.length * 4; a++) {
        d += b.charAt((c[a >> 2] >> ((a % 4) * 8 + 4)) & 15) + b.charAt((c[a >> 2] >> ((a % 4) * 8)) & 15)
    }
    return d
}

function str2binl(d) {
    var c = Array();
    var a = (1 << chrsz) - 1;
    for (var b = 0; b < d.length * chrsz; b += chrsz) {
        c[b >> 5] |= (d.charCodeAt(b / chrsz) & a) << (b % 32)
    }
    return c
}

function hex_md5(a) {
    return binl2hex(core_md5(str2binl(a), a.length * chrsz))
}

function core_md5(p, k) {
    p[k >> 5] |= 128 << ((k) % 32);
    p[(((k + 64) >>> 9) << 4) + 14] = k;
    var o = 1732584193;
    var n = -271733879;
    var m = -1732584194;
    var l = 271733878;
    for (var g = 0; g < p.length; g += 16) {
        var j = o;
        var h = n;
        var f = m;
        var e = l;
        o = md5_ff(o, n, m, l, p[g + 0], 7, -680876936);
        l = md5_ff(l, o, n, m, p[g + 1], 12, -389564586);
        m = md5_ff(m, l, o, n, p[g + 2], 17, 606105819);
        n = md5_ff(n, m, l, o, p[g + 3], 22, -1044525330);
        o = md5_ff(o, n, m, l, p[g + 4], 7, -176418897);
        l = md5_ff(l, o, n, m, p[g + 5], 12, 1200080426);
        m = md5_ff(m, l, o, n, p[g + 6], 17, -1473231341);
        n = md5_ff(n, m, l, o, p[g + 7], 22, -45705983);
        o = md5_ff(o, n, m, l, p[g + 8], 7, 1770035416);
        l = md5_ff(l, o, n, m, p[g + 9], 12, -1958414417);
        m = md5_ff(m, l, o, n, p[g + 10], 17, -42063);
        n = md5_ff(n, m, l, o, p[g + 11], 22, -1990404162);
        o = md5_ff(o, n, m, l, p[g + 12], 7, 1804603682);
        l = md5_ff(l, o, n, m, p[g + 13], 12, -40341101);
        m = md5_ff(m, l, o, n, p[g + 14], 17, -1502002290);
        n = md5_ff(n, m, l, o, p[g + 15], 22, 1236535329);
        o = md5_gg(o, n, m, l, p[g + 1], 5, -165796510);
        l = md5_gg(l, o, n, m, p[g + 6], 9, -1069501632);
        m = md5_gg(m, l, o, n, p[g + 11], 14, 643717713);
        n = md5_gg(n, m, l, o, p[g + 0], 20, -373897302);
        o = md5_gg(o, n, m, l, p[g + 5], 5, -701558691);
        l = md5_gg(l, o, n, m, p[g + 10], 9, 38016083);
        m = md5_gg(m, l, o, n, p[g + 15], 14, -660478335);
        n = md5_gg(n, m, l, o, p[g + 4], 20, -405537848);
        o = md5_gg(o, n, m, l, p[g + 9], 5, 568446438);
        l = md5_gg(l, o, n, m, p[g + 14], 9, -1019803690);
        m = md5_gg(m, l, o, n, p[g + 3], 14, -187363961);
        n = md5_gg(n, m, l, o, p[g + 8], 20, 1163531501);
        o = md5_gg(o, n, m, l, p[g + 13], 5, -1444681467);
        l = md5_gg(l, o, n, m, p[g + 2], 9, -51403784);
        m = md5_gg(m, l, o, n, p[g + 7], 14, 1735328473);
        n = md5_gg(n, m, l, o, p[g + 12], 20, -1926607734);
        o = md5_hh(o, n, m, l, p[g + 5], 4, -378558);
        l = md5_hh(l, o, n, m, p[g + 8], 11, -2022574463);
        m = md5_hh(m, l, o, n, p[g + 11], 16, 1839030562);
        n = md5_hh(n, m, l, o, p[g + 14], 23, -35309556);
        o = md5_hh(o, n, m, l, p[g + 1], 4, -1530992060);
        l = md5_hh(l, o, n, m, p[g + 4], 11, 1272893353);
        m = md5_hh(m, l, o, n, p[g + 7], 16, -155497632);
        n = md5_hh(n, m, l, o, p[g + 10], 23, -1094730640);
        o = md5_hh(o, n, m, l, p[g + 13], 4, 681279174);
        l = md5_hh(l, o, n, m, p[g + 0], 11, -358537222);
        m = md5_hh(m, l, o, n, p[g + 3], 16, -722521979);
        n = md5_hh(n, m, l, o, p[g + 6], 23, 76029189);
        o = md5_hh(o, n, m, l, p[g + 9], 4, -640364487);
        l = md5_hh(l, o, n, m, p[g + 12], 11, -421815835);
        m = md5_hh(m, l, o, n, p[g + 15], 16, 530742520);
        n = md5_hh(n, m, l, o, p[g + 2], 23, -995338651);
        o = md5_ii(o, n, m, l, p[g + 0], 6, -198630844);
        l = md5_ii(l, o, n, m, p[g + 7], 10, 1126891415);
        m = md5_ii(m, l, o, n, p[g + 14], 15, -1416354905);
        n = md5_ii(n, m, l, o, p[g + 5], 21, -57434055);
        o = md5_ii(o, n, m, l, p[g + 12], 6, 1700485571);
        l = md5_ii(l, o, n, m, p[g + 3], 10, -1894986606);
        m = md5_ii(m, l, o, n, p[g + 10], 15, -1051523);
        n = md5_ii(n, m, l, o, p[g + 1], 21, -2054922799);
        o = md5_ii(o, n, m, l, p[g + 8], 6, 1873313359);
        l = md5_ii(l, o, n, m, p[g + 15], 10, -30611744);
        m = md5_ii(m, l, o, n, p[g + 6], 15, -1560198380);
        n = md5_ii(n, m, l, o, p[g + 13], 21, 1309151649);
        o = md5_ii(o, n, m, l, p[g + 4], 6, -145523070);
        l = md5_ii(l, o, n, m, p[g + 11], 10, -1120210379);
        m = md5_ii(m, l, o, n, p[g + 2], 15, 718787259);
        n = md5_ii(n, m, l, o, p[g + 9], 21, -343485551);
        o = safe_add(o, j);
        n = safe_add(n, h);
        m = safe_add(m, f);
        l = safe_add(l, e)
    }
    return Array(o, n, m, l)
}

function md5_cmn(h, e, d, c, g, f) {
    return safe_add(bit_rol(safe_add(safe_add(e, h), safe_add(c, f)), g), d)
}

function md5_ff(g, f, k, j, e, i, h) {
    return md5_cmn((f & k) | ((~f) & j), g, f, e, i, h)
}

function md5_gg(g, f, k, j, e, i, h) {
    return md5_cmn((f & j) | (k & (~j)), g, f, e, i, h)
}

function md5_hh(g, f, k, j, e, i, h) {
    return md5_cmn(f ^ k ^ j, g, f, e, i, h)
}

function md5_ii(g, f, k, j, e, i, h) {
    return md5_cmn(k ^ (f | (~j)), g, f, e, i, h)
}

function str2binl(d) {
    var c = Array();
    var a = (1 << chrsz) - 1;
    for (var b = 0; b < d.length * chrsz; b += chrsz) {
        c[b >> 5] |= (d.charCodeAt(b / chrsz) & a) << (b % 32)
    }
    return c
}

if (!String.prototype.trim) {
    String.prototype.trim = function() {
        return this.replace(/(^\s+)|(\s+$)/g, "");
    }
}

function init(e) {
    var ipt = $('loginForm');
    if (ipt) {
        ipt.userid.focus();
    }
    if (!isAuthedUser)
        Event.observe($('btnlogin'), 'keypress', onCheckLoginKeyPress);
    Event.observe($('psw_input'), 'keypress', onCheckLoginKeyPress);
}

function checkLogin() {

    if (!validate()) {
        return;
    }

    var url = "https://account.sogou.com/act/getpairtoken?";

    var params = {
        userid: $('loginForm').userid.value,
        password: hex_md5($('loginForm').password.value),
        appid: $('loginForm').appid.value,
        ts: $('loginForm').ts.value
    };

    url += 'userid=' + encodeURIComponent(params.userid) + '&password=' + params.password + '&appid=' + params.appid + '&ts=' + params.ts;
    new Ajax.Request(url, {
        method: 'get',
        onComplete: function(t) {
            var result = '';
            result = t.responseText.trim();
            var status = result.split('|');
            if (status[0] == '0') {
                var refresh_token = status[2];
                var token = status[1];
                var userid = status[3];
                var nick = decodeURIComponent(status[4]);
                try {
                    if(supportLocalHash) {
                        window.external.passport('localhash', params.userid + "|" + hex_md5(params.userid + 'sogou' + $('loginForm').password.value));

                    }  
                        window.external.passport('result', '0|' + token + '|' + refresh_token + '|' + userid + '|' + nick);
                    
                } catch (e) {
                    alert(e)
                }
            } else {
                if ((status[0] == '2') || (status[0] == '3')) {
                    alert('用户名或密码错误，请重新输入');
                } else if (status[0] == '4') {
                    alert('您的帐号还未激活');
                } else if (status[0] == '6' || status[0] == '5') {
                    alert('系统繁忙，请稍候再试');
                } else {
                    alert('服务器正在维护，暂时无法登录，请您稍后再试');
                }
            }
        }
    });
    return false;
}

function getAutoToken() {
    var url = "https://account.sogou.com/act/getpairtoken?";

    var params = {
        userid: $('autoLoginForm').userid.value,
        sig: $('autoLoginForm').sig.value,
        timestamp: $('autoLoginForm').timestamp.value,
        appid: $('autoLoginForm').appid.value,
        ts: $('autoLoginForm').ts.value
    };


    url += 'userid=' + encodeURIComponent(params.userid) + '&sig=' + params.sig + '&timestamp=' + params.timestamp + '&' + 'appid=' + params.appid + '&ts=' + params.ts;
    new Ajax.Request(url, {
        method: 'get',
        onComplete: function(t) {
            var result = '';
            result = t.responseText.trim();
            var status = result.split('|');

            if (status[0] == '0') {
                var refresh_token = status[2];
                var token = status[1];
                var userid = status[3];
                var nick = decodeURIComponent(status[4]);

                window.external.passport('result', '0|' + token + '|' + refresh_token + '|' + userid + '|' + nick);
            } else {
                if (status[0] == '7') {
                    alert('自动登录失败， 请输入帐号密码重新登录');
                    document.location.href = "https://account.sogou.com/act/pclogin?userid=" + params.userid;
                } else {
                    alert('服务器正在维护，暂时无法登录，请您稍后再试');
                }
            }
        }
    });
    return false;
}

function validate() {
    var emptyPtn = /^\s+$/;
    if (emptyPtn.test($('loginForm').userid.value) || emptyPtn.test($('loginForm').password.value)) {
        alert('用户名或密码错误，请重新输入');
        return false;
    }

    return true;
}

function onCheckLoginKeyPress(event) {
    if (event.keyCode == 13) {
        Event.stop(event);
        checkLogin();
    }
}

Event.observe(window, 'load', init);


//Suggest below
var auto_user_id = document.getElementsByName("userid")[0];
var last_auto_user_text = auto_user_id.value;
var auto_timer;
var suggest_no = 0;
var wait = 0;
var can_hide = 1;
var suggest_val = ["@sogou.com", "@sohu.com", "@chinaren.com", "@vip.sohu.com", "@17173.com", "@focus.cn", "@game.sohu.com"];
var sugg_len=suggest_val.length;

function startAutoComplete() {
    can_hide = 0;
    auto_timer = window.setInterval("isUserNameChanged()", 500);
}

function stopAutoComplete() {
    clearInterval(auto_timer);
}

function isUserNameChanged() {
    var auto_user_text = auto_user_id.value;
    showAllSuggest();
    if (auto_user_text == "") {
        document.getElementById("auto_complete").style.display = "none";
    }
    if (auto_user_text.indexOf("@") > 0) {
        var has = 0;
        for (i = 0; i < suggest_val.length; i++) {
            if (auto_user_text.length - auto_user_text.indexOf("@") > 1) {
                if (suggest_val[i].indexOf(auto_user_text.substr(auto_user_text.indexOf("@"), auto_user_text.length)) < 0) {
                    var acc=document.getElementById("acc" + i);
                    if(acc)acc.style.display = "none";
                } else {
                    has = 1;
                }
            }
        }
        if (has == 0 && auto_user_text.length - auto_user_text.indexOf("@") > 1) {
            document.getElementById("auto_complete").style.display = "none";
        }
    }
    if (auto_user_text != last_auto_user_text && wait == 0) {
        var acc = document.getElementsByName("acc");
        clearAllSuggest();
        var has = 0;
        for (i = 0; i < sugg_len; ++i) {
            if (acc[i].style.display != "none") {
                acc[i].className = "auto_complete_content choosed";
                suggest_no = i;
                i = 8;
                has = 1;
            }
        }
        for (i = 0; i < sugg_len; ++i) {
            if (auto_user_text.indexOf("@") > 0) {
                acc[i].innerHTML = auto_user_text.substr(0, auto_user_text.indexOf("@")) + suggest_val[i];
            } else {
                acc[i].innerHTML = auto_user_text + suggest_val[i];
            }
        }
        if (has == 1) {
            document.getElementById("auto_complete").style.display = "block";
        }
    }
    last_auto_user_text = auto_user_text;
}

function selectComplete() {
    var acc = document.getElementsByName("acc");
    if (event.keyCode == 40) {
        clearAllSuggest();
        if (suggest_no == 6) suggest_no = 0;
        else suggest_no++;
        for (i = 0; i < sugg_len; ++i) {
            if (acc[suggest_no].style.display != "none") {
                acc[suggest_no].className = "auto_complete_content choosed";
                i = 8;
            } else {
                if (suggest_no == 6) suggest_no = 0;
                else suggest_no++;
            }
        }
    } else if (event.keyCode == 38) {
        clearAllSuggest();
        if (suggest_no == 0) suggest_no = 6;
        else suggest_no--;
        for (i = 0; i < sugg_len; ++i) {
            if (acc[suggest_no].style.display != "none") {
                acc[suggest_no].className = "auto_complete_content choosed";
                i = 8;
            } else {
                if (suggest_no == 0) suggest_no = 6;
                else suggest_no--;
            }
        }

    } else if (event.keyCode == 27) {
        document.getElementById("auto_complete").style.display = "none";
        showAllSuggest();
        suggest_no = 0;
    } else if (event.keyCode == 13) {
        var auto_user_text = auto_user_id.value;
        var has = 0;
        for (i = 0; i < suggest_val.length; i++) {
            if (auto_user_text.length - auto_user_text.indexOf("@") > 1) {
                if (suggest_val[i].indexOf(auto_user_text.substr(auto_user_text.indexOf("@"), auto_user_text.length)) < 0) {
                    document.getElementById("acc" + i).style.display = "none";
                } else {
                    has = 1;
                }
            }
        }
        if (auto_user_text.length - auto_user_text.indexOf("@") == 1) {
            has = 1;
        }
        if (auto_user_text.length - auto_user_text.indexOf("@") > 1) {
            has = 1;
        }
        if (has == 1) {
            if (auto_user_text.indexOf("@") < 0) {
                auto_user_id.value = auto_user_id.value + suggest_val[suggest_no];
            } else {
                auto_user_id.value = auto_user_id.value.substr(0, auto_user_id.value.indexOf("@")) + suggest_val[suggest_no];
            }
            clearAllSuggest();
            suggest_no = 0;
            showAllSuggest();
            document.getElementById("auto_complete").style.display = "none";
            wait = 1;
            setTimeout(function() {
                wait = 0;
            }, 1000);

        }
    }
}

function hideEsc() {
    setTimeout(function() {
        document.getElementById("auto_complete").style.display = "none";
        showAllSuggest();
        suggest_no = 0;
    }, 100);

}

function overComplete(a) {
    clearAllSuggest();
    var acc = document.getElementsByName("acc");
    acc[a].className = "auto_complete_content choosed";
    suggest_no = a;
}

function hideComplete() {
    if (can_hide == 1) {
        clearAllSuggest();
        suggest_no = 0;
        document.getElementById("auto_complete").style.display = "none";
    }
}

function chooseComplete(a) {
    clearAllSuggest();
    suggest_no = 0;
    var auto_user_text = auto_user_id.value;
    if (auto_user_text.indexOf("@") < 0) {
        auto_user_id.value = auto_user_id.value + suggest_val[a];
    } else {
        auto_user_id.value = auto_user_id.value.substr(0, auto_user_id.value.indexOf("@")) + suggest_val[a];
    }
    document.getElementById("auto_complete").style.display = "none";
    auto_user_id.focus();
    wait = 1;
    setTimeout(function() {
        wait = 0;
    }, 100);
}

function clearAllSuggest() {
    var acc = document.getElementsByName("acc");
    for (i = 0; i < sugg_len; ++i) {
        acc[i].className = "auto_complete_content";
    }
}

function showAllSuggest() {
    var acc = document.getElementsByName("acc");
    for (i = 0; i < sugg_len; ++i) {
        acc[i].style.display = "block";
    }
}
document.getElementById("userid").focus();