package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
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
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.account.vo.NickNameAndAvatarVO;
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
        UpdateUserInfoApiParams updateUserInfoApiParams = null;
        updateUserInfoApiParams = buildUpdateUserInfoApiParams(infoParams, ip);
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
        Result result = sgUserInfoApiManager.getUserInfo(infoApiparams);
        return result;
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
    public Result getUserNickNameAndAvatar(GetUserInfoApiparams params) {
        Result result = new APIResultSupport(false);
        NickNameAndAvatarVO nameAndAvatarVO = new NickNameAndAvatarVO();
        String uniqname = "";
        String avatarurl = "";

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
                        ConnectToken connectToken = getConnectToken(passportId, clientId);
                        if (connectToken != null) {
                            if (Strings.isNullOrEmpty(uniqname)) {
                                uniqname = connectToken.getConnectUniqname();
                                //判断uniqname,若为空，则调用 getAndUpdateUniqname 方法
                                if (Strings.isNullOrEmpty(uniqname)) {
                                    uniqname = getAndUpdateUniqname(passportId, account, uniqname);
                                }
                            }
                            if (Strings.isNullOrEmpty(avatarurl)) {
                                nameAndAvatarVO.setLarge_avatar(connectToken.getAvatarLarge());
                                nameAndAvatarVO.setMid_avatar(connectToken.getAvatarMiddle());
                                nameAndAvatarVO.setTiny_avatar(connectToken.getAvatarSmall());
                            } else {
                                obtainPhotoSizeUrl(nameAndAvatarVO, avatarurl);
                            }
                        }
                    } else {
                        obtainPhotoSizeUrl(nameAndAvatarVO, avatarurl);
                    }
                } else {
                    //非第三方账号
                    uniqname = getAndUpdateUniqname(passportId, account, uniqname);
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
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
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
                    //从浏览器论坛获取昵称、更新到account以及u_p_m、先check u_p_m昵称唯一性
                    boolean updateFlag = accountService.updateUniqName(account, uniqname);
                    if (!updateFlag) {
                        uniqname = defaultUniqname(passportId);
                    }
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
            updateUserInfoApiParams.setPersonalid(infoParams.getPersonalid());
            updateUserInfoApiParams.setModifyip(ip);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return updateUserInfoApiParams;
    }

}
