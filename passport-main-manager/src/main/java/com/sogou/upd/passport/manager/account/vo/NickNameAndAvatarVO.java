package com.sogou.upd.passport.manager.account.vo;

/**
 * 昵称和头像VO，用于AccountInfoManager的getUserNickNameAndAvatar
 * User: shipengzhi
 * Date: 14-7-10
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */
public class NickNameAndAvatarVO {

    private String large_avatar = "";  // 返回结果的json要求空字符串，不是null
    private String mid_avatar = "";
    private String tiny_avatar = "";
    private String uniqname = "";

    public String getLarge_avatar() {
        return large_avatar;
    }

    public void setLarge_avatar(String large_avatar) {
        this.large_avatar = large_avatar;
    }

    public String getMid_avatar() {
        return mid_avatar;
    }

    public void setMid_avatar(String mid_avatar) {
        this.mid_avatar = mid_avatar;
    }

    public String getTiny_avatar() {
        return tiny_avatar;
    }

    public void setTiny_avatar(String tiny_avatar) {
        this.tiny_avatar = tiny_avatar;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }
}
