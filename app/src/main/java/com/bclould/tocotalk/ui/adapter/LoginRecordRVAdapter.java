package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.LoginRecordInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/4/2.
 */

public class LoginRecordRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<LoginRecordInfo.DataBean> mDataList;

    public LoginRecordRVAdapter(Context context, List<LoginRecordInfo.DataBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_login_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_ip)
        TextView mTvIp;
        @Bind(R.id.tv_time)
        TextView mTvTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(LoginRecordInfo.DataBean dataBean) {
            mTvIp.setText(dataBean.getIp());
            mTvTime.setText(dataBean.getCreated_at());
        }
    }
}
