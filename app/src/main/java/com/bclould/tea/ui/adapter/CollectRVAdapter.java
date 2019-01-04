package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.CollectPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.CollectInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.activity.HTMLActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.xmpp.RoomManage;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclould.tea.ui.activity.CollectActivity.COLLECT_JOSN;

/**
 * Created by GA on 2018/7/13.
 */

public class CollectRVAdapter extends RecyclerView.Adapter {

    private final List<CollectInfo.DataBean> mDataList;
    private final Context mContext;
    private final CollectPresenter mCollectPresenter;
    private OnItemLongClickListener mOnItemLongClickListener;
    private int intentType;
    private String roomId;

    public CollectRVAdapter(Context context, List<CollectInfo.DataBean> dataList, CollectPresenter collectPresenter, int intentType, String roomId) {
        mDataList = dataList;
        mContext = context;
        mCollectPresenter = collectPresenter;
        this.intentType = intentType;
        this.roomId = roomId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_collect, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView mIvIcon;
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.rl_item)
        RelativeLayout mRlItem;
        @Bind(R.id.btn_delete)
        Button mBtnDelete;
        @Bind(R.id.swipe_view)
        SwipeMenuLayout mSwipeView;
        private CollectInfo.DataBean mDataBean;
        private int mPosition;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (intentType == 1) {
                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setMessage(mDataBean.getUrl());
                        RoomManage.getInstance().getRoom(roomId).sendHtml(messageInfo);
                        ((Activity) mContext).finish();
                    } else {
                        Intent intent = new Intent(mContext, HTMLActivity.class);
                        intent.putExtra("html5Url", mDataBean.getUrl());
                        mContext.startActivity(intent);
                    }
                }
            });
            if (intentType != 1) {
                mRlItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        boolean isLong = mOnItemLongClickListener.onLongClick(mPosition);
                        return isLong;
                    }
                });
                mBtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mDataBean.getUser_id() != 0) {
                            showDeleteDialog(mPosition, mSwipeView);
                        } else {
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.delete_default_collect_hint));
                            mSwipeView.quickClose();
                        }
                    }
                });
            }
        }

        public void setData(CollectInfo.DataBean dataBean, int position) {
            mDataBean = dataBean;
            mPosition = position;
            if (!dataBean.getTitle().isEmpty()) {
                mTvTitle.setText(dataBean.getTitle());
            } else {
                mTvTitle.setText(dataBean.getUrl());
            }
            mSwipeView.setSwipeEnable(true);
            if (intentType == 1) {
                mSwipeView.setSwipeEnable(false);
            }
            if (dataBean.getIcon() != null && !dataBean.getIcon().isEmpty()) {
                Glide.with(mContext).load(dataBean.getIcon()).into(mIvIcon);
            } else {
                mIvIcon.setImageDrawable(null);
            }
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(int position);
    }

    private void showDeleteDialog(final int position, final SwipeMenuLayout swipeView) {
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
                mCollectPresenter.deleteCollect(mDataList.get(position).getId(), new CollectPresenter.CallBack2() {
                    @Override
                    public void send() {
                        swipeView.quickClose();
                        mDataList.remove(position);
                        CollectInfo collectInfo = new CollectInfo();
                        collectInfo.setData(mDataList);
                        Gson gson = new Gson();
                        MySharedPreferences.getInstance().setString(COLLECT_JOSN, gson.toJson(collectInfo));
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
                swipeView.quickClose();
            }
        });
    }
}
