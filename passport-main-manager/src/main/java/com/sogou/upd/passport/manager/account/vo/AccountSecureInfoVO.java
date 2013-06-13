package com.sogou.upd.passport.manager.account.vo;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-31 Time: 下午3:28 To change this template use
 * File | Settings | File Templates.
 *
 * 安全信息返回值，包括绑定手机、绑定邮箱、注册邮箱、绑定密保问题
 */
public class AccountSecureInfoVO {

    protected String sec_mobile;
    protected String sec_email;
    protected String reg_email;
    protected String sec_ques;
    protected int sec_score;
    protected long last_login;

    public String getSec_mobile() {
        return sec_mobile;
    }

    public void setSec_mobile(String sec_mobile) {
        this.sec_mobile = sec_mobile;
    }

    public String getSec_email() {
        return sec_email;
    }

    public void setSec_email(String sec_email) {
        this.sec_email = sec_email;
    }

    public String getReg_email() {
        return reg_email;
    }

    public void setReg_email(String reg_email) {
        this.reg_email = reg_email;
    }

    public String getSec_ques() {
        return sec_ques;
    }

    public void setSec_ques(String sec_ques) {
        this.sec_ques = sec_ques;
    }

    public int getSec_score() {
        return sec_score;
    }

    public void setSec_score(int sec_score) {
        this.sec_score = sec_score;
    }

    public void setLast_login(long last_login) {
        this.last_login = last_login;
    }

    public long getLast_login() {
        return last_login;
    }
}
