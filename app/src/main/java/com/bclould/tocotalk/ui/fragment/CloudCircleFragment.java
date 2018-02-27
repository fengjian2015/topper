package com.bclould.tocotalk.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.PushBuyingActivity;
import com.bclould.tocotalk.ui.adapter.CloudCircleVPAdapter;
import com.bclould.tocotalk.ui.widget.PushingDialog;
import com.bclould.tocotalk.utils.StatusBarCompat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/19.
 */

public class CloudCircleFragment extends Fragment {

    public static final String HEADINGCODE = "heading_code";
    public static CloudCircleFragment instance = null;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.rl_selector_state)
    RelativeLayout mRlSelectorState;
    @Bind(R.id.tv_proceed)
    TextView mTvProceed;
    @Bind(R.id.tv_finish)
    TextView mTvFinish;
    @Bind(R.id.ll_order_selector)
    LinearLayout mLlOrderSelector;
    @Bind(R.id.zixun_xx)
    TextView mZixunXx;
    @Bind(R.id.tongzhi_xx)
    TextView mTongzhiXx;
    @Bind(R.id.dingdan_xx3)
    TextView mDingdanXx3;
    @Bind(R.id.cloud_circle_menu)
    LinearLayout mCloudCircleMenu;
    @Bind(R.id.tv_pushing)
    TextView mTvPushing;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.cloud_circle_vp)
    ViewPager mCloudCircleVp;


    public static CloudCircleFragment getInstance() {

        if (instance == null) {

            instance = new CloudCircleFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cloud_circle, container, false);

        ButterKnife.bind(this, view);
        mCloudCircleVp.bringToFront();
        initInterface();

        return view;
    }

    //初始化界面
    private void initInterface() {

        mStatusBarFix.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarCompat.getStateBarHeight(getActivity())));//填充状态栏

        mTvProceed.setSelected(true);

        setSelector(0);

        mCloudCircleVp.setCurrentItem(0);

        initBottomMenu();

//        initViewPager();


    }

    //初始化ViewPager
    private void initViewPager() {

        CloudCircleVPAdapter cloudCircleVPAdapter = new CloudCircleVPAdapter(getChildFragmentManager());

        mCloudCircleVp.setAdapter(cloudCircleVPAdapter);

        mCloudCircleVp.setOffscreenPageLimit(3);

        mCloudCircleVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelector(position);
                if (position == 2) {
                    mLlOrderSelector.setVisibility(View.VISIBLE);
                    mRlSelectorState.setVisibility(View.GONE);
                } else {
                    mLlOrderSelector.setVisibility(View.GONE);
                    mRlSelectorState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //初始化顶部菜单栏
    private void initBottomMenu() {

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

                        mZixunXx.setVisibility(View.VISIBLE);

                        mTongzhiXx.setVisibility(View.INVISIBLE);

                        mDingdanXx3.setVisibility(View.INVISIBLE);

                        break;
                    case 1:

                        mZixunXx.setVisibility(View.INVISIBLE);

                        mTongzhiXx.setVisibility(View.VISIBLE);

                        mDingdanXx3.setVisibility(View.INVISIBLE);

                        break;
                    case 2:

                        mZixunXx.setVisibility(View.INVISIBLE);

                        mTongzhiXx.setVisibility(View.INVISIBLE);

                        mDingdanXx3.setVisibility(View.VISIBLE);
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


    //显示弹窗
    private void showDialog() {

        ObjectAnimator rotation = ObjectAnimator.ofFloat(mTvPushing, "rotation", 0, -135);

        rotation.setDuration(500);

        rotation.start();

        PushingDialog pushingDialog = new PushingDialog(getContext(), R.style.dialog);

        Window window = pushingDialog.getWindow();

        window.setWindowAnimations(R.style.CustomDialog);

        pushingDialog.show();

        pushingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                ObjectAnimator rotation = ObjectAnimator.ofFloat(mTvPushing, "rotation", -135, 0);

                rotation.setDuration(500);

                rotation.start();

            }
        });

        dialogClick(pushingDialog);

    }

    //对弹窗点击事件的处理
    private void dialogClick(final PushingDialog pushingDialog) {

        Button buy = (Button) pushingDialog.findViewById(R.id.btn_buy);

        Button sell = (Button) pushingDialog.findViewById(R.id.btn_sell);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PushBuyingActivity.class);

                intent.putExtra(HEADINGCODE, 1);

                startActivity(intent);

                pushingDialog.dismiss();

            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PushBuyingActivity.class);

                intent.putExtra(HEADINGCODE, 2);

                startActivity(intent);

                pushingDialog.dismiss();

            }
        });
    }

    @OnClick({R.id.rl_selector_state, R.id.tv_proceed, R.id.tv_finish, R.id.tv_pushing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /*case R.id.rl_selector_state:

                break;
            case R.id.tv_proceed:
                mTvProceed.setSelected(true);
                mTvFinish.setSelected(false);
                break;
            case R.id.tv_finish:
                mTvProceed.setSelected(false);
                mTvFinish.setSelected(true);
                break;
            case R.id.tv_pushing:

                showDialog();

                break;*/
        }
    }
}
