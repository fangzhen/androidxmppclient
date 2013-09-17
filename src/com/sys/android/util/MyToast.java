package com.sys.android.util;

import android.content.Context;
import android.widget.Toast;

/**
 * ×Ô¶¨ÒåToast
 */
public class MyToast {
	
private static Toast toast;
	
	public static void showToast(Context context,String msg){
		
		if(toast == null){
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		}else{
			toast.setText(msg);
		}
			toast.show();
	}
	public static void showToast(Context context,int resId){
		
		if(toast == null){
			toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		}else{
			toast.setText(resId);
		}
		toast.show();
	}
	public static void showToastLong(Context context,int resId){
		
		if(toast == null){
			toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
		}else{
			toast.setText(resId);
		}
		toast.show();
	}
}
