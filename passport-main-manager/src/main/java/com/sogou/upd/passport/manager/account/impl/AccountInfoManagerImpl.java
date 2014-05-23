package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DBRedisUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.CheckNickNameParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.service.account.AccountService;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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
    private DBRedisUtils dbRedisUtils;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private UserInfoApiManager shPlusUserInfoApiManager;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;

    @Autowired
    private AccountService accountService;


    /**
     * 上传头像信息
     * <p/>
     * 非第三方账号迁移，第三方账号、非第三方账号 上传头像均到 account 32张表，走统一流程
     *
     * @param byteArr    需要上传图片流
     * @param passportId 用户ID
     * @param type       上传类别  0:本地图片上传 1:网络URL图片上传
     * @return
     */
    public Result uploadImg(byte[] byteArr, String passportId, String type) {
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
            if (photoUtils.uploadImg(imgName, byteArr, null, type)) {
                String imgURL = photoUtils.accessURLTemplate(imgName);
                Account account = accountService.queryAccountByPassportId(passportId);
                boolean updateAvatarSuccess = accountService.updateAvatar(account, imgURL);
                if (!updateAvatarSuccess) {
                    result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
                    return result;
                }

                //TODO 非第三方账号迁移，注释掉之前老的流程，待上线成功后，删除掉下面老的逻辑代码

                //更新缓存记录 临时方案 暂时这里写缓存，数据迁移后以 搜狗分支为主（更新库更新缓存）
                /*GetUserInfoApiparams apiparams = new GetUserInfoApiparams();
                apiparams.setUserid(passportId);

                AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
                //第三方登录 走搜狗流程
                if (domain == AccountDomainEnum.THIRD) {
                    Account account = accountService.queryAccountByPassportId(passportId);
                    if (!accountService.updateAvatar(account, imgURL)) {
                        result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
                        return result;
                    }
                } else {
                    Result resultUserInfo = shPlusUserInfoApiManager.getUserInfo(apiparams);
                    if (resultUserInfo.isSuccess()) {
                        Object obj = resultUserInfo.getModels().get("baseInfo");
                        AccountBaseInfo baseInfo = null;
                        if (obj != null) {
                            baseInfo = (AccountBaseInfo) obj;
                            //更新数据库
                            int rows = accountBaseInfoDAO.updateAvatarByPassportId(imgURL, passportId);
                            if (rows != 0) {
                                //更新缓存
                                baseInfo.setAvatar(imgURL);
                            }
                        } else {
                            //新添加记录
                            baseInfo = new AccountBaseInfo();
                            baseInfo.setPassportId(passportId);
                            baseInfo.setAvatar(imgURL);
                            baseInfo.setUniqname("");
                            accountBaseInfoDAO.insertAccountBaseInfo(passportId, baseInfo);
                        }

                        //更新缓存
                        String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_ACCOUNT_BASE_INFO + passportId;
                        dbRedisUtils.set(cacheKey, baseInfo, 30, TimeUnit.DAYS);
                    }
                }
                */
                result.setSuccess(true);
                result.setDefaultModel("image", imgURL);
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
    public Result uploadDefaultImg(String webUrl, String clientId) {
        Result result = new APIResultSupport(false);
        try {
            //获取图片名
            String imgName = clientId + "_" + System.currentTimeMillis();
            // 上传到OP图片平台
            if (photoUtils.uploadImg(imgName, null, webUrl, "1")) {
                String imgURL = photoUtils.accessURLTemplate(imgName);
                //更新缓存记录 临时方案 暂时这里写缓存，数据迁移后以 搜狗分支为主（更新库更新缓存）
                String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_AVATARURL_MAPPING + clientId;

                dbRedisUtils.hPut(cacheKey, "sgImg", imgURL);

                result.setSuccess(true);
                result.setDefaultModel("image", imgURL);
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
            String[] sizeArry = null;
            //获取size对应的appId
            if (!Strings.isNullOrEmpty(size)) {
                //检测是否是支持的尺寸
                sizeArry = size.split(",");

                if (ArrayUtils.isNotEmpty(sizeArry)) {
                    for (int i = 0; i < sizeArry.length; i++) {
                        if (Strings.isNullOrEmpty(photoUtils.getAppIdBySize(sizeArry[i]))) {
                            result.setCode(ErrorUtil.ERR_CODE_ERROR_IMAGE_SIZE);
                            return result;
                        }
                    }
                } else {
                    //为空获取所有的尺寸
                    sizeArry = photoUtils.getAllImageSize();
                }

                if (!Strings.isNullOrEmpty(imageUrl) && ArrayUtils.isNotEmpty(sizeArry)) {
                    result.setSuccess(true);
                    for (int i = 0; i < sizeArry.length; i++) {
                        //随机获取cdn域名
                        String cdnUrl = photoUtils.getCdnURL();
                        //获取图片尺寸
                        String clientId = photoUtils.getAppIdBySize(sizeArry[i]);

                        String photoURL = String.format(imageUrl, cdnUrl, clientId);
                        if (!Strings.isNullOrEmpty(photoURL)) {
                            result.setDefaultModel("img_" + sizeArry[i], photoURL);
                        }
                    }
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
                    return result;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
            return result;
        }
        return result;
    }

    @Override
    public Result checkNickName(CheckNickNameParams params) {

        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = buildUpdateUserUniqnameApiParams(params);
        Result result = sgUserInfoApiManager.checkUniqName(updateUserUniqnameApiParams);

        return result;
    }

    /**
     * 更新用户信息
     * 非第三方账号迁移前:
     *
     * @param infoParams
     * @param ip
     * @return
     */
    @Override
    public Result updateUserInfo(AccountInfoParams infoParams, String ip) {

        Result result = new APIResultSupport(false);

        UpdateUserInfoApiParams updateUserInfoApiParams = null;
        // 调用内部接口
        if (ManagerHelper.isInvokeProxyApi(infoParams.getUsername())) {
            updateUserInfoApiParams = new UpdateUserInfoApiParams();
            updateUserInfoApiParams.setUserid(infoParams.getUsername());
            updateUserInfoApiParams.setGender(infoParams.getGender());
            updateUserInfoApiParams.setClient_id(Integer.parseInt(infoParams.getClient_id()));

            //替换sohu日期
            String birthday = !Strings.isNullOrEmpty(infoParams.getBirthday()) ? infoParams.getBirthday() : null;
            if (!Strings.isNullOrEmpty(birthday)) {
                String[] birthdayArr = birthday.split("-");
                String month = birthdayArr[1];
                if (month.startsWith("0")) {
                    month = "0" + String.valueOf(Integer.parseInt(month));
                } else {
                    month = String.valueOf(Integer.parseInt(month));
                }
                if ("010".equals(month)) {
                    month = "10";
                }
                birthday = birthdayArr[0] + "-" + month + "-" + birthdayArr[2];
            }

            updateUserInfoApiParams.setBirthday(birthday);
            updateUserInfoApiParams.setUsername(infoParams.getFullname());
            updateUserInfoApiParams.setUniqname(infoParams.getUniqname());
            updateUserInfoApiParams.setProvince(infoParams.getProvince());
            updateUserInfoApiParams.setCity(infoParams.getCity());
            updateUserInfoApiParams.setPersonalId(infoParams.getPersonalid());
            updateUserInfoApiParams.setModifyip(ip);

            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(infoParams.getUsername());
            //第三方登录 走搜狗流程
            if (domain == AccountDomainEnum.THIRD) {
                result = sgUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
            } else {
                //非第三方账号，用户更新昵称信息，更新至sogou
                result = shPlusUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);

                //非第三方账号，用户更新其他信息(非头像、昵称信息)，更新至sohu
                updateUserInfoApiParams.setUniqname(null);
                proxyUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
            }
        } else {
            //TODO 提醒 非第三方账号迁移完成后, 更新用户信息,开启此分支
            updateUserInfoApiParams = buildUpdateUserInfoApiParams(infoParams, ip);
            result = sgUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
        }
        return result;
    }

    /**
     * 获取用户信息
     * <p/>
     * 非第三方账号迁移完成后
     * 用户昵称、头像信息 读写 account_base_info 切换到 account_0~32
     * 用户其他信息 读写调用搜狐Api 切换到 读写 account_info_0~32
     *
     * @param params
     * @return
     */
    @Override
    public Result getUserInfo(ObtainAccountInfoParams params) {
        Result result = new APIResultSupport(false);
        GetUserInfoApiparams infoApiparams = buildGetUserInfoApiparams(params);
        // 调用内部接口
        String passportId = params.getUsername();
        if (ManagerHelper.isInvokeProxyApi(passportId)) {
            //第三方获取个人资料
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (domain == AccountDomainEnum.THIRD) {
                result = sgUserInfoApiManager.getUserInfo(infoApiparams);
            } else {

                result = proxyUserInfoApiManager.getUserInfo(infoApiparams);

                //其中昵称和头像是获取的account_base_info
                if (infoApiparams.getFields().contains("avatarurl") || infoApiparams.getFields().contains("uniqname")) {
                    AccountBaseInfo baseInfo = getBaseInfo(infoApiparams.getUserid());
                    //如果有sogou有存储，则用sogou存的
                    if (baseInfo != null) {
                        result.getModels().put("uniqname", baseInfo.getUniqname());
                        result.getModels().put("avatarurl", baseInfo.getAvatar());
                    }
                }
            }
        } else {
            //TODO 非第三方账号迁移完成后，第三方账号、非第三方账号走相同逻辑、开启此分支
            result = sgUserInfoApiManager.getUserInfo(infoApiparams);
        }
        return result;
    }


    /**
     * 非第三方全量数据迁移完成后，采用此方法
     * 获取用户信息，用户昵称、头像 信息读取account_0~32、用户其他信息读 account_info_0~32
     *
     * @param params
     * @return
     */
    @Override
    public Result getUserInfoVersion2(ObtainAccountInfoParams params) {
        GetUserInfoApiparams infoApiparams = buildGetUserInfoApiparams(params);
        return sgUserInfoApiManager.getUserInfo(infoApiparams);
    }


    private GetUserInfoApiparams buildGetUserInfoApiparams(ObtainAccountInfoParams params) {
        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        infoApiparams.setFields(params.getFields());
        infoApiparams.setUserid(params.getUsername());
//        infoApiparams.setClient_id(Integer.parseInt(params.getClient_id()));
        return infoApiparams;
    }

    private UpdateUserInfoApiParams buildUpdateUserInfoApiParams(AccountInfoParams infoParams, String ip) {
        UpdateUserInfoApiParams updateUserInfoApiParams = new UpdateUserInfoApiParams();
        try {
            updateUserInfoApiParams.setClient_id(Integer.parseInt(infoParams.getClient_id()));
            updateUserInfoApiParams.setUniqname(infoParams.getUniqname());
            updateUserInfoApiParams.setUserid(infoParams.getUsername());

            String[] birthday = !Strings.isNullOrEmpty(infoParams.getBirthday()) ? infoParams.getBirthday().split("-") : null;
            Calendar calendar = Calendar.getInstance();
            if (birthday != null) {
                calendar.set(Calendar.YEAR, Integer.valueOf(birthday[0]));
                calendar.set(Calendar.MONTH, Integer.valueOf(birthday[1]) - 1);
                calendar.set(Calendar.DATE, Integer.valueOf(birthday[2]));
            }

            updateUserInfoApiParams.setBirthday(infoParams.getBirthday());
            updateUserInfoApiParams.setCity(infoParams.getCity());
            updateUserInfoApiParams.setGender(infoParams.getGender());
            updateUserInfoApiParams.setProvince(infoParams.getProvince());
            updateUserInfoApiParams.setFullname(infoParams.getFullname());
            updateUserInfoApiParams.setPersonalId(infoParams.getPersonalid());
            updateUserInfoApiParams.setModifyip(ip);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return updateUserInfoApiParams;
    }

    private UpdateUserUniqnameApiParams buildUpdateUserUniqnameApiParams(CheckNickNameParams params) {
        UpdateUserUniqnameApiParams updateUserUniqnameApiParams = new UpdateUserUniqnameApiParams();
        updateUserUniqnameApiParams.setUniqname(params.getNickname());
        updateUserUniqnameApiParams.setClient_id(Integer.parseInt(params.getClient_id()));
        return updateUserUniqnameApiParams;
    }

    /**
     * 用户 昵称、头像 信息读 account_base_info
     *
     * @param passportId
     * @return
     */
    private AccountBaseInfo getBaseInfo(String passportId) {
        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        infoApiparams.setUserid(passportId);
        Result getUserInfoResult = shPlusUserInfoApiManager.getUserInfo(infoApiparams);
        if (getUserInfoResult.isSuccess()) {
            Object obj = getUserInfoResult.getModels().get("baseInfo");
            AccountBaseInfo accountBaseInfo = null;
            if (obj != null) {
                accountBaseInfo = (AccountBaseInfo) obj;
            }
            return accountBaseInfo;
        }
        return null;
    }

}
