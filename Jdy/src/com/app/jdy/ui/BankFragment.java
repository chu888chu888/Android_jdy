package com.app.jdy.ui;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jdy.R;
import com.app.jdy.utils.ChineseMoneyUtils;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.Constants;
import com.app.jdy.utils.DateUtils;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.JSONUtils;
import com.app.jdy.utils.URLs;
import com.app.jdy.widget.WaitingDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * description :
 * 
 * @version 1.0
 * @author zhonghuixiong
 * @createtime : 2015-1-25 上午9:49:38
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhonghuixiong 2015-1-25
 *             上午9:49:38
 * 
 */
@SuppressLint("NewApi")
public class BankFragment extends Fragment implements OnClickListener {

	private View view;
	private TextView title;
	private String id;// 产品ID号
	private String code;// 产品类型
	private String name;// 产品名称
	private String imageString;// 图标对应的文字

	private String dataJson;
	private JSONObject jsonObject;
	private JSONObject product;
	private WebView webView;
	// private TextView detail_currencyTypeName;
	private TextView detail_statusName;
	private TextView detail_profitTypeName;
	private TextView detail_period;
	private TextView detail_minSubsAmount;
	private TextView detail_profit_time;
	private TextView detail_sale_time;
	private TextView detail_expeAnnuRevnue;
	private Button change_money_click;
	private EditText invest_money;
	private TextView invest_result;
	private TextView invest_result_unit;
	private ImageView issuerLogo;
	private TextView image_text;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(getActivity(), Constants.NO_INTENT_TIPS, Toast.LENGTH_LONG).show();
				WaitingDialog.dismissDialog();
				break;
			case 1:
				setDetailData();
				WaitingDialog.dismissDialog();
				break;
			default:
				break;
			}
		}
	};
	// private TextView invest_days;
	private TextView detail_project_subject;

	/**
	 * 
	 * 描述:构造函数
	 * 
	 * @param id
	 *            产品ID号
	 * @param code
	 *            产品类型
	 */
	public BankFragment(String id, String code, String imageString) {
		this.id = id;
		this.code = code;
		this.imageString = imageString;
	}
	
	// java.lang.InstantiationException:can't instantiate class com.app.jdy.ui.BankFragment; no empty constructor
	// 所以需要一个空的构造方法
	public BankFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_bank_detail, null);
		/**
		 * 加载进度条
		 */
		WaitingDialog.showDialog(getActivity());
		initView();// 初始化组件
		getDetailData();// 获取服务端的数据
		getWebviewData();// 获取webview的数据
		change_money_click.setOnClickListener(this);

		return view;
	}

	public void initView() {
		image_text = (TextView) view.findViewById(R.id.imageString);
		image_text.setText(imageString);
		title = (TextView) view.findViewById(R.id.detail_title);
		// detail_currencyTypeName = (TextView)
		// view.findViewById(R.id.detail_currencyTypeName);
		detail_statusName = (TextView) view.findViewById(R.id.detail_statusName);
		detail_profitTypeName = (TextView) view.findViewById(R.id.detail_profitTypeName);
		detail_period = (TextView) view.findViewById(R.id.detail_period);
		detail_minSubsAmount = (TextView) view.findViewById(R.id.detail_minSubsAmount);
		detail_profit_time = (TextView) view.findViewById(R.id.detail_profit_time);
		detail_sale_time = (TextView) view.findViewById(R.id.detail_sale_time);
		detail_expeAnnuRevnue = (TextView) view.findViewById(R.id.detail_expeAnnuRevnue);
		change_money_click = (Button) view.findViewById(R.id.change_money_click);
		invest_money = (EditText) view.findViewById(R.id.invest_money);
		invest_money = (EditText) view.findViewById(R.id.invest_money);
		// invest_days = (TextView) view.findViewById(R.id.invest_days);
		invest_result = (TextView) view.findViewById(R.id.invest_result);
		invest_result_unit = (TextView) view.findViewById(R.id.invest_result_unit);
		issuerLogo = (ImageView) view.findViewById(R.id.issuerLogo);
		webView = (WebView) view.findViewById(R.id.detail_webview);
		detail_project_subject = (TextView) view.findViewById(R.id.detail_product_subject);
		/**
		 * 设置虚线
		 */
		if (android.os.Build.VERSION.SDK_INT >= 11 ) {
			view.findViewById(R.id.stroke).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			view.findViewById(R.id.stroke1).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			view.findViewById(R.id.stroke2).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	/**
	 * 
	 * @author zhoufeng
	 * @createtime 2015-3-14 下午3:17:56
	 * @Decription 获取详情列表的数据
	 * 
	 */
	public void getDetailData() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				if (HttpUtils.isNetworkConnected(getActivity()) == true) {
					dataJson = HttpUtils.request(null, URLs.PROUDCT_DETAIL + id + "-" + code);
					if (dataJson.length() != 0 && !dataJson.equals("0x110")) {
						try {
							jsonObject = new JSONObject(dataJson);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						msg.what = 1;
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

	/**
	 * 
	 * @author zhoufeng
	 * @createtime 2015-3-14 下午3:17:42
	 * @Decription 获取详情webview数据
	 * 
	 */
	public void getWebviewData() {
		WebSettings settings = webView.getSettings();
		settings.setSupportZoom(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webView.loadUrl(URLs.PROUDCT_DETAIL_WEBVIEW + id);
		if (android.os.Build.VERSION.SDK_INT >= 11 ) {
			webView.removeJavascriptInterface("searchBoxJavaBredge_");
		}
		webView.setWebViewClient(new WebViewClient() {

			// return true才能让Webview里面的链接不去调用浏览器
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});
		webView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_img:
			getActivity().finish();
			break;
		case R.id.change_money_click:
			try {
				BigDecimal b1 = new BigDecimal(invest_money.getText().toString());
				BigDecimal b2 = new BigDecimal(product.getDouble("expeAnnuRevnue"));
				BigDecimal p_invest_days = new BigDecimal(product.getInt("period"));
				BigDecimal result = b1.multiply(b2).multiply(p_invest_days)
						.divide(new BigDecimal(365 * 100), 2, BigDecimal.ROUND_DOWN);
				invest_result.setText(ChineseMoneyUtils.numWithDigitArray(result.doubleValue())[0]);
				invest_result_unit.setText(ChineseMoneyUtils.numWithDigitArray(result.doubleValue())[1]);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @author zhoufeng
	 * @throws JSONException
	 * @createtime 2015-3-13 下午11:04:09
	 * @Decription 填充数据
	 * 
	 */
	public void setDetailData() {

		try {
			if (jsonObject == null)return;
			if (jsonObject.getString("product1") == null)return;
			product = new JSONObject(jsonObject.getString("product1"));
			name = CommonUtils.changeNullToString(
					CommonUtils.ToDBC(CommonUtils.StringFilter(product.getString("name"))), "未命名");
			detail_project_subject.setText(JSONUtils.getString(product, "productSubject", ""));
			title.setText(name);
			// detail_currencyTypeName
			// .setText(CommonUtils.changeNullToString(product.getString("currencyTypeName"),
			// "--"));
			detail_statusName.setText(CommonUtils.changeNullToString(product.getString("statusName"), "--"));
			detail_profitTypeName.setText(CommonUtils.changeNullToString(product.getString("profitTypeName"), "--"));
			// invest_days.setText(JSONUtils.getString(product, "period", "0"));
			if (JSONUtils.isEmpty(product, "period")) {
				detail_period.setText("--");
			} else {
				detail_period.setText(DateUtils.parseDayToStr(product.getInt("period")));
			}

			if (JSONUtils.isEmpty(product, "minSubsAmount")) {
				detail_minSubsAmount.setText(Constants.NO_CONTENT_N);
			} else {
				detail_minSubsAmount.setText(ChineseMoneyUtils.numWithDigit(product.getDouble("minSubsAmount")));

			}
			final String[] urls = { URLs.HTTP + URLs.HOST + product.getString("issuerLogo") };
			if (JSONUtils.isEmpty(product, "issuerLogo")) {
				issuerLogo.setVisibility(View.INVISIBLE);
			} else {
				issuerLogo.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(urls[0], issuerLogo);
				issuerLogo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						imageBrower(0, urls);
					}
				});
			}

			String profitStart, profitEnd, saleStart, saleEnd;
			profitStart = JSONUtils.getString(product, "profitStart", Constants.NO_CONTENT_N);
			profitEnd = JSONUtils.getString(product, "profitEnd", Constants.NO_CONTENT_N);
			saleStart = JSONUtils.getString(product, "saleStart", Constants.NO_CONTENT_N);
			saleEnd = JSONUtils.getString(product, "saleEnd", Constants.NO_CONTENT_N);
			profitStart = JSONUtils.getString(product, "profitStart", Constants.NO_CONTENT_N);
			detail_profit_time.setText(profitStart + " ~ " + profitEnd);
			detail_sale_time.setText(saleStart + " ~ " + saleEnd);
			detail_expeAnnuRevnue.setText(JSONUtils.getString(product, "expeAnnuRevnue", "0.00") + "%");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		getActivity().startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); 
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen"); 
	}
}
