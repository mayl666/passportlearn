package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午3:32
 * To change this template use File | Settings | File Templates.
 */
public class MobileRegParams extends MobileModifyPwdParams {

    @NotBlank(message = "应用实例id不允许为空!")
    private String instance_id;

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }
}
