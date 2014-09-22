package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.ResetPasswordBySecQuesApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-7 Time: 下午8:24 To change this template use
 * File | Settings | File Templates.
 */
@Component("sgSecureApiManager")
public class SGSecureApiManagerImpl implements SecureApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGSecureApiManagerImpl.class);
    private static final String PREFIX_UPDATE_PWD_SEND_MESSAGE = "您手机绑定的搜狗账号：";
    private static final String SUFFIX_UPDATE_PWD_SEND_MESSAGE = "密码被修改，请注意账号安全!";

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private OperateTimesService operateTimesService;

    @Override
    public Result updatePwd(String passportId, int clientId, String oldPwd, String newPwd, String modifyIp) {
        Result result = accountService.verifyUserPwdVaild(passportId, oldPwd, true);
        if (!result.isSuccess()) {
            operateTimesService.incLimitCheckPwdFail(passportId, clientId, AccountModuleEnum.RESETPWD);
            return result;
        }
        Account account = (Account) result.getDefaultModel();
        result.setModels(Maps.newHashMap());
        if (!accountService.resetPassword(account, newPwd, true)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public Result updateQues(UpdateQuesApiParams updateQuesApiParams) throws ServiceException {
        String userId = updateQuesApiParams.getUserid();
        String password = updateQuesApiParams.getPassword();
        String newQues = updateQuesApiParams.getNewquestion();
        String newAnswer = updateQuesApiParams.getNewanswer();
        int clientId = updateQuesApiParams.getClient_id();
        Result result = new APIResultSupport(false);
        try {
            Result authUserResult = accountService.verifyUserPwdVaild(userId, password, true);
            authUserResult.setDefaultModel(null);
            if (!authUserResult.isSuccess()) {
                operateTimesService.incLimitCheckPwdFail(userId, clientId, AccountModuleEnum.SECURE);
                return authUserResult;
            }
            newAnswer = DigestUtils.md5Hex(newAnswer.getBytes(CommonConstant.DEFAULT_CHARSET));
            AccountInfo accountInfo = accountInfoService.modifyQuesByPassportId(userId, newQues, newAnswer);
            if (accountInfo == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDQUES_FAILED);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("操作成功");
            return result;
        } catch (Exception e) {
            logger.error("Update Question fail! passportId:" + userId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result getUserSecureInfo(GetSecureInfoApiParams getSecureInfoApiParams) {
        String userId = getSecureInfoApiParams.getUserid();

        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryNormalAccount(userId);
            if (account == null) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            Map<String, String> map = Maps.newHashMap();
            String mobile = account.getMobile();
            map.put("sec_mobile", mobile);
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(userId);
            if (accountInfo != null) {
                String emailBind = accountInfo.getEmail();
                String question = accountInfo.getQuestion();
                map.put("sec_email", emailBind);
                map.put("sec_ques", question);
            }

            result.setSuccess(true);
            result.setMessage("查询成功");
            result.setModels(map);
            return result;
        } catch (ServiceException e) {
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result resetPasswordByQues(ResetPasswordBySecQuesApiParams resetPasswordBySecQuesApiParams) {
        String userId = resetPasswordBySecQuesApiParams.getUserid();
        int clientId = resetPasswordBySecQuesApiParams.getClient_id();
        String password = resetPasswordBySecQuesApiParams.getNewpassword();
        String answer = resetPasswordBySecQuesApiParams.getAnswer();
        String modifyIp = resetPasswordBySecQuesApiParams.getModifyip();

        Result result = new APIResultSupport(false);

        // TODO:放到之后再写
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
