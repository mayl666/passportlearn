package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountRoamInfo;
import com.sogou.upd.passport.service.account.AccountRoamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 兼容游戏支持搜狗域、搜狐域、第三方账号漫游service
 * User: chengang
 * Date: 14-7-29
 * Time: 上午11:44
 */
@Service
public class AccountRoamServiceImpl implements AccountRoamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRoamServiceImpl.class);

    //漫游用户信息 数据分割符
    private static final String ACCOUNT_ROAM_SEPARATOR_1 = "|";
    private static final String ACCOUNT_ROAM_SEPARATOR_2 = ":";


    @Autowired
    private RedisUtils redisUtils;

    /**
     * 根据sgId 获取漫游用户信息
     *
     * @param r_key
     * @return
     * @throws ServiceException
     */
    @Override
    public AccountRoamInfo getAccountRoamInfoBySgId(String r_key) throws ServiceException {
        AccountRoamInfo accountRoamInfo = new AccountRoamInfo();

        try {
            //根据sgId 从缓存中获取漫游用户信息
            if (!Strings.isNullOrEmpty(r_key)) {
                String cache_key = "前缀" + r_key;

                //数据格式：version:xxxx|userid:xxxx|status:xxxx（登录状态）|ct:xxxx(请求时间)|ip:xxxx(用户真实ip)
                String roamInfo = redisUtils.get(cache_key);
                if (!Strings.isNullOrEmpty(roamInfo)) {
                    Map<String, String> roamMap = Splitter.on(ACCOUNT_ROAM_SEPARATOR_1).withKeyValueSeparator(ACCOUNT_ROAM_SEPARATOR_2).split(roamInfo);
                    if (roamMap != null && !roamMap.isEmpty()) {
                        for (Map.Entry<String, String> kv : roamMap.entrySet()) {
                            if (kv.getKey().equals("version")) {
                                accountRoamInfo.setVersion(kv.getValue());
                            }
                            if (kv.getKey().equals("userid")) {
                                accountRoamInfo.setUserId(kv.getValue());
                            }
                            if (kv.getKey().equals("status")) {
                                accountRoamInfo.setStatus(kv.getValue());
                            }
                            if (kv.getKey().equals("ip")) {
                                accountRoamInfo.setRequestIp(kv.getValue());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("getAccountRoamInfoBySgId error. r_key:" + r_key);
            throw new ServiceException(e);
        }
        return accountRoamInfo;
    }

    /**
     * 根据sgId 清除漫游用户信息
     * <p/>
     * 验证sgId正确性后清除漫游用户信息
     *
     * @param r_key
     * @return
     * @throws ServiceException
     */
    @Override
    public void clearAccountRoamInfoBySgId(String r_key) throws ServiceException {
        try {
            if (!Strings.isNullOrEmpty(r_key)) {
                String cache_key = "前缀" + r_key;
                redisUtils.delete(cache_key);
            }
        } catch (Exception e) {
            LOGGER.error("clearAccountRoamInfoBySgId error. r_key:" + r_key);
            throw new ServiceException(e);
        }
    }


}
