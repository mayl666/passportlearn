package com.sogou.upd.passport.model.account;

/**
 * 账号信息表，包括绑定邮箱、密保问题、个人资料等
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-25 Time: 下午4:27 To change this template use
 * File | Settings | File Templates.
 */
public class AccountInfo {

    private long id;
    private String passportId;
    private String email;
    private String question;
    private String answer;

    public AccountInfo() {

    }

    public AccountInfo(String passportId) {
        this.passportId = passportId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
