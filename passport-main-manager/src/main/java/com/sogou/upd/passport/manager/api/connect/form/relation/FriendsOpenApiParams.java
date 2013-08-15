package com.sogou.upd.passport.manager.api.connect.form.relation;

import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;

import javax.validation.constraints.Min;

/**
 *关系类之获取好友列表参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-7-26
 * Time: 上午11:49
 * To change this template use File | Settings | File Templates.
 */
public class FriendsOpenApiParams extends BaseOpenApiParams {
    private String user_ip;
    private String name;
    private String fopenid;
    @Min(1)
    private int page;
    private int count;

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFopenid() {
        return fopenid;
    }

    public void setFopenid(String fopenid) {
        this.fopenid = fopenid;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
