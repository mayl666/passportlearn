package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;

import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.MoblieCodeParams;
import com.sogou.upd.passport.web.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.image.BufferedImage;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 * User: mayan Date: 13-5-7 Time: 下午6:22 To change this template use
 * File | Settings | File Templates.
 */
@Controller
public class CaptchaController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RegManager regManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    /**
     * web注册时页面展示的验证码
     *
     * @param token
     * @param model
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    @ResponseBody
    public Object obtainCaptcha(@RequestParam(defaultValue = "") String token,
                                Model model,
                                HttpServletResponse response) throws Exception {

        //生成验证码
        Map<String, Object> map = regManager.getCaptchaCode(token);
        if (map != null && map.size() > 0) {
            ImageIO.write((BufferedImage) map.get("image"), "JPEG", response.getOutputStream());//将内存中的图片通过流动形式输出到客户端

            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);

        }
        return null;
    }

    /**
     * 手机账号注册时发送的验证码
     *
     * @param reqParams 传入的参数
     */
    @RequestMapping(value = {"/v2/sendmobilecode", "/mobile/sendsms"}, method = RequestMethod.GET)
    @ResponseBody
    public Object sendMobileCode(BaseMoblieApiParams reqParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //验证client_id
        int clientId = reqParams.getClient_id();

        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result.toString();
        }

        result = proxyRegisterApiManager.sendMobileRegCaptcha(reqParams);
        return result.toString();

    }
}

