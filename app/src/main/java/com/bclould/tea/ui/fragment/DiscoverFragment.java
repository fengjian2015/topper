package com.bclould.tea.ui.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.ui.activity.GonggaoManagerActivity;
import com.bclould.tea.ui.activity.MainActivity;
import com.bclould.tea.ui.activity.NewsEditActivity;
import com.bclould.tea.ui.activity.NewsManagerActivity;
import com.bclould.tea.ui.activity.PersonageDynamicActivity;
import com.bclould.tea.ui.activity.PublicshDynamicActivity;
import com.bclould.tea.ui.adapter.CloudMessageVPAdapter;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


@RequiresApi(api = Build.VERSION_CODES.N)
public class DiscoverFragment extends Fragment {
    public static DiscoverFragment instance = null;
    @Bind(R.id.gonggao_xx)
    TextView mGonggaoXx;
    @Bind(R.id.ll_gongao_selector)
    LinearLayout mLlGongaoSelector;
    @Bind(R.id.new_xx)
    TextView mNewXx;
    @Bind(R.id.ll_news_selector)
    LinearLayout mLlNewsSelector;
    @Bind(R.id.dongtai_xx)
    TextView mDongtaiXx;
    @Bind(R.id.ll_dynamic_selector)
    LinearLayout mLlDynamicSelector;
    @Bind(R.id.cloud_circle_menu)
    LinearLayout mCloudCircleMenu;
    @Bind(R.id.iv_gonggao_manager)
    ImageView mIvGonggaoManager;
    @Bind(R.id.ll_gonggao)
    LinearLayout mLlGonggao;
    @Bind(R.id.iv_news_manager)
    ImageView mIvNewsManager;
    @Bind(R.id.iv_news_push)
    ImageView mIvNewsPush;
    @Bind(R.id.ll_news)
    LinearLayout mLlNews;
    @Bind(R.id.rl_push_dynamic_status)
    RelativeLayout mRlPushDynamicStatus;
    @Bind(R.id.iv_push_dynamic)
    ImageView mIvPushDynamic;
    @Bind(R.id.iv_my_dynamic)
    ImageView mIvMyDynamic;
    @Bind(R.id.ll_dynamic)
    RelativeLayout mLlDynamic;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.cloud_circle_vp)
    ViewPager mCloudCircleVp;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    private MainActivity.MyOnTouchListener mTouchListener;


    public static DiscoverFragment getInstance() {
        if (instance == null) {
            instance = new DiscoverFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover, container, false);
        ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initInterface();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.start_service))) {
            mRlPushDynamicStatus.setVisibility(View.VISIBLE);
        } else if (msg.equals(getString(R.string.destroy_service))) {
            mRlPushDynamicStatus.setVisibility(View.GONE);
        }
    }

    //初始化界面
    private void initInterface() {
        mLlGonggao.setVisibility(View.VISIBLE);
        initTopMenu();
        initViewPager();
        setSelector(1);
        mCloudCircleVp.setCurrentItem(1);
        mTouchListener = new MainActivity.MyOnTouchListener() {
            private float mDownY;

            @Override
            public boolean onTouch(MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownY = ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveY = ev.getY();
                        if (mDownY - moveY > 300) {
                            mTvTitle.setVisibility(View.VISIBLE);
                            mRlTitle.setVisibility(View.GONE);
                            if (mCloudCircleVp.getCurrentItem() == 0) {
                                mTvTitle.setText(getString(R.string.gonggao));
                            } else if (mCloudCircleVp.getCurrentItem() == 1) {
                                mTvTitle.setText(getString(R.string.news));
                            } else if (mCloudCircleVp.getCurrentItem() == 2) {
                                mTvTitle.setText(getString(R.string.dynamic));
                            }
                        } else if (moveY - mDownY > 300) {
                            mTvTitle.setVisibility(View.GONE);
                            mRlTitle.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return false;
            }
        };
        // 将myTouchListener注册到分发列表
        ((MainActivity) this.getActivity()).registerMyOnTouchListener(mTouchListener);
    }


    //初始化ViewPager
    private void initViewPager() {

        CloudMessageVPAdapter cloudMessageVPAdapter = new CloudMessageVPAdapter(getChildFragmentManager(), this);

        mCloudCircleVp.setAdapter(cloudMessageVPAdapter);
        mCloudCircleVp.setOffscreenPageLimit(3);

        mCloudCircleVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mRlTitle.setVisibility(View.VISIBLE);
                mTvTitle.setVisibility(View.GONE);
                if (position == 0) {
                    mLlGonggao.setVisibility(View.VISIBLE);
                    mLlDynamic.setVisibility(View.GONE);
                    mLlNews.setVisibility(View.GONE);
                } else if (position == 1) {
                    mLlGonggao.setVisibility(View.GONE);
                    mLlDynamic.setVisibility(View.GONE);
                    mLlNews.setVisibility(View.VISIBLE);
                } else {
                    mLlGonggao.setVisibility(View.GONE);
                    mLlDynamic.setVisibility(View.VISIBLE);
                    mLlNews.setVisibility(View.GONE);
                }
                setSelector(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //初始化顶部菜单栏
    private void initTopMenu() {

        for (int i = 0; i < mCloudCircleMenu.getChildCount(); i++) {

            final View childAt = mCloudCircleMenu.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mCloudCircleMenu.indexOfChild(childAt);

                    setSelector(index);

                    mCloudCircleVp.setCurrentItem(index);

                }
            });
        }
    }

    //菜单选项选中处理
    private void setSelector(int index) {

        for (int i = 0; i < mCloudCircleMenu.getChildCount(); i++) {

            if (i == index) {

                mCloudCircleMenu.getChildAt(i).setSelected(true);

                switch (index) {

                    case 0:
                        mGonggaoXx.setVisibility(View.VISIBLE);
                        mDongtaiXx.setVisibility(View.INVISIBLE);
                        mNewXx.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        mGonggaoXx.setVisibility(View.INVISIBLE);
                        mDongtaiXx.setVisibility(View.INVISIBLE);
                        mNewXx.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mGonggaoXx.setVisibility(View.INVISIBLE);
                        mDongtaiXx.setVisibility(View.VISIBLE);
                        mNewXx.setVisibility(View.INVISIBLE);
                        break;
                }

            } else {

                mCloudCircleMenu.getChildAt(i).setSelected(false);

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.rl_push_dynamic_status, R.id.iv_gonggao_manager, R.id.iv_news_manager, R.id.iv_news_push, R.id.iv_push_dynamic, R.id.iv_my_dynamic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_gonggao_manager:
                startActivity(new Intent(getActivity(), GonggaoManagerActivity.class));
                break;
            case R.id.rl_push_dynamic_status:
                ToastShow.showToast2(getActivity(), getString(R.string.toast_uploading_dynamic));
                break;
            case R.id.iv_news_manager:
                startActivity(new Intent(getActivity(), NewsManagerActivity.class));
                break;
            case R.id.iv_news_push:
                startActivity(new Intent(getActivity(), NewsEditActivity.class));
                break;
            case R.id.iv_push_dynamic:
                startActivity(new Intent(getActivity(), PublicshDynamicActivity.class));
                break;
            case R.id.iv_my_dynamic:
                Intent intent = new Intent(getActivity(), PersonageDynamicActivity.class);
                intent.putExtra("name", UtilTool.getUser());
                intent.putExtra("user", UtilTool.getTocoId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 将myTouchListener注册到分发列表
        ((MainActivity) this.getActivity()).unregisterMyOnTouchListener(mTouchListener);
    }
}
