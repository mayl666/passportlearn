package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.BaseWebParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
    private UserInfoApiManager shPlusUserInfoApiManager;

    @RequestMapping(value = { "/index", "/" })
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
            Result result = new APIResultSupport(false);
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);
            if (domain == AccountDomainEnum.THIRD) {

            //获取昵称
            String nickName = hostHolder.getNickName();
            if (Strings.isNullOrEmpty(nickName)) {
                nickName = userId;
            }
                GetUserInfoApiparams infoApiparams=new GetUserInfoApiparams();
                infoApiparams.setUserid(userId);
                Result shPlusResult=shPlusUserInfoApiManager.getUserInfo(infoApiparams);
                if(shPlusResult.isSuccess()){
                    Object obj= shPlusResult.getModels().get("baseInfo");
                    if(obj!=null){
                        AccountBaseInfo baseInfo= (AccountBaseInfo) obj;
                        String uniqname=baseInfo.getUniqname();
                        result.setDefaultModel("username", Strings.isNullOrEmpty(uniqname)?userId:uniqname);
                    }
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
