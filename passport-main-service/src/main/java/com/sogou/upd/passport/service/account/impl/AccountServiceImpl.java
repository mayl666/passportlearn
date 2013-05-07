package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.MailUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.BaseService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import com.sohu.sendcloud.Message;
import com.sohu.sendcloud.SmtpApiHeader;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * User: mayan Date: 13-3-22 Time: 下午3:38 To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl extends BaseService implements AccountService {

    private static final String CACHE_PREFIX_PASSPORT_ACCOUNT = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT;
    private static final String CACHE_PREFIX_PASSPORTID_IPBLACKLIST = CacheConstant.CACHE_PREFIX_PASSPORTID_IPBLACKLIST;
    private static final String CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN; // passportId与accountToken映射

    private static final String PASSPORT_ACTIVE_EMAIL_URL="http://account.sogou.com/web/activemail?";


    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailUtils mailUtils;

  @Override
  public Account initialWebAccount(String username) throws ServiceException {
    Account account = null;
    String cacheKey =null;
    try {
      cacheKey = buildAccountKey(username);
      if (redisUtils.checkKeyIsExist(cacheKey)) {
        Type type = new TypeToken<Account>() {
        }.getType();
        account = redisUtils.getObject(cacheKey, type);
        if(account!=null){
          account.setStatus(AccountStatusEnum.REGULAR.getValue());
          long id = accountDAO.insertAccount(username, account);
          if (id != 0) {
            //删除临时账户缓存，成为正式账户
            redisUtils.set(cacheKey, account);
            //设置cookie
            setCookie();
            return account;
          }
        }
      }
    } catch (Exception e) {
      throw new ServiceException(e);
    } finally {
      //删除激活
      cacheKey= CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN +username;
      redisUtils.delete(cacheKey);
    }


    return null;
  }

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
  public boolean sendActiveEmail(String username,String passpord, int clientId,String ip) throws ServiceException {
    boolean flag=true;
    try{
      String code = UUID.randomUUID().toString().replaceAll("-", "");

      String token = Coder.encryptMD5(username + clientId + code);

      String activeUrl =
          PASSPORT_ACTIVE_EMAIL_URL + "passport_id=" + username +
          "&client_id=" + clientId +
          "&token=" + token;

      //发送邮件
      Message message=mailUtils.getMessage();
      //模版中参数替换
      Map<String,Object> map= Maps.newHashMap();
      map.put("activeUrl",activeUrl);

      String mailBody=getMailBody("activemail.vm",map);
      // 正文， 使用html形式，或者纯文本形式
      message.setBody(mailBody);
      message.setSubject("激活您的搜狗通行证帐户");

      // X-SMTPAPI
      SmtpApiHeader smtpApiHeader = new SmtpApiHeader();
      smtpApiHeader.addCategory("register");
      smtpApiHeader.addRecipient(username);

      message.setXsmtpapiJsonStr(smtpApiHeader.toString());
      mailUtils.sendEmail(message);
      //连接失效时间
      String cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
      redisUtils.set(cacheKey, token);
      redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);
      //临时注册到缓存
      initialAccountToCache(username,passpord,ip);
    }catch (Exception e){
      flag=false;
      throw new ServiceException(e);
    }
    return flag;
  }

  @Override
  public boolean activeEmail(String username,String token,int clientId) throws ServiceException {
    try{
      String cacheKey = CACHE_PREFIX_PASSPORTID_ACTIVEMAILTOKEN + username;
      if(redisUtils.checkKeyIsExist(cacheKey)){
            String tokenCache=redisUtils.get(cacheKey);
            if(tokenCache.equals(token)){
              return true;
            }
      }
    }catch (Exception e){
      throw new ServiceException(e);
    }
    return false;
  }

  @Override
  public boolean setCookie() throws Exception {
//    ServletUtil.setCookie();
    return false;
  }

  /*
   * 外域邮箱注册
   */
  public void initialAccountToCache(String username,String password,String ip)throws ServiceException{
    int provider=AccountTypeEnum.EMAIL.getValue();
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
      account.setAccountType(provider);
      account.setStatus(AccountStatusEnum.DISABLED.getValue());
      account.setVersion(Account.NEW_ACCOUNT_VERSION);
      account.setRegIp(ip);

      String cacheKey = buildAccountKey(username);
      redisUtils.set(cacheKey, account);
      redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);

    }catch (Exception e){
         throw new ServiceException(e);
    }
  }
  private String buildAccountKey(String passportId) {
        return CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
    }
}
