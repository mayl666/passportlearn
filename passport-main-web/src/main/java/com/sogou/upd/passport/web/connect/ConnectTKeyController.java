package com.sogou.upd.passport.web.connect;

import static com.sogou.upd.passport.common.CommonConstant.*;

import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.IpLocationUtil;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.form.TKeyParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 该类为交换QQ的tKey  参见：http://svn.sogou-inc.com/svn/userplatform/updoc/passport/概要设计方案/2014Q2/搜狗输入法拉取QQ好友/输入法拉取好友列表-概要设计-passport.docx
 * Created by denghua on 14-5-12.
 */
@Controller
@RequestMapping("/connect/")
public class ConnectTKeyController {

    public static final String TKEY_VERSION = "01";
    public static final String TKEY_SECURE_KEY = "adfab231rqwqerq";
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private SessionServerManager sessionServerManager;


    @Autowired
    private ConnectConfigService connectConfigService;

    @Autowired
    private ConnectTokenService connectTokenService;


    @ResponseBody
    @RequestMapping(value = "/t_key")
    public String tKey(HttpServletRequest req, HttpServletResponse res, TKeyParams tKeyParams) throws Exception {
        Result result = new APIResultSupport(false);

        String userId = null;
        if (!hostHolder.isLogin()) {
            //判断sgid是否存在。如果不存在，则输出未登录信息
            if (tKeyParams.getSgid() == null) {
                result.setMessage("请重新登录");
                return result.toString();
            } else {
                //判断sgid的正确性。
                Result r = sessionServerManager.getPassportIdBySgid(tKeyParams.getSgid(), IpLocationUtil.getIp(req));
                if (r.isSuccess()) {
                    userId = (String) r.getModels().get("passport_id");
                } else {
                    result.setMessage("请重新登录");
                    return result.toString();
                }
            }
        } else {
            userId = hostHolder.getPassportId();
        }


        //根据client来获取相关信息。
//        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(tKeyParams.getClient_id(), AccountTypeEnum.getProvider("qq"));
        int clientId = tKeyParams.getClient_id();
        ConnectToken connectToken = getConnectToken(userId, clientId);
        if (connectToken == null) {
            result.setMessage("请重新登录!");
            return result.toString();
        }
        String tKey = createTKey_V01(clientId, connectToken);

        result.setSuccess(true);
        result.getModels().put("tKey", tKey);
        return result.toString();
    }

    private String createTKey_V01(int clientId, ConnectToken connectToken) throws Exception {
        //未加密前的字符串  //tKey组成: openId|openkey|expireIn|appid|userid|clientid|timestamp
        String tKeyString=String.format("%s|%s|%s|%s|%s|%s|%s", connectToken.getOpenid(), connectToken.getAccessToken(), connectToken.getExpiresIn(), connectToken.getAppKey(), connectToken.getPassportId(), clientId, System.currentTimeMillis());

        return TKEY_VERSION + SEPARATOR_1 + AES.encryptURLSafeString(tKeyString, TKEY_SECURE_KEY);
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
}
