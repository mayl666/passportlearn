package com.sogou.upd.passport.manager.account;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
public class SecureManagerTest extends BaseTest {
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private AccountInfoManager accountInfoManager;

    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final int CLIENT_ID = 999;
    private static final String NEW_MOBILE = "13800000000";
    private static final String EMAIL = "Binding123@163.com";
    private static final String NEW_EMAIL = "hujunfei1986@126.com";
    private static final String QUESTION = "Secure question";
    private static final String NEW_QUESTION = "New secure question";
    private static final String ANSWER = "Secure answer";
    private static final String NEW_ANSWER = "New secure answer";

    @Test
    public void testActionRecord() {
        for (int i = 0; i < 15; i++) {
            secureManager.logActionRecord(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.LOGIN, "202.106.180." + (i + 1), null);
        }
        Result result = secureManager.queryActionRecords(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.LOGIN);
        System.out.println(result.toString());
    }

    /**
     * 安全 获取用户信息单元测试
     */
    @Test
    public void testQueryAccountSecureInfo() throws Exception {
        int clientId = 1120;
        boolean process = true;
        String userId = userid_sogou_1;
        String expectString1 = "{\"data\":{\"last_login_loc\":\"IP归属地未知\",\"reg_email\":null,\"sec_ques\":\"测试问题\",\"last_login_time\":\"1375874464863\",\"sec_score\":\"30\",\"userid\":\"liulingtest01@sogou.com\",\"sec_email\":\"\",\"sec_mobile\":\"\",\"uniqname\":\"%E9%98%BF%E6%B2%90%E6%B5%8B%E8%AF%9501\",\"avatarurl\":{\"img_50\":\"http://img02.sogoucdn.com/app/a/100140007/Ti78RREtsRL63r64_1395717724516\"}},\"statusText\":\"查询成功\",\"status\":\"0\"}";
        APIResultForm expectForm1 = JacksonJsonMapperUtil.getMapper().readValue(expectString1, APIResultForm.class);
        Result actualResult1 = secureManager.queryAccountSecureInfo(userId, clientId, process);
        Map map = Maps.newHashMap();
        map.put("img_50", "http://img02.sogoucdn.com/app/a/100140007/Ti78RREtsRL63r64_1395717724516");
        actualResult1.setDefaultModel("avatarurl", map);
        APIResultForm actualForm1 = JacksonJsonMapperUtil.getMapper().readValue(actualResult1.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm1.equals(actualForm1));
        //手机账号获取全属性的个人资料
        userId = userid_phone;
        String expectString3 = "{\"statusText\":\"查询成功\",\"data\":{\"last_login_loc\":\"局域网\",\"reg_email\":null,\"sec_ques\":\"\",\"last_login_time\":\"1405839374395\",\"sec_score\":\"30\",\"userid\":\"13581695053@sohu.com\",\"sec_email\":\"\",\"sec_mobile\":\"13581695053\",\"uniqname\":\"13581695053\",\"avatarurl\":\"\"},\"status\":\"0\"}";
        APIResultForm expectForm3 = JacksonJsonMapperUtil.getMapper().readValue(expectString3, APIResultForm.class);
        Result actualResult3 = secureManager.queryAccountSecureInfo(userId, clientId, false);
        APIResultForm actualForm3 = JacksonJsonMapperUtil.getMapper().readValue(actualResult3.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm3.equals(actualForm3));
        //外域邮箱账号获取全属性的个人资料
        userId = userid_email;
        String expectString4 = "{\"data\":{\"last_login_loc\":\"局域网\",\"reg_email\":\"lo*****0@163.com\",\"sec_ques\":\"\",\"last_login_time\":\"1405839464963\",\"sec_score\":\"30\",\"userid\":\"loveerin9460@163.com\",\"sec_email\":\"loveerin9460@163.com\",\"sec_mobile\":\"\",\"uniqname\":\"loveerin9460\",\"avatarurl\":\"\"},\"status\":\"0\",\"statusText\":\"查询成功\"}";
        APIResultForm expectForm4 = JacksonJsonMapperUtil.getMapper().readValue(expectString4, APIResultForm.class);
        Result actualResult4 = secureManager.queryAccountSecureInfo(userId, clientId, process);
        APIResultForm actualForm4 = JacksonJsonMapperUtil.getMapper().readValue(actualResult4.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm4.equals(actualForm4));
        //第三方账号获取全属性的个人资料，且account_info表里没记录
        userId = userid_connect;
        String expectString5 = "{\"data\":{\"last_login_loc\":null,\"reg_email\":null,\"sec_ques\":null,\"last_login_time\":\"0\",\"sec_score\":\"0\",\"userid\":\"CFF81AB013A94663D83FEC36AC117933@qq.sohu.com\",\"sec_email\":null,\"sec_mobile\":\"\",\"uniqname\":\"阿沐\",\"avatarurl\":\"http://q.qlogo.cn/qqapp/100294784/CFF81AB013A94663D83FEC36AC117933/100\"},\"statusText\":\"查询成功\",\"status\":\"0\"}";
        APIResultForm expectForm5 = JacksonJsonMapperUtil.getMapper().readValue(expectString5, APIResultForm.class);
        Result actualResult5 = secureManager.queryAccountSecureInfo(userId, clientId, process);
        APIResultForm actualForm5 = JacksonJsonMapperUtil.getMapper().readValue(actualResult5.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm5.equals(actualForm5));
        //不存在的账号获取全属性的个人资料，且account_info表里没记录
        userId = userid_invild;
        String expectString6 = "{\"data\":{\"uniqname\":\"null\",\"avatarurl\":{\"img_50\":\"null\"}},\"statusText\":\"账号不存在\",\"status\":\"20205\"}";
        APIResultForm expectForm6 = JacksonJsonMapperUtil.getMapper().readValue(expectString6, APIResultForm.class);
        Result actualResult6 = secureManager.queryAccountSecureInfo(userId, clientId, process);
        APIResultForm actualForm6 = JacksonJsonMapperUtil.getMapper().readValue(actualResult6.toString(), APIResultForm.class);
        Assert.assertTrue(expectForm6.equals(actualForm6));
    }

