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
import com.bclould.tea.model.HistoryInfo;
import com.bclould.tea.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class HistoryAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<HistoryInfo.DataBean> mList;
    private OnclickListener mOnclickListener;

    public HistoryAdapter(Context context, List<HistoryInfo.DataBean> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_list, parent, false);
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
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_source)
        TextView mTvSource;
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

        public void setData(HistoryInfo.DataBean hashMap, int position) {
            this.position = position;
            mTvTitle.setText(hashMap.getType());
            mTvTime.setText(hashMap.getCreated_at());
            mTvMoney.setText(hashMap.getAmount());
            if(!StringUtils.isEmpty(hashMap.getFrom())){
                mTvSource.setVisibility(View.VISIBLE);
                mTvSource.setText(hashMap.getFrom());
            }else{
                mTvSource.setVisibility(View.GONE);
            }
        }
    }

    public void setOnClickListener(OnclickListener onClickListener) {
        this.mOnclickListener = onClickListener;
    }

    public interface OnclickListener {
        void onclick(int position);
    }
}
