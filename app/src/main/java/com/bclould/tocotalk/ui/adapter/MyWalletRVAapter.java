package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.MyAssetsInfo;
import com.bclould.tocotalk.ui.activity.CurrencyInOutActivity;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

public class MyWalletRVAapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<MyAssetsInfo.DataBean> mBeanList;

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
        @Bind(R.id.coin_name)
        TextView mCoinName;
        @Bind(R.id.tv_coin_count)
        TextView mTvCoinCount;
        @Bind(R.id.btn_tixian)
        Button mBtnTixian;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            mBtnTixian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CurrencyInOutActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mContext.startActivity(intent);

                }
            });

        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            Glide.with(mContext).load(ltcBean.getLogo()).into(mIvCoin);
            mTvCoinCount.setText(ltcBean.getTotal());
            mCoinName.setText(UtilTool.exChange(ltcBean.getName()));
        }
    }
}
