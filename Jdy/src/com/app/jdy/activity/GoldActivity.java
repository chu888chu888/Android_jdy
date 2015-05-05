/**
 * Copyright (c) 2015
 *
 * Licensed under the UCG License, Version 1.0 (the "License");
 */
package com.app.jdy.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jdy.R;
import com.app.jdy.adapter.ListViewAdapter;
import com.app.jdy.adapter.SearchListViewAdapter;
import com.app.jdy.entity.ProductEntity;
import com.app.jdy.ui.BaseActivity;
import com.app.jdy.ui.DetailProductActivity;
import com.app.jdy.ui.MyListView;
import com.app.jdy.utils.BitmapUtils;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.JSONUtils;
import com.app.jdy.utils.StringUtils;
import com.app.jdy.utils.URLs;
import com.app.jdy.widget.WaitingDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

/**
 * description :
 * 
 * @version 1.0
 * @author xiaqiang
 * @createtime : 2015-3-21 上午11:36:51
 */
public class GoldActivity extends BaseActivity implements OnClickListener {
	private Context mContext;

	private MyListView ATListview;
	// 返回按钮
	private ImageView back_img;
	// title_bar标题
	private TextView title_tv;
	// 搜索按钮
	private ImageView right_img;

	/**
	 * 根据不同的类型传参拿json
	 */
	// 分类搜索字符串
	private String search_ProdType;
	// 模糊搜索关键字
	private String search_keyWord;
	// 排序字段
	private String search_orderby;
	// 排序顺序(顺序,降序)
	private String search_order;

	List<LinearLayout> orderLayouts = new ArrayList<LinearLayout>();
	private LV_SEARCH_BROADCAST lvSearchReceiver;
	private KeyWordSearchReceiver keyWordSearchReceiver;
	/**
	 * 排序相关组件
	 */
	private LinearLayout line_minSubsAmount, line_expeAnnuRevnue, line_period;
	/**
	 * 自定义listViewAdapter
	 */
	private ListViewAdapter listViewAdapter;
	/**
	 * listview产品示例
	 */
	private List<ProductEntity> productList = new ArrayList<ProductEntity>();

	/**
	 * 分页计数
	 */
	private int pageNumber = 1;
	/**
	 * ProductManager Json字符串
	 */
	private PullToRefreshScrollView mPullRefreshScrollView;

	private String notice_tip;

	private String request_url;

	private Integer flag;

	private SlidingMenu menu;

	private EditText edit_search;

	private ListView lv_search;

	private SearchListViewAdapter searchListViewAdapter;

	private ArrayList<String> strs;

	private Button btn_search;

	private LinearLayout linearLayout_net_error;

	private LinearLayout linearLayout_content;

	private LinearLayout slidingMenuLayout;

	//private LinearLayout imageLayout_net_error;

