package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-11-30
 * Time: 下午7:55
 * To change this template use File | Settings | File Templates.
 */
public class AccountBaseInfoServiceTest extends BaseTest {

    @Autowired
    private AccountBaseInfoService accountBaseInfoService;

    private String passportId = "shipengzhi@qq.sohu.com";
    private String uniqname_1 = "shipengzhi";
    private String avatar_1 = "%s/app/a/%s/VjtZWqsEPEAvAOIX_1385635650505";

    /**
     * 测试第一次初始化昵称头像
     */
    @Test
    public void testFirstInsertAccountBaseInfo(){
        boolean success = accountBaseInfoService.initAccountBaseInfo(passportId, uniqname_1, avatar_1);
        Assert.assertTrue(success);
    }

    /**
     * 测试只新增昵称
     */
    @Test
    public void testFirstInsertUniqname(){
        boolean success = accountBaseInfoService.initAccountBaseInfo(passportId, "rostan", "");
        Assert.assertTrue(success);
    }

    /**
     * 测试只新增头像
     */
    @Test
    public void testFirstInsertAvatar(){
        boolean success = accountBaseInfoService.initAccountBaseInfo(passportId, "", avatar_1);
        Assert.assertTrue(success);
    }

    /**
     * 测试新增的昵称不唯一
     */
    @Test
    public void testInsertNoUniq(){
        boolean success = accountBaseInfoService.initAccountBaseInfo(passportId, uniqname_1, avatar_1);
        Assert.assertTrue(success);
    }

}
