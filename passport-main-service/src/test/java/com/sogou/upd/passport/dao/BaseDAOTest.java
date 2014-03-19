package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午1:19
 * To change this template use File | Settings | File Templates.
 */
public class BaseDAOTest extends BaseTest {

    // Account Test Constant
    public static final String PASSPORT_ID = "14100700000@sohu.com";
    public static final String NEW_PASSPORT_ID = "13600000001@sohu.com";
    public static final String MOBILE = "13600000000";
    public static final String SID = "120000";
    public static final String SNAME = "sohuplus_name";
    public static final String PASSWORD = "111111";
    public static final String NEW_PASSWORD = "123456";
    public static final int ACCOUNT_TYPE = 2; // 手机账号
    public static final String IP = "10.1.164.65";
    public static final String FLAG = "1";
    public static final String PWDTYPE = "1";

    //Account Test Constant liuling 2014.3.19
    public static final String PASSPORTID_SOGOU = "liuling@sogou.com";
    public static final String PASSPORTID_PHONE = "13730129087@sohu.com";
    public static final String PHONE = "13730129087";
    public static final String PASSPORTID_MAIL = "liuling9460@163.com";
    public static final int PHONE_ACCOUNT_TYPE = AccountTypeEnum.PHONE.getValue(); // 手机账号
    public static final int MAIL_ACCOUNT_TYPE = AccountTypeEnum.EMAIL.getValue(); // 邮箱账号
    public static final int ORIGINAL_PASSWORD_TYPE = PasswordTypeEnum.ORIGINAL.getValue(); // 无密码
    public static final int MD5_PASSWORD_TYPE = PasswordTypeEnum.MD5.getValue(); // MD5
    public static final int CRYPT_PASSWORD_TYPE = PasswordTypeEnum.CRYPT.getValue(); // 加盐
    public static final String UNIQNAME = "iamhero"; // 加盐



    // AccountInfo Test Constant ---hjf 2013.5.3
    public static final String EMAIL = "Binding123@163.com";
    public static final String NEW_EMAIL = "NewBinding123@163.com";
    public static final String QUESTION = "Secure question";
    public static final String NEW_QUESTION = "New secure question";
    public static final String ANSWER = "Secure answer";
    public static final String NEW_ANSWER = "New secure answer";

    // AccountToken Test Constant
    public static final int CLIENT_ID = 1001;
    public static final String OTHER_INSTANCE_ID = "02020110011111F4E7587A9D4893242DWE97D1C1365DF95";
    public static final
    String
            REFRESH_TOKEN =
            "oFohPgnYrnAC79ZRf1wdkxPbR0A3opImhXgRGDpJntExkRqf7flwUvzteCYNpQEQwO6hhSRleH97riuy6heBNdo7H2jlIeXvqqlNd-Fh2ao_dOZG0Zf67822RSIwfrzQ";
    public static final
    String
            ACCESS_TOKEN =
            "KWp2N45BvYQqW53-LAPpgTFs_Dc7KD1Z0KZQFq7iRh-OWWNguVWrNXe5rSuo74olPsVCVKB4OjP7gFgVIkGMjQ7EEbCOaOQgkkfzXf_n2AqvWCm3aQF3s25r_6-9W6EtUiFKVWH7epzOdzRVzslEK92tLICaAm1yLARXn33BXks";
    public static final String INSTANCE_ID = "02020110011111F4E7587A9D4893420EB97D1C1365DF95";

    // Connect Test Constant
    public static final String OPENID = "shipengzhi";
    public static final String APP_KEY = "123";
    public static final String OTHER_APP_KEY = "456";

    // Problem Test Constant
    public static final int PRO_STATUS = 0;
    public static final int PRO_TYPE_ID = 1;
    public static final String CONTENT = "在同一款游戏中累计充值1000元以上，即可以成为黄金VIP； 2.在同一款游戏中累计充值10000元以上，即可以成为白金VIP。成为白金VIP的用户将自动获得黄金VIP的一切特权"
            + "请问这个所谓的特权在哪里??? 怎么感觉龙将2这游戏没人管啊.. 要更新什么东西也没有一个提前公告,"
            + "是你们平台无所谓 还是不想让我们好好玩?";

}
