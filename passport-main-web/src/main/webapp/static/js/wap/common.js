define( ['./utils'], function(Utils) {

	var passParamsStr = Utils.getPassThroughParams();

    $('.backlink').click(function(e) {
        e.preventDefault();
        history.back();
    });

    $('nav a').each(function(idx,item){
        var chref = $(item).attr('href');
        $(item).attr('href', chref.indexOf('?') == -1 ? (chref + '?' + passParamsStr)
                     : (chref + '&' + passParamsStr));
    });
    
    return{};
});
