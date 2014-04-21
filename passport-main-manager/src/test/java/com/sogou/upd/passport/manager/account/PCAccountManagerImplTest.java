package com.sogou.upd.passport.manager.account;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-29
 * Time: 下午8:57
 * To change this template use File | Settings | File Templates.
 */
public class PCAccountManagerImplTest extends BaseTest {
    @Autowired
    private PCAccountManager pcAccountManager;
    public static String userid = "tinkame710@sogou.com";
    public static String accesstoken = "";
    public static String refreshtoken = "";
    public static final String INSTANCEID = "935972396";


    @Test
    public void testCreatePairToken() {
        try {
            PcPairTokenParams params = new PcPairTokenParams();
            params.setAppid(String.valueOf(1044));
            params.setUserid(userid);
            params.setPassword(Coder.encryptMD5("123456"));
            params.setTs("2147483647");

            Result result = pcAccountManager.createPairToken(params, modifyIp);
            Assert.assertTrue(result.isSuccess());
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            accesstoken = accountToken.getAccessToken();
            refreshtoken = accountToken.getRefreshToken();
            Assert.assertTrue(accesstoken != null);
            Assert.assertTrue(refreshtoken != null);

            System.out.println("getPassportId:" + accountToken.getPassportId() + ",getInstanceId:" + accountToken.getInstanceId() +
                    ",getAccessToken:" + accountToken.getAccessToken() + ",getAccessValidTime:" + accountToken.getAccessValidTime() + ",getRefreshToken:"
                    + accountToken.getRefreshToken() + ",getRefreshValidTime:" + accountToken.getRefreshValidTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAuthToken() {
        try {
            testCreatePairToken();

            PcAuthTokenParams params = new PcAuthTokenParams();
            params.setAppid(String.valueOf(1044));
            params.setUserid(userid);
            params.setAuthtype("0");
            params.setToken(accesstoken);
            params.setTs("2147483647");

            Result result = pcAccountManager.authToken(params);
            Assert.assertTrue(result.isSuccess());

            System.out.println("testAuthToken:" + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateAccountToken() {
        try {
            Result result = pcAccountManager.createAccountToken(userid, INSTANCEID, 1044);
            Assert.assertTrue(result.isSuccess());
            AccountToken accountToken = (AccountToken) result.getDefaultModel();

            Assert.assertTrue(!StringUtils.isBlank(accountToken.getAccessToken()));
            Assert.assertTrue(!StringUtils.isBlank(accountToken.getRefreshToken()));
            Assert.assertEquals(userid, accountToken.getPassportId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAuthRefreshToken() {
        try {
            testCreatePairToken();

            PcRefreshTokenParams params = new PcRefreshTokenParams();
            params.setAppid(String.valueOf(1044));
            params.setUserid(userid);
            params.setAuthtype("0");
            params.setRefresh_token(refreshtoken);
            params.setTs("2147483647");

            Result result = pcAccountManager.authRefreshToken(params);
            Assert.assertTrue(result.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBrowserBbsUniqname() {
        String passportId = "shipengzhi1986@sogou.com";
        String uniqname = pcAccountManager.getBrowserBbsUniqname(passportId);
        Assert.assertTrue(!StringUtils.isBlank(uniqname));
    }


    @Test
    public void testGetUniqnameByClientId() {
        String passportId = "shipengzhi1986@sogou.com";
        String uniqname = pcAccountManager.getUniqnameByClientId(passportId, 1044);
        Assert.assertTrue(!StringUtils.isBlank(uniqname));
    }

}