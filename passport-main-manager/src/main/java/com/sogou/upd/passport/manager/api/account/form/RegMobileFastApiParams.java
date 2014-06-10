package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-7-4 Time: 上午11:19 To change this template use File | Settings | File Templates.
 */
public class RegMobileFastApiParams extends BaseMoblieApiParams {

    @NotBlank(message = "createip不允许为空")
    private String createip;  //用户真实ip

    public RegMobileFastApiParams(){

    }

    public String getCreateip() {
        return createip;
    }

    public void setCreateip(String createip) {
        this.createip = createip;
    }
}
