package com.sogou.upd.passport.service.dataimport.datacheck;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.impl.AccountServiceImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-7-31
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:spring-config-service.xml","classpath:spring-config-jredis-test.xml","classpath:spring-config-dao-test.xml"})
public class TestLeakList {

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private TokenRedisUtils tokenRedisUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    @Test
    public void updateLeakMysqlAndRedis() throws Exception {
        FileReader fileInputStream = new FileReader("F://txt.txt");
        BufferedReader reader = new BufferedReader(fileInputStream);
        String str = reader.readLine();
        while(str!=null){
            System.out.println(str);
            String key = "SP.PASSPORTID:SOGOULEAKLIST_" + str;
            redisUtils.set(key,1);
            str = reader.readLine();
        }
//        redisUtils.set("test","test1");
//        System.out.print(redisUtils.get("test"))   ;
//        Account account = accountService.queryAccountByPassportId("1352012@qq.com");
//        System.out.println(account.getUniqname());
    }

    protected String buildAccountKey(String passportId) {
        return CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
    }


     @Test
     public void updateLeakMysqlAndRedis2() throws IOException {
         FileReader fileInputStream = new FileReader("F://txt.txt");
         BufferedReader reader = new BufferedReader(fileInputStream);
         String str = reader.readLine();
         while(str!=null){
             Account account;
             try {
                 String cacheKey = buildAccountKey(str);
                 account = dbShardRedisUtils.getObject(cacheKey, Account.class);
                 if(null!=account)
                     dbShardRedisUtils.delete(cacheKey);
                 account = accountDAO.getAccountByPassportId(str);
                 if (account != null) {
                     System.out.println(account.getPassportId());
                     accountDAO.updateState(3,str);
                     account = accountDAO.getAccountByPassportId(str);
                     dbShardRedisUtils.setObjectWithinSeconds(cacheKey, account, DateAndNumTimesConstant.ONE_MONTH);
                 } else {
                     System.out.println("no:" + str) ;
                 }
             } catch (Exception e) {
                 throw new ServiceException(e);
             }
             str = reader.readLine();
         }

     }
}
