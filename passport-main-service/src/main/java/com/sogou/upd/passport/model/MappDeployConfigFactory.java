package com.sogou.upd.passport.model;

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
                confMap.putIfAbsent("qqSSOLoginUrl", properties.getValue("qqSSOLoginUrl"));
                confMap.putIfAbsent("sinaSSOLoginUrl", properties.getValue("sinaSSOLoginUrl"));
                confMap.putIfAbsent("wxSSOLoginUrl", properties.getValue("wxSSOLoginUrl"));
                confMap.putIfAbsent("connectSSOLoginUrl", properties.getValue("connectSSOLoginUrl"));
                confMap.putIfAbsent("connectWapLoginUrl", properties.getValue("connectWapLoginUrl"));
                confMap.putIfAbsent("logoutUrl", properties.getValue("logoutUrl"));
                confMap.putIfAbsent("wapLoginUrl", properties.getValue("wapLoginUrl"));
                confMap.putIfAbsent("getSmsCodeUrl", properties.getValue("getSmsCodeUrl"));
                confMap.putIfAbsent("registUrl", properties.getValue("registUrl"));
                confMap.putIfAbsent("findPswUrl", properties.getValue("findPswUrl"));
                confMap.putIfAbsent("getloginCaptchaUrl", properties.getValue("getloginCaptchaUrl"));
                confMap.putIfAbsent("statisticsInfoUrl", properties.getValue("statisticsInfoUrl"));
                confMap.putIfAbsent("statReportUrl", properties.getValue("statReportUrl"));

                //threshold
                confMap.putIfAbsent("statReportSizeMobile", properties.getValue("statReportSizeMobile"));
                confMap.putIfAbsent("statReportSizeWifi", properties.getValue("statReportSizeWifi"));
                confMap.putIfAbsent("statReportTimeSpace", properties.getValue("statReportTimeSpace"));
                confMap.putIfAbsent("httpReadTimeout", properties.getValue("httpReadTimeout"));
                confMap.putIfAbsent("httpRetryTimes", properties.getValue("httpRetryTimes"));

                //switch
                confMap.putIfAbsent("isExceptionInfo", properties.getValue("isExceptionInfo"));
                confMap.putIfAbsent("isTrafficStatistics", properties.getValue("isTrafficStatistics"));
                confMap.putIfAbsent("isInterfaceStatistics", properties.getValue("isInterfaceStatistics"));
                confMap.putIfAbsent("isProductStatistics", properties.getValue("isProductStatistics"));
                confMap.putIfAbsent("isDebugLog", properties.getValue("isDebugLog"));
            }
        }
        return confMap;
    }


}
