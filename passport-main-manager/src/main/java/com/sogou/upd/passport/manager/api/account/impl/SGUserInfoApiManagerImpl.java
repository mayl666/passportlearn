package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
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
    private AccountInfoManager accountInfoManager;


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
                String[] paramArray = StringUtils.split(params, ",");
                if (ArrayUtils.isNotEmpty(paramArray)) {
                    //获取用户账号 域类型
                    AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
                    Account account = accountService.queryAccountByPassportId(passportId);
                    if (account != null) {
                        //构建用户昵称、头像信息
                        result = accountInfoManager.getUserNickNameAndAvatar(passportId, infoApiparams.getClient_id());

                        //检查是否有绑定手机
                        if (ArrayUtils.contains(paramArray, "mobile")) {
                            result.setDefaultModel("sec_mobile", account.getMobile());
                            paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, "mobile"));
                        }
                    } else if (domain == AccountDomainEnum.SOHU) {
                        //如果为"搜狐域"账号，则根据请求参数构建值为 "" 的result
                        return buildSoHuEmptyResult(result, paramArray, passportId);
                    } else {
                        //若 account 为空，并且账号域类型不是"搜狐域"账号，错误码返回:账号不存在、并且返回
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                        return result;
                    }

                    //查询用户其他信息、查询account_info_0~32 TODO 重构
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
                                        //TODO 暂先注释掉，sex 数据项应该没用
                                       /* if ("sex".equals(paramArray[i])) {
                                            String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                            result.setDefaultModel("sex", value);
                                            continue;
                                        }*/
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

    /**
     * 若获取Account为空，并且Account域为搜狐域，构建基于请求参数的、值为""、的Result
     *
     * @param result
     * @param paramArray
     * @param passportId 用于初始化搜狐矩阵账号默认昵称
     * @return
     */
    private Result buildSoHuEmptyResult(Result result, String[] paramArray, String passportId) {
        if (paramArray.length == 0) {
            return result;
        } else {
            for (String param : paramArray) {
                result.setDefaultModel(param, StringUtils.EMPTY);
            }
        }
        if (passportId.indexOf("@") > 0) {
            result.setDefaultModel("uniqname", StringUtils.substringBefore(passportId, "@"));
        } else {
            result.setDefaultModel("uniqname", passportId);
        }
        result.setSuccess(true);
        return result;
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
            //绑定密保邮箱
            param = param.replaceAll("sec_email", "email");
        }
        if (param.contains("sec_ques")) {
            //绑定密保问题
            param = param.replaceAll("sec_ques", "question");
        }
        return param;
    }

    /**
     * 非第三方账号迁移，更新用户信息
     * 对于搜狐矩阵账号，新增一条无密码的记录
     * <p/>
     * TODO 需要兼容内部接口方法
     *
     * @param params
     * @return
     */
    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams params) {
        Result result = new APIResultSupport(false);
        String passportId = params.getUserid();
        try {
            //获取用户账号类型
            AccountDomainEnum accountDomain = AccountDomainEnum.getAccountDomain(passportId);
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account != null) {
                //更新用户非昵称信息，性别、所在地、生日、真实姓名、身份证
                result = updateAccountInfo(result, params);
                if (!Strings.isNullOrEmpty(params.getUniqname()) && !params.getUniqname().equalsIgnoreCase(account.getUniqname())) {
                    //更新用户昵称信息
                    result = updateAccountNickName(result, account, params.getUniqname());
                }
            } else if (accountDomain == AccountDomainEnum.SOHU) {
                //如果是搜狐矩阵账号，则插入到account表一条无密码的记录,插入成功、涉及到用户信息更改的话，在继续执行更新操作
                Account insertSoHuAccount = accountService.initialAccount(params.getUserid(), null, false, params.getModifyip(), AccountTypeEnum.SOHU.getValue());
                if (insertSoHuAccount == null) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                    return result;
                } else {
                    //更新用户非昵称信息，性别、所在地、生日、真实姓名、身份证
                    result = updateAccountInfo(result, params);
                    if (!Strings.isNullOrEmpty(params.getUniqname())) {
                        //更新用户昵称信息
                        result = updateAccountNickName(result, insertSoHuAccount, params.getUniqname());
                    }
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
        } catch (Exception e) {
            logger.error("updateUserInfo Fail,passportId:" + passportId, e);
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
     * 非第三方账号迁移， 新增 更新用户昵称方法
     *
     * @param result
     * @param account
     * @param nickName
     * @return
     */
    private Result updateAccountNickName(Result result, Account account, String nickName) {
        try {
            //更新昵称
            //判断昵称是否存在
            String checkExist = accountService.checkUniqName(nickName);
            if (Strings.isNullOrEmpty(checkExist)) {
                //更新昵称 Account表 u_p_m映射表
                boolean accountUpdateResult = accountService.updateUniqName(account, nickName);
                if (accountUpdateResult) {
                    result.setSuccess(true);
                    result.setMessage("修改个人资料成功");
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_UPDATE_USERINFO);

                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_UNIQNAME_ALREADY_EXISTS);
            }

        } catch (Exception e) {
            logger.error("updateAccountNickName error. passportId:" + account.getPassportId(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }


    /**
     * 非第三方账号迁移，新增 更新用户其他信息 方法
     * <p/>
     * TODO 兼容内部接口方法
     *
     * @param result
     * @return
     */
    private Result updateAccountInfo(Result result, UpdateUserInfoApiParams params) {
        try {
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(params.getUserid());
            if (accountInfo == null) {
                accountInfo = new AccountInfo();
                accountInfo.setPassportId(params.getUserid());

                String[] birthday = !Strings.isNullOrEmpty(params.getBirthday()) ? params.getBirthday().split("-") : null;
                Calendar calendar = Calendar.getInstance();
                if (birthday != null) {
                    calendar.set(Calendar.YEAR, Integer.valueOf(birthday[0]));
                    calendar.set(Calendar.MONTH, Integer.valueOf(birthday[1]) - 1);
                    calendar.set(Calendar.DATE, Integer.valueOf(birthday[2]));
                }

                accountInfo.setBirthday(calendar.getTime());
                accountInfo.setGender(params.getGender());
                accountInfo.setProvince(params.getProvince());
                accountInfo.setCity(params.getCity());
                accountInfo.setFullname(params.getUsername());
                accountInfo.setPersonalid(params.getPersonalid());
                accountInfo.setCreateTime(new Date());
            } else {
                if (!Strings.isNullOrEmpty(params.getBirthday()) && !params.getBirthday().equalsIgnoreCase(accountInfo.getBirthday().toString())) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    accountInfo.setBirthday(format.parse(params.getBirthday()));
                }
                if (!Strings.isNullOrEmpty(params.getGender()) && !params.getGender().equalsIgnoreCase(accountInfo.getGender())) {
                    accountInfo.setGender(params.getGender());
                }
                if (!Strings.isNullOrEmpty(params.getProvince()) && !params.getProvince().equalsIgnoreCase(accountInfo.getProvince())) {
                    accountInfo.setProvince(params.getProvince());
                }
                if (!Strings.isNullOrEmpty(params.getCity()) && !params.getCity().equalsIgnoreCase(accountInfo.getCity())) {
                    accountInfo.setCity(params.getCity());
                }
                if (!Strings.isNullOrEmpty(params.getPersonalid()) && !params.getPersonalid().equalsIgnoreCase(accountInfo.getPersonalid())) {
                    accountInfo.setPersonalid(params.getPersonalid());
                }
                if (!Strings.isNullOrEmpty(params.getUsername()) && !params.getUsername().equalsIgnoreCase(accountInfo.getFullname())) {
                    accountInfo.setFullname(params.getUsername());
                }
            }
            accountInfo.setModifyip(params.getModifyip());

            //更新用户信息AccountInfo
            boolean updateResult = accountInfoService.updateAccountInfo(accountInfo);
            if (updateResult) {
                result.setSuccess(true);
                result.setMessage("修改个人资料成功");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_UPDATE_USERINFO);
            }
        } catch (Exception e) {
            logger.error("updateAccountInfo error. passportId:" + params.getUserid(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

}

