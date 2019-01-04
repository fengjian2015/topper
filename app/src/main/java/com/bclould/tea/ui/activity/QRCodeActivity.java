package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;

/**
 * Created by GA on 2017/10/9.
 */

public class QRCodeActivity extends BaseActivity {


    @Bind(R.id.qr_code_iv)
    ImageView mQrCodeIv;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.rl_qr)
    RelativeLayout mRlQr;
    @Bind(R.id.btn_save_qr)
    Button mBtnSaveQr;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;
    private MessageInfo mMessageInfo = new MessageInfo();
    private int type = 0;//0自己的二维码  1推荐码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        ButterKnife.bind(this);
        setTitle(getString(R.string.qr_code),getString(R.string.share));
        initIntent();
        init();
    }


    private void init() {
        try {
            DBManager mgr = new DBManager(this);
            String user = getIntent().getStringExtra("user");
            if (user == null) {
                if (type == 0)
                    user = UtilTool.getTocoId();
                else if(type==1)
                    user = UtilTool.getUser();
            }
            UtilTool.getImage(mgr, user, this, mTouxiang);
//            mTouxiang.setImageBitmap(UtilTool.getImage(mgr,user, this));
            Bitmap bitmap = null;
            if (type == 0) {
                bitmap = UtilTool.createQRImage(UtilTool.base64PetToJson(this, Constants.BUSINESSCARD, "name", user, "名片"));
            } else if(type==1){
                bitmap = UtilTool.createQRImage(UtilTool.base64PetToJson(this, Constants.COMMANDUSERNAME, "name", user, "推荐码"));
                mTvDesc.setVisibility(View.GONE);
            }
            mQrCodeIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initIntent() {
        type = getIntent().getIntExtra("type", 0);
        String action = getIntent().getAction();
        if (action != null && action.equals("android.intent.action.qrcode") && WsConnection.getInstance().getOutConnection()) {
            finish();
            startActivity(new Intent(this, InitialActivity.class));
        }
    }

    @OnClick({R.id.bark, R.id.btn_save_qr, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
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
