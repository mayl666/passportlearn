/*
 * safe module script
 * @author zhengxin
*/
 



define('safe',['./common' , './tpl' , './form' , './conf' , './utils','./uuibase'] , function(common , ursa , form , conf , utils ){

    var pagefunc = {
        common: function(data){
            common.parseHeader(data);
            if( data.actype =='phone' ){
                $('.nav li.tel').hide();
            }
        },
        index: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            var last_login_time = new Date(+data.last_login_time);
            data.time = {
                year: last_login_time.getFullYear(),
                month: last_login_time.getMonth()+1,
                day: last_login_time.getDate()
            };
            if( +data.sec_score<25 ){
                data.sec_score_desc = '极低，强烈建议设置密保措施';
            }else if( +data.sec_score<50 ){
                data.sec_score_desc = '较低，建议设置多个密保措施';
            }else if( +data.sec_score<75 ){
                data.sec_score_desc = '适中，可以设置多个密保措施';
            }else{
                data.sec_score_desc = '较高，已设置多个密保措施';
            }

            wrapper.html( ursa.render(tpl.html() , data));
            wrapper.find('.level-status b').css( 'width' , data.sec_score + '%' );
            
        },
        password: function(){

        },
        email: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            wrapper.html( ursa.render(tpl.html() , data) );
            $('.form .binded a').click(function(){
                wrapper.html( ursa.render( $('#Target2').html() , {} ) );
                form.render($('.main-content .form form'),{
                    onsuccess: function(){
                        formsuccess.email($('.main-content .form form'));
                    }
                } );
                return false;
            });
        },
        emailsuccess: function(data){
            $('.binded .result').html(data.statusText || '未知错误');

            $('.binded .timing').show();
            $('.binded .long-btn').show();
            setInterval(function(){
                var el = $('#time');
                if( +el.html() <= 1 ){
                    location.href= '/';
                }
                el.html( el.html() - 1);
            },1000);
        },
        tel: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            wrapper.html( ursa.render(tpl.html() , data) );
            form.initTel('new_mobile');
            $('.form .binded a').click(function(){
                wrapper.html( ursa.render( $('#Target2').html() , data ) );
                
                form.render(wrapper.find('form') , {
                    onsuccess: function($el , data){
                        formsuccess && formsuccess.telcheck(wrapper , data);
                    }
                } );
                form.initTel('new_mobile');

                return false;
            });

        },
        question: function(data){
            var tpl = $('#Target');
            var wrapper = tpl.parent();
            wrapper.html( ursa.render(tpl.html() , {}) );
            var selfQuestion = wrapper.find('label[for=Question2Ipt]').parent();
            selfQuestion.hide();
            var selfInput = selfQuestion.find('input');
            var oldUuitype = selfInput.attr('uui-type');
            selfInput.attr('uui-type' , '');

            wrapper.find('select').on('change' , function(){
                if($(this).val() ==0){ //self question
                    selfQuestion.show();
                    selfInput.attr('uui-type' , oldUuitype);
                }else{
                    selfQuestion.hide();
                    selfInput.attr('uui-type' , '');
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
                    minute: utils.addZero(time.getMinutes(),2),
                    second:utils.addZero(time.getSeconds(),2)

                };
            });
            data.time = {
            };

            tpl.parent().html( ursa.render(tpl.html() , data));
        }
    };



    var formfunc = {
        question: function(){
            var wrapper = $('.main-content .form form');
            var newques = $('#NewQues');
            var qselect = wrapper.find('select');

            if( +qselect.val() ){
                newques.val( qselect.find( 'option[value='+ qselect.val() +']' ).html() );
            }else{
                newques.val( wrapper.find('input[name="question2"]').val() );
            }
        }
    };

    var formsuccess = {
        common: function(){
            setTimeout(function(){
                location.reload();
            }, 1000);
        },
        email: function($el){
            var mailIpt = $el.find('input[name="new_email"]');
            $el.parent().html( ursa.render($('#Target3').html() , {
                sec_email: mailIpt.val()
            }) );
            common.bindJumpEmail();

        },
        tel: function($el){
            var formaction = $el.attr('action');
            formsuccess.common();
        },
        telcheck: function($el,data){

            $el.html( ursa.render( $('#Target3').html() , {scode:data.data.scode} ) );
            form.render($el.find('form') , {
                onsuccess: function(){
                    setTimeout(function(){
                        location.reload();
                    } , 1000);
                }
            });
            form.initTel('new_mobile');
        }
    };

    var addUrlClientId = function(){
        var targets = $('.main-content .nav li a');
        targets.each(function(idx,item){
            $(item).attr('href' , $(item).attr('href') + '?client_id=' + conf.client_id);
        });
    };


    return{
        init: function(type){
            common.showBannerUnderLine();
            //addUrlClientId();

            var data ={};
            try{
                data = $.evalJSON(server_data).data;
            }catch(e){window['console'] && console.log(e);}

            data.actype = data.actype || '';

            pagefunc.common(data);

            pagefunc[type] && pagefunc[type](data);

            $('.nav').show();

            form.render($('.main-content .form form') , {
                onbeforesubmit: function(){
                    formfunc[type] && formfunc[type]();
                    return true;
                },
                onsuccess: function(el){
                    formsuccess[type] ? formsuccess[type](el) : formsuccess.common(el);
                }
            });

        }
    };
});
