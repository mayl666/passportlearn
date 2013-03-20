package com.sogou.upd.passport.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class UserProfile extends Profile {

	private static final long serialVersionUID = -7008172497003639439L;
	
	public String openid;
	public long userid;
	public long appid;
	public String userDesc;
	public String realName;// 真实姓名
	public String realCode;// 身份证号
	public String province; // 省
	public String city; // 市
	public String region; // 区
	public int birthdayYear;
	public int birthdayMonth;
	public int birthdayDay;
	public int provider;// 冗余字段
	public String regIP;// 冗余字段
	public Date createTime;

	public int status = 1;// 默认为正式用户，冗余字段，统计和m2都需要，因此冗余，通user的status

	public String account;// 冗余字段，为了获取用户资料时，获取手机号方便

	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public long getAppid() {
		return appid;
	}
	public void setAppid(long appid) {
		this.appid = appid;
	}
	public String getUserDesc() {
		return userDesc;
	}
	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getRealCode() {
		return realCode;
	}
	public void setRealCode(String realCode) {
		this.realCode = realCode;
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
	public int getBirthdayYear() {
		return birthdayYear;
	}
	public void setBirthdayYear(int birthdayYear) {
		this.birthdayYear = birthdayYear;
	}
	public int getBirthdayMonth() {
		return birthdayMonth;
	}
	public void setBirthdayMonth(int birthdayMonth) {
		this.birthdayMonth = birthdayMonth;
	}
	public int getBirthdayDay() {
		return birthdayDay;
	}
	public void setBirthdayDay(int birthdayDay) {
		this.birthdayDay = birthdayDay;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getProvider() {
		return provider;
	}
	public void setProvider(int provider) {
		this.provider = provider;
	}
	public String getRegIP() {
		return regIP;
	}
	public void setRegIP(String regIP) {
		this.regIP = regIP;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
