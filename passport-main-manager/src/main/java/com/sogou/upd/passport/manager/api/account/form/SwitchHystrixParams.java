package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-5-13
 * Time: 下午3:59
 * To change this template use File | Settings | File Templates.
 */
public class SwitchHystrixParams extends BaseApiParams {

    @NotBlank(message = "userid不能为空")
    protected String userid;

    @NotBlank(message = "global开关不能为空")
    protected Boolean globalEnabled;

    @NotBlank(message = "qq开关不能为空")
    protected Boolean qqHystrixEnabled;

    @NotBlank(message = "kafka开关不能为空")
    protected Boolean kafkaHystrixEnabled;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Boolean getGlobalEnabled() {
        return globalEnabled;
    }

    public void setGlobalEnabled(Boolean globalEnabled) {
        this.globalEnabled = globalEnabled;
    }

    public Boolean getQqHystrixEnabled() {
        return qqHystrixEnabled;
    }

    public void setQqHystrixEnabled(Boolean qqHystrixEnabled) {
        this.qqHystrixEnabled = qqHystrixEnabled;
    }

    public Boolean getKafkaHystrixEnabled() {
        return kafkaHystrixEnabled;
    }

    public void setKafkaHystrixEnabled(Boolean kafkaHystrixEnabled) {
        this.kafkaHystrixEnabled = kafkaHystrixEnabled;
    }

}
