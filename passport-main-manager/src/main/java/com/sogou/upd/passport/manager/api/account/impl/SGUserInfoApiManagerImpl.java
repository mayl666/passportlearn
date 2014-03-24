package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
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
    private CommonManager commonManager;
    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;

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
                    //调用获取头像、昵称接口，拼接返回的result map
                    result = oAuth2ResourceManager.getUniqNameAndAvatar(infoApiparams.getUserid(), infoApiparams.getClient_id());

                    //检查是否有绑定手机
                    if (ArrayUtils.contains(paramArray, "mobile")) {
                        Account account = accountService.queryAccountByPassportId(passportId);
                        if (account != null) {
                            result.setDefaultModel("sec_mobile", account.getMobile());
                        }
                        paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, "mobile"));
                    }

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
                //判断昵称是否存在
                if (Strings.isNullOrEmpty(accountService.checkUniqName(params.getUniqname()))) {
                    //更新昵称 Account表
                    if (accountService.updateUniqName(account, params.getUniqname())) {

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
                        info.setFullname(params.getFullname());
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
}
