package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.account.AccountInfo;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-3 Time: 下午2:54 To change this template use
 * File | Settings | File Templates.
 */
public class AccountInfoDAOTest extends BaseDAOTest {

    @Autowired
    private AccountInfoDAO accountInfoDAO;

    @Before
    public void init() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setPassportId(PASSPORT_ID);
        accountInfo.setEmail(EMAIL);
        accountInfo.setQuestion(QUESTION);
        accountInfo.setAnswer(ANSWER);

        int row = accountInfoDAO.insertAccountInfo(accountInfo.getPassportId(),accountInfo);
        Assert.assertTrue(row != 0);
    }

    @After
    public void end() {
        int row = accountInfoDAO.deleteAccountInfoByPassportId(PASSPORT_ID);
        row += accountInfoDAO.deleteAccountInfoByPassportId(NEW_PASSPORT_ID);
        Assert.assertTrue(row != 0);
    }

    /**
     * 测试单条记录查询
     */
    @Test
    public void testGetAccountByPassportId() {
        AccountInfo accountInfo = accountInfoDAO.getAccountInfoByPassportId(PASSPORT_ID);
        Assert.assertTrue(accountInfo != null);
    }

    /**
     * 测试修改绑定邮箱，insert or update中update的情况
     */
    @Test
    public void testModifyEmail() {
        AccountInfo accountInfo = accountInfoDAO.getAccountInfoByPassportId(PASSPORT_ID);
        if(accountInfo==null){
            accountInfo.setCreateTime(new Date());
        }
        accountInfo.setEmail(NEW_EMAIL);
        int row = accountInfoDAO.saveEmailOrInsert(PASSPORT_ID, accountInfo);

        /*
         * 不能根据返回值是1、2来区分更新或插入，返回值依实现而不同，特别是更新时，也可能返回3
         */
        Assert.assertTrue(row != 0);
    }

    /**
     * 测试增加密保问题及答案，insert or update中insert的情况
     */
    @Test
    public void testSaveQuesAndAnswer() {
        AccountInfo accountInfo = new AccountInfo(NEW_PASSPORT_ID);
        accountInfo.setQuestion(NEW_QUESTION);
        accountInfo.setAnswer(NEW_ANSWER);
        int row = accountInfoDAO.saveQuesOrInsert(NEW_PASSPORT_ID, accountInfo);
        Assert.assertTrue(row == 1);
    }
}
