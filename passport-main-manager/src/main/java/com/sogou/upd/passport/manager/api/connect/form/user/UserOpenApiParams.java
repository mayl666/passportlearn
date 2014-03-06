package com.sogou.upd.passport.manager.api.connect.form.user;

import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;

import javax.validation.constraints.AssertTrue;

/**
 * 用户信息类
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午12:02
 * To change this template use File | Settings | File Templates.
 */
public class UserOpenApiParams extends BaseOpenApiParams {
    private int original;

    @AssertTrue(message = "请输入正确的original值!")
    private boolean isObtainOriginal() {
        if (original == 1 || original == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getOriginal() {
        return original;
    }

    public void setOriginal(int original) {
        this.original = original;
    }
}
