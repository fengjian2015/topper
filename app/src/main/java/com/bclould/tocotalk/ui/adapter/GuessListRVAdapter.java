package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.bclould.tocotalk.ui.activity.GuessDetailsActivity;

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
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (viewType == 1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_guess, parent, false);
            holder = new ViewHolder(view);
        } else if (viewType == 2) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_my_guess, parent, false);
            holder = new ViewHolder2(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 1:
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.setData(mDataList.get(position));
                break;
            case 2:
                ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                viewHolder2.setData(mDataList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getStatus();
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_kaijiang_time)
        TextView mTvKaijiangTime;
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_guess_title)
        TextView mTvGuessTitle;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.insert_coins_count)
        TextView mInsertCoinsCount;
        @Bind(R.id.tv_bonus_chi)
        TextView mTvBonusChi;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_join_time)
        TextView mTvJoinTime;
        @Bind(R.id.tv_kaijiang_result)
        TextView mTvKaijiangResult;
        @Bind(R.id.tv_guess_status)
        TextView mTvGuessStatus;
        @Bind(R.id.ll_guess_status)
        LinearLayout mLlGuessStatus;
        @Bind(R.id.ll_all)
        LinearLayout mLlAll;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final GuessListInfo.DataBean dataBean) {
            mTvGuessTitle.setText(dataBean.getTitle());
            mInsertCoinsCount.setText(dataBean.getPeriod_qty() + mContext.getString(R.string.qi));
            mTvKaijiangTime.setText(mContext.getString(R.string.kaijiang_time) + dataBean.getLottery_time());
            mTvBonusChi.setText(dataBean.getPrize_pool_number() + "/" + dataBean.getLimit_number() + " " + dataBean.getCoin_name());
            mTvKaijiangResult.setText(dataBean.getWin_number());
            mTvName.setText(dataBean.getCreated_at());
            if (dataBean.getStatus() == 1) {
            } else {
            }
            mLlAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                    intent.putExtra("bet_id", dataBean.getId());
                    intent.putExtra("period_qty", dataBean.getPeriod_qty());
                    mContext.startActivity(intent);
                }
            });
        }
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

        public void setData(final GuessListInfo.DataBean dataBean) {
            mTvGuessTitle.setText(dataBean.getTitle());
            mInsertCoinsCount.setText(dataBean.getPeriod_qty() + mContext.getString(R.string.qi));
            mTvKaijiangTime.setText(mContext.getString(R.string.kaijiang_time) + dataBean.getLottery_time());
            mTvBonusChi.setText(dataBean.getPrize_pool_number() + "/" + dataBean.getLimit_number() + " " + dataBean.getCoin_name());
            mKaijiangResult.setText(dataBean.getWin_number());
            mTvName.setText(dataBean.getCreated_at());
            if (dataBean.getStatus() == 1) {
                mBtnBet.setVisibility(View.VISIBLE);
            } else {
                mBtnBet.setVisibility(View.GONE);
            }
            mBtnBet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                    intent.putExtra("bet_id", dataBean.getId());
                    intent.putExtra("period_qty", dataBean.getPeriod_qty());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
