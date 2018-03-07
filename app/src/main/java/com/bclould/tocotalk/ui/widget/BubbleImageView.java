package com.bclould.tocotalk.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Created by GA on 2018/3/6.
 */

public class BubbleImageView extends android.support.v7.widget.AppCompatImageView {
    private Context context;
    private Bitmap iconBitmap;
    private int res;
    private static final int OK_INT = 0x0001;
    private static final int ERROR_INT = 0x0000;
    @SuppressLint("HandlerLeak")
    private Handler bitmapHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case OK_INT:
                    Bitmap bitmap_bg = BitmapFactory.decodeResource(getResources(),
                            res);
                    final Bitmap bp = getRoundCornerImage(bitmap_bg, iconBitmap);
                    setImageBitmap(bp);
                    break;
                case ERROR_INT:
                    break;
            }
        }

        ;
    };

    public BubbleImageView(Context context) {
        super(context);
        this.context = context;
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void load(String url, int res, int placeHolderPic) {
        this.setImageResource(placeHolderPic);
        this.res = res;
        Glide.with(context).load(url).into(new SimpleTarget<Drawable>() {

            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (resource != null) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                    iconBitmap = bitmapDrawable.getBitmap();
                    bitmapHandler.sendEmptyMessage(OK_INT);
                }
            }

        });
    }

    public void setLocalImageBitmap(Bitmap bm, int res) {
        // TODO Auto-generated method stub
        Bitmap bitmap_bg = BitmapFactory.decodeResource(getResources(), res);
        final Bitmap bp = getRoundCornerImage(bitmap_bg, bm);
        setImageBitmap(bp);
    }

    public Bitmap getRoundCornerImage(Bitmap bitmap_bg, Bitmap bitmap_in) {
        int width = bitmap_in.getWidth();
        int height = bitmap_in.getHeight();
        if (height != 0) {
            double scale = (width * 1.00) / height;
            if (width >= height) {
                width = getBitmapWidth();
                height = (int) (width / scale);
            } else {
                height = getBitmapHeight();
                width = (int) (height * scale);
            }
        } else {
            width = 100;
            height = 100;
        }
        Bitmap roundConcerImage = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        Rect rectF = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg,
                bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap_in, rectF, rect, paint);
        return roundConcerImage;
    }

    // 获取屏幕的宽度
    @SuppressWarnings("deprecation")
    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    // 获取屏幕的高度
    @SuppressWarnings("deprecation")
    public int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public int getBitmapWidth() {
        return getScreenWidth(context) / 3;
    }

    public int getBitmapHeight() {
        return getScreenHeight(context) / 4;
    }
}
