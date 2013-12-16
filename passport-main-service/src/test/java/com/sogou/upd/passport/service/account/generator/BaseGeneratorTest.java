package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午2:38
 * To change this template use File | Settings | File Templates.
 */
public class BaseGeneratorTest extends BaseTest {

    public static String PASSPORT_ID_PHONE = "13621009174@sohu.com";
    public static int CLIENT_ID = 1001;
    public static String CLIENT_SECRET = "40db9c5a312a145e8ee8181f4de8957334c5800a";
    public static String INSTANCE_ID = "02020110011111F4E7587A9D4893420EB97D1C1365DF95";
    public static int EXPIRES_IN = 3600 * 24;

    public static String PASSWORD = "111111";

    public static String PHONE = "13621009174";
    public static String EMAIL = "shipengzhi@sogou.com";
    public static String PASSPORT_ID_EMAIL = EMAIL;
    public static String OPENID = "166654548";
    public static int PROVIDER = 4;
    public static String PASSPORT_ID_OPENID = OPENID + "@" + AccountTypeEnum.getProviderStr(PROVIDER) + ".sohu.com";



}
