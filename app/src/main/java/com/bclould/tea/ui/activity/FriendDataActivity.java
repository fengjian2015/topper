package com.bclould.tea.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.adapter.FriendDataVPAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StatusBarCompat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/27.
 */

public class FriendDataActivity extends AppCompatActivity {

    private static final String ISEDIT = "is_edit";
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.gender)
    ImageView mGender;
    @Bind(R.id.id)
    TextView mId;
    @Bind(R.id.attention)
    TextView mAttention;
    @Bind(R.id.guanzhu_count)
    TextView mGuanzhuCount;
    @Bind(R.id.fans)
    TextView mFans;
    @Bind(R.id.qianming)
    TextView mQianming;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.send_message)
    Button mSendMessage;
    @Bind(R.id.menu)
    LinearLayout mMenu;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.xx2)
    TextView mXx2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setImmersionStateMode(this);
        setContentView(R.layout.activity_friend_data);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.getInstance().removeActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    //初始化界面
    private void initInterface() {
        //拿到储存的状态
        isClick = MySharedPreferences.getInstance().getBoolean(ISEDIT);
        //设置状态
        setAttention(isClick);
        //一开始选中0
        setSelector(0);
        //一开始选中0
        mViewPager.setCurrentItem(0);
        //初始化顶部菜单
        initTopMenu();
        //初始化ViewPage
        initViewPager();
    }

    //初始化顶部菜单栏
    private void initTopMenu() {

        for (int i = 0; i < mMenu.getChildCount(); i++) {

            final View childAt = mMenu.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mMenu.indexOfChild(childAt);

                    setSelector(index);

                    mViewPager.setCurrentItem(index);

                }
            });
        }
    }

    //菜单选项选中处理
    private void setSelector(int index) {

        for (int i = 0; i < mMenu.getChildCount(); i++) {

            if (i == index) {

                mMenu.getChildAt(i).setSelected(true);

                switch (index) {

                    case 0:

                        mXx.setVisibility(View.VISIBLE);

                        mXx2.setVisibility(View.INVISIBLE);


                        break;
                    case 1:

                        mXx.setVisibility(View.INVISIBLE);

                        mXx2.setVisibility(View.VISIBLE);


                        break;

                }

            } else {

                mMenu.getChildAt(i).setSelected(false);

            }
        }
    }

    //初始化ViewPager
    private void initViewPager() {

        mViewPager.setAdapter(new FriendDataVPAdapter(getSupportFragmentManager()));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setSelector(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    boolean isClick = false;

    @OnClick({R.id.attention, R.id.send_message, R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:

                isClick = !isClick;

                setAttention(isClick);

                break;
            case R.id.send_message:

                break;
            case R.id.bark:
                finish();
                break;
        }
    }

    //设置关注状态
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAttention(boolean isClick) {

        if (isClick) {

            mAttention.setBackground(getDrawable(R.drawable.bg_register_shape));

            mAttention.setText(getString(R.string.already_followed));

            mAttention.setTextColor(Color.GRAY);

        } else {

            mAttention.setBackground(getDrawable(R.drawable.bg_buysell_shape));

            mAttention.setText(getString(R.string.attention));

            Resources resources = getBaseContext().getResources();
            ColorStateList colorStateList = resources.getColorStateList(R.color.blue2);

            mAttention.setTextColor(colorStateList);

        }

        MySharedPreferences.getInstance().setBoolean(ISEDIT, this.isClick);
    }
}
