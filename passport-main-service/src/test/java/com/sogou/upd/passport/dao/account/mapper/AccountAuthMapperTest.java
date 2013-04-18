package com.sogou.upd.passport.dao.account.mapper;

import com.sogou.upd.passport.dao.account.AccountAuthMapper;
import com.sogou.upd.passport.model.account.AccountAuth;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-30                             AbstractJUnit4SpringContextTests
 * Time: 下午6:26
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class AccountAuthMapperTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private AccountAuthMapper accountAuthMapper;

    private static final long USER_Id = 99;
    private static final String ACCESS_TOKEN = "test_access_token";
    private static final String REFRESH_TOKEN = "test_refresh_token";
    private static final String ACCESS_VALID_TIME = "1365929992920";
    private static final String REFRESH_VALID_TIME = "1380878882920";
    private static final int CLIENT_ID = 1001;
    private static final String INSTANCE_ID = "liulingtest";
    private static final long ID = 148;

    /**
     * 测试往用户状态表中插入一条记录
     */
    @Test
    public void testInsertAccountAuth() {
        AccountAuth aa = new AccountAuth();
        aa.setInstanceId(INSTANCE_ID);
        aa.setClientId(CLIENT_ID);
        aa.setUserId(USER_Id);
        aa.setAccessToken(ACCESS_TOKEN);
        aa.setRefreshToken(REFRESH_TOKEN);
        aa.setAccessValidTime(Long.parseLong(ACCESS_VALID_TIME));
        aa.setRefreshValidTime(Long.parseLong(REFRESH_VALID_TIME));
        int row = accountAuthMapper.insertAccountAuth(aa);
        Assert.assertTrue(row == 1);
    }

    /**
     * 测试往用户状态表中插入或修改一条记录
     */
    @Test
    public void testSaveAccountAuth() {
        AccountAuth aa = new AccountAuth();
        aa.setId(ID);
        aa.setInstanceId(INSTANCE_ID);
        aa.setClientId(CLIENT_ID);
        aa.setUserId(USER_Id);
        aa.setAccessToken(ACCESS_TOKEN);
        aa.setRefreshToken(REFRESH_TOKEN);
        aa.setAccessValidTime(Long.parseLong(ACCESS_VALID_TIME));
        aa.setRefreshValidTime(Long.parseLong(REFRESH_VALID_TIME));
        int row = accountAuthMapper.saveAccountAuth(aa);
        Assert.assertTrue(row == 1);
    }

    /**
     * 测试根据refresh_token获取AccountAuth信息
     */
    @Test
    public void testGetAccountAuthByRefreshToken() {
        AccountAuth aa = accountAuthMapper.getAccountAuthByRefreshToken(REFRESH_TOKEN);
        Assert.assertNotNull(aa);
    }

    /**
     * 测试根据access_token获取AccountAuth信息
     */
    @Test
    public void testGetAccountAuthByAccessToken() {
        AccountAuth aa = accountAuthMapper.getAccountAuthByAccessToken(ACCESS_TOKEN);
        Assert.assertNotNull(aa);
    }

    /**
     * 测试根据userId,clientId查询所有这两个字段等于给定值，但instanceId不等于给定的这个值的记录，返回list集合
     */
    @Test
    public void testBatchFindAccountAuthByUserId() {
        AccountAuth aa = new AccountAuth();
        aa.setUserId(USER_Id);
        aa.setClientId(CLIENT_ID);
        aa.setInstanceId(INSTANCE_ID);
        List<AccountAuth> list = accountAuthMapper.batchFindAccountAuthByUserId(aa);
        Assert.assertTrue(list != null && list.size() > 0);
    }

    /**
     * todo 批量问题
     */
    @Test
    public void testBatchUpdateAccountAuth() {

    }
}
