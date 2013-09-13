package com.sys.android.entity;

import java.io.Serializable;

import com.sys.android.xmppmanager.XmppConnection;

import android.text.TextUtils;


@SuppressWarnings("serial")
public class FriendInfo implements Serializable{
	private String username;
	private String nickname;
	private String mood;
	
	
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickname() {
		if(TextUtils.isEmpty(nickname))
			return username;
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getJid(){
		if (username == null) return null;
		return username + "@" +XmppConnection.SERVER_NAME;
	}
}
