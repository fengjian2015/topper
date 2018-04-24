package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.GuessListInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/4/23.
 */

public class GuessListRVAdapter extends RecyclerView.Adapter {
    private final List<GuessListInfo.DataBean> mDataList;
    private final Context mContext;

    public GuessListRVAdapter(List<GuessListInfo.DataBean> dataList, Context context) {
        mDataList = dataList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_guess, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (mDataList.size() != 0)
            viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_kaijiang_time)
        TextView mTvKaijiangTime;
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_guess_title)
        TextView mTvGuessTitle;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.btn_bet)
        Button mBtnBet;
        @Bind(R.id.insert_coins_count)
        TextView mInsertCoinsCount;
        @Bind(R.id.tv_bonus_chi)
        TextView mTvBonusChi;
        @Bind(R.id.kaijiang_result)
        TextView mKaijiangResult;
        @Bind(R.id.ll_all)
        LinearLayout mLlAll;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(GuessListInfo.DataBean dataBean) {
            mTvGuessTitle.setText(dataBean.getTitle());
            mInsertCoinsCount.setText(dataBean.getPeriod_qty() + mContext.getString(R.string.qi));
            mTvKaijiangTime.setText(dataBean.getLottery_time());
            mTvBonusChi.setText(dataBean.getPrize_pool_number() + "/" + dataBean.getLimit_number() + " " + dataBean.getCoin_name());
            mKaijiangResult.setText(dataBean.getWin_number());
            mTvName.setText(dataBean.getCreated_at());
            if (dataBean.getStatus() == 1) {

            } else {
                if(dataBean.getLottery_status() == 0){

                }else if(dataBean.getLottery_status() == 1){

                }else if(dataBean.getLottery_status() == 2){

                }
            }

        }
    }
}
