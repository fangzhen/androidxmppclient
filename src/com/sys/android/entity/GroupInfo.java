package com.sys.android.entity;

import java.util.List;

public class GroupInfo {
	private String groupName;
	private List<FriendInfo> friendInfoList;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<FriendInfo> getFriendInfoList() {
		return friendInfoList;
	}
	public void setFriendInfoList(List<FriendInfo> friendInfoList) {
		this.friendInfoList = friendInfoList;
	}
}
