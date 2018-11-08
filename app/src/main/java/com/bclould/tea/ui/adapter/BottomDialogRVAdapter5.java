package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bclould.tea.R;
import com.bclould.tea.model.FinancialCoinInfo;
import com.bclould.tea.ui.activity.FinancialDetailActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by GA on 2018/3/16.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BottomDialogRVAdapter5 extends RecyclerView.Adapter {

    private final List<FinancialCoinInfo.DataBean> mCoinList;
    private final Context mContext;

    public BottomDialogRVAdapter5(Context context, List<FinancialCoinInfo.DataBean> coinList) {
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
        private double mCoin_over;
        private String mLogo;
        private String mServiceCharge;
        private FinancialCoinInfo.DataBean mData;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mContext instanceof FinancialDetailActivity){
                        FinancialDetailActivity activity = (FinancialDetailActivity) mContext;
                        activity.hideDialog(mData);
                    }
                }
            });
        }

        public void setData(FinancialCoinInfo.DataBean data) {
            mData = data;
            mName = data.getName();
            mCoin_over = data.getCoin_over();
            mLogo = data.getLogo();
            mId = data.getId();
            mTvName.setText(mName);
            mTvRemaining.setText(data.getCoin_over()+"");
            Glide.with(mContext).load(data.getLogo()).into(mTvCoinLogo);
        }
    }
}
