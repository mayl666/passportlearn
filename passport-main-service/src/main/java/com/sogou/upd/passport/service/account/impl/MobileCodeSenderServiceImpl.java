package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
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
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-21 Time: 下午5:59 To change this template
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
        } catch (Exception e) {
            logger.error("[SMS] service method checkCacheKeyIsExist error.{}", e);
            new ServiceException(e);
        }
        return flag;
    }

    @Override
    public boolean updateSmsCacheInfo(String cacheKeySendNum, String cacheKeySmscode,
                                      String curtime,String smsCode) throws ServiceException {
        boolean flag = true;
        try {
            redisUtils.hIncrBy(cacheKeySendNum, "sendNum");
            redisUtils.hPut(cacheKeySmscode, "sendTime", curtime);
            redisUtils.hPut(cacheKeySmscode, "smsCode", smsCode);
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
    public Result handleSendSms(String mobile, int clientId) throws ServiceException {
        Result result = null;
        try {
            String cacheKey = CACHE_PREFIX_ACCOUNT_SENDNUM + mobile + "_" + clientId;

            if (!redisUtils.checkKeyIsExist(cacheKey)) {
                boolean flag = redisUtils.hPutIfAbsent(cacheKey, "sendNum", "1");
                if (flag) {
                    redisUtils.expire(cacheKey, SMSUtil.SMS_ONEDAY);
                }
            } else {
                //如果存在，判断是否已经超出日发送最高限额   (比如30分钟后失效了，再次获取验证码 需要和此用户当天发送的总的条数对比)
                Map<String, String> mapCacheSendNumResult = redisUtils.hGetAll(cacheKey);
                if (MapUtils.isNotEmpty(mapCacheSendNumResult)) {
                    int sendNum = Integer.parseInt(mapCacheSendNumResult.get("sendNum"));
                    if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {     //每日最多发送短信验证码条数
                        redisUtils.hIncrBy(cacheKey, "sendNum");
                    } else {
                        result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS);
                        return result;
                    }
                }
            }
            //生成随机数
            String randomCode = RandomStringUtils.randomNumeric(6);
            //写入缓存
            cacheKey = CACHE_PREFIX_ACCOUNT_SMSCODE + mobile + "_" + clientId;
            //初始化缓存映射
            Map<String, String> mapData = Maps.newHashMap();
            mapData.put("smsCode", randomCode);    //初始化验证码
            mapData.put("mobile", mobile);        //发送手机号
            mapData.put("sendTime", Long.toString(System.currentTimeMillis()));   //发送时间

            redisUtils.hPutAll(cacheKey, mapData);

            //设置失效时间 30分钟  ，1800秒
            redisUtils.expire(cacheKey, SMSUtil.SMS_VALID);

            //读取短信内容
            String smsText = appConfigService.querySmsText(clientId, randomCode);
            if (!Strings.isNullOrEmpty(smsText) && SMSUtil.sendSMS(mobile, smsText)) {
                result = Result.buildSuccess("获取验证码成功");
            } else {
                result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
            }
        } catch (Exception e) {
            result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
            logger.error("[SMS] service method handleSendSms error.{}", e);
            new ServiceException(e);
        }
        return result;
    }

    @Override
    public boolean checkSmsInfoFromCache(String account, String smsCode, int clientId)
            throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_ACCOUNT_SMSCODE + account + "_" + clientId;
            Map<String, String> mapResult = redisUtils.hGetAll(cacheKey);
            if (MapUtils.isNotEmpty(mapResult)) {
                String strValue = mapResult.get("smsCode");
                if (StringUtils.isNotBlank(strValue) && strValue.equals(smsCode)) {
                    return true;
                }
                // TODO:目前所有手机随机码验证服务统一限制验证失败次数
                setSmsFailLimited(account, clientId);
            }
        } catch (Exception e) {
            logger.error("[SMS] service method checkSmsInfoFromCache error.{}", e);
            new ServiceException(e);
        }
        return false;
    }

    /*
     * 设置smscode验证次数限制，验证失败时递增
     * TODO：暂时为private方法，只供checkSmsInfoFromCache方法调用
     */
    private boolean setSmsFailLimited(String account, int clientId) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_MOBILE_CHECKSMSFAIL + account + "_" + clientId;
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                // cacheKey存在，则检查failTime
                Map<String, String> mapCacheFailNumResult = redisUtils.hGetAll(cacheKey);
                Date date = DateUtil.parse(mapCacheFailNumResult.get("failTime"), DateUtil.DATE_FMT_3);
                long diff = DateUtil.getTimeIntervalMins(DateUtil.getStartTime(null), date);
                if (diff < DateAndNumTimesConstant.TIME_ONEDAY && diff >= 0) {
                    // 是当日键值，递增失败次数
                    redisUtils.hIncrBy(cacheKey, "failNum");
                    return true;
                }
            }
            redisUtils.hPut(cacheKey, "failNum", "1");
            redisUtils.hPut(cacheKey, "failTime", DateUtil.format(new Date(), DateUtil.DATE_FMT_2));
            redisUtils.expire(cacheKey, SMSUtil.SMS_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("[SMS] service method setSmsFailLimited error.{}", e);
            new ServiceException(e);
        }
        return false;
    }

    @Override
    public boolean checkSmsFailLimited(String account, int clientId)
            throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_MOBILE_CHECKSMSFAIL + account + "_" + clientId;
            if (redisUtils.checkKeyIsExist(cacheKey)) {
                // 键存在，验证是否当日的键值
                Map<String, String> mapCacheFailNumResult = redisUtils.hGetAll(cacheKey);
                Date date = DateUtil.parse(mapCacheFailNumResult.get("failTime"), DateUtil.DATE_FMT_2);
                long diff = DateUtil.getTimeIntervalMins(DateUtil.getStartTime(null), date);
                if (diff < DateAndNumTimesConstant.TIME_ONEDAY && diff >= 0) {
                    // 是当日键值，验证是否超过次数
                    int checkNum = Integer.parseInt(mapCacheFailNumResult.get("failNum"));
                    if (checkNum > SMSUtil.MAX_CHECKSMS_COUNT_ONEDAY) {
                        // 当日验证码输入错误次数不超过上限
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("[SMS] service method checkSmsFailLimited error.{}", e);
            new ServiceException(e);
        }
        return false;
    }

    @Override
    public Map<String, String> getCacheMapByKey(String cacheKey) throws ServiceException {
        Map<String, String> mapCacheResult = null;
        try {
            mapCacheResult = redisUtils.hGetAll(cacheKey);
        } catch (Exception e) {
            logger.error("[SMS] service method getCacheMapByKey error.{}", e);
            new ServiceException(e);
        }

        return mapCacheResult;
    }

}
