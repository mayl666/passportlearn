package com.sogou.upd.passport.manager.connect.impl;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.connect.OAuthAuthBindManager;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.dataobject.PassportIDInfoDO;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectRelationService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-2
 * Time: 下午5:33
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OAuthAuthBindManagerImpl implements OAuthAuthBindManager {

    private static Logger logger = LoggerFactory.getLogger(OAuthAuthBindManagerImpl.class);

    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectRelationService connectRelationService;

    @Override
    public Result connectSSOBind(OAuthSinaSSOBindTokenRequest oauthRequest, int provider) {
        Result result = new APIResultSupport(false);
        int clientId = oauthRequest.getClientId();
        String bindAccessToken = oauthRequest.getBindToken();
        String openid = oauthRequest.getOpenid();
        try {
            String appKey = connectConfigService.querySpecifyAppKey(clientId, provider);

            // 检查主账号access_token是否有效
            AccountToken bindAccountToken = accountTokenService.verifyAccessToken(bindAccessToken);
            if (bindAccessToken == null) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }

            String bindPassportId = bindAccountToken.getPassportId();
            Result inspectsPassportIdRule = isAbleBindDependBindPassportId(bindPassportId, provider, appKey);
            if (!inspectsPassportIdRule.isSuccess()) {
                return inspectsPassportIdRule;
            }

            Result inspectsOpenidRule = isAbleBindDependOpenid(openid, provider, appKey);
            if (!inspectsOpenidRule.isSuccess()) {
                return inspectsOpenidRule;
            }

            // 写入数据库
            ConnectToken newConnectToken = ManagerHelper.buildConnectToken(bindPassportId, provider, appKey, openid, oauthRequest.getAccessToken(),
                    oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
            boolean isInitialConnectToken = connectTokenService.initialConnectToken(newConnectToken);
            if (!isInitialConnectToken) {
                result.setCode(ErrorUtil.BIND_CONNECT_ACCOUNT_FAIL);
                return result;
            }
            ConnectRelation newConnectRelation = ManagerHelper.buildConnectRelation(openid, provider, bindPassportId, appKey);
            boolean isInitialConnectRelation = connectRelationService.initialConnectRelation(newConnectRelation);
            if (!isInitialConnectRelation) {
                result.setCode(ErrorUtil.BIND_CONNECT_ACCOUNT_FAIL);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("绑定成功");
            return result;
        } catch (ServiceException e) {
            logger.error("SSO bind Account Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /**
     * 校验是否可以绑定第三方账号
     * 绑定规则：
     * 在同一个appKey内，一种账号类型的账号只能绑定一个；例如：只能绑定qq_1，不能绑定qq_2；
     * 不能绑定和主账号同一账号类型的账号；例如：主账号qq_1，不能绑定qq_2，可以绑定sina_1，renren_1
     *
     * @return
     * @throws ServiceException
     */
    private Result isAbleBindDependBindPassportId(String bindPassportId, int provider, String appKey) throws ServiceException {
        Result result = new APIResultSupport(false);

        PassportIDInfoDO passportIDInfoDO = PassportIDGenerator.parsePassportId(bindPassportId);
        String bindAccountTypeStr = passportIDInfoDO.getAccountTypeStr();
        int bindProvider = AccountTypeEnum.getProvider(bindAccountTypeStr);
        if (bindProvider == provider) {   // 不能绑定与主账号同一类型的账号
            result.setCode(ErrorUtil.CONNOT_BIND_SAME_TYPE_ACCOUNT);
            return result;
        }

        String openid = connectTokenService.querySpecifyOpenId(bindPassportId, provider, appKey);
        if (openid == null) {
            result.setCode(ErrorUtil.NOTALLOWED_REPEAT_BIND_SAME_TYPE_ACCOUNT);
            return result;
        }
        result.setSuccess(true);
        result.setMessage("");
        return result;
    }

    /**
     * 校验是否可以绑定第三方账号
     * 绑定规则：
     * 已注册过或被绑定的账号无法被其他账号再次绑定；
     *
     * @return
     */
    private Result isAbleBindDependOpenid(String openid, int provider, String appKey) throws ServiceException {
        Result result = new APIResultSupport(false);
        ConnectRelation connectRelation = connectRelationService.querySpecifyConnectRelation(openid, provider, appKey);
        if (connectRelation != null) {
            result.setCode(ErrorUtil.ACCOUNT_ALREADY_REG_OR_BIND);
            return result;
        }
        result.setSuccess(true);
        result.setMessage("");
        return result;
    }
}
