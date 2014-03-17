package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
@Component("proxyRegisterApiManager")
public class ProxyRegisterApiManagerImpl extends BaseProxyManager implements RegisterApiManager {

    @Override
    public Result regMailUser(RegEmailApiParams regEmailApiParams) {
        regEmailApiParams.setSend_email("1");
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.WEB_EMAIL_REG, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(regEmailApiParams);
        Result result = executeResult(requestModelXml);
        if (result.isSuccess()) {
            result.setMessage("注册成功");
            result.setDefaultModel("userid", regEmailApiParams.getUserid());

          //判断注册账号类型
          AccountDomainEnum emailType = AccountDomainEnum.getAccountDomain(regEmailApiParams.getUserid());
          switch (emailType){
            case OTHER:
              result.setDefaultModel("isSetCookie",false);
              break;
            default:
              result.setDefaultModel("isSetCookie",true);
              break;
          }
        }
        return result;
    }

    @Override
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regMobileCaptchaApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.REG_MOBILE_CAPTCHA, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(regMobileCaptchaApiParams);
        Result result = executeResult(requestModelXml, regMobileCaptchaApiParams.getMobile());
        if (result.isSuccess()) {
            result.setMessage("注册成功");
            String passportId = PassportIDGenerator.generator(regMobileCaptchaApiParams.getMobile(), AccountTypeEnum.PHONE.getValue());
            result.setDefaultModel("userid", passportId);
            result.setDefaultModel("isSetCookie",true);
        }
        return result;
    }

    @Override
    public Result sendMobileRegCaptcha(BaseMobileApiParams baseMobileApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.SEND_MOBILE_REG_CAPTCHA, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMobileApiParams);
        String mobile = baseMobileApiParams.getMobile();
        Result result = executeResult(requestModelXml, mobile);
        if (result.isSuccess()) {
            result.setMessage("验证码已发送至" + mobile);
        }
        return result;
    }

    @Override
    public Result checkUser(CheckUserApiParams checkUserApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.CHECK_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(checkUserApiParams);
        Result result = executeResult(requestModelXml);
        if (!result.isSuccess()) {
            result.setDefaultModel("userid", checkUserApiParams.getUserid());
        }
        return result;
    }

    @Override
    public Result regMobileUser(RegMobileApiParams regMobileApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.REG_MOBILE_NOCAPTCHA, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(regMobileApiParams);
        Result result = executeResult(requestModelXml, regMobileApiParams.getMobile());
        if (result.isSuccess()) {
            result.setMessage("注册成功");
            String passportId = PassportIDGenerator.generator(regMobileApiParams.getMobile(), AccountTypeEnum.PHONE.getValue());
            result.setDefaultModel("userid", passportId);
            result.setDefaultModel("isSetCookie",false);
        }
        return result;
    }
}
