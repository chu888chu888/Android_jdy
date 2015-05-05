package com.app.jdy.ui;

import com.app.jdy.R;
import com.app.jdy.adapter.GuidePagerAdapter;
import com.app.jdy.utils.BitmapUtils;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 
 * description :引导界面
 * 
 * @version 1.0
 * @author zhoufeng
 * @createtime : 2015-1-22 上午11:55:16
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhoufeng 2015-1-22 上午11:55:16
 * 
 */
public class GuideActivity extends FragmentActivity {

	private ViewPager mViewPager;// 定义一个自己的viewpager

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initView();
	}

	private void initView() {
		Boolean isOK = getIntent().getExtras().getBoolean("isOK");
		mViewPager = (ViewPager) findViewById(R.id.MyViewPager);
		GuidePagerAdapter guidePagerAdapter = new GuidePagerAdapter(this.getSupportFragmentManager(),
				GuideActivity.this, isOK);
		mViewPager.setAdapter(guidePagerAdapter);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapUtils.unbindDrawables(findViewById(R.id.my_content_view));
		System.gc();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
