package com.app.jdy.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.jdy.R;
import com.app.jdy.activity.MainActivity;
import com.app.jdy.context.MsgService;
import com.app.jdy.context.MyApplication;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.Constants;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.PollingUtils;
import com.app.jdy.utils.URLs;
import com.app.jdy.widget.DownAppDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * description :我的银行卡activity
 * 
 * @version 1.0
 * @author zhoufeng
 * @createtime : 2015-1-19 下午6:27:46
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhoufeng 2015-1-19 下午6:27:46
 * 
 */
public class SettingActivity extends Activity implements OnClickListener {
	/**
	 * 返回按钮
	 */
	private ImageView mBackImg;
	/**
	 * 标题
	 */
	private TextView title;
	/**
	 * 用户昵称
	 */
	private String username;
	/**
	 * 用户昵称组件
	 */
	private TextView setting_show_username;
	/**
	 * 修改用户昵称的点击范围
	 */
	private RelativeLayout change_username_click;
	/**
	 * 修改密码的点击范围
	 */
	private RelativeLayout change_password_click;
	/**
	 * 查看引导页
	 */
	private RelativeLayout show_guide_click;
	/**
	 * 检查更新的点击范围
	 */
	private RelativeLayout check_new_layout;
	/**
	 * 用户的ID号
	 */
	private String ID;
	/**
	 * 提交数据
	 */
	private ArrayList<NameValuePair> params;
	/**
	 * 服务器返回来的json数据
	 */
	private String dataJson;
	/**
	 * handler
	 */
	private Handler handler;
	/**
	 * 解析json数据对象
	 */
	private JSONObject jsonObject;
	/**
	 * 退出按钮
	 */
	private TextView loginoutBtn;

//	private ToggleButton adButton;
	/**
	 * 检查更新图标
	 */
	private ImageView check_new_image;

	/**
	 * 版本号
	 */
	private String versionString;
	private String tempVersionString;
	/**
	 * 版本描述
	 */
	private String versionDescrition;
	/**
	 * 是否有更新标示
	 */
	private Boolean flagBoolean = false;
	/**
	 * 是否有新版本可以下载
	 */
	private Boolean isDown = false;

	private RelativeLayout about_us;
	
	private DownAppDialog downAppDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initView();
		versionString = getVersion();
		checkNewVersion();

		mBackImg.setOnClickListener(this);
		change_password_click.setOnClickListener(this);
		change_username_click.setOnClickListener(this);
		show_guide_click.setOnClickListener(this);
		check_new_layout.setOnClickListener(this);
		about_us = (RelativeLayout) findViewById(R.id.about_us);
		about_us.setOnClickListener(this);
		// 停止推送消息

		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(SettingActivity.this, Constants.NO_INTENT_TIPS, Toast.LENGTH_LONG).show();
					break;
				case 1:
					if (CommonUtils.CompareVersion(tempVersionString, versionString)) {
						check_new_image.setVisibility(View.VISIBLE);
						isDown = true;
					} else {
						check_new_image.setVisibility(View.GONE);
						isDown = false;
					}
					break;
				default:
					break;
				}
			};
		};

