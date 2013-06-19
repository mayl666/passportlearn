package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;

import com.sogou.op.iploc.Ip2location;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午4:14 To change this template use
 * File | Settings | File Templates.
 */
public class IpAddressUtil {

    private static final Ip2location instance;

    private static final Map<String, String> city;

    static {
        instance = new Ip2location(ClassLoader.getSystemResource("").getPath() + "location.dat");
        city = Maps.newHashMap();
        Properties props = new Properties();
        try {
            props.load(ClassLoader.getSystemResourceAsStream("/cities.properties"));
        } catch (IOException e) {
            //
        }
        city.put("CN11", "\u5317\u4eac\u5e02");
        city.put("CN12", "\u5929\u6d25\u5e02");
        city.put("CN13", "\u6cb3\u5317\u7701");
        city.put("CN14", "\u5c71\u897f\u7701");
        city.put("CN15", "\u5185\u8499\u53e4\u81ea\u6cbb\u533a");
        city.put("CN21", "\u8fbd\u5b81\u7701");
        city.put("CN22", "\u5409\u6797\u7701");
        city.put("CN23", "\u9ed1\u9f99\u6c5f\u7701");
        city.put("CN31", "\u4e0a\u6d77\u5e02");
        city.put("CN32", "\u6c5f\u82cf\u7701");
        city.put("CN33", "\u6d59\u6c5f\u7701");
        city.put("CN34", "\u5b89\u5fbd\u7701");
        city.put("CN35", "\u798f\u5efa\u7701");
        city.put("CN36", "\u6c5f\u897f\u7701");
        city.put("CN37", "\u5c71\u4e1c\u7701");
        city.put("CN41", "\u6cb3\u5357\u7701");
        city.put("CN42", "\u6e56\u5317\u7701");
        city.put("CN43", "\u6e56\u5357\u7701");
        city.put("CN44", "\u5e7f\u4e1c\u7701");
        city.put("CN45", "\u5e7f\u897f\u58ee\u65cf\u81ea\u6cbb\u533a");
        city.put("CN46", "\u6d77\u5357\u7701");
        city.put("CN50", "\u91cd\u5e86\u5e02");
        city.put("CN51", "\u56db\u5ddd\u7701");
        city.put("CN52", "\u8d35\u5dde\u7701");
        city.put("CN53", "\u4e91\u5357\u7701");
        city.put("CN54", "\u897f\u85cf\u81ea\u6cbb\u533a");
        city.put("CN61", "\u9655\u897f\u7701");
        city.put("CN62", "\u7518\u8083\u7701");
        city.put("CN63", "\u9752\u6d77\u7701");
        city.put("CN64", "\u5b81\u590f\u56de\u65cf\u81ea\u6cbb\u533a");
        city.put("CN65", "\u65b0\u7586\u7ef4\u543e\u5c14\u81ea\u6cbb\u533a");
    }

    public static String getCity(String ip) {
        String location = instance.getLocation(ip);
        String cityName = "unknown";
        if (location != null && !location.equals("") && location.length() > 4) {
            String key = location.substring(0, 4);
            if (city.containsKey(key)) {
                // 如果里面存在这个城市.
                cityName = city.get(key);
            }
        }
        return location + "," + cityName;
    }
}
