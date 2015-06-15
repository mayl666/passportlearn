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
