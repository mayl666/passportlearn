package com.sogou.upd.passport.service.dataimport.fulldatacheck;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import org.junit.Test;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-13
 * Time: 下午2:50
 */
public class FullDataCheckApp extends BaseTest {

    private static final String appId = "1100";

    private static final String key = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";


    /**
     * Account表中mobile、reg_time、reg_ip和
     * Account_info表中email、birthday、gender、province、city、fullname、personalid
     */
    @Test
    public void testCheckFullData() throws Exception {

        long ct = System.currentTimeMillis();
        String code = "18910873053@sohu.com" + appId + key + ct;
        code = Coder.encryptMD5(code);

        RequestModelXml requestModelXml = new RequestModelXml("http://internal.passport.sohu.com/interface/getuserinfo", "info");
        requestModelXml.addParam("mobile", "");
        requestModelXml.addParam("createtime", "");
        requestModelXml.addParam("createip", "");
        requestModelXml.addParam("email", "");
        requestModelXml.addParam("birthday", "");
        requestModelXml.addParam("gender", "");
        requestModelXml.addParam("province", "");
        requestModelXml.addParam("city", "");
        requestModelXml.addParam("username", "");
        requestModelXml.addParam("personalid", "");
        requestModelXml.addParam("userid", "18910873053@sohu.com");
        requestModelXml.addParam("appid", appId);
        requestModelXml.addParam("ct", ct);
        requestModelXml.addParam("code", code);


        Map<String, Object> map = SGHttpClient.executeBean(requestModelXml, HttpTransformat.xml, Map.class);
        if (map != null && map.size() > 0) {
            System.out.println(map.toString());
        }


    }


}
