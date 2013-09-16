package com.sogou.upd.passport.manager.api.account.form;

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
        if (!(uniqname.length() >= 2 && uniqname.length() <= 12)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "昵称中不能含有搜狐，sohu，souhu，搜狗，sogou，sougou字样")
    private boolean isCheckSensitive() {
        String regx = "^(?!.*搜狗)(?!.*sogou)(?!.*sougou)(?!.*搜狐)(?!.*sohu)(?!.*souhu)(?!.*搜狐微博)(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        if (!uniqname.matches(regx)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "昵称不符合组成规则，只能使用中文、字母、数字和下划线组合，但不能以下划线开头或结尾")
    private boolean isCheckElement() {
        String regx = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        //返回false，说明不符合组成规则
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
