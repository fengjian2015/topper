package com.dashiji.biyun.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.model.AppInfo;
import com.dashiji.biyun.ui.activity.SendRedPacketActivity;
import com.dashiji.biyun.utils.MessageEvent;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2017/12/27.
 */

public class AppsAdapter extends BaseAdapter {

    private final String mUser;
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<AppInfo> mDdata = new ArrayList<AppInfo>();
    private List<LocalMedia> selectList = new ArrayList<>();

    public AppsAdapter(Context context, ArrayList<AppInfo> data, String user) {
        this.mContext = context;
        mUser = user;
        this.inflater = LayoutInflater.from(context);
        if (data != null) {
            this.mDdata = data;
        }
    }

    @Override
    public int getCount() {
        return mDdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mDdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_app, null);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AppInfo appBean = mDdata.get(position);
        if (appBean != null) {
            viewHolder.iv_icon.setBackgroundResource(appBean.getIcon());
            viewHolder.tv_name.setText(appBean.getFuncName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence name = viewHolder.tv_name.getText();
                    if (name.equals("红包")) {
                        Intent intent = new Intent(mContext, SendRedPacketActivity.class);
                        intent.putExtra("user", mUser);
                        mContext.startActivity(intent);
                    } else if (name.equals("图片")) {
                        EventBus.getDefault().post(new MessageEvent("打开相册"));
                    } else if (name.equals("拍照")) {
                        EventBus.getDefault().post(new MessageEvent("打开相机"));
                    } else if (name.equals("文件")) {
                        EventBus.getDefault().post(new MessageEvent("打开文件管理"));
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;
    }
}
