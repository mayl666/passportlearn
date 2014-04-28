package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.APIResultForm;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午9:19
 * To change this template use File | Settings | File Templates.
 */
public class BindApiManagerTest extends BaseTest {

    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private BindApiManager sgBindApiManager;

    private static final String mobile = mobile_2;
    private static final String mobile_passportId = userid_sohu;
    private static final String wrong_mobile = "1360x0x0x01";
    private static final String no_mobile = "13601010101";

    /**
     * 手机号已注册或已绑定的情况下---根据手机号获取passportId
     */
    @Test
    public void testGetPassportIdByMobile() throws IOException {
        BaseMoblieApiParams params = new BaseMoblieApiParams();
        params.setMobile(mobile);
        Result resultSH = proxyBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSH = JacksonJsonMapperUtil.getMapper().readValue(resultSH.toString(), APIResultForm.class);
        Assert.assertEquals("0", formSH.getStatus());
        Assert.assertEquals(mobile_passportId, formSH.getData().get("userid"));
        Assert.assertEquals("操作成功", formSH.getStatusText());
        Result resultSG = sgBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSG = JacksonJsonMapperUtil.getMapper().readValue(resultSG.toString(), APIResultForm.class);
        Assert.assertTrue(formSH.equals(formSG));
        //如果比较失败，可打开下面的结果输出对比找原因
//        System.out.println("-----------------结果如下---------------");
//        System.out.println("搜狐结果：" + resultSH);
//        System.out.println("搜狗结果：" + resultSG);
    }

    /**
     * 手机号不存在的情况下---根据手机号获取passportId
     */
    @Test
    public void testGetPassportIdByMobileNoMobile() throws IOException {
        BaseMoblieApiParams params = new BaseMoblieApiParams();
        params.setMobile(no_mobile);
        Result resultSH = proxyBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSH = JacksonJsonMapperUtil.getMapper().readValue(resultSH.toString(), APIResultForm.class);
        Assert.assertEquals(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND, formSH.getStatus());
        Assert.assertEquals(ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND), formSH.getStatusText());
        Result resultSG = sgBindApiManager.getPassportIdByMobile(params);
        APIResultForm formSG = JacksonJsonMapperUtil.getMapper().readValue(resultSG.toString(), APIResultForm.class);
        Assert.assertTrue(formSH.equals(formSG));
        //如果比较失败，可打开下面的结果输出对比找原因
//        System.out.println("-----------------结果如下---------------");
//        System.out.println("搜狐结果：" + resultSH);
//        System.out.println("搜狗结果：" + resultSG);
    }

}