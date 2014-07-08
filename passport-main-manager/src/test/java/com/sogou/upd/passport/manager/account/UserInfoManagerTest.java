package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JsonUtil;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
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


    public GetUserInfoApiparams initParams() {
        //构建参数
        GetUserInfoApiparams params = getUserInfoApiParams(userid, fileds1);
        return params;
    }

    /**
     * 数据返回对比测试
     * <p/>
     * sgUserInfoApiManager
     * <p/>
     * <p/>
     * proxyUserInfoApiManager
     */
    @Test
    public void testCheckGetUserInfo() {

//        GetUserInfoApiparams params = initParams();
        GetUserInfoApiparams params = getUserInfoApiParams(fileds1, "13562478978");

        //通过 调用sohu api 获取
        Result resultSH = proxyUserInfoApiManager.getUserInfo(params);

        //通过 读sogou 获取
        Result resultSG = sgUserInfoApiManager.getUserInfo(params);

        //比较
        String resultSHJson = JsonUtil.obj2Json(resultSH);
        String resultSGJson = JsonUtil.obj2Json(resultSG);

//        Assert.assertNotNull(resultSHJson);
//        Assert.assertNotNull(resultSGJson);
//        Assert.assertEquals(resultSHJson, resultSGJson);

        System.out.println("======================resultSHJson:" + resultSHJson);
        System.out.println("======================resultSGJson:" + resultSGJson);

    }


}
