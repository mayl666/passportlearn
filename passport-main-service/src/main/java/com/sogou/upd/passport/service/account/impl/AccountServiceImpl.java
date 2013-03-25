package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.dao.account.AccountDao;
import com.sogou.upd.passport.common.math.PassportIDGenerator;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.inject.Inject;
import javax.print.DocFlavor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Inject
    private AccountDao accountDao;
    @Inject
    private ShardedJedisPool shardedJedisPool;

    private ShardedJedis jedis;

    @Override
    public boolean checkIsRegisterAccount(Account account) {
        return accountDao.checkIsRegisterAccount(account);
    }

    @Override
    public boolean handleSendSms(String account, int appkey) {
        boolean flag = true;
        try {
            //生成随机数
            String randomCode = RandomStringUtils.randomNumeric(5);
            //写入缓存
            String keyCache = account + "_" + appkey;
            Map<String, String> map = new HashMap<String, String>();
            map.put("smsCode", randomCode);    //验证码
            map.put("sendNum", "1");
            map.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

            jedis.hmset(keyCache, map);
            jedis.expire(keyCache, SMSUtil.SMS_VALID * 60);      //有效时长30分钟
        } catch (Exception e) {
            flag = false;
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
        return flag;
    }

    @Override
    public boolean checkIsExistFromCache(String account) {
        boolean flag = true;
        try {
            flag = jedis.exists(account);
        } catch (Exception e) {
            flag = false;
        } finally {
            shardedJedisPool.returnResource(jedis);
        }
        return flag;
    }

    @Override
    public long userRegister(Account account) {
        return accountDao.userRegister(account);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long userRegiterDetail(String mobile, String passwd, String regIp,String smsCode) {
        int accountType = AccountTypeEnum.PHONE.getValue();
        String passportId = PassportIDGenerator.generator(mobile, accountType);
        int status = AccountStatusEnum.REGULAR.getValue();
        int version = Account.NEW_ACCOUNT_VERSION;
        Account account = new Account(0, passportId, passwd, mobile, new Date(), regIp, status, version, accountType);
        return accountDao.userRegister(account);
        //TODO add insert smsCode into app table
    }

    @Override
    public long initialAccount(String account, String pwd, String ip, int provider) {
        Account a = new Account();
        a.setPassportId(PassportIDGenerator.generator(account, provider));
        a.setPasswd(pwd);
        a.setRegTime(new Date());
        a.setRegIp(ip);
        a.setAccountType(provider);
        a.setStatus(AccountStatusEnum.REGULAR.getValue());
        a.setVersion(Account.NEW_ACCOUNT_VERSION);
        a.setMobile(account);
        return accountDao.userRegister(a);
    }

    @Override
    public long initialConnectAccount(String account, String ip, int provider) {
        return initialAccount(account, null, ip, provider);
    }
}
