/*
package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.DynamicListInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

*/
/**
 * Created by GA on 2018/5/22.
 *//*


public class DynamicReviewRVAdapter extends RecyclerView.Adapter {

    private static final int DYNAMIC_COMMENT = 1;
    private static final int DYNAMIC_REPLY = 2;
    private final List<DynamicListInfo.DataBean.ReviewListBean> mReviewList;
    private final Context mContext;

    public DynamicReviewRVAdapter(List<DynamicListInfo.DataBean.ReviewListBean> reviewList, Context context) {
        mReviewList = reviewList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_comment, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
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
        private int mId;
        private String mUser_name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            */
/*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.comment));
                    messageEvent.setId(mId + "");
                    messageEvent.setCoinName(mUser_name);
                    EventBus.getDefault().post(messageEvent);
                }
            });*//*

        }

        public void setData(DynamicListInfo.DataBean.ReviewListBean reviewListBean) {
            mId = reviewListBean.getId();
            mUser_name = reviewListBean.getUser_name();
            mTvName.setText(reviewListBean.getUser_name() + "  :");
            mTvContent.setText(reviewListBean.getContent());
        }
    }
}
*/
