package com.sogou.upd.passport.model;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;

import jodd.props.Props;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午8:49
 * To change this template use File | Settings | File Templates.
 */
public class OAuthConsumerFactory {

    private static String OPEN_PROVIDER = "openProvider";
    private static String RESOURCE_NAME = "oauth_consumer.properties";

    private static Props properties = null;

    final protected static ConcurrentMap<String, OAuthConsumer> consumerMap = Maps.newConcurrentMap();

    public static OAuthConsumer getOAuthConsumer(int provider) throws IOException {

        String providerStr = AccountTypeEnum.getProviderStr(provider);
        OAuthConsumer oAuthConsumer;
        synchronized (consumerMap) {
            oAuthConsumer = consumerMap.get(buildConsumerKey(providerStr));
        }
        if (oAuthConsumer == null) {
            synchronized (OAuthConsumerFactory.class) {
                oAuthConsumer = newResource(RESOURCE_NAME, providerStr);
            }
            consumerMap.putIfAbsent(buildConsumerKey(providerStr), oAuthConsumer);
        }
        return oAuthConsumer;
    }

    private static OAuthConsumer newResource(String resourceName, String providerStr) throws IOException {
        properties = new Props();
        InputStream input = OAuthConsumer.class.getClassLoader().getResourceAsStream(resourceName);
        properties.load(input);

        OAuthConsumer oAuthConsumer = new OAuthConsumer();
        oAuthConsumer.setWebUserAuthzUrl(getURL("web_userAuthzUrl", providerStr));
        oAuthConsumer.setAccessTokenUrl(getURL("accessTokenUrl", providerStr));
        oAuthConsumer.setRefreshAccessTokenUrl(getURL("refreshAccessTokenUrl", providerStr));
        oAuthConsumer.setCallbackUrl(getURL("callbackUrl", providerStr));
        oAuthConsumer.setUserInfo(getURL("userInfo", providerStr));
        oAuthConsumer.setTokenInfo(getURL("tokenInfo", providerStr));

        oAuthConsumer.setWapUserAuthzUrl(getURL("wap_userAuthzUrl", providerStr));
        
        // QQ 获取 unionId 字段
        oAuthConsumer.setUnionIdUrl(getURL("unionIdUrl", providerStr));
        return oAuthConsumer;
    }

    private static String getURL(String name, String provider) {
        name = OPEN_PROVIDER + "." + name;
        String url = properties.getValue(name, provider);
        if (Strings.isNullOrEmpty(url)) {
            return null;
        }
        return url;
    }

    private static String buildConsumerKey(String providerStr) {
        return providerStr + "_" + RESOURCE_NAME;
    }

}
