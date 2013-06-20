/*
 * common module script
 * @author zhengxin
*/
 



define(['./utils'],function(utils){


    return{
        showBannerUnderLine: function(){
            var currentBanner = $('.banner ul li.current');
            if( currentBanner.length ){
                $('.banner .underline').css('left' , currentBanner.position().left)
                    .css('width' , currentBanner.css('width'));
            }
        },
        parseHeader: function(data){
            $('#Header .username').html(data.username);
            if( data.username ){
                $('#Header .logout').show().prev().show();
                $('#Header .logout a').click(function(e){
                    utils.addIframe($(this).attr('href') , function(){
                        location.reload();
                    });
                    return false;
                });
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
