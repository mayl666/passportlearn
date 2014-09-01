package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-3
 * Time: 下午9:35
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class UniqnamePassportIdMappingTest extends BaseTest {

    @Autowired
    private UniqNamePassportMappingService uniqNamePassportMappingService;

    @Test
    public void testCheckNickName() throws Exception {
        String nickName = "KeSyren1234";
        Assert.assertTrue(StringUtils.isNotEmpty(uniqNamePassportMappingService.checkUniqName(nickName)));
        System.out.println("================= testCheckNickName:" + uniqNamePassportMappingService.checkUniqName(nickName));
    }

    @Test
    public void testDeleteNickName() throws Exception {
        String nickName = "甜菜";
        boolean isSucc = uniqNamePassportMappingService.removeUniqName(nickName);
        Assert.assertTrue(isSucc);
    }
}
