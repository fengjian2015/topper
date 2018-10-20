package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.MyAssetsInfo;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

public class MyWalletRVAapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<MyAssetsInfo.DataBean> mBeanList;
    private OnItemClickListener mOnItemClickListener;

    public MyWalletRVAapter(Context context, List<MyAssetsInfo.DataBean> beanList) {
        mContext = context;
        mBeanList = beanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_wallet, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mBeanList != null)
            return mBeanList.size();
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_coin)
        ImageView mIvCoin;
        @Bind(R.id.tv_coin_name)
        TextView mTvCoinName;
        @Bind(R.id.tv_coin_value)
        TextView mTvCoinValue;
        @Bind(R.id.tv_usable_count)
        TextView mTvUsableCount;
        @Bind(R.id.tv_freeze_count)
        TextView mTvFreezeCount;
        @Bind(R.id.iv_popup)
        ImageView mIvPopup;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            Glide.with(mContext).load(ltcBean.getLogo()).into(mIvCoin);
            mTvFreezeCount.setText(mContext.getString(R.string.freeze) + " " + ltcBean.getLock());
            mTvUsableCount.setText(mContext.getString(R.string.usable) + " " + ltcBean.getOver());
            mTvCoinName.setText(ltcBean.getName());
            mTvCoinValue.setText(ltcBean.getNumber() + ltcBean.getCurrency());
            mIvPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(view, ltcBean);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, MyAssetsInfo.DataBean dataBean);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
