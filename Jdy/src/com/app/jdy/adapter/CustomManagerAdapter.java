/**
 * Copyright (c) 2015
 *
 * Licensed under the UCG License, Version 1.0 (the "License");
 */
package com.app.jdy.adapter;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jdy.R;
import com.app.jdy.entity.ProductManager;

/**
 * description :自定义优化ListViewAdapter
 * 
 * @version 1.0
 * @author zhonghuixiong
 * @createtime : 2015-1-22 下午5:45:48
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhonghuixiong 2015-1-22
 *             下午5:45:48
 * 
 */
public class CustomManagerAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<ProductManager> listViewProductManagerList;

	public CustomManagerAdapter(Context context,
			ArrayList<ProductManager> listViewProductManagerList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.listViewProductManagerList = listViewProductManagerList;
	}

	@Override
	public int getCount() {
		if (listViewProductManagerList == null) return 0;
		return listViewProductManagerList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null || convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.savemoney_dialog_listitem,
					null);
			/** 得到各个控件的对象 */
			final TextView tv_count = (TextView) convertView
					.findViewById(R.id.tv_count);
			final TextView tv_manager = (TextView) convertView
					.findViewById(R.id.tv_manager1);
			final TextView address = (TextView) convertView
					.findViewById(R.id.address);
			final ImageView imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);
			tv_count.setText(String.valueOf(position + 1) + "、");
			tv_manager.setText(listViewProductManagerList.get(position)
					.getManagerName());
			address.setText(listViewProductManagerList.get(position)
					.getAddress());
			address.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(
							context,
							listViewProductManagerList.get(position)
									.getAddress(), 10).show();
				}
			});
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:"
									+ listViewProductManagerList.get(position)
											.getManagerPhone()));
					try {
						context.startActivity(intent);
					} catch (SecurityException e) {
						// TODO: handle exception
					} catch (ActivityNotFoundException e) {
						// TODO: handle exception
					}
				}
			});
		}
		return convertView;
	}
}