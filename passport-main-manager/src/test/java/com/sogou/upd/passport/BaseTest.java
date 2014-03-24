package com.sogou.upd.passport;

import com.sogou.upd.passport.common.CommonConstant;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午1:19
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class BaseTest extends AbstractJUnit4SpringContextTests {

    protected static final String userid = "fdretgt4@qq.sohu.com";
    protected static final String userid_uniqname = "fdretgt4";
    protected static final String userid_sogou = "liulingfast@sogou.com";
    protected static final String userid_sogou_uniqname = "liulingfast";

    protected static final String userid_mail = "liulingfast@163.com";

    protected static final String mobile = "13581695053";

    protected static final String password = "111111";

    protected static final String uniqname = "阿沐";

    protected static final int clientId = CommonConstant.SGPP_DEFAULT_CLIENTID;

    protected static final String serverSecret = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";

    protected static final String modifyIp = "10.1.164.160";

    protected static final String question = "测试啊，我是来测试的";

    protected static final String answer = "测试成功";

    protected static final String ru = "https://account.sogou.com";
    //用户个人信息
    protected static final String birthday = "1987-02-23";
    protected static final String gender = "1";
    protected static final String province = "370000";
    protected static final String city = "370501";
    protected static final String fullname = "阿沐的歌";
    protected static final String personalid = "110107198302139342";
    protected static final String new_uniqname = "阿沐是来测试的";
    protected static final String fields = "province,city,gender,birthday,fullname,personalid";
}
