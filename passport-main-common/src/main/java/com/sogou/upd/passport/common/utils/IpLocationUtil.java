package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.op.iploc.Ip2location;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午4:14 To change this template use
 * File | Settings | File Templates.
 */
public class IpLocationUtil {

    private static final Ip2location instance;

    private static final Map<String, String> city;

    static {
        String path = ClassLoader.getSystemResource("location.dat").getFile();
        path = StringUtils.removeStart(path, "/");

        instance = new Ip2location(path);
        city = Maps.newHashMap();
        try {
            path = ClassLoader.getSystemResource("cities.dat").getFile();
            path = StringUtils.removeStart(path, "/");
            FileInputStream fis = new FileInputStream(path);
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
