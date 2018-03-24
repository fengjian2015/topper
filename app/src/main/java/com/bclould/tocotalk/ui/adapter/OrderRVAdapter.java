package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.OrderListInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OrderRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final boolean mType;
    private List<OrderListInfo.DataBean> mDataBeanList;

    public OrderRVAdapter(Context context, boolean type, List<OrderListInfo.DataBean> dataBeanList) {
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
        private OrderListInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

        public void setData(OrderListInfo.DataBean dataBean) {

        }
    }
}
