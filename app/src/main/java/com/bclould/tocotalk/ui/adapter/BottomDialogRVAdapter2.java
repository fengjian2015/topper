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
import com.bclould.tocotalk.model.CoinInfo;
import com.bclould.tocotalk.ui.activity.ChatTransferActivity;
import com.bclould.tocotalk.ui.activity.OtcActivity;
import com.bclould.tocotalk.ui.activity.PaymentActivity;
import com.bclould.tocotalk.ui.activity.PushBuyingActivity;
import com.bclould.tocotalk.ui.activity.SendQRCodeRedActivity;
import com.bclould.tocotalk.ui.activity.SendRedPacketActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/28.
 */

public class BottomDialogRVAdapter2 extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<CoinInfo.DataBean> mCoinArr;


    public BottomDialogRVAdapter2(Context context, List<CoinInfo.DataBean> coinArr) {
        mContext = context;
        mCoinArr = coinArr;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_bottom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mCoinArr.get(position));
    }

    @Override
    public int getItemCount() {
        if (mCoinArr == null) {
            return 0;
        }
        return mCoinArr.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_coin_logo)
        ImageView mTvCoinLogo;
        @Bind(R.id.tv_name)
        TextView mTvName;
        private String mName;
        private int mId;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    if (mContext instanceof SendRedPacketActivity) {
                        SendRedPacketActivity activity = (SendRedPacketActivity) mContext;
                        activity.hideDialog(mName);
                    } else if (mContext instanceof PaymentActivity) {
                        PaymentActivity activity = (PaymentActivity) mContext;
                        activity.hideDialog(mName, mId);
                    } else if (mContext instanceof OtcActivity) {
                        OtcActivity activity = (OtcActivity) mContext;
                        activity.hideDialog(mName, mId);
                    } else if (mContext instanceof PushBuyingActivity) {
                        PushBuyingActivity activity = (PushBuyingActivity) mContext;
                        activity.hideDialog2(mName, mId);
                    } else if (mContext instanceof ChatTransferActivity) {
                        ChatTransferActivity activity = (ChatTransferActivity) mContext;
                        activity.hideDialog(mName, mId);
                    }else if (mContext instanceof SendQRCodeRedActivity) {
                        SendQRCodeRedActivity activity = (SendQRCodeRedActivity) mContext;
                        activity.hideDialog(mName, mId);
                    }
                }
            });
        }

        public void setData(CoinInfo.DataBean data) {
            mName = data.getName();
            mId = data.getId();
            mTvName.setText(mName);
            Glide.with(mContext).load(data.getLogo()).into(mTvCoinLogo);
        }
    }
}
