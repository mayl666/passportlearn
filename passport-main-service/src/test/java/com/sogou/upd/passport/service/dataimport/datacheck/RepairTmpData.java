package com.sogou.upd.passport.service.dataimport.datacheck;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 修复全量数据迁移的临时表
 * User: shipengzhi
 * Date: 14-5-23
 * Time: 上午12:17
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
public class RepairTmpData extends BaseTest {

    //从搜狐获取数据失败记录
    private List<String> failedList = Lists.newArrayList();

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private AccountService accountService;

    /**
     * 修复手机账号，但是moblie_passportid_mapping表里没记录的异常数据
     */
    @Test
    public void testCheckDataIsNewRegister() {
        List<String> passportIdList = FileUtil.readFileByLines("D:\\mysql\\phone_account00");
        int count = 0;
        for (String passportId : passportIdList) {
            if (!Strings.isNullOrEmpty(passportId) && passportId.contains("@")) {
                Account account = accountDAO.getAccountByPassportId(passportId);

                String[] array = passportId.split("@");
                String mobile = array[0];
                boolean isBind = accountService.bindMobile(account, mobile);
                if (!isBind) {
                    count++;
                    failedList.add(passportId);
                }
            }
        }
        failedList.add("count:" + count);
        try {
            FileUtil.storeFile("D:\\mysql\\phone_account00_result.txt", failedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
