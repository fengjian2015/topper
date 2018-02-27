package com.dashiji.biyun.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.history.DBManager;
import com.dashiji.biyun.model.UserInfo;
import com.dashiji.biyun.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/9.
 */

public class QRCodeActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.more)
    ImageView mMore;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.qr_code_iv)
    ImageView mQrCodeIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        init();
    }

    private void init() {
        DBManager mgr = new DBManager(this);
        mName.setText(UtilTool.getMyUser().substring(0, UtilTool.getMyUser().lastIndexOf("@")));
        List<UserInfo> userInfos = mgr.queryUser(UtilTool.getMyUser());
        mTouxiang.setImageBitmap(BitmapFactory.decodeFile(userInfos.get(0).getPath()));
        Bitmap bitmap = UtilTool.createQRImage(UtilTool.base64PetToJson("name", UtilTool.getMyUser(), "名片"));
        mQrCodeIv.setImageBitmap(bitmap);
    }

    @OnClick({R.id.bark, R.id.more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.more:


                break;
        }
    }
}
