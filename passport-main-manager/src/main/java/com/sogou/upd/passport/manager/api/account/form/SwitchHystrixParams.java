package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-5-13
 * Time: 下午3:59
 * To change this template use File | Settings | File Templates.
 */
public class SwitchHystrixParams  {

    @Min(0)
    protected int client_id; //应用id

    @Min(0)
    protected long ct; //单位为毫秒

    @NotBlank(message = "userid不能为空")
    protected String userid;

    @NotBlank(message = "global开关不能为空")
    protected Boolean globalEnabled;

    @NotBlank(message = "qq开关不能为空")
    protected Boolean qqHystrixEnabled;

    @NotBlank(message = "kafka开关不能为空")
    protected Boolean kafkaHystrixEnabled;

    protected String qcloudIpPort;

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

    public String getQcloudIpPort() {
        return qcloudIpPort;
    }

    public void setQcloudIpPort(String qcloudIpPort) {
        this.qcloudIpPort = qcloudIpPort;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }
}
