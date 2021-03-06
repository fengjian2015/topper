package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bclould.tea.R;
import com.bclould.tea.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */

public class NodePayDialog extends Dialog {


    @Bind(R.id.iv_image)
    ImageView mIvImage;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_content)
    TextView mTvContent;

    public NodePayDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    public NodePayDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_node_pay);
        ButterKnife.bind(this);
    }

    public void setTvTitle(String title) {
        if (!StringUtils.isEmpty(title)) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }
    }

    public void setTvContent(String content) {
        if (!StringUtils.isEmpty(content)) {
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
        }
    }

    public void setIvImage(int type){
        mIvImage.setImageResource(type);
    }

}
