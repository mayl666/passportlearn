package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;
import java.util.Map;

/**
 * 用于更新用户基本信息接口参数
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:40
 */
public class UpdateUserInfoApiParams extends BaseUserApiParams{

    @NotBlank(message = "修改用户信息的ip地址不能为空")
    private String modifyip;

    //用户生日
    private Date birthday;

    //用户性别
    private String gender;

    //省份
    private Integer province;

    //省份
    private Integer city;


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

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public String getModifyip() {
        return modifyip;
    }

    public void setModifyip(String modifyip) {
        this.modifyip = modifyip;
    }
}
