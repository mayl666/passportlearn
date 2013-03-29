package com.sogou.upd.passport.oauth2.openresource.parameters;

public enum RenrenMethod {

	GET_USER_INFO("users.getInfo"), // 获取用户信息
	GET_FRIENDS("friends.getFriends"), // 获取好友
	PUBLISH_FEED("feed.publishFeed"); // 更新状态

	private String methodName;

	RenrenMethod(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public String toString() {
		return methodName;
	}
}
