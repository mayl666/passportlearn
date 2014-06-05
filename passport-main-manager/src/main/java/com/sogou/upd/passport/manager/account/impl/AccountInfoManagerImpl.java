package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DBRedisUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.CheckNickNameParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
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

    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Autowired
    private PCAccountManager pcAccountManager;


    /**
     * 上传头像信息
     * <p/>
     * 非第三方账号迁移，第三方账号、非第三方账号 上传头像均到 account 32张表，走统一流程
     *
     * @param byteArr    需要上传图片流
     * @param passportId 用户ID
     * @param type       上传类别  0:本地图片上传 1:网络URL图片上传
     * @param ip         用户操作IP
     * @return
     */
    public Result uploadImg(byte[] byteArr, String passportId, String type, String ip) {
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

                //获取用户账号域类型
                AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
                //account 判空处理
                if (account != null) {
                    boolean updateAvatarSuccess = accountService.updateAvatar(account, imgURL);
                    if (!updateAvatarSuccess) {
                        result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
                        return result;
                    }
                } else if (domain == AccountDomainEnum.SOHU) {
                    //如果是搜狐矩阵账号、则初始化至account表
                    Account initAccount = new Account();
                    initAccount.setPassportId(passportId);
                    initAccount.setPasswordtype(PasswordTypeEnum.NOPASSWORD.getValue());
                    initAccount.setAccountType(AccountTypeEnum.SOHU.getValue());
                    initAccount.setFlag(AccountStatusEnum.REGULAR.getValue());
                    initAccount.setAvatar(imgURL);
                    initAccount.setRegIp(ip);
                    initAccount.setRegTime(new Date());

                    boolean initSuccess = accountService.initAccount(initAccount);
                    if (!initSuccess) {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                        return result;
                    }
                } else {
                    //账号不存在
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
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
     * 非第三方账号迁移
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
        /*if (ManagerHelper.isInvokeProxyApi(infoParams.getUsername())) {
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
        }*/

        // 非第三方账号迁移完成后, 更新用户信息,开启此分支
        updateUserInfoApiParams = buildUpdateUserInfoApiParams(infoParams, ip);
        result = sgUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
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
        GetUserInfoApiparams infoApiparams = buildGetUserInfoApiparams(params);
        //TODO 非第三方账号迁移完成后，第三方账号、非第三方账号 获取用户信息走相同逻辑、开启此分支
        Result result = sgUserInfoApiManager.getUserInfo(infoApiparams);

        // 调用内部接口
        /*String passportId = params.getUsername();
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
            result = sgUserInfoApiManager.getUserInfo(infoApiparams);
        }*/

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
    public Result getUserInfoFromSGAfterDataMigration(ObtainAccountInfoParams params) {
        GetUserInfoApiparams infoApiparams = buildGetUserInfoApiparams(params);
        return sgUserInfoApiManager.getUserInfo(infoApiparams);
    }

    /**
     * 非第三方账号迁移，数据迁移完成后，统一获取用户昵称方法
     * <p/>
     * OAuth2ResourceManagerService 中 getEncodedUniqname 方法重构抽取至此
     * <p/>
     * TODO 项目中调用 OAuth2ResourceManagerService服务 getEncodedUniqname方法统一替换成getUserUniqName方法
     *
     * @param passportId
     * @param clientId
     * @return
     */
    @Override
    public String getUserUniqName(String passportId, int clientId) {
        String uniqname = null;
        try {
            //获取账号类型
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account != null && !Strings.isNullOrEmpty(account.getUniqname())) {
                uniqname = account.getUniqname();
            }
            //第三方账号
            if (domain == AccountDomainEnum.THIRD) {
                if (Strings.isNullOrEmpty(uniqname)) {
                    ConnectToken connectToken = getConnectToken(passportId, clientId);
                    if (connectToken != null) {
                        uniqname = connectToken.getConnectUniqname();
                        //判断uniqname,若为空，则调用 getAndUpdateUniqname 方法
                        if (Strings.isNullOrEmpty(uniqname)) {
                            uniqname = getAndUpdateUniqname(passportId, account, uniqname);
                        }
                    }
                }
            } else {
                //非第三方账号
                uniqname = getAndUpdateUniqname(passportId, account, uniqname);
            }

        } catch (Exception e) {
            logger.error("getUserUniqName error. passportId:" + passportId, e);
        }
        return Strings.isNullOrEmpty(uniqname) ? passportId : Coder.encode(uniqname, "UTF-8");
    }

    @Override
    public Result getUserNickNameAndAvatar(String passportId, int clientId) {
        Result result = new APIResultSupport(false);
        String large_avatar = "";
        String mid_avatar = "";
        String tiny_avatar = "";
        String uniqname = "";
        String avatarurl = "";

        //判断用户类型
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
        Account account = accountService.queryAccountByPassportId(passportId);
        if (account != null) {
            uniqname = account.getUniqname();
            avatarurl = account.getAvatar();
            if (domain == AccountDomainEnum.THIRD) {
                ConnectToken connectToken = null;
                if (Strings.isNullOrEmpty(uniqname) || Strings.isNullOrEmpty(avatarurl)) {
                    connectToken = getConnectToken(passportId, clientId);
                    if (connectToken != null) {
                        if (Strings.isNullOrEmpty(uniqname)) {
                            uniqname = connectToken.getConnectUniqname();
                        }
                        if (Strings.isNullOrEmpty(avatarurl)) {
                            large_avatar = connectToken.getAvatarLarge();
                            mid_avatar = connectToken.getAvatarMiddle();
                            tiny_avatar = connectToken.getAvatarSmall();
                        }
                    }
                } else {
                    //获取不同尺寸头像
                    Result getPhotoResult = photoUtils.obtainPhoto(avatarurl, "30,50,180");
                    large_avatar = (String) getPhotoResult.getModels().get("img_180");
                    mid_avatar = (String) getPhotoResult.getModels().get("img_50");
                    tiny_avatar = (String) getPhotoResult.getModels().get("img_30");
                }

            } else {
                //非第三方账号 用户昵称、头像、数据读取从 account_base_info 切换至 account_0~32
                Result getPhotoResult = photoUtils.obtainPhoto(avatarurl, "30,50,180");
                large_avatar = (String) getPhotoResult.getModels().get("img_180");
                mid_avatar = (String) getPhotoResult.getModels().get("img_50");
                tiny_avatar = (String) getPhotoResult.getModels().get("img_30");
                uniqname = getAndUpdateUniqname(passportId, account, uniqname);
            }
        } else {
            //若 account 为空，并且账号域类型不是"搜狐域"账号，错误码返回:账号不存在、并且返回
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            return result;
        }

        Result photoResult = photoUtils.obtainPhoto(avatarurl, "50");
        if (photoResult.isSuccess()) {
            result.setDefaultModel("avatarurl", photoResult.getModels());
        }

        result.setSuccess(true);
        //是否需要编码？TODO 此处对昵称做UTF-8编码
        result.setDefaultModel("uniqname", Coder.encode(uniqname, "UTF-8"));
        result.setDefaultModel("userid", passportId);
        result.setDefaultModel("img_30", tiny_avatar);
        result.setDefaultModel("img_50", mid_avatar);
        result.setDefaultModel("img_180", large_avatar);
        return result;
    }

    /**
     * 获取用户 ConnectToken信息
     *
     * @param userId
     * @param clientId
     * @return
     */
    private ConnectToken getConnectToken(String userId, int clientId) {
        //从connect_token中获取
        int provider = AccountTypeEnum.getAccountType(userId).getValue();
        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
        ConnectToken connectToken = null;
        if (connectConfig != null) {
            connectToken = connectTokenService.queryConnectToken(userId, provider, connectConfig.getAppKey());
        }
        return connectToken;
    }

    /**
     * 从浏览器论坛取昵称
     *
     * @param passportId
     * @param account
     * @param uniqname
     * @return
     */
    private String getAndUpdateUniqname(String passportId, Account account, String uniqname) {
        if (!isValidUniqname(passportId, uniqname)) {
            //从论坛获取昵称
            uniqname = pcAccountManager.getBrowserBbsUniqname(passportId);
            if (isValidUniqname(passportId, uniqname)) {
                if (account != null) {
                    //更新用户昵称信息到account表
                    accountService.updateUniqName(account, uniqname);
                }
            }
        }
        if (!isValidUniqname(passportId, uniqname)) {
            uniqname = defaultUniqname(passportId);
        }
        return uniqname;
    }


    private boolean isValidUniqname(String passportId, String uniqname) {
        if (Strings.isNullOrEmpty(uniqname) || uniqname.equals(passportId.substring(0, passportId.indexOf("@")))) {
            return false;
        }
        return true;
    }


    /**
     * 获取默认昵称
     *
     * @param passportId
     * @return
     */
    private String defaultUniqname(String passportId) {
        if (AccountDomainEnum.THIRD == AccountDomainEnum.getAccountDomain(passportId)) {
            return "搜狗用户";
        }
        return passportId.substring(0, passportId.indexOf("@"));
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
