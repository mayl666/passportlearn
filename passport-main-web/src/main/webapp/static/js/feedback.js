
/*
 * form module script
 * @author zhengxin
*/
 



define('utils',[], function(){

    
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
 



define('common',['./utils'],function(utils){


    return{
        addUrlCommon: function(data){
            if( data.ru ){
                $('.main-content .nav li a,.banner li a').each(function(idx , item){
                    $(item).attr('href' , $(item).attr('href') + '?ru=' + encodeURIComponent(data.ru));
                });
            }

            if( data.client_id ){
                $('.main-content .nav li a,.banner li a').each(function(idx , item){
                    $(item).attr('href' , 
                                 $(item).attr('href') 
                                 + ($(item).attr('href').indexOf('?') == -1 ? '?' : '&')
                                 + 'client_id=' + data.client_id
                                );
                });
            }

        },
        showBannerUnderLine: function(){
            $('.banner ul').show();
            var currentBanner = $('.banner ul li.current');
            if( currentBanner.length ){
                $('.banner .underline').css('left' , currentBanner.position().left)
                    .css('width' , currentBanner.css('width'));
            }
        },
        parseHeader: function(data){
            $('#Header .username').html(decodeURIComponent(data.uniqname||data.username));
            if( data.username||data.uniqname ){
                $('#Header .info').show();
            }
        },
        bindJumpEmail: function(){
            $('#JumpToUrl').click(function(){
                if( $('#JumpTarget') ){
                    window.open( utils.getUrlByMail($('#JumpTarget').html()) );
                }
                return false;
            });
        },
        /**
         * [bindResendEmail description]
         * @author yinyong
         * @version 0.1
         */
        bindResendEmail:function(data){
            var self=this;
            var count=60;
            var inter=null;
            $('#ResendEmail').click(function(e){
                if(!inter){
                    var btn=this;
                    var time=count;
                
                    $.ajax({
                        url:"/web/resendActiveMail",
                        data:{
                            client_id:1120,
                            username:data.email
                        },
                        type:"post",
                        error:function(xhr,error){
                            alert("通信错误");
                        },success:function(){
                            inter = setInterval(function() {
                                $(btn).text(time--+"秒后重发");
                                if (!time) {
                                    clearInterval(inter);
                                    inter = null;
                                    $(btn).text("重发验证邮件");
                                }
                            }, 1000);
                        }
                    });
                }
                e.preventDefault();
            });
        }//bindResendEmail
    };
});

define('conf',[],function(){


    return{
        client_id:"1120",
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
                return !value.length || /^(\w)+(\.\w+)*@([\w_\-])+((\.\w+)+)$/.test(value);
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
        }, uniqname: function($el){
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
                status = true;
                var el = $(this);
                oldText = el.html();
                //el.html(timeout + text);
                //el.addClass('tel-valid-btn-disable');

                var url = el.attr('action') || '/web/sendsms';
                $.get(url , {
                    mobile: usernameIpt.val(),
                    new_mobile: usernameIpt.val(),
                    client_id: conf.client_id,
                    t: +new Date()
                } , function(data){
                    data = utils.parseResponse(data);
                    if( +data.status ){
                        if( +data.status != 20201 ){
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
        }
    };
});

/*
 * feedback module script
 * @author zhengxin
*/
 



define('feedback',['./common','./form' , './conf'] , function(common , form , conf){


    var bindFormEvent = function(){
        form.render($('.main-content .form form') , {
            onsuccess: function($el){
                $el.parent().html( $('#Target2').html() );
                
            }
        });
    };


    var addOptions = function(data){
        if( !data || !data.length )return;
        var select = $('.main-content .form form select')[0];
        for( var i=0,l=data.length; i<l; i++ ){
            var opt = new Option(data[i].typeName , data[i].id, false, false);
            select.options[select.options.length] = opt;
        }
    };

    return{
        init: function(){
            common.showBannerUnderLine();

            bindFormEvent();

            var data ={};
            try{
                data = $.evalJSON(server_data).data;
            }catch(e){window['console'] && console.log(e);}
            common.parseHeader(data);
            addOptions(data.problemTypeList);

        }
    };
});
