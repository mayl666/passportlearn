package com.sogou.upd.passport.model;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import jodd.props.Props;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午8:49
 * To change this template use File | Settings | File Templates.
 */
public class MappDeployConfigFactory {

    private static String RESOURCE_NAME = "mapp_deploy_config.properties";

    private static Props properties = null;

    protected static ConcurrentMap<String,String> confMap = Maps.newConcurrentMap();

    public static Map getMappConfig() throws IOException {
        if (confMap == null || confMap.isEmpty()) {
            properties = new Props();
            InputStream input = MappDeployConfigFactory.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
            properties.load(input);
            confMap.putIfAbsent("qqSSOLoginUrl", properties.getValue("qqSSOLoginUrl"));
            confMap.putIfAbsent("sinaSSOLoginUrl", properties.getValue("sinaSSOLoginUrl"));
        }
        return confMap;
    }


}
