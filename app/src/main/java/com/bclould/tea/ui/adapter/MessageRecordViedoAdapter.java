package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.SerMap;
import com.bclould.tea.ui.activity.ImageViewActivity;
import com.bclould.tea.ui.activity.VideoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclould.tea.ui.activity.ConversationRecordFindActivity.IMAGE_MSG;
import static com.bclould.tea.ui.activity.ConversationRecordFindActivity.VIDEO_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;

/**
 * Created by GIjia on 2018/5/15.
 */

public class MessageRecordViedoAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MessageInfo> messageInfoList;
    private int type;
    private DBManager mMdb;

    public MessageRecordViedoAdapter(Context context, List<MessageInfo> messageInfoList, DBManager mMdb) {
        this.context = context;
        this.messageInfoList = messageInfoList;
        this.mMdb = mMdb;
    }

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (type == IMAGE_MSG) {
            return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_record_video_item, null));
        } else if (type == VIDEO_MSG) {
            return new ViderViewHolder(LayoutInflater.from(context).inflate(R.layout.message_record_video_item, null));
        }
        return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_record_video_item, null));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == IMAGE_MSG) {
            setImageData(holder,position);
        } else if (itemViewType == VIDEO_MSG) {
            setVideoData(holder,position);
        }
    }

    private void setVideoData(final RecyclerView.ViewHolder holder, final int position) {
        if(messageInfoList.get(position).getVoice().startsWith("http")) {
            Glide.with(context).load(messageInfoList.get(position).getVoice()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ((ViderViewHolder) holder).iv.setImageDrawable(resource);
                    return false;
                }
            }).into( ((ViderViewHolder) holder).iv);
        }else {
            ((ViderViewHolder) holder).iv.setImageBitmap(BitmapFactory.decodeFile(messageInfoList.get(position).getVoice()));
        }
        ((ViderViewHolder) holder).iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("url", messageInfoList.get(position).getMessage());
                intent.putExtra("compressUrl",messageInfoList.get(position).getVoice());
                context.startActivity(intent);
            }
        });
    }

    public void setImageData(final RecyclerView.ViewHolder holder, final int positions){
        upDateImage();
        if(messageInfoList.get(positions).getVoice().startsWith("http")) {
            Glide.with(context).load(messageInfoList.get(positions).getVoice()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ((ImageViewHolder) holder).iv.setImageDrawable(resource);
                    return false;
                }
            }).into(((ImageViewHolder) holder).iv);
        }else {
            ((ImageViewHolder) holder).iv.setImageBitmap(BitmapFactory.decodeFile(messageInfoList.get(positions).getVoice()));
        }
        ((ImageViewHolder) holder).iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putStringArrayListExtra("images", mImageList);
                intent.putIntegerArrayListExtra("msgId", mIntegerList);
                SerMap serMap = new SerMap();
                serMap.setMap(mImageMap);
                intent.putExtra("imgMap", serMap);
                int position = positions;
                for (int i = 0; i < mImageList.size(); i++) {
                    if (messageInfoList.get(position).getMessage().equals(mImageList.get(i))) {
                        position = i;
                    }
                }
                intent.putExtra("clickedIndex", position);
                context.startActivity(intent);
            }
        });
    }
    ArrayList<String> mImageList = new ArrayList<>();
    HashMap<String, String> mImageMap = new HashMap<>();
    ArrayList<Integer> mIntegerList = new ArrayList<>();

    public void upDateImage() {
        mImageList.clear();
        mImageMap.clear();
        mIntegerList.clear();
        for (final MessageInfo info : messageInfoList) {
            if (info.getMsgType() == TO_IMG_MSG) {
                mImageList.add(info.getMessage());
                mImageMap.put(info.getMessage(), "");
                mIntegerList.add(info.getId());
            } else if (info.getMsgType() == FROM_IMG_MSG) {
                if (info.getImageType() != 0) {
                    mImageList.add(info.getMessage());
                    mImageMap.put(info.getMessage(), "");
                    mIntegerList.add(info.getId());
                } else {
                    mImageList.add(info.getVoice());
                    mImageMap.put(info.getVoice(), info.getMessage());
                    mIntegerList.add(info.getId());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageInfoList.size();
    }

    /**
     * 視頻
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;
        public ImageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 視頻
     */
    public class ViderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;
        public ViderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
