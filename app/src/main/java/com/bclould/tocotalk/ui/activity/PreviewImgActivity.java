package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.fragment.PreviewImgFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/15.
 */

public class PreviewImgActivity extends FragmentActivity {
    @Bind(R.id.images_vp)
    ViewPager mImagesVp;
    @Bind(R.id.tv_number)
    TextView mTvNumber;
    private int mIndex;
    private ArrayList<String> mImgList;
    private List<Fragment> mFragmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_view);
        ButterKnife.bind(this);
        mTvNumber.setVisibility(View.VISIBLE);
        initIntent();
        init();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mImgList = intent.getStringArrayListExtra("imgList");
        mIndex = intent.getIntExtra("index", 0);
        mTvNumber.setText(mIndex + 1 + "/" + mImgList.size());
    }

    private void init() {
        mFragmentList = new ArrayList<>();
        for (int i = 0; i < mImgList.size(); i++) {
            PreviewImgFragment previewImgFragment = new PreviewImgFragment(mImgList.get(i));
            mFragmentList.add(previewImgFragment);
        }
        // 类似缓存
        mImagesVp.setOffscreenPageLimit(mImgList.size());
        mImagesVp.setAdapter(new ImageViewFPAdapter(getSupportFragmentManager()));
        mImagesVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                mTvNumber.setText(index + 1 + "/" + mImgList.size());
                mIndex = index;
                mFragmentList.get(mIndex).onPause(); // 调用切换前Fargment的onPause()
                if (mFragmentList.get(index).isAdded()) {
                    mFragmentList.get(index).onResume(); // 调用切换后Fargment的onResume()
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mImagesVp.setCurrentItem(mIndex);
    }

    class ImageViewFPAdapter extends FragmentPagerAdapter {
        protected FragmentManager fm;


        public ImageViewFPAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:  //当屏幕检测到第一个触点按下之后就会触发到这个事件。
                finish();
                break;
        }


        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        if (mFragmentList.size() > 0) {
            for (Fragment fragment : mFragmentList)
                fragment.onDestroy();
        }
        super.onDestroy();
    }
}
