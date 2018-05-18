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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/9.
 */

public class GonggaoManagerRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<GonggaoListInfo.DataBean> mDataList;

    public GonggaoManagerRVAdapter(Context context, List<GonggaoListInfo.DataBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gonggao_manager, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
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
        private int mId;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", mId);
                    intent.putExtra("type", 1);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(GonggaoListInfo.DataBean dataBean) {
            mId = dataBean.getId();
            mTvTitle.setText(dataBean.getTitle());
            mTvTime.setText(dataBean.getCreated_at());
            mTvContent.setText(dataBean.getContent());
            if (dataBean.getIs_new() == 1) {
                mTvNew.setVisibility(View.VISIBLE);
            } else {
                mTvNew.setVisibility(View.GONE);
            }
        }
    }
}
