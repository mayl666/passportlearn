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
    protected static final String userid_connect = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";
    protected static final String userid_sohu = "13581695053@sohu.com";
    protected static final String userid_email = "loveerin9460@163.com";
    protected static final String userid_sogou_1 = "liulingtest01@sogou.com";
    protected static final String userid_sogou_2 = "osadnfdf@sogou.com";
    protected static final String userid_invild = "osadnfdf4r";
    protected static final String mobile_userid_sogou = "shipengzhi1986@sogou.com";
    //随机生成手机号码
    protected String new_mobile = new GeneratorRandomMobile().generateRandomMobile();
    protected static final String mobile_1 = "15737126381";    //已经绑定了某主账号的手机号
    protected static final String mobile_2 = "13581695053";
    //密码
    protected static final String password = "111111";
    //绑定的手机
    protected static final String mobile_sohu = "13581695053";
    protected static final String mobile_sogou_1 = null;
    protected static final String mobile_sogou_2 = null;
    protected static final String mobile_email = null;
    //注册时间
    protected static final String reg_time_sohu = "2014-3-24 22:26:49";
    protected static final String reg_time_sogou_1 = "2014-3-24 22:35:52";
    protected static final String reg_time_sogou_2 = "2014-3-25 11:22:04";
    protected static final String reg_time_email = "2014-3-24 22:28:11";
    //注册ip
    protected static final String reg_ip_sohu = "10.129.192.128";
    protected static final String reg_ip_sogou_1 = "10.129.192.121";
    protected static final String reg_ip_sogou_2 = "10.129.192.29";
    protected static final String reg_ip_email = "10.129.192.131";
    //用户状态
    protected static final String flag_sohu = "1";
    protected static final String flag_sogou_1 = "1";
    protected static final String flag_sogou_2 = "1";
    protected static final String flag_email = "1";
    //密码加密类型
    protected static final String pwdtype_sohu = "2";
    protected static final String pwdtype_sogou_1 = "2";
    protected static final String pwdtype_sogou_2 = "2";
    protected static final String pwdtype_email = "2";
    //账号类型
    protected static final String acctype_sohu = "2";
    protected static final String acctype_sogou_1 = "1";
    protected static final String acctype_sogou_2 = "1";
    protected static final String acctype_email = "1";
    //昵称
    protected static final String uniqname_sohu = null;
    protected static final String uniqname_sogou_1 = "阿沐测试01";
    protected static final String uniqname_sogou_2 = null;
    protected static final String uniqname_email = null;

    protected static final int clientId = 2009;

    protected static final String serverSecret = "Hpi%#ZT<u@hR.6F)HtfvUKf5ERYR1b";

    protected static final String modifyIp = "127.0.0.1";

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
