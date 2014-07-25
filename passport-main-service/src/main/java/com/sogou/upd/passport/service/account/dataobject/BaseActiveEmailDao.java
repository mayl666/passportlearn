package com.sogou.upd.passport.service.account.dataobject;

import com.sogou.upd.passport.common.parameter.AccountClientEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-25
 * Time: 上午10:22
 * To change this template use File | Settings | File Templates.
 */
public class BaseActiveEmailDao {
    /**
     * 激活邮件中的主账号
     */
    private String passportId;

    /**
     * 应用id
     */
    private String clientId;

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

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
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
