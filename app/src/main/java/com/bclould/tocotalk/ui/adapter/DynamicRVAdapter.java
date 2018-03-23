package com.bclould.tocotalk.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.DynamicListInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.DynamicDetailActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.previewlibrary.GPreviewBuilder;
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
    private final Context mContext;
    private final List<DynamicListInfo.DataBean> mDataList;
    private final DBManager mMgr;

    public DynamicRVAdapter(Context context, List<DynamicListInfo.DataBean> dataList, DBManager mgr) {
        mContext = context;
        mDataList = dataList;
        mMgr = mgr;
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
        @Bind(R.id.tv_zan)
        TextView mTvZan;

        TextHolder(View view) {
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
            try {
                mTouxiang.setImageBitmap(getImage(dataBean.getUser_name()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTime.setText(dataBean.getCreated_at());
            mName.setText(dataBean.getUser_name());
            mTextContent.setText(dataBean.getContent());
            mTvPinglun.setText(dataBean.getReview_count() + "");
            mTvZan.setText(dataBean.getLike_count() + "");
        }
    }

    private Bitmap getImage(String user_name) {
        String jid = user_name + "@" + Constants.DOMAINNAME;
        List<UserInfo> userInfos = mMgr.queryUser(jid);
        String path = userInfos.get(0).getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.text_content)
        TextView mTextContent;
        @Bind(R.id.video_view)
        VideoView mVideoView;
        @Bind(R.id.pinglun_count)
        TextView mPinglunCount;
        @Bind(R.id.zan_count)
        TextView mZanCount;

        VideoHolder(View view) {
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

        public ImageHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();

                    intent.setClass(mContext, DynamicDetailActivity.class);

                    intent.putStringArrayListExtra("imageList", mCompressImgList);

                    mContext.startActivity(intent);

                }
            });
            mNglImages.setAdapter(mAdapter);
            mNglImages.setItemImageClickListener(new ItemImageClickListener<String>() {
                @Override
                public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {

                    computeBoundsBackward(mImgList);//组成数据
                    GPreviewBuilder.from((Activity) context)
                            .setData(mThumbViewInfoList)
                            .setCurrentIndex(index)
                            .setType(GPreviewBuilder.IndicatorType.Dot)
                            .start();//启动

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

        public void setData(DynamicListInfo.DataBean dataBean) {
            mCompressImgList = (ArrayList<String>) dataBean.getKey_compress_urls();
            mImgList = (ArrayList<String>) dataBean.getKey_urls();
            mNglImages.setImagesData(mCompressImgList);
            try {
                mIvTouxiang.setImageBitmap(getImage(dataBean.getUser_name()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTvTime.setText(dataBean.getCreated_at());
            mTvName.setText(dataBean.getUser_name());
            mTvContent.setText(dataBean.getContent());
            mTvPinglun.setText(dataBean.getReview_count() + "");
            mTvZan.setText(dataBean.getLike_count() + "");
        }
    }
}
