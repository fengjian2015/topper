package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bclould.tea.Presenter.CollectPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.CollectInfo;
import com.bclould.tea.ui.activity.HTMLActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ToastShow;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/7/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectRVAdapter extends RecyclerView.Adapter {

    private final List<CollectInfo.DataBean> mDataList;
    private final Context mContext;
    private final CollectPresenter mCollectPresenter;

    public CollectRVAdapter(Context context, List<CollectInfo.DataBean> dataList, CollectPresenter collectPresenter) {
        mDataList = dataList;
        mContext = context;
        mCollectPresenter = collectPresenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_collect, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mDataList.size() != 0){
            return mDataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        private CollectInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, HTMLActivity.class);
                    intent.putExtra("html5Url", mDataBean.getUrl());
                    mContext.startActivity(intent);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mDataBean.getUser_id() != 0) {
                        showDialog(mDataBean);
                    }else {
                        ToastShow.showToast2((Activity)mContext, mContext.getString(R.string.no_delete_collect_hint));
                    }
                    return false;
                }
            });
        }

        public void setData(CollectInfo.DataBean dataBean) {
            mDataBean = dataBean;
            mTvTitle.setText(dataBean.getTitle());
        }
    }

    private void showDialog(final CollectInfo.DataBean dataBean) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.delete_collect_hint));
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setTextColor(mContext.getResources().getColor(R.color.red));
        confirm.setText(mContext.getString(R.string.delete));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mCollectPresenter.deleteCollect(dataBean.getId(), new CollectPresenter.CallBack2() {
                    @Override
                    public void send() {
                        mDataList.remove(dataBean);
                        notifyDataSetChanged();
                    }
                });
            }
        });
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
    }
}
