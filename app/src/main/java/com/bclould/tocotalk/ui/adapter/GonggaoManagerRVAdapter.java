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
import com.bclould.tocotalk.ui.activity.GonggaoDetailActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/9.
 */

public class GonggaoManagerRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;

    public GonggaoManagerRVAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gonggao_manager, parent, false);
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


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv)
        ImageView mIv;
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_new)
        TextView mTvNew;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.xx)
        TextView mXx;
        @Bind(R.id.tv_content)
        TextView mTvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, GonggaoDetailActivity.class));
                }
            });
        }
    }
}
