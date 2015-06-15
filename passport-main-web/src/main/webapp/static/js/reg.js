
/*
 * form module script
 * @author zhengxin
*/
 



define( 'utils',[],function(){

    
    return {
        uuid: function(){
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            };            
            return s4() + s4()  + s4()  + s4()  +
                s4() +  s4() + s4() + s4();

        },
        addZero: function(num,len){
            num = num.toString();
            while( num.length < len ){
                num = '0'+ num;
            }
            return num;
        },
        parseResponse: function(data){
            if( typeof data == 'string' ){
                try{
                    data = eval('('+data+')');
                }catch(e){
                    data = {status:-1,statusText:'服务器故障'};
                }
            }
            return data;
        },
        addIframe: function(url , callback){
            var iframe = document.createElement('iframe');
            iframe.src = url;

            iframe.style.position = 'absolute';
            iframe.style.top = '1px';
            iframe.style.left = '1px';
            iframe.style.width = '1px';
            iframe.style.height = '1px';
            
            if (iframe.attachEvent){
                iframe.attachEvent("onload", function(){
                    callback && callback();
                });
            } else {
                iframe.onload = function(){
                    callback && callback();
                };
            }

            document.body.appendChild(iframe);
        },
        getScript: function(url , callback){
            var script = document.createElement("script");
            var head = document.head;
            script.async = true;
            script.src = url;
            script.onload = script.onreadystatechange = function( _, isAbort ) {
                if ( isAbort || !script.readyState || /loaded|complete/.test( script.readyState ) ) {
                    script.onload = script.onreadystatechange = null;
                    if ( script.parentNode ) {
                        script.parentNode.removeChild( script );
                    }
                    script = null;
                    if ( !isAbort ) {
                        callback( );
                    }
                };
            };

            head.insertBefore( script, head.firstChild );
        },
        getUrlByMail:function(mail){
            mail = mail.split('@')[1];
            if( !mail ) return false;
            var hash = {
                "139.com":"mail.10086.cn",
                'gmail.com': 'mail.google.com', 
                'sina.com': 'mail.sina.com.cn', 
                'yeah.net': 'www.yeah.net', 
                'hotmail.com': 'www.hotmail.com', 
                'live.com': 'www.outlook.com', 
                'live.cn': 'www.outlook.com', 
                'live.com.cn': 'www.outlook.com', 
                'outlook.com': 'www.outlook.com', 
                'yahoo.com.cn': 'mail.cn.yahoo.com', 
                'yahoo.cn': 'mail.cn.yahoo.com', 
                'ymail.com': 'www.ymail.com', 
                'eyou.com': 'www.eyou.com', 
                '188.com': 'www.188.com', 
                'foxmail.com': 'www.foxmail.com' 
            };
            var url;
            if( mail in hash ){
                url= hash[mail];
            }else{
                url= 'mail.' + mail;
            }
            return 'http://' + url;
        }
    };

});

/*
 * common module script
 * @author zhengxin
 */



