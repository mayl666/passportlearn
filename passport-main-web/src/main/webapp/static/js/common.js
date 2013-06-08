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
        }
    };
});
