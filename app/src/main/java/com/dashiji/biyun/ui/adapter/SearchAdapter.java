package com.dashiji.biyun.ui.adapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.model.UserInfo;
import com.dashiji.biyun.ui.activity.SearchActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/27.
 */

public class SearchAdapter extends BaseAdapter {

    private final SearchActivity mSearchActivity;
    private final List<UserInfo> mUserInfos;

    public SearchAdapter(SearchActivity searchActivity, List<UserInfo> userInfos) {
        mSearchActivity = searchActivity;
        mUserInfos = userInfos;
    }

    @Override
    public int getCount() {
        return mUserInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mUserInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mSearchActivity).inflate(R.layout.item_search2, viewGroup, false);
            viewHolder = new ViewHolder(convertView, i);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;

        ViewHolder(View view, int i) {
            ButterKnife.bind(this, view);
            mIvTouxiang.setImageBitmap(BitmapFactory.decodeFile(mUserInfos.get(i).getPath()));
            mTvName.setText(mUserInfos.get(i).getUser());
        }
    }
}
