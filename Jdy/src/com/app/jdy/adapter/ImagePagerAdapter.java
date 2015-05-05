/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.app.jdy.adapter;

import java.net.URL;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.jdy.activity.PictureActivity;
import com.app.jdy.ui.GuideActivity;
import com.app.jdy.utils.URLs;
import com.app.jdy.widget.RecyclingPagerAdapter;

/**
 * 
 * description :
 * 
 * @version 1.0
 * @author zhonghuixiong
 * @createtime : 2015-1-30 下午9:48:35
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhonghuixiong 2015-1-30
 *             下午9:48:35
 * 
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private List<Bitmap> imageIdList;
	private int size;
	private boolean isInfiniteLoop;
	private int ImagePosition;

	public ImagePagerAdapter(Context context, List<Bitmap> bitmapList) {
		this.context = context;
		this.imageIdList = bitmapList;
		this.size = bitmapList.size();
		isInfiniteLoop = false;

	}

	@Override
	public int getCount() {
		// Infinite loop
		return isInfiniteLoop ? Integer.MAX_VALUE : imageIdList.size();
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		//return isInfiniteLoop ? position % size : position;
		//size为0的时候%运算会出错，所以需要进行判断
		return isInfiniteLoop && size > 0 ? position % size : position;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = holder.imageView = new ImageView(context);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		ImagePosition = position;
		holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		holder.imageView.setImageBitmap(imageIdList.get(getPosition(position)));
		holder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("debug==========position",
						String.valueOf(getPosition(ImagePosition)));
				if (getPosition(ImagePosition) == 1) {
					Intent intent = new Intent(context, PictureActivity.class);
					Bundle bundle = new Bundle();
					intent.putExtra("title", "抢红包");
					intent.putExtra("url", URLs.HONGBAO_INTRODUCE);
					intent.putExtras(bundle);
					context.startActivity(intent);
				} else if (getPosition(ImagePosition) == 0) {
					Intent intent = new Intent(context, PictureActivity.class);
					intent.putExtra("title", "珍宝岛");
					Bundle bundle = new Bundle();
					intent.putExtra("url", URLs.SHANGXIAN_INTRODUCE);
					context.startActivity(intent);
				}
			}
		});
		return view;
	}

	private static class ViewHolder {

		ImageView imageView;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop
	 *            the isInfiniteLoop to set
	 */
	public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}
}
