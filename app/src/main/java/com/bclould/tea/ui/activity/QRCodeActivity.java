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
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;

/**
 * Created by GA on 2017/10/9.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class QRCodeActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.qr_code_iv)
    ImageView mQrCodeIv;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.rl_qr)
    RelativeLayout mRlQr;
    @Bind(R.id.btn_save_qr)
    Button mBtnSaveQr;
    private MessageInfo mMessageInfo = new MessageInfo();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        init();
    }

    private void init() {
        try {
            DBManager mgr = new DBManager(this);
            String user = getIntent().getStringExtra("user");
            UtilTool.getImage(mgr, user, this, mTouxiang);
//            mTouxiang.setImageBitmap(UtilTool.getImage(mgr,user, this));
            Bitmap bitmap = UtilTool.createQRImage(UtilTool.base64PetToJson(this, Constants.BUSINESSCARD, "name", user, "名片"));
            mQrCodeIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.bark, R.id.btn_save_qr, R.id.tv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_share:
                goShare();
                break;
            case R.id.btn_save_qr:
                if (UtilTool.saveBitmap(mRlQr, this, true) != null) {
                    Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void goShare() {
        String path = UtilTool.saveBitmap(mRlQr, this, false);
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
