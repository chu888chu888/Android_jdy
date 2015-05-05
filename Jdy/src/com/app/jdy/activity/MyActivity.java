package com.app.jdy.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.jdy.R;
import com.app.jdy.adapter.GradeAdapter;
import com.app.jdy.entity.Grade;
import com.app.jdy.ui.BankCardActivity;
import com.app.jdy.ui.BaseActivity;
import com.app.jdy.ui.CashAdvanceActivity;
import com.app.jdy.ui.FaceValueActivity;
import com.app.jdy.ui.MsgListActivity;
import com.app.jdy.ui.MyCommunityActivity;
import com.app.jdy.ui.MyFaceActivity;
import com.app.jdy.ui.MyOrderActivity;
import com.app.jdy.ui.RoundAngleImageView;
import com.app.jdy.ui.SettingActivity;
import com.app.jdy.utils.BitmapUtils;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.JSONUtils;
import com.app.jdy.utils.URLs;
import com.app.jdy.utils.UploadUtil;
import com.app.jdy.utils.UploadUtil.OnUploadProcessListener;
import com.app.jdy.widget.CommonDialog;
import com.app.jdy.widget.GradeDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class MyActivity extends BaseActivity implements OnUploadProcessListener {
	/**
	 * 我的
	 */
	private ImageView right_img;
	/**
	 * 用户名称
	 */
	private TextView user_name;
	/**
	 * 头像
	 */
	private RoundAngleImageView user_head;
	/**
	 * 用户余额
	 */
	private TextView account_balance;
	/**
	 * 银行卡
	 */
	private RelativeLayout bank_card_layout;
	/**
	 * 我的面值的显示金钱
	 */
	private TextView face_value_tips;
	/**
	 * 我的面子的显示数字组件
	 */
	private TextView ring_tips;
	/**
	 * 我的圈子的显示数字组件
	 */
	private TextView face_tips;
	/**
	 * 我的订单的显示数字组件
	 */
	private TextView order_tips;
	/**
	 * 我的面值的点击范围
	 */
	private RelativeLayout click_face_value;
	/**
	 * 我的面子的点击范围
	 */
	private RelativeLayout my_face;
	/**
	 * 我的圈子的点击事件
	 */
	private RelativeLayout my_community_click;
	/**
	 * 我的订单的点击范围
	 */
	private RelativeLayout my_order_relativelayout;
	/**
	 * 设置的点击范围
	 */
	private RelativeLayout setting;
	/**
	 * handler变量
	 */
	private Handler handler;
	/**
	 * 服务器的json数据
	 */
	private String datajson;
	/**
	 * 服务器的json数据
	 */
	private String datajson1;
	/**
	 * 用户的 ID号
	 */
	private String ID;
	/**
	 * 发送的数据
	 */
	private ArrayList<NameValuePair> params;
	/**
	 * 提现点击范围
	 */
	private RelativeLayout balance_click;
	/**
	 * 提现记录组件
	 */
//	private TextView balance_tips;
	/**
	 * 消息列表
	 */
	private RelativeLayout msgLinLayout;
	/**
	 * 我的消息的组件
	 */
	private TextView my_test_tips;

	private TextView user_grade_text;

	private String gradeJson;

	/******** 状态码区域 ********/
	public static final int SELECT_PIC_BY_TACK_PHOTO = 11;// 使用照相机拍照获取图片

	public static final int SELECT_PIC_BY_PICK_PHOTO = 12;// 使用相册中的图片

	public static final int CROP_PHOTO = 13;// 裁剪完成

	/******** 变量区域 ********/
	private Bitmap bm;
	private String tempName; // 头像原图临时文件名
	private String smallTempName;// 头像裁剪图临时文件名
	/**
	 * 下拉刷新
	 */
	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	private Dialog dialog;
	private TextView user_credit_balance;
	private LinearLayout user_credit_balance_layout;
	private LinearLayout user_grade_layout;
	private TextView user_phone;
	private ImageView back_img;
	private TextView title_tv;
	private Context mContext;
	
	private LinearLayout linearLayout_content;
	private FrameLayout linearLayout_net_error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_my);
		setGuideResourceId(R.drawable.point_mine);
		dialog = new Dialog(MyActivity.this, R.style.ForwardDialog);
		right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setBackgroundResource(R.drawable.star);
		dialog.setContentView(R.layout.r_okcanceldialogview);
		right_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent goldActivityIntent = new Intent(MyActivity.this, GoldActivity.class);
				goldActivityIntent.putExtra("flag", 1);
				startActivity(goldActivityIntent);
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					if (datajson1.equals("false")) {
						my_test_tips.setText("0条");
					} else {
						my_test_tips.setText(datajson1 + "条");
					}
					break;
				case 3:
					List<Grade> list = new ArrayList<Grade>();
					try {
						JSONArray jsonArray = new JSONArray(gradeJson);
						for (int i = 0; i < jsonArray.length(); i++) {
							Grade grade = new Grade();
							grade.setGrade(jsonArray.getJSONObject(i).getString("name"));
							grade.setScore(jsonArray.getJSONObject(i).getString("exp"));
							list.add(grade);
						}
						GradeAdapter gradeAdapter = new GradeAdapter(getBaseContext(), R.layout.grade_item, list);
						GradeDialog gradeDialog = new GradeDialog(MyActivity.this, R.style.ForwardDialog, gradeAdapter);
						gradeDialog.show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 11:
					// user_head.setImageBitmap(bm);
					break;
				case 12:
					break;
				default:
					break;
				}
			}
		};
		initView();
		new GetMsgDataTask().execute();
		// getData();

		// getMyMessage();
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				refreshUI();
				mPullRefreshScrollView.onRefreshComplete();
				new GetMsgDataTask().execute();
			}
		});
		click_face_value.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), FaceValueActivity.class));
			}
		});
		balance_click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), CashAdvanceActivity.class));
			}
		});
