package com.sogou.upd.passport.manager.api.connect;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginRedirectParams;
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
     * 构造第三方登录时回调接口URL
     *
     * @param pCallbackUrl   第三方回调url
     * @param redirectParams 搜狗产品回调参数
     * @return
     */
    public static String constructRedirectURL(String pCallbackUrl, ConnectLoginRedirectParams redirectParams) {
        try {
            Map<String, Object> callbackParamMap = Maps.newTreeMap();
            String ru = URLEncoder.encode(redirectParams.getRu(), CommonConstant.DEFAULT_CHARSET);
            callbackParamMap.put(CommonConstant.RESPONSE_RU, ru);
            callbackParamMap.put("client_id", redirectParams.getClient_id());
            callbackParamMap.put("type", redirectParams.getType());
            callbackParamMap.put("ip", redirectParams.getIp());
            callbackParamMap.put("ts", redirectParams.getTs());
            callbackParamMap.put("from", redirectParams.getFrom());
            callbackParamMap.put("domain", redirectParams.getDomain());
            callbackParamMap.put("thirdInfo", redirectParams.getThirdInfo());
            callbackParamMap.put("user_agent", redirectParams.getUser_agent());
            callbackParamMap.put("v", redirectParams.getV());
            callbackParamMap.put(CommonConstant.THIRD_APPID, redirectParams.getThird_appid());
            StringBuffer query = new StringBuffer(OAuthUtils.format(callbackParamMap.entrySet(), CommonConstant.DEFAULT_CHARSET));
            return pCallbackUrl + "?" + query;
        } catch (UnsupportedEncodingException e) {
            return CommonConstant.DEFAULT_INDEX_URL;
        }
    }
}
