package com.sogou.upd.passport.common.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 省份城市对应表
 * User: liuling
 * Date: 13-9-4
 * Time: 下午12:13
 * To change this template use File | Settings | File Templates.
 */
public class ProvinceAndCityUtil {

    //省份映射信息
    public static final Map<String, String> immutableProvinceMap = ImmutableMap.copyOf(initProvinceMap());
    //省份映射信息，双向Map
    public static final BiMap<String, String> inverseProvinceMap = HashBiMap.create(initProvinceMap()).inverse();

    //城市映射信息
    public static final Map<String, String> immutableCityMap = ImmutableMap.copyOf(initCityMap());
    //城市映射信息，双向Map
    public static final BiMap<String, String> inverseCityMap = HashBiMap.create(initCityMap()).inverse();

//    public static final Map<String, String> builderProvinceMap = ImmutableMap.<String, String>builder().putAll(initProvinceMap()).build();
//    public static final Map<String, String> builderCityMap = ImmutableMap.<String, String>builder().putAll(initCityMap()).build();


    /**
     * 初始化省份映射数据
     *
     * @return
     */
    private static Map<String, String> initProvinceMap() {
        Map<String, String> initProvinceMap = Maps.newHashMap();
        //省编码对照表
        initProvinceMap.put("110000", "北京");
        initProvinceMap.put("120000", "天津");
        initProvinceMap.put("130000", "河北");
        initProvinceMap.put("140000", "山西");
        initProvinceMap.put("150000", "内蒙古");
        initProvinceMap.put("210000", "辽宁");
        initProvinceMap.put("220000", "吉林");
        initProvinceMap.put("230000", "黑龙江");
        initProvinceMap.put("310000", "上海");
        initProvinceMap.put("320000", "江苏");
        initProvinceMap.put("330000", "浙江");
        initProvinceMap.put("340000", "安徽");
        initProvinceMap.put("350000", "福建");
        initProvinceMap.put("360000", "江西");
        initProvinceMap.put("370000", "山东");
        initProvinceMap.put("410000", "河南");
        initProvinceMap.put("420000", "湖北");
        initProvinceMap.put("430000", "湖南");
        initProvinceMap.put("440000", "广东");
        initProvinceMap.put("450000", "广西");
        initProvinceMap.put("460000", "海南");
        initProvinceMap.put("500000", "重庆");
        initProvinceMap.put("510000", "四川");
        initProvinceMap.put("520000", "贵州");
        initProvinceMap.put("530000", "云南");
        initProvinceMap.put("540000", "西藏");
        initProvinceMap.put("610000", "陕西");
        initProvinceMap.put("620000", "甘肃");
        initProvinceMap.put("630000", "青海");
        initProvinceMap.put("640000", "宁夏");
        initProvinceMap.put("650000", "新疆");
        initProvinceMap.put("710000", "台湾");
        initProvinceMap.put("810000", "香港");
        initProvinceMap.put("820000", "澳门");
        initProvinceMap.put("990000", "其他国家");
        return initProvinceMap;
    }


