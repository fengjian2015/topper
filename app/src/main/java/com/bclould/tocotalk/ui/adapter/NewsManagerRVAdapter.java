package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.GonggaoListInfo;
import com.bclould.tocotalk.ui.activity.NewsDetailsActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/8.
 */

public class NewsManagerRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<GonggaoListInfo.DataBean> mDataList;

    public NewsManagerRVAdapter(Context context, List<GonggaoListInfo.DataBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_news_manager, parent, false);
                viewHolder = new ViewHolder(view);
                break;
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
                viewHolder = new ViewHolder2(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.setData(mDataList.get(position));
                break;
            case 1:
                ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                viewHolder2.setData(mDataList.get(position));
                break;
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
        if (mDataList.get(position).getIndex_pic().isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_news_title)
        TextView mTvNewsTitle;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        private int mId;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", mId);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(GonggaoListInfo.DataBean dataBean) {
            mId = dataBean.getId();
            mTvNewsTitle.setText(dataBean.getTitle());
            mTvTime.setText(dataBean.getCreated_at());
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvImage;
        @Bind(R.id.tv_news_title)
        TextView mTvNewsTitle;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        private int mId;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", mId);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(GonggaoListInfo.DataBean dataBean) {
            mId = dataBean.getId();
            mTvNewsTitle.setText(dataBean.getTitle());
            mTvTime.setText(dataBean.getCreated_at());
            Glide.with(mContext).load(dataBean.getIndex_pic()).into(mIvImage);
        }
    }
}
