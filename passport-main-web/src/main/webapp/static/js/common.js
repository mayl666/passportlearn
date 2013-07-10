/*
 * common module script
 * @author zhengxin
*/
 



define(['./utils'],function(utils){


    return{
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
