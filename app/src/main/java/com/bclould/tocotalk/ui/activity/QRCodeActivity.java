package com.bclould.tocotalk.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/9.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class QRCodeActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.more)
    ImageView mMore;
    @Bind(R.id.qr_code_iv)
    ImageView mQrCodeIv;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.rl_qr)
    RelativeLayout mRlQr;
    @Bind(R.id.btn_save_qr)
    Button mBtnSaveQr;

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
            List<UserInfo> userInfos = mgr.queryUser(UtilTool.getMyUser());
            mTouxiang.setImageBitmap(BitmapFactory.decodeFile(userInfos.get(0).getPath()));
            Bitmap bitmap = UtilTool.createQRImage(UtilTool.base64PetToJson(Constants.BUSINESSCARD, "name", UtilTool.getMyUser(), "名片"));
            mQrCodeIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.bark, R.id.more, R.id.btn_save_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.btn_save_qr:
                if(UtilTool.saveBitmap(mRlQr)){
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.more:
                break;
        }
    }
}
