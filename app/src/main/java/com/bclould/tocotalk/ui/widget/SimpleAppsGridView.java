package com.bclould.tocotalk.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.AppInfo;
import com.bclould.tocotalk.ui.adapter.AppsAdapter;

import java.util.ArrayList;

/**
 * Created by GA on 2017/12/27.
 */

public class SimpleAppsGridView extends RelativeLayout {

    protected View view;
    private String mUser;
    private Context mContext;

    public SimpleAppsGridView(Context context) {
        this(context, null);
        mContext = context;

    }

    public SimpleAppsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_apps, this);
    }

    protected void init() {
        GridView gv_apps = (GridView) view.findViewById(R.id.gv_apps);
        ArrayList<AppInfo> mAppBeanList = new ArrayList<>();
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_photo, mContext.getString(R.string.image)));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_camera, mContext.getString(R.string.paizhao)));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_video, mContext.getString(R.string.video)));
//        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_file, "文件"));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_transfer, mContext.getString(R.string.transfer)));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_red_envelope, mContext.getString(R.string.red_package)));
//        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_transfer,mContext.getString(R.string.location)));
//        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_position, mContext.getString(R.string.location)));
        AppsAdapter adapter = new AppsAdapter(getContext(), mAppBeanList, mUser);
        gv_apps.setAdapter(adapter);
    }

    public void setData(String user) {
        mUser = user;
        init();
    }
}