//		balance_tips.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(getBaseContext(), MyRecordActivity.class));
//			}
//		});
		bank_card_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), BankCardActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isOk", false);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		my_face.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), MyFaceActivity.class));
			}
		});
		my_order_relativelayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), MyOrderActivity.class));
			}
		});
		my_community_click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), MyCommunityActivity.class));
			}
		});
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent1 = new Intent(getBaseContext(), SettingActivity.class);
				Bundle bundle1 = new Bundle();
				bundle1.putString("username", user_name.getText().toString());
				intent1.putExtras(bundle1);
				startActivityForResult(intent1, 1);

			}
		});
		msgLinLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent(getBaseContext(), MsgListActivity.class);
				startActivityForResult(intent2, 1);
			}
		});
		user_head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCustomMessage();
			}
		});

		user_grade_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btnLock) return;
				btnLock = true;
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						Message msg = new Message();
						gradeJson = HttpUtils.request(null, URLs.GRADE);
						if (gradeJson.length() != 0 && !gradeJson.equals("0x110")) {
							msg.what = 3;
						} else {
							msg.what = 0;
						}
						handler.sendMessage(msg);
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								btnLock = false;
							}
						}, 200);
					}
				});
				thread.start();
			}
		});
		user_credit_balance_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonDialog commonDialog = new CommonDialog(MyActivity.this, R.style.ForwardDialog,
						R.layout.score_dialog);
				commonDialog.show();
			}
		});
	}
	
	boolean btnLock = false; // 防止等级按钮多次点击弹出多个对话框

	/**
	 * 
	 * @author zhoufeng
	 * @createtime 2015-1-25 上午10:28:10
	 * @Decription 初始化组件
	 * 
	 */
	public void initView() {

		back_img = (ImageView) findViewById(R.id.back_img);
		back_img.setVisibility(View.GONE);
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText("我");
		user_credit_balance = (TextView) findViewById(R.id.user_credit_balance);
		user_grade_text = (TextView) findViewById(R.id.user_grade_text);
		user_grade_layout = (LinearLayout) findViewById(R.id.user_grade_layout);
		user_credit_balance_layout = (LinearLayout) findViewById(R.id.user_credit_balance_layout);
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.my_pull_refresh_scrollview);
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		balance_click = (RelativeLayout) findViewById(R.id.balance_click);
//		balance_tips = (TextView) findViewById(R.id.balance_tips);
		user_name = (TextView) findViewById(R.id.user_name);
		user_phone = (TextView) findViewById(R.id.user_phone);
		account_balance = (TextView) findViewById(R.id.account_balance);
		setting = (RelativeLayout) findViewById(R.id.setting);
		msgLinLayout = (RelativeLayout) findViewById(R.id.my_msg_layout);
		my_face = (RelativeLayout) findViewById(R.id.my_face);
		bank_card_layout = (RelativeLayout) findViewById(R.id.bank_card_layout);
		my_community_click = (RelativeLayout) findViewById(R.id.my_community_click);
		click_face_value = (RelativeLayout) findViewById(R.id.click_face_value);
		my_order_relativelayout = (RelativeLayout) findViewById(R.id.my_order_relativelayout);

		// my_test_tips = (TextView) findViewById(R.id.my_test_tips);
		user_head = (RoundAngleImageView) findViewById(R.id.user_head);
		SharedPreferences userPreferences = getSharedPreferences("umeng_general_config", Context.MODE_PRIVATE);
		ID = userPreferences.getString("ID", "").trim();
		
		linearLayout_content = (LinearLayout) findViewById(R.id.linearLayout_content);
		linearLayout_net_error = (FrameLayout) findViewById(R.id.linearLayout_net_error);
		refreshUI();
	}
	
	private void refreshUI() {
		// 判断网络情况
		if (HttpUtils.isNetworkConnected(mContext)) {
			linearLayout_content.setVisibility(View.VISIBLE);
			linearLayout_net_error.setVisibility(View.GONE);
		} else {
			linearLayout_content.setVisibility(View.GONE);
			linearLayout_net_error.setVisibility(View.VISIBLE);
			return;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == 1) {
				String s = data.getAction();
				user_name.setText(s);
			} else if (resultCode == 2) {// 用户退出登录操作
				finish();
			} else if (resultCode == 3) {
				// getMyMessage();
			} else if (resultCode == 4) {
				finish();
			}
		} else if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
			if (data != null) {
				startPhotoZoom(data.getData());
			}
		} else if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {
			File file = new File(URLs.TEMP_DIR, tempName);
			startPhotoZoom(Uri.fromFile(file));
		} else if (requestCode == CROP_PHOTO) {
			/**
			 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			 * 当前功能时，会报NullException，小马只 在这个地方加下，大家可以根据不同情况在合适的 地方做判断处理类似情况
			 * 
			 */
			if (data != null) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					Bitmap smallPhoto = BitmapUtils.compressImage(photo, 90, 10);

					smallTempName = ID + ".jpg";
					File tempFile = BitmapUtils.BitmapToFile(smallPhoto, URLs.TEMP_DIR, smallTempName);

					UploadUtil uploadUtil = UploadUtil.getInstance();
					uploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

					Map<String, String> params = new HashMap<String, String>();
					params.put("memberId", ID);
					params.put("businessKey", ID);
					params.put("businessType", "MemberEntity");
					uploadUtil.uploadFile(tempFile.getAbsolutePath(), "portrait", URLs.PORTRAIT_UPLOAD, params);
					user_head.setImageBitmap(smallPhoto);
				}
			}
		}
	}

	/**
	 * 
	 * @author zhonghuixiong
	 * @createtime 2015-2-3 上午3:16:34
	 * @Decription 自定义Dialog 参数 对话框标题
	 */
	private void showCustomMessage() {
		/**
		 * 拍照按钮事件
		 */
		((LinearLayout) dialog.findViewById(R.id.takephotobutton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				tempName = UUID.randomUUID().toString() + ".jpg";
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(URLs.TEMP_DIR, tempName)));
				startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
			}
		});
		/**
		 * 相册按钮事件
		 */
		((LinearLayout) dialog.findViewById(R.id.picButton)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_PHOTO);
	}

	@Override
	public void onUploadDone(int responseCode, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUploadProcess(int uploadSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initUpload(int fileSize) {
		// TODO Auto-generated method stub

	}

	/**
	 * 下拉刷新异步任务
	 */
	private class GetMsgDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... p) {
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("memberId", ID));
			datajson = HttpUtils.request(params, URLs.USER_MESSAGE);

			// getData();
			// getMyMessage();
			return datajson;
		}

		@Override
		protected void onPostExecute(String datajson) {
			if (HttpUtils.isReqSuccess(datajson)) {
				fillMyMsgByData(datajson);
			} else {
			}

			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(datajson);
		}

		/**
		 * 通过请求的数据填充"我的"数据
		 * 
		 * @param datajson
		 */
		private void fillMyMsgByData(String datajson) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(datajson);

				user_name.setText(JSONUtils.getString(jsonObject, "name",
						getResources().getString(R.string.no_user_name)));
				user_phone.setText(JSONUtils.getString(jsonObject, "phone",
						getResources().getString(R.string.no_mobilephone)));
				// 用户等级
				user_grade_text.setText(JSONUtils.getString(jsonObject, "rankName", "V0"));
				// 用户积分余额
				user_credit_balance.setText(JSONUtils.getString(jsonObject, "creditBalance", "0"));
				// 用户余额
				// account_balance.setText(ChineseMoneyUtils.numWithDigit(Double.parseDouble(JSONUtils.getString(
				// jsonObject, "accountBalance", "0"))));
				account_balance.setText(JSONUtils.getString(jsonObject, "accountBalance", "0.00"));

				// 头像url不为空时才设置头像
				String portrait = JSONUtils.getString(jsonObject, "portrait", null);
				if (portrait != null) {
					ImageLoader.getInstance().displayImage(portrait, user_head);
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
