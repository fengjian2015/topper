package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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

import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.DynamicListInfo;
import com.bclould.tocotalk.model.LikeInfo;
import com.bclould.tocotalk.ui.activity.DynamicDetailActivity;
import com.bclould.tocotalk.ui.activity.GuessDetailsActivity;
import com.bclould.tocotalk.ui.activity.PreviewImgActivity;
import com.bclould.tocotalk.ui.activity.RewardActivity;
import com.bclould.tocotalk.ui.activity.VideoActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.previewlibrary.enitity.ThumbViewInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicRVAdapter extends RecyclerView.Adapter {

    public static final int TEXT_DYNAMIC = 0;//文本动态
    public static final int IMAGE_DYNAMIC = 1;//图片动态
    public static final int VIDEO_DYNAMIC = 3;//视频动态
    public static final int MUSIC_DYNAMIC = 2;//音乐动态
    public static final int GUESS_DYNAMIC = 4;//音乐动态
    private final Context mContext;
    private final List<DynamicListInfo.DataBean> mDataList;
    private final DBManager mMgr;
    private final DynamicPresenter mDynamicPresenter;
    public DynamicReviewRVAdapter mDynamicReviewRVAdapter;

    public DynamicRVAdapter(Context context, List<DynamicListInfo.DataBean> dataList, DBManager mgr, DynamicPresenter dynamicPresenter) {
        mContext = context;
        mDataList = dataList;
        mMgr = mgr;
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, DynamicDetailActivity.class));

                }
            });
        }

        public void setData(DynamicListInfo.DataBean dataBean) {

        }
    }

    class TextHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_look)
        TextView mTvLook;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.ll_review)
        LinearLayout mLlReview;
        @Bind(R.id.recycler_view)
        RecyclerView mRecyvlerView;
        @Bind(R.id.tv_reward)
        TextView mTvReward;
        private DynamicListInfo.DataBean mDataBean;

        TextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.setClass(mContext, DynamicDetailActivity.class);
                    bundle.putString("id", mDataBean.getId() + "");
                    bundle.putString("content", mDataBean.getContent());
                    bundle.putInt("type", 1);
                    bundle.putString("name", mDataBean.getUser_name());
                    bundle.putString("time", mDataBean.getCreated_at());
                    bundle.putInt("is_self", mDataBean.getIs_self());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
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
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            mDataBean = dataBean;
            if (dataBean.getIs_self() == 1) {
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(dataBean.getId() + "");
                    }
                });
                mTvReward.setVisibility(View.GONE);
            } else {
                mTvReward.setVisibility(View.VISIBLE);
                mIvDelete.setVisibility(View.GONE);
            }
            if (!dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mTouxiang);
            }
            if (dataBean.getReviewList().size() != 0) {
                mLlReview.setVisibility(View.VISIBLE);
                if (dataBean.getReviewList().size() > 5) {
                    mTvLook.setVisibility(View.VISIBLE);
                } else {
                    mTvLook.setVisibility(View.GONE);
                }
                mRecyvlerView.setLayoutManager(new LinearLayoutManager(mContext));
                mDynamicReviewRVAdapter = new DynamicReviewRVAdapter(dataBean.getReviewList(), mContext);
                mRecyvlerView.setAdapter(mDynamicReviewRVAdapter);
                mTvLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.setClass(mContext, DynamicDetailActivity.class);
                        bundle.putString("id", mDataBean.getId() + "");
                        bundle.putString("content", mDataBean.getContent());
                        bundle.putString("name", mDataBean.getUser_name());
                        bundle.putInt("type", 1);
                        bundle.putString("time", mDataBean.getCreated_at());
                        bundle.putInt("is_self", mDataBean.getIs_self());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                mLlReview.setVisibility(View.GONE);
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
            mTvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.like(dataBean.getId() + "", new DynamicPresenter.CallBack4() {
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
        @Bind(R.id.tv_reward)
        TextView mTvReward;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.recycler_view)
        RecyclerView mRecyclerView;
        @Bind(R.id.tv_look)
        TextView mTvLook;
        @Bind(R.id.ll_review)
        LinearLayout mLlReview;
        private DynamicListInfo.DataBean mDataBean;
        private ArrayList<String> mCompressImgList;
        private ArrayList<String> mImgList;

        VideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, DynamicDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("compressImgList", mCompressImgList);
                    bundle.putStringArrayList("imageList", mImgList);
                    bundle.putString("id", mDataBean.getId() + "");
                    bundle.putInt("type", 3);
                    bundle.putString("content", mDataBean.getContent());
                    bundle.putString("name", mDataBean.getUser_name());
                    bundle.putString("time", mDataBean.getCreated_at());
                    bundle.putInt("is_self", mDataBean.getIs_self());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
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
            mRlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("url", mImgList.get(0));
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            mDataBean = dataBean;
            mCompressImgList = (ArrayList<String>) dataBean.getKey_compress_urls();
            mImgList = (ArrayList<String>) dataBean.getKey_urls();
            if (dataBean.getIs_self() == 1) {
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(dataBean.getId() + "");
                    }
                });
                mTvReward.setVisibility(View.GONE);
            } else {
                mTvReward.setVisibility(View.VISIBLE);
                mIvDelete.setVisibility(View.GONE);
            }
            if (!dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mTouxiang);
            }
            if (dataBean.getReviewList().size() != 0) {
                mLlReview.setVisibility(View.VISIBLE);
                if (dataBean.getReviewList().size() > 5) {
                    mTvLook.setVisibility(View.VISIBLE);
                } else {
                    mTvLook.setVisibility(View.GONE);
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mDynamicReviewRVAdapter = new DynamicReviewRVAdapter(dataBean.getReviewList(), mContext);
                mRecyclerView.setAdapter(mDynamicReviewRVAdapter);
                mTvLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.setClass(mContext, DynamicDetailActivity.class);
                        bundle.putStringArrayList("compressImgList", mCompressImgList);
                        bundle.putStringArrayList("imageList", mImgList);
                        bundle.putString("id", mDataBean.getId() + "");
                        bundle.putInt("type", 3);
                        bundle.putString("content", mDataBean.getContent());
                        bundle.putString("name", mDataBean.getUser_name());
                        bundle.putString("time", mDataBean.getCreated_at());
                        bundle.putInt("is_self", mDataBean.getIs_self());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                mLlReview.setVisibility(View.GONE);
            }
            Glide.with(mContext).load((dataBean.getKey_compress_urls()).get(0)).into(mIvVideo);
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
            mTvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.like(dataBean.getId() + "", new DynamicPresenter.CallBack4() {
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
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.ngl_images)
        NineGridImageView mNglImages;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.ll_review)
        LinearLayout mLlReview;
        @Bind(R.id.recycler_view)
        RecyclerView mRecyvlerView;
        @Bind(R.id.tv_look)
        TextView mTvLook;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.tv_reward)
        TextView mTvReward;
        private ArrayList<ThumbViewInfo> mThumbViewInfoList = new ArrayList<>();
        private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String s) {
                Glide.with(context).load(s).apply(new RequestOptions().placeholder(R.mipmap.ic_empty_photo)).into(imageView);
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, DynamicDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("compressImgList", mCompressImgList);
                    bundle.putStringArrayList("imageList", mImgList);
                    bundle.putString("id", mDataBean.getId() + "");
                    bundle.putInt("type", 2);
                    bundle.putString("content", mDataBean.getContent());
                    bundle.putString("name", mDataBean.getUser_name());
                    bundle.putString("time", mDataBean.getCreated_at());
                    bundle.putInt("is_self", mDataBean.getIs_self());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            mNglImages.setAdapter(mAdapter);
            mNglImages.setItemImageClickListener(new ItemImageClickListener<String>() {
                @Override
                public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
                    Intent intent = new Intent(mContext, PreviewImgActivity.class);
                    intent.putExtra("index", index);
                    intent.putStringArrayListExtra("imgList", mCompressImgList);
                    context.startActivity(intent);

                    /*computeBoundsBackward(list);//组成数据
                    GPreviewBuilder.from((Activity) context)
                            .setData(mThumbViewInfoList)
                            .setCurrentIndex(index)
                            .setType(GPreviewBuilder.IndicatorType.Dot)
                            .start();//启动*/

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
        }

        private void computeBoundsBackward(List<String> list) {
            ThumbViewInfo item;
            mThumbViewInfoList.clear();
            for (int i = 0; i < mNglImages.getChildCount(); i++) {
                View itemView = mNglImages.getChildAt(i);
                Rect bounds = new Rect();
                if (itemView != null) {
                    ImageView thumbView = (ImageView) itemView;
                    thumbView.getGlobalVisibleRect(bounds);
                }
                item = new ThumbViewInfo(list.get(i));
                item.setBounds(bounds);
                mThumbViewInfoList.add(item);
            }
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            mDataBean = dataBean;
            mCompressImgList = (ArrayList<String>) dataBean.getKey_compress_urls();
            mImgList = (ArrayList<String>) dataBean.getKey_urls();
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
                mTvReward.setVisibility(View.GONE);
            } else {
                mTvReward.setVisibility(View.VISIBLE);
                mIvDelete.setVisibility(View.GONE);
            }
            if (dataBean.getReviewList().size() != 0) {
                mLlReview.setVisibility(View.VISIBLE);
                if (dataBean.getReview_count() > 5) {
                    mTvLook.setVisibility(View.VISIBLE);
                } else {
                    mTvLook.setVisibility(View.GONE);
                }
                mRecyvlerView.setLayoutManager(new LinearLayoutManager(mContext));
                mDynamicReviewRVAdapter = new DynamicReviewRVAdapter(dataBean.getReviewList(), mContext);
                mRecyvlerView.setAdapter(mDynamicReviewRVAdapter);
                mTvLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, DynamicDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("compressImgList", mCompressImgList);
                        bundle.putStringArrayList("imageList", mImgList);
                        bundle.putInt("type", 2);
                        bundle.putString("id", mDataBean.getId() + "");
                        bundle.putString("content", mDataBean.getContent());
                        bundle.putString("name", mDataBean.getUser_name());
                        bundle.putString("time", mDataBean.getCreated_at());
                        bundle.putInt("is_self", mDataBean.getIs_self());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                mLlReview.setVisibility(View.GONE);
            }
            mTvReward.setText(dataBean.getRewardCount() + "");
            mTvTime.setText(dataBean.getCreated_at());
            mTvName.setText(dataBean.getUser_name());
            mTvContent.setText(dataBean.getContent());
            mTvPinglun.setText(dataBean.getReview_count() + "");
            mTvZan.setText(dataBean.getLike_count() + "");
            if (dataBean.getIs_like() == 1) {
                mTvZan.setSelected(true);
            } else {
                mTvZan.setSelected(false);
            }
            mTvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.like(dataBean.getId() + "", new DynamicPresenter.CallBack4() {
                        @Override
                        public void send(LikeInfo data) {
                            mTvZan.setText(data.getData().getLikeCounts() + "");
                            dataBean.setLike_count(data.getData().getLikeCounts());
                            if (data.getData().getStatus() == 1) {
                                mTvZan.setSelected(true);
                                dataBean.setIs_like(1);
                            } else {
                                mTvZan.setSelected(false);
                                dataBean.setIs_like(0);
                            }
                        }
                    });
                }
            });
        }
    }

    private void showDialog(final String id) {
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
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_zan)
        TextView mTvZan;
        @Bind(R.id.ll_dynamic_content)
        LinearLayout mLlDynamicContent;
        @Bind(R.id.recycler_view)
        RecyclerView mRecyclerView;
        @Bind(R.id.tv_look)
        TextView mTvLook;
        @Bind(R.id.ll_review)
        LinearLayout mLlReview;
        private DynamicListInfo.DataBean mDataBean;
        private String mGuessPw;
        private int mGuessId;
        private int mPeriod_aty;
        @Bind(R.id.tv_reward)
        TextView mTvReward;

        GuessHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.setClass(mContext, DynamicDetailActivity.class);
                    bundle.putString("id", mDataBean.getId() + "");
                    bundle.putString("content", mDataBean.getContent());
                    bundle.putInt("type", 4);
                    bundle.putString("name", mDataBean.getUser_name());
                    bundle.putString("time", mDataBean.getCreated_at());
                    bundle.putInt("is_self", mDataBean.getIs_self());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            mCvGuess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGuessPw != null) {
                        showPWDialog(mGuessPw, mGuessId, mPeriod_aty);
                    } else {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", mGuessId);
                        intent.putExtra("period_qty", mPeriod_aty);
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
        }

        public void setData(final DynamicListInfo.DataBean dataBean) {
            mDataBean = dataBean;
            if (dataBean.getIs_self() == 1) {
                mIvDelete.setVisibility(View.VISIBLE);
                mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(dataBean.getId() + "");
                    }
                });
                mTvReward.setVisibility(View.GONE);
            } else {
                mTvReward.setVisibility(View.VISIBLE);
                mIvDelete.setVisibility(View.GONE);
            }
            if (!dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            }
            if (dataBean.getReviewList().size() != 0) {
                mLlReview.setVisibility(View.VISIBLE);
                if (dataBean.getReviewList().size() > 5) {
                    mTvLook.setVisibility(View.VISIBLE);
                } else {
                    mTvLook.setVisibility(View.GONE);
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mDynamicReviewRVAdapter = new DynamicReviewRVAdapter(dataBean.getReviewList(), mContext);
                mRecyclerView.setAdapter(mDynamicReviewRVAdapter);
                mTvLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.setClass(mContext, DynamicDetailActivity.class);
                        bundle.putString("id", mDataBean.getId() + "");
                        bundle.putString("content", mDataBean.getContent());
                        bundle.putString("name", mDataBean.getUser_name());
                        bundle.putString("time", mDataBean.getCreated_at());
                        bundle.putInt("type", 4);
                        bundle.putInt("is_self", mDataBean.getIs_self());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                mLlReview.setVisibility(View.GONE);
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
            mTvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.like(dataBean.getId() + "", new DynamicPresenter.CallBack4() {
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
        }
    }

    private void showPWDialog(final String guessPw, final int guessId, final int period_aty) {
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
