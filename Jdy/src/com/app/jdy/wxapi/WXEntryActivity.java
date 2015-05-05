package com.app.jdy.wxapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.jdy.R;
import com.app.jdy.context.MyApplication;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.Constants;
import com.app.jdy.utils.HttpUtils;
import com.app.jdy.utils.URLs;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	private MyApplication data;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_wxentry);
		data = (MyApplication) getApplication();
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		data.setSendResp(resp);
		data.setWxFlag(true);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:

			switch (resp.getType()) {
			// 授权登陆
			case ConstantsAPI.COMMAND_SENDAUTH:
				break;
			// 分享
			case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
				result = R.string.errcode_success;
				new Thread(new Runnable() {
					@Override
					public void run() {
						ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("productId", Constants.TMP_PRODUCTID));
						params.add(new BasicNameValuePair("memberId", Constants.TMP_MEMBERID));
						HttpUtils.request(params, URLs.SHARE_COUNT_URL);
					}
				}).start();
				
				break;
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		if (result != 0) {
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		}
		finish();
	}

	@Override
	public void onReq(BaseReq req) {
		Log.i("req", "req.transaction================" + req.transaction);
		// finish();
	}
}
