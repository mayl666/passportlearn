package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.web.BaseConnectController;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountConnectService;
import com.sogou.upd.passport.service.connect.message.response.OAuthSinaSSOTokenResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * SSO-SDK第三方登录授权回调接口
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午12:07
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/connect")
public class SSOLoginCallbackController extends BaseConnectController {

    @Inject
    private AccountService accountService;
    @Inject
    private AccountConnectService accountConnectService;

    @RequestMapping("/ssologincallback/{providerStr}")
    public void handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                       @PathVariable("providerStr") String providerStr, @RequestParam(defaultValue = "0") int appkey) throws Exception {
        int provider = AccountTypeEnum.getProvider(providerStr);

        if (provider != AccountTypeEnum.SINA.getValue() || appkey == 0) {
            // TODO record param error
            return;
        }

        // 获取第三方用户信息
        try {
            OAuthSinaSSOTokenResponse oar = OAuthSinaSSOTokenResponse.oauthCodeAuthzResponse(req);
            String connectUid = oar.getConnectUid();

            // 判断账号是否已经存在
            AccountConnectQuery query = buildAccountConnectQuery(connectUid, provider);
            List<AccountConnect> accountConnectList = accountConnectService.listAccountConnectByQuery(query);
            AccountConnect user_connect = getAppointAppKeyAccountConnect(accountConnectList, appkey);

            long userid;
            if (user_connect == null) {
                if (CollectionUtils.isEmpty(accountConnectList)) {  // TODO 换成Guava
                    // 初始化Account
                    Account account = accountService.initialConnectAccount(oar.getConnectUid(), getIp(req), provider);
                    userid = account.getId();
                } else {  // 此账号已存在，只是未在当前应用登录 TODO 注意QQ的不同appid返回的uid不同
                    userid = accountConnectList.get(0).getUserid();
                }
                // TODO 是否有必要并行初始化Account_Auth和Account_Connect？
                // 初始化Account_Auth

                // 初始化Account_Connect
                AccountConnect accountConnect = buildAccountConnect(userid, appkey, provider, AccountConnect.STUTAS_LONGIN, oar.getOAuthToken());
                accountConnectService.initialAccountConnect(accountConnect);

            } else {   // 此账号在当前应用第N次登录
                userid = user_connect.getUserid();
                // TODO 是否有必要并行更新Account_Auth和Account_Connect?
                // 更新当前应用的Account_Auth，处于安全考虑refresh_token和access_token重新生成

                // 更新当前应用的Account_Connect
                AccountConnect accountConnect = buildAccountConnect(userid, appkey, provider, AccountConnect.STUTAS_LONGIN, oar.getOAuthToken());
                accountConnectService.updateAccountConnect(accountConnect);
            }

        } catch (Exception e) {
            handleException(e, providerStr, req.getRequestURI());
        } finally {
            return;
        }
    }

    private AccountConnectQuery buildAccountConnectQuery(String connectUid, int provider) {
        AccountConnectQuery query = new AccountConnectQuery();
        query.setConnectUid(connectUid);
        query.setProvider(provider);
        return query;
    }

    /*
    该账号是否在当前应用登录过
     */
    private AccountConnect getAppointAppKeyAccountConnect(List<AccountConnect> accountConnectList, int appkey) {
        AccountConnect accountConnect = null;
        for (AccountConnect connect : accountConnectList) {
            if (appkey == connect.getAppkey()) {
                accountConnect = connect;
                break;
            }
        }
        return accountConnect;
    }

}
