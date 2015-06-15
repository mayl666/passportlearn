/*
 * recover module script
 * @author zhengxin
 */

//do not blame me

define('recover', ['./common', './form', './tpl', './utils', './lib/md5-min'], function(common, form, ursa, utils) {

    var $el = $('.main-content .form form');
    var client_id = 1120;

    var pagefunc = {
        common: function() {},
        /**
         * 显示可找回密码的途径
         * @param  {[type]} data [description]
         * @return {[type]}      [description]
         */
        type: function(data, statusText, status) {
            var smsSent = false;
            var smsLock = false;
            var sendingEmail = false;

            var to_email;

            $('input[name="username"]').val(data.userid);
            $('input[name="ru"]').val(data.ru);
            $('input[name="client_id"]').val(data.client_id);

            if (0 != status) {
                $(".form-error span").text(statusText || "未知错误");
                $(".form-error").show();
            }

            if (data.sec_process_email) {
                $('select').append('<option value="sec_email">绑定邮箱</option>');
                $('#sec_email_form .email_head').text(data.sec_process_email.split(/\*+/)[0]);
                $('#sec_email_form .email_tail').text(data.sec_process_email.split(/\*+/)[1]);
            } else {
                $('#sec_email_form').remove();
            }

            if (data.reg_process_email) {
                $('select').append('<option value="reg_email">注册邮箱</option>');
                $('#reg_email_form .email_head').text(data.reg_process_email.split(/\*+/)[0]);
                $('#reg_email_form .email_tail').text(data.reg_process_email.split(/\*+/)[1]);
            } else {
                $('#reg_email_form').remove();
            }

            if (data.sec_process_mobile) {
                $('select').append('<option value="sec_mobile">绑定手机</option>');
                $('.mobile_head').text(data.sec_process_mobile.split(/\*+/)[0]);
                $('.mobile_tail').text(data.sec_process_mobile.split(/\*+/)[1]);
            } else {
                $('#sec_mobile_form').remove();
            }

            $('select').append('<option value="other">联系客服</option>').change(function(e) {
                $('form').hide().eq(this.selectedIndex).show();
            });;

            $('form').hide().eq(0).show();

           form.initToken($el);

            /* var tpl = $('#Target');
            var mkw = ursa.render(tpl.html(), data);
            tpl.parent().html(mkw);*/

            $('.tel-valid-btn').click(function(e) {
                var $btn = $(this);
                var f = $btn.parents('form');
                var $err = f.find('.form-error');
                var seg_mobile = $.trim(f.find('input[name="seg_mobile"]').val());
                var mobile = $.trim($('.mobile_head').text()) + seg_mobile + $.trim($('.mobile_tail').text());
                var originText = $btn.text();
                e.stopPropagation();
                $err.hide();
                
                if (hex_md5(mobile) !== data.sec_mobile_md5) {
                    $err.show().find('span').text('请补充完整正确的手机号');                    
                    return;
                }

                if (smsLock) {
                    return;
                }

                $.ajax({
                    url: '/web/findpwd/sendsms',
                    data: {
                        username: data.userid,
                        client_id: data.client_id||"",
                        sec_mobile: mobile,
                        ru: data.ru||"",
                        captcha:$('input[name="code-captcha"]').val(),
                        token:$('input[name="token"]').val()
                    },
                    type:'post',
                    cache: false,
                    dataType: 'json',
                    beforeSend: function() {
                        smsLock = true;
                        $btn.text('正在发送中').addClass('tel-valid-btn-disable');
                    }
                }).fail(function() {
                    $err.show().find('span').text('发送验证码失败');
                    $btn.text(originText).removeClass('tel-valid-btn-disable');
                    smsLock = false;
                }).done(function(data) {
                    data = $.evalJSON(data || "");
                    var total = 60,
                        cur = total,
                        inter;
                    if (data && data.status == '0') {
                        smsSent = true;
                        inter = setInterval(function() {
                            $btn.text(--cur + '后重新发送');
                            if (cur < 0) {
                                clearInterval(inter);
                                $btn.text(originText).removeClass('tel-valid-btn-disable');
                                smsLock = false;
                            }
                        }, 1e3);
                    }
                    else {
                         if(20257==data.status){
                            $('.main-content .form form').find('.form-item-vcode').removeClass('hide');
                         }else if(20221==data.status){
                            form.initToken(f);
                         }
                        smsLock = false;
                        $btn.text(originText).removeClass('tel-valid-btn-disable');
                        $err.show().find('span').text((data && data.statusText) || '发送验证码失败');
                    }
                });
            });

            $('#sec_mobile_form').submit(function(e) {
                var $err = $(this).find('.form-error');
                var seg_mobile = $(this).find('input[name="seg_mobile"]').val();
                var v = $(this).find('.mobile_head').text() + seg_mobile + $(this).find('.mobile_tail').text();

                $(this).find('[name="sec_mobile"]').val(v);

                $err.hide();

                if (!$(this).find('[name="smscode"]').val()) {
                    e.preventDefault();
                    $err.show().find('span').text('请输入短信验证码');
                    return false;
                }

                if (hex_md5(v) !== data.sec_mobile_md5) {
                    e.preventDefault();
                    $err.show().find('span').text('请补充完整正确的手机号');
                    return false
                }

                if (!smsSent) {
                    e.preventDefault();
                    $err.show().find('span').text('您还未发送短信验证码');
                    return false;
                }
            }).find('input').focus(function(e) {
                $(this).parents('form').find('.form-error').hide();
            });

            $('#sec_email_form').submit(function(e) {
                var $err = $(this).find('.form-error');
                var seg_email = $.trim($(this).find('input[name="seg_email"]').val());
                var v = to_email = $.trim($(this).find('.email_head').text()) + seg_email + $.trim($(this).find('.email_tail').text());

                $(this).find('[name="sec_email"]').val(v);

                var $submitBtn = $(this).find('button[type="submit"]');
                var originText = $submitBtn.text();

                $err.hide();

                if (!seg_email) {
                    e.preventDefault();
                    $err.show().find('span').text('请补充完整邮箱');
                    return false;
                }

                if (sendingEmail) {
                    e.preventDefault();
                    return;
                }

                if (hex_md5(v) !== data.sec_email_md5) {
                    e.preventDefault();
                    $err.show().find('span').text('邮箱补充不正确');
                    return false;
                }

                e.preventDefault();

                $.ajax({
                    url: '/web/findpwd/sendbemail',
                    data: {
                        username: data.userid,
                        client_id:data.client_id||"",
                        scode:data.scode||"",
                        ru:data.ru||""
                    },
                    type: 'post',
                    dataType: 'json',
                    beforeSend: function() {
                        sendingEmail = true;
                        $submitBtn.text('正在发送邮件');
                    },
                    complete: function() {
                        sendingEmail = false;
                        $submitBtn.text(originText);
                    }
                }).done(function(ret) {
                    ret = $.evalJSON(ret);
                    data.scode = ret.data&&ret.data.scode;
                    if (ret && 0 == ret.status) {
                        $('.form').replaceWith($('.email-success').removeClass('hide'));
                    } else {
                        $err.show().find('span').text((ret && ret.statusText) || '邮件发送失败');
                    }
                }).fail(function(e) {
                    $err.show().find('span').text('邮件发送失败');
                });
            }).find('input').focus(function(e) {
                $(e).parents('form').find('.form-error').hide();
            });

            $('#reg_email_form').submit(function(e) {
                var $err = $(this).find('.form-error');
                var seg_email = $.trim($(this).find('input[name="seg_email"]').val());
                var v = to_email = $.trim($(this).find('.email_head').text()) + seg_email + $.trim($(this).find('.email_tail').text());

                $(this).find('[name="reg_email"]').val(v);

                var $submitBtn = $(this).find('button[type="submit"]');
                var originText = $submitBtn.text();

                $err.hide();

                if (!seg_email) {
                    e.preventDefault();
                    $err.show().find('span').text('请补充完整邮箱');
                    return false;
                }

                if (sendingEmail) {
                    e.preventDefault();
                    return;
                }

                if (hex_md5(v) !== data.reg_email_md5) {
                    e.preventDefault();
                    $err.show().find('span').text('邮箱补充不正确');
                    return false;
                }

                e.preventDefault();

                $.ajax({
                    url: '/web/findpwd/sendremail',
                    data: {
                        username: data.userid,
                        client_id:data.client_id||"",
                        scode:data.scode||"",
                        ru:data.ru||""
                    },
                    type: 'post',
                    dataType: 'json',
                    beforeSend: function() {
                        sendingEmail = true;
                        $submitBtn.text('正在发送邮件');
                    },
                    complete: function() {
                        sendingEmail = false;
                        $submitBtn.text(originText);
                    }
                }).done(function(ret) {
                    ret = $.evalJSON(ret);
                    data.scode = ret.data&&ret.data.scode;
                    if (ret && 0 == ret.status) {
                        $('.form').replaceWith($('.email-success').removeClass('hide'));
                    } else {
                        $err.show().find('span').text((ret && ret.statusText) || '邮件发送失败');
                    }
                }).fail(function(e) {
                    $err.show().find('span').text('邮件发送失败');
                });
            }).find('input').focus(function(e) {
                $(e).parents('form').find('.form-error').hide();
            });

            (function() {
                var resending = false;
                //重发
                $(document).delegate('.resendemail', 'click', function(e) {
                    e.preventDefault();
                    if (resending) {
                        return;
                    }
                    resending = true;
                    $.ajax({
                        url: '/web/findpwd/resendmail',
                        dataType: 'json',
                        type: 'post',
                        data: {
                            username: data.userid,
                            client_id:data.client_id||"",
                            scode: data.scode||"",
                            ru:data.ru||"",
                            to_email:to_email
                        },
                        success: function(ret) {
                            ret = $.evalJSON(ret);
                            data.scode = ret.data&&ret.data.scode;
                            if (ret.status == 0) {
                                $('img.did').show();
                                $('.line .tit').text('邮件重发成功');
                                var total = 60,
                                    cur = total,
                                    inter;
                                inter = setInterval(function() {
                                    $('.re-tip').text(cur--+"秒后可再次发送邮件");
                                    if (!cur) {
                                        resending = false;
                                        clearInterval(inter);
                                        $('.re-tip').html('<a href="#" class="resendemail">重新发送邮件</a>');
                                    }
                                }, 1e3);
                            } else {
                                resending =false;
                                $('img.did').hide();
                                $('.line .tit').text('邮件重发失败');
                                $('.re-tip').html('<a href="#" class="resendemail">重新发送邮件</a>');
                            }
                        },
                        error: function() {
                            resending=false;
                            $('img.did').hide();
                            $('.line .tit').text('邮件重发失败');
                            $('.re-tip').html('<a href="#" class="resendemail">重新发送邮件</a>');
                        }
                    });
                });
            })();

        },
        /**
         * 输入找回密码的帐号，后端校验失败仍然返回此页面
         * @param  {[type]} data       [description]
         * @param  {[type]} statusText [description]
         * @return {[type]}            [description]
         */
        index: function(data, statusText,status) {

            form.render($el, {
                onbeforesubmit: function() {
                    //同步提交
                    $el.get(0) && $el.get(0).submit();
                }
            });

            $('input[name="ru"]').val(data&&data.ru);

            //有出错信息
            if (0!=status) {
                $("#UsernameIpt").val(data.userid || "");
                $(".form-error span").text(statusText || "未知错误");
                $(".form-error").show();
            }

        },
        end: function(data, statusText, status) {
            var payload = {
                success: status == '0',
                ru:null
            };
            if(data&&(0==status)&&/^https?:\/\//.test(data.ru)){
                payload.ru = data.ru;
                var total = 3,cur = total,inter;
                inter = setInterval(function(){
                    $('.count').text(cur--);
                    if(!cur){
                        clearInterval(inter);
                        location.assign(data.ru);
                    }
                },1e3);

            }
            var wml = Ursa.render('end',payload , $('#Tpl').html());
            $('#Tpl').parent().html(wml);

            

        },
        /**
         * 手机找回，包括绑定手机和注册手机
         * @param  {[type]} data [description]
         * @return {[type]}      [description]
         */
        /*        mobile: function(data) {
            form.render($el, {
                onsuccess: function(el, ret) {
                    if (!ret.data || !ret.data.scode || !ret.data.userid) {
                        alert("服务器错误");
                    } else
                        location.assign('/web/recover/reset?scode=' + ret.data.scode + "&userid=" + ret.data.userid + "&client_id=1120");
                }
            });

            $el.find("input[name=userid]").val(data.userid);

            $(".form-text").text(data.bind_mobile || data.reg_mobile);
            //下面是间隔发验证码的逻辑，原来form里的不能重用
            var tvb = $('.tel-valid-btn'),
                status, ts, cnt = 60,
                oldText = tvb.html();
            tvb.click(function(e) {
                $('.form-error').hide();
                if (status) return;

                $.ajax({
                    url: '/web/findpwd/sendsms',
                    type: "get",
                    data: {
                        userid: data.userid,
                        client_id: 1120,
                        t: +new Date
                    },
                    success: function(ret) {
                        if (!+ret.status) {
                            ts = setInterval(function() {
                                tvb.text(cnt--+"秒后重新获取验证码").addClass('tel-valid-btn-disable');
                                if (!cnt) {
                                    tvb.text(oldText).removeClass('tel-valid-btn-disable');
                                    clearInterval(ts);
                                    status = false;
                                    cnt = 60;
                                }
                            }, 1000);
                        } else {
                            $('.form-error span').text(ret.statusText || "发送失败");
                            $('.form-error').show();
                        }
                    },
                    error: function(xhr, error) {
                        window['console'] && console.log(error);
                        alert("发送验证码失败");
                    }
                });

            });

        }*/ //,
        /**
         * 通过绑定邮箱和注册邮箱找回都在此页面
         * @param  {[type]} data [description]
         * @return {[type]}      [description]
         */
        /*  email: function(data) {
            $('#EmailWrp').html(data.reg_email || data.bind_email);
            var sent = false;
            $("#sendEmailBtn").click(function(e) {
                if (sent) return;
                var oldText = $(this).html(),
                    selfBtn = $(this);
                $.ajax({
                    url: data.reg_email ? "/web/findpwd/sendremail" : "/web/findpwd/sendbemail",
                    type: "post",
                    data: {
                        userid: data.userid,
                        client_id: 1120
                    },
                    onbeforesend: function() {
                        sent = true;
                        selfBtn.html("正在发送");
                    },
                    success: function(ret) {
                        if (!+ret.status) {
                            location.assign("/web/recover/sended?email=" + (data.reg_email || data.bind_email));
                        } else {
                            selfBtn.text(oldText);
                            $('.form-error span').text(ret.statusText || "发送失败");
                            $('.form-error').show();
                        }
                    },
                    error: function(xhr, error) {
                        selfBtn.text(oldText);
                        window['console'] && console.log(error);
                        alert("操作失败");
                    },
                    complete: function() {
                        sent = false;
                    }
                });

                e.preventDefault();
                return false;
            });
        }*/ //,
        /**
         * 发送完邮件后的页面
         * @param  {[type]} data [description]
         * @return {[type]}      [description]
         */
        /*        sended: function(data) {
            $('#EmailWrp').html(data.email);
            $("#goRecvEmail").attr("href", utils.getUrlByMail(data.email));
        },*/
        /**
         * 重置密码
         * @param  {[type]} data [description]
         * @return {[type]}      [description]
         */
        reset: function(data) {
            var $el = $('.form form');
            form.render($el, {
                onbeforesubmit: function() {
                    $el.get(0) && $el.get(0).submit();
                }
            });

            $el.find("input[name=token]").remove();
            $el.find("input[name=username]").val(data.userid);
            $el.find("input[name=scode]").val(data.scode);
            $el.find('input[name="ru"]').val(data&&data.ru);

        } //,
        /**
         * 问题找回密码
         * @param  {[type]} data [description]
         * @return {[type]}      [description]
         */
        /*        question: function(data) {
            $('.form .form-text').html(data.sec_ques);
            $('.form').css('visibility', '');
            form.render($el, {
                onsuccess: function(el, ret) {
                    if (!ret.data || !ret.data.scode || !ret.data.userid) {
                        alert("服务器错误");
                    } else
                        location.assign('/web/recover/reset?scode=' + ret.data.scode + "&userid=" + ret.data.userid + "&client_id=1120");
                },
                onfailure: function() {
                    form.freshToken($el);
                }
            });
            //!
            $el.find("input[name=userid]").val(data.userid);
        }*/
    };


    return {
        init: function(type) {
            common.showBannerUnderLine();

            var data = {},
                _server_data = {},
                statusText = "";
            try {
                _server_data = $.evalJSON(server_data);
                data = _server_data.data;
                statusText = _server_data.statusText || "";
            } catch (e) {
                window['console'] && console.log("服务器数据异常:%s", e);
            }

            pagefunc.common(data);
            //yinyong#sogou-inc.com :statusText还是有用的...
            pagefunc[type] && pagefunc[type](data, statusText, +_server_data.status);
        }
    };
});