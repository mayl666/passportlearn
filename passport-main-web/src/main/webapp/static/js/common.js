/*
 * common module script
 * @author zhengxin
*/
 



define(['./utils'],function(utils){


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
            $('#Header .username').html(data.username);
            if( data.username ){
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
        }
    };
});
