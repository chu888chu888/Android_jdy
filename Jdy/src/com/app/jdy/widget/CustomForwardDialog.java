/**
 * Copyright (c) 2015
 *
 * Licensed under the UCG License, Version 1.0 (the "License");
 */
package com.app.jdy.widget;

import java.util.ArrayList;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jdy.R;
import com.app.jdy.ui.LoginActivity;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.Constants;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.URLs;
import com.app.jdy.utils.WxUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * description :
 * 
 * @version 1.0
 * @author zhonghuixiong
 * @createtime : 2015-1-26 下午12:06:28
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhonghuixiong 2015-1-26
 *             下午12:06:28
 * 
 */
public class CustomForwardDialog extends Dialog {
	private ImageView imageView_wetchar,imageView_wetchar1;
	private Context context;
	private Map<String, String> map;
	/**
	 * 微信API
	 */
	private IWXAPI api;
	private TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7,textView8;
	private boolean isTimeLine = true;
	private LinearLayout forward_layout;
	private LinearLayout top_layout;
	private boolean isVisibility = false;
	private TextView type_textview;

	public CustomForwardDialog(Context context) {
		super(context);
	}
	
	public CustomForwardDialog(Context context, int theme,Map<String, String> map,boolean isVisibility) {
		super(context, theme);
		this.context = context;
		this.map=map;
		this.isVisibility = isVisibility;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forward_dialog);
		imageView_wetchar = (ImageView) findViewById(R.id.imageView_wetchar);
		imageView_wetchar1 = (ImageView) findViewById(R.id.imageView_wetchar1);
		forward_layout = (LinearLayout)findViewById(R.id.forward_layout);
		top_layout = (LinearLayout)findViewById(R.id.top_layout);
		api = WXAPIFactory.createWXAPI(context,Constants.APP_ID);
		if (isVisibility==false) {
			forward_layout.setVisibility(View.GONE);
			top_layout.setBackgroundResource(R.drawable.forward_dialog_bg2);
		}
		initView();
		imageView_wetchar.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				if (CommonUtils.checkLogin(context)) {
					isTimeLine = true;
					new MyAsyncTask().execute();
					dismiss();
				}else {
					context.startActivity(new Intent(context,LoginActivity.class));
				}
			}
		});
		
		imageView_wetchar1.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.checkLogin(context)) {
					isTimeLine = false;
					new MyAsyncTask().execute();
					dismiss();
				}else {
					context.startActivity(new Intent(context,LoginActivity.class));
				}
			}
		});
	}
	
	/**
	 * 
	 * @author Owater
	 * @createtime 2015-1-26 下午7:08:10
	 * @Decription 初始化界面
	 *
	 */
	private void initView() {
		type_textview = (TextView)findViewById(R.id.type_textview);
		textView1 = (TextView)findViewById(R.id.forward_text1);
		textView2 = (TextView)findViewById(R.id.forward_text2);
		textView3 = (TextView)findViewById(R.id.forward_text3);
		textView4 = (TextView)findViewById(R.id.forward_text4);
		textView5 = (TextView)findViewById(R.id.forward_text5);
		textView6 = (TextView)findViewById(R.id.forward_text6);
		textView7 = (TextView)findViewById(R.id.product_desp);
		textView8 = (TextView)findViewById(R.id.product_name);
		type_textview.setText(map.get("proType"));
		textView1.setText(map.get("text1"));
		textView2.setText(map.get("text2"));
		textView3.setText(map.get("text3"));
		textView4.setText(map.get("text4"));
		textView5.setText(map.get("text5"));
		textView6.setText(map.get("text6"));
		textView6.setText(map.get("text6"));
		textView7.setText(map.get("text7"));
//		textView6.setText(map.get("text8"));
		textView8.setText(map.get("text8"));
	}

	// called when this dialog is dismissed
	protected void onStop() {
		Log.d("TAG", "+++++++++++++++++++++++++++");
	}
	
	/**
	 * 
	 * @author Owater
	 * @createtime 2015-1-25 下午4:14:15
	 * @Decription 微信发送
	 *
	 * @param id
	 */
	private void sendUrl(String rid){
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = URLs.WX_SHARE_RESULT_URL+"/"+map.get("ID")+"-"+map.get("prodTypeCode")+"-"+rid;
		WXMediaMessage msg = new WXMediaMessage(webpage);
//		msg.title = map.get("shareSubject");
//		msg.description = map.get("productSubject");
		msg.title = map.get("name");
		msg.description = map.get("shareSubject");
		Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		msg.thumbData = WxUtil.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = isTimeLine ? SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
	 * 
	 * description : 异步网络请求
	 *
	 * @version 1.0
	 * @author Owater
	 * @createtime : 2015-1-20 上午4:22:27
	 * 
	 * 修改历史:
	 * 修改人                                          修改时间                                                  修改内容
	 * --------------- ------------------- -----------------------------------
	 * Owater        2015-1-20 上午4:22:27
	 *
	 */
	private class MyAsyncTask extends AsyncTask<Void, Integer, Boolean>{
		
		private ArrayList<NameValuePair> params;
		private String resultid;

		@Override
		protected Boolean doInBackground(Void... arg0) {
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("productId", map.get("ID")));
			params.add(new BasicNameValuePair("memberId", map.get("memberId")));
			resultid = HttpUtils.request(params, URLs.WX_SHARE_URL);
			Constants.TMP_MEMBERID = map.get("memberId");
			Constants.TMP_PRODUCTID = map.get("ID");
			try {
				JSONObject jsonObject = new JSONObject(resultid);
				resultid = jsonObject.getString("ID");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
//				Toast.makeText(context, "resultid="+resultid, Toast.LENGTH_LONG).show();
				sendUrl(resultid);
			}else {
	            Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
	        }
		}
		
	}
}
