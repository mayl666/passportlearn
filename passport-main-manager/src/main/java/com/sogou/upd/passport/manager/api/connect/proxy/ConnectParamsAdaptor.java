package com.sogou.upd.passport.manager.api.connect.proxy;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ConnectUtil;
import com.sogou.upd.passport.manager.api.connect.form.proxy.BaseConnectParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.MailConnectParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.QzoneConnectParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.WeiboConnectParams;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 第三方代理接口的参数适配类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午6:29
 * To change this template use File | Settings | File Templates.
 */
public class ConnectParamsAdaptor {

    public static Map<String, Object> CONNECT_INFO_MAP = Maps.newHashMap();

    @Autowired
    private ConnectConfigService connectConfigService;

    static {
        //第三方信息映射
        CONNECT_INFO_MAP.put("/internal/connect/qq/user/qzone/unread_num", new QzoneConnectParams());
        CONNECT_INFO_MAP.put("/internal/connect/qq/user/weibo/unread_num", new WeiboConnectParams());
        CONNECT_INFO_MAP.put("/internal/connect/qq/user/mail/unread_num", new MailConnectParams());
    }

    public static Object getCONNECT_CODE_MSG(String key) {
        return CONNECT_INFO_MAP.get(key);
    }


    public HashMap<String, Object> buildCommonParams(String sgUrl, Map<String, String> tokenMap, Map<String, Object> paramsMap) throws IllegalAccessException, InstantiationException {
        BaseConnectParams baseConnectParams = (BaseConnectParams)getCONNECT_CODE_MSG(sgUrl).getClass().newInstance();
        String openId = tokenMap.get("open_id").toString();
        String accessToken = tokenMap.get("access_token").toString();
        //获取搜狗在第三方开放平台的appkey和appsecret
        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(Integer.parseInt(tokenMap.get("client_id")), AccountTypeEnum.QQ.getValue());
        String sgAppKey = connectConfig.getAppKey();
        String protocol = CommonConstant.HTTPS;
        HashMap<String, Object> sigMap = new HashMap();
        String regularParams = ConnectUtil.getCONNECT_CODE_MSG("qq");
        String[] regularArray = regularParams.split("\\|");
        sigMap.put(regularArray[0], sgAppKey);
        sigMap.put(regularArray[1], openId);
        sigMap.put(regularArray[2], accessToken);
        if (paramsMap != null) {
            //应用传入的参数添加至map中
            Set<Map.Entry<String, Object>> entrys = paramsMap.entrySet();
            if (!CollectionUtils.isEmpty(entrys) && entrys.size() > 0) {
                for (Map.Entry<String, Object> entry : entrys) {
                    sigMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        String method = CommonConstant.CONNECT_METHOD_POST;
        //如果是http请求，则需要算签名
        if (protocol.equals(CommonConstant.HTTP)) {
            // 签名密钥
            String secret = CommonConstant.APP_CONNECT_SECRET + "&";
            // 计算签名
            String sig = null;// QQSigUtil.makeSig(method, apiUrl, sigMap, secret);
            sigMap.put(regularArray[3], sig);
        }
        return sigMap;
    }
}
