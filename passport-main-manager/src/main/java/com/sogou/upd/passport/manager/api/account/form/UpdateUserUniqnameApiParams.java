package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.validation.constraints.UniqName;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-23
 * Time: 上午10:31
 * To change this template use File | Settings | File Templates.
 */
public class UpdateUserUniqnameApiParams extends BaseApiParams {

    @NotBlank(message = "用户昵称不能为空")
    private String uniqname;

    @AssertTrue(message = "昵称长度不符合规则，长度应该在2——12字符之间")
    private boolean isCheckLength() {
        if (Strings.isNullOrEmpty(uniqname)) {
            return true;
        }
        if (!(uniqname.length() >= 2 && uniqname.length() <= 12)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "昵称格式不正确")
    private boolean isCheckSensitive() {
        if (Strings.isNullOrEmpty(uniqname)) {
            return true;
        }
        String regx = "^(?!.*搜狗)(?!.*sogou)(?!.*sougou)(?!.*搜狐)(?!.*sohu)(?!.*souhu)(?!.*搜狐微博)[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        if (!uniqname.matches(regx)) {
            return false;
        }
        return true;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }
}
