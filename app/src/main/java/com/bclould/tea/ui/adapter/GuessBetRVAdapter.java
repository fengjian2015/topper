package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.GuessInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/4/25.
 */

public class GuessBetRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<GuessInfo.DataBean.BetListBean> mDataList;

    public GuessBetRVAdapter(Context context, List<GuessInfo.DataBean.BetListBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_guess_bet, parent, false);
                viewHolder = new ViewHolder(view);
                break;
            case 2:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_guess_bet, parent, false);
                viewHolder = new ViewHolder(view);
                break;
            case 3:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_guess_bet2, parent, false);
                viewHolder = new ViewHolder2(view);
                break;
            case 4:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_guess_bet, parent, false);
                viewHolder = new ViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 1 || getItemViewType(position) == 2|| getItemViewType(position) == 4) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setData(mDataList.get(position), position + 1);
        } else {
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.setData(mDataList.get(position), position + 1);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_status)
        ImageView mIvStatus;
        @Bind(R.id.tv_several)
        TextView mTvSeveral;
        @Bind(R.id.tv_array)
        TextView mTvArray;
        @Bind(R.id.tv_time)
        TextView mTvTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(GuessInfo.DataBean.BetListBean betListBean, int position) {
            if (betListBean.getStatus() == 1) {
                mIvStatus.setVisibility(View.GONE);
            } else if (betListBean.getStatus() == 2) {
                mIvStatus.setVisibility(View.VISIBLE);
            } else if (betListBean.getStatus() == 4) {
                mIvStatus.setImageResource(R.mipmap.icon_ji_return);
            }
            mTvArray.setText(betListBean.getBet_number());
            mTvTime.setText(betListBean.getCreated_at());
            if(position < 9) {
                mTvSeveral.setText("0" + position + mContext.getString(R.string.bet));
            }else {
                mTvSeveral.setText(position + mContext.getString(R.string.bet));
            }
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_status)
        ImageView mIvStatus;
        @Bind(R.id.tv_several)
        TextView mTvSeveral;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_array)
        TextView mTvArray;
        @Bind(R.id.tv_money)
        TextView mTvMoney;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(GuessInfo.DataBean.BetListBean betListBean, int position) {
            mTvArray.setText(betListBean.getBet_number());
            mTvTime.setText(betListBean.getCreated_at());
            if(position < 9) {
                mTvSeveral.setText("0" + position + mContext.getString(R.string.bet));
            }else {
                mTvSeveral.setText(position + mContext.getString(R.string.bet));
            }
            mTvMoney.setText(betListBean.getBonus_number());
        }
    }
}
