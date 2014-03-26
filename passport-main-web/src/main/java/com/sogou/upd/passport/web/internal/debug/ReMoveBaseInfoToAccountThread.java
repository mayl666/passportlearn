package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-6
 * Time: 下午3:27
 * To change this template use File | Settings | File Templates.
 */
public class ReMoveBaseInfoToAccountThread implements Runnable {
    private CountDownLatch latch;
    private AccountDAO accountDAO;
    private String fileName;
    private AccountBaseInfoDAO accountBaseInfoDAO;
    private static final Logger logger = LoggerFactory.getLogger(AddConnectUserInfoThread.class);

    public ReMoveBaseInfoToAccountThread(CountDownLatch latch, AccountDAO accountDAO, AccountBaseInfoDAO accountBaseInfoDAO, String fileName) {
        this.latch = latch;
        this.accountDAO = accountDAO;
        this.fileName = fileName;
        this.accountBaseInfoDAO = accountBaseInfoDAO;
    }

    @Override
    public void run() {
        BufferedReader reader;
        String logOpenId = null;
        try {
            File file = new File(this.fileName);
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                String passportId = tempString.split(",")[0];
                AccountBaseInfo accountBaseInfo;
                try {
                    accountBaseInfo = accountBaseInfoDAO.getAccountBaseInfoByPassportId(passportId);
                } catch (Exception e) {
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\test.txt", true);
                    writer.write(passportId + ",error:get accountbaseinfo by passportId from accountbaseinfo table error");
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                if (accountBaseInfo == null) {
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\test.txt", true);
                    writer.write(passportId + ",error:passportId is not exists in accountbaseinfo table");
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                Account queryAccount;
                try {
                    queryAccount = accountDAO.getAccountByPassportId(passportId);
                } catch (Exception e) {
                    //更新account表之前先查询是否有此记录,查询异常的要记录下来
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\test.txt", true);
                    writer.write(passportId + ",error:get account by passportId from account table error");
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                if (queryAccount == null) {
                    //记录下来account_base_info表中有，但在account表中没有的记录
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\test.txt", true);
                    writer.write(passportId + ",error:passportId is not exist in account table");
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                queryAccount.setUniqname(accountBaseInfo.getUniqname()); //昵称
                queryAccount.setAvatar(accountBaseInfo.getAvatar());   //头像
                long id;
                try {
                    id = accountDAO.insertOrUpdateAccount(passportId, queryAccount);
                } catch (Exception e) {
                    //1.插入或更新表异常
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\test.txt", true);
                    writer.write(passportId + ",error:insert account table error");
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                if (id == 0) {
                    //2.插入更新表失败
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\test.txt", true);
                    writer.write(passportId + ",error:passportId is not exist in account table");
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
            }

        } catch (Exception e) {
            logger.error("出错记录passportId为：" + logOpenId, e);
        } finally {
            latch.countDown();
        }
    }
}
