package com.sogou.upd.passport.manager.account;

import com.rabbitmq.tools.json.JSONUtil;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.JsonUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
public class SecureManagerTest extends BaseTest {
    @Autowired
    private SecureManager secureManager;

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
    public void testAll() {

    }

    /* ------------------------------------修改密保Begin------------------------------------ */
    public void testModify() {

    }

    public void testModifyEmail() {

    }

    /* ------------------------------------修改密保End------------------------------------ */

    /* ------------------------------------修改密保End------------------------------------ */

    public void testResetPwd() {

    }

    public void testResetPwdByEmail() {

    }

    @Test
    public void testActionRecord() {
        for (int i = 0; i < 15; i++) {
            secureManager.logActionRecord(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.LOGIN, "202.106.180." + (i + 1), null);
        }

        Result
                result = secureManager.queryActionRecords(PASSPORT_ID, CLIENT_ID, AccountModuleEnum.LOGIN);
        System.out.println(result.toString());
    }

    /* ------------------------------------修改密保End------------------------------------ */


    /**
     * 安全 获取用户信息单元测试
     */
    @Test
    public void testQueryAccountSecureInfo() {
        String userId = "gang.chen0505@gmail.com";
        int clientId = 1120;
        boolean process1 = true;

        try {
            Result result1 = secureManager.queryAccountSecureInfo(userId, clientId, process1);
//            Result result2 = secureManager.queryAccountSecureInfo(userId, clientId, process1);
            Assert.assertNotNull(result1);


            //目前逻辑
            /**
             * {
             "message": "查询成功",
             "success": true,
             "models": {
                 "_defaultModel": {
                     "sec_mobile": null,
                     "last_login_loc": "北京市",
                     "last_login_time": 1401096899539,
                     "reg_email": "ga*****5@gmail.com",
                     "sec_score": 60,
                     "sec_email": "lo*****5@163.com",
                     "sec_ques": "您就读的中学名称"
             },

             //sec_email,sec_mobile,sec_ques

                 "sec_ques": "您就读的中学名称",
                 "flag": "1",
                 "userid": "gang.chen0505@gmail.com",
                 "sec_email": "loveclover0505@163.com",
                 "sec_mobile": "",
                 "uniqname": "happychenb",
                 "avatarurl": {
                 "img_50": "http://img01.sogoucdn.com/app/a/100140007/kdxKnJx3oAY0YQIj_1399892269607"
                 }
             },
             "code": "0",
             "defaultModel": {
                 "sec_mobile": null,
                 "last_login_loc": "北京市",
                 "last_login_time": 1401096899539,
                 "reg_email": "ga*****5@gmail.com",
                 "sec_score": 60,
                 "sec_email": "lo*****5@163.com",
                 "sec_ques": "您就读的中学名称"
             },
             "defaultModelKey": "_defaultModel"
             }
             */

            /**
             * 通过 sgUserInfoApiManager getUserInfo 获取
             {
             "message": "查询成功",
             "success": true,
             "models": {
             "_defaultModel": {
                 "sec_mobile": null,
                 "last_login_loc": "北京市",
                 "last_login_time": 1401096899539,
                 "sec_score": 0,
                 "sec_email": null,
                 "sec_ques": null,
                 "reg_email": "ga*****5@gmail.com"
             },
                 "img_30": null,
                 "img_50": null,
                 "img_180": null,
                 "sec_mobile": null,
                 "uniqname": "gang.chen0505"


                 //缺少的数据项
                 //sec_ques
                 //flag
                 //userid
                 //sec_email
                 //avatarurl

             },
             "defaultModel": {
                 "sec_mobile": null,
                 "last_login_loc": "北京市",
                 "last_login_time": 1401096899539,
                 "sec_score": 0,
                 "sec_email": null,
                 "sec_ques": null,
                 "reg_email": "ga*****5@gmail.com"
             },
             "code": "0",
             "defaultModelKey": "_defaultModel"
             }
             *
             */

            String temp1 = JsonUtil.obj2Json(result1);
            Assert.assertNotNull(temp1);
            System.out.println("=====================:" + temp1);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
