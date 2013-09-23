package com.sys.android.entity;

import java.io.Serializable;


import android.text.TextUtils;


@SuppressWarnings("serial")
public class FriendInfo implements Serializable{
	private String userJid;
	private String nickname;
	private String mood;
	private int newMsgNum;
	
	public int getNewMsgNum() {
		return newMsgNum;
	}
	public void setNewMsgNum(int newMsgNum) {
		this.newMsgNum = newMsgNum;
	}
	public FriendInfo() {
	}
	public FriendInfo(String userJid, String nickname){
		this.userJid = userJid;
		this.nickname = nickname;
	}
	public String getUserJid() {
		return userJid;
	}
	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public String getNickname() {
		if(TextUtils.isEmpty(nickname))
			return userJid;
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getUsername(){
		if (userJid == null) return null;
		return userJid.substring(0, userJid.indexOf('@'));
	}
}
