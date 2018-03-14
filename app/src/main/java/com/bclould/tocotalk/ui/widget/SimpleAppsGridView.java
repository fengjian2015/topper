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

    public SimpleAppsGridView(Context context) {
        this(context, null);

    }

    public SimpleAppsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_apps, this);
    }

    protected void init() {
        GridView gv_apps = (GridView) view.findViewById(R.id.gv_apps);
        ArrayList<AppInfo> mAppBeanList = new ArrayList<>();
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_photo, "图片"));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_camera, "拍照"));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_video, "视频"));
//        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_file, "文件"));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_transfer, "转账"));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_red_envelope, "红包"));
        mAppBeanList.add(new AppInfo(R.mipmap.icon_tail_position, "位置"));
        AppsAdapter adapter = new AppsAdapter(getContext(), mAppBeanList, mUser);
        gv_apps.setAdapter(adapter);
    }

    public void setData(String user) {
        mUser = user;
        init();
    }
}
