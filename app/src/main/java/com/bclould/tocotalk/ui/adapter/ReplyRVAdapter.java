package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.ReviewListInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/29.
 */

public class ReplyRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ReviewListInfo.DataBean.ListBean.ReplyListsBean> mReply_lists;

    public ReplyRVAdapter(Context context, List<ReviewListInfo.DataBean.ListBean.ReplyListsBean> reply_lists) {
        mContext = context;
        mReply_lists = reply_lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mReply_lists.get(position));
    }

    @Override
    public int getItemCount() {
        if (mReply_lists.size() != 0) {
            return mReply_lists.size();
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

        public void setData(ReviewListInfo.DataBean.ListBean.ReplyListsBean replyListsBean) {
            mTvContent.setText(replyListsBean.getContent());
            mTvName.setText(replyListsBean.getUser_name());
        }
    }
}
