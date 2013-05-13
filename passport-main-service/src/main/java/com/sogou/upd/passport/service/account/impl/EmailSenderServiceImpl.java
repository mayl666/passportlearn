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

    // private static final String PASSPORT_RESETPWD_EMAIL_URL="http://account.sogou.com/web/findpwd/checkemail?";
    // TODO:以下PASSPORT_RESETPWD_EMAIL_URL值仅供本机测试用
    private static final String PASSPORT_RESETPWD_EMAIL_URL="http://localhost/web/findpwd/checkemail?";

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailUtils mailUtils;

    @Override
    public boolean sendEmailForResetPwd(String uid, int clientId, String address) throws ServiceException {
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            String token = Coder.encryptMD5(uid + clientId + code);
            String activeUrl = PASSPORT_RESETPWD_EMAIL_URL + "uid=" + uid + "&cid=" + clientId + "&token=" + token;

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
            redisUtils.set(cacheKey, token);
            redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_TWODAY);

            // 设置邮件发送次数限制
            String resetCacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM + address;
            if (redisUtils.checkKeyIsExist(resetCacheKey)) {
                // cacheKey存在，则检查resetTime
                Map<String, String> mapCacheResetNumResult = redisUtils.hGetAll(resetCacheKey);
                Date date = DateUtil.parse(mapCacheResetNumResult.get("sendTime"), DateUtil.DATE_FMT_3);
                long diff = DateUtil.getTimeIntervalMins(DateUtil.getStartTime(null), date);
                if (diff < MailUtils.MAX_EMAIL_COUNT_ONEDAY && diff >= 0) {
                    // 是当日键值，递增失败次数
                    redisUtils.hIncrBy(resetCacheKey, "sendNum");
                    return true;
                }
            }
            redisUtils.hPut(resetCacheKey, "sendNum", "1");
            redisUtils.hPut(resetCacheKey, "sendTime", DateUtil.format(new Date(), DateUtil.DATE_FMT_2));
            redisUtils.expire(resetCacheKey, DateAndNumTimesConstant.TIME_ONEDAY);

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
            String cacheKey = CACHE_PREFIX_PASSPORTID_RESETPWDSENDEMAILNUM + email;
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                Map<String, String> mapCacheSendEmailNumResult = redisUtils.hGetAll(cacheKey);
                Date date = DateUtil.parse(mapCacheSendEmailNumResult.get("sendTime"),
                                           DateUtil.DATE_FMT_2);
                long diff = DateUtil.getTimeIntervalMins(DateUtil.getStartTime(null), date);
                if (diff < DateAndNumTimesConstant.TIME_ONEDAY && diff >= 0) {
                    // 是当日键值，验证是否超过次数
                    int checkNum = Integer.parseInt(mapCacheSendEmailNumResult.get("sendNum"));
                    if (checkNum > MailUtils.MAX_EMAIL_COUNT_ONEDAY) {
                        // 当日密码修改次数不超过上限
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
