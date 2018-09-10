package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.RpRecordInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/8/24.
 */

public class RPCoinsRVAdatper extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<RpRecordInfo.DataBean.TopBean.CoinNumberBean> mDataList;

    public RPCoinsRVAdatper(Context context, List<RpRecordInfo.DataBean.TopBean.CoinNumberBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rp_coins, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_count)
        TextView mTvCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(RpRecordInfo.DataBean.TopBean.CoinNumberBean coinNumberBean) {
            mTvCoin.setText(coinNumberBean.getName());
            mTvCount.setText(coinNumberBean.getNumber());
        }
    }
}
