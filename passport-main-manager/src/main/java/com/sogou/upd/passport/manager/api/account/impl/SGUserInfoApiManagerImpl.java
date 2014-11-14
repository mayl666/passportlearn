package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.UniqNamePassportMappingService;
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
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private UniqNamePassportMappingService uniqNamePassportMappingService;


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
        String passportIdLog = infoApiparams.getUserid();
        try {
            String passportId = commonManager.getPassportIdByUsername(infoApiparams.getUserid());
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND);
                return result;
            }
            infoApiparams.setUserid(passportId);
            passportIdLog = passportId;
            String fields = infoApiparams.getFields();
            if (!Strings.isNullOrEmpty(fields)) {
                //替换sogou相关字段
                String params = replaceParam(fields);
                String[] paramArray = StringUtils.split(params, ",");
                if (ArrayUtils.isNotEmpty(paramArray)) {
                    //获取用户账号 域类型
                    AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
                    Result accountResult = accountInfoManager.getUserNickNameAndAvatar(infoApiparams);
                    if (accountResult.isSuccess()) {
                        Account account = null;
                        Object object = accountResult.getModels().get("account");
                        if (object != null) {
                            account = (Account) object;
                        }
                        if (account != null) {
                            //检查是否有绑定手机
                            if (ArrayUtils.contains(paramArray, "mobile") || ArrayUtils.contains(paramArray, "sec_mobile")) {
                                result.setDefaultModel("sec_mobile", Strings.isNullOrEmpty(account.getMobile()) ? "" : account.getMobile());
                                paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, "mobile"));
                            }
                            //昵称
                            if (accountResult.getModels().containsKey("uniqname")) {
                                result.setDefaultModel("uniqname", accountResult.getModels().get("uniqname"));
                                paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, "uniqname"));
                            }
                            //头像
                            if (accountResult.getModels().containsKey("avatarurl")) {
                                result.setDefaultModel("avatarurl", accountResult.getModels().get("avatarurl"));
                                paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, "avatarurl"));
                            }
                            result.setDefaultModel("userid", passportId);
                        } else if (domain == AccountDomainEnum.SOHU) {
                            //如果为"搜狐域"账号，则根据请求参数构建值为 "" 的result
                            return buildSoHuEmptyResult(result, fields, passportId);
                        } else {
                            //若 account 为空，并且账号域类型不是"搜狐域"账号，错误码返回:账号不存在、并且返回
                            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                            return result;
                        }
                    } else {
                        return accountResult;
                    }

                    if ((ArrayUtils.isNotEmpty(paramArray))) {
                        int arrayLen = paramArray.length;
                        //查询用户其他信息、查询account_info_0~32
                        AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
                        if (accountInfo != null) {
                            for (int i = 0; i < paramArray.length; i++) {
                                try {
                                    if ("birthday".equals(paramArray[i])) {
                                        Date birthday = accountInfo.getBirthday();
                                        String birthdayStr = (birthday == null) ? "" : new SimpleDateFormat("yyyy-MM-dd").format(birthday);
                                        result.setDefaultModel(paramArray[i], birthdayStr);
                                        continue;
                                    }
                                    if ("email".equals(paramArray[i]) || "sec_email".equals(paramArray[i])) {
                                        String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                        result.setDefaultModel("sec_email", value);
                                        continue;
                                    }
                                    if ("question".equals(paramArray[i]) || "sec_ques".equals(paramArray[i])) {
                                        String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                        result.setDefaultModel("sec_ques", value);
                                        continue;
                                    }
                                    //web接口传的是fullname
                                    if ("fullname".equals(paramArray[i])) {
                                        String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                        result.setDefaultModel("fullname", value);
                                        continue;
                                    }
                                    //内部接口传的是username
                                    if ("username".equals(paramArray[i])) {
                                        String value = BeanUtils.getProperty(accountInfo, "fullname");
                                        result.setDefaultModel("username", value);
                                        continue;
                                    }
                                    if ("gender".equals(paramArray[i])) {
                                        String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                        result.setDefaultModel("gender", Strings.isNullOrEmpty(value) ? 0 : value);
                                        continue;
                                    }
                                    if ("personalid".equals(paramArray[i])) {
                                        String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                        result.setDefaultModel("personalid", value);
                                        continue;
                                    }
                                    //TODO 此处存在异常，有paramArray[i] 不存在于 accountInfo的情况
                                    String value = BeanUtils.getProperty(accountInfo, paramArray[i]);
                                    result.setDefaultModel(paramArray[i], value);
                                } catch (Exception e) {
                                    paramArray = ArrayUtils.remove(paramArray, ArrayUtils.indexOf(paramArray, paramArray[i]));
                                    i--;
                                }
                            }
                        }
                    } else {
                        result.setSuccess(true);
                        result.setMessage("操作成功");
                        return result;
                    }
                    if (!result.getModels().isEmpty()) {
                        result.setSuccess(true);
                        result.setMessage("操作成功");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getUserInfo Fail,passportId:" + passportIdLog, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }

        return result;
    }

    /**
     * 若获取Account为空，并且Account域为搜狐域，构建基于请求参数的、值为""、的Result
     *
     * @param result
     * @param params     原始参数
     * @param passportId 用于初始化搜狐矩阵账号默认昵称
     * @return
     */
    private Result buildSoHuEmptyResult(Result result, String params, String passportId) {
        String[] paramArray = null;
        if (!Strings.isNullOrEmpty(params)) {
            paramArray = StringUtils.split(params, ",");
        }
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
        result.setDefaultModel("userid", passportId);
        result.setSuccess(true);
        return result;
    }

    private String replaceParam(String param) {
        //sec_mobile, sec_email, sec_ques,  username
//        if (param.contains("username")) {
//            //真实姓名
//            param = param.replaceAll("username", "fullname");
//        }
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
        String passportIdLog = params.getUserid();
        try {
            String passportId = commonManager.getPassportIdByUsername(params.getUserid());
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND);
                return result;
            }
            params.setUserid(passportId);
            passportIdLog = passportId;
            //获取用户账号类型
            AccountDomainEnum accountDomain = AccountDomainEnum.getAccountDomain(passportId);
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account != null) {
                //更新用户非昵称信息，性别、所在地、生日、真实姓名、身份证
                result = updateAccountInfo(params);
                if (!Strings.isNullOrEmpty(params.getUniqname()) && !params.getUniqname().equalsIgnoreCase(account.getUniqname())) {
                    //更新用户昵称信息
                    result = updateAccountNickName(account, params.getUniqname());
                }
            } else if (accountDomain == AccountDomainEnum.SOHU) {
                //如果是搜狐矩阵账号，则插入到account表一条无密码的记录,插入成功、涉及到用户信息更改的话，在继续执行更新操作
                Account insertSoHuAccount = accountService.initialAccount(params.getUserid(), null, false, params.getModifyip(), AccountTypeEnum.SOHU.getValue());
                if (insertSoHuAccount == null) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                    return result;
                } else {
                    //更新用户非昵称信息，性别、所在地、生日、真实姓名、身份证
                    result = updateAccountInfo(params);
                    if (!Strings.isNullOrEmpty(params.getUniqname())) {
                        //更新用户昵称信息
                        result = updateAccountNickName(insertSoHuAccount, params.getUniqname());
                    }
                }
            } else {
                //记录Log 跟踪数据同步延时情况
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
        } catch (Exception e) {
            logger.error("updateUserInfo Fail,passportId:" + passportIdLog, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {

        Result result = new APIResultSupport(false);
        String nickname = updateUserUniqnameApiParams.getUniqname();
        try {
            //前端在个人资料页面填写昵称后，鼠标离开即检查昵称唯一性，这里不能编码，因为保存时没有编码
//            nickname = new String(updateUserUniqnameApiParams.getUniqname().getBytes("ISO8859-1"), "UTF-8");

            String passportId = uniqNamePassportMappingService.checkUniqName(nickname);
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
     * @param account
     * @param nickName
     * @return
     */
    private Result updateAccountNickName(Account account, String nickName) {
        Result result = new APIResultSupport(false);
        try {
            //更新昵称 Account表 u_p_m映射表
            boolean accountUpdateResult = accountService.updateUniqName(account, nickName);
            if (accountUpdateResult) {
                result.setSuccess(true);
                result.setMessage("修改成功");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_UPDATE_USERINFO);
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
     * @return
     */
    private Result updateAccountInfo(UpdateUserInfoApiParams params) {

        Result result = new APIResultSupport(false);
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
                if (!Strings.isNullOrEmpty(params.getBirthday()) && !params.getBirthday().equalsIgnoreCase(String.valueOf(accountInfo.getBirthday()))) {
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
                //web页面修改真实姓名传的username，内部接口传的fullname
                if (!Strings.isNullOrEmpty(params.getUsername()) && !params.getUsername().equalsIgnoreCase(accountInfo.getFullname())) {
                    accountInfo.setFullname(params.getUsername());
                } else if (!Strings.isNullOrEmpty(params.getFullname())) {
                    accountInfo.setFullname(params.getFullname());
                }

            }
            accountInfo.setModifyip(params.getModifyip());

            //更新用户信息AccountInfo
            boolean updateResult = accountInfoService.updateAccountInfo(accountInfo);
            if (updateResult) {
                result.setSuccess(true);
                result.setMessage("修改成功");
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

