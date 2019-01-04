package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import com.andy6804tw.zoomimageviewlibrary.ZoomImageView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.utils.QRDiscernUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LargerImageActivity extends BaseActivity {
    @Bind(R.id.image)
    ZoomImageView mImage;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        setContentView(R.layout.activity_larger_image);
        ButterKnife.bind(this);
        initgetIntent();
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        Glide.with(this).load(url).apply(new RequestOptions().override(width,width).placeholder(R.mipmap.img_nfriend_headshot1)).into(mImage);
        mImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new QRDiscernUtil(LargerImageActivity.this).discernQR(url);
                return false;
            }
        });
    }

    @OnClick({R.id.image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image:
                finish();
                break;
        }
    }

    private void initgetIntent() {
        url = getIntent().getStringExtra("url");
    }
}
