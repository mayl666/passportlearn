package com.sogou.upd.passport.service.account.impl;

import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sogou.upd.passport.common.utils.MailUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.EmailSenderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-7 Time: 下午4:18 To change this template use
 * File | Settings | File Templates.
 */
@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private static final String CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDEMAILTOKEN;

    private static final String PASSPORT_RESETPWD_EMAIL_URL="http://account.sogou.com/web/findpwd/checkemail?";

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailUtils mailUtils;

    @Override
    public boolean sendEmailForResetPwd(String uid, int clientId, String address) throws ServiceException {
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            String token = Coder.encryptMD5(uid + clientId + code);
            String activeUrl = PASSPORT_RESETPWD_EMAIL_URL + "uid=" + uid + "&token=" + token;

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
}
