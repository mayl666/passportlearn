/*
 * form module script
 * @author zhengxin
*/
 



define( function(){

    
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
        addIframe: function(url){
            var iframe = document.createElement('iframe');
            iframe.src = url;
            
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
        }
    };

});
