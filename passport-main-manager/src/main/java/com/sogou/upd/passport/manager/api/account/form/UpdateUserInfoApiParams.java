package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ProvinceAndCityUtil;
import com.sogou.upd.passport.common.utils.UniqNameUtil;
import com.sogou.upd.passport.common.validation.constraints.IdCard;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import java.text.SimpleDateFormat;

/**
 * 用于更新用户基本信息接口参数
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午11:40
 */
public class UpdateUserInfoApiParams extends BaseUserApiParams {

    @NotBlank(message = "修改用户信息的ip地址不能为空")
    private String modifyip;

    //用户生日
//    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private String birthday;
    //用户性别
    private String gender;
    //省份
    private String province;
    //省份
    private String city;
    //昵称
    private String uniqname;
    //搜狗姓名
    private String fullname;
    //搜狐姓名
    private String username;

    //身份证号
    @IdCard
    private String personalid;

    @AssertTrue(message = "用户昵称格式不正确!")
    private boolean isCheckUinqName() {

        if (Strings.isNullOrEmpty(uniqname)) {
            return true;
        } else {
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
        if (Strings.isNullOrEmpty(modifyip)) {
            return true;
        } else {
            String regx = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
            boolean flag = modifyip.matches(regx);
            return flag;
        }
    }

    @AssertTrue(message = "性别参数错误！")
    private boolean isGender() {
        if (Strings.isNullOrEmpty(gender)) {
            return true;
        } else {
            String regx = "^(1|2)$";
            if (!gender.matches(regx)) {
                return false;
            }
        }
        return true;
    }


    @AssertTrue(message = "生日参数错误！")
    private boolean isCheckBirthday() {
        if (StringUtils.isNotEmpty(birthday)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(birthday);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


    @AssertTrue(message = "省市参数错误！")
    private boolean isCheckProvinceAndCity() {
        if (StringUtils.isNotEmpty(province)) {
            if (Strings.isNullOrEmpty(ProvinceAndCityUtil.immutableProvinceMap.get(province))) {
                return false;
            }
        }
        if (StringUtils.isNotEmpty(city)) {
            if (Strings.isNullOrEmpty(ProvinceAndCityUtil.immutableCityMap.get(city))) {
                return false;
            }
        }
        if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
            if (!StringUtils.substring(province, 0, 2).equals(StringUtils.substring(city, 0, 2))) {
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

    public String getPersonalid() {
        return personalid;
    }

    public void setPersonalid(String personalid) {
        this.personalid = personalid;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
