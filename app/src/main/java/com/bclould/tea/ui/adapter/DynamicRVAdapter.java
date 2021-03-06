package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.DynamicListInfo;
import com.bclould.tea.model.LikeInfo;
import com.bclould.tea.ui.activity.GuessDetailsActivity;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.ui.activity.PreviewImgActivity;
import com.bclould.tea.ui.activity.RewardActivity;
import com.bclould.tea.ui.activity.VideoActivity;
import com.bclould.tea.ui.widget.CommentsView;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/27.
 */

public class DynamicRVAdapter extends RecyclerView.Adapter {

    public static final int TEXT_DYNAMIC = 0;//文本动态
    public static final int IMAGE_DYNAMIC = 1;//图片动态
    public static final int VIDEO_DYNAMIC = 3;//视频动态
    public static final int MUSIC_DYNAMIC = 2;//音乐动态
    public static final int GUESS_DYNAMIC = 4;//音乐动态
    private final Context mContext;
    private final List<DynamicListInfo.DataBean> mDataList;
    private final DynamicPresenter mDynamicPresenter;
    private int mDynamicId;

    public DynamicRVAdapter(Context context, List<DynamicListInfo.DataBean> dataList, DynamicPresenter dynamicPresenter) {
        mContext = context;
        mDataList = dataList;
        mDynamicPresenter = dynamicPresenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case MUSIC_DYNAMIC:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_music_dynamic, parent, false);
                holder = new MusicHolder(view);
                break;
            case TEXT_DYNAMIC:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_text_dynamic, parent, false);
                holder = new TextHolder(view);
                break;
            case VIDEO_DYNAMIC:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_video_dynamic, parent, false);
                holder = new VideoHolder(view);
                break;
            case IMAGE_DYNAMIC:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_image_dynamic, parent, false);
                holder = new ImageHolder(view);
                break;
            case GUESS_DYNAMIC:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_guess_dynamic, parent, false);
                holder = new GuessHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case MUSIC_DYNAMIC:
                MusicHolder musicHolder = (MusicHolder) holder;
                musicHolder.setData(mDataList.get(position));
                break;
            case TEXT_DYNAMIC:
                TextHolder textHolder = (TextHolder) holder;
                textHolder.setData(mDataList.get(position));
                break;
            case VIDEO_DYNAMIC:
                VideoHolder videoHolder = (VideoHolder) holder;
                videoHolder.setData(mDataList.get(position));
                break;
            case IMAGE_DYNAMIC:
                ImageHolder imageHolder = (ImageHolder) holder;
                imageHolder.setData(mDataList.get(position));
                break;
            case GUESS_DYNAMIC:
                GuessHolder guessHolder = (GuessHolder) holder;
                guessHolder.setData(mDataList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0)
            return mDataList.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getKey_type();
    }

    public void setLookCount(int dynamicId) {
        mDynamicId = dynamicId;
    }

    class MusicHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.play)
        ImageView mPlay;
        @Bind(R.id.song_name)
        TextView mSongName;
        @Bind(R.id.ll_song)
        LinearLayout mLlSong;
        @Bind(R.id.pinglun_count)
        TextView mPinglunCount;
        @Bind(R.id.zan_count)
        TextView mZanCount;

        MusicHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(DynamicListInfo.DataBean dataBean) {

        }
    }

    class TextHolder extends RecyclerView.ViewHolder {
        private DynamicListInfo.DataBean mDataBean;
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.iv_location)
        ImageView mIvLocation;
        @Bind(R.id.tv_location)
        TextView mTvLocation;
        @Bind(R.id.tv_reward)
        TextView mTvReward;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_menu)
        LinearLayout mLlMenu;
        @Bind(R.id.commentView)
        CommentsView mCommentView;
        @Bind(R.id.tv_look_all)
        TextView mTvLookAll;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.rl_comment)
        RelativeLayout mRlComment;

        TextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mTvReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDataBean.getIs_self() == 1) {
                        ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.self_dynamic_no_reward));
                    }else {
                        Intent intent = new Intent(mContext, RewardActivity.class);
                        intent.putExtra("name", mDataBean.getUser_name());
                        intent.putExtra("url", mDataBean.getAvatar());
                        intent.putExtra("dynamic_id", mDataBean.getId());
                        mContext.startActivity(intent);
                    }
                }
            });
            mTvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.like(mDataBean.getId() + "", new DynamicPresenter.CallBack4() {
                        @Override
                        public void send(LikeInfo data) {
                            mTvZan.setText(data.getData().getLikeCounts() + "");
                            if (data.getData().getStatus() == 1) {
                                mTvZan.setSelected(true);
                            } else {
                                mTvZan.setSelected(false);
                            }
                        }
                    });
                }
            });
            mTvPinglun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.comment));
                    messageEvent.setState(mDataBean.getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    messageEvent.setType(false);
                }
            });
            mTouxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                    intent.putExtra("roomId", mDataBean.getToco_id());
                    intent.putExtra("name", mDataBean.getUser_name());
                    intent.putExtra("user", mDataBean.getToco_id());
                    mContext.startActivity(intent);
                }
            });
            mTvLookAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTvLookAll.setVisibility(View.GONE);
                    if (mDataBean.getReviewList().size() != 0) {
                        mRlComment.setVisibility(View.VISIBLE);
                        mCommentView.setList(mDataBean.getReviewList(), mDataBean.getId());
                        mCommentView.notifyDataSetChanged();
                    } else {
                        mRlComment.setVisibility(View.GONE);
                    }
                }
            });
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            if(mTvLocation==null){
                return;
            }
            mDataBean = dataBean;
            if (dataBean.getPosition() != null && !dataBean.getPosition().isEmpty()) {
                mTvLocation.setVisibility(View.VISIBLE);
                mIvLocation.setVisibility(View.VISIBLE);
                mTvLocation.setText(dataBean.getPosition());
            } else {
                mTvLocation.setVisibility(View.GONE);
                mIvLocation.setVisibility(View.GONE);
            }
            if (dataBean.getIs_self() == 1) {
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(dataBean.getId() + "");
                    }
                });
            } else {
                mIvDelete.setVisibility(View.GONE);
            }
            if (!dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mTouxiang);
            }
            if (dataBean.getReviewList().size() != 0) {
                mRlComment.setVisibility(View.VISIBLE);
                if (dataBean.getId() != mDynamicId) {
                    if (dataBean.getReviewList().size() > 5) {
                        mTvLookAll.setVisibility(View.VISIBLE);
                        mCommentView.setList(dataBean.getReviewList().subList(0, 5), dataBean.getId());
                    } else {
                        mTvLookAll.setVisibility(View.GONE);
                        mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                    }
                } else {
                    mTvLookAll.setVisibility(View.GONE);
                    mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                }
                mCommentView.notifyDataSetChanged();
            } else {
                mTvLookAll.setVisibility(View.GONE);
                mRlComment.setVisibility(View.GONE);
            }
            mTvReward.setText(dataBean.getRewardCount() + "");
            mTime.setText(dataBean.getCreated_at());
            mName.setText(dataBean.getUser_name());
            mTextContent.setText(dataBean.getContent());
            mTvPinglun.setText(dataBean.getReview_count() + "");
            mTvZan.setText(dataBean.getLike_count() + "");
            if (dataBean.getIs_like() == 1) {
                mTvZan.setSelected(true);
            } else {
                mTvZan.setSelected(false);
            }
        }
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.iv_video)
        ImageView mIvVideo;
        @Bind(R.id.iv_video_play)
        ImageView mIvVideoPlay;
        @Bind(R.id.rl_video)
        RelativeLayout mRlVideo;
        @Bind(R.id.iv_location)
        ImageView mIvLocation;
        @Bind(R.id.tv_location)
        TextView mTvLocation;
        @Bind(R.id.tv_reward)
        TextView mTvReward;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.commentView)
        CommentsView mCommentView;
        @Bind(R.id.tv_look_all)
        TextView mTvLookAll;
        @Bind(R.id.rl_comment)
        RelativeLayout mRlComment;
        private DynamicListInfo.DataBean mDataBean;
        private ArrayList<String> mCompressImgList;
        private ArrayList<String> mImgList = new ArrayList<>();

        VideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mTvReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDataBean.getIs_self() == 1) {
                        ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.self_dynamic_no_reward));
                    }else {
                        Intent intent = new Intent(mContext, RewardActivity.class);
                        intent.putExtra("name", mDataBean.getUser_name());
                        intent.putExtra("url", mDataBean.getAvatar());
                        intent.putExtra("dynamic_id", mDataBean.getId());
                        mContext.startActivity(intent);
                    }
                }
            });
            mRlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("url", mImgList.get(0));
                    intent.putExtra("compressUrl",(String) mDataBean.getKey_compress_urls().get(0));
                    mContext.startActivity(intent);
                }
            });

            mTvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.like(mDataBean.getId() + "", new DynamicPresenter.CallBack4() {
                        @Override
                        public void send(LikeInfo data) {
                            mTvZan.setText(data.getData().getLikeCounts() + "");
                            if (data.getData().getStatus() == 1) {
                                mTvZan.setSelected(true);
                            } else {
                                mTvZan.setSelected(false);
                            }
                        }
                    });
                }
            });
            mTvPinglun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.comment));
                    messageEvent.setState(mDataBean.getId() + "");
                    messageEvent.setType(false);
                    EventBus.getDefault().post(messageEvent);
                }
            });
            mTouxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                    intent.putExtra("roomId", mDataBean.getToco_id());
                    intent.putExtra("name", mDataBean.getUser_name());
                    intent.putExtra("user", mDataBean.getToco_id());
                    mContext.startActivity(intent);
                }
            });
            mTvLookAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTvLookAll.setVisibility(View.GONE);
                    if (mDataBean.getReviewList().size() != 0) {
                        mRlComment.setVisibility(View.VISIBLE);
                        mCommentView.setList(mDataBean.getReviewList(), mDataBean.getId());
                        mCommentView.notifyDataSetChanged();
                    } else {
                        mRlComment.setVisibility(View.GONE);
                    }
                }
            });
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            if(mTvLocation==null){
                return;
            }
            mDataBean = dataBean;
            mCompressImgList = (ArrayList<String>) dataBean.getKey_compress_urls();
            mImgList.clear();
            mImgList.addAll((ArrayList<String>) dataBean.getKey_urls());
            if(mTvLocation==null){
                return;
            }
            if (dataBean.getPosition() != null && !dataBean.getPosition().isEmpty()) {
                mTvLocation.setVisibility(View.VISIBLE);
                mIvLocation.setVisibility(View.VISIBLE);
                mTvLocation.setText(dataBean.getPosition());
            } else {
                mTvLocation.setVisibility(View.GONE);
                mIvLocation.setVisibility(View.GONE);
            }
            if (dataBean.getIs_self() == 1) {
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(dataBean.getId() + "");
                    }
                });
            } else {
                mIvDelete.setVisibility(View.GONE);
            }
            if (!dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mTouxiang);
            }
            if (dataBean.getReviewList().size() != 0) {
                mRlComment.setVisibility(View.VISIBLE);
                if (dataBean.getId() != mDynamicId) {
                    if (dataBean.getReviewList().size() > 5) {
                        mTvLookAll.setVisibility(View.VISIBLE);
                        mCommentView.setList(dataBean.getReviewList().subList(0, 5), dataBean.getId());
                    } else {
                        mTvLookAll.setVisibility(View.GONE);
                        mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                    }
                } else {
                    mTvLookAll.setVisibility(View.GONE);
                    mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                }
                mCommentView.notifyDataSetChanged();
            } else {
                mTvLookAll.setVisibility(View.GONE);
                mRlComment.setVisibility(View.GONE);
            }
            if (dataBean.getKey_compress_urls() != null) {
                if (dataBean.getKey_compress_urls().size() != 0) {
                    Glide.with(mContext)
                            .load((dataBean.getKey_compress_urls()).get(0))
                            .apply(new RequestOptions().error(R.mipmap.ic_empty_photo).centerCrop().placeholder(R.mipmap.ic_empty_photo))
                            .into(mIvVideo);
                }
            }
            mTvReward.setText(dataBean.getRewardCount() + "");
            mTime.setText(dataBean.getCreated_at());
            mName.setText(dataBean.getUser_name());
            if (dataBean.getContent().isEmpty()) {
                mTextContent.setVisibility(View.GONE);
            }else {
                mTextContent.setText(dataBean.getContent());
            }
            mTvPinglun.setText(dataBean.getReview_count() + "");
            mTvZan.setText(dataBean.getLike_count() + "");
            if (dataBean.getIs_like() == 1) {
                mTvZan.setSelected(true);
            } else {
                mTvZan.setSelected(false);
            }
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.ngl_images)
        NineGridImageView mNglImages;
        @Bind(R.id.iv_location)
        ImageView mIvLocation;
        @Bind(R.id.tv_location)
        TextView mTvLocation;
        @Bind(R.id.tv_reward)
        TextView mTvReward;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.commentView)
        CommentsView mCommentView;
        @Bind(R.id.tv_look_all)
        TextView mTvLookAll;
        @Bind(R.id.rl_comment)
        RelativeLayout mRlComment;
        private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String s) {
                Glide.with(context).load(s).apply(new RequestOptions().placeholder(R.mipmap.ic_empty_photo).error(R.mipmap.ic_empty_photo)).into(imageView);
            }

            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

            @Override
            protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
