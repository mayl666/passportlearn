/*
 * recover module script
 * @author zhengxin
*/
 



define(['./common','./form' , './tpl'] , function(common , form , ursa){


    var pagefunc = {
        common: function(){},
        type: function(data){
            var tpl = $('#Target');
            tpl.parent().html( ursa.render(tpl.html() , data));

        },
        email: function(data){
            $('#EmailWrp').html(data.email);
        },
        sended: function(data){
            $('#EmailWrp').html(data.email);
        },
        reset:function(){
            form.render($('.form form'));
        },
        question: function(data){
            $('.form .form-text').html(data.question);
            $('.form').css('visibility' , '');
        }
    };


    return{
        init: function(type){
            common.showBannerUnderLine();
            
            var data ={};
            try{
                data = $.evalJSON(server_data).data;
            }catch(e){window['console'] && console.log(e);}
            
            pagefunc.common(data);

            pagefunc[type] && pagefunc[type](data);


            form.render($('.main-content .form form'));
        }
    };
});
