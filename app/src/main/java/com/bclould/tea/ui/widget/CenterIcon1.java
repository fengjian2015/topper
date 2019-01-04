package com.bclould.tea.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.bclould.tea.R;
import com.bclould.tea.utils.UtilTool;

public class CenterIcon1 extends View {

	public static int w;
	public static int h;
	public static Bitmap mBitmap;
	public static View mMapView;
	private Context mContext;

	public CenterIcon1(Context context, View mMapView) {
		super(context);
		mContext=context;
		// 设置屏幕中心的图标
		mBitmap = BitmapFactory.decodeResource(getResources(),
				R.mipmap.curr_position);
		CenterIcon1.mMapView = mMapView;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		// 获取屏幕中心的坐标
		w = mMapView.getWidth() / 2 - mBitmap.getWidth() / 2;
		h =mMapView.getHeight() / 2+ UtilTool.dip2px(mContext,50);
		canvas.drawBitmap(mBitmap, w, h, null);
	}

}