package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.base.BaseInfoConstants;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fengjian on 2019/1/2.
 */

public class ExchangeFRCAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;


    public ExchangeFRCAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fgc_list, parent, false);
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
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_income)
        TextView mTvIncome;
        @Bind(R.id.tv_rate)
        TextView mTvRate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(Map dataBean) {
            mTvMoney.setText(dataBean.get(BaseInfoConstants.GC_NUMBER)+"GC");
            mTvIncome.setText("+ "+dataBean.get(BaseInfoConstants.FRC_NUMBER)+"FRC");
            mTvTime.setText(dataBean.get(BaseInfoConstants.CREATED_AT)+"");
            mTvRate.setText(dataBean.get(BaseInfoConstants.RATE)+"");
        }
    }

}
