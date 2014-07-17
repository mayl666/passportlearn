package com.sogou.upd.passport;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-1-9
 * Time: 下午6:54
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class VerifySgrdigCookieTest extends BaseTest {

    @Autowired
    private LoginApiManager sgLoginApiManager;

    @Test
    public void testVerifySgrdig() {
        try {
            List<String> openidList = FileIOUtil.readFileByLines("D:\\daohang_openid_100w.txt");
            FileOutputStream fos = new FileOutputStream("d:/verify_sgrdig_error.txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            long start = System.currentTimeMillis();
            for (String openid : openidList) {
                if (isOpenid(openid)) {
                    String passportId = openid + "@qq.sohu.com";
                    int client_id = 1120;
                    // userid, client_id, ru, ip, uniqname, refnick
                    CookieApiParams cookieApiParams = new CookieApiParams(passportId, client_id, "", "", "", "");
                    Result result = sgLoginApiManager.getCookieInfo(cookieApiParams);
                    if (result.isSuccess()) {
                        Map map = result.getModels();
                        String sginf = (String) map.get("sginf");
                        String sgrdig = (String) map.get("sgrdig");
                        RequestModel requestModel = new RequestModel("http://10.11.195.95/");
                        requestModel.addHeader("Cookie", "sginf=" + sginf + ";" + "sgrdig=" + sgrdig);
                        try{
                        String response = SGHttpClient.executeStr(requestModel);

                        if (Strings.isNullOrEmpty(response) || !response.equals(passportId)) {
                            bw.write("verifyCookieInfoError:" + openid + "\n");
                        }
                        }catch (Exception e){
                            bw.write("timeoutOpenid:" + openid + "\n");
                             continue;
                        }
                    } else {
                        bw.write("getCookieInfoError:" + openid + "\n");
                    }
                } else {
                    bw.write("notIsOpenid:" + openid + "\n");
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * 验证openid是否合法
     */
    private boolean isOpenid(String openid) {
        return (openid.length() == 32) && openid.matches("^[0-9A-Fa-f]+$");
    }
}
