package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.DealListInfo;
import com.bclould.tocotalk.ui.activity.BuySellActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/20.
 */

public class BuySellRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final boolean mType;
    private final List<DealListInfo.DataBean> mDataBeanList;

    public BuySellRVAdapter(Context context, boolean type, List<DealListInfo.DataBean> dataBeanList) {
        mContext = context;
        mDataBeanList = dataBeanList;
        mType = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_buy_sell, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataBeanList.size() != 0)
            return mDataBeanList.size();
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_username)
        TextView mTvUsername;
        @Bind(R.id.tv_pay_way)
        TextView mTvPayWay;
        @Bind(R.id.tv_price)
        TextView mTvPrice;
        @Bind(R.id.tv_trade_count)
        TextView mTvTradeCount;
        @Bind(R.id.tv_coin_count)
        TextView mTvCoinCount;
        @Bind(R.id.ll_credit)
        LinearLayout mLlCredit;
        @Bind(R.id.tv_limit)
        TextView mTvLimit;
        @Bind(R.id.btn_sell_buy)
        Button mBtnSellBuy;
        private DealListInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            if (mType) {
                mBtnSellBuy.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape2));
                mBtnSellBuy.setTextColor(mContext.getColor(R.color.sell));
                mBtnSellBuy.setText(mContext.getString(R.string.sell));
                mTvPayWay.setBackground(mContext.getDrawable(R.drawable.bg_payway_shape2));
            }
            mBtnSellBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BuySellActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("type", mType);
                    bundle.putSerializable("data", mDataBean);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(DealListInfo.DataBean dataBean) {
            mDataBean = dataBean;
            mTvPayWay.setText(dataBean.getPay_type());
            mTvPrice.setText(dataBean.getPrice());
            mTvUsername.setText(dataBean.getUsername());
            mTvTradeCount.setText("交易 " + dataBean.getCount_trans_number());
            mTvCoinCount.setText("数量 " + dataBean.getNumber() + dataBean.getCoin_name());
            mTvLimit.setText("限额 " + dataBean.getMin_amount() + "-" + dataBean.getMax_amount() + " " + dataBean.getCurrency());
        }
    }
}
