package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.activity.ChatTransferActivity;
import com.bclould.tea.ui.activity.CoinExchangeActivity;
import com.bclould.tea.ui.activity.FinancialActivity;
import com.bclould.tea.ui.activity.OtcActivity;
import com.bclould.tea.ui.activity.PaymentActivity;
import com.bclould.tea.ui.activity.PushBuyingActivity;
import com.bclould.tea.ui.activity.RewardActivity;
import com.bclould.tea.ui.activity.SendQRCodeRedActivity;
import com.bclould.tea.ui.activity.SendRedGroupActivity;
import com.bclould.tea.ui.activity.SendRedPacketActivity;
import com.bclould.tea.ui.activity.StartGuessActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by GA on 2018/3/16.
 */

public class BottomDialogRVAdapter4 extends RecyclerView.Adapter {

    private final List<CoinListInfo.DataBean> mCoinList;
    private final Context mContext;

    public BottomDialogRVAdapter4(Context context, List<CoinListInfo.DataBean> coinList) {
        mContext = context;
        mCoinList = coinList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_bottom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mCoinList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mCoinList.size() == 0)
            return 0;
        return mCoinList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_coin_logo)
        ImageView mTvCoinLogo;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_remaining)
        TextView mTvRemaining;
        private String mName;
        private int mId;
        private String mCoin_over;
        private String mLogo;
        private String mServiceCharge;
        private CoinListInfo.DataBean mData;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mContext instanceof CoinExchangeActivity) {
                        CoinExchangeActivity activity = (CoinExchangeActivity) mContext;
                        activity.hideDialog(mName, mId, mLogo, mCoin_over, mServiceCharge);
                    } else if (mContext instanceof OtcActivity) {
                        OtcActivity activity = (OtcActivity) mContext;
                        activity.hideDialog(mData);
                    } else if (mContext instanceof PushBuyingActivity) {
                        PushBuyingActivity activity = (PushBuyingActivity) mContext;
                        activity.hideDialog2(mName, mId, mServiceCharge,mLogo);
                    } else if (mContext instanceof StartGuessActivity) {
                        StartGuessActivity activity = (StartGuessActivity) mContext;
                        activity.hideDialog2(mData);
                    } else if (mContext instanceof SendRedPacketActivity) {
                        SendRedPacketActivity activity = (SendRedPacketActivity) mContext;
                        activity.hideDialog(mName,mLogo);
                    } else if (mContext instanceof PaymentActivity) {
                        PaymentActivity activity = (PaymentActivity) mContext;
                        activity.hideDialog(mName, mId,mLogo);
                    } else if (mContext instanceof ChatTransferActivity) {
                        ChatTransferActivity activity = (ChatTransferActivity) mContext;
                        activity.hideDialog(mName, mId,mLogo);
                    } else if (mContext instanceof SendQRCodeRedActivity) {
                        SendQRCodeRedActivity activity = (SendQRCodeRedActivity) mContext;
                        activity.hideDialog(mName, mId,mLogo);
                    } else if (mContext instanceof RewardActivity) {
                        RewardActivity activity = (RewardActivity) mContext;
                        activity.hideDialog(mName, mId,mLogo);
                    }else if(mContext instanceof SendRedGroupActivity){
                        SendRedGroupActivity activity = (SendRedGroupActivity) mContext;
                        activity.hideDialog(mName,mLogo);
                    }else if(mContext instanceof FinancialActivity){
                        FinancialActivity activity = (FinancialActivity) mContext;
                        activity.hideDialog(mData);
                    }
                }
            });
        }

        public void setData(CoinListInfo.DataBean data) {
            mData = data;
            mName = data.getName();
            mCoin_over = data.getCoin_over();
            mLogo = data.getLogo();
            mId = data.getId();
            mServiceCharge = data.getOut_exchange();
            mTvName.setText(mName);
            mTvRemaining.setText(data.getCoin_over());
            Glide.with(mContext).load(data.getLogo()).into(mTvCoinLogo);
        }
    }
}
