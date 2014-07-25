package com.sogou.upd.passport.service.account.dataobject;

import com.sogou.upd.passport.common.parameter.AccountClientEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-25
 * Time: 下午5:28
 * To change this template use File | Settings | File Templates.
 */
public class ActiveEmailDO {
    /**
     * 激活邮件中的主账号
     */
    private String passportId;

    /**
     * 应用id
     */
    private int clientId;

    /**
     * 回跳的ru
     */
    private String ru;

    /**
     * 客户端类型
     */
    private AccountClientEnum clientEnum;

    /**
     * 模块类型
     */
    private AccountModuleEnum module;

    /**
     * 发送邮件的对象
     */
    private String toEmail;

    /**
     * 是否保存发送邮件对象与scode的对应关系,默认不保存，值为false
     */
    private boolean saveEmail = false;

    /**
     * wap页面的皮肤值,red-红色；默认green，绿色
     */
    private String skin;

    /**
     * wap版本:1-简易版；2-炫彩版；5-触屏版  0-返回json数据。此接口值为5。
     */
    private String v;

    public ActiveEmailDO() {
    }

    public ActiveEmailDO(String passportId, int clientId, String ru, AccountClientEnum clientEnum, AccountModuleEnum module, String toEmail, boolean saveEmail, String skin, String v) {
        this.passportId = passportId;
        this.clientId = clientId;
        this.ru = ru;
        this.clientEnum = clientEnum;
        this.module = module;
        this.toEmail = toEmail;
        this.saveEmail = saveEmail;
        this.skin = skin;
        this.v = v;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public AccountClientEnum getClientEnum() {
        return clientEnum;
    }

    public void setClientEnum(AccountClientEnum clientEnum) {
        this.clientEnum = clientEnum;
    }

    public AccountModuleEnum getModule() {
        return module;
    }

    public void setModule(AccountModuleEnum module) {
        this.module = module;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public boolean isSaveEmail() {
        return saveEmail;
    }

    public void setSaveEmail(boolean saveEmail) {
        this.saveEmail = saveEmail;
    }
}
