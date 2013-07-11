package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * web注册时页面展示的验证码
 * User: mayan
 * Date: 13-5-7 Time: 下午6:22
 */
@Controller
public class CaptchaAction {

    @Autowired
    private RegManager regManager;

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
            ImageIO.write((BufferedImage) map.get("image"), "png", response.getOutputStream());//将内存中的图片通过流动形式输出到客户端

            response.setContentType("image/png");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
        }
        return null;
    }




}

