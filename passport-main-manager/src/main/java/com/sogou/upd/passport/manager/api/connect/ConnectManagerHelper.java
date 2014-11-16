package com.sogou.upd.passport.manager.api.connect;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-30
 * Time: 上午1:24
 * To change this template use File | Settings | File Templates.
 */
public class ConnectManagerHelper {

    /**
     * 构造第三方登录时回调接口的参数
     * @param type  不同终端的登录类型
     * @param instanceId 客户端实例ID，实现不同PC客户端登录状态分离
     * @param pCallbackUrl 第三方开平url
     * @param from  和type搭配使用
     * @param domain  非sogou.com域时需传递
     * @param thirdInfo  是否需要个人信息
     * @param userAgent  输入法PC客户端根据ua判断显示不同的错误页面
     * @param v  浏览器PC客户端根据v判断显示新旧UI样式
     * @param thirdAppId 应用传递自己的第三方appid
     * @return
     */
    public static String constructRedirectURI(int clientId, String ru, String type, String instanceId, String pCallbackUrl, String ip, String from,String domain,String thirdInfo, String userAgent, String v, String thirdAppId) {
        try {
            ru = URLEncoder.encode(ru, CommonConstant.DEFAULT_CHARSET);
            Map<String, Object> callbackParams = Maps.newHashMap();
            callbackParams.put(CommonConstant.CLIENT_ID, clientId);
            callbackParams.put(CommonConstant.RESPONSE_RU, ru);
            callbackParams.put("type", type);
            callbackParams.put("ip", ip);
            callbackParams.put("ts", instanceId);
            if(!Strings.isNullOrEmpty(from)){
                callbackParams.put("from", from);
            }
            if(!Strings.isNullOrEmpty(domain)){
                callbackParams.put("domain", domain);
            }
            if(!Strings.isNullOrEmpty(thirdInfo)){
                callbackParams.put("thirdInfo", thirdInfo);
            }
            if(!Strings.isNullOrEmpty(userAgent)){
                callbackParams.put(CommonConstant.USER_AGENT, userAgent);
            }
            if(!Strings.isNullOrEmpty(v)){
                callbackParams.put(CommonConstant.BROWER_VERSION, v);
            }
            if(!Strings.isNullOrEmpty(thirdAppId)){
                callbackParams.put(CommonConstant.THIRD_APPID, thirdAppId);
            }
            StringBuffer query = new StringBuffer(OAuthUtils.format(callbackParams.entrySet(), CommonConstant.DEFAULT_CHARSET));
            return pCallbackUrl + "?" + query;
        } catch (UnsupportedEncodingException e) {
            return CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
    }
}
