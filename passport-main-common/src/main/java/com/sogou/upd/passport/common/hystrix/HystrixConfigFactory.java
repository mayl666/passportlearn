package com.sogou.upd.passport.common.hystrix;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import jodd.props.Props;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-8
 * Time: 下午5:41
 * To change this template use File | Settings | File Templates.
 */
public class HystrixConfigFactory {
    private static String HYSTRIX_PROPERTY_FILE = "hystrix_config.properties";

    private static Props properties = null;
    protected static ConcurrentMap<String, String> hystrixConfigMap = Maps.newConcurrentMap();

    public static String getHystrixConfValue(String confName)  {
        String confValue;

        synchronized (hystrixConfigMap) {
            confValue = hystrixConfigMap.get(confName);
        }
        if (confValue == null) {
            synchronized (HystrixConfigFactory.class) {
                try {
                    confValue = loadHystrixConfigItem(confName);
                }   catch (Exception e){
                    confValue=null;
                }

                hystrixConfigMap.putIfAbsent(confName, confValue);
            }
        }
        return confValue;
    }

    private static String loadHystrixConfigItem(String confName) throws IOException {
        properties = new Props();
        InputStream input = HystrixConfigFactory.class.getClassLoader().getResourceAsStream(HYSTRIX_PROPERTY_FILE);
        properties.load(input);

        String configValue = properties.getValue(confName);
        if (Strings.isNullOrEmpty(configValue)) {
            return null;
        }
        return configValue;
    }

}
