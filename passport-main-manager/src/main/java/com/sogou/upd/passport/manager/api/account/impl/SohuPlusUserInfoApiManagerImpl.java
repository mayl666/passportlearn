package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DBRedisUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * sohu+个人资料获取
 * User: mayan
 * Date: 13-11-27
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
@Component("shPlusUserInfoApiManager")
public class SohuPlusUserInfoApiManagerImpl extends BaseProxyManager implements UserInfoApiManager {

    @Autowired
    private DBRedisUtils dbRedisUtils;
    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;

    private static final String CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO;
    private static final String CACHE_PREFIX_NICKNAME_PASSPORTID = CacheConstant.CACHE_PREFIX_NICKNAME_PASSPORTID;
    private static final Logger logger = LoggerFactory.getLogger(SohuPlusUserInfoApiManagerImpl.class);

    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        Result result = new APIResultSupport(false);
        AccountBaseInfo baseInfo = null;
        try {
            String passportId = getUserInfoApiparams.getUserid();
            baseInfo = accountBaseInfoService.queryAccountBaseInfo(passportId);
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
            if (baseInfo != null) {
                String imgUrl = baseInfo.getAvatar();
                //非搜狗图片，重新上传到搜狗op
                if (!Strings.isNullOrEmpty(imgUrl) && !imgUrl.matches("%s/app/[a-z]+/%s/[a-zA-Z0-9]+_\\d+")) {
                    //获取图片名
                    String imgName = photoUtils.generalFileName();
                    // 上传到OP图片平台 ，更新数据库，更新缓存
                    if (photoUtils.uploadImg(imgName, null, imgUrl, "1")) {
                        imgUrl = photoUtils.accessURLTemplate(imgName);
                        //更新数据库
                        int rows = accountBaseInfoDAO.updateAvatarByPassportId(imgUrl, passportId);
                        if (rows != 0) {
                            //更新缓存
                            baseInfo.setAvatar(imgUrl);
                            cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
                            dbRedisUtils.set(cacheKey, baseInfo, 30, TimeUnit.DAYS);
                        }
                    }
                } else {
                    dbRedisUtils.set(cacheKey, baseInfo, 30, TimeUnit.DAYS);
                }
            }
        } catch (Exception e) {
            logger.error("SohuPlusUserInfoApiManagerImpl error", e);
        }
        result.setSuccess(true);
        result.getModels().put("baseInfo", baseInfo);
        return result;
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {

        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        infoApiparams.setUserid(updateUserInfoApiParams.getUserid());
        Result result = getUserInfo(infoApiparams);
        if (result.isSuccess()) {
            Object obj = result.getModels().get("baseInfo");
            AccountBaseInfo accountBaseInfo = null;
            if (obj != null) {
                accountBaseInfo = (AccountBaseInfo) obj;
                boolean flag = accountBaseInfoService.updateUniqname(accountBaseInfo, updateUserInfoApiParams.getUniqname());
                if (flag) {
                    result.setSuccess(true);
                    result.setMessage("修改成功");
                    return result;
                } else {
                    result = new APIResultSupport(false);
                    result.setCode(ErrorUtil.ERR_CODE_UNIQNAME_ALREADY_EXISTS);
                    return result;
                }
            } else {
                try {
                    String passportId = updateUserInfoApiParams.getUserid();
                    String uniqname = updateUserInfoApiParams.getUniqname();

                    accountBaseInfo = new AccountBaseInfo();
                    accountBaseInfo.setAvatar("");
                    accountBaseInfo.setPassportId(updateUserInfoApiParams.getUserid());
                    int row = 0;
                    if (!accountBaseInfoService.isUniqNameExist(uniqname)) {
                        //初始化昵称映射表
                        row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                        if (row > 0) {
                            String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                            dbRedisUtils.set(cacheKey, passportId);
                        }
                        accountBaseInfo.setUniqname(uniqname);
                    } else {
                        accountBaseInfo.setUniqname("");
                    }
                    //初始化accountBaseInfo
                    row = accountBaseInfoDAO.insertAccountBaseInfo(passportId, accountBaseInfo);
                    if (row > 0) {
                        //初始化缓存
                        String cacheKey = buildAccountBaseInfoKey(passportId);
                        dbRedisUtils.set(cacheKey, accountBaseInfo, 30, TimeUnit.DAYS);
                        result.setSuccess(true);
                        result.setMessage("修改成功");
                    }
                    return result;
                } catch (Exception e) {
                    logger.error(String.format("SohuPlusUserInfoApiManagerImpl updateUserInfo error.userid:[%s],uniqname:[%s]",
                            updateUserInfoApiParams.getUserid(), updateUserInfoApiParams.getUniqname()), e);
                }
            }
        }
        return result;
    }


    private String buildAccountBaseInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {
        return null;
    }
}
