package com.bclould.tocotalk.ui.fragment;


import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.GonggaoManagerActivity;
import com.bclould.tocotalk.ui.activity.NewsEditActivity;
import com.bclould.tocotalk.ui.activity.NewsManagerActivity;
import com.bclould.tocotalk.ui.activity.PersonageDynamicActivity;
import com.bclould.tocotalk.ui.activity.PublicshDynamicActivity;
import com.bclould.tocotalk.ui.adapter.CloudMessageVPAdapter;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


@RequiresApi(api = Build.VERSION_CODES.N)
public class DiscoverFragment extends Fragment {
    public static DiscoverFragment instance = null;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.gonggao_xx)
    TextView mGonggaoXx;
    @Bind(R.id.new_xx)
    TextView mNewXx;
    @Bind(R.id.dongtai_xx)
    TextView mDongtaiXx;
    @Bind(R.id.cloud_circle_menu)
    LinearLayout mCloudCircleMenu;
    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.cloud_circle_vp)
    ViewPager mCloudCircleVp;
    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;


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
        getPhoneSize();
        initInterface();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.start_service))) {

        }
    }

    //初始化界面
    private void initInterface() {
        setSelector(0);
        mCloudCircleVp.setCurrentItem(0);
        initTopMenu();
        initViewPager();
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

    //获取屏幕高度
    private void getPhoneSize() {
        mDm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDm);
        mHeightPixels = mDm.heightPixels;
    }

    //初始化pop
    private void showPopup() {

        int widthPixels = mDm.widthPixels;

        mView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.pop_news, null);

        mPopupWindow = new PopupWindow(mView, widthPixels / 100 * 35, mHeightPixels / 3, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.9f;
        getActivity().getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        mPopupWindow.showAsDropDown(mXx, (widthPixels - widthPixels / 100 * 35 - 20), 0);
        popChildClick();
    }

    private void popChildClick() {

        int childCount = mView.getChildCount();

        for (int i = 0; i < childCount; i++) {

            final View childAt = mView.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mView.indexOfChild(childAt);

                    switch (index) {
                        case 0:
                            startActivity(new Intent(getActivity(), GonggaoManagerActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 1:
                            startActivity(new Intent(getActivity(), NewsManagerActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 2:
                            startActivity(new Intent(getActivity(), NewsEditActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 3:
                            startActivity(new Intent(getActivity(), PublicshDynamicActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 4:
                            Intent intent = new Intent(getActivity(), PersonageDynamicActivity.class);
                            intent.putExtra("name", UtilTool.getUser());
                            startActivity(intent);
                            mPopupWindow.dismiss();
                            break;
                    }

                }
            });
        }
    }

    @OnClick({R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                showPopup();
                break;
        }
    }
}
