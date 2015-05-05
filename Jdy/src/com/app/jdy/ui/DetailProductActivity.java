package com.app.jdy.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.jdy.R;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * description :
 * 
 * @version 1.0
 * @author zhoufeng
 * @createtime : 2015-3-13 下午10:02:03
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhoufeng 2015-3-13 下午10:02:03
 * 
 */
public class DetailProductActivity extends BaseFragmentActivity implements OnClickListener {
	private Context mContext;
	/**
	 * 产品类别
	 */
	private String code;
	/**
	 * 产品ID号
	 */
	private String id;
	/**
	 * 图标对应的文字
	 */
	private String imageString;
	private FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	private TextView title_tv;
	private String shareSubject;
	private String name;
	private String memberID;
	private ImageView back_img;
	private Button super_share;
	private Button super_buy;
	private String productSubject;
	private ScrollView product_detail_srollview;
	private float currentY = 0;
	private LinearLayout bottom_button_layout;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.product_detail);
		setGuideResourceId(R.drawable.point_detail);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initData();
		/**
		 * 对产品类型进行判断，并对应加载不同产品的Fragment
		 */
		if (null != code) {// 由于现在所有产品都是一个模板，所以不存在判断。
			fragmentTransaction.replace(R.id.fragmentmain, new BankFragment(id, code, imageString));
			fragmentTransaction.commit();
		}
		initView();
	}

	@Override
	protected void onResume() {
		if (product_detail_srollview == null) {
			product_detail_srollview = (ScrollView) this.findViewById(R.id.product_detail_srollview);
		}
//		product_detail_srollview.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_MOVE:
//					System.out.println("scrollY:" + v.getScrollY());
//					if (v.getScrollY() != currentY) {
//						bottom_button_layout.setVisibility(View.INVISIBLE);
//					} else {
//						// System.out.println("上滑");
//					}
//					currentY = v.getScrollY();
//					break;
//				case MotionEvent.ACTION_UP:
//					if (!bottom_button_layout.isShown()) {
//						new Handler().postDelayed(new Runnable() {
//							public void run() {
//								bottom_button_layout.setVisibility(View.VISIBLE);
//							}
//						}, 1000);
//					}
//					break;
//				case MotionEvent.ACTION_DOWN:
//					break;
//				default:
//					break;
//				}
//				return false;
//			}
//		});
		super.onResume();
		MobclickAgent.onResume(this); 
	}

	private void initData() {
		// 构造数据
		id = getIntent().getExtras().getString("ID");
		name = getIntent().getExtras().getString("name");
		code = getIntent().getExtras().getString("prodTypeCode");
		imageString = CommonUtils.TypeforCode(code);
		shareSubject = getIntent().getExtras().getString("shareSubject");
		productSubject = getIntent().getExtras().getString("productSubject");
		// 会员ID
		memberID = CommonUtils.getMemberID(this);
	}

	private void initView() {
		mContext = this;
		back_img = (ImageView) findViewById(R.id.back_img);
		back_img.setOnClickListener(this);
		super_share = (Button) findViewById(R.id.super_share);
		super_share.setOnClickListener(this);
		super_buy = (Button) findViewById(R.id.super_buy);
		super_buy.setOnClickListener(this);
		bottom_button_layout = (LinearLayout) findViewById(R.id.bottom_button_layout);
		UIUtils.changeRadioButtonImageSize(super_share, 0, 30, 30);
		UIUtils.changeRadioButtonImageSize(super_buy, 0, 30, 30);

		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText("产品详情");
	}

	@Override
	public void onClick(View v) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("ID", id);
		map.put("prodTypeCode", code);
		map.put("memberId", memberID);
		map.put("shareSubject", shareSubject);
		map.put("productSubject", productSubject);
		switch (v.getId()) {
		case R.id.back_img:
			this.finish();
			break;
		case R.id.super_share:
			UIUtils.shareProductDialog(mContext, map);
			break;
		case R.id.super_buy:
			UIUtils.showCouponDialog(mContext, map);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
