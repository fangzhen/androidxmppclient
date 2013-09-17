package com.sys.android.db;

import java.util.ArrayList;
import java.util.List;

import com.sys.android.entity.ChatMsg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper {
	private static DbHelper instance = null;

	private SqlLiteHelper helper;
	private SQLiteDatabase db;

	public DbHelper(Context context) {
		helper = new SqlLiteHelper(context);
		db = helper.getWritableDatabase();
	}

	public void closeDb(){
		db.close();
		helper.close();
	}
	public static DbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbHelper(context);
		}
		return instance;
	}
	
	private class SqlLiteHelper extends SQLiteOpenHelper {

		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "syslovefy";

		public SqlLiteHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			String sql = "CREATE TABLE  IF NOT EXISTS chat"
						+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, username text , type integer , body text)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropTable(db);
			onCreate(db);
		}

		private void dropTable(SQLiteDatabase db) {
			String sql = "DROP TABLE IF EXISTS chat";
			db.execSQL(sql);
		}

	}
	
	public void saveChatMsg(ChatMsg msg){
		ContentValues values = new ContentValues();
		values.put("username", msg.getUsername());
		values.put("type", msg.getType());
		values.put("body", msg.getMsg());
		@SuppressWarnings("unused")
		long count = db.insert("chat", "_id", values);
	}
	

	public List<ChatMsg> getChatMsgByUserName(String username){
		List<ChatMsg> list = new ArrayList<ChatMsg>();
		ChatMsg msg;
		String sql = " select type,body from chat where username = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{username});
		while(cursor.moveToNext()){
			msg = new ChatMsg();
			msg.setType(cursor.getInt(0));
			msg.setMsg(cursor.getString(1));
			msg.setUsername(username);
			list.add(msg);
			msg = null;
		}
		cursor.close();
		
		return list;
	}
}
