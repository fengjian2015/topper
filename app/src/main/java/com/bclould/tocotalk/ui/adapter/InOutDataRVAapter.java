package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.InOutInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/4.
 */

public class InOutDataRVAapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<InOutInfo.DataBean> mData;

    public InOutDataRVAapter(Context context, List<InOutInfo.DataBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_in_out_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData.size() != 0) {
            return mData.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.type_name)
        TextView mTypeName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.tv_reality_transfer)
        TextView mTvRealityTransfer;
        @Bind(R.id.tv_reailty_income)
        TextView mTvReailtyIncome;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(InOutInfo.DataBean dataBean) {
            mTypeName.setText(dataBean.getUser_name());
            mTime.setText(dataBean.getCreated_at());
            mTvRealityTransfer.setText(dataBean.getNumber());
            mTvReailtyIncome.setText(dataBean.getNumber_u());
        }
    }
}
