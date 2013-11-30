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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
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

    @Override
    public void initConnectAccountBaseInfo(String passportId, ConnectUserInfoVO connectUserInfoVO, boolean isAsync) {
        if (connectUserInfoVO != null) {
            if (isAsync) { // 异步
                initConnectAccountBaseInfo(passportId, connectUserInfoVO.getNickname(), connectUserInfoVO.getImageURL());
            } else { // 同步
                uploadImgExecutor.execute(new UpdateAccountBaseInfoTask(passportId, connectUserInfoVO));
            }
        }
        return;
    }

    @Override
    public AccountBaseInfo queryAccountBaseInfo(String passportId) throws ServiceException {

        String cacheKey = buildAccountBaseInfoKey(passportId);
        AccountBaseInfo accountBaseInfo;
        try {
            accountBaseInfo = redisUtils.getObject(cacheKey, AccountBaseInfo.class);
            if (accountBaseInfo == null) {
                accountBaseInfo = accountBaseInfoDAO.getAccountBaseInfoByPassportId(passportId);
                if (accountBaseInfo != null) {
                    redisUtils.set(cacheKey, accountBaseInfo);
                }
            }
        } catch (Exception e) {
            logger.error("queryAccountBaseInfo fail!", e);
            throw new ServiceException();
        }
        return accountBaseInfo;
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
    public boolean simpleSaveAccountBaseInfo(AccountBaseInfo accountBaseInfo) {
        String passportId = accountBaseInfo.getPassportId();
        try {
            int accountBaseInfoRow = accountBaseInfoDAO.saveAccountBaseInfo(passportId, accountBaseInfo);
            if (accountBaseInfoRow > 0) {
                String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
                redisUtils.set(cacheKey, accountBaseInfo, 30, TimeUnit.DAYS);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("simpleSaveAccountBaseInfo fail", e);
            return false;
        }
    }

    @Override
    public boolean initAccountBaseInfo(String passportId, String uniqname, String avatar) {
        try {
            if (Strings.isNullOrEmpty(uniqname) && Strings.isNullOrEmpty(avatar)) {
                return true;
            }
            AccountBaseInfo accountBaseInfo = queryAccountBaseInfo(passportId);
            boolean isInsertAccountBaseInfo = false;    // 是否需要插入AccountBaseInfo
            boolean isInsertUniqMapping = false;        // 是否需要插入昵称映射表
            if (accountBaseInfo != null) {  // 已经存在昵称头像、需对比是否更新昵称或头像
                String oldUniqname = accountBaseInfo.getUniqname();
                String oldAvatar = accountBaseInfo.getAvatar();
                if (Strings.isNullOrEmpty(oldUniqname)) {
                    accountBaseInfo.setUniqname(uniqname);
                    isInsertUniqMapping = true;
                    isInsertAccountBaseInfo = true;
                }
                if (Strings.isNullOrEmpty(oldAvatar)) {
                    accountBaseInfo.setAvatar(avatar);
                    isInsertAccountBaseInfo = true;
                }
            } else {   // 原先没有昵称头像，则不用对比直接插入
                accountBaseInfo = newAccountBaseInfo(passportId, uniqname, avatar);
                if (!Strings.isNullOrEmpty(uniqname)) {
                    isInsertUniqMapping = true;
                }
                isInsertAccountBaseInfo = true;
            }
            String insertUniqname = accountBaseInfo.getUniqname(); // 需要插入的昵称
            boolean isInsertUniqname = true;
            if (isInsertUniqMapping && !Strings.isNullOrEmpty(insertUniqname)) {  // 先插入昵称头像映射表
                isInsertUniqname = uniqNamePassportMappingService.checkAndInsertUniqName(passportId, insertUniqname);
            }
            if (isInsertAccountBaseInfo) {
                if (!Strings.isNullOrEmpty(insertUniqname) && !isInsertUniqname) {
                    return false;
                }
                return simpleSaveAccountBaseInfo(accountBaseInfo);
            }
            return true;
        } catch (Exception e) {
            logger.error("insertOrUpdateAccountBaseInfo fail", e);
            return false;
        }
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
            initConnectAccountBaseInfo(passportId, connectUserInfoVO.getNickname(), connectUserInfoVO.getImageURL());
        }
    }

    private boolean initConnectAccountBaseInfo(String passportId, String connectNickName, String connectAvatar) {
        String avatar = "";
        if (!Strings.isNullOrEmpty(connectAvatar)) {
            avatar = photoUtils.uploadWebImg(connectAvatar);
        }
        return initAccountBaseInfo(passportId, connectNickName, avatar);
    }

    private String buildAccountBaseInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
    }
}
