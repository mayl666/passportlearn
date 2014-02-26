package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * 账号信息表，包括绑定邮箱、密保问题、个人资料
 * User: mayan
 * Date: 13-8-8
 * Time: 下午9:50
 */
public class AccountInfo {

    private long id;
    private String passportId;
    private String email;
    private String question;
    private String answer;
    private String modifyip;
    //用户生日
    private Date birthday;
    //用户性别
    private String gender;
    //省份
    private String province;
    //城市
    private String city;
    //姓名
    private String fullname;
    //身份证号
    private String personalid;
    //记录修改时间
    private Date updateTime;
    //记录创建时间
    private Date createTime;

    public AccountInfo(String passportId) {
        this.passportId = passportId;
    }

    public AccountInfo() { }


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

    public String getModifyip() {
        return modifyip;
    }

    public void setModifyip(String modifyip) {
        this.modifyip = modifyip;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPersonalid() {
        return personalid;
    }

    public void setPersonalid(String personalid) {
        this.personalid = personalid;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
