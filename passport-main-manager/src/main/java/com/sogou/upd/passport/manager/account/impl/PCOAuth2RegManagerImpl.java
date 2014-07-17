package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.PCOAuth2RegManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.service.account.SnamePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-12
 * Time: 下午7:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PCOAuth2RegManagerImpl implements PCOAuth2RegManager {
    public static final Logger logger = LoggerFactory.getLogger(PCOAuth2RegManagerImpl.class);
    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private BindApiManager sgBindApiManager;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;
    @Autowired
    SnamePassportMappingService snamePassportMappingService;

    @Override
    public Result isPcAccountNotExists(String username, boolean type) {
        Result result = new APIResultSupport(false);
        String sohuPassportId = snamePassportMappingService.queryPassportIdBySnameOrPhone(username);
        if (!Strings.isNullOrEmpty(sohuPassportId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
            return result;
        }

        //如果sohu+库里没有，说明用户名肯定不会与老用户重复,再验证sohu库里有没有该用户
        if (type) {
            //手机号判断绑定账户
            BaseMoblieApiParams params = new BaseMoblieApiParams();
            params.setMobile(username);
            //TODO 目前及搜狗账号迁移完成，手机注册都需要查sohu库；全部账号迁移完成后，手机注册查sogou库，不需要查sohu库了
            if (ManagerHelper.isInvokeProxyApi(username)) {
                result = proxyBindApiManager.getPassportIdByMobile(params);
            } else {
                result = sgBindApiManager.getPassportIdByMobile(params);
            }
            if (result.isSuccess()) {
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                return result;
            }
        } else {
            //个性账号注册
            username = username + "@sogou.com";
            CheckUserApiParams checkUserApiParams = buildProxyApiParams(username);
            //TODO 目前，查sohu库，搜狗账号迁移完成后，查sogou库
            if (ManagerHelper.isInvokeProxyApi(username)) {
                result = proxyRegisterApiManager.checkUser(checkUserApiParams);
            } else {
                result = sgRegisterApiManager.checkUser(checkUserApiParams);
            }
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
                return result;
            }
        }
        result.setSuccess(true);
        result.setMessage("账号可以注册");
        return result;
    }

    private CheckUserApiParams buildProxyApiParams(String username) {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid(username);
        return checkUserApiParams;
    }
}
