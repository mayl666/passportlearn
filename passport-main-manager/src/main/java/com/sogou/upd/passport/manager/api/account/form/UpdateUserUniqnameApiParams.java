package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.common.validation.constraints.UniqName;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-23
 * Time: 上午10:31
 * To change this template use File | Settings | File Templates.
 */
public class UpdateUserUniqnameApiParams extends BaseApiParams {

    @NotBlank(message = "用户昵称不能为空")
    @UniqName
    private String uniqname;

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }
}
