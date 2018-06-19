package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.GuessListInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/4/23.
 */

public class MyJoinRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<GuessListInfo.DataBean> mDataList;

    public MyJoinRVAdapter(Context context, List<GuessListInfo.DataBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_guess, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 20;
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
