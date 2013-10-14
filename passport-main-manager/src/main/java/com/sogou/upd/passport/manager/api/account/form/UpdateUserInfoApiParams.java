package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ProvinceAndCityUtil;
import com.sogou.upd.passport.common.utils.UniqNameUtil;
import com.sogou.upd.passport.common.validation.constraints.IdCard;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import java.text.SimpleDateFormat;

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
//    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private String birthday;

    //用户性别
    private String gender;

    //省份
    private Integer province;

    //省份
    private Integer city;

    //昵称
    private String uniqname;

    //搜狗姓名
    private String fullname;
    //搜狐姓名
    private String username;

    //身份证号
    @IdCard
    private String personalId;

    @AssertTrue(message = "用户昵称格式不正确!")
    private boolean isCheckUinqName() {

        if(Strings.isNullOrEmpty(uniqname)){
            return true;
        } else{
            UniqNameUtil uniqNameUtil = new UniqNameUtil();
            if (!uniqNameUtil.checkUniqNameIsCorrect(uniqname)) {
                return false;
            } else {
                return true;
            }
        }
    }


    @AssertTrue(message = "IP参数错误！")
    private boolean isIp() {
        if(Strings.isNullOrEmpty(modifyip)){
            return true;
        }else{
            String regx = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
            boolean flag = modifyip.matches(regx);
            return flag;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @AssertTrue(message = "性别参数错误！")
    private boolean isGender(){
        if(Strings.isNullOrEmpty(gender)){
            return true;
        }else {
            String regx = "^(1|2)$";
            if (!gender.matches(regx)) {
                return false;
            }
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @AssertTrue(message = "生日参数错误！")
    private boolean isCheckBirthday() {
        if(birthday!=null){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            try{
                sdf.parse(birthday);
            }catch (Exception e){
                return false;
            }
        }
        return true;
    }


    @AssertTrue(message = "省市参数错误！")
    private boolean isCheckProvinceAndCity() {
        if(province !=null ){
            if (Strings.isNullOrEmpty(ProvinceAndCityUtil.provinceMap.get(String.valueOf(province)))) {
                return false;
            }
        }
        if(city !=null){
            if (Strings.isNullOrEmpty(ProvinceAndCityUtil.cityMap.get(String.valueOf(city)))) {
                return false;
            }
        }

        if (province !=null && city!=null) {
            String subProvince = province.toString().substring(0, 2);
            String subCity = city.toString().substring(0, 2);
            if (!subProvince.equals(subCity)) {
                return false;
            }
        }
        return true;
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

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
