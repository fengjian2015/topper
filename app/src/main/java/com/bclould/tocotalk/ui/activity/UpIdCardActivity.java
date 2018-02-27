package com.bclould.tocotalk.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

public class UpIdCardActivity extends BaseActivity {

    private static final int ZHENGMIAN = 0;
    private static final int FANMIAN = 1;
    private static final int SHOUCHI = 2;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.help)
    ImageView mHelp;
    @Bind(R.id.zhengmian_iv)
    ImageView mZhengmianIv;
    @Bind(R.id.zhengmian)
    RelativeLayout mZhengmian;
    @Bind(R.id.fanmian_iv)
    ImageView mFanmianIv;
    @Bind(R.id.fanmian)
    RelativeLayout mFanmian;
    @Bind(R.id.shouchi_iv)
    ImageView mShouchiIv;
    @Bind(R.id.shouchi)
    RelativeLayout mShouchi;
    @Bind(R.id.up_check)
    Button mUpCheck;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_idcard);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.help, R.id.zhengmian, R.id.fanmian, R.id.shouchi, R.id.up_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.help:
                break;
            case R.id.zhengmian:

                callPhotoAlbum(ZHENGMIAN);

                break;
            case R.id.fanmian:

                callPhotoAlbum(FANMIAN);

                break;
            case R.id.shouchi:

                callPhotoAlbum(SHOUCHI);

                break;
            case R.id.up_check:


                break;
        }
    }

    //拿到选中的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath, requestCode);
            c.close();
        }
    }

    //把选中的图片设置到ImageView
    private void showImage(String imagePath, int requestCode) {


        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        switch (requestCode) {

            case ZHENGMIAN:

                mZhengmianIv.setImageBitmap(bitmap);

                break;
            case FANMIAN:

                mFanmianIv.setImageBitmap(bitmap);

                break;
            case SHOUCHI:

                mShouchiIv.setImageBitmap(bitmap);

                break;
        }
    }

    //调用系统相册
    private void callPhotoAlbum(int code) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, code);

    }
}
