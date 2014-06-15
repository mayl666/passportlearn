package com.sogou.upd.passport;

import com.google.common.base.Strings;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-23
 * Time: 上午1:19
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class BaseTest extends AbstractJUnit4SpringContextTests {
    //主账号
    protected static final String userid = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";

    //随机生成手机号码
    protected String new_mobile = new GeneratorRandomMobile().generateRandomMobile();

    protected static final String password = "111111";

    protected static final String uniqname = "你好";

    protected static final int clientId = 2009;

    protected static final String serverSecret = "Hpi%#ZT<u@hR.6F)HtfvUKf5ERYR1b";

    protected static final String modifyIp = "10.1.164.160";

    protected static final String ip = "172.165.0.32";

    protected static final String question = "测试啊，我是来测试的";

    protected static final String answer = "测试成功";

    class GeneratorRandomMobile {
        //生成随机的手机号码
        private String generateRandomMobile() {
            String mobile = "135";
            DecimalFormat a = new DecimalFormat("00000000");//随机到非7位数时前面加0
            mobile = mobile + a.format((int) (Math.random() * 4720001));//随机数0-4720000
            return mobile;
        }
    }

    //构建检查昵称是否唯一参数
    protected UpdateUserUniqnameApiParams getUpdateUserUniqnameApiParams(String uniqname) {
        UpdateUserUniqnameApiParams params = new UpdateUserUniqnameApiParams();
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(uniqname, clientId, serverSecret, ct);
        params.setUniqname(uniqname);
        params.setClient_id(clientId);
        params.setCt(ct);
        params.setCode(code);
        return params;
    }

    //构造更新用户信息参数
    protected UpdateUserInfoApiParams getUpdateUserInfoApiParams(String userid, String uniqname, String fullname, String province, String city, String gender, String personalId, String birthday, String username) {
        UpdateUserInfoApiParams params = new UpdateUserInfoApiParams();
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(userid, clientId, serverSecret, ct);
        params.setUniqname(uniqname);
        params.setClient_id(clientId);
        params.setCt(ct);
        params.setCode(code);
        params.setModifyip(modifyIp);
        params.setUserid(userid);
        if (!Strings.isNullOrEmpty(fullname)) {
            params.setUsername(fullname);
            params.setFullname(fullname);
        }
        if (!Strings.isNullOrEmpty(province)) params.setProvince(province);
        if (!Strings.isNullOrEmpty(city)) params.setCity(city);
        if (!Strings.isNullOrEmpty(gender)) params.setGender(gender);
        if (!Strings.isNullOrEmpty(personalId)) params.setPersonalid(personalId);
        if (!Strings.isNullOrEmpty(birthday)) params.setBirthday(birthday);
        return params;
    }

    //构造获取用户信息参数
    protected GetUserInfoApiparams getUserInfoApiParams(String fields, String userid) {
        GetUserInfoApiparams params = new GetUserInfoApiparams();
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(userid, clientId, serverSecret, ct);
        params.setFields(fields);
        params.setUserid(userid);
        params.setClient_id(clientId);
        params.setCode(code);
        params.setCt(ct);
        return params;
    }
}
