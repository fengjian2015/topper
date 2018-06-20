package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.SerMap;
import com.bclould.tea.ui.fragment.ImageViewFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GA on 2018/3/7.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ImageViewActivity extends FragmentActivity {
    private ArrayList<String> imageList;
    private List<Fragment> fragList;
    private ViewPager imageVp;
    private int currentPage;
    private HashMap<String, String> mImageMap;
    private DBManager mMgr;
    private ArrayList<Integer> mIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMgr = new DBManager(this);
        MyApp.getInstance().addActivity(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("images")) {
                imageList = bundle.getStringArrayList("images");
            }
            if (bundle.containsKey("clickedIndex")) {
                currentPage = bundle.getInt("clickedIndex");
            }
            if (bundle.containsKey("imgMap")) {
                SerMap serMap = (SerMap) bundle.getSerializable("imgMap");
                mImageMap = serMap.getMap();
            }
            if (bundle.containsKey("msgId")) {
                mIdList = bundle.getIntegerArrayList("msgId");
            }
        }
        setContentView(R.layout.activity_images_view);
        findView();
        init();
    }

    private void init() {
        fragList = new ArrayList<Fragment>();
        for (int i = 0; i < imageList.size(); i++) {
            ImageViewFragment imageVF = new ImageViewFragment();
            imageVF.setImageUrl(imageList.get(i), mImageMap.get(imageList.get(i)), mMgr, mIdList.get(i));
            fragList.add(imageVF);
        }
        // 类似缓存
        imageVp.setOffscreenPageLimit(imageList.size());
        imageVp.setAdapter(new ImageViewFPAdapter(getSupportFragmentManager()));
        imageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                currentPage = index;
                fragList.get(currentPage).onPause(); // 调用切换前Fargment的onPause()
                if (fragList.get(index).isAdded()) {
                    fragList.get(index).onResume(); // 调用切换后Fargment的onResume()
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        imageVp.setCurrentItem(currentPage);
    }

    protected void findView() {
        imageVp = (ViewPager) findViewById(R.id.images_vp);
    }

    class ImageViewFPAdapter extends FragmentPagerAdapter {
        protected FragmentManager fm;


        public ImageViewFPAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragList.size();
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
        if (fragList.size() > 0) {
            for (Fragment fragment : fragList)
                fragment.onDestroy();
        }
        super.onDestroy();
    }
}
