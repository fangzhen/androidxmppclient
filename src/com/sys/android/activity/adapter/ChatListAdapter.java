package com.sys.android.activity.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sys.android.entity.Msg;
import com.sys.android.util.OpenfileFunction;
import com.sys.android.xmpp.R;

public class ChatListAdapter extends BaseAdapter {
	private Context cxt;
	private LayoutInflater inflater;
	private List<Msg> listMsg;

	public ChatListAdapter(Context formClient, List<Msg> list) {
		this.cxt = formClient;
		listMsg = list;
	}

	@Override
	public int getCount() {
		return listMsg.size();
	}

	@Override
	public Object getItem(int position) {
		return listMsg.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.inflater = (LayoutInflater) this.cxt
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (listMsg.get(position).getFrom().equals("IN")) {
			convertView = this.inflater.inflate(R.layout.formclient_chat_in,
					null);
		} else {
			convertView = this.inflater.inflate(R.layout.formclient_chat_out,
					null);
		}

		TextView useridView = (TextView) convertView
				.findViewById(R.id.formclient_row_userid);
		TextView dateView = (TextView) convertView
				.findViewById(R.id.formclient_row_date);
		TextView msgView = (TextView) convertView
				.findViewById(R.id.formclient_row_msg);
		useridView.setText(listMsg.get(position).getUserid());
		dateView.setText(listMsg.get(position).getDate());
		msgView.setText(listMsg.get(position).getMsg());

		if (!Msg.TYPE[2].equals(listMsg.get(position).getType())) {// normal 普通msg
			final Msg msg = listMsg.get(position);
			TextView msgStatus = (TextView) convertView
					.findViewById(R.id.msg_status);
			msgStatus.setText(listMsg.get(position).getReceive() + "");
			convertView.setOnClickListener(new OnClickListener() {// 点击查看
						@Override
						public void onClick(View v) {
							Intent intent = OpenfileFunction.openFile(msg
									.getFilePath());
							if (intent != null) {
								cxt.startActivity(intent);
							}

						}
					});
		} else {
			TextView msgStatus = (TextView) convertView
					.findViewById(R.id.msg_status);
			msgStatus.setVisibility(View.GONE);// 影藏
		}

		return convertView;
	}
}