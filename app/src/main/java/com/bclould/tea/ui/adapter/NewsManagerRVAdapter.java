package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.Presenter.NewsNoticePresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.GonggaoListInfo;
import com.bclould.tea.ui.activity.NewsDetailsActivity;
import com.bclould.tea.ui.activity.NewsEditActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
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
    private final NewsNoticePresenter mNewsNoticePresenter;
    private final int mType;

    public NewsManagerRVAdapter(Context context, List<GonggaoListInfo.DataBean> dataList, NewsNoticePresenter newsNoticePresenter, int type) {
        mContext = context;
        mDataList = dataList;
        mNewsNoticePresenter = newsNoticePresenter;
        mType = type;
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
        @Bind(R.id.tv_status)
        TextView mTvStatus;
        private int mId;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            if (mType == 0 || mType == 3) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showDialog(mId);
                        return true;
                    }
                });
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    if (mType == 3) {
                        intent = new Intent(mContext, NewsEditActivity.class);
                    } else {
                        intent = new Intent(mContext, NewsDetailsActivity.class);
                    }
                    intent.putExtra("id", mId);
                    intent.putExtra("type", mType);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(GonggaoListInfo.DataBean dataBean) {
            mId = dataBean.getId();
            mTvNewsTitle.setText(dataBean.getTitle());
            mTvTime.setText(dataBean.getCreated_at());
            if (dataBean.getStatus() == 1) {
                mTvStatus.setText(mContext.getString(R.string.check_pending));
                mTvStatus.setTextColor(mContext.getResources().getColor(R.color.gray3));
            } else if (dataBean.getStatus() == 2) {
                mTvStatus.setText(mContext.getString(R.string.pass));
                mTvStatus.setTextColor(mContext.getResources().getColor(R.color.blue2));
            } else if (dataBean.getStatus() == 5) {
                mTvStatus.setText(mContext.getString(R.string.no_pass));
                mTvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }
    }

    private void showDialog(final int id) {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.delete_news_hint));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setTextColor(mContext.getResources().getColor(R.color.red));
        confirm.setText(mContext.getString(R.string.delete));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mNewsNoticePresenter.deleteNews(id, mType);
            }
        });
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
            if (mType == 0 || mType == 3) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showDialog(mId);
                        return true;
                    }
                });
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    if (mType == 3) {
                        intent = new Intent(mContext, NewsEditActivity.class);
                    } else {
                        intent = new Intent(mContext, NewsDetailsActivity.class);
                    }
                    intent.putExtra("id", mId);
                    intent.putExtra("type", mType);
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
