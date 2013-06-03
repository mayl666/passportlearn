package com.sogou.upd.passport.service.account.impl;

import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.exception.MailException;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.MailUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.EmailSenderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-7 Time: 下午4:18 To change this template use
 * File | Settings | File Templates.
 */
@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

    private static final String CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN;
    private static final String CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM;
    private static final String CACHE_PREFIX_PASSPORTID_BINDINGEMAILTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDEMAILTOKEN;
    private static final String CACHE_PREFIX_PASSPORTID_BINDEMAILSENDNUM = CacheConstant.CACHE_PREFIX_PASSPORTID_BINDEMAILSENDNUM;


    // private static final String PASSPORT_RESETPWD_EMAIL_URL="http://account.sogou.com/web/findpwd/checkemail?";
    // TODO:以下PASSPORT_RESETPWD_EMAIL_URL值仅供本机测试用
    // TODO:绑定验证URL待修改，考虑以后其他验证EMAIL的URL
    private static final String PASSPORT_RESETPWD_EMAIL_URL="http://localhost/web/findpwd/checkemail?";
    private static final String PASSPORT_BINDING_EMAIL_URL = "http://localhost/web/secure/checkbindemail?";

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailUtils mailUtils;

    @Override
    public boolean sendEmailForResetPwd(String uid, int clientId, String address) throws ServiceException {
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            String token = Coder.encryptMD5(uid + clientId + code);
            String activeUrl = PASSPORT_RESETPWD_EMAIL_URL + "passport_id=" + uid + "&client_id=" + clientId + "&scode=" + token;

            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);

            //模版中参数替换
            Map<String,Object> map= Maps.newHashMap();
            map.put("activeUrl",activeUrl);
            activeEmail.setMap(map);

            activeEmail.setTemplateFile("resetpwdmail.vm");
            activeEmail.setSubject("搜狗通行证找回密码服务");
            activeEmail.setCategory("resetpwd");
            activeEmail.setToEmail(address);

            mailUtils.sendEmail(activeEmail);

            //连接失效时间
            String cacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN + uid;
            redisUtils.setWithinSeconds(cacheKey, token, DateAndNumTimesConstant.TIME_TWODAY);
            /*redisUtils.set(cacheKey, token);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);*/

            // 设置邮件发送次数限制
            String resetCacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM + address + "_"
                                   + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            if (redisUtils.checkKeyIsExist(resetCacheKey)) {
                redisUtils.increment(resetCacheKey);
            } else {
                redisUtils.setWithinSeconds(resetCacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY);
                /*redisUtils.set(resetCacheKey, "1");
                redisUtils.expire(resetCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);*/
            }

        } catch(MailException me) {
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @Override
    public boolean checkEmailForResetPwd(String uid, int clientId, String token) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN + uid;
            if(redisUtils.checkKeyIsExist(cacheKey)){
                String tokenCache = redisUtils.get(cacheKey);
                if(tokenCache.equals(token)){
                    return true;
                }
            }
        } catch (Exception e){
            throw new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean deleteEmailCacheResetPwd(String uid, int clientId) throws ServiceException {
        try {
            redisUtils.delete(CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN + uid);
        } catch (Exception e) {
            logger.error("[SMS] service method deleteEmailCache error.{}", e);
            throw new ServiceException(e);
        }
        return true;
    }

    @Override
    public boolean checkSendEmailForPwdLimited(String email, int clientId) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM + email + "_"
                              + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                int checkNum = Integer.parseInt(redisUtils.get(cacheKey));
                if (checkNum > MailUtils.MAX_EMAIL_COUNT_ONEDAY) {
                    // 当日密码修改次数不超过上限
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean sendEmailForBinding(String uid, int clientId, String address) throws ServiceException {
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            String token = Coder.encryptMD5(uid + clientId + code);
            String activeUrl = PASSPORT_BINDING_EMAIL_URL + "passport_id=" + uid + "&client_id=" + clientId + "&token=" + token;

            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);

            //模版中参数替换
            Map<String,Object> map= Maps.newHashMap();
            map.put("activeUrl",activeUrl);
            activeEmail.setMap(map);

            activeEmail.setTemplateFile("bindemail.vm");
            activeEmail.setSubject("搜狗通行证绑定邮箱服务");
            activeEmail.setCategory("bindemail");
            activeEmail.setToEmail(address);

            mailUtils.sendEmail(activeEmail);

            // 记录绑定邮箱地址，则check后直接取绑定地址，不用再输入或记录
            String cacheKey = CACHE_PREFIX_PASSPORTID_BINDINGEMAILTOKEN + uid;
            redisUtils.hPut(cacheKey, "email", address);
            redisUtils.hPut(cacheKey, "token", token);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);

            // 设置邮件发送次数限制
            String resetCacheKey = CACHE_PREFIX_PASSPORTID_BINDEMAILSENDNUM + address + "_"
                    + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            if (redisUtils.checkKeyIsExist(resetCacheKey)) {
                redisUtils.increment(resetCacheKey);
            } else {
                redisUtils.setWithinSeconds(resetCacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY);
                /*redisUtils.set(resetCacheKey, "1");
                redisUtils.expire(resetCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);*/
            }
            return true;
        } catch(MailException me) {
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /*
     * 需要返回绑定邮箱
     */
    @Override
    public String checkEmailForBinding(String uid, int clientId, String token) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_BINDINGEMAILTOKEN + uid;
            if (redisUtils.checkKeyIsExist(cacheKey)){
                Map<String, String> mapToken = redisUtils.hGetAll(cacheKey);
                String tokenCache = mapToken.get("token");
                if (tokenCache.equals(token)){
                    return mapToken.get("email");
                }
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkSendEmailNumForBinding(String email, int clientId) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_PASSPORTID_BINDEMAILSENDNUM + email + "_"
                    + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                int checkNum = Integer.parseInt(redisUtils.get(cacheKey));
                if (checkNum > MailUtils.MAX_EMAIL_COUNT_ONEDAY) {
                    // 当日密码修改次数不超过上限
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean deleteEmailCacheForBinding(String uid, int clientId) throws ServiceException {
        try {
            redisUtils.delete(CACHE_PREFIX_PASSPORTID_BINDINGEMAILTOKEN + uid);
        } catch (Exception e) {
            logger.error("[SMS] service method deleteEmailCache error.{}", e);
            throw new ServiceException(e);
        }
        return true;
    }
}
