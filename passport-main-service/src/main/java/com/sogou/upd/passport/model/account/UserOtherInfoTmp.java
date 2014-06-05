package com.sogou.upd.passport.model.account;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-5-26
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
public class UserOtherInfoTmp {

    private String userid;
    private String personalid;
    private String mobile;
    private String mobileflag;
    private String email;
    private String emailflag;
    private String province;
    private String uniqname;
    private String city;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPersonalid() {
        return personalid;
    }

    public void setPersonalid(String personalid) {
        this.personalid = personalid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileflag() {
        return mobileflag;
    }

    public void setMobileflag(String mobileflag) {
        this.mobileflag = mobileflag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailflag() {
        return emailflag;
    }

    public void setEmailflag(String emailflag) {
        this.emailflag = emailflag;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserOtherInfoTmp that = (UserOtherInfoTmp) o;

        if (!city.equals(that.city)) return false;
        if (!email.equals(that.email)) return false;
        if (!emailflag.equals(that.emailflag)) return false;
        if (!mobile.equals(that.mobile)) return false;
        if (!mobileflag.equals(that.mobileflag)) return false;
        if (!personalid.equals(that.personalid)) return false;
        if (!province.equals(that.province)) return false;
        if (!uniqname.equals(that.uniqname)) return false;
        if (!userid.equals(that.userid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userid.hashCode();
        result = 31 * result + personalid.hashCode();
        result = 31 * result + mobile.hashCode();
        result = 31 * result + mobileflag.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + emailflag.hashCode();
        result = 31 * result + province.hashCode();
        result = 31 * result + uniqname.hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}
