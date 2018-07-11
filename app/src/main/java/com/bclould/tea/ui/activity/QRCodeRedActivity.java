package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.utils.UtilTool;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;

/**
 * Created by GA on 2018/1/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class QRCodeRedActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_redpacket_record)
    TextView mTvRedpacketRecord;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_qr_code)
    ImageView mTvQrCode;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.btn_save_qr)
    Button mBtnSaveQr;
    @Bind(R.id.rl_red)
    RelativeLayout mRlRed;
    private MessageInfo mMessageInfo = new MessageInfo();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_red);
        getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket4));
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        String code = intent.getStringExtra("code");
        boolean type = intent.getBooleanExtra("type", false);
        if (type) {
            mTvRedpacketRecord.setVisibility(View.VISIBLE);
        } else {
            mTvRedpacketRecord.setVisibility(View.GONE);
        }
        Bitmap bitmap = UtilTool.createQRImage(code);
        mTvQrCode.setImageBitmap(bitmap);
    }

    @OnClick({R.id.btn_save_qr, R.id.bark, R.id.tv_redpacket_record, R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_share:
                goShare();
                break;
            case R.id.btn_save_qr:
                if (UtilTool.saveBitmap(mRlRed, this, true) != null) {
                    Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_redpacket_record:
                startActivity(new Intent(this, RedPacketRecordActivity.class));
                break;
        }
    }

    private void goShare() {
        String path = UtilTool.saveBitmap(mRlRed, this, false);
        if (path != null) {
            mMessageInfo.setVoice(path);
            Intent intent = new Intent(this, SelectConversationActivity.class);
            intent.putExtra("type", 2);
            intent.putExtra("msgType", TO_IMG_MSG);
            intent.putExtra("messageInfo", mMessageInfo);
            startActivity(intent);
        }
    }
}
