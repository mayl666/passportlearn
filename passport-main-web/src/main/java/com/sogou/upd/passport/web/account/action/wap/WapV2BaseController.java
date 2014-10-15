package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.account.form.wap.WapRegMobileCodeParams;
import org.springframework.ui.Model;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-10-15
 * Time: 上午12:20
 * To change this template use File | Settings | File Templates.
 */
public class WapV2BaseController extends BaseController {

    /**
     * 在接口渲染VM模板的页面增加model的attribute
     * @param hasError
     * @param ru
     * @param errorMsg
     * @param client_id
     * @param skin
     * @param v
     * @param needCaptcha
     * @param model
     */
    protected void addReturnPageModel(boolean hasError, String ru, String errorMsg, String client_id, String skin, String v, boolean needCaptcha, Model model) {
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("hasError", hasError);
        model.addAttribute("ru", Strings.isNullOrEmpty(ru) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(ru));
        model.addAttribute("skin", Strings.isNullOrEmpty(skin) ? WapConstant.WAP_SKIN_GREEN : skin);
        model.addAttribute("needCaptcha", needCaptcha);
        model.addAttribute("v", Strings.isNullOrEmpty(v) ? WapConstant.WAP_COLOR : v);
        model.addAttribute("client_id", client_id);
    }

    /**
     * 在重定向的中间页面增加model的attribute
     * @param model
     * @param params
     * @param scode
     * @param hasError
     */
    protected void addRedirectPageModule(Model model, WapRegMobileCodeParams params, String scode, boolean hasError) {
        model.addAttribute("errorMsg", params.getErrorMsg());
        model.addAttribute("hasError", hasError);
        model.addAttribute("ru", params.getRu());
        model.addAttribute("skin", params.getSkin());
        model.addAttribute("needCaptcha", params.getNeedCaptcha());
        model.addAttribute("v", params.getV());
        model.addAttribute("client_id", params.getClient_id());
        model.addAttribute("mobile", params.getMobile());
        model.addAttribute("username", params.getMobile());
        model.addAttribute("scode", scode);
    }
}
