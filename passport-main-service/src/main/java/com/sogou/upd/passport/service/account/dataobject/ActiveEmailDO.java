package com.sogou.upd.passport.service.account.dataobject;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;

/**
 * 构造激活邮件参数对象
 * 父类以web端为准
 * User: liuling
 * Date: 14-7-25
 * Time: 下午5:28
 * To change this template use File | Settings | File Templates.
 */
public class ActiveEmailDO {

    protected String passportId;  //激活邮件中的主账号

    protected int clientId; //应用id

    protected String ru; //回跳的ru

    protected AccountModuleEnum module;  //模块类型，例如：register、login、findpwd、security、userinfo等

    protected String toEmail;  //发送邮件的对象

    protected boolean saveEmail = false; //是否保存发送邮件对象与scode的对应关系,默认不保存，值为false，绑定邮箱是为true
    
    protected boolean rtp = true; // redirect to passport 是否跳转到 passport，若否则直接跳回 ru
    
    protected String lang;
    
    public ActiveEmailDO(String passportId, int clientId, String ru, AccountModuleEnum module, String toEmail, boolean saveEmail) {
        this.passportId = passportId;
        this.clientId = clientId;
        this.ru = ru;
        this.module = module;
        this.toEmail = toEmail;
        this.saveEmail = saveEmail;
    }
    
    public ActiveEmailDO(String passportId, int clientId, String ru, AccountModuleEnum module,
                         String toEmail, boolean saveEmail, boolean rtp, String lang) {
        this.passportId = passportId;
        this.clientId = clientId;
        this.ru = ru;
        this.module = module;
        this.toEmail = toEmail;
        this.saveEmail = saveEmail;
        this.rtp = rtp;
        this.lang = lang;
    }
    
    public String getPrefix() {
        return CommonConstant.DEFAULT_INDEX_URL;
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
  
    public boolean isRtp() {
        return rtp;
    }
    
    public void setRtp(boolean rtp) {
        this.rtp = rtp;
    }
    
    public String getLang() {
        return lang;
    }
    
    public void setLang(String lang) {
        this.lang = lang;
    }
}
