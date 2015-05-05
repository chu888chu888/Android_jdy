package com.app.jdy.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import com.app.jdy.activity.MainActivity;
import com.app.jdy.adapter.ListViewAdapter;
import com.app.jdy.adapter.SearchListViewAdapter;
import com.app.jdy.entity.ProductEntity;
import com.app.jdy.utils.ChineseMoneyUtils;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.Constants;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.URLs;
import com.app.jdy.widget.WaitingDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * description :我的Activity
 * 
 * @version 1.0
 * @author zhonghuixiong
 * @createtime : 2015-1-13 下午7:40:34
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhonghuixiong 2015-1-13
 *             下午7:40:34
 * 
 */
public class FollowActivity extends Activity {
	/**
	 * 初始化控件
	 */
	private MyListView followListview;
	/**
	 * 侧滑搜索相关
	 */
	@SuppressWarnings("unused")
	private Context context;
	private SlidingMenu menu;
	private List<String> strs;
	private SearchListViewAdapter searchListViewAdapter;
	private ListView lv_search;
	private Button btn_search;
	private TextView tv_search;
	private EditText edit_search;
	/**
	 * 用来解析json的ProductEntity
	 */
	private ProductEntity tempEntity;
	private ProductEntity tempEntity2;
	private ProductEntity tempEntity3;
	private ProductEntity tempEntity4;
	private ProductEntity tempEntity5;
	private ProductEntity tempEntity6;
	private ProductEntity tempEntity7;
	private ProductEntity tempEntity8;
	private Handler mHandler;
	/**
	 * 自定义listViewAdapter
	 */
	private ListViewAdapter listViewAdapter;
	/**
	 * 产品类型
	 */
	private String issuerType;
	/**
	 * 解析json数组
	 */
	private JSONArray jsonArray;
	/**
	 * 嵌套json对象
	 */
	private JSONObject item;
	/**
	 * 存续期/保障期限
	 */
	private int period;
	/**
	 * 保险类型
	 */
	private String insuranceType;
	/**
	 * listview产品示例
	 */
	private ArrayList<ProductEntity> listViewProductEntitiesList;
	/**
	 * 格式化最高收益率
	 */
	private DecimalFormat df;
	/**
	 * 产品名称
	 */
	private String prodTypeName;
	/**
	 * 起购金额
	 */
	private String minSubsAmount;
	/**
	 * 预期年化收益利率
	 */
	private String expeAnnuRevnue;
	/**
	 * 获取请求的json字符串
	 */
	private String dataJson = null;
	/**
	 * 产品类型码
	 */
	private String prodTypeCode = null;
	/**
	 * prodid
	 */
	private String productId = null;
	/**
	 * 产品名称
	 * 
	 */
	private String name;
	/**
	 * 投资方向
	 */
	private String investDirection;
	private String shareSubject;
	private String productSubject;
	/**
	 * 发行公司
	 */
	private String issuer;
	/**
	 * 基金管理人
	 */
	private String fundDirector;
	/**
	 * 基金类型
	 */
	private String fundType;
	/**
	 * 产品详情
	 */
	private String prodDetail;
	/**
	 * 搜索相关
	 */
	private ImageView right_img;
	/**
	 * 最高收益
	 */
	private String topGain;
	/**
	 * 分页计数
	 */
	private int pageNumber = 1;

	// 判断我的产品当前页面显示的列表是总产品列表还是搜索后的列表
	// 默认显示全部数据

