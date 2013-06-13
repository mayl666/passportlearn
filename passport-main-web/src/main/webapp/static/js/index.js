define('index' , ['./ui' , './utils'] , function(ui , utils){
    

    return {
        init: function(){
            ui.checkbox('#RemChb');
            $('#Login').append('<input type="hidden" name="token" value="'+ utils.uuid()  +'" class="token"/>');

            $('#Login').on('submit' , function(){
                $.post($(this).attr('action'), $(this).serialize() , function(data){
                    alert($.toJSON(data));
                });
                
                return false;
            });
        }
    };
});
