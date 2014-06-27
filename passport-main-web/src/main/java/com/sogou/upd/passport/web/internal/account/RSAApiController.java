package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Base64Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.api.account.form.RSAApiParams;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 通过rsa加密的方式来获取相关信息。
 * <p>
 * 以rsa当做验证状态。 加密者使用公钥加密。 passport使用私钥解密，得到相关信息。
 * </p>
 * Created by denghua on 14-6-10.
 */
@Controller
@RequestMapping("/internal/rsa")
public class RSAApiController extends BaseController {

    public static final int TIME_LIMIT = 60 * 60 * 24 * 1000;
    private static Logger logger = LoggerFactory.getLogger(RSAApiController.class);

    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;


    @InterfaceSecurity
    @RequestMapping(value = "/userid", method = RequestMethod.POST)
    @ResponseBody
    public String getUserId(HttpServletRequest request, RSAApiParams params) {

        Result result = new APIResultSupport(false);
        String userId=null;
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            try {
                userId=getUserId(params.getCipherText());
                if(Strings.isNullOrEmpty(userId)){
                    result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                    result.setMessage("加密错误！");
                }else{
                    result.setSuccess(true);
                    result.setMessage("操作成功");
                    result.getModels().put("userid", userId);
                    return result.toString();
                }
            } catch (ControllerException e) {
                result.setCode(e.getCode());
                return result.toString();
            }

        } finally {
            //记录log
            UserOperationLog userOperationLog = new UserOperationLog(userId,request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * 检查密文的正确性。
     * @param cipherText 密文
     * @return userId
     */
    public String getUserId(String cipherText) throws ControllerException{
        String clearText;
        try {
            clearText = RSA.decryptByPrivateKey(Base64Coder.decode(cipherText), TokenGenerator.PRIVATE_KEY);
        } catch (Exception e) {
            logger.error("decrypt error, cipherText:" + cipherText, e);
            throw new ControllerException(ErrorUtil.ERR_CODE_RSA_DECRYPT);
        }
//        String passportId=null;
        if (!Strings.isNullOrEmpty(clearText)) {
            String[] textArray = clearText.split("\\|");
            if (textArray.length == 4) { //数据组成： userid|clientId|token|timestamp
                    //判断时间有效性
                    long timeStamp=Long.parseLong(textArray[3])*1000;
                    if(Math.abs(timeStamp - System.currentTimeMillis())> TIME_LIMIT){
                            logger.error("time expired, text:"+clearText+ " current:"+ System.currentTimeMillis());
                            throw new ControllerException(ErrorUtil.ERR_CODE_RSA_DECRYPT);
                    }

                    //判断用户名是否和token取得的一致
                    Result getUserIdResult = oAuth2ResourceManager.getPassportIdByToken(textArray[2], Integer.parseInt(textArray[1]));
                    if (getUserIdResult.isSuccess()) {
                        String passportId = (String) getUserIdResult.getDefaultModel();

                        if (!Strings.isNullOrEmpty(passportId) && passportId.equals(textArray[0])) { //解密后的token得到userid，需要和传入的userid一样，保证安全及toke有效性。
                            return textArray[0];
                        }
                    }else{
                        logger.error("can't get token, text:"+clearText);
                        throw new ControllerException(getUserIdResult.getCode());
                    }

            } else {
                //长度不对。
                logger.error("text to array  length error, expect 4, text:"+clearText);
                throw new ControllerException(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            }
        } else {
            logger.error("clearText is empty cipherText:" + cipherText);
            throw new ControllerException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return null;
    }
}


class ControllerException extends Exception{
    private String code;
    public ControllerException(String code) {
        this.code=code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}