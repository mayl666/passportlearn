/*
 * ui module script
 * @author zhengxin
*/
 



define(function(){



    return{
        checkbox: function(el){
            el = $(el);
            var target = el.data('target');
            if( !target ) return;
            target = $('#' +target);
            var checkedClass = 'checkbox-checked';
            
            function doCheck(force){
                if( force ){
                    el.prop( 'checked' , el.prop('checked')? null:true );
                }
                var checked = el.prop('checked');
                checked ? target.addClass(checkedClass) :
                    target.removeClass(checkedClass);
            }

            el.click( function(){
                doCheck();
            });
            target.click(function(){
                doCheck(1);
            });
        }

    };
});
