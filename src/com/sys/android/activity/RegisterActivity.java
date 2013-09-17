package com.sys.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;

import com.sys.android.util.DialogFactory;
import com.sys.android.xmpp.R;
import com.sys.android.xmppmanager.XmppConnection;

@SuppressWarnings("all")
public class RegisterActivity extends Activity implements OnClickListener {

	private Button mBtnRegister;
	private Button mRegBack;
	private EditText mEmailEt, mNameEt, mPasswdEt, mPasswdEt2,nameMCH;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.register);
		mBtnRegister = (Button) findViewById(R.id.register_btn);
		mRegBack = (Button) findViewById(R.id.reg_back_btn);
		mBtnRegister.setOnClickListener(this);
		mRegBack.setOnClickListener(this);

		nameMCH = (EditText) findViewById(R.id.reg_nameMCH);
		mEmailEt = (EditText) findViewById(R.id.reg_email);
		mNameEt = (EditText) findViewById(R.id.reg_name);
		mPasswdEt = (EditText) findViewById(R.id.reg_password);
		mPasswdEt2 = (EditText) findViewById(R.id.reg_password2);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.reg_back_btn:
			login();
			break;
		case R.id.register_btn:
			registered();
			break;
		default:
			break;
		}
		
	}


	private void registered() {

		String accounts = mNameEt.getText().toString();
		String password = mPasswdEt.getText().toString();
		String email = mEmailEt.getText().toString();
		String mingcheng = nameMCH.getText().toString();
		
		
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(XmppConnection.getConnection().getServiceName());
		reg.setUsername(accounts);
		reg.setPassword(password);
		reg.addAttribute("name", mingcheng);
		reg.addAttribute("email", email);
		
		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(
		                                reg.getPacketID()), new PacketTypeFilter(
		                                IQ.class));
		PacketCollector collector = XmppConnection.getConnection().
		createPacketCollector(filter);
		XmppConnection.getConnection().sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration
		                                .getPacketReplyTimeout());
		                        // Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		if (result == null) {
		Toast.makeText(getApplicationContext(), "服务器没有返回结果", Toast.LENGTH_SHORT).show();
		} else if (result.getType() == IQ.Type.ERROR) {
		if (result.getError().toString()
		                        .equalsIgnoreCase("conflict(409)")) {
		    Toast.makeText(getApplicationContext(), "这个账号已经存在", Toast.LENGTH_SHORT).show();
		    } else {
		        Toast.makeText(getApplicationContext(), "注册失败",
		                                        Toast.LENGTH_SHORT).show();
		    }
		} else if (result.getType() == IQ.Type.RESULT) {
			try {
				XmppConnection.getConnection().login(accounts, password);
				Presence presence = new Presence(Presence.Type.available);
				XmppConnection.getConnection().sendPacket(presence);
				DialogFactory.ToastDialog(this, "QQ注册", "亲，恭喜你，注册成功了！");
				Intent intent = new Intent();
				intent.putExtra("USERID", accounts);
				intent.setClass(RegisterActivity.this, FriendListActivity.class);
				startActivity(intent);
			} catch (XMPPException e) {
				e.printStackTrace();
			}	
		}
		
	}

	private void login() {
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
	}
}