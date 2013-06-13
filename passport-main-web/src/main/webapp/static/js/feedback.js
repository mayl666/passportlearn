/*
 * feedback module script
 * @author zhengxin
*/
 



define(['./common','./form'] , function(common , form){


    var bindFormEvent = function(){
        form.render($('.main-content .form form'));
    };

    var addFormItem = function(){
        $('.main-content .form form').append('<input name="client_id" value="1100" type="hidden"/>');
    };

    return{
        init: function(){
            common.showBannerUnderLine();

            bindFormEvent();
            addFormItem();
        }
    };
});
