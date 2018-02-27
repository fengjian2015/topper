package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/19.
 */

public class DynamicDetailRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;

    public DynamicDetailRVAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_detail, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time_tv)
        ImageView mTimeTv;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.comment_text)
        TextView mCommentText;
        @Bind(R.id.ll_zan)
        LinearLayout mLlZan;
        @Bind(R.id.dynamic_content)
        RelativeLayout mDynamicContent;
        boolean isZan = false;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mLlZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isZan = !isZan;
                    if (isZan)
                        mLlZan.setSelected(true);
                    else
                        mLlZan.setSelected(false);

                }
            });
        }
    }
}
