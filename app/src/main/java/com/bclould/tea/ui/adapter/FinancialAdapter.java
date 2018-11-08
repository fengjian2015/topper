package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.FinancialInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FinancialAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<FinancialInfo.DataBean.ProductListsBean> mList;


    private OnclickListener mOnclickListener;

    public FinancialAdapter(Context context, List<FinancialInfo.DataBean.ProductListsBean> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_financia_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mList.size() != 0) {
            return mList.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_income)
        TextView mTvIncome;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_start)
        TextView mTvStart;

        private int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnclickListener != null) {
                        mOnclickListener.onclick(position);
                    }
                }
            });
        }

        public void setData(FinancialInfo.DataBean.ProductListsBean hashMap, int position) {
            this.position = position;
            mTvIncome.setText(hashMap.getIncome_rate());
            mTvTime.setText(hashMap.getTitle());
            mTvStart.setText(hashMap.getSave_min_number_desc());
        }

    }

    public void setOnClickListener(OnclickListener onClickListener) {
        this.mOnclickListener = onClickListener;
    }

    public interface OnclickListener {
        void onclick(int position);
    }
}
