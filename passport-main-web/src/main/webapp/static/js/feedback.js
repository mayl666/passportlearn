/*
 * feedback module script
 * @author zhengxin
*/
 



define(['./common','./form' , './conf'] , function(common , form , conf){


    var bindFormEvent = function(){
        form.render($('.main-content .form form'));
    };

    var addFormItem = function(){
        $('.main-content .form form').append('<input name="client_id" value="'+ conf.client_id +'" type="hidden"/>');
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
            addFormItem();

            var data ={};
            try{
                data = $.evalJSON(server_data);
            }catch(e){window['console'] && console.log(e);}

            addOptions(data);
        }
    };
});
