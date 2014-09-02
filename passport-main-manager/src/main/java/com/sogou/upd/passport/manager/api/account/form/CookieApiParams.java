package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-10-10
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public class CookieApiParams extends BaseApiParams {

    public static final int IS_ACTIVE = 1;      //激活
    public static final int IS_NOT_ACTIVE = 0;  //未激活

    @NotBlank(message = "用户id不能为空")
    private String userid;     //用户id
    private int trust;   //激活：1，未激活：0   只外域邮箱用到，其它账号默认为1，激活
    @Ru
    @URL
    private String ru;
    @NotBlank
    private String ip;                     //用户的机器ip
    private String persistentcookie;         //是否记住密码（0：否 1：是）或是否使用持久cookie 0:session级别的cookie 1:长时间有效的cookie，目前是两天

    private String uniqname; // Passport统一的昵称
    private String refnick;  // 第三方昵称

    private int maxAge;

    private String domain;


    //是否生成之后就种cookie，0:生成后就种、1:生成后不种
    private int createAndSet;

    public CookieApiParams() {
    }

    public CookieApiParams(String userid, int client_id, String ru, String ip) {
        this(userid, client_id, IS_ACTIVE, ru, ip, "1", null, null);
    }

    public CookieApiParams(String userid, int client_id, String ru, String ip, String uniqname, String refnick) {
        this(userid, client_id, IS_ACTIVE, ru, ip, "1", uniqname, refnick);
    }

    public CookieApiParams(String userid, int client_id, int trust, String ru, String ip, String persistentcookie, String uniqname, String refnick) {
        this.userid = userid;
        this.client_id = client_id;
        this.trust = trust;
        this.ru = ru;
        this.ip = ip;
        this.persistentcookie = persistentcookie;
        this.uniqname = uniqname;
        this.refnick = refnick;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getTrust() {
        return trust;
    }

    public void setTrust(int trust) {
        this.trust = trust;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPersistentcookie() {
        return persistentcookie;
    }

    public void setPersistentcookie(String persistentcookie) {
        this.persistentcookie = persistentcookie;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }

    public String getRefnick() {
        return refnick;
    }

    public void setRefnick(String refnick) {
        this.refnick = refnick;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getCreateAndSet() {
        return createAndSet;
    }

    public void setCreateAndSet(int createAndSet) {
        this.createAndSet = createAndSet;
    }
}
