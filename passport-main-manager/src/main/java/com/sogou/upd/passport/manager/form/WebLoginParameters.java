package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * 用于web端的登陆的参数
 * User: liagng201716@sogou-inc.com
 * Date: 13-5-12
 * Time: 下午10:01
 */
public class WebLoginParameters {

    /**
     * 登陆用户名
     */
    @Length(min = 1, max = 200, message = "用户名或密码错误，请重新输入！")
    @NotBlank(message = "请输入用户名！")
    private String account;


    /**
     * 登陆密码
     */
    @Length(min = 1, max = 200, message = "用户名或密码错误，请重新输入！")
    @NotBlank(message = "请输入密码！")
    private String password;


    /**
     * 是否自动登陆，自动登陆cookie时长设置两周
     */
    private boolean autoLogin;

    /**
     * 验证码
     * 用户连续3次登陆失败需要输入验证码
     */
    private String captcha;

    /**
     * 账户类型
     */
    private AccountDomainEnum accountDomainEnum;


    /**
     * 检查账户类型
     *
     * @return
     */
    @AssertTrue(message = "用户名或密码错误，请重新输入!")
    private boolean isValidAccountDomainEnum() {
        AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(this.getAccount());
        if (accountDomainEnum == AccountDomainEnum.UNKNOWN) {
            return false;
        }
        this.setAccountDomainEnum(accountDomainEnum);
        return true;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        if (account != null) {
            account = account.trim();
        }
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            password = password.trim();
        }
        this.password = password;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public AccountDomainEnum getAccountDomainEnum() {
        return accountDomainEnum;
    }

    public void setAccountDomainEnum(AccountDomainEnum accountDomainEnum) {
        this.accountDomainEnum = accountDomainEnum;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
