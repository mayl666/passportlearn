package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.UniqNamePassportMappingService;
import net.paoding.rose.jade.annotation.SQLParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-11-28
 * Time: 上午1:39
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountBaseInfoServiceImpl implements AccountBaseInfoService {

    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UniqNamePassportMappingService uniqNamePassportMappingService;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private TaskExecutor uploadImgExecutor;

    private static final Logger logger = LoggerFactory.getLogger(AccountBaseInfoService.class);
    private static final String CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO;

    public AccountBaseInfo getAccountBaseInfoByPassportId(String passport_id) throws ServiceException {
        AccountBaseInfo accountBaseInfo =  accountBaseInfoDAO.getAccountBaseInfoByPassportId(passport_id);
        return accountBaseInfo;
    }

    @Override
    public void asyncUpdateAccountBaseInfo(String passportId, ConnectUserInfoVO connectUserInfoVO) {
        if (connectUserInfoVO != null) {
            //检查昵称是否存在
             if(isUniqNameExist(connectUserInfoVO.getNickname())){

             }
            //检查头像url是否存在
            uploadImgExecutor.execute(new UpdateAccountBaseInfoTask(passportId, connectUserInfoVO));
        }
        return;
    }

    @Override
    public boolean updateUniqname(AccountBaseInfo baseInfo, String uniqname) {
        String oldUniqName = baseInfo.getUniqname();
        String passportId = baseInfo.getPassportId();
        try {
            if (!oldUniqName.equals(uniqname)) {
                //检查昵称是否存在
                if(isUniqNameExist(uniqname)){
                   return false;
                }
                //更新数据库
                int row = accountBaseInfoDAO.updateUniqnameByPassportId(uniqname, passportId);
                if (row > 0) {
                    String cacheKey = buildAccountBaseInfoKey(passportId);
                    baseInfo.setUniqname(uniqname);
                    redisUtils.set(cacheKey, baseInfo, 30, TimeUnit.DAYS);
                    //移除原来映射表
                    if (uniqNamePassportMappingService.removeUniqName(oldUniqName)) {
                        boolean isInsert = uniqNamePassportMappingService.insertUniqName(passportId, uniqname);
                        return isInsert;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("insertOrUpdateAccountBaseInfo fail", e);
            return false;
        }
    }


    @Override
    public boolean insertOrUpdateAccountBaseInfo(String passportId, String uniqname, String avatar) throws ServiceException {
        try {
            boolean isInertMapping = true;
            if (Strings.isNullOrEmpty(uniqname) && Strings.isNullOrEmpty(avatar)) {
                return true;
            }
            if (!Strings.isNullOrEmpty(uniqname)) {
                String existPassportId = uniqNamePassportMappingService.checkUniqName(uniqname);
                if (!Strings.isNullOrEmpty(existPassportId)) {
                    uniqname = "";      // 如果昵称重复则置为空
                } else {
                    isInertMapping = uniqNamePassportMappingService.insertUniqName(passportId, uniqname);
                }
            }
            if (isInertMapping) {
                AccountBaseInfo accountBaseInfo = newAccountBaseInfo(passportId, uniqname, avatar);
                int accountBaseInfoRow = accountBaseInfoDAO.saveAccountBaseInfo(passportId, accountBaseInfo);
                if (accountBaseInfoRow > 0) {
                    String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
                    redisUtils.set(cacheKey, accountBaseInfo, 30, TimeUnit.DAYS);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("insertOrUpdateAccountBaseInfo fail", e);
            return false;
        }
    }

    public boolean isUniqNameExist(String uniqname) {
        if (!Strings.isNullOrEmpty(uniqname)) {
            String existPassportId = uniqNamePassportMappingService.checkUniqName(uniqname);
            if (Strings.isNullOrEmpty(existPassportId)) {
                return false;
            }
        }
        return true;
    }

    private AccountBaseInfo newAccountBaseInfo(String passportId, String uniqname, String avatar) {
        AccountBaseInfo accountBaseInfo = new AccountBaseInfo();
        accountBaseInfo.setPassportId(passportId);
        accountBaseInfo.setUniqname(uniqname);
        accountBaseInfo.setAvatar(avatar);
        return accountBaseInfo;
    }

    class UpdateAccountBaseInfoTask implements Runnable {

        private String passportId;
        private ConnectUserInfoVO connectUserInfoVO;

        UpdateAccountBaseInfoTask(String passportId, ConnectUserInfoVO connectUserInfoVO) {
            this.passportId = passportId;
            this.connectUserInfoVO = connectUserInfoVO;
        }

        @Override
        public void run() {
            String uniqname = connectUserInfoVO.getNickname();
            String connectAvatarUrl = connectUserInfoVO.getImageURL();
            String avatar = photoUtils.uploadWebImg(connectAvatarUrl);
            insertOrUpdateAccountBaseInfo(passportId, uniqname, avatar);
        }
    }

    private String buildAccountBaseInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
    }
}
