package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.exception.MailException;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.MailUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.EmailSenderService;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;
import com.sogou.upd.passport.service.account.dataobject.WapActiveEmailDO;
import com.sogou.upd.passport.service.account.generator.SecureCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-7 Time: 下午4:18 To change this template use
 * File | Settings | File Templates.
 */
@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

    // TODO:以下PASSPORT_RESETPWD_EMAIL_URL值仅供本机测试用
    // TODO:绑定验证URL待修改，考虑以后其他验证EMAIL的URL
    private static final String PASSPORT_EMAIL_URL_PREFIX = CommonConstant.DEFAULT_INDEX_URL + "/";
    private static final String PASSPORT_EMAIL_URL_SUFFIX = "/checkemail?";
    private static final Map<AccountModuleEnum, String> subjects = AccountModuleEnum.buildEmailSubjects();

    private static final String CACHE_PREFIX_PASSPORTID_EMAILSCODE = CacheConstant.CACHE_PREFIX_PASSPORTID_EMAILSCODE;
    private static final String CACHE_PREFIX_PASSPORTID_SENDEMAILNUM = CacheConstant.CACHE_PREFIX_PASSPORTID_SENDEMAILNUM;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailUtils mailUtils;

    @Override
    public boolean sendEmail(ActiveEmailDO activeEmailDO)
            throws ServiceException {
        try {
            String passportId = activeEmailDO.getPassportId();
            int clientId = activeEmailDO.getClientId() == 0 ? CommonConstant.SGPP_DEFAULT_CLIENTID : activeEmailDO.getClientId();
            String scode = SecureCodeGenerator.generatorSecureCode(passportId, clientId);
            String activeUrl = buildActiveUrl(activeEmailDO, scode);
            AccountModuleEnum module = activeEmailDO.getModule();
            String address = activeEmailDO.getToEmail();
            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);
            //模版中参数替换
            Map<String, Object> map = Maps.newHashMap();
            map.put("activeUrl", activeUrl);
            map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            activeEmail.setMap(map);
            activeEmail.setTemplateFile(module.getDirect() + ".vm");
            activeEmail.setSubject(subjects.get(module));
            activeEmail.setCategory(module.getDirect());
            activeEmail.setToEmail(address);
            mailUtils.sendEmail(activeEmail);
            //连接失效时间
            String cacheKey = buildCacheKeyForScode(passportId, clientId, module);
            if (activeEmailDO.isSaveEmail()) {
                Map<String, String> mapResult = new HashMap<>();
                mapResult.put("email", address);
                mapResult.put("scode", scode);
                redisUtils.setWithinSeconds(cacheKey, mapResult, DateAndNumTimesConstant.TIME_TWODAY);
            } else {
                redisUtils.setWithinSeconds(cacheKey, scode, DateAndNumTimesConstant.TIME_TWODAY);
            }
            return true;
        } catch (MailException me) {
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //构建激活邮件中的激活链接public
    private  String buildActiveUrl(ActiveEmailDO activeEmailDO, String scode) throws Exception {
        String prefix = activeEmailDO.getPrefix();
        String passportId = activeEmailDO.getPassportId();
        String ru = Strings.isNullOrEmpty(activeEmailDO.getRu()) ? prefix : activeEmailDO.getRu();

        StringBuilder activeUrl = new StringBuilder();
        activeUrl.append(prefix);
        activeUrl.append("/");
        if (activeEmailDO instanceof WapActiveEmailDO) {   //一定要先判断子类类型，最后判断父类类型
            activeUrl.append("wap");
        } else {
            activeUrl.append("web");
        }
        activeUrl.append("/");
        activeUrl.append(activeEmailDO.getModule().getDirect());
        activeUrl.append(PASSPORT_EMAIL_URL_SUFFIX);
        activeUrl.append("username=" + passportId);
        activeUrl.append("&client_id=" + activeEmailDO.getClientId());
        activeUrl.append("&scode=" + scode);
        if (activeEmailDO instanceof WapActiveEmailDO) {
            WapActiveEmailDO wapActiveEmailDO = (WapActiveEmailDO) activeEmailDO;
            if (!Strings.isNullOrEmpty(wapActiveEmailDO.getV()))
                activeUrl.append("&v=" + wapActiveEmailDO.getV());
            if (!Strings.isNullOrEmpty(wapActiveEmailDO.getSkin()))
                activeUrl.append("&skin=" + wapActiveEmailDO.getSkin());
        }
        activeUrl.append("&ru=" + Coder.encodeUTF8(ru));
        return activeUrl.toString();
    }

    @Override
    public boolean sendBindEmail(String passportId, int clientId, AccountModuleEnum module, String address, String ru)
            throws ServiceException {
        try {
            String scode = SecureCodeGenerator.generatorSecureCode(passportId, clientId);
            String activeUrl = PASSPORT_EMAIL_URL_PREFIX + module.getDirect() + PASSPORT_EMAIL_URL_SUFFIX;
            activeUrl += "username=" + passportId + "&client_id=" + clientId + "&scode=" + scode + "&ru=" + ru;

            //发送邮件
            ActiveEmail activeEmail = new ActiveEmail();
            activeEmail.setActiveUrl(activeUrl);

            //模版中参数替换
            Map<String, Object> map = Maps.newHashMap();
            map.put("activeUrl", activeUrl);
            activeEmail.setMap(map);

            activeEmail.setTemplateFile(module.getDirect() + ".vm");
            activeEmail.setSubject(subjects.get(module));
            activeEmail.setCategory(module.getDirect());
            activeEmail.setToEmail(address);
            mailUtils.sendEmail(activeEmail);

            //连接失效时间
            String cacheKey = buildCacheKeyForScode(passportId, clientId, module);
            Map<String, String> mapResult = Maps.newHashMap();
            mapResult.put("email", address);
            mapResult.put("scode", scode);
            redisUtils.setWithinSeconds(cacheKey, mapResult, DateAndNumTimesConstant.TIME_TWODAY);
            return true;
        } catch (MailException me) {
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public String checkScodeForEmail(String passportId, int clientId, AccountModuleEnum module, String scode, boolean saveEmail)
            throws ServiceException {
        try {
            String cacheKey = buildCacheKeyForScode(passportId, clientId, module);
            if (saveEmail) {
                Map<String, String> mapScode = redisUtils.getObject(cacheKey, Map.class);
                if (mapScode != null && !mapScode.isEmpty()) {
                    String scodeCache = mapScode.get("scode");
                    if (!Strings.isNullOrEmpty(scodeCache) && scodeCache.equals(scode)) {
                        return mapScode.get("email");
                    }
                }
            } else {
                String scodeCache = redisUtils.get(cacheKey);
                if (!Strings.isNullOrEmpty(scodeCache) && scodeCache.equals(scode)) {
                    return passportId;
                }
            }
            return null;
        } catch (Exception e) {
            // throw new ServiceException(e);
            logger.error("[Email] service method checkScodeForEmail error.", e);
            return null;
        }
    }

    @Override
    public boolean incLimitForSendEmail(String userId, int clientId, AccountModuleEnum module, String email)
            throws ServiceException {
        String cacheKey = buildCacheKeyForEmailLimited(userId, clientId, module, email);
        try {
            // 设置邮件发送次数限制
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                redisUtils.increment(cacheKey);
            } else {
                redisUtils.setWithinSeconds(cacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY);
            }
            return true;
        } catch (Exception e) {
            logger.error("[Email] service method inc limit for send email error.", e);
            return false;
        }
    }

    @Override
    public boolean checkLimitForSendEmail(String passportId, int clientId, AccountModuleEnum module, String email)
            throws ServiceException {
        try {
            String cacheKey = buildCacheKeyForEmailLimited(passportId, clientId, module, email);
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                int checkNum = Integer.parseInt(redisUtils.get(cacheKey));
                if (checkNum >= MailUtils.MAX_EMAIL_COUNT_ONEDAY) {
                    // 当日邮件发送次数不超过上限
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("[Email] service method checkLimitForSendEmail error.", e);
            return true;
        }
    }

    @Override
    public boolean deleteScodeCacheForEmail(String passportId, int clientId, AccountModuleEnum module) throws ServiceException {
        try {
            String cacheKey = buildCacheKeyForScode(passportId, clientId, module);
            redisUtils.delete(cacheKey);
            return true;
        } catch (Exception e) {
            logger.error("[Email] service method deleteScodeCacheForEmail error.", e);
            return false;
        }
    }

    private String buildCacheKeyForScode(String passportId, int clientId, AccountModuleEnum module) {
        return CACHE_PREFIX_PASSPORTID_EMAILSCODE + module + "_" + clientId + "_" + passportId;
    }

    private String buildCacheKeyForEmailLimited(String passportId, int clientId, AccountModuleEnum module, String email) {
        return CACHE_PREFIX_PASSPORTID_SENDEMAILNUM + module + "_" + email + "_"
                + DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
    }

    private String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encode param error.", e);
            return param;
        }
    }
}