    /**
     * 初始化城市映射数据信息
     *
     * @return
     */
    private static Map<String, String> initCityMap() {

        Map<String, String> initCityMap = Maps.newHashMap();
        //市编码对照表
        initCityMap.put("110100", "北京");
        initCityMap.put("120100", "天津");

        //河北省  11地级市-石家庄、唐山、邯郸、秦皇岛、保定、张家口、承德、廊坊、沧州、衡水、邢台
        initCityMap.put("130101", "石家庄");
        initCityMap.put("130201", "唐山");
        initCityMap.put("130301", "秦皇岛");
        initCityMap.put("130701", "张家口");
        initCityMap.put("130801", "承德");
        initCityMap.put("131001", "廊坊");
        initCityMap.put("130401", "邯郸");
        initCityMap.put("130501", "邢台");
        initCityMap.put("130601", "保定");
        initCityMap.put("130901", "沧州");
        initCityMap.put("133001", "衡水");

        //山西省 11地级市-太原、大同、忻州、阳泉、长治、晋城、朔州、晋中、运城、临汾、吕梁
        initCityMap.put("140101", "太原");
        initCityMap.put("140201", "大同");
        initCityMap.put("140301", "阳泉");
        initCityMap.put("140501", "晋城");
        initCityMap.put("140601", "朔州");
        initCityMap.put("142201", "忻州");
        initCityMap.put("142331", "离石"); //不属于地级市 属于“吕梁”的一个区
        initCityMap.put("142401", "榆次"); //不属于地级市 属于“晋中”的一个区
        initCityMap.put("142601", "临汾");
        initCityMap.put("142701", "运城");
        initCityMap.put("140401", "长治");

        //add by chengang 2014-04-14
        initCityMap.put("140701", "晋中");
        initCityMap.put("141101", "吕梁");


        //内蒙古  9地级市-呼和浩特、包头、乌海、赤峰、通辽、鄂尔多斯、呼伦贝尔、巴彦淖尔、乌兰察布
        initCityMap.put("150101", "呼和浩特");
        initCityMap.put("150201", "包头");
        initCityMap.put("150301", "乌海");
        initCityMap.put("152601", "集宁");//不属于地级市 属于“乌兰察布市”的一个区
        initCityMap.put("152701", "东胜");//不属于地级市 属于“鄂尔多斯”的一个区
        initCityMap.put("152801", "临河");//不属于地级市 属于“巴彦淖尔市”的一个区
        initCityMap.put("152921", "阿拉善左旗");
        initCityMap.put("150401", "赤峰");
        initCityMap.put("152301", "通辽");
        initCityMap.put("152502", "锡林浩特");
        initCityMap.put("152101", "海拉尔");
        initCityMap.put("152201", "乌兰浩特");


        //add by chengang 2014-04-14
        initCityMap.put("150701", "呼伦贝尔");
        initCityMap.put("150600", "鄂尔多斯");
        initCityMap.put("150801", "巴彦淖尔");
        initCityMap.put("150901", "乌兰察布");


        //辽宁省  14地级市-沈阳、大连、金州、鞍山、抚顺、本溪、丹东、锦州、营口、阜新、辽阳、盘锦、铁岭、朝阳、葫芦岛
        initCityMap.put("210101", "沈阳");
        initCityMap.put("210201", "大连");
        initCityMap.put("210301", "鞍山");
        initCityMap.put("210401", "抚顺");
        initCityMap.put("210501", "本溪");
        initCityMap.put("210701", "锦州");
        initCityMap.put("210801", "营口");
        initCityMap.put("210901", "阜新");
        initCityMap.put("211001", "辽阳");
        initCityMap.put("211101", "盘锦");
        initCityMap.put("211201", "铁岭");
        initCityMap.put("211301", "朝阳");
        initCityMap.put("211401", "锦西");//不属于地市级、属于“葫芦岛”的一个区
        initCityMap.put("210601", "丹东");

        //add by chengang 2014-04-14
        initCityMap.put("211400", "葫芦岛");


        //吉林省  8地级市-长春、吉林、四平、辽源、通化、白山、松原、白城
        initCityMap.put("220101", "长春");
        initCityMap.put("220201", "吉林");
        initCityMap.put("220301", "四平");
        initCityMap.put("220401", "辽源");
//        initCityMap.put("220601", "浑江");//不是地级市、属于白山的一个区
        initCityMap.put("222301", "白城");
        initCityMap.put("222401", "延吉");
        initCityMap.put("220501", "通化");

        //add by chengang 2014-04-14
        initCityMap.put("220701", "松原");
        initCityMap.put("220601", "白山");

        //黑龙江  13地级市：哈尔滨、大庆、齐齐哈尔、佳木斯、鸡西、鹤岗、双鸭山、牡丹江、伊春、七台河、黑河、绥化 加格达奇
        initCityMap.put("230101", "哈尔滨");
        initCityMap.put("230301", "鸡西");
        initCityMap.put("230401", "鹤岗");
        initCityMap.put("230501", "双鸭山");
        initCityMap.put("230701", "伊春");
        initCityMap.put("230801", "佳木斯");
        initCityMap.put("230901", "七台河");
        initCityMap.put("231001", "牡丹江");
        initCityMap.put("232301", "绥化");
        initCityMap.put("230201", "齐齐哈尔");
        initCityMap.put("230601", "大庆");
        initCityMap.put("232601", "黑河");
        initCityMap.put("232700", "加格达奇");

        initCityMap.put("310100", "上海");

        //江苏省 13地级市-南京、镇江、常州、无锡、苏州、徐州、连云港、淮安、盐城、扬州、泰州、南通、宿迁
        initCityMap.put("320101", "南京");
        initCityMap.put("320201", "无锡");
        initCityMap.put("320301", "徐州");
        initCityMap.put("320401", "常州");
        initCityMap.put("320501", "苏州");
        initCityMap.put("320600", "南通");
        initCityMap.put("320701", "连云港");
        initCityMap.put("320801", "淮阴");//不是地级市、属于淮安的一个区，不删，库表中可能已经存储相应的城市码
        initCityMap.put("320901", "盐城");
        initCityMap.put("321001", "扬州");
        initCityMap.put("321101", "镇江");

        //add by chengang 2014-04-14
        initCityMap.put("321301", "宿迁");
        initCityMap.put("320800", "淮安");
        initCityMap.put("321201", "泰州");


        //浙江省  11地级市-杭州、嘉兴、湖州、宁波、金华、温州、丽水、绍兴、衢州、舟山、台州
        initCityMap.put("330101", "杭州");
        initCityMap.put("330201", "宁波");
        initCityMap.put("330301", "温州");
        initCityMap.put("330401", "嘉兴");
        initCityMap.put("330501", "湖州");
        initCityMap.put("330601", "绍兴");
        initCityMap.put("330701", "金华");
        initCityMap.put("330801", "衢州");
        initCityMap.put("330901", "舟山");
        initCityMap.put("332501", "丽水");
        initCityMap.put("332602", "临海");

        //add by chengang 2014-04-14
        initCityMap.put("331001", "台州");


        //安徽省  17地级市-合肥、蚌埠、芜湖、淮南、亳州、阜阳、淮北、宿州、滁州、安庆、巢湖、马鞍山、宣城、黄山、池州、铜陵
        initCityMap.put("340101", "合肥");
        initCityMap.put("340201", "芜湖");
        initCityMap.put("340301", "蚌埠");
        initCityMap.put("340401", "淮南");
        initCityMap.put("340501", "马鞍山");
        initCityMap.put("340601", "淮北");
        initCityMap.put("340701", "铜陵");
        initCityMap.put("340801", "安庆");
        initCityMap.put("341001", "黄山");
        initCityMap.put("342101", "阜阳");
        initCityMap.put("342201", "宿州");
        initCityMap.put("342301", "滁州");
        initCityMap.put("342401", "六安");
        initCityMap.put("342501", "宣州");
        initCityMap.put("342601", "巢湖");
        initCityMap.put("342901", "贵池");//不是地级市、属于“池州”的一个区 不能删，因库表中可能已经存储相应的城市码

        //add by chengang 2014-04-14
        initCityMap.put("341601", "亳州");
        initCityMap.put("341701", "池州");


        //福建省  9地级市-福州、厦门、泉州、三明、南平、漳州、莆田、宁德、龙岩
        initCityMap.put("350101", "福州");
        initCityMap.put("350201", "厦门");
        initCityMap.put("350301", "莆田");
        initCityMap.put("350401", "三明");
        initCityMap.put("350501", "泉州");
        initCityMap.put("350601", "漳州");
        initCityMap.put("352101", "南平");
        initCityMap.put("352201", "宁德");
        initCityMap.put("352601", "龙岩");

        //江西  11地级市-南昌、九江、赣州、吉安、鹰潭、上饶、萍乡、景德镇、新余、宜春、抚州
        initCityMap.put("360101", "南昌");
        initCityMap.put("360201", "景德镇");
        initCityMap.put("362101", "赣州");
        initCityMap.put("360301", "萍乡");
        initCityMap.put("360401", "九江");
        initCityMap.put("360501", "新余");
        initCityMap.put("360601", "鹰潭");
        initCityMap.put("362201", "宜春");
        initCityMap.put("362301", "上饶");
        initCityMap.put("362401", "吉安");
        initCityMap.put("362502", "临川");

        //add by chengang 2014-04-14
        initCityMap.put("361001", "抚州");


        //山东 17地级市-济南、青岛、淄博、枣庄、东营、烟台、潍坊、济宁、泰安、威海、日照、莱芜、临沂、德州、聊城、菏泽、滨州
        initCityMap.put("370101", "济南");
        initCityMap.put("370201", "青岛");
        initCityMap.put("370301", "淄博");
        initCityMap.put("370401", "枣庄");
        initCityMap.put("370501", "东营");
        initCityMap.put("370601", "烟台");
        initCityMap.put("370701", "潍坊");
        initCityMap.put("370801", "济宁");
        initCityMap.put("370901", "泰安");
        initCityMap.put("371001", "威海");
        initCityMap.put("371100", "日照");
        initCityMap.put("372301", "滨州");
        initCityMap.put("372401", "德州");
        initCityMap.put("372501", "聊城");
        initCityMap.put("372801", "临沂");
        initCityMap.put("372901", "菏泽");

        //add by chengang 2014-04-14
        initCityMap.put("371201", "莱芜");

        //河南省  17地级市-郑州、洛阳、开封、漯河、安阳、新乡、周口、三门峡、焦作、平顶山、信阳、南阳、鹤壁、濮阳、许昌、商丘、驻马店
        initCityMap.put("410101", "郑州");
        initCityMap.put("410201", "开封");
        initCityMap.put("410301", "洛阳");
        initCityMap.put("410401", "平顶山");
        initCityMap.put("410501", "安阳");
        initCityMap.put("410601", "鹤壁");
        initCityMap.put("410701", "新乡");
        initCityMap.put("410801", "焦作");
        initCityMap.put("410901", "濮阳");
        initCityMap.put("411001", "许昌");
        initCityMap.put("411101", "漯河");
        initCityMap.put("411201", "三门峡");
        initCityMap.put("412301", "商丘");
        initCityMap.put("412701", "周口");
        initCityMap.put("412801", "驻马店");
        initCityMap.put("412901", "南阳");
        initCityMap.put("413001", "信阳");

        //湖北 12地级市-武汉、襄樊、宜昌、黄石、鄂州、随州、荆州、荆门、十堰、孝感、黄冈、咸宁
        initCityMap.put("420101", "武汉");
        initCityMap.put("420201", "黄石");
        initCityMap.put("420301", "十堰");
        initCityMap.put("420400", "沙市");
        initCityMap.put("420501", "宜昌");
        initCityMap.put("420601", "襄樊");
        initCityMap.put("420701", "鄂州");
        initCityMap.put("420801", "荆门");
        initCityMap.put("422103", "黄州");//不属于地级市，属于黄冈的一个区
        initCityMap.put("422201", "孝感");
        initCityMap.put("422301", "咸宁");
        initCityMap.put("422421", "江陵");//部署地级市，属于荆州的一个县
        initCityMap.put("422801", "恩施");

        //add by chengang 2014-04-14
        initCityMap.put("421001", "荆州");
        initCityMap.put("421101", "黄冈");
        initCityMap.put("421301", "随州");


        //湖南  13地级市-长沙、株洲、湘潭、衡阳、岳阳、郴州、永州、邵阳、怀化、常德、益阳、张家界、娄底
        initCityMap.put("430101", "长沙");
        initCityMap.put("430401", "衡阳");
        initCityMap.put("430501", "邵阳");
        initCityMap.put("432801", "郴州");
        initCityMap.put("432901", "永州");
//        initCityMap.put("430801", "大庸");//不属于地级市，行政区划中没找到对应
        initCityMap.put("433001", "怀化");
        initCityMap.put("433101", "吉首");
        initCityMap.put("430201", "株洲");
        initCityMap.put("430301", "湘潭");
        initCityMap.put("430601", "岳阳");
        initCityMap.put("430701", "常德");
        initCityMap.put("432301", "益阳");
        initCityMap.put("432501", "娄底");

        //add by chengang 2014-04-14
        initCityMap.put("430801", "张家界");


        //广东省  21地级市-广州、深圳、汕头、惠州、珠海、揭阳、佛山、河源、阳江、茂名、湛江、梅州、肇庆、韶关、潮州、东莞、中山、清远、江门、汕尾、云浮
        initCityMap.put("440101", "广州");
        initCityMap.put("440301", "深圳");
        initCityMap.put("441501", "汕尾");
        initCityMap.put("441301", "惠州");
        initCityMap.put("441601", "河源");
        initCityMap.put("440601", "佛山");
        initCityMap.put("441801", "清远");
        initCityMap.put("441901", "东莞");
        initCityMap.put("440401", "珠海");
        initCityMap.put("440701", "江门");
        initCityMap.put("441201", "肇庆");
        initCityMap.put("442001", "中山");
        initCityMap.put("440801", "湛江");
        initCityMap.put("440901", "茂名");
        initCityMap.put("440201", "韶关");
        initCityMap.put("440501", "汕头");
        initCityMap.put("441401", "梅州");
        initCityMap.put("441701", "阳江");

        // add by chengang 2014-04-14
        initCityMap.put("445101", "潮州");
        initCityMap.put("445201", "揭阳");
        initCityMap.put("445301", "云浮");


        //广西  14地级市-南宁、柳州、桂林、梧州、北海、崇左、来宾、贺州、玉林、百色、河池、钦州、防城港、贵港
        initCityMap.put("450101", "南宁");
        initCityMap.put("450401", "梧州");
        initCityMap.put("452501", "玉林");
        initCityMap.put("450301", "桂林");
        initCityMap.put("452601", "百色");
        initCityMap.put("452701", "河池");
        initCityMap.put("452802", "钦州");
        initCityMap.put("450201", "柳州");
        initCityMap.put("450501", "北海");

        //add by chengang 2014-04-14
        initCityMap.put("450601", "防城港");
        initCityMap.put("451101", "贺州");
        initCityMap.put("451301", "来宾");
        initCityMap.put("451401", "崇左");
        initCityMap.put("450801", "贵港");

        //海南 3个地级市 海口、三亚、三沙
        initCityMap.put("460100", "海口");
        initCityMap.put("460200", "三亚");
        initCityMap.put("460300", "三沙");

        //四川 18地级市-成都、绵阳、德阳、广元、自贡、攀枝花、乐山、南充、内江、遂宁、广安、泸州、达州、眉山、宜宾、雅安、资阳
        initCityMap.put("510101", "成都");
        initCityMap.put("513321", "康定");
        initCityMap.put("513101", "雅安");
        initCityMap.put("513229", "马尔康");
        initCityMap.put("510301", "自贡");
        initCityMap.put("512901", "南充");
        initCityMap.put("510501", "泸州");
        initCityMap.put("510601", "德阳");
        initCityMap.put("510701", "绵阳");
        initCityMap.put("510901", "遂宁");
        initCityMap.put("511001", "内江");
        initCityMap.put("511101", "乐山");
        initCityMap.put("512501", "宜宾");
        initCityMap.put("510801", "广元");
        initCityMap.put("513021", "达县");
        initCityMap.put("513401", "西昌");
        initCityMap.put("510401", "攀枝花");

        //add by chengang 2014-04-14
        initCityMap.put("511601", "广安");
        initCityMap.put("511901", "巴中");
        initCityMap.put("511401", "眉山");
        initCityMap.put("512001", "资阳");


        initCityMap.put("500100", "重庆");
        initCityMap.put("500239", "黔江土家族苗族自治县");
        initCityMap.put("513200", "阿坝藏族羌族自治州");

        //贵州省 4地级市-贵阳、六盘水、遵义、安顺
        initCityMap.put("520101", "贵阳");
        initCityMap.put("520200", "六盘水");
        initCityMap.put("522201", "铜仁");
        initCityMap.put("522501", "安顺");
        initCityMap.put("522601", "凯里");
        initCityMap.put("522701", "都匀");
        initCityMap.put("522301", "兴义");
        initCityMap.put("522421", "毕节");
        initCityMap.put("522101", "遵义");

        //云南  8地级市-昆明、曲靖、玉溪、保山、昭通、丽江、思茅、普洱、临沧
        initCityMap.put("530101", "昆明");
        initCityMap.put("530201", "东川");
        initCityMap.put("532201", "曲靖");
        initCityMap.put("532301", "楚雄");
        initCityMap.put("532401", "玉溪");
        initCityMap.put("532501", "个旧");
        initCityMap.put("532621", "文山");
        initCityMap.put("532721", "思茅");
        initCityMap.put("532101", "昭通");
        initCityMap.put("532821", "景洪");
        initCityMap.put("532901", "大理");
        initCityMap.put("533001", "保山");
        initCityMap.put("533121", "潞西");
        initCityMap.put("533221", "丽江纳西族自治县");
        initCityMap.put("533321", "泸水");
        initCityMap.put("533421", "中甸");
        initCityMap.put("533521", "临沧");

        //add by chengang 2014-04-14
        initCityMap.put("532500", "红河");
        initCityMap.put("532800", "西双版纳傣族自治州");

        //西藏
        initCityMap.put("540101", "拉萨");
        initCityMap.put("542121", "昌都");
        initCityMap.put("542200", "泽当镇");
        initCityMap.put("542221", "乃东");
        initCityMap.put("542301", "日喀则");
        initCityMap.put("542421", "那曲");
        initCityMap.put("542523", "噶尔");
        initCityMap.put("542600", "八一镇");
        initCityMap.put("542621", "林芝");

        //陕西 10地级市-西安、咸阳、铜川、延安、宝鸡、渭南、汉中、安康、商洛、榆林
        initCityMap.put("610101", "西安");
        initCityMap.put("610201", "铜川");
        initCityMap.put("610301", "宝鸡");
        initCityMap.put("610401", "咸阳");
        initCityMap.put("612101", "渭南");
        initCityMap.put("612301", "汉中");
        initCityMap.put("612401", "安康");
        initCityMap.put("612501", "商州");
        initCityMap.put("612601", "延安");
        initCityMap.put("612701", "榆林");

        //add by chengang 2014-04-14
        initCityMap.put("611001", "商洛");

        //甘肃 12地级市-兰州、天水、平凉、酒泉、嘉峪关、金昌、白银、武威、张掖、庆阳、定西、陇南
        initCityMap.put("620101", "兰州");
        initCityMap.put("620401", "白银");
        initCityMap.put("620301", "金昌");
        initCityMap.put("620501", "天水");
        initCityMap.put("622201", "张掖");
        initCityMap.put("622301", "武威");
        initCityMap.put("622421", "定西");
        initCityMap.put("622624", "成县");
        initCityMap.put("622701", "平凉");
        initCityMap.put("622801", "西峰");
        initCityMap.put("622901", "临夏");
        initCityMap.put("623027", "夏河");
        initCityMap.put("620201", "嘉峪关");
        initCityMap.put("622102", "酒泉");

        //add by chengang 2014-04-14
        initCityMap.put("621001", "庆阳");
        initCityMap.put("621201", "陇南");

        //青海省 西宁市、海东地区、海北藏族自治州、黄南藏族自治州
        initCityMap.put("630100", "西宁");
        initCityMap.put("632121", "平安");
        initCityMap.put("632221", "门源回族自治县");
        initCityMap.put("632321", "同仁");
        initCityMap.put("632521", "共和");
        initCityMap.put("632621", "玛沁");
        initCityMap.put("632721", "玉树");
        initCityMap.put("632802", "德令哈");

        //add by chengang 2014-04-14
        initCityMap.put("632100", "海东");


        //宁夏 5地级市-银川、石嘴山、吴忠、固原、中卫
        initCityMap.put("640101", "银川");
        initCityMap.put("640201", "石嘴山");
        initCityMap.put("642101", "吴忠");
        initCityMap.put("642221", "固原");

        //add by chengang 2014-04-14
        initCityMap.put("640501", "中卫");


        //新疆 2地级市-乌鲁木齐、克拉玛依
        //19县级市-石河子、阿拉尔市、图木舒克、五家渠、哈密、吐鲁番、阿克苏、喀什、和田、伊宁、塔城、阿勒泰、奎屯、博乐、昌吉、阜康、库尔勒、阿图什、乌苏
        initCityMap.put("650101", "乌鲁木齐");
        initCityMap.put("650201", "克拉玛依");
        initCityMap.put("652101", "吐鲁番");
        initCityMap.put("652201", "哈密");
        initCityMap.put("652301", "昌吉");
        initCityMap.put("652701", "博乐");
        initCityMap.put("652801", "库尔勒");
        initCityMap.put("652901", "阿克苏");
        initCityMap.put("653001", "阿图什");
        initCityMap.put("653101", "喀什");
        initCityMap.put("653201", "和田");
        initCityMap.put("654101", "伊宁");

        //add by chengang 2014-04-14
        initCityMap.put("654301", "阿勒泰");
        initCityMap.put("654202", "乌苏");
        initCityMap.put("652302", "阜康");
        initCityMap.put("654003", "奎屯");
        initCityMap.put("654201", "塔城");
        initCityMap.put("659004", "五家渠");
        initCityMap.put("659003", "图木舒克");
        initCityMap.put("659002", "阿拉尔市");
        initCityMap.put("659001", "石河子");


        //台湾  7市-台北、台中、基隆、高雄、台南、新竹、嘉义
        initCityMap.put("710001", "台北");
        initCityMap.put("710002", "基隆");
        initCityMap.put("710020", "台南");
        initCityMap.put("710019", "高雄");
        initCityMap.put("710008", "台中");

        initCityMap.put("820000", "澳门");
        initCityMap.put("810000", "香港");

        initCityMap.put("990000", "国外");
        return initCityMap;
    }


    /**
     * 根据省份代码获取省份信息
     *
     * @param provinceCode
     * @return
     */
    public static final String getProvinceByPCode(String provinceCode) {
        return immutableProvinceMap.get(provinceCode);
    }

    /**
     * 根据城市代码获取城市信息
     *
     * @param cityCode
     * @return
     */
    public static final String getCityByCityCode(String cityCode) {
        return immutableCityMap.get(cityCode);
    }
}
