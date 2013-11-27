package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.CheckNickNameParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-8-6
 * Time: 下午3:37
 */
@Component("accountInfoManager")
public class AccountInfoManagerImpl implements AccountInfoManager {
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoManagerImpl.class);

    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private UserInfoApiManager shPlusUserInfoApiManager;

    public Result uploadImg(byte[] byteArr,String passportId,String type) {
        Result result = new APIResultSupport(false);
        try {
            //判断后缀是否符合要求
            if (!PhotoUtils.checkPhotoExt(byteArr)) {
                result.setCode(ErrorUtil.ERR_CODE_PHOTO_EXT);
                return result;
            }
            //获取图片名
            String imgName = photoUtils.generalFileName();
            // 上传到OP图片平台
            if (photoUtils.uploadImg(imgName, byteArr,null,type)) {
                String imgURL = photoUtils.accessURLTemplate(imgName);
                //更新缓存记录 临时方案 暂时这里写缓存，数据迁移后以 搜狗分支为主（更新库更新缓存）
                String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_AVATARURL_MAPPING + passportId;

                redisUtils.hPut(cacheKey,"sgImg",imgURL);


                result.setSuccess(true);
                result.setDefaultModel("image",imgURL);
                result.setMessage("头像设置成功");
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
            return result;
        }
    }

    @Override
    public Result uploadDefaultImg(String webUrl,String clientId) {
        Result result = new APIResultSupport(false);
        try {
            //获取图片名
            String imgName = clientId+"_"+System.currentTimeMillis();
            // 上传到OP图片平台
            if (photoUtils.uploadImg(imgName, null,webUrl,"1")) {
                String imgURL = photoUtils.accessURLTemplate(imgName);
                //更新缓存记录 临时方案 暂时这里写缓存，数据迁移后以 搜狗分支为主（更新库更新缓存）
                String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_AVATARURL_MAPPING + clientId;

                redisUtils.hPut(cacheKey,"sgImg",imgURL);

                result.setSuccess(true);
                result.setDefaultModel("image",imgURL);
                result.setMessage("头像设置成功");
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
            return result;
        }
    }

    @Override
    public Result obtainPhoto(String imageUrl, String size) {
        Result result = new APIResultSupport(false);
        try {
            String []sizeArry=null;
            //获取size对应的appId
            if(!Strings.isNullOrEmpty(size)){
                //检测是否是支持的尺寸
                sizeArry=size.split(",");

                if(ArrayUtils.isNotEmpty(sizeArry)){
                    for(int i=0;i<sizeArry.length;i++){
                        if(Strings.isNullOrEmpty(photoUtils.getAppIdBySize(sizeArry[i]))){
                            result.setCode(ErrorUtil.ERR_CODE_ERROR_IMAGE_SIZE);
                            return result;
                        }
                    }
                } else {
                    //为空获取所有的尺寸
                   sizeArry=photoUtils.getAllImageSize();
                }

                if(!Strings.isNullOrEmpty(imageUrl) && ArrayUtils.isNotEmpty(sizeArry)){
                    result.setSuccess(true);
                    for (int i=0;i<sizeArry.length;i++){
                        //随机获取cdn域名
                        String cdnUrl=photoUtils.getCdnURL();
                        //获取图片尺寸
                        String clientId=photoUtils.getAppIdBySize(sizeArry[i]);

                        String photoURL =String.format(imageUrl, cdnUrl, clientId);
                        if(!Strings.isNullOrEmpty(photoURL)){
                            result.setDefaultModel("img_"+sizeArry[i],photoURL);
                        }
                    }
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
                    return result;
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
            return result;
        }
        return result;
    }
    @Override
    public Result checkNickName(CheckNickNameParams params) {

        UpdateUserUniqnameApiParams updateUserUniqnameApiParams=buildUpdateUserUniqnameApiParams(params);
        Result result = sgUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);

        return result;
    }

    @Override
    public Result updateUserInfo(AccountInfoParams infoParams,String ip) {

        Result result = new APIResultSupport(false);

        UpdateUserInfoApiParams updateUserInfoApiParams = null;
        // 调用内部接口
        if (ManagerHelper.isInvokeProxyApi(infoParams.getUsername())) {
            updateUserInfoApiParams = new UpdateUserInfoApiParams();
            updateUserInfoApiParams.setUserid(infoParams.getUsername());
            updateUserInfoApiParams.setGender(infoParams.getGender());
            updateUserInfoApiParams.setClient_id(Integer.parseInt(infoParams.getClient_id()));

            //替换sohu日期
            String birthday= !Strings.isNullOrEmpty(infoParams.getBirthday()) ?infoParams.getBirthday():null;
            if (!Strings.isNullOrEmpty(birthday)){
                String []birthdayArr=birthday.split("-");
                String month=birthdayArr[1];
                if(month.startsWith("0")){
                    month="0"+String.valueOf(Integer.parseInt(month)+1);
                } else{
                    month=String.valueOf(Integer.parseInt(month)+1);
                }
                if("010".equals(month)){
                    month="10";
                }
                birthday=birthdayArr[0]+"-"+month+"-"+birthdayArr[2];
            }

            updateUserInfoApiParams.setBirthday(birthday);
            updateUserInfoApiParams.setUsername(infoParams.getFullname());

            updateUserInfoApiParams.setProvince(infoParams.getProvince());

            updateUserInfoApiParams.setCity(infoParams.getCity());
            updateUserInfoApiParams.setPersonalId(infoParams.getPersonalid());
            updateUserInfoApiParams.setModifyip(ip);
            result = proxyUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);

            //更新昵称
            updateUserInfoApiParams.setUniqname(infoParams.getNickname());
            result=shPlusUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
        } else {
            updateUserInfoApiParams = buildUpdateUserInfoApiParams(infoParams, ip);
            result = sgUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
        }

        return result;
    }

    @Override
    public Result getUserInfo(ObtainAccountInfoParams params) {
        Result result = new APIResultSupport(false);

        GetUserInfoApiparams infoApiparams=buildGetUserInfoApiparams(params);

        // 调用内部接口
        if (ManagerHelper.isInvokeProxyApi(params.getUsername())) {
            result = proxyUserInfoApiManager.getUserInfo(infoApiparams);
            //其中昵称是获取的account_base_info
            Result shPlusResult=shPlusUserInfoApiManager.getUserInfo(infoApiparams);
            if(shPlusResult.isSuccess()){
                Object obj= shPlusResult.getModels().get("baseInfo");
                if(obj!=null){
                    AccountBaseInfo baseInfo= (AccountBaseInfo) obj;
                    result.getModels().put("uniqname",baseInfo.getUniqname());
                }
            }
        } else {
            result = sgUserInfoApiManager.getUserInfo(infoApiparams);
        }
        return result;
    }


    private GetUserInfoApiparams buildGetUserInfoApiparams(ObtainAccountInfoParams params) {
        GetUserInfoApiparams infoApiparams=new GetUserInfoApiparams();
        infoApiparams.setFields(params.getFields());
        infoApiparams.setUserid(params.getUsername());
//        infoApiparams.setClient_id(Integer.parseInt(params.getClient_id()));
        return infoApiparams;
    }

    private UpdateUserInfoApiParams buildUpdateUserInfoApiParams(AccountInfoParams infoParams,String ip){
        UpdateUserInfoApiParams updateUserInfoApiParams=new UpdateUserInfoApiParams();
        try {
            updateUserInfoApiParams.setClient_id(Integer.parseInt(infoParams.getClient_id()));
            updateUserInfoApiParams.setUniqname(infoParams.getNickname());
            updateUserInfoApiParams.setUserid(infoParams.getUsername());

            String []birthday=!Strings.isNullOrEmpty(infoParams.getBirthday())?infoParams.getBirthday().split("-"):null;
            Calendar calendar=Calendar.getInstance();
            if(birthday!=null){
                calendar.set(Calendar.YEAR,Integer.valueOf(birthday[0]));
                calendar.set(Calendar.MONTH,Integer.valueOf(birthday[1])-1);
                calendar.set(Calendar.DATE,Integer.valueOf(birthday[2]));
            }

            updateUserInfoApiParams.setBirthday(infoParams.getBirthday());
            updateUserInfoApiParams.setCity(infoParams.getCity());
            updateUserInfoApiParams.setGender(infoParams.getGender());
            updateUserInfoApiParams.setProvince(infoParams.getProvince());
            updateUserInfoApiParams.setFullname(infoParams.getFullname());
            updateUserInfoApiParams.setPersonalId(infoParams.getPersonalid());
            updateUserInfoApiParams.setModifyip(ip);

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return updateUserInfoApiParams;
    }

    private UpdateUserUniqnameApiParams buildUpdateUserUniqnameApiParams(CheckNickNameParams params){
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams=new UpdateUserUniqnameApiParams();
        updateUserUniqnameApiParams.setUniqname(params.getNickname());
        updateUserUniqnameApiParams.setClient_id(Integer.parseInt(params.getClient_id()));
        return updateUserUniqnameApiParams;
    }
}
