package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.ExchangeOrderInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/4/4.
 */

public class CoinExchangeRVAdapter extends RecyclerView.Adapter {

    private final List<ExchangeOrderInfo.DataBeanX.DataBean> mDataList;
    private final Context mContext;

    public CoinExchangeRVAdapter(Context context, List<ExchangeOrderInfo.DataBeanX.DataBean> dataList) {
        mDataList = dataList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_coin_exchange, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() == 0)
            return 0;
        return mDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_price)
        TextView mTvPrice;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_time)
        TextView mTvTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(ExchangeOrderInfo.DataBeanX.DataBean dataBean) {
            mTvType.setText("卖出");
            mTvCount.setText(dataBean.getNumber());
            mTvPrice.setText(dataBean.getPrice());
            mTvTime.setText(dataBean.getCreated_at());
        }
    }
}
