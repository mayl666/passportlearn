package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.common.validation.constraints.Phone;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 绑定手机账号的参数类
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:25
 */
public class BindMobileApiParams extends BaseUserApiParams {

    //原来绑定的手机号
    @Phone
    private String oldMobile;

    //老手机的验证码
    private String oldCaptcha;

    //新手机号
    @Phone
    private String newMobile;

    //新绑定手机号的验证码
    private String newCaptcha;


    public String getOldMobile() {
        return oldMobile;
    }

    public void setOldMobile(String oldMobile) {
        this.oldMobile = oldMobile;
    }

    public String getOldCaptcha() {
        return oldCaptcha;
    }

    public void setOldCaptcha(String oldCaptcha) {
        this.oldCaptcha = oldCaptcha;
    }

    public String getNewMobile() {
        return newMobile;
    }

    public void setNewMobile(String newMobile) {
        this.newMobile = newMobile;
    }

    public String getNewCaptcha() {
        return newCaptcha;
    }

    public void setNewCaptcha(String newCaptcha) {
        this.newCaptcha = newCaptcha;
    }
}
