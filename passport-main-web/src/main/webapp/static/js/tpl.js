/*
 * tpl module script
 * @author zhengxin
*/
 



define(['./Ursa'] , function(Ursa){

    Ursa.setConfig({
        starter:'<',
        ender:'>'
    });

    return{
        render: function(tpl , data){
            return Ursa.render( + new Date() ,data, tpl );
        }
    };
});
