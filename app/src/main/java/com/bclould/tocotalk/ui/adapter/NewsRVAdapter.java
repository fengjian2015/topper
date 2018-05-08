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
import com.bclould.tocotalk.model.NewsListInfo;
import com.bclould.tocotalk.ui.activity.NewsDetailsActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/21.
 */

public class NewsRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<NewsListInfo.ListsBean> mNewsList;

    public NewsRVAdapter(Context context, List<NewsListInfo.ListsBean> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mNewsList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mNewsList.size() != 0){
            return mNewsList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvImage;
        @Bind(R.id.tv_news_title)
        TextView mTvNewsTitle;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        private int mId;

        ViewHolder(View view) {
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

        public void setData(NewsListInfo.ListsBean listBean) {
            mId = listBean.getId();
            mTvNewsTitle.setText(listBean.getTitle());
            mTvTime.setText(listBean.getCreated_at());
            Glide.with(mContext).load(listBean.getIndex_pic()).into(mIvImage);
        }
    }
}
