package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Base64Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.RSAApiParams;
import com.sogou.upd.passport.service.account.MappTokenService;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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

    private static Logger logger = LoggerFactory.getLogger(RSAApiController.class);

    @Autowired
    private MappTokenService mappTokenService;


    @InterfaceSecurity
    @RequestMapping(value = "/userid", method = RequestMethod.POST)
    @ResponseBody
    public String getUserId(HttpServletRequest request, RSAApiParams params) {

        Result result = new APIResultSupport(false);
        try {


            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }

            String clearText = null;
            try {
//            System.out.println(params.getCipherText());
                clearText = RSA.decryptByPrivateKey(Base64Coder.decode(params.getCipherText()), TokenGenerator.PRIVATE_KEY);
            } catch (Exception e) {
                logger.error("decrypt error, cipherText:" + params.getCipherText(), e);
                result.setCode(ErrorUtil.ERR_CODE_RSA_DECRYPT);
                return result.toString();
            }

            if (!Strings.isNullOrEmpty(clearText)) {
                String[] textArray = clearText.split("\\|");
                if (textArray.length == 3) { //数据组成： userid|token|timestamp
                    try {
                        String passportId = mappTokenService.getPassprotIdByToken(textArray[1]);
                        if (!Strings.isNullOrEmpty(passportId) && passportId.equals(textArray[0])) { //解密后的token得到userid，需要和传入的userid一样，保证安全及toke有效性。
                            result.setSuccess(true);
                            result.setMessage("操作成功");
                            result.getModels().put("userid", textArray[0]);
                            return result.toString();
                        }
                        result.setCode(ErrorUtil.ERR_SIGNATURE_OR_TOKEN);
                        return result.toString();

                    } catch (Exception e) {
                        logger.error("ras token error fail, clear text:" + clearText, e);
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
                        return result.toString();
                    }
                } else {
                    //长度不对。
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
                    return result.toString();
                }
            } else {
                result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                result.setMessage("加密错误！");
            }

            return result.toString();
        } finally {
            //记录log
            UserOperationLog userOperationLog = new UserOperationLog(request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), result.getMessage(), getIp(request));
            UserOperationLogUtil.log(userOperationLog);

        }
    }
}
