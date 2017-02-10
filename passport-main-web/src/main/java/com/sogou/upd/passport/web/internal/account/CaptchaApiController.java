package com.sogou.upd.passport.web.internal.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.account.RegManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 验证码校验
 */
@Controller
@RequestMapping("/internal")
public class CaptchaApiController {

    @Autowired
    private RegManager regManager;

    /**
     * web注册时页面展示的验证码
     *
     * @param token
     * @param captcha
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/verify_captcha", method = RequestMethod.POST)
    @ResponseBody
    public Object verifyCaptcha(String token, String captcha) throws Exception {
        // 检查验证码是否通过
        Result result = regManager.checkCaptchaToken(token, captcha);

        return result.toString();
    }
}

