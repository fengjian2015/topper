package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class ShareDialog extends Dialog {

    @Bind(R.id.iv_head)
    ImageView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.iv_image)
    ImageView mIvImage;
    @Bind(R.id.et_text)
    EditText mEtText;
    @Bind(R.id.btn_cancel)
    Button mBtnCancel;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    private OnClickListener onClickListener;
    private Context mContext;


    public ShareDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.mContext=context;
    }

    public ShareDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        ButterKnife.bind(this);
        init();
    }



    public void setTvName(String name){
        mTvName.setText(name);
    }

    public void setIvHead(String url){
        UtilTool.setCircleImg(mContext,url,mIvHead);
    }

    public void setIvImage(Object url){
        if(url==null)return;
        mIvImage.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(url).apply(requestOptions).into(mIvImage);
    }

    RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.mipmap.image_placeholder)
            .error(R.mipmap.image_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private void init() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onOkClick(mEtText.getText().toString());
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
        void onOkClick(String content);
    }
}
