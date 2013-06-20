package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.op.iploc.Ip2location;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午4:14 To change this template use
 * File | Settings | File Templates.
 */
public class IpLocationUtil {

    private static final Ip2location instance;

    private static final Map<String, String> city;

    static {
/*        instance = new Ip2location();
        new IpLocationUtil().loadCities();*/
        System.out.println(IpLocationUtil.class.getResource("").getPath());
        instance = new Ip2location(IpLocationUtil.class.getClassLoader().getResource("").getPath() + "resources/location.dat");
        city = Maps.newHashMap();
        try {
            //String url = ClassLoader.getSystemResource("").getPath() + "cities.dat";
            String url =  Thread.currentThread().getContextClassLoader() + "cities.dat";
            FileInputStream fis = new FileInputStream(url);
            BufferedReader is = new BufferedReader(new InputStreamReader(fis));
            String readValue = is.readLine();
            while (readValue != null) {
                String[] kv = readValue.split("\\|", 2);
                if (kv.length >= 2) {
                    city.put(kv[0], kv[1]);
                }
                readValue = is.readLine();
            }
        } catch (IOException e) {
            // 无默认数据
        }
    }

    private void loadCities() {
        try {
            System.out.println(this.getClass().getResource("").getPath());
            instance.readData(this.getClass().getClassLoader().getSystemResource("").getPath() + "location.dat");
        } catch (Exception e) {
            System.out.println("Exception");
        }

    }

    public static String getCity(String ip) {
        String cityName = "IP归属地未知";
        try {
            if (Strings.isNullOrEmpty(ip)) {
                return cityName;
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
