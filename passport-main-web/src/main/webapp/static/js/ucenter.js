/*
 * ucenter module script
 * @author zhengxin
*/
 



define('ucenter',['./common' , './tpl'] , function(common , ursa ){

    var pagefunc = {
        common: function(data){
            common.parseHeader(data);
        },
        disable: function(data){
            var tpl = $('#TargetDisable');
            var wrapper = $('#Target').parent();
            data.username=decodeURIComponent(data.username||"");
            wrapper.html( ursa.render(tpl.html() , data));
            //Stupid way to hide
            //fixme
            $($('.banner li')[2]).hide();
            $('.sidebar .ucenter-sidebar span.dynamic').hide();
            $('.sidebar .ucenter-sidebar .hr').hide();
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
            data.uniqname=decodeURIComponent(data.uniqname);
            data.username=decodeURIComponent(data.username||"");
            try {
                wrapper.html(ursa.render(tpl.html(), data));
            } catch (e) {
                alert("系统出错");
                return;
            }
            wrapper.find('.level-status b').css( 'width' , data.sec_score + '%' );

            if(data.avatarurl&&data.avatarurl.img_50){
                wrapper.find("#avatar").attr("src",data.avatarurl.img_50);
            }

            if( data.actype == 'phone' ){
                var span = $('.ucenter-sidebar a[href="/web/security/mobile"]').parent();
                span.hide();
                $(span.parent().find('.hr')[1]).hide();
            }
        }

    };


    return{
        init: function(type){
            common.showBannerUnderLine();

            var data ={};
            try{
                var _server_data=$.evalJSON(server_data);
                //yinyong#sogou-inc.com:validate status
                if (!_server_data) {throw 'server_data not found'}
                else if(+_server_data.status){
                    window['console']&&console.log('operation failed');
                }
                
                data = _server_data.data||data;
            }catch(e){window['console'] && console.log(e);}

            data.actype = data.actype || '';


            pagefunc.common(data);

            if( data.disable ){
                pagefunc.disable(data);
            }else{
                pagefunc[type] && pagefunc[type](data);
            }
            $('.sidebar .ucenter-sidebar').show();
        }
    };
});
