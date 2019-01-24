package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.ui.activity.HTMLActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */

public class NewYearDialog extends Dialog {


    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.bt_ok)
    Button mBtOk;
    private OnClickListener onClickListener;

    public NewYearDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    public NewYearDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_year);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mBtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });
    }

    public void setData(String title,String content){
        mTvTitle.setText(title);
        mTvContent.setText(content);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick();
    }
}
