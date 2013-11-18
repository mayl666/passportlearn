package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.CookieWebParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-10-10
 * Time: 上午11:36
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web")
public class CookieAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CookieAction.class);

    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";

    private String PPINF = "2|1381574146|1382783746|bG9naW5pZDowOnx1c2VyaWQ6MTg6Y2hlbnRpbmdAc29nb3UuY29tfHNlcnZpY2V1c2U6MjA6MDAxMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjEwOjIwMDUtMDMtMDZ8ZW10OjE6MHxhcHBpZDo0OjExMjB8dHJ1c3Q6MToxfHBhcnRuZXJpZDoxOjB8cmVsYXRpb246MDp8dXVpZDoxNjoxMDY0NTg3MmJjZmM0NzVzfHVpZDo5OnU3MzE0NzUxNHx1bmlxbmFtZTowOnw";
    private String PPRDIG = "N7Jl5y3iAmeYrGEkMgZ7lMG-DkCjATfllxVcQ2qU7OxgkAvT1MQUpI1TIfg-pXuTpNU4kOHjhYn54bPjtOE7fBRxRlzk8Hg5NYwi5feaKa9SOraHeR5Hl4YhdNBQDMvHvZhYhLMP4HKraT0tx7_fp9n6dLZ-EsQBM26XnxYepS0";


    @Autowired
    private CookieManager cookieManager;
    @Autowired
    private CommonManager commonManager;

    /**
     * 种sogou域cookie
     */
    @RequestMapping(value = "/account/setcookie", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletRequest request, HttpServletResponse response, CookieWebParams cookieWebParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(cookieWebParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String username = cookieWebParams.getUserid();
        CookieApiParams cookieApiParams;
        //todo 检查code签名是否正确，sohu最终的签名策略可能变，这里暂时保持原样
        result = checkCodeIsCorrect(username, cookieWebParams.getClient_id(), Long.parseLong(cookieWebParams.getCt()), cookieWebParams.getCode());
        if (!result.isSuccess()) {
            return result.toString();
        }
        //todo 数据迁移第一步时，只有@sogou账号需要查sogou库，其它账号直接传递过去；全部账号迁移完成后，都需要查sogou库
        if (!ManagerHelper.isInvokeProxyApi(username)) {
            //搜狗账号验证账号是否合法
            Account account = commonManager.queryAccountByPassportId(username);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result.toString();
            } else if (account.getStatus() == AccountStatusEnum.KILLED.getValue()) {
                //封杀用户不允许操作
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                return result.toString();
            }
            //根据搜狗用户状态置激活状态
            cookieApiParams = transferWebParamsToApiParams(account, cookieWebParams);
        } else {
            //非搜狗账号默认激活
            cookieApiParams = transferWebParamsToApiParams(null, cookieWebParams);
        }
        //是否种持久型cookie，0：否 1：是
        int maxAge = (int) (cookieWebParams.getPersistentcookie() == 0 ? -1 : DateAndNumTimesConstant.TWO_WEEKS);
        result = cookieManager.setCookie(response, cookieApiParams, maxAge);
        if (result.isSuccess()) {
            //设置来源
            String ru = cookieWebParams.getRu();
            if (Strings.isNullOrEmpty(ru)) {
                ru = LOGIN_INDEX_URL;
            }
            result.setDefaultModel("ru", ru);
        }
        return result.toString();
    }

    private CookieApiParams transferWebParamsToApiParams(Account account, CookieWebParams cookieWebParams) {
        CookieApiParams cookieApiParams = new CookieApiParams();
        cookieApiParams.setClient_id(Integer.parseInt(cookieWebParams.getClient_id()));
        cookieApiParams.setUserid(cookieWebParams.getUserid());
        String ru = cookieWebParams.getRu();
        if (Strings.isNullOrEmpty(ru)) {
            ru = LOGIN_INDEX_URL;
        }
        cookieApiParams.setRu(ru);
        if (account != null) {
            int status = account.getStatus();
            switch (status) {
                case 0:
                    cookieApiParams.setTrust(0);             //未激活
                    break;
                case 1:
                    cookieApiParams.setTrust(1);             //激活
                    break;
                default:
                    cookieApiParams.setTrust(1);            //默认激活
                    break;
            }
        } else {
            cookieApiParams.setTrust(1);          //非搜狗账号默认激活
        }
        return cookieApiParams;
    }

    private Result checkCodeIsCorrect(String userid, String client_id, long ct, String originalCode) {
        Result result = new APIResultSupport(false);
        int clientId = Integer.parseInt(client_id);
        AppConfig appConfig = cookieManager.queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            String secret = appConfig.getServerSecret();
            String code = ManagerHelper.generatorCodeGBK(userid.toString(), clientId, secret, ct);
            if (code.equalsIgnoreCase(originalCode)) {
                result.setSuccess(true);
                result.setMessage("内部接口code签名正确！");
            } else {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            }
        } else {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
        }
        return result;
    }
}