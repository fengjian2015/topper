package com.bclould.tocotalk.ui.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.CoinInfo;
import com.bclould.tocotalk.ui.activity.SendRedPacketActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/28.
 */

public class BottomDialogRVAdapter2 extends RecyclerView.Adapter {

    private final SendRedPacketActivity mSendRedPacketActivity;
    private final List<CoinInfo.DataBean> mCoinArr;


    public BottomDialogRVAdapter2(SendRedPacketActivity sendRedPacketActivity, List<CoinInfo.DataBean> coinArr) {
        mSendRedPacketActivity = sendRedPacketActivity;
        mCoinArr = coinArr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mSendRedPacketActivity).inflate(R.layout.item_dialog_bottom, parent, false);
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    mSendRedPacketActivity.hideDialog(mName);
                }
            });
        }

        public void setData(CoinInfo.DataBean data) {
            mName = data.getName();
            mTvName.setText(mName);
            Glide.with(mSendRedPacketActivity).load(data.getLogo()).into(mTvCoinLogo);
        }
    }
}
