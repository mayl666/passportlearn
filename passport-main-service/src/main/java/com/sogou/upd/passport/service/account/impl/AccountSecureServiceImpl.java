package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.utils.CoreKvUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.ActionRecord;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;
import com.sogou.upd.passport.service.account.generator.SecureCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-21 Time: 上午11:52 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class AccountSecureServiceImpl implements AccountSecureService {

    private static final String CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE = CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE;
    private static final String CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE = CacheConstant.CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE;
    private static final String CACHE_PREFIX_SECURECODE = CacheConstant.CACHE_PREFIX_SECURECODE;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CoreKvUtils coreKvUtils;
    @Autowired
    private TaskExecutor discardTaskExecutor;

    @Override
    public boolean getUpdateSuccessFlag(String passportId) throws ServiceException {
        String cacheKey = buildCacheKey(passportId);
        try {
            String flag = redisUtils.get(cacheKey);
            if (CommonConstant.HAVE_UPDATE.equals(flag)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }


    @Override
    public boolean updateSuccessFlag(String passportId) throws ServiceException {
        try {
            String cachePassportIdKey = buildCacheKey(passportId); //主账号
            redisUtils.setWithinSeconds(cachePassportIdKey, CommonConstant.HAVE_UPDATE, DateAndNumTimesConstant.ONE_HOUR_INSECONDS);
            return true;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private String buildCacheKey(String passportId) {
        return CacheConstant.CACHE_PREFIX_PASSPORTID_UPDATE_PWD_OR_BIND + passportId;
    }

    @Override
    public String getSecureCodeResetPwd(String passportId, int clientId) throws ServiceException {
        return getSecureCode(passportId, clientId, CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE);
    }

    @Override
    public boolean checkSecureCodeResetPwd(String passportId, int clientId, String secureCode)
            throws ServiceException {
        return checkSecureCode(passportId, clientId, secureCode, CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE);
    }

    @Override
    public String getSecureCodeModSecInfo(String passportId, int clientId) throws ServiceException {
        return getSecureCode(passportId, clientId, CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE);
    }

    @Override
    public boolean checkSecureCodeModSecInfo(String passportId, int clientId, String secureCode)
            throws ServiceException {
        return checkSecureCode(passportId, clientId, secureCode, CACHE_PREFIX_PASSPORTID_MODSECINFOSECURECODE);
    }

    @Override
    public String getSecureCodeRandom(String flag) throws ServiceException {
        String scode = UUID.randomUUID().toString().replaceAll("-", "") + flag;
        String cacheKey = CACHE_PREFIX_SECURECODE + scode;
        try {
            redisUtils.setWithinSeconds(cacheKey, flag, DateAndNumTimesConstant.TIME_TWODAY);
            return scode;
        } catch (Exception e) {
            redisUtils.delete(cacheKey);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkSecureCodeRandom(String scode, String flag) throws ServiceException {
        try {
            String cacheKey = CACHE_PREFIX_SECURECODE + scode;
            String value = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(value) || !value.equals(flag)) {
                return false;
            }
            redisUtils.delete(cacheKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setActionRecord(final String userId, final int clientId, final AccountModuleEnum action,
                                final String ip, final String note) {
        discardTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 获取实际需要存储的参数类，节省存储空间
                ActionStoreRecordDO storeRecordDO = new ActionStoreRecordDO(clientId, System.currentTimeMillis(), ip);
                //保存用户行为记录到核心kv
                String coreKvKey = buildCoreKvKeyForActionRecord(userId, action);
                coreKvStoreRecord(coreKvKey, storeRecordDO, DateAndNumTimesConstant.ACTIONRECORD_NUM);
            }
        });

    }

    @Override
    public void setActionRecord(final ActionRecord actionRecord) {
        discardTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (actionRecord == null) {
                    return;
                }
                String userId = actionRecord.getUserId();
                int clientId = actionRecord.getClientId();
                AccountModuleEnum action = actionRecord.getAction();

                // 获取实际需要存储的参数类，节省存储空间
                ActionStoreRecordDO storeRecordDO = actionRecord.obtainStoreRecord();
                //保存用户行为记录到核心kv
                String coreKvKey = buildCoreKvKeyForActionRecord(userId, action);
                coreKvStoreRecord(coreKvKey, storeRecordDO, DateAndNumTimesConstant.ACTIONRECORD_NUM);
            }
        });
    }

    @Override
    public ActionStoreRecordDO getLastActionStoreRecord(String userid, int clientId, AccountModuleEnum action) {
//        String cacheKey = buildCacheKeyForActionRecord(userid, clientId, action);

        //kv迁移，获取用户登录行为数据切换到核心新kv edit by chengang 2014-04-01
        String cacheKey = buildCoreKvKeyForActionRecord(userid, action);
        ActionStoreRecordDO record = queryLastRecord(cacheKey, ActionStoreRecordDO.class);

        return record;
    }

    @Override
    public List<ActionStoreRecordDO> getActionStoreRecords(String userId, int clientId, AccountModuleEnum action) {
        String cacheKey = buildCoreKvKeyForActionRecord(userId, action);
        List<ActionStoreRecordDO> records = queryRecords(cacheKey, ActionStoreRecordDO.class);

        return records;
    }

    private String getSecureCode(String passportId, int clientId, String prefix)
            throws ServiceException {
        String cacheKey = prefix + passportId + "_" + clientId;
        try {
            String secureCode = SecureCodeGenerator.generatorSecureCode(passportId, clientId);
            redisUtils.setWithinSeconds(cacheKey, secureCode, DateAndNumTimesConstant.SECURECODE_VALID);
            return secureCode;
        } catch (Exception e) {
            redisUtils.delete(cacheKey);
            throw new ServiceException(e);
        }
    }

    private boolean checkSecureCode(String passportId, int clientId, String secureCode, String prefix)
            throws ServiceException {
        try {
            String cacheKey = prefix + passportId + "_" + clientId;
            boolean flag = false;
            String scode = redisUtils.get(cacheKey);
            if (!Strings.isNullOrEmpty(scode)) {
                if (scode.equals(secureCode)) {
                    flag = true;
                }
                redisUtils.delete(cacheKey);
            }
            return flag;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /*-------------------------------采用K-V系统-------------------------------*/

    /**
     * 保存用户操作行为到核心kv集群
     *
     * @param key
     * @param record
     * @param maxLen
     * @param <T>
     */
    private <T> void coreKvStoreRecord(String key, T record, int maxLen) {
        coreKvUtils.pushObjectWithMaxLen(key, record, maxLen);
    }

    // 方便以后修改存储方式
    private <T> List<T> queryRecords(String key, Class<T> clazz) {
        return coreKvUtils.getList(key, clazz);
    }

    private <T> T queryLastRecord(String key, Class<T> clazz) {
        return coreKvUtils.top(key, clazz);
    }

    /**
     * 构建保存至核心kv集群 用户行为记录key
     *
     * @param userId
     * @param action
     * @return
     */
    private String buildCoreKvKeyForActionRecord(String userId, AccountModuleEnum action) {
        return CacheConstant.CORE_KV_PREFIX_PASSPORTID_ACTIONRECORD + action + "_" + userId;
    }

}
