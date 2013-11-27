package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
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
    private AccountInfoManager accountInfoManager;

    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {

        Result result = new APIResultSupport(false);
        String nickname=null;
        try {
            nickname=new String(updateUserUniqnameApiParams.getUniqname());

            String passportId= accountService.checkUniqName(nickname);
             if(!Strings.isNullOrEmpty(passportId)){
                result.setCode(ErrorUtil.ERR_CODE_UNIQNAME_ALREADY_EXISTS);
                result.setDefaultModel("userid",passportId);
                return result;
            } else {
                result.setSuccess(true);
                result.setMessage("昵称未被占用,可以使用");
                return result;
            }
        }catch (Exception e) {
            logger.error("checkUniqName Fail,nickname:"+nickname, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }
}
