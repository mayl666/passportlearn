package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.UniqNameUtil;
import com.sogou.upd.passport.common.validation.constraints.IllegalSensitive;
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
    @IllegalSensitive
    private String uniqname;

    @AssertTrue(message = "昵称长度不符合规则，长度应该在2——12字符之间")
    private boolean isCheckUinqName() {
        if (Strings.isNullOrEmpty(uniqname)) {
            return true;
        }
        UniqNameUtil uniqNameUtil = new UniqNameUtil();
        if (!uniqNameUtil.checkUniqNameIsCorrect(uniqname)) {
            return false;
        } else {
            return true;
        }
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }
}
