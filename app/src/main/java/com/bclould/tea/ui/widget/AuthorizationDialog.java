package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */

public class AuthorizationDialog extends Dialog {

    @Bind(R.id.iv_image)
    ImageView mIvImage;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.btn_cancel)
    Button mBtnCancel;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    private OnClickListener onClickListener;
    private Context mActivity;

    public AuthorizationDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mActivity=context;
    }

    public AuthorizationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mActivity=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_authorization);
        ButterKnife.bind(this);
        init();
    }

    public void setIvImage(String url){
        UtilTool.setCircleImg(mActivity,url,mIvImage);
    }

    public void setTvTitle(String title) {
        if (!StringUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }
    }

    public void setTvContent(String content) {
        if (!StringUtils.isEmpty(content)) {
            mTvContent.setText(content);
        }
    }

    public void setBtnConfirm(String confirm) {
        if (!StringUtils.isEmpty(confirm)) {

        }
    }

    private void init() {
        //授权
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });

        //拒绝
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onCancel();
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick();

        void onCancel();
    }
}
