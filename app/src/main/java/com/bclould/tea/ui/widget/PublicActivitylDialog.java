package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.ui.activity.HTMLActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */

public class PublicActivitylDialog extends Dialog {


    @Bind(R.id.iv_gojuanz)
    ImageView mIvGojuanz;
    @Bind(R.id.iv_cancel)
    ImageView mIvCancel;
    private OnClickListener onClickListener;

    public PublicActivitylDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    public PublicActivitylDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity_public);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mIvGojuanz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent intent = new Intent(getContext(), HTMLActivity.class);
                intent.putExtra("html5Url", "http://fitoex.com:33324/index");
                getContext().startActivity(intent);
//                if (onClickListener != null) {
//                    onClickListener.onClick();
//                }
            }
        });
        mIvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
