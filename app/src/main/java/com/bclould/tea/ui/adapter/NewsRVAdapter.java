package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.NewsListInfo;
import com.bclould.tea.ui.activity.NewsDetailsActivity;
import com.bclould.tea.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by GA on 2018/3/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<NewsListInfo.ListsBean> mNewsList;



    public NewsRVAdapter(Context context, List<NewsListInfo.ListsBean> newsList) {
        mContext = context;
        mNewsList = newsList;
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
                viewHolder.setData(mNewsList.get(position));
                break;
            case 1:
                ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                viewHolder2.setData(mNewsList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mNewsList.size() != 0) {
            return mNewsList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mNewsList.get(position).getIndex_pic().isEmpty()) {
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
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_look_count)
        TextView mTvLookCount;
        private int mId;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", mId);
                    intent.putExtra("type", Constants.NEWS_MAIN_TYPE);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(NewsListInfo.ListsBean listsBean) {
            if (listsBean.getIs_ad() == 1) {
                mTvType.setVisibility(View.VISIBLE);
                mTvLookCount.setVisibility(View.GONE);
            } else {
                mTvType.setVisibility(View.GONE);
                mTvLookCount.setVisibility(View.VISIBLE);
                mTvLookCount.setText(listsBean.getView_count() + "");
            }
            mId = listsBean.getId();
            mTvNewsTitle.setText(listsBean.getTitle());
            mTvTime.setText(listsBean.getCreated_at());
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvImage;
        @Bind(R.id.tv_news_title)
        TextView mTvNewsTitle;
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_look_count)
        TextView mTvLookCount;
        private int mId;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", mId);
                    intent.putExtra("type", Constants.NEWS_MAIN_TYPE);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(NewsListInfo.ListsBean listBean) {
            if (listBean.getIs_ad() == 1) {
                mTvType.setVisibility(View.VISIBLE);
                mTvLookCount.setVisibility(View.GONE);
            } else {
                mTvType.setVisibility(View.GONE);
                mTvLookCount.setVisibility(View.VISIBLE);
                mTvLookCount.setText(listBean.getView_count() + "");
            }
            mId = listBean.getId();
            mTvNewsTitle.setText(listBean.getTitle());
            mTvTime.setText(listBean.getCreated_at());

            RequestOptions requestOptions = new RequestOptions()
                    .error(R.mipmap.ic_empty_photo)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_empty_photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            if(mContext==null)return;
            Glide.with(mContext).load(listBean.getIndex_pic()).apply(requestOptions).into(mIvImage);
        }
    }
}
