package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */

public class ConfirmDialog extends Dialog {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;

    private OnClickListener onClickListener;

    public ConfirmDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_otr);
        ButterKnife.bind(this);
        init();
    }

    public void setTvTitle(String title){
        if(!StringUtils.isEmpty(title)){
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }
    }

    public void setTvContent(String content){
        if(!StringUtils.isEmpty(content)) {
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
        }
    }

    public void setBtnConfirm(String confirm){
        if(!StringUtils.isEmpty(confirm)){
            mBtnConfirm.setText(confirm);
        }
    }

    private void init() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(onClickListener!=null){
                    onClickListener.onClick();
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public interface OnClickListener{
        void onClick();
    }
}
