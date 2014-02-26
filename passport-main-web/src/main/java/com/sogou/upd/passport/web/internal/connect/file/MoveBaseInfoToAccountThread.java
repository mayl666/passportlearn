package com.sogou.upd.passport.web.internal.connect.file;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-26
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
public class MoveBaseInfoToAccountThread implements Runnable {
    private CountDownLatch latch;
    private String fileName;
    private AccountDAO accountDAO;
    private static final Logger logger = LoggerFactory.getLogger(AddConnectUserInfoThread.class);

    public MoveBaseInfoToAccountThread(CountDownLatch latch, String fileName, AccountDAO accountDAO) {
        this.latch = latch;
        this.fileName = fileName;
        this.accountDAO = accountDAO;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        String logOpenId = null;
        try {
            long start = System.currentTimeMillis();
            File file = new File(this.fileName);
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                String[] rowString = tempString.split(",");
                String passportId = rowString[1]; //passportId;
                logOpenId = passportId;
                String uniqname = rowString[2];//第三方用户昵称
                String avatar = null;
                if(rowString.length == 4){
                    avatar = rowString[3];//第三方用户头像
                }
                Account account = new Account();
                account.setPassportId(passportId);
                account.setUniqname(StringUtil.strToUTF8(uniqname));
                account.setAvatar(avatar);
                account.setPasswordtype(Account.NO_PASSWORD);
                long id;
                try {
                    id = accountDAO.insertOrUpdateAccount(passportId, account);
                } catch (Exception e) {
                    //1.插入或更新表异常
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\update_exception.txt", true);
                    writer.write(passportId);
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                if (id == 0) {
                    //2.插入更新表失败
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\update_failed.txt", true);
                    writer.write(passportId);
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
            }
            reader.close();
            System.out.println(Thread.currentThread().getName() + ":" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            logger.error("出错记录passportId为：" + logOpenId, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    logger.error("异常信息：", e1);
                }
            }
            latch.countDown();
        }
    }
}
