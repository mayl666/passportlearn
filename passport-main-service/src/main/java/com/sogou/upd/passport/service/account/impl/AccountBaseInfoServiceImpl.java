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

    @Override
    public void asyncUpdateAccountBaseInfo(String passportId, ConnectUserInfoVO connectUserInfoVO) {
        if (connectUserInfoVO != null) {
            uploadImgExecutor.execute(new UpdateAccountBaseInfoTask(passportId, connectUserInfoVO));
        }
        return;
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
                    uniqname = "";      // 如果第三方昵称重复则默认为空
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
            throw new ServiceException(e);
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
            String uniqname = connectUserInfoVO.getNickname();
            String connectAvatarUrl = connectUserInfoVO.getImageURL();
            String avatar = photoUtils.uploadWebImg(connectAvatarUrl);
            insertOrUpdateAccountBaseInfo(passportId, uniqname, avatar);
        }
    }
}
