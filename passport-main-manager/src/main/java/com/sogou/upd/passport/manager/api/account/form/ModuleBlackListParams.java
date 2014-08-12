package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-8-12
 * Time: 下午10:29
 * To change this template use File | Settings | File Templates.
 */
public class ModuleBlackListParams {

    private String version;
    private String nginx_version;
    @Min(0)
    private int client_id;
    @NotBlank(message = "root_domain不能为空")
    private String root_domain;
    private String instance_id;
    private String update_interval;
    private String update_timestamp;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNginx_version() {
        return nginx_version;
    }

    public void setNginx_version(String nginx_version) {
        this.nginx_version = nginx_version;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getRoot_domain() {
        return root_domain;
    }

    public void setRoot_domain(String root_domain) {
        this.root_domain = root_domain;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getUpdate_interval() {
        return update_interval;
    }

    public void setUpdate_interval(String update_interval) {
        this.update_interval = update_interval;
    }

    public String getUpdate_timestamp() {
        return update_timestamp;
    }

    public void setUpdate_timestamp(String update_timestamp) {
        this.update_timestamp = update_timestamp;
    }
}
