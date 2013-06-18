/*
 * common module script
 * @author zhengxin
*/
 



define(function(){


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
            }
        }
    };
});
