package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileWriter;
import java.util.List;
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
    private AccountDAO accountDAO;
    private List<AccountBaseInfo> listConnectBaseInfo;
    private DBShardRedisUtils dbShardRedisUtils;
    private static final Logger logger = LoggerFactory.getLogger(AddConnectUserInfoThread.class);

    public MoveBaseInfoToAccountThread(CountDownLatch latch, AccountDAO accountDAO, DBShardRedisUtils dbShardRedisUtils, List<AccountBaseInfo> listConnectBaseInfo) {
        this.latch = latch;
        this.accountDAO = accountDAO;
        this.listConnectBaseInfo = listConnectBaseInfo;
        this.dbShardRedisUtils = dbShardRedisUtils;
    }

    @Override
    public void run() {
        String logOpenId = null;
        try {
            List<AccountBaseInfo> listInfo = this.listConnectBaseInfo;
            for (AccountBaseInfo accountBaseInfo : listInfo) {
                String passportId = accountBaseInfo.getPassportId();
                Account queryAccount;
                try {
                    queryAccount = accountDAO.getAccountByPassportId(passportId);
                } catch (Exception e) {
                    //更新account表之前先查询是否有此记录,查询异常的要记录下来
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\query_account_exception.txt", true);
                    writer.write(passportId + ",error:get account by passportId from account table error");
                    writer.write("\r\n");
                    writer.close();
                    continue;
                }
                if (queryAccount == null) {
                    //记录下来account_base_info表中有，但在account表中没有的记录
                    FileWriter writer = new FileWriter("D:\\transfer\\account_base_info\\query_account_null.txt", true);
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
                String cacheKey = buildAccountKey(passportId);
                dbShardRedisUtils.setWithinSeconds(cacheKey, queryAccount, DateAndNumTimesConstant.THREE_MONTH);
            }
        } catch (Exception e) {
            logger.error("出错记录passportId为：" + logOpenId, e);
        } finally {
            latch.countDown();
        }
    }

    private String buildAccountKey(String passportId) {
        return CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
    }
}
