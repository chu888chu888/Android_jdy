/**
 * Copyright (c) 2015
 *
 * Licensed under the UCG License, Version 1.0 (the "License");
 */
package com.app.jdy.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jdy.R;
import com.app.jdy.utils.Constants;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.StringUtils;
import com.app.jdy.utils.URLs;
import com.app.jdy.widget.WithdrawCashDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * description :
 * 
 * @version 1.0
 * @author liangdong
 * @createtime : 2015-2-3 上午11:14:17
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- liangdong 2015-2-3 上午11:14:17
 * 
 */
public class CashAdvanceActivity extends BaseActivity {

	// 会员可提现金额
	String canWithdCash;
	// 会员id
	String memberId;
	// 提现金额
	String money;
	// 银行卡号
	String bankCode1;

	Handler handler;
	ImageView backBtn;
	Button button;
	TextView textView;
	TextView textView1;
	TextView textView2;
	TextView textView4;
	TextView textView3;
	TextView textView5;
	EditText editText;
	ArrayList<NameValuePair> params;

	// 确认提现对话框
	WithdrawCashDialog withdrawCashDialog;
	// 确认提现广播接收
	BtnConfirmReceiver btnConfirmReceiver;
	// 向服务器申请提现的返回结果
	String withdrawresult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cashadvance);
		initView();

		// 注册广播接收相关，监听确认提现对话框回传
		btnConfirmReceiver = new BtnConfirmReceiver();
		IntentFilter intentFilter = new IntentFilter("org.jdy.action.WITHDCASH_BROADCAST");
		CashAdvanceActivity.this.registerReceiver(btnConfirmReceiver, intentFilter);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				// 首次进入提现界面的数据初始化
				case 1:
					Bundle bundle = msg.getData();

					canWithdCash = bundle.getString("canWithdCash");
					String bankName = bundle.getString("bankName");
					bankCode1 = bundle.getString("bankCode1");
					String bankCode = bundle.getString("bankCode");

					if (StringUtils.isNullOrEmpty(bankName)) {
						bankName = "暂无账号";
					}
					// 设置默认银行卡开户行
					textView4.setText(bankName);

					// 设置默认银行卡尾数
					textView3.setText("(尾数" + bankCode + ")");
					if (StringUtils.isNullOrEmpty(bankCode)) {
						textView3.setText("");
					}

					// 设置页面显示可提现金额数据
					textView2.setText(canWithdCash);

					// 设置输入框可提现金额数据
					editText.setText(canWithdCash);

					break;

				// 接收到确认提现框的取消操作，关闭对话框
				case 2:
					if (withdrawCashDialog != null) {
						withdrawCashDialog.dismiss();
					}
					break;

				// 向服务器申请提现成功，进行提示
				case 3:
					Toast.makeText(CashAdvanceActivity.this, "提交成功，还有 " + withdrawresult + "元 可以提现", Toast.LENGTH_LONG)
							.show();

					editText.setText("");
					if (withdrawCashDialog != null) {
						withdrawCashDialog.dismiss();
					}
					finish();

					break;

				// 向服务器申请提现失败，进行提示
				case 0x110:
					Toast.makeText(CashAdvanceActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

					if (withdrawCashDialog != null) {
						withdrawCashDialog.dismiss();
					}
					break;

				// 进入提现界面后，进行提现操作进入对话框，如果再次遇到网络异常，进行提示
				case 4:
					Toast.makeText(CashAdvanceActivity.this, Constants.NO_INTENT_TIPS, Toast.LENGTH_LONG).show();
					if (withdrawCashDialog != null) {
						withdrawCashDialog.dismiss();
					}
					break;
				default:
					break;
				}

			}
		};
		addEvents();
	}

	Thread withdcashthread = new Thread(new Runnable() {
		@Override
		public void run() {
			params = new ArrayList<NameValuePair>();

			money = editText.getText().toString();

			params.add(new BasicNameValuePair("memId", memberId));
			params.add(new BasicNameValuePair("withdCash", money));
			params.add(new BasicNameValuePair("bankCode", bankCode1));
			withdrawresult = HttpUtils.request(params, URLs.GETCASH_URL);
			if (!withdrawresult.equals("noenough") || !withdrawresult.equals("false")
					&& !withdrawresult.equals("0x110")) {
				Message msg = handler.obtainMessage();
				msg.what = 3;
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				msg.what = 0x110;
				handler.sendMessage(msg);
			}

		}
	});
	private ImageView right_img;

	// 提现界面的两个监听事件
	private void addEvents() {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 提现前先判断网络是否异常
				if (HttpUtils.isNetworkConnected(CashAdvanceActivity.this)) {

					if (editText.getText().toString().length() == 0) {
						Toast.makeText(CashAdvanceActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
					} else {

						// 先判断输入框的数字是否正常
						int count = 0, start = 0;
						while ((start = editText.getText().toString().indexOf(".", start)) >= 0) {
							start += ".".length();
							count++;
						}
						if (count > 1 || editText.getText().toString().indexOf(".") == 0) {
							Toast.makeText(CashAdvanceActivity.this, "请输入正确的数字", Toast.LENGTH_SHORT).show();
						} else {

							// 获取将要提现的金额
							BigDecimal judgemoney = null;
							try {
								judgemoney = new BigDecimal(editText.getText().toString());
							} catch (NumberFormatException e) {
								Toast.makeText(CashAdvanceActivity.this, "请输入正确的数字", Toast.LENGTH_SHORT).show();
								return;
							}
							judgemoney = judgemoney.setScale(2, BigDecimal.ROUND_HALF_UP);

							// 获取可提现金额
							String judgecanWithdCash = textView2.getText().toString();

							if (textView4.getText().toString().equals("暂无账号")
									|| textView4.getText().toString().length() == 0
									|| textView3.getText().toString().length() == 0) {
								Toast.makeText(CashAdvanceActivity.this, "请选择银行卡", Toast.LENGTH_SHORT).show();
							} else if (judgemoney.toString() == "0.00") {
								Toast.makeText(CashAdvanceActivity.this, "不能输入0，请重新输入", Toast.LENGTH_SHORT).show();
							} else if (judgemoney.compareTo(BigDecimal.valueOf(Double.valueOf(judgecanWithdCash))) == 1) {
								// BigDecimal类型的compareTo结果是-1 表示小于， 0
								// 表示等于，1表示大于
								Toast.makeText(CashAdvanceActivity.this, "提交失败，超出可提现金额", Toast.LENGTH_LONG).show();
							} else {
								editText.setText(judgemoney.toString());

								withdrawCashDialog = new WithdrawCashDialog(CashAdvanceActivity.this,
										R.style.ForwardDialog, judgemoney.toString());
								withdrawCashDialog.show();
							}

						}

					}
				} else {
					// 网路异常，进行提示
					Toast.makeText(CashAdvanceActivity.this, Constants.NO_INTENT_TIPS, Toast.LENGTH_LONG).show();
				}

			}
		});

		// 提现界面更换账号的监听事件
		findViewById(R.id.cash_textView5).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CashAdvanceActivity.this, BankCardActivity.class);

				Bundle bundle = new Bundle();
				bundle.putBoolean("isOk", true);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		});

	}

	public void initView() {
		backBtn = (ImageView) findViewById(R.id.back_img);
		right_img = (ImageView) findViewById(R.id.right_img);
		backBtn.setVisibility(View.VISIBLE);
		right_img.setVisibility(View.VISIBLE);
		right_img.setBackgroundResource(R.drawable.withdrawcash_history);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		right_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), MyRecordActivity.class));
			}
		});
		textView = (TextView) findViewById(R.id.title_tv);
		textView.setText("提现");
		textView1 = (TextView) findViewById(R.id.cash_textView1);
		textView2 = (TextView) findViewById(R.id.cash_textView2);
		textView1.setText("可提现余额(元)");
		textView4 = (TextView) findViewById(R.id.cash_textView4);
		textView5 = (TextView) findViewById(R.id.cash_textView5);
		textView3 = (TextView) findViewById(R.id.cash_textView6);
		editText = (EditText) findViewById(R.id.cash_editext1);

		// 监听输入提现框，只保留两位小数
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable edt) {
				String temp = edt.toString();
				int posDot = temp.indexOf(".");
				if (posDot <= 0)
					return;
				if (temp.length() - posDot - 1 > 2) {
					edt.delete(posDot + 3, posDot + 4);
				}
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

		});

		button = (Button) findViewById(R.id.cash_button1);
		// 首次进入界面，如果网络异常则进行提示
		if (HttpUtils.isNetworkConnected(CashAdvanceActivity.this)) {
			getData();
		} else {
			Toast.makeText(CashAdvanceActivity.this, Constants.NO_INTENT_TIPS, Toast.LENGTH_LONG).show();
		}

	}

	// 初始化提现界面的数据
	public void getData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				SharedPreferences userPreferences = getSharedPreferences("umeng_general_config", Context.MODE_PRIVATE);
				memberId = userPreferences.getString("ID", "").trim();

				canWithdCash = HttpUtils.request(null, URLs.CanWithdCash_URL + "/" + memberId);

				String bankCard = HttpUtils.request(null, URLs.BANKCARD_URL + "/" + memberId);

				Message msg = new Message();
				msg.what = 1;
				Bundle bundle = new Bundle();

				try {
					JSONObject jsonObject = new JSONObject(bankCard);
					String tmpJson = jsonObject.getString("data");
					JSONArray jsonArray = new JSONArray(tmpJson);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject tmpObj = (JSONObject) jsonArray.get(i);
						String bankName = tmpObj.getString("bankName");
						String bankCode = tmpObj.getString("bankCode");
						String bankCode1 = tmpObj.getString("bankCode1");

						bundle.putString("bankName", bankName);
						bundle.putString("bankCode", bankCode);
						bundle.putString("bankCode1", bankCode1);
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

				bundle.putString("canWithdCash", canWithdCash);

				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		}).start();
	}

	public class BtnConfirmReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getStringExtra("msg").equals("exit")) {
				// 接收到确认提现框的返回操作
				Message msg = handler.obtainMessage();
				msg.what = 2;
				handler.sendMessage(msg);
			} else {
				// 接收到确认提现框的确定操作

				// 判断网络是否正常
				if (HttpUtils.isNetworkConnected(CashAdvanceActivity.this)) {
					// 网络正常，启动提现线程
					withdcashthread.start();

				} else {
					// 网路异常，进行提示
					Message msg = handler.obtainMessage();
					msg.what = 4;
					handler.sendMessage(msg);
				}

			}
		}

	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); 
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SplashScreen"); 
		MobclickAgent.onPause(this);
	}
}
