package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
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
//        Bitmap bmp = BitmapFactory.decodeFile(url);
//        mCropImageView.setImageBitmap(bmp);
        adjustImage(url);
    }


    private void adjustImage(String absolutePath) {
        Bitmap bm;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        bm = BitmapFactory.decodeFile(absolutePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }

        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(absolutePath, opt);

        // 用imageview显示出bitmap
        mCropImageView.setImageBitmap(bm);
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
                commit();
                break;
        }
    }

    private synchronized void commit(){
        try {
            final File newFile = new File(Constants.PUBLICDIR + System.currentTimeMillis()+".jpg");
            Bitmap bitmap = mCropImageView.getCroppedBitmap();
            UtilTool.comp(bitmap, newFile);
            Intent intent=new Intent(CropImageActivity.this,ConversationGroupDetailsActivity.class);
            intent.putExtra("path",newFile.getAbsolutePath());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
        finish();
    }

}
