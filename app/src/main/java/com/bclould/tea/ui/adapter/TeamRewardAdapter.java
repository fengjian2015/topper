package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.base.BaseInfoConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fengjian on 2019/1/2.
 */

public class TeamRewardAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<HashMap> mData;


    public TeamRewardAdapter(Context context, List<HashMap> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_team_reward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData.size() != 0) {
            return mData.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_state)
        TextView mTvState;
        @Bind(R.id.tv_out_money)
        TextView mTvOutMoney;
        @Bind(R.id.tv_source)
        TextView mTvSource;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(Map dataBean) {
            mTvOutMoney.setText(dataBean.get(BaseInfoConstants.NUMBER)+"");
            mTvTime.setText(dataBean.get(BaseInfoConstants.CREATED_AT)+"");
            String source = String.format(mContext.getResources().getString(R.string.reward_source),dataBean.get(BaseInfoConstants.NAME)+"",dataBean.get(BaseInfoConstants.BUY_NUMBER)+"");
            mTvSource.setText(source);
        }
    }

}
