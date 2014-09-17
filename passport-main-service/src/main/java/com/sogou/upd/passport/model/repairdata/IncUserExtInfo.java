package com.sogou.upd.passport.model.repairdata;

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
    private String gender;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCreateip() {
        return createip;
    }

    public void setCreateip(String createip) {
        this.createip = createip;
    }
}
