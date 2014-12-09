package com.sogou.upd.passport.web.account.form.mapp;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.MappStatReportTypeEnum;
import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * 移动端数据上报接口
 * User: shipengzhi
 * Date: 14-11-22
 * Time: 下午6:02
 */
public class MappStatReportParams extends MappBaseParams{

    @NotBlank(message = "data不允许为空！")
    private String data;
    @NotBlank(message ="type不允许为空")
    private String type;

    @AssertTrue(message = "不支持的type")
    private boolean isSupportType() {
        if (Strings.isNullOrEmpty(this.type)) {
            return true;
        }
        if (!MappStatReportTypeEnum.isSupportType(this.type)) {
            return false;
        }
        return true;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
