package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mayan Date: 13-3-22 Time: 下午2:02 To change this template use File | Settings | File
 * Templates.
 */
public class SMSUtil {

    public static final
    String
            SMS_PROXY =
            "http://sms.sogou-op.org/portal/mobile/smsproxy.php";

    public static final long SEND_SMS_INTERVAL = 1000 * 60; // 发送短信验证码的间隔，1分钟只能发1条短信，单位ms

    public static final long MAX_SMS_COUNT_ONEDAY = 3; // 每日最多发送短信验证码条数

    public static final long SMS_VALID = 30 * 60; // 短信验证码的有效期，30分钟

    public static final long SMS_ONEDAY = 24 * 60 * 60; // 每日最多发送20短信验证码，时间 一天 1440分钟 ,单位s

    public static final long MAX_CHECKSMS_COUNT_ONEDAY = 10; // 每日最多检查短信验证码错误次数

    static final Logger logger = LoggerFactory.getLogger(SMSUtil.class);

    /**
     * 发送短信代码，电话之间用逗号隔开
     */
    public static boolean sendSMS(String tel, String content) {
        try {
            Map<String, Object> params = Maps.newHashMap();
            params.put("appid","sogoupassport");
            params.put("number",tel);
            params.put("desc",content);

            RequestModel requestModel = new RequestModel(SMS_PROXY);
            requestModel.setHttpMethodEnum(HttpMethodEnum.GET);
            requestModel.setParams(params);
            String result = SGHttpClient.executeStr(requestModel);
            if (result.contains("code: 00")) {
                return true;
            } else {
                logger.error("send sms error;" + result);
            }
            return false;
        } catch (Exception e) {
            logger.error("send sms error." + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(sendSMS("15210832767", "测试发送短信"));
        String str ="code: 00\n" +
                "desc: Sent to cellphone successfully, note it";
        if(str.contains("code: 00")){
            System.out.println("TEST OK");

        }
    }
}
