package com.sogou.upd.passport.model.connect;

/**
 * 第三方用户信息.
 * User: nahongxu
 * Date: 15-1-7
 * Time: 下午6:11
 * To change this template use File | Settings | File Templates.
 */
public class OriginalConnectInfo {

    private String connectUniqname;  // 第三方昵称
    private String avatarSmall;   // 第三方头像（小图）
    private String avatarMiddle;  // 第三方头像（中图）
    private String avatarLarge;  // 第三方头像（大图）
    private String gender;   // 性别。 0-女，1-男，默认为1

    public OriginalConnectInfo(ConnectToken connectToken) {
        this.connectUniqname = connectToken.getConnectUniqname();
        this.avatarLarge = connectToken.getAvatarLarge();
        this.avatarSmall = connectToken.getAvatarSmall();
        this.avatarMiddle = connectToken.getAvatarMiddle();
        this.gender = connectToken.getGender();

    }

    public OriginalConnectInfo(){

    }

    public String getConnectUniqname() {
        return connectUniqname;
    }

    public void setConnectUniqname(String connectUniqname) {
        this.connectUniqname = connectUniqname;
    }

    public String getAvatarSmall() {
        return avatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        this.avatarSmall = avatarSmall;
    }

    public String getAvatarMiddle() {
        return avatarMiddle;
    }

    public void setAvatarMiddle(String avatarMiddle) {
        this.avatarMiddle = avatarMiddle;
    }

    public String getAvatarLarge() {
        return avatarLarge;
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
