package com.sogou.upd.passport.manager.api.account.form;

/**
 * 下发验证码的接口参数
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 下午5:28
 */
public class SendCaptchaApiParams extends BaseMobileApiParams {

    //下发验证码的类型 3、绑定手机号；4解绑手机号
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
