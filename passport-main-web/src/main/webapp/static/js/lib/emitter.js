/**
  * emitter.js
  *
  * changelog
  * 2014-06-20[17:19:23]:created
  *
  * @info sogou-inc\yinyong,windows-x64,UTF-8,10.129.192.39,js,Y:\sogou-passport-front\static\js\lib
  * @author yanni4night@gmail.com
  * @version 0.0.1
  * @since 0.0.1
  */
define([],function(){
    function Emitter(){
        var listeners = {};

        this.on = function(evt,func,thisArg){
            if(!listeners[evt]){
                listeners[evt] = [];
            }

            listeners[evt].push({
                thisArg:thisArg,
                func:func,
                type:evt
            });

        };

        this.emit = function(evt,data){
            if(Array.isArray(listeners[evt])){
                listeners[evt].forEach(function(s){
                    s.func.call(s.thisArg||null,s,data);
                });
            }
        };
    };

    return Emitter;
});