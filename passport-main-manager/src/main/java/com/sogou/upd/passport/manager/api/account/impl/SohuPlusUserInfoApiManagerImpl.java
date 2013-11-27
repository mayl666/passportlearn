package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.exception.ServiceException;
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
    private RedisUtils redisUtils;
    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private TaskExecutor uploadImgExecutor;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;

    private static final String CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO;
    private static final String CACHE_PREFIX_NICKNAME_PASSPORTID = CacheConstant.CACHE_PREFIX_NICKNAME_PASSPORTID;

    private static final Logger logger = LoggerFactory.getLogger(SohuPlusUserInfoApiManagerImpl.class);


    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        Result result = new APIResultSupport(false);
        AccountBaseInfo baseInfo=null;
        try {
            final String passportId = getUserInfoApiparams.getUserid();
            String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
            baseInfo = redisUtils.getObject(cacheKey, AccountBaseInfo.class);

            if (baseInfo == null) {
                baseInfo = accountBaseInfoDAO.getAccountBaseInfoByPassportId(passportId);
                if (baseInfo != null) {
                    final String imgUrl = baseInfo.getAvatar();
                    //非搜狗图片，重新上传到搜狗op
                    if (!Strings.isNullOrEmpty(imgUrl) && !imgUrl.matches("%s/app/[a-z]+/%s/[a-zA-Z0-9]+_\\d+")) {
                        //获取图片名
                        final String imgName = photoUtils.generalFileName();
                        // 上传到OP图片平台 ，更新数据库，更新缓存
                        uploadImgExecutor.execute(new UpdateAccountBaseInfoTask(photoUtils, imgName, imgUrl, passportId, accountBaseInfoDAO, baseInfo, redisUtils));
                    }else{
                        redisUtils.set(cacheKey, baseInfo, 30, TimeUnit.DAYS);
                    }
                }
            }
        }catch (Exception e){
            logger.error("SohuPlusUserInfoApiManagerImpl error",e);
        }
        result.setSuccess(true);
        result.getModels().put("baseInfo",baseInfo);
        return result;
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {

        GetUserInfoApiparams infoApiparams= new GetUserInfoApiparams();
        infoApiparams.setUserid(updateUserInfoApiParams.getUserid());
        Result result = getUserInfo(infoApiparams);
        if(result.isSuccess()){
            Object obj=result.getModels().get("baseInfo");
            AccountBaseInfo accountBaseInfo=null;
            if(obj!=null){
                accountBaseInfo= (AccountBaseInfo) obj;
                boolean flag=updateUserInfo(accountBaseInfo,updateUserInfoApiParams.getUniqname());
                if(flag){
                    result.setSuccess(true);
                    result.setMessage("修改成功");
                    return result;
                }
            } else {
                String passportId=updateUserInfoApiParams.getUserid();
                String uniqname=updateUserInfoApiParams.getUniqname();

                accountBaseInfo=new AccountBaseInfo();
                accountBaseInfo.setUniqname(uniqname);
                accountBaseInfo.setAvatar("");
                accountBaseInfo.setPassportId(updateUserInfoApiParams.getUserid());
                //初始化accountBaseInfo
                accountBaseInfoDAO.insertAccountBaseInfo(passportId,accountBaseInfo);
                //初始化昵称映射表
                int row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                String cacheKey =null;
                if (row > 0) {
                    cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                    redisUtils.set(cacheKey, passportId);
                }
                //初始化缓存
                cacheKey = buildAccountBaseInfoKey(passportId);
                redisUtils.set(cacheKey, accountBaseInfo, 30, TimeUnit.DAYS);
                result.setSuccess(true);
                result.setMessage("修改成功");
                return result;
            }
        }
        return result;
    }

    public boolean updateUserInfo(AccountBaseInfo baseInfo,String uniqname) {
        String oldUniqName = baseInfo.getUniqname();
        String passportId = baseInfo.getPassportId();
        if(!Strings.isNullOrEmpty(oldUniqName) && !oldUniqName.equals(uniqname)){
            //更新数据库
            int row = accountBaseInfoDAO.updateUniqnameByPassportId(uniqname, passportId);
            if (row > 0) {
                String cacheKey = buildAccountBaseInfoKey(passportId);
                baseInfo.setUniqname(uniqname);
                redisUtils.set(cacheKey, baseInfo, 30, TimeUnit.DAYS);

                //移除原来映射表
                if (removeUniqName(oldUniqName)) {
                    //更新新的映射表
                    row = uniqNamePassportMappingDAO.insertUniqNamePassportMapping(uniqname, passportId);
                    if (row > 0) {
                        cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                        redisUtils.set(cacheKey, passportId);
                    }
                    return true;
                }
            }
        }
        return true;
    }

    //缓存中移除原来昵称
    public boolean removeUniqName(String uniqname) throws ServiceException {
        try {
            if (!Strings.isNullOrEmpty(uniqname)) {
                //更新映射
                int row = uniqNamePassportMappingDAO.deleteUniqNamePassportMapping(uniqname);
                if (row > 0) {
                    String cacheKey = CACHE_PREFIX_NICKNAME_PASSPORTID + uniqname;
                    redisUtils.delete(cacheKey);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("removeUniqName fail", e);
            return false;
        }
        return false;
    }

    private String buildAccountBaseInfoKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
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
                    redisUtils.set(cacheKey, baseInfo, 30, TimeUnit.DAYS);
                }
            }
        }catch (Exception e){
            logger.error("UpdateAccountBaseInfoTask error!",e);
        }

    }
}