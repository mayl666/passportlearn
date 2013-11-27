package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

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
    private RedisUtils redisUtils;
    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private TaskExecutor uploadImgExecutor;


    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        Result result = new APIResultSupport(false);
        final String passportId = getUserInfoApiparams.getUserid();
        String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
        AccountBaseInfo baseInfo = redisUtils.getObject(cacheKey, AccountBaseInfo.class);

        if (baseInfo == null) {
            baseInfo = accountBaseInfoDAO.getAccountBaseInfoByPassportId(passportId);
            if (baseInfo != null) {
                final String imgUrl = baseInfo.getAvatar();
                //非搜狗图片，重新上传到搜狗op
                if (!Strings.isNullOrEmpty(imgUrl) && !imgUrl.matches("http://imgstore\\d\\d.cdn.sogou.com")) {
                    //获取图片名
                    final String imgName = photoUtils.generalFileName();
                    // 上传到OP图片平台 ，更新数据库，更新缓存
                    uploadImgExecutor.execute(new UpdateAccountBaseInfoTask(photoUtils, imgName, imgUrl, passportId, accountBaseInfoDAO, baseInfo, redisUtils));
                }
            }
        }
        result.setDefaultModel("baseInfo",baseInfo);
        return result;
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
class UpdateAccountBaseInfoTask implements Runnable{

    private PhotoUtils photoUtils;
    private String imgName;
    private String imgUrl;
    private String passportId;
    private AccountBaseInfoDAO accountBaseInfoDAO;
    private AccountBaseInfo baseInfo;
    private RedisUtils redisUtils;

    private static final Logger logger = LoggerFactory.getLogger(UpdateAccountBaseInfoTask.class);

    UpdateAccountBaseInfoTask(PhotoUtils photoUtils, String imgName, String imgUrl, String passportId, AccountBaseInfoDAO accountBaseInfoDAO,AccountBaseInfo baseInfo,RedisUtils redisUtils) {
        this.photoUtils = photoUtils;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.passportId = passportId;
        this.accountBaseInfoDAO = accountBaseInfoDAO;
        this.baseInfo=baseInfo;
        this.redisUtils=redisUtils;
    }

    @Override
    public void run() {
        try {
            if (photoUtils.uploadImg(imgName, null, imgUrl, "1")) {
                String imgUrl = photoUtils.accessURLTemplate(imgName);
                //更新数据库
                int rows=accountBaseInfoDAO.updateAvatarByPassportId(imgUrl,passportId);
                if(rows!=0){
                    //更新缓存
                    baseInfo.setAvatar(imgUrl);
                    String cacheKey= CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
                    redisUtils.set(cacheKey,baseInfo);
                }
            }
        }catch (Exception e){
            logger.error("UpdateAccountBaseInfoTask error!",e);
        }

    }
}