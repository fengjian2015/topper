package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.SubscribeCoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.MyAssetsInfo;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/2/27.
 */

public class SubscribeCoinRVAdatper extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<MyAssetsInfo.DataBean> mInfo;
    private final SubscribeCoinPresenter mSubscribeCoinPresenter;

    public SubscribeCoinRVAdatper(Context context, List<MyAssetsInfo.DataBean> info, SubscribeCoinPresenter subscribeCoinPresenter) {
        mContext = context;
        mInfo = info;
        mSubscribeCoinPresenter = subscribeCoinPresenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_add_asset, parent, false);
        return new SubscribeCoinRVAdatper.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mInfo.get(position));
    }

    @Override
    public int getItemCount() {
        if (mInfo.size() == 0)
            return 0;
        return mInfo.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_coin)
        ImageView mIvCoin;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.btc_add)
        Button mBtcAdd;
        private MyAssetsInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mBtcAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDataBean.getStatus() == 1) {
                        mDataBean.setStatus(2);
                        mSubscribeCoinPresenter.unSubscribeAsset(mDataBean.getId());
                        mBtcAdd.setBackgroundResource(R.drawable.bg_buysell_shape);
                        mBtcAdd.setText(mContext.getString(R.string.add));
                        mBtcAdd.setTextColor(mContext.getResources().getColor(R.color.blue));
                    } else {
                        mDataBean.setStatus(1);
                        mSubscribeCoinPresenter.subscribeAsset(mDataBean.getId());
                        mBtcAdd.setBackgroundResource(R.drawable.bg_register_shape);
                        mBtcAdd.setText(mContext.getString(R.string.cancel));
                        mBtcAdd.setTextColor(mContext.getResources().getColor(R.color.black));
                    }
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean dataBean) {
            mDataBean = dataBean;
            Glide.with(mContext).load(dataBean.getLogo()).into(mIvCoin);
            mTvCoin.setText(dataBean.getName());
            if (dataBean.getStatus() == 1) {
                mBtcAdd.setBackgroundResource(R.drawable.bg_register_shape);

                mBtcAdd.setText(mContext.getString(R.string.cancel));

                mBtcAdd.setTextColor(mContext.getResources().getColor(R.color.black));
            } else {
                mBtcAdd.setBackgroundResource(R.drawable.bg_buysell_shape);

                mBtcAdd.setText(mContext.getString(R.string.add));

                mBtcAdd.setTextColor(mContext.getResources().getColor(R.color.blue));
            }
        }
    }
}
