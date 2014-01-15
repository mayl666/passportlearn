package com.sogou.upd.passport.oauth2.openresource.vo;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-29
 * Time: 下午8:55
 * To change this template use File | Settings | File Templates.
 */
public class ConnectUserInfoVO {

    private String nickname;
    public String imageURL;
    public String userDesc;
    public int gender; // 0-女，1-男
    public String province; // 省
    public String city; // 市
    public String region; // 区

    public Map<String, Object> original;//第三方返回的原始json串

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Map<String, Object> getOriginal() {
        return original;
    }

    public void setOriginal(Map<String, Object> original) {
        this.original = original;
    }
}
