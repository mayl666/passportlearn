/**
 * person.js
 *
 * This javascript file is for userinfo page and avatar-uploading page.
 * @author yinyong#sogou-inc.com
 * @version 0.0.1
 */

define("person", ['./common', './tpl', './form', './utils'], function (common, ursa, form, utils) {

    /**
     * Copied from zhengxin#sogou-inc.com.Not validated.For sync validating nickname.
     */
    var errorUnames = {};
    var createSpan = function ($el, className) {
        if (!$el.parent().parent().find('.' + className).length) {
            $el.parent().parent().append('<span class="' + className + '"></span>');
        }
    };
    var getSpan = function ($el, className) {
        return $el.parent().parent().find('.' + className);
    };
    var checkNickname = function ($el, cb) {
        var ipt = $el.find('input[name="nickname"]');
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
            showError(errorUnames[ipt.val()]); //FIXME
            cb && cb(1);
            return;
        }
        $.get('/web/userinfo/checknickname', {
            nickname: ipt.val(),
            client_id: $("[name='client_id']").val(),
            t: +new Date()
        }, function (data) {
            data = utils.parseResponse(data);
            if (!+data.status) { //success
                cb && cb(0);
            } else {
                showError(data.statusText);
                errorUnames[ipt.val()] = data.statusText; //FIXME
                cb && cb(1);
            }
        });

    };

    /**
     * Provinces and cities construction
     * A hash to pre-two letters should be defined for cities.
     */
    var _loc = function () {
        var obj = {};
        this.put = function (code, name) {
            if (code)
                obj[code] = name;
        };
        this.list = function (iterate) {
            for (var e in obj) {
                if (!obj.hasOwnProperty(e)) continue;
                iterate(e, obj[e]);
            }
        };
    };

    var cityMap = new _loc();
    var provinceMap = new _loc();
    //Really long code,copied from mayan#sogou-inc.com totally.
    (function () {
        provinceMap.put("110000", "北京");
        provinceMap.put("120000", "天津");
        provinceMap.put("130000", "河北");
        provinceMap.put("140000", "山西");
        provinceMap.put("150000", "内蒙古");
        provinceMap.put("210000", "辽宁");
        provinceMap.put("220000", "吉林");
        provinceMap.put("230000", "黑龙江");
        provinceMap.put("310000", "上海");
        provinceMap.put("320000", "江苏");
        provinceMap.put("330000", "浙江");
        provinceMap.put("340000", "安徽");
        provinceMap.put("350000", "福建");
        provinceMap.put("360000", "江西");
        provinceMap.put("370000", "山东");
        provinceMap.put("410000", "河南");
        provinceMap.put("420000", "湖北");
        provinceMap.put("430000", "湖南");
        provinceMap.put("440000", "广东");
        provinceMap.put("450000", "广西");
        provinceMap.put("460000", "海南");
        provinceMap.put("500000", "重庆");
        provinceMap.put("510000", "四川");
        provinceMap.put("520000", "贵州");
        provinceMap.put("530000", "云南");
        provinceMap.put("540000", "西藏");
        provinceMap.put("610000", "陕西");
        provinceMap.put("620000", "甘肃");
        provinceMap.put("630000", "青海");
        provinceMap.put("640000", "宁夏");
        provinceMap.put("650000", "新疆");
        provinceMap.put("710000", "台湾");
        provinceMap.put("810000", "香港");
        provinceMap.put("820000", "澳门");
        provinceMap.put("990000", "国外");
        //city
        cityMap.put("110100", "北京");

        cityMap.put("120100", "天津");

        //河北省
        cityMap.put("130101", "石家庄");
        cityMap.put("130201", "唐山");
        cityMap.put("130301", "秦皇岛");
        cityMap.put("130701", "张家口");
        cityMap.put("130801", "承德");
        cityMap.put("131001", "廊坊");
        cityMap.put("130401", "邯郸");
        cityMap.put("130501", "邢台");
        cityMap.put("130601", "保定");
        cityMap.put("130901", "沧州");
        cityMap.put("133001", "衡水");

        //山西省
        cityMap.put("140101", "太原");
        cityMap.put("140201", "大同");
        cityMap.put("140301", "阳泉");
        cityMap.put("140501", "晋城");
        cityMap.put("140601", "朔州");
        cityMap.put("142201", "忻州");
        cityMap.put("142331", "离石");
        cityMap.put("142401", "榆次");
        cityMap.put("142601", "临汾");
        cityMap.put("142701", "运城");
        cityMap.put("140401", "长治");
        //add by chengang 2014-04-14
        cityMap.put("140701", "晋中");
        cityMap.put("141101", "吕梁");


        //内蒙古
        cityMap.put("150101", "呼和浩特");
        cityMap.put("150201", "包头");
        cityMap.put("150301", "乌海");
        cityMap.put("152601", "集宁");
        cityMap.put("152701", "东胜");
        cityMap.put("152801", "临河");
        cityMap.put("152921", "阿拉善左旗");
        cityMap.put("150401", "赤峰");
        cityMap.put("152301", "通辽");
        cityMap.put("152502", "锡林浩特");
        cityMap.put("152101", "海拉尔");
        cityMap.put("152201", "乌兰浩特");
        //add by chengang 2014-04-14 补充
        cityMap.put("150701", "呼伦贝尔");
        cityMap.put("150600", "鄂尔多斯");
        cityMap.put("150801", "巴彦淖尔");
        cityMap.put("150901", "乌兰察布");

        //辽宁省
        cityMap.put("210101", "沈阳");
        cityMap.put("210201", "大连");
        cityMap.put("210301", "鞍山");
        cityMap.put("210401", "抚顺");
        cityMap.put("210501", "本溪");
        cityMap.put("210701", "锦州");
        cityMap.put("210801", "营口");
        cityMap.put("210901", "阜新");
        cityMap.put("211001", "辽阳");
        cityMap.put("211101", "盘锦");
        cityMap.put("211201", "铁岭");
        cityMap.put("211301", "朝阳");
        cityMap.put("211401", "锦西");//不属于地市级、属于“葫芦岛”的一个区
        cityMap.put("210601", "丹东");
        //add by chengang 2014-04-14
        cityMap.put("211400", "葫芦岛");


        //吉林省
        cityMap.put("220101", "长春");
        cityMap.put("220201", "吉林");
        cityMap.put("220301", "四平");
        cityMap.put("220401", "辽源");
        cityMap.put("220601", "白山");
        cityMap.put("220701", "松原");
        cityMap.put("222301", "白城");
        cityMap.put("222401", "延吉");
        cityMap.put("220501", "通化");


        //黑龙江
        cityMap.put("230101", "哈尔滨");
        cityMap.put("230301", "鸡西");
        cityMap.put("230401", "鹤岗");
        cityMap.put("230501", "双鸭山");
        cityMap.put("230701", "伊春");
        cityMap.put("230801", "佳木斯");
        cityMap.put("230901", "七台河");
        cityMap.put("231001", "牡丹江");
        cityMap.put("232301", "绥化");
        cityMap.put("230201", "齐齐哈尔");
        cityMap.put("230601", "大庆");
        cityMap.put("232601", "黑河");
        cityMap.put("232700", "加格达奇");

        cityMap.put("310100", "上海");


        //江苏省
        cityMap.put("320101", "南京");
        cityMap.put("320201", "无锡");
        cityMap.put("320301", "徐州");
        cityMap.put("320401", "常州");
        cityMap.put("320501", "苏州");
        cityMap.put("320600", "南通");
        cityMap.put("320701", "连云港");
        cityMap.put("320801", "淮阴");
        cityMap.put("320901", "盐城");
        cityMap.put("321001", "扬州");
        cityMap.put("321101", "镇江");
        //add by chengang 2014-04-14
        cityMap.put("321301", "宿迁");
        cityMap.put("320800", "淮安");
        cityMap.put("321201", "泰州");

        //浙江省
        cityMap.put("330101", "杭州");
        cityMap.put("330201", "宁波");
        cityMap.put("330301", "温州");
        cityMap.put("330401", "嘉兴");
        cityMap.put("330501", "湖州");
        cityMap.put("330601", "绍兴");
        cityMap.put("330701", "金华");
        cityMap.put("330801", "衢州");
        cityMap.put("330901", "舟山");
        cityMap.put("332501", "丽水");
        cityMap.put("332602", "临海");
        //add by chengang 2014-04-14
        cityMap.put("331001", "台州");

        //安徽省
        cityMap.put("340101", "合肥");
        cityMap.put("340201", "芜湖");
        cityMap.put("340301", "蚌埠");
        cityMap.put("340401", "淮南");
        cityMap.put("340501", "马鞍山");
        cityMap.put("340601", "淮北");
        cityMap.put("340701", "铜陵");
        cityMap.put("340801", "安庆");
        cityMap.put("341001", "黄山");
        cityMap.put("342101", "阜阳");
        cityMap.put("342201", "宿州");
        cityMap.put("342301", "滁州");
        cityMap.put("342401", "六安");
        cityMap.put("342501", "宣州");
        cityMap.put("342601", "巢湖");
        cityMap.put("342901", "贵池");
        //add by chengang 2014-04-14
        cityMap.put("341601", "亳州");
        cityMap.put("341701", "池州");


        //福建省
        cityMap.put("350101", "福州");
        cityMap.put("350201", "厦门");
        cityMap.put("350301", "莆田");
        cityMap.put("350401", "三明");
        cityMap.put("350501", "泉州");
        cityMap.put("350601", "漳州");
        cityMap.put("352101", "南平");
        cityMap.put("352201", "宁德");
        cityMap.put("352601", "龙岩");

        //江西省
        cityMap.put("360101", "南昌");
        cityMap.put("360201", "景德镇");
        cityMap.put("362101", "赣州");
        cityMap.put("360301", "萍乡");
        cityMap.put("360401", "九江");
        cityMap.put("360501", "新余");
        cityMap.put("360601", "鹰潭");
        cityMap.put("362201", "宜春");
        cityMap.put("362301", "上饶");
        cityMap.put("362401", "吉安");
        cityMap.put("362502", "临川");
        //add by chengang 2014-04-14
        cityMap.put("361001", "抚州");


        //山东省
        cityMap.put("370101", "济南");
        cityMap.put("370201", "青岛");
        cityMap.put("370301", "淄博");
        cityMap.put("370401", "枣庄");
        cityMap.put("370501", "东营");
        cityMap.put("370601", "烟台");
        cityMap.put("370701", "潍坊");
        cityMap.put("370801", "济宁");
        cityMap.put("370901", "泰安");
        cityMap.put("371001", "威海");
        cityMap.put("371100", "日照");
        cityMap.put("372301", "滨州");
        cityMap.put("372401", "德州");
        cityMap.put("372501", "聊城");
        cityMap.put("372801", "临沂");
        cityMap.put("372901", "菏泽");
        //add by chengang 2014-04-14
        cityMap.put("371201", "莱芜");


        //河南省
        cityMap.put("410101", "郑州");
        cityMap.put("410201", "开封");
        cityMap.put("410301", "洛阳");
        cityMap.put("410401", "平顶山");
        cityMap.put("410501", "安阳");
        cityMap.put("410601", "鹤壁");
        cityMap.put("410701", "新乡");
        cityMap.put("410801", "焦作");
        cityMap.put("410901", "濮阳");
        cityMap.put("411001", "许昌");
        cityMap.put("411101", "漯河");
        cityMap.put("411201", "三门峡");
        cityMap.put("412301", "商丘");
        cityMap.put("412701", "周口");
        cityMap.put("412801", "驻马店");
        cityMap.put("412901", "南阳");
        cityMap.put("413001", "信阳");


        //湖北省
        cityMap.put("420101", "武汉");
        cityMap.put("420201", "黄石");
        cityMap.put("420301", "十堰");
        cityMap.put("420400", "沙市");
        cityMap.put("420501", "宜昌");
        cityMap.put("420601", "襄樊");
        cityMap.put("420701", "鄂州");
        cityMap.put("420801", "荆门");
        cityMap.put("422103", "黄州");
        cityMap.put("422201", "孝感");
        cityMap.put("422301", "咸宁");
        cityMap.put("422421", "江陵");
        cityMap.put("422801", "恩施");
        //add by chengang 2014-04-14
        cityMap.put("421001", "荆州");
        cityMap.put("421101", "黄冈");
        cityMap.put("421301", "随州");


        //湖南
        cityMap.put("430101", "长沙");
        cityMap.put("430401", "衡阳");
        cityMap.put("430501", "邵阳");
        cityMap.put("432801", "郴州");
        cityMap.put("432901", "永州");
//        cityMap.put("430801", "大庸");
        cityMap.put("433001", "怀化");
        cityMap.put("433101", "吉首");
        cityMap.put("430201", "株洲");
        cityMap.put("430301", "湘潭");
        cityMap.put("430601", "岳阳");
        cityMap.put("430701", "常德");
        cityMap.put("432301", "益阳");
        cityMap.put("432501", "娄底");
        //add by chengang 2014-04-14
        cityMap.put("430801", "张家界");


        //广东省
        cityMap.put("440101", "广州");
        cityMap.put("440301", "深圳");
        cityMap.put("441501", "汕尾");
        cityMap.put("441301", "惠州");
        cityMap.put("441601", "河源");
        cityMap.put("440601", "佛山");
        cityMap.put("441801", "清远");
        cityMap.put("441901", "东莞");
        cityMap.put("440401", "珠海");
        cityMap.put("440701", "江门");
        cityMap.put("441201", "肇庆");
        cityMap.put("442001", "中山");
        cityMap.put("440801", "湛江");
        cityMap.put("440901", "茂名");
        cityMap.put("440201", "韶关");
        cityMap.put("440501", "汕头");
        cityMap.put("441401", "梅州");
        cityMap.put("441701", "阳江");
        // add by chengang 2014-04-14
        cityMap.put("445101", "潮州");
        cityMap.put("445201", "揭阳");
        cityMap.put("445301", "云浮");


        //广西
        cityMap.put("450101", "南宁");
        cityMap.put("450401", "梧州");
        cityMap.put("452501", "玉林");
        cityMap.put("450301", "桂林");
        cityMap.put("452601", "百色");
        cityMap.put("452701", "河池");
        cityMap.put("452802", "钦州");
        cityMap.put("450201", "柳州");
        cityMap.put("450501", "北海");
        //add by chengang 2014-04-14
        cityMap.put("450601", "防城港");
        cityMap.put("451101", "贺州");
        cityMap.put("451301", "来宾");
        cityMap.put("451401", "崇左");
        cityMap.put("450801", "贵港");

        cityMap.put("460100", "海口");
        cityMap.put("460200", "三亚");
        //yinyong#sogou-inc.com:A new city since 2012.
        cityMap.put("460300", "三沙");

        //四川
        cityMap.put("510101", "成都");
        cityMap.put("513321", "康定");
        cityMap.put("513101", "雅安");
        cityMap.put("513229", "马尔康");
        cityMap.put("510301", "自贡");
        cityMap.put("500100", "重庆");
        cityMap.put("512901", "南充");
        cityMap.put("510501", "泸州");
        cityMap.put("510601", "德阳");
        cityMap.put("510701", "绵阳");
        cityMap.put("510901", "遂宁");
        cityMap.put("511001", "内江");
        cityMap.put("511101", "乐山");
        cityMap.put("512501", "宜宾");
        cityMap.put("510801", "广元");
        cityMap.put("513021", "达县");
        cityMap.put("513401", "西昌");
        cityMap.put("510401", "攀枝花");
        //add by chengang 2014-04-14
        cityMap.put("511601", "广安");
        cityMap.put("511901", "巴中");
        cityMap.put("511401", "眉山");
        cityMap.put("512001", "资阳");

        cityMap.put("500239", "黔江土家族苗族自治县"); //K,too long

        //贵州省
        cityMap.put("520101", "贵阳");
        cityMap.put("520200", "六盘水");
        cityMap.put("522201", "铜仁");
        cityMap.put("522501", "安顺");
        cityMap.put("522601", "凯里");
        cityMap.put("522701", "都匀");
        cityMap.put("522301", "兴义");
        cityMap.put("522421", "毕节");
        cityMap.put("522101", "遵义");

        //云南
        cityMap.put("530101", "昆明");
        cityMap.put("530201", "东川");
        cityMap.put("532201", "曲靖");
        cityMap.put("532301", "楚雄");
        cityMap.put("532401", "玉溪");
        cityMap.put("532501", "个旧");
        cityMap.put("532621", "文山");
        cityMap.put("532721", "思茅");
        cityMap.put("532101", "昭通");
        cityMap.put("532821", "景洪");
        cityMap.put("532901", "大理");
        cityMap.put("533001", "保山");
        cityMap.put("533121", "潞西");
        cityMap.put("533221", "丽江纳西族自治县");
        cityMap.put("533321", "泸水");
        cityMap.put("533421", "中甸");
        cityMap.put("533521", "临沧");
        //add by chengang 2014-04-14
        cityMap.put("532500", "红河");
        cityMap.put("532800", "西双版纳傣族自治州");

        //西藏
        cityMap.put("540101", "拉萨");
        cityMap.put("542121", "昌都");
        cityMap.put("542200", "泽当镇");
        cityMap.put("542221", "乃东");
        cityMap.put("542301", "日喀则");
        cityMap.put("542421", "那曲");
        cityMap.put("542523", "噶尔");
        cityMap.put("542600", "八一镇");
        cityMap.put("542621", "林芝");

        //陕西
        cityMap.put("610101", "西安");
        cityMap.put("610201", "铜川");
        cityMap.put("610301", "宝鸡");
        cityMap.put("610401", "咸阳");
        cityMap.put("612101", "渭南");
        cityMap.put("612301", "汉中");
        cityMap.put("612401", "安康");
        cityMap.put("612501", "商州");
        cityMap.put("612601", "延安");
        cityMap.put("612701", "榆林");
        //add by chengang 2014-04-14
        cityMap.put("611001", "商洛");

        //甘肃
        cityMap.put("620101", "兰州");
        cityMap.put("620401", "白银");
        cityMap.put("620301", "金昌");
        cityMap.put("620501", "天水");
        cityMap.put("622201", "张掖");
        cityMap.put("622301", "武威");
        cityMap.put("622421", "定西");
        cityMap.put("622624", "成县");
        cityMap.put("622701", "平凉");
        cityMap.put("622801", "西峰");
        cityMap.put("622901", "临夏");
        cityMap.put("623027", "夏河");
        cityMap.put("620201", "嘉峪关");
        cityMap.put("622102", "酒泉");
        //add by chengang 2014-04-14
        cityMap.put("621001", "庆阳");
        cityMap.put("621201", "陇南");

        //青海省
        cityMap.put("630100", "西宁");
        cityMap.put("632121", "平安");
        cityMap.put("632221", "门源回族自治县");
        cityMap.put("632321", "同仁");
        cityMap.put("632521", "共和");
        cityMap.put("632621", "玛沁");
        cityMap.put("632721", "玉树");
        cityMap.put("632802", "德令哈");
        //add by chengang 2014-04-14
        cityMap.put("632100", "海东");

        //宁夏
        cityMap.put("640101", "银川");
        cityMap.put("640201", "石嘴山");
        cityMap.put("642101", "吴忠");
        cityMap.put("642221", "固原");
        //add by chengang 2014-04-14
        cityMap.put("640501", "中卫");

        //新疆
        cityMap.put("650101", "乌鲁木齐");
        cityMap.put("650201", "克拉玛依");
        cityMap.put("652101", "吐鲁番");
        cityMap.put("652201", "哈密");
        cityMap.put("652301", "昌吉");
        cityMap.put("652701", "博乐");
        cityMap.put("652801", "库尔勒");
        cityMap.put("652901", "阿克苏");
        cityMap.put("653001", "阿图什");
        cityMap.put("653101", "喀什");
        cityMap.put("653201", "和田");
        cityMap.put("654101", "伊宁");
        //add by chengang 2014-04-14
        cityMap.put("654301", "阿勒泰");
        cityMap.put("654202", "乌苏");
        cityMap.put("652302", "阜康");
        cityMap.put("654003", "奎屯");
        cityMap.put("654201", "塔城");
        cityMap.put("659004", "五家渠");
        cityMap.put("659003", "图木舒克");
        cityMap.put("659002", "阿拉尔市");
        cityMap.put("659001", "石河子");

        cityMap.put("710001", "台北");
        cityMap.put("710002", "基隆");
        cityMap.put("710020", "台南");
        cityMap.put("710019", "高雄");
        cityMap.put("710008", "台中");

        cityMap.put("820000", "澳门");

        cityMap.put("810000", "香港");
        //This is added by yinyong#sogou-inc.com:Every province item has to own one city item at least.
        cityMap.put("990000", "国外");
    })();


    var pagefunc = {
        common: function (data) {
            common.parseHeader(data);
            if (data.actype == 'phone') {
                $('.nav li.tel').hide();
            }
        },
        disable: function (data) {
            //Stupid way to hide
            //fixme
            $($('.banner li')[2]).hide();
            //$('.sidebar .ucenter-sidebar span.dynamic').hide();
            //$('.sidebar .ucenter-sidebar .hr').hide();
        },
        index: function (data) {
            var tpl = $('#Target');
            if (tpl.size()) {
                //This means the userinfo page.

                var wrapper = tpl.parent();
                wrapper.html(ursa.render(tpl.html(), data));
                var $el = $('.main-content .form form');

                /**
                 * I am against about validating on submit function,cause if the
                 * validating code crashed,the form may still submit,and the whole page jumps.
                 */
                form.render($el, {
                    onbeforesubmit: function () {

                        //As we have a sync validating,post here.

                        var year = $("#s-year").val();
                        var month = $("#s-month").val();
                        var day = $("#s-day").val();
                        var date = new Date(year, month - 1, day);//may be a invalid date
                        //Check whether the date is illegal
                        if (!(date.getFullYear() == year && date.getMonth() == month - 1 && date.getDate() == day)) {
                            return alert("日期不合法");
                        } else {
                            if (month < 10)month = "0" + String(month);
                            if (day < 10)day = "0" + String(day);
                            $("#birthday").val(year + "-" + month + "-" + day);//like 1987-01-01
                        }

                        $("#flag").val($("#NicknameIpt").val() == decodeURIComponent(data.uniqname) ? 0 : 1);

                        //Maybe city&province,gender,personalid should be validated again,
                        //as DOM could be modified.

                        //if nickname has never changed,do not check
                        if ($("#NicknameIpt").val() != decodeURIComponent(data.uniqname)) {
                            checkNickname($el, function (status) {
                                if (!+status) {
                                    $.post($el.attr('action'), $el.serialize(), function (data) {
                                        data = utils.parseResponse(data);
                                        if (!+data.status) {
                                            //Go to main page if saved successfully.
                                            window.location = "/";
                                            try {
                                                window.external.passport("onProfileChange");
                                            } catch (e) {
                                            }
                                        } else {
                                            form.showFormError(data.statusText);
                                        }
                                    });
                                }
                                return false;
                            });
                        } else {
                            //@todo
                            $.post($el.attr('action'), $el.serialize(), function (data) {
                                data = utils.parseResponse(data);
                                if (!+data.status) {
                                    //Go to main page if saved successfully.
                                    window.location = "/";
                                    try {
                                        window.external.passport("onProfileChange");
                                    } catch (e) {
                                    }
                                } else {
                                    form.showFormError(data.statusText);
                                }
                            });
                        }

                        return false; //No more submit
                    }
                    /*,
                     onsuccess: function(el) {
                     formsuccess[type] ? formsuccess[type](el) : formsuccess.common(el);
                     window.location="/";
                     }*/
                });

                //返回按钮
                $("button.back").click(function () {
                    history.back();
                });

                $el.find('input[name=nickname]').blur(function () {
                    if (data.uniqname == $(this).val())return;
                    var errorspan = $(this).parent().parent().find('.error');
                    if (!errorspan || !errorspan.length || errorspan.css('display') == 'none') {
                        setTimeout(function () {
                            checkNickname($el);
                        }, 100);
                    }
                });

                var birthday = (/\d{4}\-\d{1,2}\-\d{1,2}/.test(data.birthday)) ? data.birthday.split('-') : [1987, 0, 1];
                if (birthday.length != 3) birthday = [1987, 0, 1]; //Default:Thu Jan 01 1987 00:00:00 GMT+0900 (CST)

                var yearS = $("#s-year");
                var monthS = $("#s-month");
                var dayS = $("#s-day");

                var thisYear = new Date().getFullYear();
                //From 1900 AD. to this year
                for (var i = 1900; i <= thisYear; ++i) {
                    yearS.append("<option value=" + i + ">" + i + "</option>");
                }
                //Set input
                yearS.val(+birthday[0]);

                for (var i = 0; i <= 11; ++i) {
                    monthS.append("<option value=" + (1 + i ) + ">" + (i + 1) + "</option>");
                }
                //Sever offers the month at 1.Damn.
                monthS.val(+birthday[1]);

                //Note that Jan has 31 days,even not 1st or 1987.
                //If u wanna change the default month,u may need to change here.
                for (var i = 1; i <= 31; ++i) {
                    dayS.append("<option value=" + i + ">" + i + "</option>");
                }
                dayS.val(+birthday[2]);

                /**
                 * Reset the day selector.
                 * @param  {[interger]} countOfDays  u know
                 * @return {[undefined]}
                 * @todo Could be more efficient.
                 */
                var resetDay = function (countOfDays) {
                    var oldDay = dayS.val();//save the value
                    dayS.empty();
                    for (var i = 1; i < 1 + countOfDays; ++i)
                        dayS.append("<option value=" + i + ">" + i + "</option>");
                    dayS.val(oldDay);
                };

                //Because the count of days depends on the year and the month,
                //we have to calculate it when year and month change.   
                var calendarChange = function (e) {
                    var year = +yearS.val();
                    var month = +monthS.val();
                    if (isNaN(year) || !isFinite(year) || year < 1900) {
                        return (window['console'] && console.log("Illegal year"));
                    }
                    if (isNaN(month) || !isFinite(month) || month < 0 || month > 11) {
                        return (window['console'] && console.log("Illegal month"));
                    }

                    //Really need to calculate?
                    if (/1|3|5|7|8|10|12/.test(1 + month)) {
                        resetDay(31)
                    } else if (/4|6|9|11/.test(1 + month)) {
                        resetDay(30)
                    } else {
                        //Feb
                        if (year % 4 == 0 && year % 400 != 0) {
                            resetDay(29)
                        } else {
                            resetDay(28)
                        }
                    }

                };

                //Register change event
                yearS.change(calendarChange);
                monthS.change(calendarChange);

                //Show provinces
                provinceMap.list(function (code, name) {
                    $('#s-province').append("<option value='" + code + "'>" + name + "</option>");
                });
                /**
                 * Cause the list of cities depends on the current province,
                 * we have to refresh the list of cities when province changes.
                 *
                 * @todo Could be more efficient.
                 */
                var changeCities = function () {
                    $('#s-city').empty();
                    var provCode = $(this).val();
                    cityMap.list(function (code, name) {
                        //Note that the code of a city equals its province's code at first 2 letters!
                        //Not my invention.
                        if (provCode.slice(0, 2) == code.slice(0, 2))
                            $('#s-city').append("<option value='" + code + "'>" + name + "</option>");
                    });
                };

                //Set values to the inputs or selects.
                //I really doubt that whether the values should be validated.
                $("#s-province").val(data.province || "");
                $("#s-province").change(changeCities).trigger('change'); //show the list before set city value.
                $("#s-city").val(data.city || "");
                $("#FullnameIpt").val(data.fullname || "");
                $("#UniqnameIpt").val(decodeURIComponent(data.uniqname) || ""); //Uniqname or nickname?I cannot tell.
                $("#PersonalidIpt").val(data.personalid || ""); //Note:if the personalid is not empty,we think it not editable!
                if (data.personalid) {
                    $('#PersonalidIpt').parent('span').attr('class', 'form-text').empty().append("****************** 已验证");
                }
                $(".snick").text(decodeURIComponent(data.uniqname) || "");
                if (+data.gender === 1)
                    $(":radio[name='gender']").eq(1).prop('checked', true);
                else
                    $(":radio[name='gender']").eq(0).prop('checked', true);

            } else {
                window.as2js = function (msg) {
                    if ('goodluck' == msg) {
                        try {
                            window.external.passport("onProfileChange");
                        } catch (e) {
                        }
                        location.assign('/');
                    }
                };
                //avatar page,just show the flash
                $('#UploadPhoto').size() && require(["/./static/js/lib/jquery.flash.js"], function () {
                    $('#UploadPhoto').flash({
                        src: '/./static/swf/upjsp.swf',
                        width: 504,
                        height: 416,
                        flashvars: {
                            jurl: "/web/userinfo/uploadavatar?client_id=1120",
                            furl: "/index",
                            durl: "/"
                        }
                    });
                });
            }
        }
    };

    return {
        init: function () {

            //I'm confused about here,maybe zhengxin#sogou-inc.com knows more.

            common.showBannerUnderLine();
            $('.nav').show();

            var data = {};
            try {
                var _server_data = $.evalJSON(server_data);
                if (!_server_data) {
                    throw "server_data not found";
                }
                else if (+_server_data.status) {
                    window['console'] && console.log('operation failed');
                    //If status neq 0,uploading photo will be not accepted.
                    $('#UploadPhoto').replaceWith('<div class="avatar-not-support-sohu">暂时不支持sohu域内邮箱用户修改头像</div>');
                }

                data = _server_data.data || data;

                //trim
                for (var e in data) {
                    var it = data[e];
                    if (!data.hasOwnProperty(e))continue;
                    if (typeof it === 'string' || (it && it.constructor == String)) {
                        data[e] = it.replace(/^\s+|\s+$/g, '');
                    }
                }
                ;
            } catch (e) {
                window['console'] && console.log(e);
            }

            pagefunc.index(data);
            pagefunc.common(data);

            if (data && data.disable) {
                pagefunc.disable();
            }

        }
    };
});