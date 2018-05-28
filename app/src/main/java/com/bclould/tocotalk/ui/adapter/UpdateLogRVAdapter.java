package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.UpdateLogInfo;
import com.bclould.tocotalk.ui.activity.NewsDetailsActivity;
import com.bclould.tocotalk.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/28.
 */

public class UpdateLogRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<UpdateLogInfo.DataBean> mDataList;

    public UpdateLogRVAdapter(Context context, List<UpdateLogInfo.DataBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_update_log, parent, false);
        return new ViewHolder(view);
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
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        private int mId;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", mId);
                    intent.putExtra("type", Constants.UPDATE_LOG_TYPE);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(UpdateLogInfo.DataBean dataBean) {
            mId = dataBean.getId();
            mTvTitle.setText(mContext.getString(R.string.app_name) + dataBean.getVersion() + mContext.getString(R.string.zhuyao_update));
            mTvTime.setText(dataBean.getCreated_at());
        }
    }
}
