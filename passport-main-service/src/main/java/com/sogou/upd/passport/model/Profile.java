package com.sogou.upd.passport.model;

import java.io.Serializable;
import java.util.Date;

public class Profile implements Serializable {

	private static final long serialVersionUID = -2165691146729293409L;

	public long id;
	public String nickname;
	public String imageURL;
	public int gender; // 0-女，1-男
	public Date updateTime;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getGender() {
		return gender;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
