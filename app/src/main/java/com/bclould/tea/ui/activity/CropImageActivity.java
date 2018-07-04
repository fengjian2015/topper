package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import com.isseiaoki.simplecropview.CropImageView;
import com.umeng.commonsdk.debug.E;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CropImageActivity extends BaseActivity {

    @Bind(R.id.CropImageView)
    CropImageView mCropImageView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);
        initIntent();
        init();
    }

    private void init() {
        Bitmap bmp = BitmapFactory.decodeFile(url);
        mCropImageView.setImageBitmap(bmp);
    }

    private void initIntent() {
        url = getIntent().getStringExtra("url");
    }


    @OnClick({R.id.bark, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_ok:
                //完成
                try {
                    final File newFile = new File(Constants.PUBLICDIR + System.currentTimeMillis()+".jpg");
                    Bitmap bitmap = mCropImageView.getCroppedBitmap();
                    UtilTool.comp(bitmap, newFile);
                    Intent intent=new Intent(CropImageActivity.this,ConversationGroupDetailsActivity.class);
                    intent.putExtra("path",newFile.getAbsolutePath());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }
}
