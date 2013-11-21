define("person", ['./common', './tpl', './form', './utils'], function(common, ursa, form,utils) {

     var errorUnames = {};
    var createSpan = function($el, className) {
        if (!$el.parent().parent().find('.' + className).length) {
            $el.parent().parent().append('<span class="' + className + '"></span>');
        }
    };
    var getSpan = function($el, className) {
        return $el.parent().parent().find('.' + className);
    };
    var checkNickname = function($el, cb) {
        var ipt = $el.find('input[name="uniqname"]');
        if (!ipt || !ipt.length) {
            cb && cb(0);
            return;
        }
        if (!ipt.val().length) {
            cb && cb(-1);
            return;
        }
        var errSpan = getSpan(ipt, 'error');

        if (errSpan && errSpan.length && errSpan.css('display') != 'none') {
            cb && cb(-1);
            return;
        }

        function showError(text) {
            createSpan(ipt, 'error');
            getSpan(ipt, 'error').show().html(text);
            getSpan(ipt, 'desc').hide();
        }

        if (errorUnames[ipt.val()]) {
            showError(errorUnames[ipt.val()]);//FIXME
            cb && cb(1);
            return;
        }
        $.get('/web/userinfo/checknickname', {
            nickname: ipt.val(),
            client_id:$("[name='client_id']").val(),
            t: +new Date()
        }, function(data) {
            data = utils.parseResponse(data);
            if (!+data.status) { //success
                cb && cb(0);
            } else {
                showError(data.statusText);
                errorUnames[ipt.val()] = data.statusText;//FIXME
                cb && cb(1);
            }
        });

    };

    var city = {

        initCityList: function() {
            var tmp, cities, tmp2, cIds;

            var cityList = [];
            this.cityList = cityList;

            var Item = function(province, cities, cIds) {
                this.province = province;
                this.cities = cities;
                this.cIds = cIds;
            };

            cityList[0] = new Item('北京', ['北京'], ['110000']);
            cityList[1] = new Item('上海', ['上海'], ['310000']);
            cityList[2] = new Item('天津', ['天津'], ['120000']);
            cityList[3] = new Item('重庆', ['重庆'], ['500000']);

            tmp = '哈尔滨,齐齐哈尔,鸡西,鹤岗,双鸭山,大庆,伊春,佳木斯,七台河,牡丹江,黑河,绥化,大兴安岭';
            tmp2 = '230100,230200,230300,230400,230500,230600,230700,230800,230900,231000,231100,231200,232700';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[4] = new Item('黑龙江', cities, cIds);

            tmp = '长春,吉林,四平,辽源,通化,白山,松原,白城,延边';
            tmp2 = '220100,220200,220300,220400,220500,220600,220700,220800,222400';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[5] = new Item('吉林', cities, cIds);

            tmp = '沈阳,大连,鞍山,抚顺,本溪,丹东,锦州,营口,阜新,辽阳,盘锦,铁岭,朝阳,葫芦岛';
            tmp2 = '210100,210200,210300,210400,210500,210600,210700,210800,210900,211000,211100,211200,211300,211400';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[6] = new Item('辽宁', cities, cIds);

            tmp = '石家庄,唐山,秦皇岛,邯郸,邢台,保定,张家口,承德,沧州,廊坊,衡水';
            tmp2 = '130100,130200,130300,130400,130500,130600,130700,130800,130900,131000,131100';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[7] = new Item('河北', cities, cIds);

            tmp = '太原,大同,阳泉,长治,晋城,朔州,晋中,运城,忻州,临汾,吕梁';
            tmp2 = '140100,140200,140300,140400,140500,140600,140700,140800,140900,141000,141100';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[8] = new Item('山西', cities, cIds);

            tmp = '呼和浩特,包头,乌海,赤峰,通辽,鄂尔多斯,呼伦贝尔,巴彦淖尔,乌兰察布,兴安,锡林郭勒,阿拉善';
            tmp2 = '150100,150200,150300,150400,150500,150600,150700,150800,150900,152200,152500,152900';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[9] = new Item('内蒙古', cities, cIds);

            tmp = '济南,青岛,淄博,枣庄,东营,烟台,潍坊,济宁,泰安,威海,日照,莱芜,临沂,德州,聊城,滨州,菏泽';
            tmp2 = '370100,370200,370300,370400,370500,370600,370700,370800,370900,371000,371100,371200,371300,371400,371500,371600,371700';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[10] = new Item('山东', cities, cIds);

            tmp = '南京,无锡,徐州,常州,苏州,南通,连云港,淮安,盐城,扬州,镇江,泰州,宿迁';
            tmp2 = '320100,320200,320300,320400,320500,320600,320700,320800,320900,321000,321100,321200,321300';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[11] = new Item('江苏', cities, cIds);

            tmp = '杭州,宁波,温州,嘉兴,湖州,绍兴,金华,衢州,舟山,台州,丽水';
            tmp2 = '330100,330200,330300,330400,330500,330600,330700,330800,330900,331000,331100';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[12] = new Item('浙江', cities, cIds);

            tmp = '合肥,芜湖,蚌埠,淮南,马鞍山,淮北,铜陵,安庆,黄山,滁州,阜阳,宿州,巢湖,六安,亳州,池州,宣城';
            tmp2 = '340100,340200,340300,340400,340500,340600,340700,340800,341000,341100,341200,341300,341400,341500,341600,341700,341800';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[13] = new Item('安徽', cities, cIds);

            tmp = '南昌,景德镇,萍乡,九江,新余,鹰潭,赣州,吉安,宜春,抚州,上饶';
            tmp2 = '360100,360200,360300,360400,360500,360600,360700,360800,360900,361000,361100';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[14] = new Item('江西', cities, cIds);

            tmp = '广州,韶关,深圳,珠海,汕头,佛山,江门,湛江,茂名,肇庆,惠州,梅州,汕尾,河源,阳江,清远,东莞,中山,潮州,揭阳,云浮';
            tmp2 = '440100,440200,440300,440400,440500,440600,440700,440800,440900,441200,441300,441400,441500,441600,441700,441800,441900,442000,445100,445200,445300';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[15] = new Item('广东', cities, cIds);

            tmp = '南宁,柳州,桂林,梧州,北海,防城港,钦州,贵港,玉林,百色,贺州,河池,来宾,崇左';
            tmp2 = '450100,450200,450300,450400,450500,450600,450700,450800,450900,451000,451100,451200,451300,451400';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[16] = new Item('广西', cities, cIds);

            tmp = '郑州,开封,洛阳,平顶山,安阳,鹤壁,新乡,焦作,濮阳,许昌,漯河,三门峡,南阳,商丘,信阳,周口,驻马店';
            tmp2 = '410100,410200,410300,410400,410500,410600,410700,410800,410900,411000,411100,411200,411300,411400,411500,411600,411700';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[17] = new Item('河南', cities, cIds);

            tmp = '武汉,黄石,十堰,宜昌,襄樊,鄂州,荆门,孝感,荆州,黄冈,咸宁,随州,恩施';
            tmp2 = '420100,420200,420300,420500,420600,420700,420800,420900,421000,421100,421200,421300,422800';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[18] = new Item('湖北', cities, cIds);

            tmp = '长沙,株洲,湘潭,衡阳,邵阳,岳阳,常德,张家界,益阳,郴州,永州,怀化,娄底,湘西';
            tmp2 = '430100,430200,430300,430400,430500,430600,430700,430800,430900,431000,431100,431200,431300,433100';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[19] = new Item('湖南', cities, cIds);

            tmp = '福州,厦门,莆田,三明,泉州,漳州,南平,龙岩,宁德';
            tmp2 = '350100,350200,350300,350400,350500,350600,350700,350800,350900';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[20] = new Item('福建', cities, cIds);

            tmp = '海口,三亚';
            tmp2 = '460100,460200';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[21] = new Item('海南', cities, cIds);

            tmp = '乌鲁木齐,克拉玛依,吐鲁番,哈密,昌吉,博尔塔拉,巴音郭楞,阿克苏,克孜勒苏,喀什,和田,伊犁,塔城,阿勒泰';
            tmp2 = '650100,650200,652100,652200,652300,652700,652800,652900,653000,653100,653200,654000,654200,654300';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[22] = new Item('新疆', cities, cIds);

            tmp = '银川,石嘴山,吴忠,固原,中卫';
            tmp2 = '640100,640200,640300,640400,640500';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[23] = new Item('宁夏', cities, cIds);

            tmp = '西宁,海东,海北,黄南,海南,果洛,玉树,海西';
            tmp2 = '630100,632100,632200,632300,632500,632600,632700,632800';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[24] = new Item('青海', cities, cIds);

            tmp = '兰州,嘉峪关,金昌,白银,天水,武威,张掖,平凉,酒泉,庆阳,定西,陇南,临夏,甘南';
            tmp2 = '620100,620200,620300,620400,620500,620600,620700,620800,620900,621000,621100,621200,622900,623000';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[25] = new Item('甘肃', cities, cIds);

            tmp = '西安,铜川,宝鸡,咸阳,渭南,延安,汉中,榆林,安康,商洛';
            tmp2 = '610100,610200,610300,610400,610500,610600,610700,610800,610900,611000';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[26] = new Item('陕西', cities, cIds);

            tmp = '成都,自贡,攀枝花,泸州,德阳,绵阳,广元,遂宁,内江,乐山,南充,眉山,宜宾,广安,达州,雅安,巴中,资阳,阿坝,甘孜,凉山';
            tmp2 = '510100,510300,510400,510500,510600,510700,510800,510900,511000,511100,511300,511400,511500,511600,511700,511800,511900,512000,513200,513300,513400';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[27] = new Item('四川', cities, cIds);

            tmp = '贵阳,六盘水,遵义,安顺,铜仁,黔西南,毕节,黔东,黔南';
            tmp2 = '520100,520200,520300,520400,522200,522300,522400,522600,522700';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[28] = new Item('贵州', cities, cIds);

            tmp = '昆明,曲靖,玉溪,保山,昭通,丽江,普洱,临沧,楚雄,红河,文山,西双版纳,大理,德宏,怒江,迪庆';
            tmp2 = '530100,530300,530400,530500,530600,530700,530800,530900,532300,532500,532600,532800,532900,533100,533300,533400';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[29] = new Item('云南', cities, cIds);

            tmp = '拉萨,昌都,山南,日喀则,那曲,阿里,林芝';
            tmp2 = '540100,542100,542200,542300,542400,542500,542600';
            cIds = tmp2.split(',');
            cities = tmp.split(',');
            cityList[30] = new Item('西藏', cities, cIds);
        },
        getProvs: function() {
            return this.cityList;
        },
        getCityByProv: function(provId) {
            for (var i = 0; i < this.cityList.length; i++) {
                if(this.cityList[i].cIds[0]==provId)
                    return  this.cityList[i];
            };
          return [];
       }
    };

    var pagefunc = {
        common: function(data) {
            common.parseHeader(data);
            if (data.actype == 'phone') {
                $('.nav li.tel').hide();
            }
        },
        index: function(data) {
            var tpl = $('#Target');
            if (tpl.size()) { //index

                var wrapper = tpl.parent();
                wrapper.html(ursa.render(tpl.html(), data));
                var $el=$('.main-content .form form');

                form.render($el, {
                    onbeforesubmit: function() {

                        $("#birthday").val($("#s-year").val() + "_" + $("#s-month").val() + "_" + $("#s-day").val());

                        checkNickname($el, function(status) {
                            if (!+status) {
                                $.post($el.attr('action'), $el.serialize(), function(data) {
                                    data = utils.parseResponse(data);
                                    if (!+data.status) {
                                        formsuccess[type] && formsuccess[type]($el, data);
                                    } else {
                                        form.showFormError(data.statusText);
                                    }
                                });
                            }
                            return false;
                        });


                        return false;
                    },
                    onsuccess: function(el) {
                        formsuccess[type] ? formsuccess[type](el) : formsuccess.common(el);
                    }
                });
                
                var birthday=(/\d{4}_\d{1,2}_\d{1,2}/.test(data.birthday))?data.birthday.split('_'):[1987,1,1];
                if(birthday.length!=3)birthday=[1987,1,1];

                var yearS=$("#s-year");
                var monthS=$("#s-month");
                var dayS=$("#s-day");
                 
                for (var i = 1900; i <= new Date().getFullYear(); ++i) {
                    yearS.append("<option value=" + i + ">" + i + "</option>");
                }
                yearS.val(birthday[0]);

                for (var i = 1; i <= 12; ++i) {
                    monthS.append("<option value=" + i + ">" + i + "</option>");
                }
                monthS.val(birthday[1]);

                for (var i = 1; i <= 31; ++i) {
                    dayS.append("<option value=" + i + ">" + i + "</option>");
                }
                dayS.val(birthday[2]);


                var calendarChange = function(e) {
                    var year = +yearS.val();
                    var month = +monthS.val();
                    if (isNaN(year) || year < 1900) {
                        return (window['console'] && console.log("Illegal year"));
                    }
                    if (isNaN(month) || month < 1 || month > 12) {
                        return (window['console'] && console.log("Illegal month"));
                    }
                    if (/1|3|5|7|8|10|12/.test(month)) {
                        var oldDay = yearS.val();
                        dayS.empty();
                        for (var i = 1; i < 32; ++i)
                            dayS.append("<option value=" + i + ">" + i + "</option>");
                        dayS.val(oldDay);
                    } else if (/4|6|9|11/.test(month)) {
                        var oldDay = dayS.val();
                        dayS.empty();
                        for (var i = 1; i < 31; ++i)
                            dayS.append("<option value=" + i + ">" + i + "</option>");
                        dayS.val(oldDay);
                    } else {
                        //Feb
                        if (year % 4 == 0 && year % 400 != 0) {
                            var oldDay = dayS.val();
                            dayS.empty();
                            for (var i = 1; i < 30; ++i)
                                dayS.append("<option value=" + i + ">" + i + "</option>");
                            dayS.val(oldDay);
                        } else {
                            var oldDay = dayS.val();
                            dayS.empty();
                            for (var i = 1; i < 29; ++i)
                                dayS.append("<option value=" + i + ">" + i + "</option>");
                            dayS.val(oldDay);
                        }
                    }

                };

                yearS.change(calendarChange);
                monthS.change(calendarChange);

                //City
                city.initCityList();

                var provs = city.getProvs();
                for (var i = 0; i < provs.length; ++i) {
                    $('#s-province').append("<option value='" + provs[i].cIds[0]+ "'>" + provs[i].province + "</option>");
                }

                var changeCities = function() {
                    var currProvIndex = $(this).val();
                    var prov = city.getCityByProv(+currProvIndex);
                    $("#s-city").empty();
                    for (var i = 0; i < prov.cities.length; ++i) {
                        $("#s-city").append("<option value='" + prov.cIds[i]+ "'>" + prov.cities[i] + "</option>");
                    }
                };

                $("#s-province").change(changeCities).trigger('change');
                
                //Set values
                $("#s-province").val(data.province||"");
                $("#s-city").val(data.city||"");
                $("#UsernameIpt").val(data.fullname||"");
                $("#PersonalidIpt").val(data.personalId||"");
                $(":radio[name='gender']").eq(+data.gender||0).prop('checked',true);

            } else {
                //avatar
                require(["/./static/js/lib/jquery.flash.js"],function(){
                    $('#UploadPhoto').flash({
                        src: '/./static/swf/upjsp.swf',
                        width: 504,
                        height: 416,
                        flashvars: {
                            jurl: "/web/userinfo/uploadPortrait?username=" ,//+ PassportSC.cookieHandle(),
                            furl: "/index"
                        }
                    });
                });
            }
        }
    };

    return {
        init: function() {
            common.showBannerUnderLine();
            $('.nav').show();

            var data = {};
            try {
                data = $.evalJSON(server_data).data;
            } catch (e) {
                window['console'] && console.log(e);
            }

            pagefunc.index(data);

        }
    };
});