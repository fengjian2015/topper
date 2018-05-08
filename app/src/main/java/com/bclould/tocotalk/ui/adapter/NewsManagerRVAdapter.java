package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/8.
 */

public class NewsManagerRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;

    public NewsManagerRVAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_news_manager, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_news_title)
        TextView mTvNewsTitle;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.iv_jiantou)
        ImageView mIvJiantou;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