define('common',['./utils'], function(utils) {


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
define('conf',[],function(){


    return{
        client_id:"@client_id@",
        redirectUrl: "/static/api/jump.htm",
        thirdRedirectUrl:"/static/api/tj.htm"
    };
});

/**
 给jQuery添加可直接操作uui的接口，以支持链式语法

 @module base
 @author sogou ufo team
 **/
(function($) {
    window.uuiJQuery = $;
    /**
     *继承，类继承，即父类有子类没有的才继承
     *
     *@param target 子类
     *@param obj 父类
     *@method myExtend
     * */
    function myExtend(target, obj) {
       for(var i in obj) {
           if(target[i])continue;
           target[i] = obj[i];
       } 
    };
    var os = {};
    function detect(ua){
        var webkit = ua.match(/WebKit\/([\d.]+)/),
            android = ua.match(/(Android)\s+([\d.]+)/),
            ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
            iphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
            webos = ua.match(/(webOS|hpwOS)[\s\/]([\d.]+)/),
            touchpad = webos && ua.match(/TouchPad/),
            kindle = ua.match(/Kindle\/([\d.]+)/),
            silk = ua.match(/Silk\/([\d._]+)/),
            blackberry = ua.match(/(BlackBerry).*Version\/([\d.]+)/)

        // todo clean this up with a better OS/browser
        // separation. we need to discern between multiple
        // browsers on android, and decide if kindle fire in
        // silk mode is android or not

        //if (browser.webkit = !!webkit) browser.version = webkit[1]

        if (android) os.android = true, os.version = android[2]
        if (iphone) os.ios = os.iphone = true, os.version = iphone[2].replace(/_/g, '.')
        if (ipad) os.ios = os.ipad = true, os.version = ipad[2].replace(/_/g, '.')
        if (webos) os.webos = true, os.version = webos[2]
        if (touchpad) os.touchpad = true
        if (blackberry) os.blackberry = true, os.version = blackberry[2]
        if (kindle) os.kindle = true, os.version = kindle[1]
        //if (silk) browser.silk = true, browser.version = silk[1]
        //if (!silk && os.android && ua.match(/Kindle Fire/)) browser.silk = true
    };
    if(!$.os) {
        detect(navigator.userAgent);
    } else {
        os = $.os;
    }
    
    var xy
        uiDict = {};
    function getScroll() {
        return {
            top: (document.body.scrollTop || document.documentElement.scrollTop),
            left:  (document.body.scrollLeft || document.documentElement.scrollLeft) 
        }    
    };
    function getEPos(e) {
        var scroll = getScroll();
        return {
            clientX: e.clientX,
            clientY: e.clientY,
            left: e.clientX + scroll.left, 
            top : e.clientY + scroll.top 
        }
    };
    function S4() {
        return Math.floor(Math.random() * 0x10000).toString(16);
    };
    // 判断是否是mobile
    var ismobile = os && os.version;
    // mobile重写getEPos
    if(ismobile) {
        var tmp = getEPos;
        getEPos = function(e) {
            // e是jQuery event
            var e = e.originalEvent;
            if(e.touches) return tmp(e.touches[0])
            return tmp(e);
        }
    };
    /**
     Returns this，以支持链式语法，同时将缓存到dom上的ui实例注入到jQuery对象里，经此调用后的jQuery实例将失去之前筛选的dom队列

     @method $.fn.getUUI
     @return ui list
     @example $('.datepicker').getUUI();
     **/
    $.fn.getUUI = function(uiName) {
        var arr = [];
        var uiName = uiName || this.uiName;
        this.each(function(i, dom) {
            //this.tmp.push(dom);
            var un = $(dom).data(uiName);
            if(un && uiDict[un]) {
                arr.push(uiDict[un]);
            }
        });
        return arr;
    };
    /**
     链式方式调用组件方法，不会返回执行结果

     @method $.fn.excUUICMD
     @param {String} cmd api名.
     @param {Object} options 传递给api的参数.
     @example $('.datepicker').excUUICMD('setDate','2012-09-04');
     **/
    $.fn.excUUICMD = function(cmd, options) {
        var uis = this.getUUI(this.uiName);
        $.each(uis, function(i, ui) {
            ui.excUUICMD && ui.excUUICMD(cmd, options);
        });
        return this;
    };
    /**
     通过$.UUIBase.createSgUI创建一个UI组件

     @namespace jQuery
     @class $.UUIBase
     **/
    $.UUIBase = {
        ismobile: ismobile,
        /**
         * 阻止冒泡
         *
         * @method stopPropagation
         **/
        stopPropagation: function(event) {
            event.stopPropagation && event.stopPropagation(); 
            event.cancelBubble = true; 
        },
        preventDefault: function(event){ 
            if(event && event.preventDefault) 
                event.preventDefault(); 
            else 
                window.event.returnValue = false; 
            return false; 
        }, 
        /**
         * 获取滚动条高度
         *
         * @method getScroll
         * */
        getScroll: getScroll,
        /**
         * 计算鼠标位置
         * 
         * @method getEPos
         * @return {Object} {left:,top:,clientX:,clientY}
         * */
        getEPos: getEPos,
        /**
         * 生成唯一数
         *
         * @method guid
         * @return {Number} 唯一随机数
         * */
        guid: function() {
            return (
                S4() + S4() + 
                S4() + S4() + 
                S4() + S4() + 
                S4() + S4()
            );
        },
        /**
         * 返回mousemove更新的鼠标位置
         *
         * @return {Object} {left:,top:,clientX:,clientY}
         * */
        getMousePos: function() {
            return xy    
        },
        /**
         * 清空选中
         *
         * @method empty
         * */
        empty: function() {
            if(document.selection && document.selection.empty){
                document.selection.empty();
            } else if(window.getSelection) {
                window.getSelection().removeAllRanges();
            };     
        },
        /**
         * 计算元素的offset,height,width
         *
         * @method offset
         * */
        offset: function($this) {
            return $.extend({
                width: $this.width(),
                height: $this.height()
            }, $this.offset()) 
        },
        // 路由移动和pc的事件，如果是mobile，则将mousedown => touchstart, mouseup => touchend, mousemove => touchmove, mouseover => touchstart
        eventHash: {
            mousedown: ismobile ? 'touchstart' : 'mousedown',
            mousemove: ismobile ? 'touchmove'  : 'mousemove',
            mouseover: ismobile ? 'touchstart' : 'mouseover',
            mouseup  : ismobile ? 'touchend'   : 'mouseup',
            click    : 'click'
        },
        baseClass: {
            /**
             每个UI都会继承的方法，用于以命令行形式调用ui的接口

             @method excUUICMD
             @param {String} cmd ui接口名字.
             @param {Object} options 传递给接口的参数，必须是key=>value形式.
             @return 返回接口执行结果.
             @protected
            **/
            excUUICMD: function(cmd, options) {
                if (this[cmd]) {
                    this[cmd](options);
                }
                if(cmd == 'destroy') {
                    this._destroy(options); 
                }
            },
            /**
             * 存储事件队列
             *
             * @property eventList
             * @type Array
             * */
            //eventList: [],
            /**
             * 封装的事件绑定
             *
             * @method on
             * @param {Object} $this jQuery实例
             * @param {String} event 事件名字
             * @param {String | Function} query 当query和handle参数同时出现的时候，query是选择器，事件绑定会走代理，否则query是handle
             * @param {Function} handle 回调
             * @return {arguments} 将传入的参数返回，用于销毁
             * */
            on: function($this, event, query, handle) {
                // 实现事件pc和mobile的路由
                arguments[1] = this.eventName(event);
                this.eventList ? this.eventList.push(arguments) : this.eventList = [arguments];
                $this.on.apply($this, Array.prototype.slice.call(arguments, 1));
                // 返回参数
                return arguments;
            },
            /**
             * 路由事件名称，实现mobile和pc透明封装
             * @method eventName
             * @param {String} eventName
             * @return {String} eventName
             * */
            eventName: function(eventName) {
                return $.UUIBase.eventHash[eventName] || eventName; 
            },
            /**
             * 移除绑定的事件，参数必须是通过组建on方法绑定事件的返回值
             *
             * @method off
             * @param {arguments} onReturn 通过on方法绑定的事件的返回值 
             * */
            off: function(onReturn) {
                var o = onReturn[0], _ = this;
                o.off.apply(o, Array.prototype.slice.call(onReturn, 1));
                $.each(this.eventList, function (i, arg) {
                    if(onReturn == arg)_.eventList.splice(i, 1);   
                });
            },
            /**
             *每个UI都继承的自我销毁逻辑，另可各自实现destroy逻辑，因为每个组件可能需要有自己额外销毁逻辑
             *
             *@method _destroy
             *
             * */
            _destroy: function() {
                if(!this.eventList)return;
                $.each(this.eventList, function(j, evtSingle) {
                    evtSingle[0].off.apply(evtSingle[0], Array.prototype.slice.call(evtSingle, 1));
                })
            }
        },
        data: {},
        css: [],
        /**
         将js内的css注入到页面里，暂时只针对mobile做此处理

         @method $.UUIBase.init
         @example $.UUIBase.init()
         * */
        init: function() {
            if ($.UUIBase.css.length) {
                var cssText = $.UUIBase.css.join('');
                if (cssText == '')return;
                var style = document.createElement('style');
                style.setAttribute('type', 'text/css');
                style.innerHTML = cssText;
                $('head').append(style);
                $.UUIBase.css = [];
                $.UUIBase.data = [];
            }
        },
        /**
         创建一个uui，将其注册到jQuery上面

         @method $.UUIBase.create
         @param {String} uiName 组件名.
         @param {Function} classCode 组件代码.
          @example $.UUIBase.create('datepicker',function($this,options){xxxx});
         **/
        create: function(uiName, classCode) {
            $[uiName] = classCode;
            // 继承基类，如果子类有该方法的实现，则不继承该方法
            myExtend($[uiName].prototype, $.UUIBase.baseClass);
            $.fn[uiName] = function(options) {
                var _options = options || {};
                this.uiName = uiName;
                this.each(function(i, item) {
                    var un = $(item).data(uiName);
                    // 已经存在一个
                    if (un) {
                        if (_options.destroy) {
                            // 移除
                            uiDict[un].excUUICMD('destroy', _options);
                            delete uiDict[un];
                            $(item).removeData(uiName);
                        }
                        else
                        // 如果传递了!false的options，则更新ui，否则只是实现获取ui
                        options && uiDict[un].excUUICMD('update', _options);
                    }
                    else if (!_options.destroy) {
                        // 新建，默认enable
                        if (_options.enable === undefined && _options.disable === undefined)
                                _options.enable = true;

                        un = uiName + (+(new Date()));
                        $(item).data(uiName, un);
                        uiDict[un] = new $[uiName]($(item), _options);
                    }
                });
                if (_options.instance)
                    // 返回uilist 
                    return this.getUUI();
                return this;
            };
        }
    };
    $(function() {
        $(document).on($.UUIBase.eventHash['mousemove'], function(e) {
            xy = getEPos(e);
        });
        // 由于手机没有鼠标，因此需要在touchstart的时候更新一下xy的位置
        ismobile && $(document).on($.UUIBase.eventHash['mousedown'], function(e) {
            xy = getEPos(e);
        });
    });
})(jQuery);

define("uuibase", function(){});

/**
 * 组件

 * @module uuiForm
 * @authoer zhengxin@sogou ufo team
 **/
(function($) {
    /**
     * 表单校验组件
     * 需要在表单元素上手工制定校验类型
     * 可以使用内置或自定义类型，在表单上声明 uui-type="{type}"既可以
     * 内置type包括:
     * * require  必填项
     * * num 数字
     * * cellphone 手机号码
     * * email 邮箱
     * * min(10) 至少输入字数，括号内为最少的值
     * * max(10) 最多输入字数，括号内为最多的值
     * * range(5,10) 输入字数范围
     * 自定义类型也可以有传入值，依序附加在参数里
     * 也可以在表单上制定自定义正则表达式 uui-reg="\d+"
     * @class $.fn.uuiForm
     * @constructor
     * @param {jQuery} $this 需要校验的表单元素
     * @param {Object} options 配置
     * @param {String} options.type 表单校验的时机，取值为submit|blur，默认submit
     * @param {Function} options.onsinglefail 单个表单项表单校验不通过时触发，接受两个参数，$el为当前出错的表单项,name为出错对应的错误类型
     * @param {Function} options.onsinglesuccess 单个表单项校验通过时触发，接受一个参数，$el为当前通过表单项
     * @param {Function} options.onfocus 单个表单项focus时触发,接受一个参数，$el为当前表单项
     * @param {Function} options.onblur 单个表单项blur时触发,接受一个参数，$el为当前表单项
     * @param {Function} options.onformfail 表单提交校验失败时触发，如需阻止默认提交行为，需要return false
     * @param {Function} options.onformsuccess 表单提交校验成功时触发，如需阻止默认提交行为，需要return false
     * @example $('.uuiForm').uuiForm({onformsuccess:function(){alert(1)}})
     * */
    // default setting
    var _options = {
        type:'submit',
        onsinglefail:function(){},
        onsinglesuccess:function(){},
        onfocus: function(){},
        onblur: function(){},
        onformfail:function(){
            return false;
        },
        onformsuccess:function(){
            return false;
        }
    };
    function uuiForm($this, options) {
        var me = this;
        me._dom = $this;
        // extend default setting
        var opt = me.options = $.extend({}, _options);
        me.guid = $.UUIBase.guid();
        me.update(options || {});

        me._bindEvent();
    };
    // 如果不需要，可以删除
    uuiForm.prototype = {
        /**
         * 更新实例实现，请通过$('.uuiForm').uuiForm({xxxx})调用

         * @method update
         * @param {Object} options 参数配置
         * @example $('.uuiForm').uuiForm().excUUICMD('update', {enable:1 }) = $('.uuiForm').uuiForm({enable: 1});
         * */
        update: function(options) {
            this.options = $.extend(this.options, options);
        },
        /*
        \/**
         * 实例内部自我销毁的接口，可以不实现，如未实现，destroy操作会被定为到继承的medestroy上，但是不能销毁和dom的绑定，不建议调用，请使用$('.uuiForm').uuiForm({destroy: 1})

         * @method destroy
         * @param {Object} options 参数配置
         * @example $('.uuiForm').uuiForm().excUUICMD('destroy');
         * *\/
        destroy: function(options) {
            // 组件自有的特殊的自我销毁逻辑
        }
        */
        /**
         * 默认类型
         */
        _types: {
            num: function(value){
                return !value.length || /^\d+$/.test(value);
            },
            cellphone: function(value){
                return !value.length || /^1\d{10}$/.test(value);
            },
            require: function(value){
                return $.trim(value).length;
            },
            email: function(value){
                return !value.length || /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value);
            },
            min:function(value , min){
                return !value.length|| value.length >= min;
            },
            max: function(value , max){
                return !value.length || value.length <= max;
            },
            range: function(value , min , max){
                return !value.length || value.length <= max && value.length >= min;
            }
        },

        _bindEvent: function(){
            var me = this;
            var $el = me._dom;
            this.on($el , 'submit' , function(){
                var form = $(this);
                var result = true;

                var els = form.find(':input');

                els.each(function(idx,el){
                    result = me._validate($(el)) && result;
                });

                return (result? me.options.onformsuccess(form) : me.options.onformfail(form));
            });

            $el.on('focus' , ':input' , function(){
                me.options.onfocus($(this));
            });
            $el.on('blur' , ':input' , function(){
                me.options.onblur($(this));
            });

            if( me.options.type == 'blur' ){
                $el.on('blur' , ':input' , function(){
                    return me._validate($(this));
                });
            }
        },

        _validate: function($el){
            var uuitype = $el.attr('uui-type'),
                uuireg = $el.attr('uui-reg'),
                me = this;
            if( !uuitype && !uuireg )
                return true;
            
            var validFuncs = [] ;
            
            if( uuireg ){
                validFuncs.push([function(value){
                    return new RegExp(uuireg , 'g').test(value);
                } , []]);
            }
            if( uuitype ){
                uuitype = uuitype.split(' ');
                for( var i=0,l=uuitype.length; i<l; i++ ){
                    var stype = uuitype[i];
                    var validFunc , params = [];
                    var pattern = /^(\w+)\((.*)\)$/g ,
                        result;
                    result=pattern.exec(stype);
                    if( result ){
                        validFunc = me._types[result[1]];
                        params = result[2].split(',');
                    }else{
                        validFunc = me._types[stype];
                    }
                    validFuncs.push([ validFunc , params , stype ]);
                }
            }
            
            for( var i=0,l=validFuncs.length; i<l ;i++ ){
                var validFunc = validFuncs[i][0],
                    params = validFuncs[i][1],
                    name = validFuncs[i][2];
                params.unshift($el.val());
                if( !validFunc )
                    continue;
                var validresult = validFunc.apply(null, params);
                validresult ? me.options.onsinglesuccess($el , name) : me.options.onsinglefail($el , name);
                if( !validresult )
                    break;
            }

            return validresult;
        }
    };

    /**
     * 添加校验规则
     * @param {String} name 添加的规则的名字
     * @param {Function} func 添加的规则的校验方法，方法第一个参数为表单的值，其余为校验方法声明的变量，返回值true表示校验通过
     * @example $.uuiForm.addType('custom' , function(){});
     */
    uuiForm.addType = function(name,func){
        uuiForm.prototype._types[name] = func;
    };

    $.UUIBase.create('uuiForm', uuiForm);
    // 创建css
    $($.UUIBase.init);
})(jQuery);

define("uuiForm", function(){});

/*
 * form module script
 * @author zhengxin
*/

define('form',['./utils','./conf','./uuibase' , './uuiForm'] , function(utils,conf){

    /**
     * Just accept "CJK Unified Ideographs"
     * @see http://zh.wikipedia.org/wiki/Unicode%E4%B8%AD%E6%97%A5%E9%9F%93%E7%B5%B1%E4%B8%80%E8%A1%A8%E6%84%8F%E6%96%87%E5%AD%97%E5%88%97%E8%A1%A8
     */
    
    var nicknameReg=/^([a-zA-Z0-9]|[\u4e00-\u9fa5]){2,12}$/;
    var invalidNicknameKey=['搜狐','搜狗','搜狐微博','sohu','souhu','sogou','sougou'];

    if(!Array.indexOf)
    {
        Array.prototype.indexOf = function(item) {
            for (var i = 0; i < this.length; ++i) {
                if (this[i] == item)
                    return i;
            }
            return -1;
        };
    }

    $.uuiForm.addType('password' , function(value){
        return value.length<=16 && value.length>=6;
    });
    $.uuiForm.addType('vpasswd' , function(value , target){
        var targetIpt = $( '#' + target.slice(0,1).toUpperCase() + target.slice(1) + 'Ipt' );
        if( targetIpt && targetIpt.length){
            var vvalue = targetIpt.val();
            return vvalue == value;
        }
        return true;
    });
    $.uuiForm.addType('nick' , function(value){
        return /^[a-zA-Z]([a-zA-Z0-9_.]{3,15})$/.test(value);
    }); 

    $.uuiForm.addType('new_answer' , function(value){
        return value&&value.replace(/[^\x00-\xff]/g,'xx').length<=50;
    });    

    $.uuiForm.addType('nickname' , function(value){
        return  nicknameReg.test(value)&&(invalidNicknameKey.indexOf(value)<0);
    });

    $.uuiForm.addType('uniqname' , function(value){
        return  nicknameReg.test(value)&&(invalidNicknameKey.indexOf(value)<0);
    });    

    $.uuiForm.addType('fullname' , function(value){
        return   value.length<=50&&(value==''||/^[a-z\u4e00-\u9fa5]+$/i.test(value));
    });

    $.uuiForm.addType('personalid', function(value) {
        return (value == "") || idTester.valid(value);
    });
    $.uuiForm.addType('nonempty', function(value) {
        return !!$.trim(value);
    });
    
    //yinyong#sogou-inc.com:Copied from Internet.
    var idTester = {
        aCity: {
            11: "北京",
            12: "天津",
            13: "河北",
            14: "山西",
            15: "内蒙古",
            21: "辽宁",
            22: "吉林",
            23: "黑龙江",
            31: "上海",
            32: "江苏",
            33: "浙江",
            34: "安徽",
            35: "福建",
            36: "江西",
            37: "山东",
            41: "河南",
            42: "湖北",
            43: "湖南",
            44: "广东",
            45: "广西",
            46: "海南",
            50: "重庆",
            51: "四川",
            52: "贵州",
            53: "云南",
            54: "西藏",
            61: "陕西",
            62: "甘肃",
            63: "青海",
            64: "宁夏",
            65: "新疆",
            71: "台湾",
            81: "香港",
            82: "澳门",
            91: "国外"
        },
        valid: function(sId) {
            var iSum = 0;
            var info = "";
            if (!/^\d{17}(\d|x)$/i.test(sId)) return false;
            sId = sId.replace(/x$/i, "a");
            if (this.aCity[parseInt(sId.substr(0, 2))] == null) return false;//"Error:非法地区";
            var sBirthday = sId.substr(6, 4) + "-" + Number(sId.substr(10, 2)) + "-" + Number(sId.substr(12, 2));
            var d = new Date(sBirthday.replace(/-/g, "/"))
            if (sBirthday != (d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate())) return false;//"Error:非法生日";
            for (var i = 17; i >= 0; i--) iSum += (Math.pow(2, i) % 11) * parseInt(sId.charAt(17 - i), 11)
            if (iSum % 11 != 1) return false;//"Error:非法证号";
            return true;//aCity[parseInt(sId.substr(0, 2))] + "," + sBirthday + "," + (sId.substr(16, 1) % 2 ? "男" : "女")
        }
    };

    var ErrorDesc = {
        require: function($el){
            var label= $el.parent().prev().html();
            return '请填写' + label.replace('：', '');
        },
        nonempty: function($el){
            return '不能为空';
        },
        email: function(){
            return '邮箱格式不正确';
        },
        password: function(){
            return '密码长度为6-16位';
        },
        cellphone: function(){
            return '请输入正确的手机号码';
        },
        vpasswd: function(){
            return '两次密码输入不一致';
        },
        range: function($el){
            return "";
        },
        max: function($el , max){
            return '输入字符请少于' + max + '个字';
        },
        nickname: function($el){
            if($el.val().length <2 || $el.val().length>12 ){
                return '昵称长度为2-12位';
            }else if(/[^\u4e00-\u9fa5a-zA-Z0-9]/.test($el.val())){
                return "只能使用中文、字母、数字";
            }else if(invalidNicknameKey.indexOf($el.val())>-1){
                return "含有非法关键字"
            }
            return '昵称不合法';
        }, 
        uniqname: function($el){
            if($el.val().length <2 || $el.val().length>12 ){
                return '昵称长度为2-12位';
            }else if(/[^\u4e00-\u9fa5a-zA-Z0-9]/.test($el.val())){
                return "只能使用中文、字母、数字";
            }else if(invalidNicknameKey.indexOf($el.val())>-1){
                return "含有非法关键字"
            }
            return '昵称不合法';
        },
        fullname:function($el){
            if ($el.val().length > 50)
                return "不能超过50个字符"
            else return "真实姓名仅允许输入英文字母和汉字";
        },  
        new_answer:function($el){
             return "不能超过50个英文字母或25个汉字"
        },
        nick: function($el){
            if( $el.val().length <4 || $el.val().length>16 ){
                return '个性帐号长度为4-16位';
            }
            return '字母开头的数字、字母、下划线或组合';
        },
        personalid:function(){
            return "请输入18位有效的身份证号码";
        }
    };

    var NormalDesc = {
        email:"请输入您作为帐号的邮箱名",
        password:"6-16位，字母(区分大小写)、数字、符号",
        nick: "字母开头的数字、字母、下划线或组合"
    };

    var createSpan= function($el , className){
        if( !$el.parent().parent().find('.'+className).length ){
            $el.parent().parent().append('<span class="'+ className +'"></span>');
        }
    };
    var getSpan=function( $el, className ){
        return $el.parent().parent().find('.' + className);
    };
    var getDesc= function($el){
        if( $el.attr('data-desc') )
            return $el.attr('data-desc');
        var types = $el.attr('uui-type');
        types = (types || '').split(' ');
        var type;
        _.forEach(types , function(value){
            if( value!= 'require' && !type && NormalDesc[value] )
                type = value;
        });
        return type? ( NormalDesc[type] || '' ) : '';
    };
    var getError = function($el , name , args){
        return ErrorDesc[name] && ErrorDesc[name]($el , args) || '';
    };

    var initToken = function($el){
        var token = utils.uuid();
        $el.find('.token').val(token);
        $el.find('.vpic img').attr('src' , "/captcha?token="+ token + '&t=' + +new Date());
    };


    var bindOptEvent = function($el){
        $el.find('.vpic img,.change-vpic').click(function(){
            $el.find('.vpic img').attr('src' , "/captcha?token="+ $el.find('.token').val() + '&t=' + +new Date());
            return false;
        });
        $el.click(function(){
            $el.find('.form-error,.form-success').hide();;
        });
    };

    return{
        render: function($el , config){
            config = config || {};
            $el.uuiForm({
                type:'blur',
                onfocus: function($el){
                    $el.parent().addClass('form-el-focus');
                    getSpan($el , 'error').hide();
                    var desc = getDesc($el);
                    if( desc && desc.length ){
                        createSpan($el,'desc');
                        getSpan($el , 'desc').show().html(desc);
                    }
                    
                },
                onblur: function($el){
                    $el.parent().removeClass('form-el-focus');
                    getSpan($el , 'desc').hide();
                },
                onsinglefail: function($el , name){
                    var args = name.split('(')[1];
                    name = name.split('(')[0];
                    args = args ? args.slice(0,-1).split(','):[];
                    var desc = getError($el , name , args);
                    if( desc && desc.length ){
                        createSpan($el,'error');
                        getSpan($el , 'desc').hide();
                        getSpan($el , 'error').show().html(desc);
                    }
                },
                onsinglesuccess: function($el,name){
                    getSpan($el,'error').hide();
                },
                onformsuccess: function($el){
                    if( !config.onbeforesubmit || config.onbeforesubmit($el) ){
                        $.post($el.attr('action'), $el.serialize() , function(data){
                            data = utils.parseResponse(data);
                            
                            if( !+data.status ){
                                $el.find('.form-success').show().find('span').html( data.statusText? data.statusText: '提交成功');
                                config.onsuccess && config.onsuccess($el , data);
                            }else{
                                var errorText = data.statusText ? data.statusText.split('|')[0]: '未知错误';
                                $el.find('.form-error').show().find('span').html(errorText);
                                config.onfailure && config.onfailure($el);
                            }
                        });
                    }
                    return false;
                },
                onformfail: function($el){
                    $el.find('.desc').hide();
                    config.onformfail && config.onformfail();
                    return false;
                }
            });
            $el.append('<input type="hidden" name="token" value="" class="token"/>');

            var data = {};
            try{
                data = $.evalJSON(server_data).data || {};
            }catch(e){window['console'] && console.log(e);}
            

            $el.append('<input name="client_id" value="'+ ( data.client_id? data.client_id : conf.client_id) +'" type="hidden"/>');
            
            $el.find('.form-btn').before('<div class="form-error"><span></span></div>');
            $el.find('.form-btn').before('<div class="form-success"><span></span></div>');
            
            initToken($el);
            bindOptEvent($el);
        },
        initTel: function(iptname){
            var tm,
                text = '秒后重新获取验证码',
                oldText,
                oldtimeout = 60,
                timeout = oldtimeout,
                status;

            $('.tel-valid-btn').click(function(){
                if(status)return;
                $('.main-content .form form').find('.tel-valid-error').hide();

                var usernameIpt = $('.main-content .form form input[name="'+ ( iptname?iptname: 'username' ) +'"]');
                if( usernameIpt && usernameIpt.length ){
                    var errorSpan = usernameIpt.parent().find('.error');
                    if( !$.trim(usernameIpt.val()).length ){
                        usernameIpt.blur();
                        return;
                    }
                    if( errorSpan.length && errorSpan.css('display') != 'none' )
                        return;
                }

                var captchaIpt = $('.main-content .form form input[name="code-captcha"]');
                status = true;
                var el = $(this),$form=el.parents('form');
                oldText = el.html();
                //el.html(timeout + text);
                //el.addClass('tel-valid-btn-disable');

                var url = el.attr('action') || '/web/sendsms';
                $.post(url , {
                    mobile: usernameIpt.val(),
                    new_mobile: usernameIpt.val(),
                    client_id: conf.client_id,
                    captcha:captchaIpt.val(),
                    t: +new Date(),
                    token:$('input[name=token]').val()
                } , function(data){
                    data = utils.parseResponse(data);

                    if( +data.status ){
                        
                        if(20257==data.status){
                            //显示验证码
                            $('.main-content .form form').find('.form-item-vcode').removeClass('hide');
                        }
                        else if( +data.status != 20201 ){
                            if(20221==data.status){
                                //验证码错误
                                initToken($form);
                            }
                            $('.main-content .form form').find('.tel-valid-error').show().html(data.statusText? data.statusText : '系统错误');;
                        }
                        resetBtn();
                    }else{
                        //Fixed by yinyong
                        el.addClass('tel-valid-btn-disable');
                        tm = setInterval(function() {
                            if (!--timeout) {
                                resetBtn();
                            } else {
                                el.html(timeout + text);

                            }

                        }, 1000);
                    }
                        
                });

                function resetBtn(){
                    el.html(oldText);
                    clearInterval(tm);
                    status = false;
                    timeout = oldtimeout;
                    el.removeClass('tel-valid-btn-disable');
                }

            });

        },
        showFormError: function(text){
            $('.main-content .form form').find('.form-error').show().find('span').html(text);;
        },
        freshToken:function($el){
            initToken($el);
        },
        initToken:function($el){
            initToken($el);
            bindOptEvent($el);
        }
    };
});

var __ssjs__ = typeof exports == 'undefined' ? false : true;
if (__ssjs__) {
    var Ursa = {varType:{}, escapeType:{}};
}
	
(function() {
    if (!__ssjs__) {
        if (typeof Ursa != 'undefined' && typeof Ursa.render != 'undefined') return;
        window.Ursa = window.Ursa || {varType:{}, escapeType:{}};
    };
	
	/*
		所有语法必须由 starter + type starter + statement + type ender + ender组成
	 */
	var config = {
			starter: '{',
			ender  : '}',
			commentStarter: '#',
			commentEnder: '#',
			opStarter: '{',
			opEnder: '}',    
			statementStarter: '%',
			statementEnder  : '%'
		}
		, starter = config.starter
		, ender   = config.ender
		, commentStarter = config.commentStarter
		, commentEnder = config.commentEnder
		, opStarter = config.opStarter
		, opEnder = config.opEnder
		, statementStarter = config.statementStarter
		, statementEnder   = config.statementEnder
		, endStartReg = new RegExp('[' + opEnder + commentEnder + statementEnder + ']', 'g');
		
	function setConfig(conf) {
		for(var i in conf) {
			if(config[i]) config[i] = conf[i];
		}
		starter = config.starter
		, ender   = config.ender
		, commentStarter = config.commentStarter
		, commentEnder = config.commentEnder
		, opStarter = config.opStarter
		, opEnder = config.opEnder
		, statementStarter = config.statementStarter
		, statementEnder   = config.statementEnder;
	};
    // filter and func area begin

    // func

    /**
     * 生成一个数组
     * @method func range
     *
     * @return [].
     * @param start 开始.
     * @param end   结束位置.
     * @param size  递增间隔.
     */
    function range(start, end, size) {
        var res = []
            , size = size || 1;
        if (start <= end) {
            while (start < end) {
                res.push(start);
                start += size * 1;
            }
        } else {
            while (start > end) {
                res.push(start);
                start = start - size;
            }
        }
        return res;
    };

    /**
     * 循环
     *
     * @method each
     * @param Array|Object range
     */
    function each(rge, callback) {
        if(rge instanceof Array) {
            for (var i = 0, len = rge.length; i < len; i++) {
                callback && callback(rge[i], i, i);
            }
        } else if(rge instanceof Object) {
            var index = 0;
            for (var key in rge) {
                if (typeof rge[key] != 'function') {
                    callback && callback(rge[key], key, index);
                    index++;
                }
            }
        }
    };

    /**
     * @method dumpError
     */
    function dumpError(code, tplString, pointer, matches) {
        var msg;
        switch(code) {
            case 1:  msg = '错误的使用了\\，行数:' + getLineNumber(tplString, pointer);break;
            case 2:  msg = '缺少结束符}"，行数:' + getLineNumber(tplString, pointer);break;
            case 3:  msg = '缺少"{","#"或者"%"，行数:' + getLineNumber(tplString, pointer);break;
            case 4:  msg = '未闭合的{，,行数:' + getLineNumber(tplString, pointer);break;
            case 5:  msg = '以下标签未闭合' + matches.join(',');break;
            case 6:  msg = '创建模板失败' + tplString;break;
            case 7:  msg = '缺少"' + matches.replace('end', '') + ',行数:' + getLineNumber(tplString, pointer);break;
            case 8: msg = '缺少结束符}' + tplString;break;
            default: msg = '出错了';break;
        }
        throw new Error(msg);
    };
    var __undefinded;
    /**
     *
     * @method clear whitespace
     */
    function cleanWhiteSpace(result) {
        result = result.replace(/\t/g,   "    ");
        result = result.replace(/\r\n/g, "\n");
        result = result.replace(/\r/g,   "\n");
        result = result.replace(/^(\s*\S*(\s+\S+)*)\s*$/, '$1'); // Right trim by Igor Poteryaev.
        return result;
    }
    /**
     * 获取循环的length
     *
     * @method _length
     * @return Number.
     * @param Object|Array rge
     */
    function _length(rge) {
        if (!rge) return 0;
        if (rge instanceof Array) return rge.length;
        var length = 0;
        each(rge, function(item, i, index) {
            length = index + 1;
        });
        return length;
    };
    /**
     * 变量是否在指定的rge内
     *
     * @method _jsIn
     * @param * key
     * @param rge rge
     */
    function _jsIn(key, rge) {
        if(!key || !rge) return false;
        if(rge instanceof Array) {
            for(var i = 0, len = rge.length; i < len; i++) {
                if(key == rge[i]) return true;
            }    
        }    
        try{
            return rge.match(key) ? true : false;    
        }catch(e) {
            return false;    
        }
    };

    /**
     * 判断变量是否是指定的类型
     *
     * @method _jsIs
     * @param vars 变量名
     * @param type 指定的类型
     */
    function _jsIs(vars, type, args3, args4) {
        switch(type) {
            case 'odd':return vars % 2 == 1;break;
            case 'even':return vars % 2 == 0;break;
            case 'divisibleby': return vars % args3 == 0;break;
            case 'defined': return typeof vars != 'undefined';break;
            default:if(Ursa.varType && Ursa.varType[type]) {
                return Ursa.varType[type].apply(null, arguments);
            } else {
                return false
            };
        }
    };
    
    function _trim(str) {
        return str ? (str + '').replace(/(^\s*)|(\s*$)/g, "") : '';
    };
     
    function _default(vars) {
        return vars;
    };
    
    function _abs(vars) {
        return Math.abs(vars);
    };
    
    function _format(vars) {
        if(!vars) return '';
        var placeHolder = vars.split(/%s/g);
        var str = ''
            , arg = arguments;
        each(placeHolder, function(item, key, i) {
            str += item + (arg[i + 1] ? arg[i + 1] : '');
        });
        return str;
    };
    
    function _join(vars, div) {
        if(!vars) return '';
        if(vars instanceof Array) return vars.join(typeof div != 'undefined' ? div : ',');
        return vars;
    };
    
    function _replace(str, replacer) {
        if(!str) return '';
        var str = str;
        each(replacer, function(value, key) {
            str = str.replace(new RegExp(key, 'g'), value);
        });
        return str;
    };
    
    function _slice(arr, start, length) {
        if(arr && arr.slice) {
            return arr.slice(start, start + length);
        } else {
            return arr;
        }
    };
    
    function _sort(arr) {
        if(arr && arr.sort) {
            arr.sort(function(a, b) {return a -b});
        }
        return arr;
    };
    
    function _escape(str, type) {
        if(typeof str == 'undefined' || str == null) return '';
        if(str && (str.safe == 1)) return str.str;
        var str = str.toString();
        // js
        if(type == 'js') return str.replace(/\'/g, '\\\'').replace(/\"/g, '\\"');
        // none
        if(type == 'none') return {str:str, safe: 1};
        
        if(Ursa.escapeType && Ursa.escapeType[type]) return Ursa.escapeType[type](str);
        // default is html
        return str.replace(/<|>/g, function(m){
            if(m == '<') return '&lt;';
            return '&gt;';
        })
    };
    // escape none
    function _raw(str) {
        return {
            safe: 1,
            str: str    
        }    
    };
    // 截取字符串
    function _truncate(str, len, killwords, end) {
        if(typeof str == 'undefined') return '';   
        var str = new String(str);
        var killwords = killwords || false;
        var end = typeof end == 'undefined' ? '...' : '';
        if(killwords) return (typeof len == 'undefined' ? str.substr(0, str.length) : str.substr(0, len) + (str.length <= len ? '' : end));
        return end;
    };
    function _substring(str, start, end) {
        if(typeof str == 'undefined') return '';   
        var str = new String(str);
        var end = typeof end != 'undefined' ? end : str.length;
        return str.substring(start, end);
    };
    function _upper(str) {
        if(typeof str == 'undefined') return '';   
        return new String(str).toUpperCase();
    };
    function _lower(str) {
        if(typeof str == 'undefined') return '';   
        return new String(str).toLowerCase();
    };
    // filter and function area end
    
    // cache tpl function
    Ursa._tpl = {};
    /**
     * 渲染模板
     *
     * @method render
     * @return html片段.
     * @param string tplName 模板名.
     * @param Object data 数据.
     * @param string [tplString] 模板源，可缺省.
     */
    Ursa.render = function(tplName, data, tplString) {
        if(!Ursa._tpl[tplName])Ursa.compile(tplString, tplName);
        return Ursa._tpl[tplName](data);
    };
    /**
     * 编译模板
     *
     * @method render
     * @return function 模板函数.
     * @param string tplString 模板源.
     * @param string [tplName] 模板名，可缺省.
     */
    Ursa.compile = function(tplString, tplName) {
        var str = SyntaxGetter(tplString);
        try{
            eval('Ursa._tpl["' + tplName + '"] = ' + str);
        } catch(e) {
            dumpError(6, e);
        }
        return Ursa._tpl[tplName];
    };

    var tags = '^(for|endfor|if|elif|else|endif|set)';
    var tagsReplacer = {
            'for': {
                'validate': /for[\s]+[^\s]+\sin[\s]+[\S]+/g,
                'pfixFunc': function(obj) {
                    var statement = obj.statement
                        // 形参
                        , args = statement.split(/[\s]+in[\s]+/g)[0]
                        , _args
                        , _value = _args
                        , _key = args
                        // 被循环的对象
                        , context = statement.replace(new RegExp('^' + args + '[\\s]+in[\\s]+', 'g'), '');
                    if(args.indexOf(',') != -1){
                        args = args.split(',');
                        if(args.length > 2) dumpError('多余的","在' + args.join(','), 'tpl');
                        _key = args[0];
                        _value = args[1];
                        _args = args.reverse().join(',');
                    } else {
                        _key = '_key';    
                        _value = args;
                        _args = args + ',' + '_key';
                    }
                    return '(function() {' +
                                'var loop = {' +
                                    'index:0,' +
                                    'index0:-1,' +
                                    'length: _length(' + context + ')' +
                                '}; ' +
                            'if(loop.length > 0) {' +
                                'each(' + context +', function(' + _args + ') {' + 
                                    'loop.index ++;' +
                                    'loop.index0 ++;' +
                                    'loop.key = ' + _key + ';' +
                                    'loop.value = ' + _value + ';' +
                                    'loop.first = loop.index0 == 0;' + 
                                    'loop.last = loop.index == loop.length;'
                }
            },
            'endfor': {
                'pfixFunc': function(obj, hasElse) {
                    // 是否存在forelse
                    return (hasElse ? '' : '})') + 
                        '}' + 
                        '})();' 
                }
            },
            'if': {
                'validate': /if[\s]+[^\s]+/g,
                'pfixFunc': function(obj) {
                    var statement = obj.statement;
                    var tests = compileOperator(statement);
                    return 'if(' + tests;
                },
                'sfix': ') {'
            },
            'elif': {
                'validate': /elif[\s]+[^\s]+/g,
                'pfixFunc': function(obj) {
                    var statement = obj.statement;
                    var tests = compileOperator(statement);
                    return '} else if(' + tests;
                },
                'sfix': ') {'
            },
            'else': {
                'pfixFunc': function(obj, start) {
                    // forelse
                    if(start == 'for') return  '})} else {';
                    return '} else {';
                } 
            },
            'endif': {
                'pfix': '}' 
            },
            'set': {
                'validate': /set[\s]+[^\s]+/g,
                'pfixFunc': function(obj) {
                    var statement = obj.statement;
                    var tests = compileOperator(statement);
                    return 'var ' + tests;
                },
                'sfix': ';' 
            }
        };

    var operator = '\\/\\/|\\*\\*|\\||in|is';
    var operatorReplacer = {
            '//': {
                'pfix': 'parseInt(',    
                'sfix': ')'
            },
            '**': {
                'pfixFunc': function() {
                    return 'Math.pow(';
                },
                'sfix': ')'    
            },
            '|': {
                'sfix': ')'   
            },
            'in': {
                'pfixFunc': function(vars) {
                    return  '_jsIn(((typeof ' + vars + ' != "undefined") ? ' + vars + ': __undefinded)';
                },
                'sfix': ')' 
            },
            'is': {
                'pfixFunc': function(vars) {
                    return '_jsIs(typeof ' + vars + ' != "undefined" ? ' + vars + ' : __undefinded';
                },
                'sfix': ')'                
            },
            'and': {
                'pfixFunc': function(obj) {
                    var statement = obj.statement;
                    return statement.replace(/[\s]*and[\s]*/g, ' && ');
                }    
            },
            'or': {
                'pfixFunc': function(obj) {
                    var statement = obj.statement;
                    return statement.replace(/[\s]*or[\s]*/g, ' || ');
                }      
            },
            'not': {
                'pfixFunc': function(obj) {
                    var statement = obj.statement;
                    return statement.replace(/[\s]*not[\s]*/g, '!');
                }    
            }
        };
    function merge(obj, opstatement, start) {
        return (obj.pfixFunc && obj.pfixFunc(opstatement, start) || obj.pfix || '') + (opstatement.sfix || obj.sfix || '');
    };
    //提取 is in的参数，后续不能包含空格
    function funcVars(str) {
        var str = str.replace(/\([\s]*\)/g,'').replace(/[\s\(]+/g, ',').replace(/\)$/g, '');
        var dot = str.indexOf(',');
        if(dot == -1) {
            str += '"';
        } else {
            str = str.substring(0, dot) + '"' + str.substring(dot);
        }
        return str;
    };
    function redoGetStrings(str, bark) {
        each(bark, function(value, key) {
            str = str.replace(new RegExp(key, 'g'), value);
        });
        return str;
    };
    
    function compileOperator(opstatement) {
        // 需要特别处理的操作符
        
        var reg = new RegExp('(^(not)|[\\s]+(and|or|not))[\\s]+', 'g')
                , matches;
        // not => not vars or not ()
        opstatement = opstatement.replace(/[^\s\(\)]+[\s]+is[\s]+not[\s]+[^\s\(\)]+(\([^\)]*\))?/g, function(m) {
            var vars = m.split(/[\s]+is[\s]+not/);
            var str = '!' + operatorReplacer['is']['pfixFunc'](vars[0]);
            vars.splice(0, 1);
            vars = funcVars(_trim(vars.join('')));
            return str + (vars ? ', "' + vars + '' : '') + operatorReplacer['is']['sfix']
        })
        // is => var is func(type) or var is not func(type)
        opstatement = opstatement.replace(/[^\s\(\)]+[\s]+is[\s]+[^\s\(\)]+(\([^\)]*\))?/g, function(m) {
            var vars = m.split(/[\s]+is[\s]+/);
            var str = operatorReplacer['is']['pfixFunc'](vars[0]);
            vars.splice(0, 1);
            vars = funcVars(_trim(vars.join('is')));
            return str + (vars ? ', "' + vars + '' : '') + operatorReplacer['is']['sfix']
        })
        // in => var in range() or var in "string"
        var vars = opstatement.match(/[^\s]+[\s]+in[\s]+[^\s]+/g);
        if(vars) {
            for(var i = 0, len = vars.length; i < len; i++) {
                var varName = vars[i].split(/[\s]+/g);
                // 获取in的操作对象range，range内不能出现空格
                var rge = varName[varName.length - 1];
                // get 变量名
                varName = varName[0];
                opstatement = opstatement.replace(vars[i], operatorReplacer['in'].pfixFunc(varName) + ',' + rge + operatorReplacer['in'].sfix);
            }
        }
        
        // 替换and or not
        opstatement = opstatement.replace(reg, function(m) {
            var m = _trim(m);
            if(m == 'not') return '!';
            if(m == 'and') return '&&';
            if(m == 'or') return '||';
        });
        return opstatement;
    };
    function output(source) {
        source = source.split('|');
        var str = compileOperator(source[0]);
        for(var i = 1, len = source.length; i< len; i ++) {
            var func = '_' + _trim(source[i]);
            var fs = func.split('(');
            var fname = _trim(fs[0]);
            fs.splice(0, 1);
            fs = _trim(fs.join('('));
            if(fname == '_default') {
                str = fname + '( typeof ' + str + ' == "undefined" ? ' + fs.replace(/\)$/g, '') + ' : ' + str + ')';
            } else {
                // 函数调用以(结束或者没有包含()
                str = fname + '(' + str + ((!fs || fs == ')')  ? ')' : ',' + fs);
            }
        }
        return '__output.push(_escape(' + str + '));';
    };
    // get error line number
    function getLineNumber(tplString, pointer) {
        return tplString ? (tplString.substr(0,pointer + 1).match(/\n/g) || []).length + 1 : 0; 
    }
    function setKeyV(obj, value) {
        var k = Math.random() * 100000 >> 0;
        while(!obj['__`begin`__' + k + '__`end`__']) {
            k ++;
            obj['__`begin`__' + k + '__`end`__'] = value;
        }
        return '__`begin`__' + k + '__`end`__';
    };
    /*
        转换产出
        将语法识别和编译替换拆分
     */
    // 模板头编译产物
    Ursa.ioStart = function() {
        return 'function (__context) {var __output = [];with(__context) {';
    };
    // 模板尾编译产物
    Ursa.ioEnd = function() {
        return '};return __output.join("");}';
    };
    // 模板html片段编译产物
    Ursa.ioHTML= function(ins) {
        return '__output.push("' + _escape(ins, 'js') + '");'
    };
    /*
     模板语法的编译需要完成对表达式内filter,function,and not in等操作符的编译替换
     */
    // 输出语句编译产物
    Ursa.ioOutput = function(ins) {
        return output(ins);
    };
    // 不包含tag的语句编译产物
    Ursa.ioOP = function(ins) {
        return compileOperator(ins) + ';'
    };
    // 包含tag的语句编译产物
    Ursa.ioMerge = function(matches, sourceObj, flag) {
        return merge(tagsReplacer[matches], sourceObj, flag);
    };
    /*
     end
     */
    Ursa.set = function(key, value) {
        Ursa[key] = value;
    };
    /**
     * 解析器
     *
     * @return function.
     * @param string tplString 模板源.
     */
	function SyntaxGetter(tplString) {
		var pointer = -1
			, tplString = cleanWhiteSpace(tplString)
			, character
			, stack = ''
			, statement = ''
			, endType = ''
			, tree = []
			, oldType
			, result = Ursa.ioStart()
			, tagStack = []
			, tagStackPointer = []
			, strDic = {}
			, type  = false;  

		while((character = tplString.charAt(++pointer)) != '') { 
			id = tagStackPointer.length;
			// 注释
			if(type == 3) {
				// 注释结束标记
				if(character == commentEnder) {
					character = tplString.charAt(++pointer);
					// 语法结束标记 
					if(character == ender) {
						type = false;
					}
				} 
				continue;
			}
			// 字符串常量
			if(type % 3 == 1 && (character == '\'' || character == '"')) {
				var start = tplString.charAt(pointer)
					, tmpStr = start;
				//stack.push(start);
				while((character = tplString.charAt(++pointer)) && (character != start)) {
					if(character == '\\') {
						tmpStr += '\\';
						character = tplString.charAt(++pointer);
					}
					tmpStr += character;
				}
				tmpStr += start;
				//stack += tmpStr;
				stack += setKeyV(strDic, tmpStr);
				//stack += '__string__';
			// 将非语句内的\一律当成字符串常量处理
			} else if(character == '\\') {   
				type = 2;
				stack += character + character;
				//character = tplString.charAt(++pointer);    
				//stack += character == '\\' ? character + character : character;
			// 语法起始符
			} else if(character == starter) {
				character = tplString.charAt(++pointer);
				oldType = type;
				switch(character) {
					case commentStarter: type = 3;break;
					case opStarter:      type = 4;break;
					case statementStarter:type = 1;break; 
					default:/*可能是字符串常量开始，回退一个字符*/stack += starter;if(character.match(/[\'\"]/g)) {pointer--;} else {stack += character};continue;break;
				}
				// 非语法出栈
				if(oldType == 2) {
					//tree.push(stack);
					result += Ursa.ioHTML(stack);
					stack = '';
				// 出错
				} else if(character == ender){
					//stack = ''; 
					//dumpError(2, tplString, pointer);
				}
			// 语法结束
			} else if(endType = character.match(endStartReg)) {
				// 结束标记起始，语句 or 输出
				endType = endType[0];
				if(type != 2) {
					character = tplString.charAt(++pointer); 
					// 语法结束
					if(character == ender) {
						// 输出结束
						if(endType == opEnder) {
							/*
							tree.push({
								type: 4,
								id: id + 1,
								v   : stack
							}) 
							*/
							result += Ursa.ioOutput(_trim(stack));
						// 语句
						} else {
							var start = tagStackPointer[tagStackPointer.length - 1]
								, matches
								, flag = start && start.type
								, source = _trim(stack)
								, id = 1;
							if((matches = source.match(tags))) {
								matches = matches[0];
								// 结束标签，出栈
								if(matches.indexOf('end') == 0) {
									id = tagStackPointer.length;
									// 主要为for服务，检查是否存在forelse
									flag = tagStack.splice(start.p, tagStack.length - start.p).length > 1;
									tagStackPointer.splice(tagStackPointer.length - 1, 1);
								// 需要进栈的标签
								} else if(matches != 'set') {
									tagStack.push(matches);
									if(matches == 'if' || matches == 'for') tagStackPointer.push({p: tagStack.length - 1, type: matches});
									id = tagStackPointer.length;
								}
								
								result += Ursa.ioMerge(matches, {statement: source.replace(new RegExp('^' + matches + '[\\s]*', 'g'), '')}, flag);
							} else {
								result += Ursa.ioOP(source);
							}
							/*
							tree.push({
								type: 1,
								id: id, 
								elif: flag,
								v:    stack    
							})    
							*/
						} 
						type = false;
						stack = '';
						continue;
					} else if(character.match(endStartReg)){
						pointer --;
						stack += endType;
						continue;
					} else {
						stack += endType + character;
					}
				} else {
					stack += endType;
				}
			} else {
				if(!type) {
					type = 2    
				}    
				stack += character;
			}
		}
		if(stack) {
			if(type == 2) {
				//tree.push(stack);    
				result += Ursa.ioHTML(stack); 
				stack = null;
			} else {
				// 出错    
				dumpError(8, stack);
			}
		}
		result += Ursa.ioEnd();
		// 标签未闭合，可以加个自动修复，哈哈
		if(tagStack.length) dumpError(5, tplString, pointer, tagStack);
		// 移除换行符，并反字符串转义
		return redoGetStrings(result.replace(/\n/g, ''), strDic);
	};
	Ursa.parse = SyntaxGetter;
	Ursa.setConfig = setConfig;
})();

if(__ssjs__) {
    exports.Ursa = Ursa;
} else {
    if(window['define']){
        define('Ursa',[],function(){
            return Ursa;
        });
    }
}
;
/*
 * tpl module script
 * @author zhengxin
*/
 



define('tpl',['./Ursa'] , function(Ursa){

    Ursa.setConfig({
        starter:'<',
        ender:'>'
    });

    return{
        render: function(tpl , data){
            return Ursa.render( + new Date() ,data, tpl );
        }
    };
});

/*
 * ui module script
 * @author zhengxin
*/
 



define('ui',[],function(){



    return{
        checkbox: function(el){
            el = $(el);
            var target = el.data('target');
            if( !target ) return;
            target = $('#' +target);
            var checkedClass = 'checkbox-checked';
            
            function doCheck(force){
                if( force ){
                    el.prop( 'checked' , el.prop('checked')? null:true );
                }
                var checked = el.prop('checked');
                checked ? target.addClass(checkedClass) :
                    target.removeClass(checkedClass);
            }

            el.click( function(){
                doCheck();
            });
            el.on('focus',function(){
                if( this.blur && window.attachEvent ){
                    this.blur();
                }
            });
            target.click(function(){
                doCheck(1);
            });
        }

    };
});

/*! http://mths.be/placeholder v2.0.7 by @mathias */
;(function(window, document, $) {

    var isInputSupported = 'placeholder' in document.createElement('input');
    var isTextareaSupported = 'placeholder' in document.createElement('textarea');
    var prototype = $.fn;
    var valHooks = $.valHooks;
    var propHooks = $.propHooks;
    var hooks;
    var placeholder;

    if (isInputSupported && isTextareaSupported) {

        placeholder = prototype.placeholder = function() {
            return this;
        };

        placeholder.input = placeholder.textarea = true;

    } else {

        placeholder = prototype.placeholder = function() {
            var $this = this;
            $this
                .filter((isInputSupported ? 'textarea' : ':input') + '[placeholder]')
                .not('.placeholder')
                .bind({
                    'focus.placeholder': clearPlaceholder,
                    'blur.placeholder': setPlaceholder
                })
                .data('placeholder-enabled', true)
                .trigger('blur.placeholder');
            return $this;
        };

        placeholder.input = isInputSupported;
        placeholder.textarea = isTextareaSupported;

        hooks = {
            'get': function(element) {
                var $element = $(element);

                var $passwordInput = $element.data('placeholder-password');
                if ($passwordInput) {
                    return $passwordInput[0].value;
                }

                return $element.data('placeholder-enabled') && $element.hasClass('placeholder') ? '' : element.value;
            },
            'set': function(element, value) {
                var $element = $(element);

                var $passwordInput = $element.data('placeholder-password');
                if ($passwordInput) {
                    return $passwordInput[0].value = value;
                }

                if (!$element.data('placeholder-enabled')) {
                    return element.value = value;
                }
                if (value == '') {
                    element.value = value;
                    // Issue #56: Setting the placeholder causes problems if the element continues to have focus.
                    if (element != document.activeElement) {
                        // We can't use `triggerHandler` here because of dummy text/password inputs :(
                        setPlaceholder.call(element);
                    }
                } else if ($element.hasClass('placeholder')) {
                    clearPlaceholder.call(element, true, value) || (element.value = value);
                } else {
                    element.value = value;
                }
                // `set` can not return `undefined`; see http://jsapi.info/jquery/1.7.1/val#L2363
                return $element;
            }
        };

        if (!isInputSupported) {
            valHooks.input = hooks;
            propHooks.value = hooks;
        }
        if (!isTextareaSupported) {
            valHooks.textarea = hooks;
            propHooks.value = hooks;
        }

        $(function() {
            // Look for forms
            $(document).delegate('form', 'submit.placeholder', function() {
                // Clear the placeholder values so they don't get submitted
                var $inputs = $('.placeholder', this).each(clearPlaceholder);
                setTimeout(function() {
                    $inputs.each(setPlaceholder);
                }, 10);
            });
        });

        // Clear placeholder values upon page reload
        $(window).bind('beforeunload.placeholder', function() {
            $('.placeholder').each(function() {
                this.value = '';
            });
        });

    }

    function args(elem) {
        // Return an object of element attributes
        var newAttrs = {};
        var rinlinejQuery = /^jQuery\d+$/;
        $.each(elem.attributes, function(i, attr) {
            if (attr.specified && !rinlinejQuery.test(attr.name)) {
                newAttrs[attr.name] = attr.value;
            }
        });
        return newAttrs;
    }

    function clearPlaceholder(event, value) {
        var input = this;
        var $input = $(input);
        if (input.value == $input.attr('placeholder') && $input.hasClass('placeholder')) {
            if ($input.data('placeholder-password')) {
                $input = $input.hide().next().show().attr('id', $input.removeAttr('id').data('placeholder-id'));
                // If `clearPlaceholder` was called from `$.valHooks.input.set`
                if (event === true) {
                    return $input[0].value = value;
                }
                $input.focus();
            } else {
                input.value = '';
                $input.removeClass('placeholder');
                input == document.activeElement && input.select();
            }
        }
    }

    function setPlaceholder() {
        var $replacement;
        var input = this;
        var $input = $(input);
        var id = this.id;
        if (input.value == '') {
            if (input.type == 'password') {
                if (!$input.data('placeholder-textinput')) {
                    try {
                        $replacement = $input.clone().attr({ 'type': 'text' });
                    } catch(e) {
                        $replacement = $('<input>').attr($.extend(args(this), { 'type': 'text' }));
                    }
                    $replacement
                        .removeAttr('name')
                        .data({
                            'placeholder-password': $input,
                            'placeholder-id': id
                        })
                        .bind('focus.placeholder', clearPlaceholder);
                    $input
                        .data({
                            'placeholder-textinput': $replacement,
                            'placeholder-id': id
                        })
                        .before($replacement);
                }
                $input = $input.removeAttr('id').hide().prev().attr('id', id).show();
                // Note: `$input[0] != input` now!
            }
            $input.addClass('placeholder');
            $input[0].value = $input.attr('placeholder');
        } else {
            $input.removeClass('placeholder');
        }
    }

}(this, document, jQuery));
define("lib/jquery.placeholder", function(){});

/*
 * reg module script
 * @author zhengxin
 */



define('reg', ['./common', './form', './conf', './utils', './tpl', './ui', './lib/jquery.placeholder'], function(common, form, conf, utils, ursa, ui) {

    var reg_data = {};

    var createSpan = function($el, className) {
        if (!$el.parent().parent().find('.' + className).length) {
            $el.parent().parent().append('<span class="' + className + '"></span>');
        }
    };
    var getSpan = function($el, className) {
        return $el.parent().parent().find('.' + className);
    };
    var errorUnames = {};
    var checkUsername = function($el, cb) {
        var ipt = $el.find('input[name="username"]');
        if (!ipt || !ipt.length) {
            cb && cb(0);
            return;
        }
        if (!ipt.val().length) {
            cb && cb(-1);
            return;
        }
        var errSpan = getSpan(ipt, 'error');

        if (errSpan && errSpan.length && errSpan.css('display') != 'none') {
            cb && cb(-1);
            return;
        }

        function showError(text) {
            createSpan(ipt, 'error');
            getSpan(ipt, 'error').show().html(text);
            getSpan(ipt, 'desc').hide();
        }

        if (errorUnames[ipt.val()]) {
            showError(errorUnames[ipt.val()]);
            cb && cb(1);
            return;
        }
        $.get('/web/account/checkusername', {
            username: ipt.val(),
            t: +new Date()
        }, function(data) {
            data = utils.parseResponse(data);
            if (!+data.status) { //success
                cb && cb(0);
            } else {
                showError(data.statusText);
                errorUnames[ipt.val()] = data.statusText;
                cb && cb(1);
            }
        });

    };


    var bindFormEvent = function(type) {
        var $el = $('.main-content .form form');

        form.render($el, {
            onformfail: function() {
                checkUsername($el);
                return false;
            },
            onbeforesubmit: function() {
                checkUsername($el, function(status) {
                    if (!status) {

                        var isdescchecked = $el.find('.form-desc input').prop('checked');
                        if (!isdescchecked) {
                            form.showFormError('请阅读协议');
                            return;
                        }

                        $.post($el.attr('action'), $el.serialize(), function(data) {
                            data = utils.parseResponse(data);
                            if (!+data.status) {
                                formsuccess[type] && formsuccess[type]($el, data);
                            } else {
                                if (+data.status == 20221) {
                                    var token = $el.find('.token').val();
                                    $el.find('.vpic img').attr('src', "/captcha?token=" + token + '&t=' + (+new Date()));
                                }
                                form.showFormError(data.statusText);
                            }
                        });
                    }
                    return false;
                });
            }
        });
        $el.append('<input type="hidden" name="ru" value="' + (reg_data.ru || '') + '" class="ru"/>');

        //$el.find(".nick-holder").placeholder();

        $el.find('input[name=username]').blur(function() {
            var errorspan = $(this).parent().parent().find('.error');
            if (!errorspan || !errorspan.length || errorspan.css('display') == 'none') {
                setTimeout(function() {
                    checkUsername($el);
                }, 100);
            }
        });

    };


    var formsuccess = {
        common: function($el, data) {
            var ru = data.data.ru;
            location.href = ru;
        },
        nick: function($el, data) {
            formsuccess.common($el, data);
        },
        tel: function($el, data) {
            formsuccess.common($el, data);
        },
        email: function($el, data) {
            $('.main-content .step').addClass('step2');
            $el.parent().html(ursa.render($('#Target3').html(), {
                sec_email: $el.find('input[name="username"]').val()
            }));
            common.bindJumpEmail();
            common.bindResendEmail(data);
        }
    };


    function initRemind(data) {
        $('#JumpTarget').html(data.email || '');
        common.bindJumpEmail();
        common.bindResendEmail(data);
    }

    if((new RegExp("[?&]client_id\=([^&]+)", "g").exec(location.href))[1]==1014){
        $('#closeMailTip').show();
    }

    return {
        init: function(type) {
            var server_data = {};
            try {
                server_data = $.evalJSON(_server_data);
                reg_data = server_data.data || {};
            } catch (e) {
                server_data = {};
                window['console'] && console.log(e);
            }


            common.addUrlCommon(reg_data);

            common.showBannerUnderLine();

            bindFormEvent(type);
            $('.nav').show();

            form.initTel();

            ui.checkbox('#ConfirmChb');

            if (type === 'remind') {
                initRemind(reg_data);
            } else if ('emailsuccess' === type) {
                var html = Ursa.render('x', {
                    data: server_data
                }, $('#result-tpl').html());
                $('#result-tpl').parent().html(html);

                if (server_data.status == 0 && server_data.data && server_data.data.ru) {
                    var count = 5;
                    setInterval(function() {
                        $('#time').text(count--);
                        if (!count) {
                            location.href = server_data.data.ru;
                        }
                    }, 1e3);
                }

            }
        }
    };
});