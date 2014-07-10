/**
 * Copyright (C) 2014 yanni4night.com
 * findpwd.js
 *
 * changelog
 * 2014-06-23[15:51:29]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define(['./skin'], function() {
    return {
        init: function() {
            $('.backlink').click(function(e) {
                e.preventDefault();
                history.back();
            });
        }
    };
});