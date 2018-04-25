package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.GuessInfo;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_guess_bet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        if(mDataList.size() != 0){
            return mDataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
            mTvArray.setText(betListBean.getBet_number());
            mTvTime.setText(betListBean.getCreated_at());
            mTvSeveral.setText("0" + position + mContext.getString(R.string.bet));
        }
    }
}