    @Test
    public void testGetSecureInfoUniqName() {
        int clientId = 1120;
        //搜狗账号获取全属性的个人资料
        String passportId = userid_sogou_1;
        String expectString = accountInfoManager.getUserUniqName(passportId, clientId, true);
        Result result = secureManager.queryAccountSecureInfo(passportId,clientId,false);
        String actualString = (String) result.getModels().get("uniqname");
        Assert.assertTrue(expectString.equals(actualString));
        //个性账号获取全属性的个人资料
        passportId = userid_sogou_1_another;
        expectString = accountInfoManager.getUserUniqName(passportId, clientId, true);
        result = secureManager.queryAccountSecureInfo(passportId,clientId,false);
        actualString = (String) result.getModels().get("uniqname");
        Assert.assertTrue(expectString.equals(actualString));
        //手机账号获取全属性的个人资料
        passportId = userid_phone;
        expectString = accountInfoManager.getUserUniqName(passportId, clientId, true);
        result = secureManager.queryAccountSecureInfo(passportId,clientId,false);
        actualString = (String) result.getModels().get("uniqname");
        Assert.assertTrue(expectString.equals(actualString));
        //外域邮箱账号获取全属性的个人资料
        passportId = userid_email;
        expectString = accountInfoManager.getUserUniqName(passportId, clientId, true);
        result = secureManager.queryAccountSecureInfo(passportId,clientId,false);
        actualString = (String) result.getModels().get("uniqname");
        Assert.assertTrue(expectString.equals(actualString));
        //第三方账号获取全属性的个人资料，且account_info表里没记录
        passportId = userid_connect;
        expectString = accountInfoManager.getUserUniqName(passportId, clientId, true);
        result = secureManager.queryAccountSecureInfo(passportId,clientId,false);
        actualString = (String) result.getModels().get("uniqname");
        Assert.assertTrue(expectString.equals(actualString));
        //不存在的账号获取全属性的个人资料，且account_info表里没记录
        passportId = userid_invild;
        expectString = accountInfoManager.getUserUniqName(passportId, clientId, true);
        result = secureManager.queryAccountSecureInfo(passportId,clientId,false);
        actualString = (String) result.getModels().get("uniqname");
        Assert.assertTrue(expectString.equals(actualString));
    }

}
