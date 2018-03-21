package com.bclould.tocotalk.ui.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.PublicshDynamicActivity;
import com.bclould.tocotalk.ui.adapter.CloudMessageVPAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


@RequiresApi(api = Build.VERSION_CODES.N)
public class DiscoverFragment extends Fragment {

    public static DiscoverFragment instance = null;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.dongtai_xx)
    TextView mDongtaiXx;
    @Bind(R.id.new_xx)
    TextView mNewXx;
    @Bind(R.id.cloud_circle_menu)
    LinearLayout mCloudCircleMenu;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.cloud_circle_vp)
    ViewPager mCloudCircleVp;
    @Bind(R.id.tv_push)
    TextView mTvPush;


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
        initInterface();
        return view;
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

                        mDongtaiXx.setVisibility(View.VISIBLE);

                        mNewXx.setVisibility(View.INVISIBLE);

                        break;
                    case 1:

                        mDongtaiXx.setVisibility(View.INVISIBLE);

                        mNewXx.setVisibility(View.VISIBLE);

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
    }

    @OnClick(R.id.tv_push)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), PublicshDynamicActivity.class));
    }
}
