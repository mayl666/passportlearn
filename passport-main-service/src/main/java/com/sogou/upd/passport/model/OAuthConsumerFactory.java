package com.sogou.upd.passport.model;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import jodd.props.Props;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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

    protected static Map<String, OAuthConsumer> consumerMap = Maps.newConcurrentMap();

    public static OAuthConsumer getOAuthConsumer(int provider) throws IOException {

        String providerStr = AccountTypeEnum.getProviderStr(provider);
        OAuthConsumer oAuthConsumer = consumerMap.get(RESOURCE_NAME);
        if (oAuthConsumer == null) {
            oAuthConsumer = newResource(RESOURCE_NAME, providerStr);
        }
        synchronized (consumerMap) {
            OAuthConsumer first = consumerMap.get(RESOURCE_NAME);
            if (first == null) {
                consumerMap.put(RESOURCE_NAME, oAuthConsumer);
            } else {                /*
                 * 有可能另外一个线程构造了一个OAuthParams
				 * 用最新的
				 */
                oAuthConsumer = first;
            }
        }
        return oAuthConsumer;
    }

    private static OAuthConsumer newResource(String resourceName, String providerStr) throws IOException {
        properties = new Props();
        InputStream input = OAuthConsumer.class.getClassLoader().getResourceAsStream(resourceName);
        properties.load(input);

        OAuthConsumer oAuthConsumer = new OAuthConsumer();
        oAuthConsumer.setWebUserAuthzUrl(getURL("web_userAuthzUrl", providerStr));
        oAuthConsumer.setAppUserAuthzUrl(getURL("app_userAuthzUrl", providerStr));
        oAuthConsumer.setAccessTokenUrl(getURL("accessTokenUrl", providerStr));
        oAuthConsumer.setRefreshAccessTokenUrl(getURL("refreshAccessTokenUrl", providerStr));
        oAuthConsumer.setOpenIdUrl(getURL("openIdUrl", providerStr));
        oAuthConsumer.setCallbackUrl(getURL("callbackUrl", providerStr));
        oAuthConsumer.setUserInfo(getURL("userInfo", providerStr));
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
}
