package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.IllegalSensitive;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-9-22
 * Time: 下午12:27
 * To change this template use File | Settings | File Templates.
 */
public class CheckOrUpdateNickNameParams {

    @NotBlank(message = "用户昵称不能为空")
    @IllegalSensitive
    private String nickname;
    private String sname = "";  //账号，sohu+继承而来，这里不做处理

    @AssertTrue(message = "昵称长度不符合规则，长度应该在2——12字符之间")
    private boolean isCheckLength() {
        if (!(nickname.length() >= 2 && nickname.length() <= 12)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "昵称中不能含有搜狐，sohu，souhu，搜狗，sogou，sougou字样")
    private boolean isCheckSensitive() {
        String regx = "^(?!.*搜狗)(?!.*sogou)(?!.*sougou)(?!.*搜狐)(?!.*sohu)(?!.*souhu)(?!.*搜狐微博)(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        if (!nickname.matches(regx)) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "昵称不符合组成规则，只能使用中文、字母、数字和下划线组合，但不能以下划线开头或结尾")
    private boolean isCheckElement() {
        String regx = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        //返回false，说明不符合组成规则
        if (!nickname.matches(regx)) {
            return false;
        }
        return true;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

}
