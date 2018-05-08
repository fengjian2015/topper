package com.bclould.tocotalk.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bclould.tocotalk.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/8.
 */

public class NewsEditActivity extends AppCompatActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.btn_selector_img)
    Button mBtnSelectorImg;
    @Bind(R.id.btn_publish)
    Button mBtnPublish;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.et_new_title)
    EditText mEtNewTitle;
    @Bind(R.id.et_new_content)
    EditText mEtNewContent;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_edit);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bark, R.id.btn_selector_img, R.id.btn_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_selector_img:
                break;
            case R.id.btn_publish:
                break;
        }
    }
}
