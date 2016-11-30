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

    protected static ConcurrentMap<String, String> confMap = Maps.newConcurrentMap();

    public static Map getMappConfig() throws IOException {
        if (confMap == null || confMap.isEmpty()) {
            synchronized (MappDeployConfigFactory.class) {
                properties = new Props();
                InputStream input = MappDeployConfigFactory.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
                properties.load(input);
                //url
                setProperties(confMap, properties, "qqSSOLoginUrl");
                setProperties(confMap, properties, "sinaSSOLoginUrl");
                setProperties(confMap, properties, "wxSSOLoginUrl");
                setProperties(confMap, properties, "connectSSOLoginUrl");
                setProperties(confMap, properties, "connectWapLoginUrl");
                setProperties(confMap, properties, "logoutUrl");
                setProperties(confMap, properties, "wapLoginUrl");
                setProperties(confMap, properties, "getSmsCodeUrl");
                setProperties(confMap, properties, "registUrl");
                setProperties(confMap, properties, "findPswUrl");
                setProperties(confMap, properties, "getloginCaptchaUrl");
                setProperties(confMap, properties, "statisticsInfoUrl");
                setProperties(confMap, properties, "statReportUrl");
                setProperties(confMap, properties, "getUserInfoUrl");
                setProperties(confMap, properties, "ssoCheckAppUrl");
                setProperties(confMap, properties, "ssoSwapSgidUrl");

                //threshold
                setProperties(confMap, properties, "statReportSizeMobile");
                setProperties(confMap, properties, "statReportSizeWifi");
                setProperties(confMap, properties, "statReportTimeSpace");
                setProperties(confMap, properties, "httpReadTimeout");
                setProperties(confMap, properties, "httpRetryTimes");

                //switch
                setProperties(confMap, properties, "isExceptionLog");
                setProperties(confMap, properties, "isNetFlowStatistics");
                setProperties(confMap, properties, "isInterfaceStatistics");
                setProperties(confMap, properties, "isProductStatistics");
                setProperties(confMap, properties, "isErrorLog");
                setProperties(confMap, properties, "isDebugLog");
            }
        }
        return confMap;
    }

    private static void setProperties(ConcurrentMap map, Props properties, String propKey) {
        if (!Strings.isNullOrEmpty(propKey)) {
            String propValue = properties.getValue(propKey);
            if (!Strings.isNullOrEmpty(propValue)) {
                map.putIfAbsent(propKey, propValue);
            }
        }
    }
}
