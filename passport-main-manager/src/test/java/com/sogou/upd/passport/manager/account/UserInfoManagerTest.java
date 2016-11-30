package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户信息单元数据比对测试
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-26
 * Time: 下午8:47
 */
public class UserInfoManagerTest extends BaseTest {


    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;

    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;


    private static final String userid = "gang.chen0505@gmail.com";
    private static final int clientId = 1120;

    //    private static final String fileds = "province,city,gender,birthday,fullname,personalid";

    private static final String secure_files = "email,mobile,question";

    private static final String fileds = "birthday,gender,sec_mobile,sec_email,personalid,username";
    private static final String fileds1 = "uniqname,avatarurl";

    private static final String appId = "1100";

    private static final String key = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";


    public GetUserInfoApiparams initParams() {
        //构建参数
        GetUserInfoApiparams params = getUserInfoApiParams(userid, fileds1);
        return params;
    }


    @Test
    public void testCheckNickName() {
        //检查昵称
        String nickName = "阿瓦达的我0903";

        UpdateUserUniqnameApiParams params = new UpdateUserUniqnameApiParams();
        params.setUniqname(nickName);

        Result result = sgUserInfoApiManager.checkUniqName(params);
        Assert.assertEquals(true, result.isSuccess());
        try {
            String clientId = "1100";
            long ct = System.currentTimeMillis();
            String code = Coder.encryptMD5(nickName + clientId + key + ct);
            System.out.println("==============code :" + code + "  ct " + ct);
        } catch (Exception e) {
            e.getMessage();
        }

    }


}
