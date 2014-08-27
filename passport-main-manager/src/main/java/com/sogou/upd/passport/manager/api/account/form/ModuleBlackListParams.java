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
    private String nginx_version;   //nginx版本号
    @Min(0)
    private int client_id;
    //    @NotBlank(message = "root_domain不能为空")
//    sohu的是全局只有一个root_domain所以可以传过去. 现在我们改成了每个server可以配不同的, 所以没法往后传了
//    private String root_domain;   //产品所在的域名
    private String instance_id;   //实例ID
    private int update_interval;   //获取黑名单的访问间隔,单位s
    private long update_timestamp;   //黑名单的有效期

    private int is_delta;//全量/增量（0/1）

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

 /*   public String getRoot_domain() {
        return root_domain;
    }

    public void setRoot_domain(String root_domain) {
        this.root_domain = root_domain;
    }*/

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public int getUpdate_interval() {
        return update_interval;
    }

    public void setUpdate_interval(int update_interval) {
        this.update_interval = update_interval;
    }

    public long getUpdate_timestamp() {
        return update_timestamp;
    }

    public void setUpdate_timestamp(long update_timestamp) {
        this.update_timestamp = update_timestamp;
    }

    public int getIs_delta() {
        return is_delta;
    }

    public void setIs_delta(int is_delta) {
        this.is_delta = is_delta;
    }
}
