package com.sys.android.activity;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sys.android.activity.adapter.ChatListAdapter;
import com.sys.android.entity.MessageInfo;
import com.sys.android.selfview.RecordButton;
import com.sys.android.selfview.RecordButton.OnFinishedRecordListener;
import com.sys.android.util.TimeRender;
import com.sys.android.xmpp.R;
import com.sys.android.xmppmanager.XmppConnection;

public class ChatActivity extends Activity {

	private String userChat = "";// 当前聊天 userChat
	private String userChatSendFile = "";// 给谁发文件
	private ChatListAdapter adapter;
	private List<MessageInfo> listMsg = new LinkedList<MessageInfo>();
	private String pUSERID;// 自己的user
	private String pFRIENDID;// 窗口的 名称
	private EditText msgText;
	private TextView chat_name;
	private NotificationManager mNotificationManager;
	private ChatManager cm;
	private RecordButton mRecordButton;

	// 发送文件
	private OutgoingFileTransfer sendTransfer;
	public static String FILE_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/chat/file";
	public static String RECORD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/chat/record";
	Chat newchat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_client);

		init();

		mRecordButton = (RecordButton) findViewById(R.id.record_button);

		String path = RECORD_ROOT_PATH;
		File file = new File(path);
		file.mkdirs();
		path += "/" + System.currentTimeMillis() + ".amr";
		mRecordButton.setSavePath(path);
		mRecordButton
				.setOnFinishedRecordListener(new OnFinishedRecordListener() {

					@Override
					public void onFinishedRecord(String audioPath, int time) {
						Log.i("RECORD!!!", "finished!!!!!!!!!! save to "
								+ audioPath);

						if (audioPath != null) {
							try {
								// 自己显示消息
								MessageInfo myChatMsg = new MessageInfo(pUSERID,
										time + "”语音消息", TimeRender.getDate(),
										MessageInfo.FROM_TYPE[1], MessageInfo.TYPE[0],
										MessageInfo.STATUS[3], time + "", audioPath);
								listMsg.add(myChatMsg);
								String[] pathStrings = audioPath.split("/"); // 文件名

								// 发送 对方的消息
								String fileName = null;
								if (pathStrings != null
										&& pathStrings.length > 0) {
									fileName = pathStrings[pathStrings.length - 1];
								}
								MessageInfo sendChatMsg = new MessageInfo(pUSERID, time
										+ "”语音消息", TimeRender.getDate(),
										MessageInfo.FROM_TYPE[0], MessageInfo.TYPE[0],
										MessageInfo.STATUS[3], time + "", fileName);

								// 刷新适配器
								adapter.notifyDataSetChanged();

								// 发送消息
								newchat.sendMessage(MessageInfo.toJson(sendChatMsg));
								sendFile(audioPath, myChatMsg);//
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(ChatActivity.this, "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					}
				});

	}

	private void init() {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Service.NOTIFICATION_SERVICE);
		// 获取Intent传过来的用户名
		this.pUSERID = getIntent().getStringExtra("USERID");
		this.userChat = getIntent().getStringExtra("user");/*
															 * + "/" +
															 * FriendListActivity
															 * .RESOUCE_NAME;
															 */
		userChatSendFile = userChat + "/" + FriendListActivity.MY_RESOUCE_NAME;
		this.pFRIENDID = getIntent().getStringExtra("FRIENDID");
		/*
		 * System.out.println("接收消息的用户pFRIENDID是：" + userChat);
		 * System.out.println("发送消息的用户pUSERID是：" + pUSERID);
		 * System.out.println(" 消息的用户pFRIENDID是：" + pFRIENDID);
		 */

		chat_name = (TextView) findViewById(R.id.chat_name);
		chat_name.setText(pFRIENDID);
		ListView listview = (ListView) findViewById(R.id.formclient_listview);
		listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		this.adapter = new ChatListAdapter(this, listMsg);
		listview.setAdapter(adapter);
		// 获取文本信息
		this.msgText = (EditText) findViewById(R.id.formclient_text);
		// 消息监听
		cm = XmppConnection.getConnection().getChatManager();

		// 返回按钮
		Button mBtnBack = (Button) findViewById(R.id.chat_back);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

//		receivedMsg();// 接收消息
		textMsg();// 发送+接受消息
		receivedFile();// 接收文件

	}

	/**
	 * 接收消息
	 */
	public void receivedMsg() {

		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat chat2, Message message) {
						// 收到来自pc服务器的消息（获取自己好友发来的信息）
						if (message.getFrom().contains(userChat)) {
							// Msg.analyseMsgBody(message.getBody(),userChat);
							// 获取用户、消息、时间、IN
							/*
							 * String[] args = new String[] { userChat,
							 * message.getBody(), TimeRender.getDate(), "IN" };
							 */
							// 在handler里取出来显示消息
							android.os.Message msg = handler.obtainMessage();
							System.out.println("服务器发来的消息是 chat："
									+ message.getBody());
							msg.what = 1;
							msg.obj = message.getBody();
							msg.sendToTarget();

						}
					}
				});
			}
		});
	}

	/**
	 * 发送消息
	 * 
	 * @author Administrator
	 * 
	 */
	public void textMsg() {
		// 发送消息
		Button btsend = (Button) findViewById(R.id.formclient_btsend);
		// 发送消息给pc服务器的好友（获取自己的服务器，和好友）
		//TODO:应先检测是否有未读消息
		newchat = cm.createChat(userChat, new MessageListener() {
		    public void processMessage(Chat chat, Message message) {
		        System.out.println("Received message: " + message);
				MessageInfo chatMsg = new MessageInfo(pUSERID, message.getBody(), TimeRender.getDate(),
						MessageInfo.FROM_TYPE[0]); 
				listMsg.add(chatMsg);
				// 刷新适配器 费UI线程不能直接更新
					ChatActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();						
						}
					});
		    }
		});


		btsend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取text文本
				final String msg = msgText.getText().toString();
				if (msg.length() > 0) {
					// 自己显示消息
					MessageInfo chatMsg = new MessageInfo(pUSERID, msg, TimeRender.getDate(),
							MessageInfo.FROM_TYPE[1]);
					listMsg.add(chatMsg);
					// 刷新适配器
					adapter.notifyDataSetChanged();
					try {
						// 发送消息
						Message toSend = new Message();
						toSend.setBody(msg);
						newchat.sendMessage(toSend);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(ChatActivity.this, "发送信息不能为空",
							Toast.LENGTH_SHORT).show();
				}
				// 清空text
				msgText.setText("");
			}
		});
	}

	/**
	 * 接收文件
	 * 
	 * @author Administrator
	 * 
	 */
	public void receivedFile() {
		/**
		 * 接收文件
		 */
		// Create the file transfer manager
		final FileTransferManager manager = new FileTransferManager(
				XmppConnection.getConnection());
		// Create the listener
		manager.addFileTransferListener(new FileTransferListener() {

			public void fileTransferRequest(FileTransferRequest request) {
				// Check to see if the request should be accepted
				Log.d("receivedFile ", " receive file");
				if (shouldAccept(request)) {
					// Accept it
					IncomingFileTransfer transfer = request.accept();
					try {

						System.out.println(request.getFileName());
						File file = new File(RECORD_ROOT_PATH
								+ request.getFileName());

						android.os.Message msg = handler.obtainMessage();
						transfer.recieveFile(file);
						MessageInfo msgInfo = queryMsgForListMsg(file.getName());
						msgInfo.setFilePath(file.getPath());// 更新 filepath
						new MyFileStatusThread(transfer, msgInfo).start();

					} catch (XMPPException e) {
						e.printStackTrace();
					}
				} else {
					// Reject it
					request.reject();
					String[] args = new String[] { userChat,
							request.getFileName(), TimeRender.getDate(), "IN",
							MessageInfo.TYPE[0], MessageInfo.STATUS[1] };
					MessageInfo msgInfo = new MessageInfo(args[0], "redio", args[2], args[3],
							MessageInfo.TYPE[0], MessageInfo.STATUS[1]);
					// 在handler里取出来显示消息
					android.os.Message msg = handler.obtainMessage();
					msg.what = 5;
					msg.obj = msgInfo;
					handler.sendMessage(msg);
				}
			}
		});
	}

	/**
	 * 发送文件
	 * 
	 * @param path
	 */
	public void sendFile(String path, MessageInfo msg) {
		/**
		 * 发送文件
		 */
		// Create the file transfer manager
		FileTransferManager sendFilemanager = new FileTransferManager(
				XmppConnection.getConnection());

		// Create the outgoing file transfer
		sendTransfer = sendFilemanager
				.createOutgoingFileTransfer(userChatSendFile);
		// Send the file
		try {

			sendTransfer.sendFile(new java.io.File(path), "send file");
			new MyFileStatusThread(sendTransfer, msg).start();
			/**
			 * 监听
			 */
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	class MyFileStatusThread extends Thread {
		private FileTransfer transfer;
		private MessageInfo msg;

		public MyFileStatusThread(FileTransfer tf, MessageInfo msg) {
			transfer = tf;
			this.msg = msg;
		}

		public void run() {
			System.out.println(transfer.getStatus());
			System.out.println(transfer.getProgress());
			android.os.Message message = new android.os.Message();// handle
			message.what = 3;
			while (!transfer.isDone()) {
				System.out.println(transfer.getStatus());
				System.out.println(transfer.getProgress());

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (transfer.getStatus().equals(Status.error)) {
				msg.setReceive(MessageInfo.STATUS[2]);
			} else if (transfer.getStatus().equals(Status.refused)) {
				msg.setReceive(MessageInfo.STATUS[1]);
			} else {
				msg.setReceive(MessageInfo.STATUS[0]);// 成功

			}

			handler.sendMessage(message);
			/*
			 * System.out.println(transfer.getStatus());
			 * System.out.println(transfer.getProgress());
			 */
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MessageInfo chatMsg = MessageInfo.analyseMsgBody(msg.obj.toString());
				if (chatMsg != null) {
					listMsg.add(chatMsg);// 添加到聊天消息
					adapter.notifyDataSetChanged();
				}

				break;
			case 2: // 发送文件

				break;
			case 3: // 更新文件发送状态
				adapter.notifyDataSetChanged();
				break;
			case 5: // 接收文件
				MessageInfo msg2 = (MessageInfo) msg.obj;
				System.out.println(msg2.getFrom());
				listMsg.add(msg2);
				adapter.notifyDataSetChanged();
			default:
				break;
			}
		};
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// XmppConnection.closeConnection();
		System.exit(0);
	}

	protected void setNotiType(int iconId, String s) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent appIntent = PendingIntent.getActivity(this, 0, intent, 0);
		Notification myNoti = new Notification();
		myNoti.icon = iconId;
		myNoti.tickerText = s;
		myNoti.defaults = Notification.DEFAULT_SOUND;
		myNoti.flags |= Notification.FLAG_AUTO_CANCEL;
		myNoti.setLatestEventInfo(this, "QQ消息", s, appIntent);
		mNotificationManager.notify(0, myNoti);
	}

	/**
	 * 是否接收
	 * 
	 * @param request
	 * @return
	 */
	private boolean shouldAccept(FileTransferRequest request) {
		final boolean isAccept[] = new boolean[1];

		return true;
	}

	protected void dialog() {

	}

	/**
	 * init file
	 */
	static {
		File root = new File(FILE_ROOT_PATH);
		root.mkdirs();// 没有根目录创建根目录
		root = new File(RECORD_ROOT_PATH);
		root.mkdirs();
	}

	/**
	 * 从list 中取出 分拣名称相同的 Msg
	 */
	private MessageInfo queryMsgForListMsg(String filePath) {

		MessageInfo msg = null;
		for (int i = listMsg.size() - 1; i >= 0; i--) {
			msg = listMsg.get(i);
			if (filePath != null && filePath.contains(msg.getFilePath())) {// 对方传过来的只是文件的名称
				return msg;
			}
		}
		return msg;
	}
}