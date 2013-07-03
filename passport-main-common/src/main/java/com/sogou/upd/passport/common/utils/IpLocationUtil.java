package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.utils.iploc.Ip2location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午4:14 To change this template use
 * File | Settings | File Templates.
 */
public class IpLocationUtil {

    private static final Ip2location instance;

    private static final Map<String, String> city;

    static {
        instance = new Ip2location();
        city = Maps.newHashMap();
        try {
            InputStream inloc = IpLocationUtil.class.getResourceAsStream("/location.dat");
            instance.readData(inloc);

            // TODO:由于读取文件流时，部分服务器会出现乱码现象，暂时采用直接put方案
            setCities(city);

            /*InputStream incity = IpLocationUtil.class.getResourceAsStream("/cities.dat");
            BufferedReader is = new BufferedReader(new InputStreamReader(incity, "UTF-8"));
            String readValue = is.readLine();
            while (readValue != null) {
                System.out.println(readValue.toCharArray());
                String[] kv = readValue.split("\\|", 2);
                if (kv.length >= 2) {
                    city.put(kv[0], kv[1]);
                }
                readValue = is.readLine();
            }*/
        } catch (IOException e) {
            // 无默认数据
        }
    }

    // TODO:暂时方案
    private static void setCities(Map<String, String> cities) {
        cities.put("CN1100","北京市");
        cities.put("CN1101","北京市");
        cities.put("CN1102","北京市");
        cities.put("CN1200","天津市");
        cities.put("CN1201","天津市");
        cities.put("CN1202","天津市");
        cities.put("CN1301","河北石家庄市");
        cities.put("CN1302","河北唐山市");
        cities.put("CN1303","河北秦皇岛市");
        cities.put("CN1304","河北邯郸市");
        cities.put("CN1305","河北邢台市");
        cities.put("CN1306","河北保定市");
        cities.put("CN1307","河北张家口市");
        cities.put("CN1308","河北承德市");
        cities.put("CN1309","河北沧州市");
        cities.put("CN1310","河北廊坊市");
        cities.put("CN1311","河北衡水市");
        cities.put("CN1401","山西太原市");
        cities.put("CN1402","山西大同市");
        cities.put("CN1403","山西阳泉市");
        cities.put("CN1404","山西长治市");
        cities.put("CN1405","山西晋城市");
        cities.put("CN1406","山西朔州市");
        cities.put("CN1409","山西忻州市");
        cities.put("CN1423","山西吕梁地区");
        cities.put("CN1407","山西晋中市");
        cities.put("CN1410","山西临汾市");
        cities.put("CN1408","山西运城市");
        cities.put("CN1501","内蒙古呼和浩特市");
        cities.put("CN1502","内蒙古包头市");
        cities.put("CN1503","内蒙古乌海市");
        cities.put("CN1504","内蒙古赤峰市");
        cities.put("CN1521","内蒙古呼伦贝尔盟");
        cities.put("CN1522","内蒙古兴安盟");
        cities.put("CN1505","内蒙古通辽市");
        cities.put("CN1521","内蒙古海拉尔市");
        cities.put("CN1525","内蒙古锡林郭勒盟");
        cities.put("CN1526","内蒙古乌兰察布盟");
        cities.put("CN1506","内蒙古鄂尔多斯市");
        cities.put("CN1528","内蒙古巴彦淖尔盟");
        cities.put("CN1528","内蒙古临河市");
        cities.put("CN1529","内蒙古阿拉善盟");
        cities.put("CN2101","辽宁沈阳市");
        cities.put("CN2102","辽宁大连市");
        cities.put("CN2103","辽宁鞍山市");
        cities.put("CN2104","辽宁抚顺市");
        cities.put("CN2105","辽宁本溪市");
        cities.put("CN2106","辽宁丹东市");
        cities.put("CN2107","辽宁锦州市");
        cities.put("CN2108","辽宁营口市");
        cities.put("CN2109","辽宁阜新市");
        cities.put("CN2110","辽宁辽阳市");
        cities.put("CN2111","辽宁盘锦市");
        cities.put("CN2112","辽宁铁岭市");
        cities.put("CN2113","辽宁朝阳市");
        cities.put("CN2114","辽宁葫芦岛市");
        cities.put("CN2201","吉林长春市");
        cities.put("CN2202","吉林吉林市");
        cities.put("CN2203","吉林四平市");
        cities.put("CN2204","吉林辽源市");
        cities.put("CN2205","吉林通化市");
        cities.put("CN2206","吉林白山市");
        cities.put("CN2207","吉林松原市");
        cities.put("CN2208","吉林白城市");
        cities.put("CN2224","吉林延边朝鲜族自治州");
        cities.put("CN2224","吉林延吉市");
        cities.put("CN2301","黑龙江哈尔滨市");
        cities.put("CN2302","黑龙江齐齐哈尔市");
        cities.put("CN2303","黑龙江鸡西市");
        cities.put("CN2304","黑龙江鹤岗市");
        cities.put("CN2305","黑龙江双鸭山市");
        cities.put("CN2306","黑龙江大庆市");
        cities.put("CN2307","黑龙江伊春市");
        cities.put("CN2308","黑龙江佳木斯市");
        cities.put("CN2309","黑龙江七台河市");
        cities.put("CN2310","黑龙江牡丹江市");
        cities.put("CN2311","黑龙江黑河市");
        cities.put("CN2312","黑龙江绥化市");
        cities.put("CN2327","黑龙江大兴安岭地区");
        cities.put("CN3100","上海市");
        cities.put("CN3101","上海市");
        cities.put("CN3102","上海市");
        cities.put("CN3201","江苏南京市");
        cities.put("CN3202","江苏无锡市");
        cities.put("CN3203","江苏徐州市");
        cities.put("CN3204","江苏常州市");
        cities.put("CN3205","江苏苏州市");
        cities.put("CN3206","江苏南通市");
        cities.put("CN3207","江苏连云港市");
        cities.put("CN3208","江苏淮安市");
        cities.put("CN3209","江苏盐城市");
        cities.put("CN3210","江苏扬州市");
        cities.put("CN3211","江苏镇江市");
        cities.put("CN3212","江苏泰州市");
        cities.put("CN3213","江苏宿迁市");
        cities.put("CN3301","浙江杭州市");
        cities.put("CN3302","浙江宁波市");
        cities.put("CN3303","浙江温州市");
        cities.put("CN3304","浙江嘉兴市");
        cities.put("CN3305","浙江湖州市");
        cities.put("CN3306","浙江绍兴市");
        cities.put("CN3307","浙江金华市");
        cities.put("CN3308","浙江衢州市");
        cities.put("CN3309","浙江舟山市");
        cities.put("CN3310","浙江台州市");
        cities.put("CN3311","浙江丽水市");
        cities.put("CN3401","安徽合肥市");
        cities.put("CN3402","安徽芜湖市");
        cities.put("CN3403","安徽蚌埠市");
        cities.put("CN3404","安徽淮南市");
        cities.put("CN3405","安徽马鞍山市");
        cities.put("CN3406","安徽淮北市");
        cities.put("CN3407","安徽铜陵市");
        cities.put("CN3408","安徽安庆市");
        cities.put("CN3410","安徽黄山市");
        cities.put("CN3411","安徽滁州市");
        cities.put("CN3412","安徽阜阳市");
        cities.put("CN3413","安徽宿州市");
        cities.put("CN3415","安徽六安市");
        cities.put("CN3418","安徽宣城市");
        cities.put("CN3414","安徽巢湖市");
        cities.put("CN3417","安徽池州市");
        cities.put("CN3416","安徽亳州市");
        cities.put("CN3501","福建福州市");
        cities.put("CN3502","福建厦门市");
        cities.put("CN3503","福建莆田市");
        cities.put("CN3504","福建三明市");
        cities.put("CN3505","福建泉州市");
        cities.put("CN3506","福建漳州市");
        cities.put("CN3507","福建南平市");
        cities.put("CN3508","福建龙岩市");
        cities.put("CN3509","福建宁德市");
        cities.put("CN3601","江西南昌市");
        cities.put("CN3602","江西景德镇市");
        cities.put("CN3603","江西萍乡市");
        cities.put("CN3604","江西九江市");
        cities.put("CN3605","江西新余市");
        cities.put("CN3606","江西鹰潭市");
        cities.put("CN3607","江西赣州市");
        cities.put("CN3609","江西宜春市");
        cities.put("CN3624","江西吉安市");
        cities.put("CN3610","江西抚州市");
        cities.put("CN3611","江西上饶市");
        cities.put("CN3701","山东济南市");
        cities.put("CN3702","山东青岛市");
        cities.put("CN3703","山东淄博市");
        cities.put("CN3704","山东枣庄市");
        cities.put("CN3705","山东东营市");
        cities.put("CN3706","山东烟台市");
        cities.put("CN3707","山东潍坊市");
        cities.put("CN3708","山东济宁市");
        cities.put("CN3709","山东泰安市");
        cities.put("CN3710","山东威海市");
        cities.put("CN3711","山东日照市");
        cities.put("CN3712","山东莱芜市");
        cities.put("CN3713","山东临沂市");
        cities.put("CN3714","山东德州市");
        cities.put("CN3715","山东聊城市");
        cities.put("CN3716","山东滨州市");
        cities.put("CN3717","山东荷泽市");
        cities.put("CN4101","河南郑州市");
        cities.put("CN4102","河南开封市");
        cities.put("CN4103","河南洛阳市");
        cities.put("CN4104","河南平顶山市");
        cities.put("CN4105","河南安阳市");
        cities.put("CN4106","河南鹤壁市");
        cities.put("CN4107","河南新乡市");
        cities.put("CN4108","河南焦作市");
        cities.put("CN4109","河南濮阳市");
        cities.put("CN4110","河南许昌市");
        cities.put("CN4111","河南漯河市");
        cities.put("CN4112","河南三门峡市");
        cities.put("CN4113","河南南阳市");
        cities.put("CN4114","河南商丘市");
        cities.put("CN4115","河南信阳市");
        cities.put("CN4116","河南周口市");
        cities.put("CN4117","河南驻马店市");
        cities.put("CN4201","湖北武汉市");
        cities.put("CN4202","湖北黄石市");
        cities.put("CN4203","湖北十堰市");
        cities.put("CN4205","湖北宜昌市");
        cities.put("CN4206","湖北襄樊市");
        cities.put("CN4207","湖北鄂州市");
        cities.put("CN4208","湖北荆门市");
        cities.put("CN4209","湖北孝感市");
        cities.put("CN4210","湖北荆州市");
        cities.put("CN4211","湖北黄冈市");
        cities.put("CN4212","湖北咸宁市");
        cities.put("CN4228","湖北施土家族苗族自治州");
        cities.put("CN4228","湖北恩施自治州");
        cities.put("CN4290","湖北级行政单位");
        cities.put("CN4213","湖北随州市");
        cities.put("CN4301","湖南长沙市");
        cities.put("CN4302","湖南株洲市");
        cities.put("CN4303","湖南湘潭市");
        cities.put("CN4304","湖南衡阳市");
        cities.put("CN4305","湖南邵阳市");
        cities.put("CN4306","湖南岳阳市");
        cities.put("CN4307","湖南常德市");
        cities.put("CN4308","湖南张家界市");
        cities.put("CN4309","湖南益阳市");
        cities.put("CN4310","湖南郴州市");
        cities.put("CN4311","湖南永州市");
        cities.put("CN4312","湖南怀化市");
        cities.put("CN4313","湖南娄底市");
        cities.put("CN4331","湖南湘西土家族苗族自治州");
        cities.put("CN4401","广东广州市");
        cities.put("CN4402","广东韶关市");
        cities.put("CN4403","广东深圳市");
        cities.put("CN4404","广东珠海市");
        cities.put("CN4405","广东汕头市");
        cities.put("CN4406","广东佛山市");
        cities.put("CN4407","广东江门市");
        cities.put("CN4408","广东湛江市");
        cities.put("CN4409","广东茂名市");
        cities.put("CN4412","广东肇庆市");
        cities.put("CN4413","广东惠州市");
        cities.put("CN4414","广东梅州市");
        cities.put("CN4415","广东汕尾市");
        cities.put("CN4416","广东河源市");
        cities.put("CN4417","广东阳江市");
        cities.put("CN4418","广东清远市");
        cities.put("CN4419","广东东莞市");
        cities.put("CN4420","广东中山市");
        cities.put("CN4451","广东潮州市");
        cities.put("CN4452","广东揭阳市");
        cities.put("CN4453","广东云浮市");
        cities.put("CN4501","广西南宁市");
        cities.put("CN4502","广西柳州市");
        cities.put("CN4503","广西桂林市");
        cities.put("CN4504","广西梧州市");
        cities.put("CN4505","广西北海市");
        cities.put("CN4506","广西防城港市");
        cities.put("CN4507","广西钦州市");
        cities.put("CN4508","广西贵港市");
        cities.put("CN4509","广西玉林市");
        cities.put("CN4521","广西南宁地区");
        cities.put("CN4522","广西柳州地区");
        cities.put("CN4524","广西贺州地区");
        cities.put("CN4526","广西百色地区");
        cities.put("CN4527","广西河池地区");
        cities.put("CN4601","海南海口市");
        cities.put("CN4602","海南三亚市");
        cities.put("CN5001","重庆市辖区");
        cities.put("CN5001","重庆万州");
        cities.put("CN5000","重庆市");
        cities.put("CN5002","重庆市");
        cities.put("CN5003","重庆市");
        cities.put("CN5101","四川成都市");
        cities.put("CN5103","四川自贡市");
        cities.put("CN5104","四川攀枝花市");
        cities.put("CN5105","四川泸州市");
        cities.put("CN5106","四川德阳市");
        cities.put("CN5107","四川绵阳市");
        cities.put("CN5108","四川广元市");
        cities.put("CN5109","四川遂宁市");
        cities.put("CN5110","四川内江市");
        cities.put("CN5111","四川乐山市");
        cities.put("CN5113","四川南充市");
        cities.put("CN5115","四川宜宾市");
        cities.put("CN5116","四川广安市");
        cities.put("CN5117","四川达州市");
        cities.put("CN5118","四川雅安市");
        cities.put("CN5132","四川阿坝藏族羌族自治州");
        cities.put("CN5133","四川甘孜藏族自治州");
        cities.put("CN5134","四川凉山彝族自治州");
        cities.put("CN5134","四川西昌市");
        cities.put("CN5119","四川巴中市");
        cities.put("CN5114","四川眉山市");
        cities.put("CN5120","四川资阳市");
        cities.put("CN5123","四川涪陵市");
        cities.put("CN5201","贵州贵阳市");
        cities.put("CN5202","贵州六盘水市");
        cities.put("CN5203","贵州遵义市");
        cities.put("CN5222","贵州铜仁地区");
        cities.put("CN5223","贵州黔西南布依族苗族自治州");
        cities.put("CN5224","贵州毕节地区");
        cities.put("CN5204","贵州安顺市");
        cities.put("CN5226","贵州黔东南苗族侗族自治州");
        cities.put("CN5227","贵州黔南布依族苗族自治州");
        cities.put("CN5301","云南昆明市");
        cities.put("CN5303","云南曲靖市");
        cities.put("CN5304","云南玉溪市");
        cities.put("CN5306","云南昭通市");
        cities.put("CN5323","云南楚雄彝族自治州");
        cities.put("CN5325","云南红河哈尼族彝族自治州");
        cities.put("CN5326","云南文山壮族苗族自治州");
        cities.put("CN5327","云南思茅地区");
        cities.put("CN5328","云南西双版纳傣族自治州");
        cities.put("CN5329","云南大理白族自治州");
        cities.put("CN5305","云南保山市");
        cities.put("CN5331","云南德宏傣族景颇族自治州");
        cities.put("CN5332","云南丽江市");
        cities.put("CN5333","云南怒江傈僳族自治州");
        cities.put("CN5334","云南迪庆藏族自治州");
        cities.put("CN5335","云南临沧市");
        cities.put("CN5401","西藏拉萨市");
        cities.put("CN5421","西藏昌都地区");
        cities.put("CN5422","西藏山南地区");
        cities.put("CN5423","西藏日喀则地区");
        cities.put("CN5424","西藏那曲地区");
        cities.put("CN5425","西藏阿里地区");
        cities.put("CN5426","西藏林芝地区");
        cities.put("CN6101","陕西西安市");
        cities.put("CN6102","陕西铜川市");
        cities.put("CN6103","陕西宝鸡市");
        cities.put("CN6104","陕西咸阳市");
        cities.put("CN6105","陕西渭南市");
        cities.put("CN6106","陕西延安市");
        cities.put("CN6107","陕西汉中市");
        cities.put("CN6109","陕西安康市");
        cities.put("CN6110","陕西商洛市");
        cities.put("CN6108","陕西榆林市");
        cities.put("CN6230","甘肃甘南藏族自治州");
        cities.put("CN6229","甘肃临夏回族自治州");
        cities.put("CN6228","甘肃庆阳市");
        cities.put("CN6227","甘肃平凉市");
        cities.put("CN6226","甘肃陇南市");
        cities.put("CN6224","甘肃定西市");
        cities.put("CN6223","甘肃武威市");
        cities.put("CN6222","甘肃张掖市");
        cities.put("CN6221","甘肃酒泉市");
        cities.put("CN6205","甘肃天水市");
        cities.put("CN6204","甘肃白银市");
        cities.put("CN6203","甘肃金昌市");
        cities.put("CN6202","甘肃嘉峪关市");
        cities.put("CN6201","甘肃兰州市");
        cities.put("CN6206","甘肃武威市");
        cities.put("CN6328","青海海西蒙古族藏族自治州");
        cities.put("CN6327","青海玉树藏族自治州");
        cities.put("CN6326","青海果洛藏族自治州");
        cities.put("CN6325","青海海南藏族自治州");
        cities.put("CN6323","青海黄南藏族自治州");
        cities.put("CN6322","青海海北藏族自治州");
        cities.put("CN6321","青海海东地区");
        cities.put("CN6301","青海西宁市");
        cities.put("CN6401","宁夏银川市");
        cities.put("CN6402","宁夏石嘴山市");
        cities.put("CN6403","宁夏吴忠市");
        cities.put("CN6404","宁夏固原市");
        cities.put("CN6501","新疆乌鲁木齐市");
        cities.put("CN6502","新疆克拉玛依市");
        cities.put("CN6521","新疆吐鲁番地区");
        cities.put("CN6522","新疆哈密地区");
        cities.put("CN6523","新疆昌吉回族自治州");
        cities.put("CN6527","新疆博尔塔拉蒙古自治州");
        cities.put("CN6528","新疆巴音郭楞蒙古自治州");
        cities.put("CN6528","新疆库尔勒市");
        cities.put("CN6529","新疆阿克苏地区");
        cities.put("CN6530","新疆克孜勒苏柯尔克孜自治州");
        cities.put("CN6531","新疆喀什地区");
        cities.put("CN6532","新疆和田地区");
        cities.put("CN6540","新疆伊犁哈萨克自治州");
        cities.put("CN6541","新疆伊宁市");
        cities.put("CN6542","新疆塔城地区");
        cities.put("CN6543","新疆阿勒泰地区");
        cities.put("CN6590","新疆省直辖行政单位");
        cities.put("CN7100","台湾省");
        cities.put("CN8100","香港特别行政区");
        cities.put("CN8200","澳门特别行政区");
    }

    public static String getCity(String ip) {
        // String cityName = "IP归属地未知";
        String cityName = "IP归属地未知";
        try {
            if (Strings.isNullOrEmpty(ip)) {
                return cityName;
            }

            if (ip.indexOf("10.") == 0 || ip.indexOf("192.168.") == 0) {
                return "局域网";
            }
            String location = instance.getLocation(ip);
            if (location != null && !location.equals("") && location.length() >= 6) {
                String key = location.substring(0, 6);
                if (city.containsKey(key)) {
                    // 如果里面存在这个城市.
                    cityName = city.get(key);
                }
            }
            return cityName;
        } catch (Exception e) {
            return cityName;
        }
    }

}
