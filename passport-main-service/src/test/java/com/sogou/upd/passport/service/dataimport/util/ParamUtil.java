package com.sogou.upd.passport.service.dataimport.util;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-6-13
 * Time: 下午7:31
 */
public class ParamUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamUtil.class);

    private static final String appId = "1120";

    private static final String key = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";


    private static final String REQUEST_URL = "http://internal.passport.sohu.com/interface/getuserinfo";

    private static final String REQUEST_INFO = "info";

    /**
     * 构建请求参数
     *
     * @return
     */
    public static RequestModelXml buildRequestModelXml(String passportId) {

        RequestModelXml requestModelXml = new RequestModelXml(REQUEST_URL, REQUEST_INFO);
        try {
            long ct = System.currentTimeMillis();
            String code = passportId + appId + key + ct;
            code = Coder.encryptMD5(code);

//            requestModelXml.addParam("question", "");
//            requestModelXml.addParam("mobile", "");
//            requestModelXml.addParam("mobileflag", "");
//            requestModelXml.addParam("createtime", "");
//            requestModelXml.addParam("createip", "");
//            requestModelXml.addParam("email", "");
//            requestModelXml.addParam("emailflag", "");
//            requestModelXml.addParam("birthday", "");
//            requestModelXml.addParam("gender", "");
//            requestModelXml.addParam("province", "");
//            requestModelXml.addParam("city", "");
//            requestModelXml.addParam("username", "");
//            requestModelXml.addParam("personalid", "");
            requestModelXml.addParam("userid", passportId);
            requestModelXml.addParam("uniqname", "");
            requestModelXml.addParam("appid", appId);
            requestModelXml.addParam("ct", ct);
            requestModelXml.addParam("code", code);
        } catch (Exception e) {
            LOGGER.error("build RequestModelXml error.", e);
            e.printStackTrace();
        }
        return requestModelXml;
    }
}
