package com.bclould.tea.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bclould.tea.R;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;

import java.io.File;

/**
 * Created by GIjia on 2018/5/22.
 */

public class OpenMapFromBottomPopup extends PopupWindow implements View.OnClickListener{
    private View view;
    private Context context;
    private double mLat;
    private double mLng;
    private float oLat;
    private float oLng;

    public OpenMapFromBottomPopup(Activity context, double mLat, double mLng, float oLat, float oLng) {
        super(context);
        this.context = context;
        this.mLat=mLat;
        this.mLng=mLng;
        this.oLng=oLng;
        this.oLat=oLat;
    }


    public void executeShowPopWindow(View parent) {
        view = View.inflate(context, R.layout.popup_open_map_from_bottom, null);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setContentView(view);
        if(!ActivityUtil.isActivityOnTop(context))return;
        showAtLocation(parent, Gravity.RIGHT | Gravity.BOTTOM, 10, 10);;
        update();
        bindEvent();
    }
    private void bindEvent() {
        if (view != null) {
            view.findViewById(R.id.tv_baidu).setOnClickListener(this);
//            view.findViewById(R.id.tv_tencent).setOnClickListener(this);
            view.findViewById(R.id.tv_gaode).setOnClickListener(this);
            view.findViewById(R.id.head_rl).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_baidu:
                openBaidu();
                break;
            case R.id.tv_gaode:
                openGaode();
                break;
//            case R.id.tv_tencent:
//
//                break;
            case R.id.head_rl:
                this.dismiss();
                break;
        }
    }

    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    private void openGaode(){
        if(isInstallByread("com.autonavi.minimap")){
            Intent intent = new Intent("android.intent.action.VIEW",
                    android.net.Uri.parse("androidamap://route?sourceApplication=Tocotalk"+ "&dlat="+
                            oLat+ "&dlon="+ oLng+ "&dev=0" + "&t=1"));
            intent.addCategory("android.intent.category.DEFAULT");
            context.startActivity(intent);
        }else{
            ToastShow.showToast2((Activity) context,context.getString(R.string.no_gode_map));
        }
    }

    private void openBaidu(){
        if(isInstallByread("com.baidu.BaiduMap")){
            double[] doubles=gaoDeToBaidu(oLng,oLat);
            Intent intent = new Intent();
            intent.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+mLng +"," +mLat +"&destination=" +doubles[1]+","+doubles[0]+"&mode=transit&sy=0&index=0&target=1"));
            intent.setPackage("com.baidu.BaiduMap");
            context.startActivity(intent); // 启动调用
        }else {
            ToastShow.showToast2((Activity) context,context.getString(R.string.no_baidu_map));
        }
    }


    /**
     * 高德地图定位经纬度转百度经纬度
     * @param gd_lon
     * @param gd_lat
     * @return
     */
    public static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }
}
