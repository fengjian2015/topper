package com.bclould.tea.ui.adapter;

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
import com.bclould.tea.Presenter.BuySellPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.DealListInfo;
import com.bclould.tea.ui.activity.BuySellActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.UtilTool;
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
    private final BuySellPresenter mBuySellPresenter;

    public BuySellRVAdapter(Context context, boolean type, List<DealListInfo.DataBean> dataBeanList, BuySellPresenter buySellPresenter) {
        mContext = context;
        mDataBeanList = dataBeanList;
        mBuySellPresenter = buySellPresenter;
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final DealListInfo.DataBean dataBean) {
            if (mType) {
                if (dataBean.getSelf_trans() == 1) {
                    mTvPayWay.setBackground(mContext.getResources().getDrawable(R.drawable.bg_payway_shape2));
                    mBtnSellBuy.setBackground(mContext.getResources().getDrawable(R.drawable.bg_buysell_shape3));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.red));
                    mBtnSellBuy.setText(mContext.getString(R.string.sold_out));
                    mBtnSellBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog(dataBean);
                        }
                    });
                } else {
                    mBtnSellBuy.setBackground(mContext.getResources().getDrawable(R.drawable.bg_buysell_shape2));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.sell));
                    mBtnSellBuy.setText(mContext.getString(R.string.work_off));
                    mTvPayWay.setBackground(mContext.getResources().getDrawable(R.drawable.bg_payway_shape2));
                    mBtnSellBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, BuySellActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("type", mType);
                            bundle.putSerializable("data", dataBean);
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else {
                if (dataBean.getSelf_trans() == 1) {
                    mBtnSellBuy.setBackground(mContext.getResources().getDrawable(R.drawable.bg_buysell_shape3));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.red));
                    mBtnSellBuy.setText(mContext.getString(R.string.sold_out));
                    mBtnSellBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog(dataBean);
                        }
                    });
                } else {
                    mBtnSellBuy.setBackground(mContext.getResources().getDrawable(R.drawable.bg_buysell_shape));
                    mBtnSellBuy.setTextColor(mContext.getResources().getColor(R.color.blue2));
                    mBtnSellBuy.setText(mContext.getString(R.string.buy));
                    mTvPayWay.setBackground(mContext.getResources().getDrawable(R.drawable.bg_payway_shape));
                    mBtnSellBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, BuySellActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("type", mType);
                            bundle.putSerializable("data", dataBean);
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                        }
                    });
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

    private void showDialog(final DealListInfo.DataBean dataBean) {
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
                        mDataBeanList.remove(dataBean);
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
