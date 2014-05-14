package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 搜狗用户个人信息
 * User: mayan
 * Date: 13-8-8
 * Time: 下午9:50
 */
@Component("sgUserInfoApiManager")
public class SGUserInfoApiManagerImpl extends BaseProxyManager implements UserInfoApiManager {

    private static Logger logger = LoggerFactory.getLogger(SGUserInfoApiManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private PCAccountManager pcAccountManager;


    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;


    /**
     * 获取用户信息
     * <p/>
     * 数据迁移前（全量数据+增量数据完成导入前）:非第三方账号用户昵称、头像信息 读取account_base_info表，用户其他信息通过调用搜狐api获取
     * <p/>
     * 目标:数据迁移后（全量数据+增量数据完成导入后）:非第三方账号用户昵称、头像信息 读取account_0~32表，用户其他信息读取account_info_0~32表
     *
     * @param infoApiparams
     * @return
     */
    @Override
    public Result getUserInfo(GetUserInfoApiparams infoApiparams) {
        Result result = new APIResultSupport(false);
        String passportId = infoApiparams.getUserid();
        try {
            String params = infoApiparams.getFields();
            if (!Strings.isNullOrEmpty(params)) {
                //替换sogou相关字段

                params = replaceParam(params);
                String[] paramArray = params.split(",");

                if (ArrayUtils.isNotEmpty(paramArray)) {

                    //调用 获取昵称接口 拼接返回的result map 原调用方法
//                    result = oAuth2ResourceManager.getUserInfo(infoApiparams.getUserid(), infoApiparams.getClient_id());

                    Account account = accountService.queryAccountByPassportId(passportId);
                    if (account != null) {
                        //构建用户昵称、头像信息
                        result = buildUserNickNameAndAvatar(result, account, infoApiparams.getClient_id());
                        //检查是否有绑定手机
                        if (ArrayUtils.contains(paramArray, "mobile")) {
                            if (account != null) {
                                result.setDefaultModel("sec_mobile", account.getMobile());
                            }
                            paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, "mobile"));
                        }
                    }

                    //查询用户其他信息、查询account_info_0~32
                    //查询其他的个人信息 参数匹配
                    AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
                    if (accountInfo != null) {
                        if (ArrayUtils.isNotEmpty(paramArray)) {
                            result.setSuccess(true);
                            for (int i = 0; i < paramArray.length; i++) {
                                try {
                                    if (!"birthday".equals(paramArray[i])) {
                                        if ("email".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("sec_email", value);
                                            continue;
                                        }
                                        if ("question".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("sec_ques", value);
                                            continue;
                                        }
                                        if ("fullname".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("fullname", value);
                                            continue;
                                        }
                                        if ("sex".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("sex", value);
                                            continue;
                                        }
                                        //TODO 此处存在异常，有paramArray[i] 不存在于 accountInfo的情况
                                        String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                        result.setDefaultModel(paramArray[i], value);
                                    } else {
                                        Date birthday = accountInfo.getBirthday();
                                        result.setDefaultModel(paramArray[i], new SimpleDateFormat("yyyy-MM-dd").format(birthday));
                                    }
                                } catch (Exception e) {
                                    paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, paramArray[i]));
                                }
                            }
                        }
                    }
                    if (!result.getModels().isEmpty()) {
                        result.setSuccess(true);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getUserInfo Fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }

        return result;
    }



    /*@Override
    public Result getUserInfo(GetUserInfoApiparams infoApiparams) {
        Result result = new APIResultSupport(false);
        String passportId = infoApiparams.getUserid();
        try {
            String params = infoApiparams.getFields();
            if (!Strings.isNullOrEmpty(params)) {
                //替换sogou相关字段

                params = replaceParam(params);

                String[] paramArray = params.split(",");

                if (ArrayUtils.isNotEmpty(paramArray)) {

                    //调用 获取昵称接口 拼接返回的result map
                    result = oAuth2ResourceManager.getUserInfo(infoApiparams.getUserid(), infoApiparams.getClient_id());

                    //检查是否有绑定手机
                    if (ArrayUtils.contains(paramArray, "mobile")) {
                        Account account = accountService.queryAccountByPassportId(passportId);
                        if (account != null) {
                            result.setDefaultModel("sec_mobile", account.getMobile());
                        }
                        paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, "mobile"));
                    }

                    //查询用户其他信息、查询account_info_0~32
                    //查询其他的个人信息 参数匹配
                    AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
                    if (accountInfo != null) {
                        if (ArrayUtils.isNotEmpty(paramArray)) {
                            result.setSuccess(true);
                            for (int i = 0; i < paramArray.length; i++) {
                                try {
                                    if (!"birthday".equals(paramArray[i])) {
                                        if ("email".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("sec_email", value);
                                            continue;
                                        }
                                        if ("question".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("sec_ques", value);
                                            continue;
                                        }
                                        if ("fullname".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("fullname", value);
                                            continue;
                                        }
                                        if ("sex".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("sex", value);
                                            continue;
                                        }
                                        //TODO 此处存在异常，有paramArray[i] 不存在于 accountInfo的情况
                                        String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                        result.setDefaultModel(paramArray[i], value);
                                    } else {
                                        Date birthday = accountInfo.getBirthday();
                                        result.setDefaultModel(paramArray[i], new SimpleDateFormat("yyyy-MM-dd").format(birthday));
                                    }
                                } catch (Exception e) {
                                    paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, paramArray[i]));
                                }
                            }
                        }
                    }
                    if (!result.getModels().isEmpty()) {
                        result.setSuccess(true);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getUserInfo Fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }

        return result;
    }
*/


