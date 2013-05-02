package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.MD5Encryption;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.MailUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final String CACHE_PREFIX_PASSPORT_ACCOUNT = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT;
    private static final String CACHE_PREFIX_PASSPORTID_IPBLACKLIST = CacheConstant.CACHE_PREFIX_PASSPORTID_IPBLACKLIST;
    private static final String CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN; // passportId与accountToken映射

    private static final String PASSPORT_ACTIVE_EMAIL_URL="http://account.sogou.com/web/activeemail?";


    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailUtils mailUtils;

    @Override
    public Account initialAccount(String username, String password, String ip, int provider) throws ServiceException {
        Account account = new Account();
        String passportId = PassportIDGenerator.generator(username, provider);
        account.setPassportId(passportId);
        String passwordSign = null;
        try {
            if (!Strings.isNullOrEmpty(password)) {
                passwordSign = PwdGenerator.generatorPwdSign(password);
            }
            account.setPasswd(passwordSign);
            account.setRegTime(new Date());
            account.setRegIp(ip);
            account.setAccountType(provider);
            account.setStatus(AccountStatusEnum.REGULAR.getValue());
            account.setVersion(Account.NEW_ACCOUNT_VERSION);
            String mobile = null;
            if (AccountTypeEnum.isPhone(username, provider)) {
                mobile = username;
            }
            account.setMobile(mobile);
            long id = accountDAO.insertAccount(passportId, account);
            if (id != 0) {
                String cacheKey = buildAccountKey(passportId);
                redisUtils.set(cacheKey, account);
                return account;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

    @Override
    public Account initialConnectAccount(String passportId, String ip, int provider) throws ServiceException {
        return initialAccount(passportId, null, ip, provider);
    }

    @Override
    public Account queryAccountByPassportId(String passportId) throws ServiceException {
        Account account;
        try {
            String cacheKey = buildAccountKey(passportId);
            Type type = new TypeToken<Account>() {
            }.getType();
            account = redisUtils.getObject(cacheKey, type);
            if (account == null) {
                account = accountDAO.getAccountByPassportId(passportId);
                if (account != null) {
                    redisUtils.set(cacheKey, account);
                }
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return account;
    }

    @Override
    public Account verifyUserPwdVaild(String passportId, String password) throws ServiceException {
        String pwdSign;
        try {
            pwdSign = PwdGenerator.generatorPwdSign(password);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        Account userAccount;
        try {
            userAccount = queryAccountByPassportId(passportId);
        } catch (ServiceException e) {
            throw e;
        }
        if (userAccount != null && pwdSign.equals(userAccount.getPasswd())) {
            return userAccount;
        }
        return null;
    }

    @Override
    public Account verifyAccountVaild(String passportId) throws ServiceException {
        Account account = queryAccountByPassportId(passportId);
        if (account.isNormalAccount()) {
            return account;
        }
        return null;
    }

    @Override
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException {
        try {
            int row = accountDAO.deleteAccountByPassportId(passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                redisUtils.delete(cacheKey);
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public Account resetPassword(String passportId, String password) throws ServiceException {
        try {
            Account account = verifyAccountVaild(passportId);
            String passwdSign = PwdGenerator.generatorPwdSign(password);
            int row = accountDAO.modifyPassword(passwdSign, passportId);
            if (row != 0) {
                String cacheKey = buildAccountKey(passportId);
                account.setPasswd(passwdSign);
                redisUtils.set(cacheKey, account);
                return account;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return null;
    }

  @Override
  public boolean isInAccountBlackListByIp(String passportId, String ip) throws ServiceException {
    boolean flag=true;
    long ipCount = 0;
    try {
      String cacheKey = CACHE_PREFIX_PASSPORTID_IPBLACKLIST + ip;
      String ipValue = redisUtils.get(cacheKey);
      if (Strings.isNullOrEmpty(ipValue)) {
        redisUtils.set(cacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY, TimeUnit.SECONDS);
      } else {
        ipCount = Long.parseLong(ipValue);
        //判断ip注册限制次数（一天20次）
        if (ipCount < DateAndNumTimesConstant.IP_LIMITED) {
          redisUtils.increment(cacheKey);
        } else {
          return false;
        }
      }
    } catch (Exception e) {
      flag=false;
      throw new ServiceException(e);
    }
    return flag;
  }

  @Override
  public boolean sendActiveEmail(String username, int clientId) throws Exception {
    boolean flag=true;
    try{
      String token = UUID.randomUUID().toString().replaceAll("-", "");

      String code = MD5Encryption.MD5(username + clientId + token +
                                      (new SimpleDateFormat("yyyy-MM-dd")
                                           .format(new Date().getTime())));

      String activeUrl =
          PASSPORT_ACTIVE_EMAIL_URL + "passport_id=" + username +
          "&client_id=" + clientId +
          "&token=" + token +
          "&code=" + code;

      //发送邮件

//      mailUtils.sendEmail();
      //连接失效
      String cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
      redisUtils.set(cacheKey, token);
      redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);
      //临时数据写入缓存

    }catch (Exception e){
      flag=false;
      throw new ServiceException(e);
    }
    return flag;
  }

  private String buildAccountKey(String passportId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
    }
}
