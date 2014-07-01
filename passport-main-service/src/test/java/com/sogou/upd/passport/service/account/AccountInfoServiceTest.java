package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.model.account.AccountInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-6 Time: 下午6:49 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
public class AccountInfoServiceTest extends BaseTest {
    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final String EMAIL = "Binding123@163.com";
    private static final String NEW_EMAIL = "NewBinding123@163.com";
    private static final String QUESTION = "Secure question";
    private static final String NEW_QUESTION = "New secure question";
    private static final String ANSWER = "Secure answer";
    private static final String NEW_ANSWER = "New secure answer";

    @Autowired
    private AccountInfoService accountInfoService;

    /**
     * 测试查询是否成功
     */
    @Test
    public void testQueryAccountInfoByPassportId() {
        AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(PASSPORT_ID);
        if (accountInfo != null) {
            System.out.println("查询成功！");
        } else {
            System.out.println("查询失败！");
        }
    }

    /**
     * 测试修改
     */
    @Test
    public void testModifyEmailByPassportId() {
        AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(PASSPORT_ID);
        accountInfoService.modifyBindEmailByPassportId(PASSPORT_ID, NEW_EMAIL);
        AccountInfo accountInfo1 = accountInfoService.queryAccountInfoByPassportId(PASSPORT_ID);
        if (NEW_EMAIL.equals(accountInfo1.getEmail())) {
            System.out.println("修改成功:"+NEW_EMAIL);
        } else {
            System.out.println("修改失败");
        }
        accountInfoService.modifyBindEmailByPassportId(PASSPORT_ID, accountInfo.getEmail());
    }

    @Test
    public void testModifyQuesByPassportId() {
        AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(PASSPORT_ID);
        accountInfoService.modifyQuesByPassportId(PASSPORT_ID, NEW_QUESTION, NEW_ANSWER);
        AccountInfo accountInfo1 = accountInfoService.queryAccountInfoByPassportId(PASSPORT_ID);
        if (NEW_QUESTION.equals(accountInfo1.getQuestion()) && NEW_ANSWER.equals(accountInfo1.getAnswer())) {
            System.out.println("修改成功:"+accountInfo1.getQuestion() + accountInfo1.getAnswer());
            System.out.println("原："+accountInfo.getQuestion() + accountInfo.getAnswer());
        } else {
            System.out.println("修改失败");
        }
        accountInfoService.modifyQuesByPassportId(PASSPORT_ID, accountInfo.getQuestion(), accountInfo.getAnswer());
    }
}
