package com.sogou.upd.passport.common.utils;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * 省份城市对应码测试类
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-4-14
 * Time: 下午4:06
 */
public class ProvinceAndCityUtilTest {


    public static Map<String, String> provinceMap = Maps.newHashMap();

    public static Map<String, String> cityMap = Maps.newHashMap();


    static {
        //省编码对照表
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
        provinceMap.put("990000", "其他国家");

        //市编码对照表
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


        //add by chengang 2014-04-14
        cityMap.put("150701", "呼伦贝尔");
        cityMap.put("150600", "鄂尔多斯");


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
        cityMap.put("211401", "锦西");
        cityMap.put("210601", "丹东");

        //add by chengang 2014-04-14
//        cityMap.put("211401", "葫芦岛");//城市码被 “锦西”占用


        //吉林省
        cityMap.put("220101", "长春");
        cityMap.put("220201", "吉林");
        cityMap.put("220301", "四平");
        cityMap.put("220401", "辽源");
        cityMap.put("220601", "浑江");
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
//        cityMap.put("320801", "淮安"); //暂注释，城市码被 "淮阴" 占用
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

        //江西
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


        //山东
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

        //湖北
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
        cityMap.put("430801", "大庸");
        cityMap.put("433001", "怀化");
        cityMap.put("433101", "吉首");
        cityMap.put("430201", "株洲");
        cityMap.put("430301", "湘潭");
        cityMap.put("430601", "岳阳");
        cityMap.put("430701", "常德");
        cityMap.put("432301", "益阳");
        cityMap.put("432501", "娄底");


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

        //海南
        cityMap.put("460100", "海口");
        cityMap.put("460200", "三亚");
        cityMap.put("460300", "三沙");

        //四川
        cityMap.put("510101", "成都");
        cityMap.put("513321", "康定");
        cityMap.put("513101", "雅安");
        cityMap.put("513229", "马尔康");
        cityMap.put("510301", "自贡");
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


        cityMap.put("500100", "重庆");
        cityMap.put("500239", "黔江土家族苗族自治县");
        cityMap.put("513200", "阿坝藏族羌族自治州");

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
        cityMap.put("632600", "果洛藏族自治州");
        cityMap.put("632100", "海东");

        //宁夏
        cityMap.put("640101", "银川");
        cityMap.put("640201", "石嘴山");
        cityMap.put("642101", "吴忠");
        cityMap.put("642221", "固原");


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


        //台湾
        cityMap.put("710001", "台北");
        cityMap.put("710002", "基隆");
        cityMap.put("710020", "台南");
        cityMap.put("710019", "高雄");
        cityMap.put("710008", "台中");


        cityMap.put("820000", "澳门");
        cityMap.put("810000", "香港");

        cityMap.put("990000", "国外");

    }


    public static Map<String, String> getProvinceMap() {
        return provinceMap;
    }


    public static Map<String, String> getCityMap() {
        return cityMap;
    }


    @Test
    public void testGetProvinceImmutableMap() {
        String provinceCode = "220000";
        Stopwatch time = new Stopwatch();
        time.start();
        Assert.assertNotNull(ProvinceAndCityUtil.getProvinceByPCode(provinceCode));
        System.out.println("use time :" + time.elapsedMillis());

        Assert.assertNotNull(ProvinceAndCityUtil.immutableProvinceMap.get(provinceCode));
        Assert.assertEquals("吉林", ProvinceAndCityUtil.immutableProvinceMap.get(provinceCode));
    }

    @Test
    public void testGetCityImmutableMap() {
        String cityCode = "142201";
        Assert.assertNotNull(ProvinceAndCityUtil.getCityByCityCode(cityCode));
        Assert.assertEquals("忻州", ProvinceAndCityUtil.getCityByCityCode(cityCode));
    }


    @Test
    public void testGetProvinceByBuilderPCode() {
        String provinceCode = "220000";
        Assert.assertNotNull(ProvinceAndCityUtil.getProvinceByBuilderPCode(provinceCode));
        Assert.assertNotNull(ProvinceAndCityUtil.builderProvinceMap.get(provinceCode));
        Assert.assertEquals("吉林", ProvinceAndCityUtil.builderProvinceMap.get(provinceCode));
    }

    @Test
    public void testGetCityByBuilderCCode() {
        String cityCode = "142201";
        Assert.assertNotNull(ProvinceAndCityUtil.getCityByBuilderCityCode(cityCode));
        Assert.assertNotNull(ProvinceAndCityUtil.builderCityMap.get(cityCode));
        Assert.assertEquals("忻州", ProvinceAndCityUtil.getCityByBuilderCityCode(cityCode));
    }

}
