/*
 * common module script
 * @author zhengxin
 */



define(['./utils'], function(utils) {


    return {
        addUrlCommon: function(data) {
            if (data.ru) {
                $('.main-content .nav li a,.banner li a').each(function(idx, item) {
                    $(item).attr('href', $(item).attr('href') + '?ru=' + encodeURIComponent(data.ru));
                });
            }

            if (data.client_id) {
                $('.main-content .nav li a,.banner li a').each(function(idx, item) {
                    $(item).attr('href',
                        $(item).attr('href') + ($(item).attr('href').indexOf('?') == -1 ? '?' : '&') + 'client_id=' + data.client_id
                    );
                });
            }

        },
        showBannerUnderLine: function() {
            $('.banner ul').show();
            var currentBanner = $('.banner ul li.current');
            if (currentBanner.length) {
                $('.banner .underline').css('left', currentBanner.position().left)
                    .css('width', currentBanner.css('width'));
            }
        },
        parseHeader: function(data) {
            $('#Header .username').html(decodeURIComponent(data.uniqname || data.username));
            if (data.username || data.uniqname) {
                $('#Header .info').show();
            }
        },
        bindJumpEmail: function() {
            $('#JumpToUrl').click(function() {
                if ($('#JumpTarget')) {
                    window.open(utils.getUrlByMail($('#JumpTarget').html()));
                }
                return false;
            });
        },
        /**
         * [bindResendEmail description]
         * @author yinyong
         * @version 0.1
         */
        bindResendEmail: function(data) {
            var self = this;
            var count = 60;
            var inter = null;
            var $ResendEmail = $('#ResendEmail');
            $ResendEmail.click(function(e) {
                if (!inter) {
                    var btn = this;
                    var time = count;

                    $.ajax({
                        url: "/web/resendActiveMail",
                        data: {
                            client_id: 1120,
                            username: data.email || $ResendEmail.attr('data-email')
                        },
                        type: "post",
                        dataType:'json',
                        beforeSend: function() {
                            $(btn).addClass('disabled');
                        },
                        error: function(xhr, error) {
                            $ResendEmail.removeClass('disabled');
                            $(btn).text("重发验证邮件");
                            alert("通信错误");
                        },
                        success: function(data) {
                            data = $.evalJSON(data);
                            if (0 != data.status) {
                                $(btn).text("重发验证邮件");
                                $ResendEmail.removeClass('disabled');
                                return alert(data.statusText || '重发验证邮件失败');
                            }
                            inter = setInterval(function() {
                                $(btn).text(time--+"秒后重发");
                                if (!time) {
                                    clearInterval(inter);
                                    inter = null;
                                    $(btn).text("重发验证邮件");
                                    $(btn).removeClass('disabled');
                                }
                            }, 1000);
                        }
                    });
                }
                e.preventDefault();
            });
        } //bindResendEmail
    };
});