package com.sogou.upd.passport.model.repairdata;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-15
 * Time: 下午6:35
 * To change this template use File | Settings | File Templates.
 */
public class IncUserExtInfo {

    private String inc_type;
    private String userid;
    private String question;
    private String answer;
    private String username;
    private Date birthday;
    private String gender;
    private Date createtime;
    private String createip;

    public String getInc_type() {
        return inc_type;
    }

    public void setInc_type(String inc_type) {
        this.inc_type = inc_type;
    }

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getCreateip() {
        return createip;
    }

    public void setCreateip(String createip) {
        this.createip = createip;
    }
}