	private PullToRefreshScrollView mPullRefreshScrollView;
	@SuppressWarnings("unused")
	private ScrollView mScrollView;
	private String memberId;
	private String search_text;
	private ImageView back_img;
	private TextView title_tv;
	// 拿列表json
	private String getListJsonList = "getList";
	// 拿分类搜索json
	private String getListJsonProdType = "getProdType";
	// 拿模糊搜索json
	private String getListJsonKeyWord = "getKeyWordList";
	// 拿升序起购金额
	private String getListJsonMinSubsAmountAsc = "getMinSubsAmountAsc";
	// 拿升序年华收益
	private String getListJsonExpeAnnuRevnueAsc = "getExpeAnnuRevnueAsc";
	// 拿升序理财期限
	private String getListJsonPeriodAsc = "getPeriodAsc";
	// 拿降序起购金额
	private String getListJsonMinSubsAmountDesc = "getMinSubsAmountDesc";
	// 拿降序年华收益
	private String getListJsonExpeAnnuRevnueDesc = "getExpeAnnuRevnueDesc";
	// 拿降序理财期限
	private String getListJsonPeriodDesc = "getPeriodDesc";
	// 起购金额升序标记
	private Boolean flagMinSubsAmountAsc = true;
	// 年华收益升序标记
	private Boolean flagExpeAnnuRevnueAsc = true;
	// 理财期限升序标记
	private Boolean flagPeriodAsc = true;
	// 分类搜索字符串
	private String search_ProdType;
	// 模糊搜索关键字
	private String search_keyWord;
	// 下拉刷新类型(sortMinSubsAmountAscRequest,sortExpeAnnuRevnueAscRequest,sortPeriodAscRequest,
	// sortMinSubsAmountDescRequest,sortExpeAnnuRevnueDescRequest,sortPeriodDescRequest,prodTypeSearchRequest,keyWordRequest)
	private String sortMinSubsAmountAscRequest = "sortMinSubsAmountAscRequest";
	private String sortMinSubsAmountDescRequest = "sortMinSubsAmountDescRequest";
	private String sortExpeAnnuRevnueAscRequest = "sortExpeAnnuRevnueAscRequest";
	private String sortExpeAnnuRevnueDescRequest = "sortExpeAnnuRevnueDescRequest";
	private String sortPeriodAscRequest = "sortPeriodAscRequest";
	private String sortPeriodDescRequest = "sortPeriodDescRequest";
	private String refreshType = "total";
	@SuppressWarnings("unused")
	private String orderby;
	@SuppressWarnings("unused")
	private String order;
	/**
	 * 排序相关组件
	 */
	private View view_period, view_expeAnnuRevnue, view_minSubsAmount;
	private LinearLayout line_minSubsAmount, line_expeAnnuRevnue, line_period;
	private ImageView img_minSubsAmount, img_period, img_expeAnnuRevnue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_layout);
		/**
		 * 排序相关
		 */
		view_minSubsAmount = (View) findViewById(R.id.view_follow_minSubsAmount);
		view_expeAnnuRevnue = (View) findViewById(R.id.view_follow_expeAnnuRevnue);
		view_period = (View) findViewById(R.id.view_follow_period);
		view_expeAnnuRevnue.setVisibility(View.INVISIBLE);
		view_period.setVisibility(View.INVISIBLE);
		line_minSubsAmount = (LinearLayout) findViewById(R.id.line_follow_minSubsAmount);
		line_expeAnnuRevnue = (LinearLayout) findViewById(R.id.line_follow_expeAnnuRevnue);
		line_period = (LinearLayout) findViewById(R.id.line_follow_period);
		img_minSubsAmount = (ImageView) findViewById(R.id.img_follow_minSubsAmount);
		img_period = (ImageView) findViewById(R.id.img_follow_period);
		img_expeAnnuRevnue = (ImageView) findViewById(R.id.img_follow_expeAnnuRevnue);
		line_expeAnnuRevnue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listViewProductEntitiesList.clear();
				view_expeAnnuRevnue.setVisibility(View.VISIBLE);
				view_minSubsAmount.setVisibility(View.INVISIBLE);
				view_period.setVisibility(View.INVISIBLE);
				img_minSubsAmount.setBackgroundResource(R.drawable.up_arrow);
				img_period.setBackgroundResource(R.drawable.up_arrow);
				if (flagExpeAnnuRevnueAsc == false) {
					img_expeAnnuRevnue
							.setBackgroundResource(R.drawable.down_arrow);
					refreshType = sortExpeAnnuRevnueAscRequest;
					flagExpeAnnuRevnueAsc = !flagExpeAnnuRevnueAsc;
					order = "Desc";
				} else {
					img_expeAnnuRevnue
							.setBackgroundResource(R.drawable.up_arrow);
					refreshType = sortExpeAnnuRevnueDescRequest;
					flagExpeAnnuRevnueAsc = !flagExpeAnnuRevnueAsc;
					order = "Asc";
				}
				if (HttpUtils.isNetworkConnected(FollowActivity.this) == true) {
					new Thread(Sort_ListRunnable).start();
					WaitingDialog.showDialog(FollowActivity.this);
				} else {
					Toast.makeText(FollowActivity.this,
							Constants.NO_INTENT_TIPS, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		line_minSubsAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listViewProductEntitiesList.clear();
				view_expeAnnuRevnue.setVisibility(View.INVISIBLE);
				view_minSubsAmount.setVisibility(View.VISIBLE);
				view_period.setVisibility(View.INVISIBLE);
				img_expeAnnuRevnue.setBackgroundResource(R.drawable.up_arrow);
				img_period.setBackgroundResource(R.drawable.up_arrow);
				orderby = "minSubsAmount";
				if (flagMinSubsAmountAsc == false) {
					img_minSubsAmount
							.setBackgroundResource(R.drawable.down_arrow);
					refreshType = sortMinSubsAmountAscRequest;
					flagMinSubsAmountAsc = !flagMinSubsAmountAsc;
					order = "Desc";
				} else {
					img_minSubsAmount
							.setBackgroundResource(R.drawable.up_arrow);
					refreshType = sortMinSubsAmountDescRequest;
					flagMinSubsAmountAsc = !flagMinSubsAmountAsc;
					order = "Asc";
				}
				if (HttpUtils.isNetworkConnected(FollowActivity.this) == true) {
					new Thread(Sort_ListRunnable).start();
					WaitingDialog.showDialog(FollowActivity.this);
				} else {
					Toast.makeText(FollowActivity.this,
							Constants.NO_INTENT_TIPS, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		line_period.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listViewProductEntitiesList.clear();
				view_expeAnnuRevnue.setVisibility(View.INVISIBLE);
				view_minSubsAmount.setVisibility(View.INVISIBLE);
				view_period.setVisibility(View.VISIBLE);
				img_expeAnnuRevnue.setBackgroundResource(R.drawable.up_arrow);
				img_minSubsAmount.setBackgroundResource(R.drawable.up_arrow);
				orderby = "period";
				if (flagPeriodAsc == false) {
					img_period.setBackgroundResource(R.drawable.down_arrow);
					refreshType = sortPeriodAscRequest;
					flagPeriodAsc = !flagPeriodAsc;
					order = "Desc";
				} else {
					img_period.setBackgroundResource(R.drawable.up_arrow);
					refreshType = sortPeriodDescRequest;
					flagPeriodAsc = !flagPeriodAsc;
					order = "Asc";
				}
				if (HttpUtils.isNetworkConnected(FollowActivity.this) == true) {
					new Thread(Sort_ListRunnable).start();
					WaitingDialog.showDialog(FollowActivity.this);
				} else {
					Toast.makeText(FollowActivity.this,
							Constants.NO_INTENT_TIPS, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		/**
		 * 保留小数点后两位
		 */
		df = new DecimalFormat("0.00");
		followListview = (MyListView) findViewById(R.id.followListview);
		right_img = (ImageView) findViewById(R.id.right_img);
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText("我的产品");
		right_img.setBackgroundResource(R.drawable.search);
		initData();
		back_img = (ImageView) findViewById(R.id.back_img);
		back_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		right_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menu.showMenu();
			}
		});
		/**
		 * 侧滑搜索
		 */
		context = this;
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		View view = LayoutInflater.from(this).inflate(
				R.layout.search_popuwindow, null);
		edit_search = (EditText) view.findViewById(R.id.edit_search);
		menu.setMenu(view);
		lv_search = (ListView) view.findViewById(R.id.lv_search);
		searchListViewAdapter = new SearchListViewAdapter(strs,
				FollowActivity.this);
		lv_search.setAdapter(searchListViewAdapter);
		lv_search.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				tv_search = (TextView) view.findViewById(R.id.tv_search);
				search_ProdType = CommonUtils.CodeForName(tv_search.getText()
						.toString());
				if (HttpUtils.isNetworkConnected(FollowActivity.this) == true) {
					new Thread(prodTypeSearch_ListRunnable).start();
					WaitingDialog.showDialog(FollowActivity.this);
				} else {
					menu.toggle(true);
					Toast.makeText(FollowActivity.this,
							Constants.NO_INTENT_TIPS, Toast.LENGTH_SHORT)
							.show();
				}
			};
		});
		edit_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				searchListViewAdapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		btn_search = (Button) view.findViewById(R.id.btn_search);
		/**
		 * 取消搜索按钮事件
		 */
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_keyWord = edit_search.getText().toString();
				CommonUtils.HideKeyBoard(FollowActivity.this, edit_search);
				if (HttpUtils.isNetworkConnected(FollowActivity.this) == true) {
					new Thread(keyWordSearch_ListRunnable).start();
					WaitingDialog.showDialog(FollowActivity.this);
				} else {
					menu.toggle(true);
					Toast.makeText(FollowActivity.this,
							Constants.NO_INTENT_TIPS, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		/**
		 * 监听下拉刷新相关
		 */
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.follow_pull_refresh_scrollview);
		listViewProductEntitiesList = new ArrayList<ProductEntity>();
		if (HttpUtils.isNetworkConnected(FollowActivity.this) == true) {
			mScrollView = mPullRefreshScrollView.getRefreshableView();
			WaitingDialog.showDialog(FollowActivity.this);
			new Thread(Sort_ListRunnable).start();// 线程启动
			/**
			 * UI线程中设置Adapter
			 */
			mHandler = new Handler() {
				public void handleMessage(Message msg) {// 此方法在ui线程运行
					switch (msg.what) {
					case 0:
						listViewAdapter = new ListViewAdapter(
								FollowActivity.this,
								listViewProductEntitiesList);
						followListview.setAdapter(listViewAdapter);
						WaitingDialog.dismissDialog();
						break;
					case 1:
						// 下拉刷新没有新数据的时候执行此步
						Toast.makeText(FollowActivity.this, "沒有更多数据了",
								Toast.LENGTH_SHORT).show();
						break;
					case 2:
						WaitingDialog.dismissDialog();
						break;

					case 3:
						Bundle bundle = msg.getData();
						String pageNumber = bundle.getString("pageNumber");
						if (pageNumber.equals("1")) {
							// 点击搜索没有数据的时候执行此步，进行提示
							Toast.makeText(FollowActivity.this,
									"亲，搜索  " + search_text + " 目前没有你的数据",
									Toast.LENGTH_LONG).show();
						} else {
							// 搜索后下拉刷新没有数据的时候执行此步，进行提示
							Toast.makeText(FollowActivity.this,
									"亲， " + search_text + " 没有更多数据了 ",
									Toast.LENGTH_LONG).show();
						}
						break;
					case 9:
						listViewAdapter.notifyDataSetChanged();
						WaitingDialog.dismissDialog();
						menu.toggle(true);
						mPullRefreshScrollView.onRefreshComplete();
						break;
					case 10:
						listViewAdapter.notifyDataSetChanged();
						WaitingDialog.dismissDialog();
						menu.toggle(true);
						mPullRefreshScrollView.onRefreshComplete();
					case 11:
						listViewAdapter.notifyDataSetChanged();
						WaitingDialog.dismissDialog();
						mPullRefreshScrollView.onRefreshComplete();
						break;
					default:
						break;
					}
				}
			};
			mPullRefreshScrollView
					.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
						@Override
						public void onRefresh(
								PullToRefreshBase<ScrollView> refreshView) {
							// 每次下拉刷新前都判断网络是否异常
							if (HttpUtils
									.isNetworkConnected(FollowActivity.this) == true) {

								WaitingDialog.showDialog(FollowActivity.this);
								// 如果遇到网络异常，则重新加载广告图片
								pageNumber++;
								new Thread(Sort_ListRunnable).start();
								mPullRefreshScrollView.onRefreshComplete();
							} else {
								mHandler.sendEmptyMessage(3);
							}
						}
					});
			followListview.setOnItemClickListener(new onClickview());
		} else {
			Toast.makeText(FollowActivity.this, Constants.NO_INTENT_TIPS, 10)
					.show();
		}
	}

	class onClickview implements android.widget.AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			Intent intent = new Intent(FollowActivity.this,
					DetailProductActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("prodTypeCode",
					listViewProductEntitiesList.get(position).getProdTypeCode());
			bundle.putString("ID", listViewProductEntitiesList.get(position)
					.getID());
			bundle.putString(
					"imageString",
					CommonUtils.TypeforCode(listViewProductEntitiesList.get(
							position).getProdTypeCode()));
//			bundle.putString("ExpeAnnuRevnue",
//					listViewProductEntitiesList.get(position)
//							.getExpeAnnuRevnue());
//			bundle.putString("MinSubsAmount",
//					listViewProductEntitiesList.get(position)
//							.getMinSubsAmount());
			bundle.putString("name", listViewProductEntitiesList.get(position)
					.getName());
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		};
	};

	/**
	 * GetprodTypeSearchListJson线程
	 */
	Runnable prodTypeSearch_ListRunnable = new Runnable() {
		@Override
		public void run() {
			listViewProductEntitiesList.clear();
			pageNumber = 1;
			listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL, ""
					+ getListJsonProdType, pageNumber);
			mHandler.sendEmptyMessage(9);
		}
	};
	/**
	 * GetkeyWordSearchListJson线程
	 */
	Runnable keyWordSearch_ListRunnable = new Runnable() {
		@Override
		public void run() {
			listViewProductEntitiesList.clear();
			pageNumber = 1;
			listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL, ""
					+ getListJsonKeyWord, pageNumber);
			mHandler.sendEmptyMessage(10);
		}
	};
	/**
	 * GetSortListJson线程
	 */
	Runnable Sort_ListRunnable = new Runnable() {
		@Override
		public void run() {
			if (refreshType.equals(sortMinSubsAmountAscRequest)) {
				listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL,
						"" + getListJsonMinSubsAmountAsc, pageNumber);
				mHandler.sendEmptyMessage(11);
			} else if (refreshType.equals(sortExpeAnnuRevnueAscRequest)) {
				listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL,
						"" + getListJsonExpeAnnuRevnueAsc, pageNumber);
				mHandler.sendEmptyMessage(11);
			} else if (refreshType.equals(sortPeriodAscRequest)) {
				listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL,
						"" + getListJsonPeriodAsc, pageNumber);
				mHandler.sendEmptyMessage(11);
			} else if (refreshType.equals(sortMinSubsAmountDescRequest)) {
				listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL,
						"" + getListJsonMinSubsAmountDesc, pageNumber);
				mHandler.sendEmptyMessage(11);
			} else if (refreshType.equals(sortExpeAnnuRevnueDescRequest)) {
				listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL,
						"" + getListJsonExpeAnnuRevnueDesc, pageNumber);
				mHandler.sendEmptyMessage(11);
			} else if (refreshType.equals(sortPeriodDescRequest)) {
				listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL,
						"" + getListJsonPeriodDesc, pageNumber);
				mHandler.sendEmptyMessage(11);

			} else {
				listViewProductEntitiesList = GetListJson(URLs.FOLLOWLIST_URL,
						"" + getListJsonList, pageNumber);
				mHandler.sendEmptyMessage(0);
			}
		}
	};

	/**
	 * GetListJson方法
	 */
	private ArrayList<ProductEntity> GetListJson(String url, String type,
			int pageNumber) {
		/**
		 * 获取用户memberId
		 */
		SharedPreferences userPreferences = getSharedPreferences(
				"umeng_general_config", Context.MODE_PRIVATE);
		memberId = userPreferences.getString("ID", "").trim();
		if (type.equals(getListJsonList)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonProdType)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("prodTypeCode", search_ProdType));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonKeyWord)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("keyWord", search_keyWord));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonMinSubsAmountAsc)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("orderby", "minSubsAmount"));
			params.add(new BasicNameValuePair("order", "Asc"));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonExpeAnnuRevnueAsc)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("orderby", "expeAnnuRevnue"));
			params.add(new BasicNameValuePair("order", "Asc"));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonPeriodAsc)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("orderby", "period"));
			params.add(new BasicNameValuePair("order", "Asc"));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonMinSubsAmountDesc)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("orderby", "minSubsAmount"));
			params.add(new BasicNameValuePair("order", "Desc"));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonExpeAnnuRevnueDesc)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("orderby", "expeAnnuRevnue"));
			params.add(new BasicNameValuePair("order", "Desc"));
			dataJson = HttpUtils.request(params, url);
		} else if (type.equals(getListJsonPeriodDesc)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageNumber", String
					.valueOf(pageNumber)));
			params.add(new BasicNameValuePair("memberId", memberId));
			params.add(new BasicNameValuePair("orderby", "period"));
			params.add(new BasicNameValuePair("order", "Desc"));
			dataJson = HttpUtils.request(params, url);
		}

		if (dataJson.equals("false")) {
			// 金矿首页下拉刷新没有数据的时候执行此步，进行提示
			mHandler.sendEmptyMessage(1);
			return listViewProductEntitiesList;
		}

		try {
			jsonArray = new JSONArray(dataJson);
			for (int i = 0; i < jsonArray.length(); i++) {
				item = jsonArray.getJSONObject(i);
				String string = item.getString("prodTypeName");
				if ("银行类".equals(string)) {
					if (item.getString("prodTypeName").equals("")) {
						prodTypeName = null;
					} else {
						prodTypeName = item.getString("prodTypeName");
					}
					if (String.valueOf(item.getString("period")).equals("null")) {
						period = 0;
					} else {
						period = item.getInt("period");
					}
					if (item.getString("prodTypeCode").equals("")) {
						prodTypeCode = null;
					} else {
						prodTypeCode = item.getString("prodTypeCode");
					}
					if (item.getString("productId").equals("")) {
						productId = null;
					} else {
						productId = item.getString("productId");
					}
					if (item.getString("name").equals("")) {
						name = null;
					} else {
						name = item.getString("name");
					}
					if (String.valueOf(item.getString("minSubsAmount")).equals(
							"null")) {
						minSubsAmount = "";
					} else {
						minSubsAmount = ChineseMoneyUtils
								.numWithDigitArray(item
										.getDouble("minSubsAmount"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("minSubsAmount"))[1];
					}
					if (String.valueOf(item.getString("expeAnnuRevnue"))
							.equals("null")) {
						expeAnnuRevnue = "0.00";
					} else {
						expeAnnuRevnue = df.format(item
								.getDouble("expeAnnuRevnue"));
					}
					if (item.getString("shareSubject").equals("")) {
						shareSubject = "";
					} else {
						shareSubject = item.getString("shareSubject");
					}
					if (item.getString("productSubject").equals("")) {
						productSubject = "";
					} else {
						productSubject = item.getString("productSubject");
					}
					tempEntity = new ProductEntity();
					tempEntity.setShareSubject(shareSubject);
					tempEntity.setProdTypeCode(prodTypeCode);
					tempEntity.setPeriod(period);
//					tempEntity.setExpeAnnuRevnue(expeAnnuRevnue);
//					tempEntity.setMinSubsAmount(minSubsAmount);
					tempEntity.setName(name);
					tempEntity.setID(productId);
					tempEntity.setProdTypeName(prodTypeName);
					tempEntity.setProductSubject(productSubject);
				} else if ("保险类".equals(string)) {
					if (item.getString("prodTypeName").equals("")) {
						prodTypeName = null;
					} else {
						prodTypeName = item.getString("prodTypeName");
					}
					if (item.getString("insuranceType").equals("")) {
						insuranceType = null;
					} else {
						insuranceType = item.getString("insuranceType");
					}
					if (String.valueOf(item.getString("period")).equals("null")) {
						period = 0;
					} else {
						period = item.getInt("period");
					}
					if (item.getString("issuer").equals("")) {
						issuer = null;
					} else {
						issuer = item.getString("issuer");
					}
					if (item.getString("prodTypeCode").equals("")) {
						prodTypeCode = null;
					} else {
						prodTypeCode = item.getString("prodTypeCode");
					}
					if (item.getString("productId").equals("")) {
						productId = null;
					} else {
						productId = item.getString("productId");
					}
					if (item.getString("name").equals("")) {
						name = null;
					} else {
						name = item.getString("name");
					}
					if (String.valueOf(item.getString("minSubsAmount")).equals(
							"null")) {
						minSubsAmount = "";
					} else {
						minSubsAmount = ChineseMoneyUtils
								.numWithDigitArray(item
										.getDouble("minSubsAmount"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("minSubsAmount"))[1];
					}
					if (String.valueOf(item.getString("expeAnnuRevnue"))
							.equals("null")) {
						expeAnnuRevnue = "0.00";
					} else {
						expeAnnuRevnue = df.format(item
								.getDouble("expeAnnuRevnue"));
					}
					if (item.getString("shareSubject").equals("")) {
						shareSubject = "";
					} else {
						shareSubject = item.getString("shareSubject");
					}
					if (item.getString("productSubject").equals("")) {
						productSubject = "";
					} else {
						productSubject = item.getString("productSubject");
					}
					tempEntity2 = new ProductEntity();
					tempEntity2.setShareSubject(shareSubject);
					tempEntity2.setInsuranceType(insuranceType);
					tempEntity2.setPeriod(period);
					tempEntity2.setIssuer(issuer);
					tempEntity2.setProdTypeName(prodTypeName);
//					tempEntity2.setExpeAnnuRevnue(expeAnnuRevnue);
//					tempEntity2.setMinSubsAmount(minSubsAmount);
					tempEntity2.setName(name);
					tempEntity2.setProdTypeCode(prodTypeCode);
					tempEntity2.setID(productId);
					tempEntity2.setProductSubject(productSubject);
				} else if ("公募基金类".equals(string)) {
					if (item.getString("prodTypeName").equals("")) {
						prodTypeName = null;
					} else {
						prodTypeName = item.getString("prodTypeName");
					}
					if (item.getString("fundDirector").equals("")) {
						fundDirector = null;
					} else {
						fundDirector = item.getString("fundDirector");
					}
					if (item.getString("fundType").equals("")) {
						fundType = null;
					} else {
						fundType = item.getString("fundType");
					}
					if (item.getString("prodTypeCode").equals("")) {
						prodTypeCode = null;
					} else {
						prodTypeCode = item.getString("prodTypeCode");
					}
					if (item.getString("productId").equals("")) {
						productId = null;
					} else {
						productId = item.getString("productId");
					}
					if (item.getString("name").equals("")) {
						name = null;
					} else {
						name = item.getString("name");
					}
					if (String.valueOf(item.getString("minSubsAmount")).equals(
							"null")) {
						minSubsAmount = "";
					} else {
						minSubsAmount = ChineseMoneyUtils
								.numWithDigitArray(item
										.getDouble("minSubsAmount"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("minSubsAmount"))[1];
					}
					if (String.valueOf(item.getString("expeAnnuRevnue"))
							.equals("null")) {
						expeAnnuRevnue = "0.00";
					} else {
						expeAnnuRevnue = df.format(item
								.getDouble("expeAnnuRevnue"));
					}
					if (item.getString("shareSubject").equals("")) {
						shareSubject = "";
					} else {
						shareSubject = item.getString("shareSubject");
					}
					if (item.getString("productSubject").equals("")) {
						productSubject = "";
					} else {
						productSubject = item.getString("productSubject");
					}
					tempEntity3 = new ProductEntity();
					tempEntity3.setFundDirector(fundDirector);
					tempEntity3.setFundType(fundType);
					tempEntity3.setProdTypeName(prodTypeName);
//					tempEntity3.setExpeAnnuRevnue(expeAnnuRevnue);
//					tempEntity3.setMinSubsAmount(minSubsAmount);
					tempEntity3.setName(name);
					tempEntity3.setProdTypeCode(prodTypeCode);
					tempEntity3.setID(productId);
					tempEntity3.setShareSubject(shareSubject);
					tempEntity3.setProductSubject(productSubject);
				} else if ("私募基金类".equals(string)) {
					if (item.getString("fundType").equals("equity")) {
						if (item.getString("prodTypeName").equals("")) {
							prodTypeName = null;
						} else {
							prodTypeName = item.getString("prodTypeName");
						}
						if (String.valueOf(item.getString("period")).equals(
								"null")) {
							period = 0;
						} else {
							period = item.getInt("period");
						}
						if (item.getString("fundDirector").equals("")) {
							fundDirector = null;
						} else {
							fundDirector = item.getString("fundDirector");
						}
						if (item.getString("fundType").equals("")) {
							fundType = null;
						} else {
							fundType = item.getString("fundType");
						}
						if (item.getString("prodTypeCode").equals("")) {
							prodTypeCode = null;
						} else {
							prodTypeCode = item.getString("prodTypeCode");
						}
						if (item.getString("id").equals("")) {
							productId = null;
						} else {
							productId = item.getString("productId");
						}
						if (item.getString("name").equals("")) {
							name = null;
						} else {
							name = item.getString("name");
						}
						if (String.valueOf(item.getString("minSubsAmount"))
								.equals("null")) {
							minSubsAmount = "";
						} else {
							minSubsAmount = ChineseMoneyUtils
									.numWithDigitArray(item
											.getDouble("minSubsAmount"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("minSubsAmount"))[1];
						}
						if (String.valueOf(item.getString("expeAnnuRevnue"))
								.equals("null")) {
							expeAnnuRevnue = "0.00";
						} else {
							expeAnnuRevnue = df.format(item
									.getDouble("expeAnnuRevnue"));
						}
						if (item.getString("shareSubject").equals("")) {
							shareSubject = "";
						} else {
							shareSubject = item.getString("shareSubject");
						}
						if (item.getString("productSubject").equals("")) {
							productSubject = "";
						} else {
							productSubject = item.getString("productSubject");
						}
						tempEntity4 = new ProductEntity();
						tempEntity4.setPeriod(period);
						tempEntity4.setFundDirector(fundDirector);
						tempEntity4.setProdTypeName(prodTypeName);
						tempEntity4.setProdDetail(prodDetail);
						tempEntity4.setFundType(fundType);
//						tempEntity4.setExpeAnnuRevnue(expeAnnuRevnue);
//						tempEntity4.setMinSubsAmount(minSubsAmount);
						tempEntity4.setName(name);
						tempEntity4.setProdTypeCode(prodTypeCode);
						tempEntity4.setID(productId);
						tempEntity4.setShareSubject(shareSubject);
						tempEntity4.setProductSubject(productSubject);
					} else if (item.getString("fundType") == "claim") {
						if (item.getString("prodTypeName").equals("")) {
							prodTypeName = null;
						} else {
							prodTypeName = item.getString("prodTypeName");
						}
						if (String.valueOf(item.getString("period")).equals(
								"null")) {
							period = 0;
						} else {
							period = item.getInt("period");
						}
						if (item.getString("investDirection").equals("")) {
							investDirection = null;
						} else {
							investDirection = item.getString("investDirection");
						}
						if (item.getString("fundType").equals("")) {
							fundType = null;
						} else {
							fundType = item.getString("fundType");
						}
						if (item.getString("prodTypeCode").equals("")) {
							prodTypeCode = null;
						} else {
							prodTypeCode = item.getString("prodTypeCode");
						}
						if (item.getString("id").equals("")) {
							productId = null;
						} else {
							productId = item.getString("productId");
						}
						if (item.getString("name").equals("")) {
							name = null;
						} else {
							name = item.getString("name");
						}
						if (String.valueOf(item.getString("minSubsAmount"))
								.equals("null")) {
							minSubsAmount = "";
						} else {
							minSubsAmount = ChineseMoneyUtils
									.numWithDigitArray(item
											.getDouble("minSubsAmount"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("minSubsAmount"))[1];
						}
						if (String.valueOf(item.getString("expeAnnuRevnue"))
								.equals("null")) {
							expeAnnuRevnue = "0.00";
						} else {
							expeAnnuRevnue = df.format(item
									.getDouble("expeAnnuRevnue"));
						}
						if (item.getString("issuerType").equals("")) {
							issuerType = "";
						} else {
							issuerType = item.getString("issuerType");
						}
						if (item.getString("shareSubject").equals("")) {
							shareSubject = "";
						} else {
							shareSubject = item.getString("shareSubject");
						}
						if (item.getString("productSubject").equals("")) {
							productSubject = "";
						} else {
							productSubject = item.getString("productSubject");
						}
						tempEntity4 = new ProductEntity();
						tempEntity4.setPeriod(period);
						tempEntity4.setInvestDirection(investDirection);
						tempEntity4.setProdTypeName(prodTypeName);
						tempEntity4.setFundType(fundType);
//						tempEntity4.setExpeAnnuRevnue(expeAnnuRevnue);
//						tempEntity4.setMinSubsAmount(minSubsAmount);
						tempEntity4.setName(name);
						tempEntity4.setProdTypeCode(prodTypeCode);
						tempEntity4.setID(productId);
						tempEntity4.setShareSubject(shareSubject);
						tempEntity4.setProductSubject(productSubject);
					} else if (item.getString("fundType").equals("stock")) {
						if (item.getString("prodTypeName").equals("")) {
							prodTypeName = null;
						} else {
							prodTypeName = item.getString("prodTypeName");
						}
						if (item.getString("fundDirector").equals("")) {
							fundDirector = null;
						} else {
							fundDirector = item.getString("fundDirector");
						}
						if (String.valueOf(item.getString("topGain")).equals(
								"null")) {
							topGain = "";
						} else {
							topGain = ChineseMoneyUtils.numWithDigitArray(item
									.getDouble("topGain"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("topGain"))[1];
						}
						if (item.getString("fundType").equals("")) {
							fundType = null;
						} else {
							fundType = item.getString("fundType");
						}
						if (item.getString("prodTypeCode").equals("")) {
							prodTypeCode = null;
						} else {
							prodTypeCode = item.getString("prodTypeCode");
						}
						if (item.getString("id").equals("")) {
							productId = null;
						} else {
							productId = item.getString("productId");
						}
						if (item.getString("name").equals("")) {
							name = null;
						} else {
							name = item.getString("name");
						}
						if (String.valueOf(item.getString("minSubsAmount"))
								.equals("null")) {
							minSubsAmount = "";
						} else {
							minSubsAmount = ChineseMoneyUtils
									.numWithDigitArray(item
											.getDouble("minSubsAmount"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("minSubsAmount"))[1];
						}
						if (String.valueOf(item.getString("expeAnnuRevnue"))
								.equals("null")) {
							expeAnnuRevnue = "0.00";
						} else {
							expeAnnuRevnue = df.format(item
									.getDouble("expeAnnuRevnue"));
						}
						if (item.getString("shareSubject").equals("")) {
							shareSubject = "";
						} else {
							shareSubject = item.getString("shareSubject");
						}
						if (item.getString("productSubject").equals("")) {
							productSubject = "";
						} else {
							productSubject = item.getString("productSubject");
						}
						tempEntity4 = new ProductEntity();
//						tempEntity4.setTopGain(topGain);
						tempEntity4.setFundDirector(fundDirector);
						tempEntity4.setProdTypeName(prodTypeName);
						tempEntity4.setFundType(fundType);
//						tempEntity4.setExpeAnnuRevnue(expeAnnuRevnue);
//						tempEntity4.setMinSubsAmount(minSubsAmount);
						tempEntity4.setName(name);
						tempEntity4.setProdTypeCode(prodTypeCode);
						tempEntity4.setID(productId);
						tempEntity4.setIssuerType(issuerType);
						tempEntity4.setShareSubject(shareSubject);
						tempEntity4.setProductSubject(productSubject);
					} else if (item.getString("fundType").equals("exchange")) {
						if (item.getString("prodTypeName").equals("")) {
							prodTypeName = null;
						} else {
							prodTypeName = item.getString("prodTypeName");
						}

						if (item.getString("fundDirector").equals("")) {
							fundDirector = null;
						} else {
							fundDirector = item.getString("fundDirector");
						}
						if (String.valueOf(item.getString("topGain")).equals(
								"null")) {
							topGain = "";
						} else {
							topGain = ChineseMoneyUtils.numWithDigitArray(item
									.getDouble("topGain"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("topGain"))[1];
						}
						if (item.getString("fundType").equals("")) {
							fundType = null;
						} else {
							fundType = item.getString("fundType");
						}
						if (item.getString("prodTypeCode").equals("")) {
							prodTypeCode = null;
						} else {
							prodTypeCode = item.getString("prodTypeCode");
						}
						if (item.getString("id").equals("")) {
							productId = null;
						} else {
							productId = item.getString("productId");
						}
						if (item.getString("name").equals("")) {
							name = null;
						} else {
							name = item.getString("name");
						}
						if (String.valueOf(item.getString("minSubsAmount"))
								.equals("null")) {
							minSubsAmount = "";
						} else {
							minSubsAmount = ChineseMoneyUtils
									.numWithDigitArray(item
											.getDouble("minSubsAmount"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("minSubsAmount"))[1];
						}
						if (String.valueOf(item.getString("expeAnnuRevnue"))
								.equals("null")) {
							expeAnnuRevnue = "0.00";
						} else {
							expeAnnuRevnue = df.format(item
									.getDouble("expeAnnuRevnue"));
						}
						if (item.getString("shareSubject").equals("")) {
							shareSubject = "";
						} else {
							shareSubject = item.getString("shareSubject");
						}
						if (item.getString("productSubject").equals("")) {
							productSubject = "";
						} else {
							productSubject = item.getString("productSubject");
						}
						tempEntity4 = new ProductEntity();
						tempEntity4.setFundDirector(fundDirector);
						tempEntity4.setFundType(fundType);
						tempEntity4.setProdTypeName(prodTypeName);
//						tempEntity4.setExpeAnnuRevnue(expeAnnuRevnue);
//						tempEntity4.setMinSubsAmount(minSubsAmount);
						tempEntity4.setName(name);
						tempEntity4.setProdTypeCode(prodTypeCode);
						tempEntity4.setID(productId);
						tempEntity4.setShareSubject(shareSubject);
						tempEntity4.setProductSubject(productSubject);
					} else if (item.getString("fundType").equals("derivative")) {
						if (item.getString("prodTypeName").equals("")) {
							prodTypeName = null;
						} else {
							prodTypeName = item.getString("prodTypeName");
						}

						if (item.getString("fundDirector").equals("")) {
							fundDirector = null;
						} else {
							fundDirector = item.getString("fundDirector");
						}
						if (String.valueOf(item.getString("topGain")).equals(
								"null")) {
							topGain = "";
						} else {
							topGain = ChineseMoneyUtils.numWithDigitArray(item
									.getDouble("topGain"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("topGain"))[1];
						}
						if (item.getString("fundType").equals("")) {
							fundType = null;
						} else {
							fundType = item.getString("fundType");
						}
						if (item.getString("prodTypeCode").equals("")) {
							prodTypeCode = null;
						} else {
							prodTypeCode = item.getString("prodTypeCode");
						}
						if (item.getString("productId").equals("")) {
							productId = null;
						} else {
							productId = item.getString("productId");
						}
						if (item.getString("name").equals("")) {
							name = null;
						} else {
							name = item.getString("name");
						}
						if (String.valueOf(item.getString("minSubsAmount"))
								.equals("null")) {
							minSubsAmount = "";
						} else {
							minSubsAmount = ChineseMoneyUtils
									.numWithDigitArray(item
											.getDouble("minSubsAmount"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("minSubsAmount"))[1];
						}
						if (String.valueOf(item.getString("expeAnnuRevnue"))
								.equals("null")) {
							expeAnnuRevnue = "0.00";
						} else {
							expeAnnuRevnue = df.format(item
									.getDouble("expeAnnuRevnue"));
						}
						if (item.getString("shareSubject").equals("")) {
							shareSubject = "";
						} else {
							shareSubject = item.getString("shareSubject");
						}
						if (item.getString("productSubject").equals("")) {
							productSubject = "";
						} else {
							productSubject = item.getString("productSubject");
						}
						tempEntity4 = new ProductEntity();
						tempEntity4.setFundDirector(fundDirector);
//						tempEntity4.setTopGain(topGain);
//						tempEntity4.setProdTypeName(prodTypeName);
//						tempEntity4.setProdDetail(prodDetail);
//						tempEntity4.setFundType(fundType);
//						tempEntity4.setExpeAnnuRevnue(expeAnnuRevnue);
//						tempEntity4.setMinSubsAmount(minSubsAmount);
						tempEntity4.setName(name);
						tempEntity4.setProdTypeCode(prodTypeCode);
						tempEntity4.setID(productId);
						tempEntity4.setShareSubject(shareSubject);
						tempEntity4.setProductSubject(productSubject);
					} else if (item.getString("fundType").equals("bond")) {
						if (item.getString("prodTypeName").equals("")) {
							prodTypeName = null;
						} else {
							prodTypeName = item.getString("prodTypeName");
						}

						if (item.getString("fundDirector").equals("")) {
							fundDirector = null;
						} else {
							fundDirector = item.getString("fundDirector");
						}
						if (String.valueOf(item.getString("topGain")).equals(
								"null")) {
							topGain = "";
						} else {
							topGain = ChineseMoneyUtils.numWithDigitArray(item
									.getDouble("topGain"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("topGain"))[1];
						}
						if (item.getString("fundType").equals("")) {
							fundType = null;
						} else {
							fundType = item.getString("fundType");
						}
						if (item.getString("prodTypeCode").equals("")) {
							prodTypeCode = null;
						} else {
							prodTypeCode = item.getString("prodTypeCode");
						}
						if (item.getString("id").equals("")) {
							productId = null;
						} else {
							productId = item.getString("productId");
						}
						if (item.getString("name").equals("")) {
							name = null;
						} else {
							name = item.getString("name");
						}
						if (String.valueOf(item.getDouble("minSubsAmount"))
								.equals("")) {
							minSubsAmount = "";
						} else {
							minSubsAmount = ChineseMoneyUtils
									.numWithDigitArray(item
											.getDouble("minSubsAmount"))[0]
									+ ChineseMoneyUtils.numWithDigitArray(item
											.getDouble("minSubsAmount"))[1];
						}
						if (String.valueOf(item.getString("expeAnnuRevnue"))
								.equals("null")) {
							expeAnnuRevnue = "0.00";
						} else {
							expeAnnuRevnue = df.format(item
									.getDouble("expeAnnuRevnue"));
						}
						if (item.getString("shareSubject").equals("")) {
							shareSubject = "";
						} else {
							shareSubject = item.getString("shareSubject");
						}
						if (item.getString("productSubject").equals("")) {
							productSubject = "";
						} else {
							productSubject = item.getString("productSubject");
						}
						tempEntity4 = new ProductEntity();
//						tempEntity4.setTopGain(topGain);
//						tempEntity4.setFundDirector(fundDirector);
//						tempEntity4.setProdTypeName(prodTypeName);
//						tempEntity4.setFundType(fundType);
//						tempEntity4.setExpeAnnuRevnue(expeAnnuRevnue);
//						tempEntity4.setMinSubsAmount(minSubsAmount);
						tempEntity4.setName(name);
						tempEntity4.setProdTypeCode(prodTypeCode);
						tempEntity4.setID(productId);
						tempEntity4.setShareSubject(shareSubject);
						tempEntity4.setProductSubject(productSubject);
					}
				} else if ("信托类".equals(string)) {
					if (item.getString("prodTypeName").equals("")) {
						prodTypeName = null;
					} else {
						prodTypeName = item.getString("prodTypeName");
					}
					if (item.getString("investDirection").equals("")) {
						investDirection = null;
					} else {
						investDirection = item.getString("investDirection");
					}
					if (String.valueOf(item.getString("topGain"))
							.equals("null")) {
						topGain = "";
					} else {
						topGain = ChineseMoneyUtils.numWithDigitArray(item
								.getDouble("topGain"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("topGain"))[1];
					}
					if (String.valueOf(item.getString("period")).equals("null")) {
						period = 0;
					} else {
						period = item.getInt("period");
					}
					if (item.getString("prodTypeCode").equals("")) {
						prodTypeCode = null;
					} else {
						prodTypeCode = item.getString("prodTypeCode");
					}
					if (item.getString("id").equals("")) {
						productId = null;
					} else {
						productId = item.getString("productId");
					}
					if (item.getString("name").equals("")) {
						name = null;
					} else {
						name = item.getString("name");
					}
					if (String.valueOf(item.getString("minSubsAmount")).equals(
							"null")) {
						minSubsAmount = "";
					} else {
						minSubsAmount = ChineseMoneyUtils
								.numWithDigitArray(item
										.getDouble("minSubsAmount"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("minSubsAmount"))[1];
					}
					if (String.valueOf(item.getString("expeAnnuRevnue"))
							.equals("null")) {
						expeAnnuRevnue = "0.00";
					} else {
						expeAnnuRevnue = df.format(item
								.getDouble("expeAnnuRevnue"));
					}
					if (item.getString("shareSubject").equals("")) {
						shareSubject = "";
					} else {
						shareSubject = item.getString("shareSubject");
					}
					if (item.getString("productSubject").equals("")) {
						productSubject = "";
					} else {
						productSubject = item.getString("productSubject");
					}
					tempEntity5 = new ProductEntity();
//					tempEntity5.setTopGain(topGain);
//					tempEntity5.setPeriod(period);
//					tempEntity5.setInvestDirection(investDirection);
//					tempEntity5.setProdTypeName(prodTypeName);
//					tempEntity5.setExpeAnnuRevnue(expeAnnuRevnue);
//					tempEntity5.setMinSubsAmount(minSubsAmount);
					tempEntity5.setName(name);
					tempEntity5.setProdTypeCode(prodTypeCode);
					tempEntity5.setID(productId);
					tempEntity5.setShareSubject(shareSubject);
					tempEntity5.setProductSubject(productSubject);
				} else if ("资管类".equals(string)) {
					if (item.getString("prodTypeName").equals("")) {
						prodTypeName = null;
					} else {
						prodTypeName = item.getString("prodTypeName");
					}
					if (item.getString("investDirection").equals("")) {
						investDirection = null;
					} else {
						investDirection = item.getString("investDirection");
					}
					if (String.valueOf(item.getString("topGain"))
							.equals("null")) {
						topGain = "";
					} else {
						topGain = ChineseMoneyUtils.numWithDigitArray(item
								.getDouble("topGain"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("topGain"))[1];
					}
					if (String.valueOf(item.getString("period")).equals("null")) {
						period = 0;
					} else {
						period = item.getInt("period");
					}
					if (item.getString("prodTypeCode").equals("")) {
						prodTypeCode = null;
					} else {
						prodTypeCode = item.getString("prodTypeCode");
					}
					if (item.getString("id").equals("")) {
						productId = null;
					} else {
						productId = item.getString("productId");
					}
					if (item.getString("name").equals("")) {
						name = null;
					} else {
						name = item.getString("name");
					}
					if (String.valueOf(item.getString("minSubsAmount")).equals(
							"null")) {
						minSubsAmount = "";
					} else {
						minSubsAmount = ChineseMoneyUtils
								.numWithDigitArray(item
										.getDouble("minSubsAmount"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("minSubsAmount"))[1];
					}
					if (String.valueOf(item.getString("expeAnnuRevnue"))
							.equals("null")) {
						expeAnnuRevnue = "0.00";
					} else {
						expeAnnuRevnue = df.format(item
								.getDouble("expeAnnuRevnue"));
					}
					if (item.getString("shareSubject").equals("")) {
						shareSubject = "";
					} else {
						shareSubject = item.getString("shareSubject");
					}
					if (item.getString("productSubject").equals("")) {
						productSubject = "";
					} else {
						productSubject = item.getString("productSubject");
					}
					tempEntity6 = new ProductEntity();
//					tempEntity6.setTopGain(topGain);
//					tempEntity6.setPeriod(period);
//					tempEntity6.setInvestDirection(investDirection);
//					tempEntity6.setProdTypeName(prodTypeName);
//					tempEntity6.setExpeAnnuRevnue(expeAnnuRevnue);
//					tempEntity6.setMinSubsAmount(minSubsAmount);
					tempEntity6.setName(name);
					tempEntity6.setProdTypeCode(prodTypeCode);
					tempEntity6.setID(productId);
					tempEntity6.setShareSubject(shareSubject);
					tempEntity6.setProductSubject(productSubject);
				} else if ("债权众筹类".equals(string)) {
					if (item.getString("prodTypeName").equals("")) {
						prodTypeName = null;
					} else {
						prodTypeName = item.getString("prodTypeName");
					}
					if (String.valueOf(item.getString("topGain"))
							.equals("null")) {
						topGain = "";
					} else {
						topGain = ChineseMoneyUtils.numWithDigitArray(item
								.getDouble("topGain"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("topGain"))[1];
					}
					if (String.valueOf(item.getString("period")).equals("null")) {
						period = 0;
					} else {
						period = item.getInt("period");
					}
					if (item.getString("fundDirector").equals("")) {
						fundDirector = null;
					} else {
						fundDirector = item.getString("fundDirector");
					}
					if (item.getString("prodTypeCode").equals("")) {
						prodTypeCode = null;
					} else {
						prodTypeCode = item.getString("prodTypeCode");
					}
					if (item.getString("id").equals("")) {
						productId = null;
					} else {
						productId = item.getString("productId");
					}
					if (item.getString("name").equals("")) {
						name = null;
					} else {
						name = item.getString("name");
					}
					if (String.valueOf(item.getString("minSubsAmount")).equals(
							"null")) {
						minSubsAmount = "";
					} else {
						minSubsAmount = ChineseMoneyUtils
								.numWithDigitArray(item
										.getDouble("minSubsAmount"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("minSubsAmount"))[1];
					}
					if (String.valueOf(item.getString("expeAnnuRevnue"))
							.equals("null")) {
						expeAnnuRevnue = "0.00";
					} else {
						expeAnnuRevnue = df.format(item
								.getDouble("expeAnnuRevnue"));
					}
					if (item.getString("shareSubject").equals("")) {
						shareSubject = "";
					} else {
						shareSubject = item.getString("shareSubject");
					}
					if (item.getString("productSubject").equals("")) {
						productSubject = "";
					} else {
						productSubject = item.getString("productSubject");
					}
					tempEntity7 = new ProductEntity();
					tempEntity7.setFundDirector(fundDirector);
					tempEntity7.setPeriod(period);
//					tempEntity7.setTopGain(topGain);
//					tempEntity7.setProdTypeName(prodTypeName);
//					tempEntity7.setProdDetail(prodDetail);
//					tempEntity7.setExpeAnnuRevnue(expeAnnuRevnue);
//					tempEntity7.setMinSubsAmount(minSubsAmount);
					tempEntity7.setName(name);
					tempEntity7.setProdTypeCode(prodTypeCode);
					tempEntity7.setID(productId);
					tempEntity7.setShareSubject(shareSubject);
					tempEntity7.setProductSubject(productSubject);
				} else if ("股权众筹类".equals(string)) {
					if (item.getString("prodTypeName").equals("")) {
						prodTypeName = null;
					} else {
						prodTypeName = item.getString("prodTypeName");
					}
					if (item.getString("fundDirector").equals("")) {
						fundDirector = null;
					} else {
						fundDirector = item.getString("fundDirector");
					}
					if (item.getString("investDirection").equals("")) {
						investDirection = null;
					} else {
						investDirection = item.getString("investDirection");
					}
					if (item.getString("prodTypeCode").equals("")) {
						prodTypeCode = null;
					} else {
						prodTypeCode = item.getString("prodTypeCode");
					}
					if (item.getString("productId").equals("")) {
						productId = null;
					} else {
						productId = item.getString("productId");
					}
					if (item.getString("name").equals("")) {
						name = null;
					} else {
						name = item.getString("name");
					}
					if (String.valueOf(item.getString("minSubsAmount")).equals(
							"null")) {
						minSubsAmount = "";
					} else {
						minSubsAmount = ChineseMoneyUtils
								.numWithDigitArray(item
										.getDouble("minSubsAmount"))[0]
								+ ChineseMoneyUtils.numWithDigitArray(item
										.getDouble("minSubsAmount"))[1];
					}
					if (String.valueOf(item.getString("expeAnnuRevnue"))
							.equals("null")) {
						expeAnnuRevnue = "0.00";
					} else {
						expeAnnuRevnue = df.format(item
								.getDouble("expeAnnuRevnue"));
					}
					if (item.getString("shareSubject").equals("")) {
						shareSubject = "";
					} else {
						shareSubject = item.getString("shareSubject");
					}
					if (item.getString("productSubject").equals("")) {
						productSubject = "";
					} else {
						productSubject = item.getString("productSubject");
					}
					tempEntity8 = new ProductEntity();
					tempEntity8.setFundDirector(fundDirector);
					tempEntity8.setInvestDirection(investDirection);
					tempEntity8.setProdTypeName(prodTypeName);
//					tempEntity8.setExpeAnnuRevnue(expeAnnuRevnue);
//					tempEntity8.setMinSubsAmount(minSubsAmount);
					tempEntity8.setName(name);
					tempEntity8.setProdTypeCode(prodTypeCode);
					tempEntity8.setID(productId);
					tempEntity8.setShareSubject(shareSubject);
					tempEntity8.setProductSubject(productSubject);
				} else {
				}
				String string2 = item.getString("prodTypeName");
				if ("银行类".equals(string2)) {
					listViewProductEntitiesList.add(tempEntity);
				} else if ("保险类".equals(string2)) {
					listViewProductEntitiesList.add(tempEntity2);
				} else if ("公募基金类".equals(string2)) {
					listViewProductEntitiesList.add(tempEntity3);
				} else if ("私募基金类".equals(string2)) {
					if (item.getString("fundType").equals("equity")) {
						listViewProductEntitiesList.add(tempEntity4);
					} else if (item.getString("fundType").equals("claim")) {
						listViewProductEntitiesList.add(tempEntity4);
					} else if (item.getString("fundType").equals("stock")) {
						listViewProductEntitiesList.add(tempEntity4);
					} else if (item.getString("fundType").equals("exchange")) {
						listViewProductEntitiesList.add(tempEntity4);
					} else if (item.getString("fundType").equals("derivative")) {
						listViewProductEntitiesList.add(tempEntity4);
					} else if (item.getString("fundType").equals("bond")) {
						listViewProductEntitiesList.add(tempEntity4);
					}
				} else if ("信托类".equals(string2)) {
					listViewProductEntitiesList.add(tempEntity5);
				} else if ("资管类".equals(string2)) {
					listViewProductEntitiesList.add(tempEntity6);
				} else if ("债权众筹类".equals(string2)) {
					listViewProductEntitiesList.add(tempEntity7);
				} else if ("股权众筹类".equals(string2)) {
					listViewProductEntitiesList.add(tempEntity8);
				} else {
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (jsonArray != null && jsonArray.length() > 0) { // 返回的datajson有内容时执行此步
			return listViewProductEntitiesList;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @author Owater
	 * @createtime 2015-1-14 上午9:23:21
	 * @Decription 判断是否曾经登录过
	 * 
	 * @return
	 */
	public boolean getUserInfo() {
		SharedPreferences userPreferences = getBaseContext()
				.getSharedPreferences("umeng_general_config",
						Context.MODE_PRIVATE);
		String string = userPreferences.getString("jdycode", "");
		if (string.length() > 10) {
			return true;
		}
		return false;
	}

	/**
	 * 总产品列表下拉刷新异步任务
	 */
	/*
	 * private class GetTotalDataTask extends AsyncTask<Void, Void, String[]> {
	 * 
	 * @Override protected String[] doInBackground(Void... params) { //
	 * Simulates a background job. try { Thread.sleep(1000);
	 * GetListJson(URLs.GETFOLLOWLIST, getListJsonList, pageNumber);
	 * judgeMineViewList = "totalData"; } catch (InterruptedException e) { }
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(String[] result) { // Do some
	 * stuff here // Call onRefreshComplete when the list has been refreshed.
	 * mPullRefreshScrollView.onRefreshComplete();
	 * listViewAdapter.notifyDataSetChanged(); mHandler.sendEmptyMessage(2);
	 * super.onPostExecute(result); } }
	 */
	/**
	 * 
	 * @author zhonghuixiong
	 * @createtime 2015-1-27 上午11:08:38
	 * @Decription 初始化搜索框数据
	 * 
	 */
	private void initData() {
		// TODO Auto-generated method stub
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

	/**
	 * 搜索分类按钮刷新异步任务
	 */
	/*
	 * private class GetSearchBtnDataTask extends AsyncTask<Void, Void,
	 * String[]> {
	 * 
	 * @Override protected String[] doInBackground(Void... params) { //
	 * Simulates a background job. try { Thread.sleep(1000);
	 * GetListJson(URLs.FOLLOWLIST_URL, getListJsonKeyWord, pageNumber);
	 * judgeMineViewList = "btnSearchData"; } catch (InterruptedException e) { }
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(String[] result) { // Do some
	 * stuff here // Call onRefreshComplete when the list has been refreshed.
	 * mPullRefreshScrollView.onRefreshComplete();
	 * listViewAdapter.notifyDataSetChanged(); mHandler.sendEmptyMessage(2);
	 * super.onPostExecute(result); } }
	 */
	/**
	 * 搜索分类列表刷新异步任务
	 */
	/*
	 * private class GetSearchListDataTask extends AsyncTask<Void, Void,
	 * String[]> {
	 * 
	 * @Override protected String[] doInBackground(Void... params) { //
	 * Simulates a background job. try { Thread.sleep(1000);
	 * GetListJson(URLs.FOLLOWLIST_URL, getListJsonProdType, pageNumber);
	 * judgeMineViewList = "typeListData"; } catch (InterruptedException e) { }
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(String[] result) { // Do some
	 * stuff here // Call onRefreshComplete when the list has been refreshed.
	 * mPullRefreshScrollView.onRefreshComplete();
	 * listViewAdapter.notifyDataSetChanged(); mHandler.sendEmptyMessage(2);
	 * super.onPostExecute(result); } }
	 */

	/**
	 * 监听返回键
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			Intent intent = new Intent(FollowActivity.this, MainActivity.class);
			startActivity(intent);
			// overridePendingTransition(android.R.anim.slide_in_left,
			// android.R.anim.slide_out_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
