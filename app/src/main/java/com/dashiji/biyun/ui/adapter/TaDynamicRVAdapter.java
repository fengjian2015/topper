package com.dashiji.biyun.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dashiji.biyun.R;
import com.dashiji.biyun.model.PostInfo;
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

public class TaDynamicRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final ArrayList<PostInfo> mPostList;


    public TaDynamicRVAdapter(Context context, ArrayList<PostInfo> postList) {

        mContext = context;

        mPostList = postList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        RecyclerView.ViewHolder holder = null;

        switch (viewType) {

            case 0:

                view = LayoutInflater.from(mContext).inflate(R.layout.item_music_dynamic, parent, false);

                holder = new MusicHolder(view);

                break;
            case 1:

                view = LayoutInflater.from(mContext).inflate(R.layout.item_text_dynamic, parent, false);

                holder = new TextHolder(view);

                break;
            case 2:

                view = LayoutInflater.from(mContext).inflate(R.layout.item_video_dynamic, parent, false);

                holder = new VideoHolder(view);

                break;
            case 3:

                view = LayoutInflater.from(mContext).inflate(R.layout.item_image_dynamic, parent, false);

                holder = new ImageHolder(view);

                break;

        }


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ImageHolder) {
            ((ImageHolder) holder).setData(position);
        }

    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position % 4 == 0) {

            return 0;

        } else if (position % 4 == 1) {

            return 1;

        } else if (position % 4 == 2) {

            return 2;

        } else {

            return 3;

        }
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
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mContext.startActivity(new Intent(mContext, DynamicDetailActivity.class));

                }
            });*/
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
        @Bind(R.id.pinglun_count)
        TextView mPinglunCount;
        @Bind(R.id.zan_count)
        TextView mZanCount;

        TextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mContext.startActivity(new Intent(mContext, DynamicDetailActivity.class));

                }
            });*/
        }
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
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mContext.startActivity(new Intent(mContext, DynamicDetailActivity.class));

                }
            });*/
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        private NineGridImageView<String> mNglContent;
        private TextView mTvContent;
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
        private ArrayList<String> mImgUrlList;

        public ImageHolder(View view) {
            super(view);
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();

                    intent.setClass(mContext, DynamicDetailActivity.class);

                    intent.putStringArrayListExtra("imageList", mImgUrlList);

                    mContext.startActivity(intent);

                }
            });*/
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            mNglContent = (NineGridImageView<String>) itemView.findViewById(R.id.ngl_images);
            mNglContent.setAdapter(mAdapter);
            mNglContent.setItemImageClickListener(new ItemImageClickListener<String>() {
                @Override
                public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {

                    computeBoundsBackward(list);//组成数据
                    GPreviewBuilder.from((Activity) context)
                            .setData(mThumbViewInfoList)
                            .setCurrentIndex(index)
                            .setType(GPreviewBuilder.IndicatorType.Dot)
                            .start();//启动

                }
            });
        }

        /**
         * 查找信息
         *
         * @param list 图片集合
         */
        private void computeBoundsBackward(List<String> list) {
            ThumbViewInfo item;
            mThumbViewInfoList.clear();
            for (int i = 0; i < mNglContent.getChildCount(); i++) {
                View itemView = mNglContent.getChildAt(i);
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

        public void setData(int position) {

            mImgUrlList = (ArrayList<String>) mPostList.get(position).getImgUrlList();

            mNglContent.setImagesData(mImgUrlList);
        }
    }
}
