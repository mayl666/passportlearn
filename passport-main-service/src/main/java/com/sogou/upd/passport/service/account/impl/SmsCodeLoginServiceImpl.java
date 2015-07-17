package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.SmsCodeLoginService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 短信验证码登录
 * User: chengang
 * Date: 15-6-4
 * Time: 下午3:06
 */
@Service
public class SmsCodeLoginServiceImpl implements SmsCodeLoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsCodeLoginServiceImpl.class);

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Result createSmsCode(final String mobile, final int clientId) {
        Result result = new APIResultSupport(false);
        try {
            //验证请求校验码次数是否超限
            //临时方案：所以code存储的时候都用client=1024与前端interface.js保持一致
            int fakeClientId=1024;
            boolean checkGetIfBeyond = operateTimesService.checkGetSmsCodeNumIfBeyond(mobile, fakeClientId);
            if (checkGetIfBeyond) {
                result.setCode(ErrorUtil.ERROR_CODE_SMS_CODE_GET_FREQUENCY);
                result.setMessage(CommonConstant.SMS_CODE_GET_FREQUENCY);
                return result;
            }

            String smsCodeCacheKey = CacheConstant.CACHE_PREFIX_SMS_CODE_LOGIN + mobile + "_" + fakeClientId;
            String smsCodeVal = redisUtils.get(smsCodeCacheKey);
            if (Strings.isNullOrEmpty(smsCodeVal)) {
                //生成校验码 6位的
                String smsCode = RandomStringUtils.randomNumeric(6);
                //短信内容
                String smsContent = appConfigService.querySmsText(clientId, smsCode);
                if (!Strings.isNullOrEmpty(smsContent) && SMSUtil.sendSMS(mobile, smsContent)) {
                    //未请求过短信校验码、或者校验码已经失效
                    //操作 cache
                    operateCache(smsCode, smsCodeCacheKey, mobile, false);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                    return result;
                }
            } else {
                //上次请求下发的校验码还在有效期内，删除旧的校验码，重新生成新的校验码
                //TODO 继续使用上次下发的校验码 还是重新生成一个新的校验码  6位的
                String newSmsCode = RandomStringUtils.randomNumeric(6);
                String newSmsContent = appConfigService.querySmsText(clientId, newSmsCode);
                if (!Strings.isNullOrEmpty(newSmsContent) && SMSUtil.sendSMS(mobile, newSmsContent)) {
                    //操作cache
                    operateCache(newSmsCode, smsCodeCacheKey, mobile, true);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                    return result;
                }
            }
            result.setSuccess(true);
            result.setMessage(String.format(CommonConstant.SMS_CODE_SEND_MESSAGE, mobile));
        } catch (Exception e) {
            LOGGER.error("SmsCodeLoginService createSmsCode error. message:{}", e.getMessage());
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result checkSmsCode(String mobile, String smsCode, int clientId) {
        Result result = new APIResultSupport(false);
        try {
            //判断 校验码尝试失败次数是否超限制 10次/天
            boolean checkTryIfBeyond = operateTimesService.checkTrySmsCodeNumIfBeyond(mobile, clientId);
            if (checkTryIfBeyond) {
                result.setCode(ErrorUtil.ERROR_CODE_SMS_CODE_TRY_FREQUENCY);
                result.setMessage(CommonConstant.SMS_CODE_TRY_FREQUENCY);
                return result;
            }

            //校验验证码是否过期
            String smsCodeCacheKey = CacheConstant.CACHE_PREFIX_SMS_CODE_LOGIN + mobile + "_" + clientId;
            String smsCodeCacheVal = redisUtils.get(smsCodeCacheKey);
            if (Strings.isNullOrEmpty(smsCodeCacheVal)) {
                result.setCode(ErrorUtil.ERROR_CODE_SMS_CODE_OVER_DUE);
                result.setMessage(CommonConstant.SMS_CODE_OVER_DUE);
                return result;
            }

            //校验验证码是否正确
            if (!Strings.isNullOrEmpty(smsCode) && !smsCode.equalsIgnoreCase(smsCodeCacheVal)) {
                //记录尝试失败次数
                operateTimesService.incTrySmsCodeFailTimes(mobile, clientId);
                result.setCode(ErrorUtil.ERROR_CODE_SMS_CODE_ERROR);
                result.setMessage(CommonConstant.SMS_CODE_CHECK_FAIL);
                return result;
            }

            //验证成功，清除缓存
            delCache(mobile, clientId);

            result.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("SmsCodeLoginService checkSmsCode error. message:{}", e.getMessage());
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }


    /**
     * 操作缓存
     *
     * @param smsCode
     * @param smsCodeCacheKey
     * @param isNew
     */
    private void operateCache(String smsCode, String smsCodeCacheKey, String mobile, boolean isNew) {
        try {
            if (isNew) {
                //重新生成sms code，del cache
                redisUtils.delete(smsCodeCacheKey);
            }
            //set cache
            redisUtils.setWithinSeconds(smsCodeCacheKey, smsCode, SMSUtil.SMS_VALID);
        } catch (Exception e) {
            LOGGER.error("SmsCodeLoginService operateCache error.mobile:{},isNew:{}", mobile, isNew);
        }
    }

    /**
     * 校验码验证成功，清除缓存
     *
     * @param mobile
     * @param clientId
     */
    private void delCache(final String mobile, final int clientId) {
        //验证成功，清除缓存
        redisUtils.delete(CacheConstant.CACHE_PREFIX_SMS_CODE_LOGIN + mobile + "_" + clientId);

//        redisUtils.delete(CacheConstant.CACHE_PREFIX_SMS_CODE_GET_NUM + mobile + "_" + clientId);
//        redisUtils.delete(CacheConstant.CACHE_PREFIX_SMS_CODE_CHECK_FAIL_NUM + mobile + "_" + clientId);
    }
}
