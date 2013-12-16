/**
  * dialog.js
  *
  * changelog
  * 2013-11-22[13:46:02]:modified to sohu+2
  *
  * @info yinyong,osx-x64,UTF-8,10.129.173.11,js,/Volumes/yinyong/sohuplus/static/js/oauth2pc/app
  * @author yinyong#sogou-inc.com
  * @version 0.0.2
  * @since 0.0.1
  */
;//yinyong@sogou-inc.com,2013-11-22[13:46:20]
//removed jquery
define([/*'jquery'*/], function(/*$*/) {
	var Dialog = (function() {
		var _dialog = {
			init: function(type) {
				this.initWrapper(type)
				this.initEvents()
			},
			initWrapper: function(type) {
				this.$wrap = $("#pro_confirm_box")
				this.$icon = this.$wrap.find("div.modal-body>i")
				this.$content = this.$wrap.find("div.modal-body>span")

				if (type === "confirm") {
					var btnhtml = [
						'<a href="javascript:" class="btn btn-blue">确定</a>',
						'<a href="javascript:" class="btn">取消</a>', ].join("")
					this.$wrap.find("div.modal-footer").html(btnhtml)
					this.$comfirm = this.$wrap.find("div.modal-footer>a:first")
					this.$cancel = this.$wrap.find("div.modal-footer>a:last")
				}
				if (type === "alert") {
					var btnhtml = '<a href="javascript:" class="btn btn-blue">确定</a>'
					this.$wrap.find("div.modal-footer").html(btnhtml)
					this.$comfirm = this.$wrap.find("div.modal-footer>a:first")
				}

			},
			judgePosition: function() {
				var scrolltop = $(document).scrollTop() > 0 ?
				 $(document).scrollTop()/2 :  0

				this.$wrap.css({
					top: $(window).height() / 2 + this.$wrap.height()/4  + scrolltop,
					left: $(window).width() / 2 - this.$wrap.width() / 2
				})
			},
			initEvents: function() {
				var self = this
				$(window).on("click", function(e) {
					self.$content.empty()
					self.$wrap.hide()
				})
				$("div#pro_confirm_box").on("click", function(e) {
					e.stopPropagation()
				})
				$(window).resize(function() {
					self.judgePosition()
				})
			},
			_confirm: function(content, callback) {
				var self = this

				this.init("confirm")


				this.$wrap.show()
				this.judgePosition()
				this.$content.html(content)
				this.$comfirm.on("click", function(e) {
					// e.preventDefault()
					if (callback) callback()
					self.$content.empty()
					self.$wrap.hide()
				})
				this.$cancel.on("click", function(e) {
					// e.preventDefault()
					self.$content.empty()
					self.$wrap.hide()
				})
			},
			_alert: function(content, callback) {
				var self = this

				this.init("alert")


				this.$wrap.show()
				this.judgePosition()
				this.$content.html(content)
				this.$comfirm.on("click", function(e) {
					// e.preventDefault()
					if (callback) callback()
					self.$content.empty()
					self.$wrap.hide()
				})
			}


		}
		return {
			confirm: function(content, callback) {
				return _dialog._confirm(content, callback)
			},
			alert: function(content, callback) {
				return _dialog._alert(content, callback)
			}
		}
	})()

	return {
		confirm: function(content, callback) {
			return Dialog.confirm(content, callback)
		},
		alert: function(content, callback) {
			return Dialog.alert(content, callback)
		}
	}
});