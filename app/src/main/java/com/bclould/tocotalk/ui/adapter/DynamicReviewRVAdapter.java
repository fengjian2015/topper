package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.DynamicListInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/22.
 */

public class DynamicReviewRVAdapter extends RecyclerView.Adapter {

    private final List<DynamicListInfo.DataBean.ReviewListBean> mReviewList;
    private final Context mContext;

    public DynamicReviewRVAdapter(List<DynamicListInfo.DataBean.ReviewListBean> reviewList, Context context) {
        mReviewList = reviewList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mReviewList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mReviewList.size() != 0) {
            return mReviewList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_content)
        TextView mTvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(DynamicListInfo.DataBean.ReviewListBean reviewListBean) {
            mTvName.setText(reviewListBean.getUser_name() + "  :");
            mTvContent.setText(reviewListBean.getContent());
        }
    }
}