//                Toast.makeText(context, "image position is " + index, Toast.LENGTH_SHORT).show();
            }
        };
        private ArrayList<String> mCompressImgList;
        private ArrayList<String> mImgList;
        private DynamicListInfo.DataBean mDataBean;

        public ImageHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mNglImages.setAdapter(mAdapter);
            mNglImages.setItemImageClickListener(new ItemImageClickListener<String>() {
                @Override
                public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
                    Intent intent = new Intent(mContext, PreviewImgActivity.class);
                    intent.putExtra("index", index);
                    intent.putStringArrayListExtra("imgList", mImgList);
                    context.startActivity(intent);

                }
            });
            mTvReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDataBean.getIs_self() == 1) {
                        ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.self_dynamic_no_reward));
                    }else {
                        Intent intent = new Intent(mContext, RewardActivity.class);
                        intent.putExtra("name", mDataBean.getUser_name());
                        intent.putExtra("url", mDataBean.getAvatar());
                        intent.putExtra("dynamic_id", mDataBean.getId());
                        mContext.startActivity(intent);
                    }
                }
            });

            mTvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.like(mDataBean.getId() + "", new DynamicPresenter.CallBack4() {
                        @Override
                        public void send(LikeInfo data) {
                            mTvZan.setText(data.getData().getLikeCounts() + "");
                            mDataBean.setLike_count(data.getData().getLikeCounts());
                            if (data.getData().getStatus() == 1) {
                                mTvZan.setSelected(true);
                                mDataBean.setIs_like(1);
                            } else {
                                mTvZan.setSelected(false);
                                mDataBean.setIs_like(0);
                            }
                        }
                    });
                }
            });

            mTvPinglun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.comment));
                    messageEvent.setState(mDataBean.getId() + "");
                    messageEvent.setType(false);
                    EventBus.getDefault().post(messageEvent);
                }
            });
            mIvTouxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                    intent.putExtra("roomId", mDataBean.getToco_id());
                    intent.putExtra("name", mDataBean.getUser_name());
                    intent.putExtra("user", mDataBean.getToco_id());
                    mContext.startActivity(intent);
                }
            });
            mTvLookAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTvLookAll.setVisibility(View.GONE);
                    if (mDataBean.getReviewList().size() != 0) {
                        mRlComment.setVisibility(View.VISIBLE);
                        mCommentView.setList(mDataBean.getReviewList(), mDataBean.getId());
                        mCommentView.notifyDataSetChanged();
                    } else {
                        mRlComment.setVisibility(View.GONE);
                    }
                }
            });
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            if(mTvLocation==null){
                return;
            }
            mDataBean = dataBean;
            mCompressImgList = (ArrayList<String>) dataBean.getKey_compress_urls();
            mImgList = (ArrayList<String>) dataBean.getKey_urls();
            if (dataBean.getPosition() != null && !dataBean.getPosition().isEmpty()) {
                mTvLocation.setVisibility(View.VISIBLE);
                mIvLocation.setVisibility(View.VISIBLE);
                mTvLocation.setText(dataBean.getPosition());
            } else {
                mTvLocation.setVisibility(View.GONE);
                mIvLocation.setVisibility(View.GONE);
            }
            mNglImages.setImagesData(mCompressImgList);
            if (!dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            }
            if (dataBean.getIs_self() == 1) {
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(dataBean.getId() + "");
                    }
                });
            } else {
                mIvDelete.setVisibility(View.GONE);
            }
            if (dataBean.getReviewList().size() != 0) {
                mRlComment.setVisibility(View.VISIBLE);
                if (dataBean.getId() != mDynamicId) {
                    if (dataBean.getReviewList().size() > 5) {
                        mTvLookAll.setVisibility(View.VISIBLE);
                        mCommentView.setList(dataBean.getReviewList().subList(0, 5), dataBean.getId());
                    } else {
                        mTvLookAll.setVisibility(View.GONE);
                        mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                    }
                } else {
                    mTvLookAll.setVisibility(View.GONE);
                    mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                }
                mCommentView.notifyDataSetChanged();
            } else {
                mTvLookAll.setVisibility(View.GONE);
                mRlComment.setVisibility(View.GONE);
            }
            mTvReward.setText(dataBean.getRewardCount() + "");
            mTvTime.setText(dataBean.getCreated_at());
            mTvName.setText(dataBean.getUser_name());
            if (dataBean.getContent().isEmpty()) {
                mTvContent.setVisibility(View.GONE);
            }else {
                mTvContent.setText(dataBean.getContent());
            }
            mTvPinglun.setText(dataBean.getReview_count() + "");
            mTvZan.setText(dataBean.getLike_count() + "");
            if (dataBean.getIs_like() == 1) {
                mTvZan.setSelected(true);
            } else {
                mTvZan.setSelected(false);
            }
        }
    }

    private void showDialog(final String id) {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.delete_dynamic_hint));
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
                mDynamicPresenter.deleteDynamic(id);
            }
        });
    }

    class GuessHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.iv_logo)
        ImageView mIvLogo;
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_who)
        TextView mTvWho;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.cv_guess)
        CardView mCvGuess;
        @Bind(R.id.iv_location)
        ImageView mIvLocation;
        @Bind(R.id.tv_location)
        TextView mTvLocation;
        @Bind(R.id.tv_reward)
        TextView mTvReward;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.commentView)
        CommentsView mCommentView;
        @Bind(R.id.tv_look_all)
        TextView mTvLookAll;
        @Bind(R.id.rl_comment)
        RelativeLayout mRlComment;
        private DynamicListInfo.DataBean mDataBean;
        private String mGuessPw;
        private int mGuessId;
        private int mPeriod_aty;

        GuessHolder(View view) {
            super(view);
            try {
                ButterKnife.bind(this, view);
                mTvReward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mDataBean.getIs_self() == 1) {
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.self_dynamic_no_reward));
                        }else {
                            Intent intent = new Intent(mContext, RewardActivity.class);
                            intent.putExtra("name", mDataBean.getUser_name());
                            intent.putExtra("url", mDataBean.getAvatar());
                            intent.putExtra("dynamic_id", mDataBean.getId());
                            mContext.startActivity(intent);
                        }
                    }
                });
                mTvReward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, RewardActivity.class);
                        intent.putExtra("name", mDataBean.getUser_name());
                        intent.putExtra("url", mDataBean.getAvatar());
                        intent.putExtra("dynamic_id", mDataBean.getId());
                        mContext.startActivity(intent);
                    }
                });
                mTvPinglun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.comment));
                        messageEvent.setState(mDataBean.getId() + "");
                        messageEvent.setType(false);
                        EventBus.getDefault().post(messageEvent);
                    }
                });
                mTvZan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDynamicPresenter.like(mDataBean.getId() + "", new DynamicPresenter.CallBack4() {
                            @Override
                            public void send(LikeInfo data) {
                                mTvZan.setText(data.getData().getLikeCounts() + "");
                                if (data.getData().getStatus() == 1) {
                                    mTvZan.setSelected(true);
                                } else {
                                    mTvZan.setSelected(false);
                                }
                            }
                        });
                    }
                });
                mIvTouxiang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                        intent.putExtra("roomId", mDataBean.getToco_id());
                        intent.putExtra("name", mDataBean.getUser_name());
                        intent.putExtra("user", mDataBean.getToco_id());
                        mContext.startActivity(intent);
                    }
                });
                mTvLookAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mTvLookAll.setVisibility(View.GONE);
                        if (mDataBean.getReviewList().size() != 0) {
                            mRlComment.setVisibility(View.VISIBLE);
                            mCommentView.setList(mDataBean.getReviewList(), mDataBean.getId());
                            mCommentView.notifyDataSetChanged();
                        } else {
                            mRlComment.setVisibility(View.GONE);
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            if(mTvLocation==null){
                return;
            }
            mDataBean = dataBean;
            if (dataBean.getPosition() != null && !dataBean.getPosition().isEmpty()) {
                mTvLocation.setVisibility(View.VISIBLE);
                mIvLocation.setVisibility(View.VISIBLE);
                mTvLocation.setText(dataBean.getPosition());
            } else {
                mTvLocation.setVisibility(View.GONE);
                mIvLocation.setVisibility(View.GONE);
            }
            if (dataBean.getIs_self() == 1) {
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(dataBean.getId() + "");
                    }
                });
            } else {
                mIvDelete.setVisibility(View.GONE);
            }
            if (!dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            }
            if (dataBean.getReviewList().size() != 0) {
                mRlComment.setVisibility(View.VISIBLE);
                if (dataBean.getId() != mDynamicId) {
                    if (dataBean.getReviewList().size() > 5) {
                        mTvLookAll.setVisibility(View.VISIBLE);
                        mCommentView.setList(dataBean.getReviewList().subList(0, 5), dataBean.getId());
                    } else {
                        mTvLookAll.setVisibility(View.GONE);
                        mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                    }
                } else {
                    mTvLookAll.setVisibility(View.GONE);
                    mCommentView.setList(dataBean.getReviewList(), dataBean.getId());
                }
                mCommentView.notifyDataSetChanged();
            } else {
                mTvLookAll.setVisibility(View.GONE);
                mRlComment.setVisibility(View.GONE);
            }
            if (dataBean.getContent().contains(Constants.GUESS_DYNAMIC_SEPARATOR)) {
                UtilTool.Log("競猜分享", dataBean.getContent());
                String[] split = dataBean.getContent().split(Constants.GUESS_DYNAMIC_SEPARATOR);
                if (split.length == 7) {
                    mGuessPw = split[6];
                } else {
                    mGuessPw = null;
                }
                mGuessId = Integer.parseInt(split[4]);
                mPeriod_aty = Integer.parseInt(split[5]);
                mTvContent.setText(split[0]);
                mTvTitle.setText(split[1]);
                mTvWho.setText(mContext.getString(R.string.fa_qi_ren) + ":" + split[2]);
                mTvCoin.setText(split[3] + mContext.getString(R.string.guess));
            }
            mTvReward.setText(dataBean.getRewardCount() + "");
            mTvTime.setText(dataBean.getCreated_at());
            mTvName.setText(dataBean.getUser_name());
            mTvPinglun.setText(dataBean.getReview_count() + "");
            mTvZan.setText(dataBean.getLike_count() + "");
            if (dataBean.getIs_like() == 1) {
                mTvZan.setSelected(true);
            } else {
                mTvZan.setSelected(false);
            }
        }
    }

    private void showPWDialog(final String guessPw, final int guessId, final int period_aty) {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_command, mContext, R.style.dialog);
        deleteCacheDialog.show();
        final EditText etGuessPw = (EditText) deleteCacheDialog.findViewById(R.id.et_guess_password);
        Button btnConfirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = etGuessPw.getText().toString();
                if (pw.isEmpty()) {
                    AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                    Toast.makeText(mContext, mContext.getString(R.string.toast_guess_pw), Toast.LENGTH_SHORT).show();
                } else {
                    if (guessPw.equals(pw)) {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", guessId);
                        intent.putExtra("period_qty", period_aty);
                        intent.putExtra("guess_pw", guessPw);
                        mContext.startActivity(intent);
                        deleteCacheDialog.dismiss();
                    } else {
                        AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                        Toast.makeText(mContext, mContext.getString(R.string.toast_guess_pw_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
