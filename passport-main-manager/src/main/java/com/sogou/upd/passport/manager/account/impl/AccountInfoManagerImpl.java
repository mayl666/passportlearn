package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.vo.NickNameAndAvatarVO;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.form.AccountInfoParams;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.connect.OriginalConnectInfo;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

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
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private OperateTimesService operateTimesService;

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
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    return result;
                }
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
        UpdateUserInfoApiParams updateUserInfoApiParams = buildUpdateUserInfoApiParams(infoParams, ip);
        Result result = sgUserInfoApiManager.updateUserInfo(updateUserInfoApiParams);
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
        infoApiparams.setImagesize("30,50,180");
        Result result = sgUserInfoApiManager.getUserInfo(infoApiparams);
        return result;
    }

    /**
     * 非第三方账号迁移，数据迁移完成后，统一获取用户昵称方法
     * <p/>
     * OAuth2ResourceManagerService 中 getEncodedUniqname 方法重构抽取至此
     * <p/>
     *
     * @param passportId
     * @param clientId
     * @return
     */
    @Override
    public String getUniqName(String passportId, int clientId, boolean isEncode) {
        String uniqname = null;
        try {
            //获取账号类型
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (AccountDomainEnum.isIndivid(passportId)) {
                passportId = passportId + CommonConstant.SOGOU_SUFFIX;
            }
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account != null && !Strings.isNullOrEmpty(account.getUniqname())) {
                uniqname = account.getUniqname();
            }
            //第三方账号
            if (domain == AccountDomainEnum.THIRD) {
                if (Strings.isNullOrEmpty(uniqname)) {
                    OriginalConnectInfo connectInfo = getOriginalConnectInfo(passportId);
                    if (connectInfo != null) {
                        uniqname = connectInfo.getConnectUniqname();
                        //判断uniqname,若为空，则调用 getDefaultUniqname 方法
                        if (Strings.isNullOrEmpty(uniqname)) {
                            uniqname = getDefaultUniqname(passportId, uniqname);
                        }
                    }
                }
            } else {
                //非第三方账号
                uniqname = getDefaultUniqname(passportId, uniqname);
            }
        } catch (Exception e) {
            logger.error("getUniqName error. passportId:" + passportId, e);
        }
        if (Strings.isNullOrEmpty(uniqname)) {
            uniqname = passportId;
        } else if (isEncode) {
            uniqname = Coder.encode(uniqname, "UTF-8");
        }
        return uniqname;
    }

    @Override
    public Result getUserNickNameAndAvatar(GetUserInfoApiparams params) {
        Result result = new APIResultSupport(false);
        NickNameAndAvatarVO nameAndAvatarVO = new NickNameAndAvatarVO();
        String uniqname = "";
        String avatarurl = "";
        String gender = "0";
        String passportId = params.getUserid();
        int clientId = params.getClient_id();
        try {
            //判断用户类型
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account != null) {
                uniqname = account.getUniqname();
                avatarurl = account.getAvatar();
                //第三方
                if (domain == AccountDomainEnum.THIRD) {
                    if (Strings.isNullOrEmpty(uniqname) || Strings.isNullOrEmpty(avatarurl)) {
                        OriginalConnectInfo connectInfo = getOriginalConnectInfo(passportId);
                        if (connectInfo != null) {
                            if (Strings.isNullOrEmpty(uniqname)) {
                                uniqname = connectInfo.getConnectUniqname();
                                //判断uniqname,若为空，则调用 getDefaultUniqname 方法
                                if (Strings.isNullOrEmpty(uniqname)) {
                                    uniqname = getDefaultUniqname(passportId, uniqname);
                                }
                            }
                            if (Strings.isNullOrEmpty(avatarurl)) {
                                nameAndAvatarVO.setLarge_avatar(connectInfo.getAvatarLarge());
                                nameAndAvatarVO.setMid_avatar(connectInfo.getAvatarMiddle());
                                nameAndAvatarVO.setTiny_avatar(connectInfo.getAvatarSmall());
                            } else {
                                obtainPhotoSizeUrl(nameAndAvatarVO, avatarurl);
                            }
                            //处理gender信息，默认为0
                            if (!Strings.isNullOrEmpty(connectInfo.getGender())) {
                                gender = connectInfo.getGender();
                            }
                        }
                    } else {
                        obtainPhotoSizeUrl(nameAndAvatarVO, avatarurl);
                    }
                    // 处理gender信息，默认为0
                    if (StringUtils.contains(params.getFields(), "gender")) {
                        result.setDefaultModel("gender", gender);
                    }
                } else {
                    //非第三方账号
                    uniqname = getDefaultUniqname(passportId, uniqname);
                    if (!Strings.isNullOrEmpty(avatarurl)) {
                        obtainPhotoSizeUrl(nameAndAvatarVO, avatarurl);
                    }
                }
                nameAndAvatarVO.setUniqname(uniqname);
                //参数包含 昵称
                setUniqNameAndAvatarResult(result, params.getFields(), nameAndAvatarVO, passportId);
            } else if (domain == AccountDomainEnum.SOHU) {
                setUniqNameAndAvatarResult(result, params.getFields(), nameAndAvatarVO, passportId);
            } else {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            result.getModels().put("account", account);
            result.setDefaultModel("userid", passportId);
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error("getUserNickNameAndAvatar error. passportId:" + passportId, e);
        }
        return result;
    }

    @Override
    public boolean checkNickNameExistInBlackList(final String ip, final String cookie) {
        if (operateTimesService.checkNickNameExistInBlackList(ip, cookie)) {
            //是否在白名单中
            if (!operateTimesService.checkRegInWhiteList(ip)) {
                return true;
            }
        }
        operateTimesService.incCheckNickNameExistTimes(ip, cookie);
        return false;
    }

    /*
     * 根据获取到的昵称或头像，设置result返回结果
     */
    private void setUniqNameAndAvatarResult(Result result, String fields, NickNameAndAvatarVO nameAndAvatarVO, String passportId) {
        //参数包含 昵称
        if (StringUtils.contains(fields, "uniqname")) {
            String uniqName = nameAndAvatarVO.getUniqname();
            if (Strings.isNullOrEmpty(uniqName)) {
                uniqName = defaultUniqname(passportId);
            }
            result.setDefaultModel("uniqname", uniqName);
        }
        //参数包含 头像
        if (StringUtils.contains(fields, "avatarurl")) {
            result.setDefaultModel("img_30", nameAndAvatarVO.getTiny_avatar());
            result.setDefaultModel("img_50", nameAndAvatarVO.getMid_avatar());
            result.setDefaultModel("img_180", nameAndAvatarVO.getLarge_avatar());
            result.setDefaultModel("avatarurl", nameAndAvatarVO.getMid_avatar());
        }
    }

    /*
     * 根据基础url获取三种尺寸的头像url
     */
    private void obtainPhotoSizeUrl(NickNameAndAvatarVO nameAndAvatarVO, String avatarurl) {
        Result getPhotoResult = photoUtils.obtainPhoto(avatarurl, "30,50,180");
        String large_avatar = (String) getPhotoResult.getModels().get("img_180");
        String mid_avatar = (String) getPhotoResult.getModels().get("img_50");
        String tiny_avatar = (String) getPhotoResult.getModels().get("img_30");
        nameAndAvatarVO.setLarge_avatar(large_avatar);
        nameAndAvatarVO.setMid_avatar(mid_avatar);
        nameAndAvatarVO.setTiny_avatar(tiny_avatar);
    }

    /**
     * 获取第三方用户原始信息
     */
    private OriginalConnectInfo getOriginalConnectInfo(String userId) {
        //
        int provider = AccountTypeEnum.getAccountType(userId).getValue();
        OriginalConnectInfo connectInfo = connectTokenService.queryOriginalConnectInfo(userId, provider);

        return connectInfo;
    }

    /**
     * 从浏览器论坛取昵称
     *
     * @param passportId
     * @param uniqname
     * @return
     */
    private String getDefaultUniqname(String passportId, String uniqname) {
        if (Strings.isNullOrEmpty(uniqname) || uniqname.equals(passportId.substring(0, passportId.indexOf("@")))) {
            uniqname = defaultUniqname(passportId);
        }
        return uniqname;
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
        if (passportId.contains("@")) {
            return passportId.substring(0, passportId.indexOf("@"));
        } else {
            return passportId;
        }
    }


    private GetUserInfoApiparams buildGetUserInfoApiparams(ObtainAccountInfoParams params) {
        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        infoApiparams.setFields(params.getFields());
        infoApiparams.setUserid(params.getUsername());
        if (!Strings.isNullOrEmpty(params.getImagesize())) {
            infoApiparams.setImagesize(params.getImagesize());
        }
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
            updateUserInfoApiParams.setPersonalid(infoParams.getPersonalid());
            updateUserInfoApiParams.setModifyip(ip);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return updateUserInfoApiParams;
    }
}
