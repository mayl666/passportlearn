package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-5-23
 * Time: 上午12:40
 * To change this template use File | Settings | File Templates.
 */
public class UserExtInfoTmp {

    private String userid;
    private String question;
    private String answer;
    private String username;
    private String birthday;
    private String gender;
    private String createtime;
    private String createip;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCreateip() {
        return createip;
    }

    public void setCreateip(String createip) {
        this.createip = createip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserExtInfoTmp that = (UserExtInfoTmp) o;

        if (!answer.equals(that.answer)) return false;
        if (!birthday.equals(that.birthday)) return false;
        if (!createip.equals(that.createip)) return false;
        if (!createtime.equals(that.createtime)) return false;
        if (!gender.equals(that.gender)) return false;
        if (!question.equals(that.question)) return false;
        if (!userid.equals(that.userid)) return false;
        if (!username.equals(that.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userid.hashCode();
        result = 31 * result + question.hashCode();
        result = 31 * result + answer.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + birthday.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + createtime.hashCode();
        result = 31 * result + createip.hashCode();
        return result;
    }
}
