package com.bclould.tocotalk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.bclould.tocotalk.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

/**
 * Created by GA on 2018/5/14.
 */

public class GlideImgLoader implements IZoomMediaLoader {
    private static GlideImgLoader instance = null;

    public static GlideImgLoader getInstance() {
        if (instance == null) {
            instance = new GlideImgLoader();
        }
        return instance;
    }

    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, final @NonNull MySimpleTarget<Bitmap> simpleTarget) {

        RequestOptions requestOptions = new RequestOptions()
                .error(R.mipmap.ic_empty_photo)
                .centerCrop()
                .placeholder(R.mipmap.ic_empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(path)
                .apply(requestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        BitmapDrawable bd = (BitmapDrawable) resource;
                        simpleTarget.onResourceReady(bd.getBitmap());
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        simpleTarget.onLoadStarted();
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        simpleTarget.onLoadFailed(errorDrawable);
                    }
                });
    }

    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context).onStop();
    }

    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
    }

}
