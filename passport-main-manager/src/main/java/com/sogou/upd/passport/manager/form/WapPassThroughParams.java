package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * wap qq透传
 * User: mayan
 * Date: 13-12-17
 * Time: 下午6:57
 */
public class WapPassThroughParams {
    @NotBlank(message = "data is null")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