	private LinearLayout order_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gold_layout);
		flag = getIntent().getIntExtra("flag", 0);
		if (flag == 0) {
			setGuideResourceId(R.drawable.point_golds);
		}
		initData();
		initView();
	}

	private void initView() {
		mContext = this;
		notice_tip = getResources().getString(R.string.no_more_product);
		linearLayout_net_error = (LinearLayout) findViewById(R.id.linearLayout_net_error);
		linearLayout_content = (LinearLayout) findViewById(R.id.linearLayout_content);
		//imageLayout_net_error = (LinearLayout) findViewById(R.id.imageLayout_net_error);
		back_img = (ImageView) findViewById(R.id.back_img);
		title_tv = (TextView) findViewById(R.id.title_tv);
		right_img = (ImageView) findViewById(R.id.right_img);

		right_img.setBackgroundResource(R.drawable.search);
		right_img.setOnClickListener(this);
		if (flag == 0) {
			request_url = URLs.GETPRODUCTLIST;
			title_tv.setText("金矿");
			back_img.setVisibility(View.INVISIBLE);
		} else if (flag == 1) {
			request_url = URLs.FOLLOWLIST_URL;
			title_tv.setText("我的产品");
			back_img.setVisibility(View.VISIBLE);

			menu = new SlidingMenu(this);
			menu.setMode(SlidingMenu.RIGHT);
			menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			menu.setShadowWidthRes(R.dimen.shadow_width);
			menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			menu.setFadeDegree(0.35f);
			menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
			View view = LayoutInflater.from(mContext).inflate(R.layout.search_popuwindow, null);
			edit_search = (EditText) view.findViewById(R.id.edit_search);
			lv_search = (ListView) view.findViewById(R.id.lv_search);
			searchListViewAdapter = new SearchListViewAdapter(strs, mContext);
			lv_search.setAdapter(searchListViewAdapter);
			lv_search.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					TextView tv = (TextView) view.findViewById(R.id.tv_search);
					Intent intent = new Intent();
					intent.setAction("org.jdy.action.LV_SEARCH_BROADCAST");
					intent.putExtra("search_ProdType", CommonUtils.CodeForName(tv.getText().toString()));
					intent.putExtra("reqFlag", 1);
					sendBroadcast(intent);
					menu.showContent();
				};
			});
			btn_search = (Button) view.findViewById(R.id.btn_search);
			btn_search.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonUtils.HideKeyBoard(mContext, edit_search);
					Intent intent = new Intent();
					intent.setAction("org.jdy.action.KEYWORD_SEARCH_BROADCAST");
					intent.putExtra("search_keyWord", edit_search.getText().toString());
					intent.putExtra("reqFlag", 1);
					sendBroadcast(intent);
					menu.showContent();
				}
			});
			menu.setMenu(view);
			slidingMenuLayout = (LinearLayout) findViewById(R.id.slidingmenu);
			final Bitmap bitmap = BitmapUtils.readBitMap(mContext, R.drawable.sliding_bg,
					slidingMenuLayout.getMeasuredWidth(), slidingMenuLayout.getMeasuredHeight());
			slidingMenuLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					slidingMenuLayout.getViewTreeObserver().removeOnPreDrawListener(this);
					BitmapUtils.blur(bitmap, slidingMenuLayout);
					return true;
				}
			});
		}

		back_img.setOnClickListener(this);
		order_layout = (LinearLayout) findViewById(R.id.order_layout);
		line_minSubsAmount = (LinearLayout) findViewById(R.id.line_minSubsAmount);
		line_expeAnnuRevnue = (LinearLayout) findViewById(R.id.line_expeAnnuRevnue);
		line_period = (LinearLayout) findViewById(R.id.line_period);

		orderLayouts.add(line_minSubsAmount);
		orderLayouts.add(line_expeAnnuRevnue);
		orderLayouts.add(line_period);
		line_expeAnnuRevnue.setOnClickListener(this);
		line_minSubsAmount.setOnClickListener(this);
		line_period.setOnClickListener(this);
		ATListview = (MyListView) findViewById(R.id.gold_Listview);
		ATListview.setOnItemClickListener(new onClickview());
		listViewAdapter = new ListViewAdapter(mContext, productList);
		ATListview.setAdapter(listViewAdapter);

		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.gold_pull_refresh_scrollview);
		/**
		 * 注册广播
		 */
		lvSearchReceiver = new LV_SEARCH_BROADCAST();
		IntentFilter intentFilter2 = new IntentFilter("org.jdy.action.LV_SEARCH_BROADCAST");
		registerReceiver(lvSearchReceiver, intentFilter2);
		keyWordSearchReceiver = new KeyWordSearchReceiver();
		IntentFilter intentFilter3 = new IntentFilter("org.jdy.action.KEYWORD_SEARCH_BROADCAST");
		registerReceiver(keyWordSearchReceiver, intentFilter3);
		/**
		 * 监听下拉刷新相关
		 */
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				pageNumber++;
				notice_tip = getResources().getString(R.string.no_more_product);
				new QueryProductListTask().execute();
				mPullRefreshScrollView.onRefreshComplete();
			}
		});

		new QueryProductListTask().execute();
	}

	private void initData() {
		strs = new ArrayList<String>();
		strs.add("银行理财");
		strs.add("保险理财");
		strs.add("公募基金");
		strs.add("信托理财");
		strs.add("资管理财");
		strs.add("债权众筹");
		strs.add("股权众筹");
		strs.add("私募基金");
	}

	private void resetOrder(LinearLayout v) {
		productList.clear();
		for (LinearLayout layout : orderLayouts) {
			String layoutId = getResources().getResourceEntryName(layout.getId());
			// 得到点击的标识字段,比如peroid
			String key = layoutId.split("_")[1];
			//TextView tv = (TextView) findViewByType(layout, 1);
			ImageView iv = (ImageView) findViewByType(layout, 2);
			View split = findViewById(CommonUtils.getIdFromStr(mContext, "view_" + key));
			if (layout != v) {
				iv.setBackgroundResource(R.drawable.up_arrow);
				// 设置蓝线不可见
				split.setVisibility(View.INVISIBLE);
			} else {
				if (search_orderby == null) {
					search_order = "desc";
					iv.setBackgroundResource(R.drawable.down_arrow);
				} else if (search_orderby.equals(key)) {
					if ("desc".equals(search_order)) {
						// 当前是倒序则设置正序,箭头向上
						search_order = "asc";
						iv.setBackgroundResource(R.drawable.up_arrow);
					} else if ("asc".equals(search_order) || search_order == null) {
						// 当前是正序则设置倒序,箭头向下
						search_order = "desc";
						iv.setBackgroundResource( R.drawable.down_arrow);
					}
				} else {
					search_order = "desc";
					iv.setBackgroundResource(R.drawable.down_arrow);
				}
				// 设置排序关键字为当前点击的
				search_orderby = key;
				// 设置蓝线可见
				split.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 递归获得某控件下的对应控件
	 * 
	 * @param viewGroup
	 * @param flag
	 * @return
	 */
	public View findViewByType(ViewGroup viewGroup, int flag) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View view = viewGroup.getChildAt(i);
			if (view instanceof TextView && flag == 1) {
				return view;
			} else if (view instanceof ImageView && flag == 2) {
				return view;
			} else if (view instanceof ViewGroup) {
				view = findViewByType((ViewGroup) view, flag);
				if (view != null) {
					return view;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * description :ListView&GridView的Item点击事件处理类
	 * 
	 * @version 1.0
	 * @author zhonghuixiong
	 * @createtime : 2015-1-13 下午11:07:19
	 * 
	 */
	class onClickview implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			Intent intent = new Intent(GoldActivity.this, DetailProductActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("prodTypeCode", productList.get(position).getProdTypeCode());
			bundle.putString("ID", productList.get(position).getID());
			bundle.putString("name", productList.get(position).getName());
			bundle.putString("shareSubject", productList.get(position).getShareSubject());
			bundle.putString("productSubject", productList.get(position).getProductSubject());
			intent.putExtras(bundle);
			startActivity(intent);
		};
	}

	private class QueryProductListTask extends AsyncTask<Void, Integer, List<ProductEntity>> {

		@Override
		protected void onPreExecute() {
			// 判断网络情况
			if (HttpUtils.isNetworkConnected(mContext)) {
				linearLayout_content.setVisibility(View.VISIBLE);
				order_layout.setVisibility(View.VISIBLE);
				linearLayout_net_error.setVisibility(View.GONE);
			} else {
				linearLayout_content.setVisibility(View.GONE);
				order_layout.setVisibility(View.GONE);
				linearLayout_net_error.setVisibility(View.VISIBLE);
				//imageLayout_net_error.setBackgroundDrawable(BitmapUtils.readDrawable(mContext, R.drawable.net_error));
				// LayoutParams layoutParams = new
				// LayoutParams(LayoutParams.MATCH_PARENT,
				// LayoutParams.MATCH_PARENT);
				// linearLayout_net_error.setLayoutParams(layoutParams);
				cancel(true);
				return;
			}
			// 显示等待框
			WaitingDialog.showDialog(mContext);
			super.onPreExecute();
		}

		@Override
		protected List<ProductEntity> doInBackground(Void... param) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String.valueOf(pageNumber)));
			if (flag == 1) {
				params.add(new BasicNameValuePair("memberId", CommonUtils.getMemberID(mContext)));
			}
			if (StringUtils.isNotEmpty(search_orderby)) {
				params.add(new BasicNameValuePair("orderby", search_orderby));
			}
			if (StringUtils.isNotEmpty(search_order)) {
				params.add(new BasicNameValuePair("order", search_order));
			}
			if (StringUtils.isNotEmpty(search_ProdType)) {
				params.add(new BasicNameValuePair("prodTypeCode", search_ProdType));
			}
			if (StringUtils.isNotEmpty(search_keyWord)) {
				params.add(new BasicNameValuePair("keyword", search_keyWord));
			}
			String dataJson = HttpUtils.request(params, request_url);
			List<ProductEntity> thisTimeLoadList = new ArrayList<ProductEntity>();
			if (!dataJson.equals("false")) {
				ProductEntity[] productEntitys = JSONUtils.toBean(dataJson, ProductEntity[].class);
				if (null != productEntitys) {
					thisTimeLoadList = Arrays.asList(productEntitys);
					productList.addAll(thisTimeLoadList);
				}
			}
			return thisTimeLoadList;
		}

		@Override
		protected void onPostExecute(List<ProductEntity> result) {
			if (result.size() == 0) {
				// 金矿首页下拉刷新没有数据的时候执行此步，进行提示
				if (StringUtils.isNotEmpty(notice_tip)) {
					Toast.makeText(mContext, notice_tip, Toast.LENGTH_SHORT).show();
				}
			}
			listViewAdapter.notifyDataSetChanged();
			WaitingDialog.dismissDialog();
			super.onPostExecute(result);
		}
	}

	/**
	 * 
	 * description :模糊搜索广播通知
	 * 
	 * @version 1.0
	 * @author zhonghuixiong
	 * @createtime : 2015-1-29 下午5:33:44
	 * 
	 */
	public class KeyWordSearchReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			productList.clear();
			search_keyWord = intent.getStringExtra("search_keyWord");
			search_order = null;
			search_orderby = null;
			pageNumber = 1;
			notice_tip = "亲,没有搜索到对应产品";
			Integer reqFlag = intent.getIntExtra("reqFlag", -1);
			if (reqFlag == flag) {
				new QueryProductListTask().execute();
			}
		}
	}

	/**
	 * 
	 * description :分类搜索广播通知
	 * 
	 * @version 1.0
	 * @author zhonghuixiong
	 * @createtime : 2015-1-29 下午5:33:44
	 * 
	 */
	public class LV_SEARCH_BROADCAST extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			productList.clear();
			if (flag == 0) {
				((MainActivity) getParent()).clearKeywordEditText();
				back_img.setVisibility(View.VISIBLE);
			} else if (flag == 1) {
				EditText et = (EditText) menu.getMenu().findViewById(R.id.edit_search);
				et.setText("");
			}

			search_ProdType = intent.getStringExtra("search_ProdType");
			title_tv.setText(CommonUtils.TypeforCode(search_ProdType));
			search_keyWord = null;
			search_order = null;
			search_orderby = null;
			pageNumber = 1;
			Integer reqFlag = intent.getIntExtra("reqFlag", -1);
			if (reqFlag == flag) {
				new QueryProductListTask().execute();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.line_expeAnnuRevnue:
			resetOrder((LinearLayout) v);
			productList.clear();
			pageNumber = 1;
			notice_tip = "";
			new QueryProductListTask().execute();
			break;
		case R.id.line_minSubsAmount:
			resetOrder((LinearLayout) v);
			productList.clear();
			pageNumber = 1;
			notice_tip = "";
			new QueryProductListTask().execute();
			break;
		case R.id.line_period:
			resetOrder((LinearLayout) v);
			productList.clear();
			pageNumber = 1;
			notice_tip = "";
			new QueryProductListTask().execute();
			break;
		case R.id.right_img:
			if (flag == 0) {
				((MainActivity) getParent()).showMenu();
				((MainActivity) getParent()).editTextSelectAll();
			} else if (flag == 1) {
				menu.toggle(true);
			}
			break;
		case R.id.back_img:
			if (flag == 0) {
				title_tv.setText("金矿");
				back_img.setVisibility(View.INVISIBLE);
				productList.clear();
				search_ProdType = null;
				search_keyWord = null;
				search_order = null;
				search_orderby = null;
				pageNumber = 1;
				new QueryProductListTask().execute();
			} else if (flag == 1) {
				if (search_ProdType != null) {
					title_tv.setText("我的产品");
					productList.clear();
					search_ProdType = null;
					search_keyWord = null;
					search_order = null;
					search_orderby = null;
					pageNumber = 1;
					new QueryProductListTask().execute();
				} else {
					finish();
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(lvSearchReceiver);
		unregisterReceiver(keyWordSearchReceiver);
		super.onDestroy();
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
