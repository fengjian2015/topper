package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.CoinListInfo;
import com.bclould.tocotalk.ui.activity.CoinExchangeActivity;
import com.bclould.tocotalk.ui.activity.OtcActivity;
import com.bclould.tocotalk.ui.activity.PushBuyingActivity;
import com.bclould.tocotalk.ui.activity.StartGuessActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by GA on 2018/3/16.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
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
                        activity.hideDialog(mName, mId,mData.getOut_otc());
                    } else if (mContext instanceof PushBuyingActivity) {
                        PushBuyingActivity activity = (PushBuyingActivity) mContext;
                        activity.hideDialog2(mName, mId, mServiceCharge);
                    } else if (mContext instanceof StartGuessActivity) {
                        StartGuessActivity activity = (StartGuessActivity) mContext;
                        activity.hideDialog2(mData);
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
            Glide.with(mContext).load(data.getLogo()).into(mTvCoinLogo);
        }
    }
}
