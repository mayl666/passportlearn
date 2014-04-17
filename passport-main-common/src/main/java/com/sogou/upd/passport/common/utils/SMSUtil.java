package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * User: mayan Date: 13-3-22 Time: 下午2:02 To change this template use File | Settings | File
 * Templates.
 */
public class SMSUtil {

    private static final
    String
            SMS_PROXY =
            "http://sms.sogou-op.org/portal/mobile/smsproxy.php?appid=sogoupassport";

    public static final long SEND_SMS_INTERVAL = 1000 * 60; // 发送短信验证码的间隔，1分钟只能发1条短信，单位ms

    public static final long MAX_SMS_COUNT_ONEDAY = 20; // 每日最多发送20短信验证码

    public static final long SMS_VALID = 30 * 60; // 短信验证码的有效期，30分钟

    public static final long SMS_ONEDAY = 24 * 60 * 60; // 每日最多发送20短信验证码，时间 一天 1440分钟 ,单位s

    public static final long MAX_CHECKSMS_COUNT_ONEDAY = 10; // 每日最多检查短信验证码错误次数

    static final Logger logger = LoggerFactory.getLogger(SMSUtil.class);

    /**
     * 发送短信代码，电话之间用逗号隔开
     */
    public static boolean sendSMS(String tel, String content) {
        try {
            String contentGBK = Coder.encode(content, "gbk");
            StringBuilder url = new StringBuilder(SMS_PROXY);
            url.append("&number=").append(tel).append("&desc=").append(contentGBK);

            RequestModel requestModel = new RequestModel(url.toString());
            requestModel.setHttpMethodEnum(HttpMethodEnum.GET);
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
    }
}
