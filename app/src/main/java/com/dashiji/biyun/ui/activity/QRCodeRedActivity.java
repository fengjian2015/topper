package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/23.
 */

public class QRCodeRedActivity extends AppCompatActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_redpacket_record)
    TextView mTvRedpacketRecord;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_qr_code)
    ImageView mTvQrCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_red);
        getWindow().setStatusBarColor(getColor(R.color.redpacket3));
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        String code = intent.getStringExtra("code");
        boolean type = intent.getBooleanExtra("type", false);
        if(type){
            mTvRedpacketRecord.setVisibility(View.VISIBLE);
        }else {
            mTvRedpacketRecord.setVisibility(View.GONE);
        }
        Bitmap bitmap = UtilTool.createQRImage(code);
        mTvQrCode.setImageBitmap(bitmap);
    }

    @OnClick({R.id.bark, R.id.tv_redpacket_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_redpacket_record:
                startActivity(new Intent(this, RedPacketRecordActivity.class));
                break;
        }
    }
}
