/**
 * Copyright (C) 2014 yanni4night.com
 * dialog.js
 *
 * changelog
 * 2014-08-05[09:51:16]:authorized
 *
 * @author yanni4night@gmail.com
 * @version 0.1.0
 * @since 0.1.0
 */
define([], function() {
    var gDialogs = [];
    var noop = function() {};

    function Dialog(options, inherts) {
        var opt = this.opt = $.extend({
                $container: $('.dialog'),
                $mask: $('.mask'),
                init: noop,
                onOk: noop,
                onBeforeOk: function() {
                    return true;
                }
            }, options || {}),
            self = this;

        opt.$container.on('click', '.x', function(e) {
            e.preventDefault();
            if ('function' === typeof opt.onBeforeOk && false === opt.onBeforeOk.call(self)) {
                return;
            }
            self.hide();
            opt.onOk.call(self)
        });
        opt.$mask.click(function() {
            self.hide();
        });

        ('function' === typeof opt.init) && opt.init.call(this);
        $.extend(this, inherts || {});
        gDialogs.push(this);
    }

    Dialog.prototype = {
        show: function() {
            var self = this;
            gDialogs.forEach(function(dialog) {
                (this!==self )&&dialog.hide();
            });

            this.opt.$mask.removeClass('hide');
            this.opt.$container.removeClass('hide');

            if ('function' === typeof this.opt.onShow) {
                this.opt.onShow.call(self);
            }

            return this;
        },
        hide: function() {

            this.opt.$mask.addClass('hide');
            this.opt.$container.addClass('hide');
            return this;
        }
    };

    return Dialog;
});