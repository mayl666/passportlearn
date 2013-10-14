package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.CheckNickNameParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * User: mayan
 * Date: 13-8-6
 * Time: 下午3:37
 */
@Component
public class AccountInfoManagerImpl implements AccountInfoManager {
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoManagerImpl.class);

    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;

    public Result uploadImg(byte[] byteArr, String passportId,String type) {
        Result result = new APIResultSupport(false);
        try {
            //判断后缀是否符合要求
            if (!PhotoUtils.checkPhotoExt(byteArr)) {
                result.setCode(ErrorUtil.ERR_PHOTO_EXT);
                return result;
            }
            //获取图片名
            String imgName = PhotoUtils.generalFileName();
            // 上传到OP图片平台
            if (photoUtils.uploadImg(imgName, byteArr,null,type)) {
                String imgURL = photoUtils.accessURLTemplate(imgName);
                //更新数据库 缓存记录
                Account account = commonManager.queryAccountByPassportId(passportId);
                if (account != null) {
                    if (accountService.updateImage(account, imgURL)) {
                        result.setSuccess(true);
                        result.setMessage("头像设置成功");
                        return result;
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            } else {
                result.setCode(ErrorUtil.ERR_UPLOAD_PHOTO);
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_UPLOAD_PHOTO);
            return result;
        }
        return result;
    }

    @Override
    public Result obtainPhoto(String username, String size) {
        Result result = new APIResultSupport(false);
        try {
            Account account= commonManager.queryAccountByPassportId(username);
            //获取size对应的appId
            if(!Strings.isNullOrEmpty(size)){
                String clientId=photoUtils.getAppIdBySize(size);

                if(account!=null){
                    //随机获取cdn域名
                    String cdnUrl=photoUtils.getCdnURL();
                    String photoURL =String.format(account.getImage(),cdnUrl, clientId);
                    if(!Strings.isNullOrEmpty(photoURL)){
                        result.setSuccess(true);
                        result.setDefaultModel(size,photoURL);
                        return result;
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            }else{

                if(account!=null){
                    //为空返回全部
                    Map<String,String> mapAppId=photoUtils.getAllAppId();
                    if(MapUtils.isNotEmpty(mapAppId)){

                        result.setSuccess(true);
                        Set<Map.Entry<String, String>> set = mapAppId.entrySet();
                        for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
                            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                            String clientId=photoUtils.getAppIdBySize(entry.getKey());
                            //随机获取cdn域名
                            String cdnUrl=photoUtils.getCdnURL();
                            String photoURL =String.format(account.getImage(),cdnUrl, clientId);
                            result.setDefaultModel(entry.getKey(),photoURL);
                        }
                        return result;
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result.setCode(ErrorUtil.ERR_OBTAIN_PHOTO);
            return result;
        }
        return result;
    }

    @Override
    public Result checkNickName(CheckNickNameParams params) {

        UpdateUserUniqnameApiParams updateUserUniqnameApiParams=buildUpdateUserUniqnameApiParams(params);
        // 调用内部接口
        Result result = proxyUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);
        //先检查sohu昵称是否存在，然后检查sogou昵称
        if(result.isSuccess()){
            result = sgUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);
        }

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
            updateUserInfoApiParams.setUniqname(infoParams.getNickname());
            updateUserInfoApiParams.setUsername(infoParams.getFullname());

            updateUserInfoApiParams.setProvince(infoParams.getProvince());

            updateUserInfoApiParams.setCity(infoParams.getCity());
            updateUserInfoApiParams.setPersonalId(infoParams.getPersonalid());
            updateUserInfoApiParams.setModifyip(ip);
            result = proxyUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
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
