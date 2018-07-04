package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.ui.fragment.ImageViewFragment;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/25.
 */

public class SearchImageRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private ArrayList<String> imageList;
    private float width;
    private Handler handler;
    public SearchImageRVAdapter(Context context, ArrayList<String> imageList, float width, Handler handler) {
        this.imageList=imageList;
        mContext = context;
        this.width=width;
        this.handler=handler;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_image, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.setData(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView image;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
        public void setData(final String url){
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) image.getLayoutParams();
            params.height= (int) width;
            image.setLayoutParams(params);
            Glide.with(mContext).load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).apply(requestOptions).into(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(){
                        @Override
                        public void run() {
                            String content= getImgPathFromCache(url);
                            Message message=new Message();
                            message.what=2;
                            message.obj=content;
                            handler.sendMessage(message);
                        }
                    }.start();
                }
            });
        }


        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.image_placeholder)
                .error(R.mipmap.image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
    }

    /**
     * 获取glide之前缓存过的图片地址
     *
     * @param url 网络图片的地址
     * @param width 图片宽
     * @param height 图片高
     * @return
     */
    private String getImgPathFromCache(String url) {
        FutureTarget<File> future = Glide.with(mContext)
                .load(url)
                .downloadOnly(400, 400);
        try {
            File cacheFile = future.get();
            String absolutePath = cacheFile.getAbsolutePath();
            UtilTool.Log("fengjian","圖片地址："+absolutePath);
            return absolutePath;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
