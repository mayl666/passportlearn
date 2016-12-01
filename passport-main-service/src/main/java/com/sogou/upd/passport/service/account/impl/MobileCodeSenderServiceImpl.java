package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-6-18 Time: 上午10:22 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class MobileCodeSenderServiceImpl implements MobileCodeSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MobileCodeSenderServiceImpl.class);

    //account与smscode映射
    private static final String CACHE_PREFIX_ACCOUNT_SMSCODE = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE;
    private static final String CACHE_PREFIX_ACCOUNT_SENDNUM = CacheConstant.CACHE_PREFIX_MOBILE_SENDNUM;
    private static final String CACHE_PREFIX_MOBILE_CHECKSMSFAIL = CacheConstant.CACHE_PREFIX_MOBILE_CHECKSMSFAIL;

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean checkIsExistMobileCode(String cacheKey) throws ServiceException {

        cacheKey = CACHE_PREFIX_ACCOUNT_SMSCODE + cacheKey;
        boolean flag = false;
        try {
            flag = redisUtils.checkKeyIsExist(cacheKey);
            return flag;
        } catch (Exception e) {
            // new ServiceException(e);
            return flag;
        }
    }

    @Override
    public boolean updateSmsCacheInfo(String cacheKeySendNum, String cacheKeySmscode,
                                      String curtime, String smsCode) throws ServiceException {
        boolean flag = true;
        try {
            String sendNumStr = redisUtils.get(cacheKeySendNum);
            if (Strings.isNullOrEmpty(sendNumStr) || !StringUtil.checkIsDigit(sendNumStr)) {
                redisUtils.setWithinSeconds(cacheKeySendNum, "1", SMSUtil.SMS_ONEDAY);
            } else {
                redisUtils.increment(cacheKeySendNum);
            }
            Map<String, String> mapData = Maps.newHashMap();
            mapData.put("smsCode", smsCode);    //初始化验证码
            mapData.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间
            redisUtils.hPutAll(cacheKeySmscode, mapData);
        } catch (Exception e) {
            flag = false;
            logger.error("[SMS] service method updateSmsCacheInfo error.{}", e);
            new ServiceException(e);
        }
        return flag;
    }

    @Override
    public boolean deleteSmsCache(String mobile, int clientId) throws ServiceException {
        boolean flag = true;
        try {
            redisUtils.delete(CACHE_PREFIX_ACCOUNT_SMSCODE + mobile + "_" + clientId);
            redisUtils.delete(CACHE_PREFIX_ACCOUNT_SENDNUM + mobile + "_" + clientId);
        } catch (Exception e) {
            flag = false;
            logger.error("[SMS] service method deleteSmsCache error.{}", e);
            new ServiceException(e);
        }
        return flag;
    }

    @Override
    public boolean deleteSmsCache(String mobile, AccountModuleEnum module) throws ServiceException {
        boolean flag = true;
        try {
            String cacheKey = buildCacheKeyForSmsCode(mobile, module);
            redisUtils.delete(cacheKey);
        } catch (Exception e) {
            flag = false;
            logger.error("[SMS] service method deleteSmsCache error.{}", e);
            new ServiceException(e);
        }
        return flag;
    }

    @Override
    public Result sendSmsCode(String mobile, int clientId, AccountModuleEnum module) throws ServiceException {
        Result result = new APIResultSupport(false);
        try {
            if (!checkLimitForSendSms(mobile, clientId, module)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS);
                return result;
            }
            String cacheKeySendNum = buildCacheKeyForSmsLimit(mobile, clientId, module);


            //写入缓存
            String cacheKey = buildCacheKeyForSmsCode(mobile, module);
            //初始化缓存映射
            Map<String, String> cacheMap = redisUtils.hGetAll(cacheKey);
            if (MapUtils.isEmpty(cacheMap) || !StringUtil.checkIsDigit(cacheMap.get("sendTime"))) {
                //生成随机数
                String randomCode = RandomStringUtils.randomNumeric(5);
                //读取短信内容
                String smsText = appConfigService.querySmsText(clientId, randomCode);
                if (!Strings.isNullOrEmpty(smsText) && SMSUtil.sendSMS(mobile, smsText)) {
                    //更新缓存
                    updateSmsCacheInfo(cacheKeySendNum, cacheKey, String.valueOf(System.currentTimeMillis()), randomCode);
                    redisUtils.expire(cacheKey, SMSUtil.SMS_VALID);

                    result.setSuccess(true);
                    result.setMessage("验证码已发送至" + mobile);
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                    return result;
                }
            } else {
                //获取缓存数据
                long sendTime = Long.parseLong(cacheMap.get("sendTime"));
                long curtime = System.currentTimeMillis();
                boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
                if (!valid) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MINUTELIMIT);
                    return result;
                }
                // 使用原来验证码
                String randomCode = cacheMap.get("smsCode");
                //读取短信内容
                if (Strings.isNullOrEmpty(randomCode)) {
                    randomCode = RandomStringUtils.randomNumeric(5);
                }
                String smsText = appConfigService.querySmsText(clientId, randomCode);
                if (!Strings.isNullOrEmpty(smsText) && SMSUtil.sendSMS(mobile, smsText)) {
                    //更新缓存
                    updateSmsCacheInfo(cacheKeySendNum, cacheKey, String.valueOf(curtime), randomCode);
                    result.setSuccess(true);
                    result.setMessage("验证码已发送至" + mobile);
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                    return result;
                }
            }
        } catch (Exception e) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
            logger.error("[SMS] service method sendSms error.{}", e);
            return result;
        }
    }

    public boolean checkLimitForSendSms(String mobile, int clientId, AccountModuleEnum module) {
        try {
            String cacheKey = buildCacheKeyForSmsLimit(mobile, clientId, module);
            String sendNumStr = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(sendNumStr) || !StringUtil.checkIsDigit(sendNumStr)) {
                return true;
            } else {
                //如果存在，判断是否已经超出日发送最高限额   (比如30分钟后失效了，再次获取验证码 需要和此用户当天发送的总的条数对比)
                int sendNum = Integer.parseInt(sendNumStr);
                if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("[SMS] service method check sms send limit error.{}", e);
            return true; // TODO:缓存出现问题不影响发送短信
        }
    }

    @Override
    public boolean checkSmsInfoFromCache(String mobile, int clientId, AccountModuleEnum module, String smsCode)
            throws ServiceException {
        try {
            String cacheKey = buildCacheKeyForSmsCode(mobile, module);
            Map<String, String> mapResult = redisUtils.hGetAll(cacheKey);
            if (MapUtils.isNotEmpty(mapResult)) {
                String strValue = mapResult.get("smsCode");
                if (StringUtils.isNotBlank(strValue) && strValue.equals(smsCode)) {
                    return true;
                } else {
                    setSmsFailLimited(mobile, clientId, module);
                }
            }
            return false;
        } catch (Exception e) {
            // new ServiceException(e);
            return false;
        }
    }


    @Override
    public boolean checkLimitForSmsFail(String mobile, int clientId, AccountModuleEnum module)
            throws ServiceException {
        try {
            String cacheKey = buildCacheKeyForSmsFailLimit(mobile, clientId, module);
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                int checkNum = Integer.parseInt(redisUtils.get(cacheKey));
                if (checkNum > SMSUtil.MAX_CHECKSMS_COUNT_ONEDAY) {
                    // 当日验证码输入错误次数不超过上限
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            // new ServiceException(e);
            return false; // 防止redis宕机时，频繁短信验证
        }
    }

    @Override
    public Result checkSmsCode(String mobile, int clientId, AccountModuleEnum module, String smsCode) throws ServiceException {
        Result result = new APIResultSupport(false);
        try {
            if (!checkLimitForSmsFail(mobile, clientId, module)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT);
                return result;
            }

            // 验证手机号码与验证码是否匹配
            if (!checkSmsInfoFromCache(mobile, clientId, module, smsCode)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
                return result;
            }

            //清除验证码的缓存
            deleteSmsCache(mobile, module);
            result.setSuccess(true);
//            result.setMessage("短信随机码验证成功！");
            return result;
        } catch (Exception e) {
            logger.error("[SMS] service method check sms code error.{}", e);
            // new ServiceException(e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 设置smscode验证次数限制，验证失败时递增
     */
    private boolean setSmsFailLimited(String mobile, int clientId, AccountModuleEnum module) throws ServiceException {
        try {
            String cacheKey = buildCacheKeyForSmsFailLimit(mobile, clientId, module);
            // 由于既需要自增，同时又需要设置有效时间，无法在自增同时设置有效时间，不可避免建立两次连接
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                redisUtils.increment(cacheKey);
            } else {
                redisUtils.setWithinSeconds(cacheKey, "1", DateAndNumTimesConstant.TIME_ONEDAY);
                /*redisUtils.set(cacheKey, "1");
                redisUtils.expire(cacheKey, DateAndNumTimesConstant.TIME_ONEDAY);*/
            }
            return true;
        } catch (Exception e) {
            logger.error("[SMS] service method setSmsFailLimited error.{}", e);
            new ServiceException(e);
        }
        return false;
    }

    private String buildCacheKeyForSmsCode(String mobile, AccountModuleEnum module) {
        //key中不能包含clientId，解决别的应用跳转过来产生的问题
        return CACHE_PREFIX_ACCOUNT_SMSCODE + module + "_" + mobile;
    }

    private String buildCacheKeyForSmsFailLimit(String mobile, int clientId, AccountModuleEnum module) {
        return CACHE_PREFIX_MOBILE_CHECKSMSFAIL + module + "_" + mobile + "_" +
                DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
    }

    private String buildCacheKeyForSmsLimit(String mobile, int clientId, AccountModuleEnum module) {
        return CACHE_PREFIX_ACCOUNT_SENDNUM + module + "_" + mobile + "_" +
                DateUtil.format(new Date(), DateUtil.DATE_FMT_0);
    }
}
