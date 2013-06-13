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

    return{
        init: function(){
            common.showBannerUnderLine();

            bindFormEvent();
            addFormItem();
        }
    };
});
