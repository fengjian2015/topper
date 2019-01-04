/*
package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.LikeInfo;
import com.bclould.tea.model.ReviewListInfo;
import com.bclould.tea.ui.activity.PreviewImgActivity;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

*/
/**
 * Created by GA on 2017/10/19.
 *//*


public class DynamicDetailRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ReviewListInfo.DataBean.ListBean> mDataList;
    private final DBManager mMgr;
    private final DynamicPresenter mDynamicPresenter;
    public ReplyRVAdapter mReplyRVAdapter;

    public DynamicDetailRVAdapter(Context context, List<ReviewListInfo.DataBean.ListBean> dataList, DBManager mgr, DynamicPresenter dynamicPresenter) {
        mContext = context;
        mDataList = dataList;
        mMgr = mgr;
        mDynamicPresenter = dynamicPresenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_detail, parent, false);
            viewHolder = new ViewHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_detail2, parent, false);
            viewHolder = new ViewHolder2(view);
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
        return mDataList.get(position).getKey_type();
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
        @Bind(R.id.tv_zan_count)
        TextView mTvZanCount;
        @Bind(R.id.ll_zan)
        LinearLayout mLlZan;
        @Bind(R.id.dynamic_content)
        RelativeLayout mDynamicContent;
        @Bind(R.id.tv_look_reply)
        TextView mTvLookReply;
        @Bind(R.id.recycler_view)
        RecyclerView mRecyclerView;
        private ReviewListInfo.DataBean.ListBean mListBean;
        public boolean isLookReply;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.reply_comment));
                    messageEvent.setId(mListBean.getId() + "");
                    messageEvent.setCoinName(mListBean.getUser_name());
                    EventBus.getDefault().post(messageEvent);
                }
            });
            mLlZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.reviewLike(mListBean.getId() + "", new DynamicPresenter.CallBack4() {
                        @Override
                        public void send(LikeInfo data) {
                            mTvZanCount.setText(data.getData().getLikeCounts() + "");
                            if (data.getData().getStatus() == 1) {
                                mLlZan.setSelected(true);
                            } else {
                                mLlZan.setSelected(false);
                            }
                        }
                    });
                }
            });
            mTvLookReply.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    isLookReply = !isLookReply;
                    if (isLookReply) {
                        mTvLookReply.setText(mContext.getString(R.string.pack_up_reply));
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mTvLookReply.setText(mContext.getString(R.string.look_reply));
                        mRecyclerView.setVisibility(View.GONE);
                    }
                }
            });
        }

        public void setData(ReviewListInfo.DataBean.ListBean listBean) {
            mListBean = listBean;
            mCommentText.setText(listBean.getContent());
            mName.setText(listBean.getUser_name());
            mTime.setText(listBean.getCreated_at());
            mTvZanCount.setText(listBean.getLike_count() + "");
            if (listBean.getIs_like() == 1) {
                mLlZan.setSelected(true);
            } else {
                mLlZan.setSelected(false);
            }
            UtilTool.setCircleImg(mContext, listBean.getAvatar(), mTouxiang);
            if (listBean.getReply_lists() != null) {
                if (listBean.getReply_lists().size() != 0) {
                    mTvLookReply.setVisibility(View.VISIBLE);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mReplyRVAdapter = new ReplyRVAdapter(mContext, mListBean.getReply_lists());
                    mRecyclerView.setAdapter(mReplyRVAdapter);
                } else {
                    mTvLookReply.setVisibility(View.GONE);
                }
            }
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
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
        @Bind(R.id.iv_img)
        ImageView mIvImg;
        @Bind(R.id.tv_zan_count)
        TextView mTvZanCount;
        @Bind(R.id.ll_zan)
        LinearLayout mLlZan;
        @Bind(R.id.dynamic_content)
        RelativeLayout mDynamicContent;
        @Bind(R.id.tv_look_reply)
        TextView mTvLookReply;
        @Bind(R.id.recycler_view)
        RecyclerView mRecyclerView;
        private ReviewListInfo.DataBean.ListBean mListBean;
        ArrayList<String> mImgList = new ArrayList<>();
        private boolean isLookReply;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.reply_comment));
                    messageEvent.setId(mListBean.getId() + "");
                    messageEvent.setCoinName(mListBean.getUser_name());
                    EventBus.getDefault().post(messageEvent);
                }
            });
            mLlZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.reviewLike(mListBean.getId() + "", new DynamicPresenter.CallBack4() {
                        @Override
                        public void send(LikeInfo data) {
                            mTvZanCount.setText(data.getData().getLikeCounts() + "");
                            if (data.getData().getStatus() == 1) {
                                mLlZan.setSelected(true);
                            } else {
                                mLlZan.setSelected(false);
                            }
                        }
                    });
                }
            });
            mIvImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PreviewImgActivity.class);
                    intent.putExtra("index", 0);
                    intent.putStringArrayListExtra("imgList", mImgList);
                    mContext.startActivity(intent);
                }
            });
            mTvLookReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isLookReply = !isLookReply;
                    if (isLookReply) {
                        mTvLookReply.setText(mContext.getString(R.string.pack_up_reply));
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mTvLookReply.setText(mContext.getString(R.string.look_reply));
                        mRecyclerView.setVisibility(View.GONE);
                    }
                }
            });
        }

        public void setData(ReviewListInfo.DataBean.ListBean listBean) {
            mImgList.add(listBean.getUrl());
            RequestOptions requestOptions = new RequestOptions()
                    .error(R.mipmap.ic_empty_photo)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_empty_photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mContext).load(listBean.getUrl()).apply(requestOptions).into(mIvImg);
            mListBean = listBean;
            mCommentText.setText(listBean.getContent());
            mName.setText(listBean.getUser_name());
            mTime.setText(listBean.getCreated_at());
            mTvZanCount.setText(listBean.getLike_count() + "");
            if (listBean.getIs_like() == 1) {
                mLlZan.setSelected(true);
            } else {
                mLlZan.setSelected(false);
            }
            UtilTool.setCircleImg(mContext, listBean.getAvatar(), mTouxiang);
            if (listBean.getReply_lists() != null) {
                if (listBean.getReply_lists().size() != 0) {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mReplyRVAdapter = new ReplyRVAdapter(mContext, mListBean.getReply_lists());
                    mRecyclerView.setAdapter(mReplyRVAdapter);
                    mTvLookReply.setVisibility(View.VISIBLE);
                } else {
                    mTvLookReply.setVisibility(View.GONE);
                }
            }
        }
    }
}
*/
