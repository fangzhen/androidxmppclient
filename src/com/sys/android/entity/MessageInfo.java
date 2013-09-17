package com.sys.android.entity;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sys.android.util.TimeRender;

 
public class MessageInfo {
	String userid;
	String msg;
	String date;
	String from;
	String type  =TYPE[2];//类型 normal 普通消息
	String receive;// 接收
	String time;//语音时长
	String filePath;
	
	public static final String USERID ="userid";
	public static final String MSG_CONTENT ="msg";//消息内容
	public static final String DATE ="date";
	public static final String FROM ="from";
	public static final String MSG_TYPE ="type";
	public static final String RECEIVE_STAUTS="receive";// 接收状态
	public static final String TIME_REDIO="time";
	public static final String FIL_PAHT="filePath";
	
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public static final String[] STATUS={"success","refused","fail","wait"};
	public static final String[] TYPE= {"record","photo","normal"};
	public static final String[] FROM_TYPE= {"IN","OUT"};

	public MessageInfo(){
		
	}
	public MessageInfo(String userid, String msg, String date, String from) {
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
	}
	
	 
	 

	public MessageInfo(String userid, String msg, String date, String from,
			String type, String receive, String time, String filePath) {
		super();
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
		this.type = type;
		this.receive = receive;
		this.time = time;
		this.filePath = filePath;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Msg [userid=" + userid + ", msg=" + msg + ", date=" + date
				+ ", from=" + from + ", type=" + type + ", receive=" + receive
				+ ", time=" + time + ", filePath=" + filePath + "]";
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReceive() {
		return receive;
	}

	public void setReceive(String receive) {
		this.receive = receive;
	}

	public static String[] getStatus() {
		return STATUS;
	}

	public MessageInfo(String userid, String msg, String date, String from,
			String type, String receive) {
		super();
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
		this.type = type;
		this.receive = receive;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * 分析消息内容
	 * @param body
	 * Json
	 */
	public static MessageInfo analyseMsgBody(String jsonStr) {
		MessageInfo msg = new MessageInfo();
		// 获取用户、消息、时间、IN
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			msg.setUserid(jsonObject.getString(MessageInfo.USERID));
			msg.setFrom(jsonObject.getString(MessageInfo.FROM));
			msg.setMsg(jsonObject.getString(MessageInfo.MSG_CONTENT));
			msg.setDate(jsonObject.getString(MessageInfo.DATE));
			msg.setType(jsonObject.getString(MessageInfo.MSG_TYPE));
			msg.setReceive(jsonObject.getString(MessageInfo.RECEIVE_STAUTS));
			msg.setTime(jsonObject.getString(MessageInfo.TIME_REDIO));
			msg.setFilePath(jsonObject.getString(MessageInfo.FIL_PAHT));
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			return msg;
		}
		
		
		 
		
		 
		 
	}
	
	
	/**
	 * 传json 
	 */
	public static  String  toJson(MessageInfo msg){
		JSONObject jsonObject=new JSONObject();
		String jsonStr="";
		try {
			jsonObject.put(MessageInfo.USERID, msg.getUserid()+"");
			jsonObject.put(MessageInfo.MSG_CONTENT, msg.getMsg()+"");
			jsonObject.put(MessageInfo.DATE, msg.getDate()+"");
			jsonObject.put(MessageInfo.FROM, msg.getFrom()+"");
			jsonObject.put(MessageInfo.MSG_TYPE, msg.getType()+"");
			jsonObject.put(MessageInfo.RECEIVE_STAUTS, msg.getReceive()+"");
			jsonObject.put(MessageInfo.TIME_REDIO, msg.getTime());
			jsonObject.put(MessageInfo.FIL_PAHT, msg.getFilePath());
			jsonStr= jsonObject.toString();
			Log.d("msg json", jsonStr+""); 
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			return jsonStr;
		}
	}
	
}