//		adButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// 当按钮第一次被点击时候响应的事件
//				if (adButton.isChecked()) {
//					isClose(true);
//
//				}
//				// 当按钮再次被点击时候响应的事件
//				else {
//					isClose(false);
//					PollingUtils.stopPollingService(SettingActivity.this, MsgService.class, MsgService.ACTION);
//
//					stopService(new Intent(SettingActivity.this, MsgService.class));
//				}
//			}
//		});
		loginoutBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SharedPreferences userPreferences = getSharedPreferences("umeng_general_config", Context.MODE_PRIVATE);
				userPreferences.edit().clear().commit();
				MyApplication.isGo = false;// 关闭轮询
				setResult(2);
				new Thread(new Runnable() {

					@Override
					public void run() {
						HttpUtils.request(null, URLs.LOGINOU_URL);
					}
				}).start();
				Intent intent = new Intent(SettingActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				setResult(4, new Intent().setAction(""));
			}
		});
	}

	private void isClose(boolean isClose) {
		SharedPreferences userPreferences = getSharedPreferences("umeng_general_config", Context.MODE_PRIVATE);
		Editor editor = userPreferences.edit();
		if (isClose) {
			editor.putBoolean("isClose", true);
			editor.commit();
		} else {
			editor.putBoolean("isClose", false);
			editor.commit();
		}
	}

	/**
	 * 初始化组件
	 */
	@SuppressLint("NewApi")
	public void initView() {
		
		downAppDialog = new DownAppDialog(this, R.style.ForwardDialog);
		
		mBackImg = (ImageView) findViewById(R.id.back_img);
		mBackImg.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title_tv);
		title.setText("设置");

		SharedPreferences userPreferences = getSharedPreferences("umeng_general_config", Context.MODE_PRIVATE);
		ID = userPreferences.getString("ID", "").trim();
		Boolean isClose = userPreferences.getBoolean("isClose", true);

		setting_show_username = (TextView) findViewById(R.id.setting_show_username);
		change_username_click = (RelativeLayout) findViewById(R.id.change_username_click);
		change_password_click = (RelativeLayout) findViewById(R.id.change_password_click);
		check_new_layout = (RelativeLayout) findViewById(R.id.check_new_layout);
		check_new_image = (ImageView) findViewById(R.id.check_new_image);
		loginoutBtn = (TextView) findViewById(R.id.setting_loginout);
		show_guide_click = (RelativeLayout) findViewById(R.id.show_guide_click);
//		adButton = (ToggleButton) findViewById(R.id.ad_push_ornot_btn);
		username = getIntent().getExtras().getString("username");
		setting_show_username.setText(username);
//		if (isClose) {
//			adButton.setChecked(true);
//		} else {
//			adButton.setChecked(false);
//		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "没有发现新版本";
		}
	}

	/**
	 * 检查更新
	 */
	public void checkNewVersion() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				if (HttpUtils.isNetworkConnected(SettingActivity.this) == true) {
					dataJson = HttpUtils.request(null, URLs.GETNEWAPKVERSION);
					if (dataJson.length() != 0 && !dataJson.equals("0x110")) {
						try {
							jsonObject = new JSONObject(dataJson);
							if (jsonObject.getString("versionNumber").equals("null")) {
								tempVersionString = "0.0";
							} else {
								tempVersionString = jsonObject.getString("versionNumber");
							}
							if (jsonObject.getString("versionDescrition").equals("null")) {
								versionDescrition = "";
							} else {
								versionDescrition = jsonObject.getString("versionDescrition");
							}
							msg.what = 1;
						} catch (Exception e) {
							// TODO: handle exception
						}

					} else {
						msg.what = 0;
					}
				} else {
					msg.what = 0;
				}
				handler.sendMessage(msg);
			}
		});
		thread.start();
	}
	
	public void downNewApp(){
		if (isDown) {
			downAppDialog.setVersionDescrition(versionDescrition);
			downAppDialog.showDownMessage();
		}else{
			Toast.makeText(this, "当前已经是最新版本了", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == 1) {
				String s = data.getAction();
				setting_show_username.setText(s);
				setResult(1, new Intent().setAction(s));
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_img:
			finish();
			break;
		case R.id.change_password_click:
			startActivity(new Intent(SettingActivity.this, ChangePasswordActivity.class));
			break;
		case R.id.change_username_click:
			Intent intent = new Intent(SettingActivity.this, UpdateNameActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", setting_show_username.getText().toString());
			intent.putExtras(bundle);
			startActivityForResult(intent, 1);
			break;
		case R.id.show_guide_click:
			Intent intent2 = new Intent(SettingActivity.this, GuideActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putBoolean("isOK", true);
			intent2.putExtras(bundle2);
			startActivity(intent2);
			break;
		case R.id.check_new_layout:
			downNewApp();
			break;
		case R.id.about_us:
			Intent intent3 = new Intent(SettingActivity.this, AboutUsActivity.class);
			startActivity(intent3);
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
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