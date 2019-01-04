package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.ui.activity.SelectConversationActivity;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */

public class ImageDialog extends Dialog {
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.btn_cancel)
    Button mBtnCancel;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    private OnClickListener onClickListener;
    private Context mContext;
    private String filePath;

    public ImageDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mContext=context;
    }

    public ImageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image);
        ButterKnife.bind(this);
        init();
    }


    public void setBtnConfirm(String confirm) {
        if (!StringUtils.isEmpty(confirm)) {
            mBtnConfirm.setText(confirm);
        }
    }

    public void setImage(final Object url){
        Glide.with(mContext).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Thread() {
                    @Override
                    public void run() {
                        filePath = UtilTool.getImgPathFromCache(url, mContext);
                    }
                }.start();
                return false;
            }
        }).into(mImage);
    }

    private void init() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    if(StringUtils.isEmpty(filePath)){
                        return;
                    }
                    onClickListener.onClick(filePath);
                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(String path);
    }
}