    private String replaceParam(String param) {
        //sec_mobile, sec_email, sec_ques,  username
        if (param.contains("username")) {
            //真实姓名
            param = param.replaceAll("username", "fullname");
        }
        if (param.contains("sec_mobile")) {
            //绑定手机号
            param = param.replaceAll("sec_mobile", "mobile");
        }
        if (param.contains("sec_email")) {
            //绑定手机号
            param = param.replaceAll("sec_email", "email");
        }
        if (param.contains("sec_ques")) {
            //绑定手机号
            param = param.replaceAll("sec_ques", "question");
        }
        return param;
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryAccountByPassportId(params.getUserid());
            if (account != null) {
                //更新用户除“昵称”外的其他信息
                AccountInfo info = new AccountInfo();
                info.setPassportId(params.getUserid());

                String[] birthday = !Strings.isNullOrEmpty(params.getBirthday()) ? params.getBirthday().split("-") : null;
                Calendar calendar = Calendar.getInstance();
                if (birthday != null) {
                    calendar.set(Calendar.YEAR, Integer.valueOf(birthday[0]));
                    calendar.set(Calendar.MONTH, Integer.valueOf(birthday[1]) - 1);
                    calendar.set(Calendar.DATE, Integer.valueOf(birthday[2]));
                }

                info.setBirthday(calendar.getTime());
                info.setGender(params.getGender());
                info.setProvince(params.getProvince());
                info.setCity(params.getCity());
                info.setFullname(params.getUsername());
                info.setPersonalid(params.getPersonalId());
                info.setModifyip(params.getModifyip());
                info.setUpdateTime(new Date());

                //更新用户信息AccountInfo
                boolean updateResult = accountInfoService.updateAccountInfo(info);
                if (updateResult) {
                    result.setSuccess(true);
                    result.setMessage("修改个人资料成功");
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_UPDATE_USERINFO);
                }

                //判断昵称是否存在
                String checkExist = accountService.checkUniqName(params.getUniqname());
                if (Strings.isNullOrEmpty(checkExist)) {
                    //更新昵称 Account表 u_p_m映射表
                    boolean accountUpdateResult = accountService.updateUniqName(account, params.getUniqname());
                    if (accountUpdateResult) {
                        result.setSuccess(true);
                        result.setMessage("修改个人资料成功");
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_UPDATE_USERINFO);
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_UNIQNAME_ALREADY_EXISTS);
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }

        } catch (Exception e) {
            logger.error("updateUserInfo Fail,passportId:" + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {

        Result result = new APIResultSupport(false);
        String nickname = null;
        try {
            nickname = new String(updateUserUniqnameApiParams.getUniqname().getBytes("ISO8859-1"), "UTF-8");

            String passportId = accountService.checkUniqName(nickname);
            if (!Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_UNIQNAME_ALREADY_EXISTS);
                result.setDefaultModel("userid", passportId);
                return result;
            } else {
                result.setSuccess(true);
                result.setMessage("昵称未被占用,可以使用");
                return result;
            }
        } catch (Exception e) {
            logger.error("checkUniqName Fail,nickname:" + nickname, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }


    /**
     * 构建用户"昵称、头像"信息
     *
     * @param userInfoResult
     * @param clientId
     * @return
     */
    private Result buildUserNickNameAndAvatar(Result userInfoResult, Account account, int clientId) {

        //TODO 非第三方账号数据迁移 、业务逻辑方法重构

        Result result = userInfoResult;

        String avatarurl = "";
        String large_avatar = "";
        String mid_avatar = "";
        String tiny_avatar = "";
        String passportId = account.getPassportId();
        String uniqname = defaultUniqname(passportId);

        if (account != null) {
            uniqname = account.getUniqname();
            avatarurl = account.getAvatar();
        }

        //判断用户类型
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
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
            result.setDefaultModel("userid", passportId);
        } else {
            //非第三方账号 用户昵称、头像、数据读取从 account_base_info 切换至 account_0~32
            Result getPhotoResult = photoUtils.obtainPhoto(avatarurl, "30,50,180");
            large_avatar = (String) getPhotoResult.getModels().get("img_180");
            mid_avatar = (String) getPhotoResult.getModels().get("img_50");
            tiny_avatar = (String) getPhotoResult.getModels().get("img_30");
            uniqname = getAndUpdateUniqname(passportId, account, uniqname);
        }

        //是否需要编码？TODO
        result.setDefaultModel("uniqname", uniqname);

        result.setDefaultModel("img_30", tiny_avatar);
        result.setDefaultModel("img_50", mid_avatar);
        result.setDefaultModel("img_180", large_avatar);
        return result;
    }


    /**
     * 获取用户 默认昵称
     *
     * @param passportId
     * @return
     */
    public String defaultUniqname(String passportId) {
        if (AccountDomainEnum.THIRD == AccountDomainEnum.getAccountDomain(passportId)) {
            return "搜狗用户";
        }
        return passportId.substring(0, passportId.indexOf("@"));
    }

    /**
     * 获取用户 ConnectToken
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
                accountService.updateUniqName(account, uniqname);
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


}
