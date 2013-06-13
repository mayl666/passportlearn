/*
 * safe module script
 * @author zhengxin
*/
 



define(['./common' , './tpl' , './form'] , function(common , ursa , form ){

    var pagefunc = {
        common: function(data){
            $('#Header .username').html(data.username);
        },
        index: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            var last_login_time = new Date(data.last_login_time);
            data.time = {
                year: last_login_time.getFullYear(),
                month: last_login_time.getMonth()+1,
                day: last_login_time.getDate()
            };

            wrapper.html( ursa.render(tpl.html() , data));
            wrapper.find('.level-status b').css( 'width' , data.score + '%' );
            
        },
        password: function(){

        },
        email: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            wrapper.html( ursa.render(tpl.html() , data) );

            $('.form .binded a').click(function(){
                wrapper.html( ursa.render( $('#Target2').html() , {} ) );
                return false;
            });
        },
        tel: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            wrapper.html( ursa.render(tpl.html() , data) );
            $('.form .binded a').click(function(){
                wrapper.html( ursa.render( $('#Target2').html() , {} ) );
                
                $('#RebindStep1').on('submit' , function(){
                    wrapper.html( ursa.render( $('#Target3').html() , {} ) );
                    return false;
                });
                return false;
            });

        },
        question: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            wrapper.html( ursa.render(tpl.html() , {}) );
            var selfQuestion = wrapper.find('label[for=Question2Ipt]').parent();
            selfQuestion.hide();

            wrapper.find('select').on('change' , function(){
                if($(this).val() ==0){ //self question
                    selfQuestion.show();
                }else{
                    selfQuestion.hide();
                }
            });
        },
        history: function(data){
            var tpl = $('#Target');
            _.each(data.records , function(item){
                var time = new Date(item.time);
                item.time = {
                    year: time.getFullYear(),
                    month: time.getMonth()+1,
                    day: time.getDate(),
                    hour: time.getHours(),
                    minute: time.getMinutes(),
                    second:time.getSeconds()

                };
            });
            data.time = {
            };

            tpl.parent().html( ursa.render(tpl.html() , data));
        }
    };


    return{
        init: function(type){
            common.showBannerUnderLine();

            var data ={};
            try{
                data = $.evalJSON(server_data);
            }catch(e){window['console'] && console.log(e);}
            
            pagefunc.common(data);

            pagefunc[type] && pagefunc[type](data);

            form.render($('.main-content .form form'));

        }
    };
});
