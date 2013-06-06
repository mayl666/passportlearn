package com.sogou.upd.passport.oauth2.openresource.vo;

public class RenrenUserInfoVO {

	public int uid;
	public String name;
	public int sex;  // 表示性别，值1表示男性；值0表示女性
	public String birthday;  // 表示出生时间，格式为：yyyy-mm-dd，需要自行格式化日期显示格式。注：年份60后，实际返回1760-mm-dd；70后，返回1770-mm-dd；80后，返回1780-mm-dd；90后，返回1790-mm-dd
	public String tinyurl;  // 表示头像链接 50*50大小
	public String headurl;  // 表示头像链接 100*100大小
	public String mainurl;  // 表示头像链接 200*200大小
	public HomeTown hometown_location; // 表示家乡信息
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getTinyurl() {
		return tinyurl;
	}
	public void setTinyurl(String tinyurl) {
		this.tinyurl = tinyurl;
	}
	public String getHeadurl() {
		return headurl;
	}
	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}
	public String getMainurl() {
		return mainurl;
	}
	public void setMainurl(String mainurl) {
		this.mainurl = mainurl;
	}
	public HomeTown getHometown_location() {
		return hometown_location;
	}
	public void setHometown_location(HomeTown hometown_location) {
		this.hometown_location = hometown_location;
	}
	
	public class HomeTown{
		public String province;
		public String city;
		
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
	}
}
