package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.BaseWebParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-8 Time: 下午2:12 To change this template use
 * File | Settings | File Templates.
 */
@Controller
public class IndexAction extends BaseController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;

    @RequestMapping(value = {"/index", "/"})
    @LoginRequired(value = false)
    public String indexPage(BaseWebParams params, Model model) throws Exception {
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return "redirect:/web/webLogin"; // TODO:返回错误页面
        }
        if (hostHolder.isLogin()) {
            String userId = hostHolder.getPassportId();
            int clientId = Integer.parseInt(params.getClient_id());

            // 第三方账号不显示安全信息
            Result result;
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);
            if (domain == AccountDomainEnum.THIRD) {
                result = oAuth2ResourceManager.getUniqNameAndAvatar(userId, clientId);
                if (result.isSuccess()) {
                    result.getModels().put("uniqname", result.getModels().get("uniqname"));
                    result.getModels().put("username", result.getModels().get("uniqname"));
                    Map<String, String> map = result.getModels();
                    map.remove("uniqname");
                    map.remove("username");
                    map.remove("userid");
                    result.getModels().put("avatarurl", map);
                }
                result.setDefaultModel("disable", true);
                result.setSuccess(true);
            } else {
                result = secureManager.queryAccountSecureInfo(userId, clientId, true);
            }

            if (domain == AccountDomainEnum.PHONE) {
                result.setDefaultModel("actype", "phone");
            }

            model.addAttribute("data", result.toString());
            return "ucenter/index";
        }
        return "redirect:/web/webLogin";
    }
}
