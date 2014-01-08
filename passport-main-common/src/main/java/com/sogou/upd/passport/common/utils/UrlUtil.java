package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-12-26
 * Time: 下午10:14
 * To change this template use File | Settings | File Templates.
 */
public class UrlUtil {
    public static Map<String,String> buildOriginalParam(String url){
        String [] params=url.split("&");

        Map<String,String> map= Maps.newHashMap();
        if(ArrayUtils.isNotEmpty(params)){
            for(String param:params){
                String[] paramapping=param.split("=");
                map.put(paramapping[0],paramapping[1]);
            }
        }
        return map;
    }

    public static String buildRuParam(Map<String,String> map){
        StringBuilder sb=null;
        if (MapUtils.isNotEmpty(map)) {
            sb=new StringBuilder();
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                sb.append(key).append("=").append(value).append("&");
            }
        }
        return sb.toString();
    }
}
