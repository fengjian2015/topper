package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.BuySellPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.MyAdListInfo;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/4/27.
 */

public class MyPushAdRVAdapter extends RecyclerView.Adapter {


    private final Context mContext;
    private final List<MyAdListInfo.DataBean> mDataList;
    private final BuySellPresenter mBuySellPresenter;
    private final int mType;

    public MyPushAdRVAdapter(Context context, List<MyAdListInfo.DataBean> dataList, BuySellPresenter buySellPresenter, int type) {
        mContext = context;
        mDataList = dataList;
        mBuySellPresenter = buySellPresenter;
        mType = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_ad, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0)
            return mDataList.size();
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MyAdListInfo.DataBean dataBean) {
            if (mType == 1) {
                mTvPayWay.setBackground(mContext.getDrawable(R.drawable.bg_payway_shape));
                if (dataBean.getStatus() == 1) {
                    mBtnSellBuy.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape3));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.red_color));
                    mBtnSellBuy.setText(mContext.getString(R.string.sold_out));
                    mBtnSellBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog(dataBean);
                        }
                    });
                } else {
                    mBtnSellBuy.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape4));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                    mBtnSellBuy.setText(mContext.getString(R.string.yi_sold_out));
                }
            } else {
                mTvPayWay.setBackground(mContext.getDrawable(R.drawable.bg_payway_shape2));
                if (dataBean.getStatus() == 1) {
                    mBtnSellBuy.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape3));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.red_color));
                    mBtnSellBuy.setText(mContext.getString(R.string.sold_out));
                    mBtnSellBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog(dataBean);
                        }
                    });
                } else {
                    mBtnSellBuy.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape4));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                    mBtnSellBuy.setText(mContext.getString(R.string.yi_sold_out));
                }
            }
            if (dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mIvTouxiang);
            }
            mTvPayWay.setText(dataBean.getPay_type());
            mTvPrice.setText(dataBean.getPrice() + " " + dataBean.getCurrency());
            mTvUsername.setText(dataBean.getUsername());
            mTvTradeCount.setText(mContext.getString(R.string.deal) + " " + dataBean.getCount_trans_number());
            mTvCoinCount.setText(mContext.getString(R.string.count) + " " + dataBean.getNumber() + dataBean.getCoin_name());
            mTvLimit.setText(mContext.getString(R.string.limit) + " " + dataBean.getMin_amount() + "-" + dataBean.getMax_amount() + " " + dataBean.getCurrency());
        }
    }

    private void showDialog(final MyAdListInfo.DataBean dataBean) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.sold_out_hint));
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBuySellPresenter.cancelAd(dataBean.getId(), new BuySellPresenter.CallBack4() {
                    @Override
                    public void send() {
                        dataBean.setStatus(0);
                        if (mType == 1) {
                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.sold_out_buy)));
                        } else {
                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.sold_out_sell)));
                        }
                        notifyDataSetChanged();
                        deleteCacheDialog.dismiss();
                    }
                });
            }
        });
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
    }
}
