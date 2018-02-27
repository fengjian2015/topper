package com.dashiji.biyun.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.ui.activity.AddFriendActivity;
import com.dashiji.biyun.ui.activity.GrabQRCodeRedActivity;
import com.dashiji.biyun.ui.activity.PublicshDynamicActivity;
import com.dashiji.biyun.ui.activity.ScanQRCodeActivity;
import com.dashiji.biyun.ui.activity.SendQRCodeRedActivity;
import com.dashiji.biyun.ui.adapter.CloudMessageVPAdapter;
import com.dashiji.biyun.utils.StatusBarCompat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GA on 2017/9/19.
 */

public class CloudMessageFragment extends Fragment {

    @Bind(R.id.cloud_circle_menu)
    LinearLayout mCloudCircleMenu;
    @Bind(R.id.cloud_circle_add)
    RelativeLayout mCloudCircleAdd;
    @Bind(R.id.cloud_circle_vp)
    ViewPager mCloudCircleVp;
    @Bind(R.id.yunxin_xx)
    TextView mYunxinXx;
    @Bind(R.id.haoyou_xx)
    TextView mHaoyouXx;
    @Bind(R.id.dongtai_xx)
    TextView mDongtaiXx;

    public static CloudMessageFragment instance = null;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.xx)
    TextView mXx;
    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;
    private int QRCODE = 1;

    public static CloudMessageFragment getInstance() {

        if (instance == null) {

            instance = new CloudMessageFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cloud_message, container, false);

        ButterKnife.bind(this, view);

        initInterface();

        return view;
    }

    //初始化界面
    private void initInterface() {

        getPhoneSize();

        mStatusBarFix.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarCompat.getStateBarHeight(getActivity())));//填充状态栏

        setSelector(0);

        mCloudCircleVp.setCurrentItem(0);

        initBottomMenu();

        initViewPager();
    }

    //初始化ViewPager
    private void initViewPager() {

        CloudMessageVPAdapter cloudMessageVPAdapter = new CloudMessageVPAdapter(getChildFragmentManager());

        mCloudCircleVp.setAdapter(cloudMessageVPAdapter);

        mCloudCircleVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*if(position == 1){
                    EventBus.getDefault().post(new MessageEvent(3));
                }*/
                setSelector(position);
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

                        mYunxinXx.setVisibility(View.VISIBLE);

                        mHaoyouXx.setVisibility(View.INVISIBLE);

                        mDongtaiXx.setVisibility(View.INVISIBLE);

                        break;
                    case 1:

                        mYunxinXx.setVisibility(View.INVISIBLE);

                        mHaoyouXx.setVisibility(View.VISIBLE);

                        mDongtaiXx.setVisibility(View.INVISIBLE);

                        break;
                    case 2:

                        mYunxinXx.setVisibility(View.INVISIBLE);

                        mHaoyouXx.setVisibility(View.INVISIBLE);

                        mDongtaiXx.setVisibility(View.VISIBLE);

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

    @OnClick(R.id.cloud_circle_add)
    public void onViewClicked() {
        initPopWindow();
    }

    //获取屏幕高度
    private void getPhoneSize() {

        mDm = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDm);

        mHeightPixels = mDm.heightPixels;
    }

    //初始化pop
    private void initPopWindow() {

        int widthPixels = mDm.widthPixels;

        mView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.pop_cloud_message, null);

        mPopupWindow = new PopupWindow(mView, widthPixels / 100 * 35, mHeightPixels / 4, true);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            byte[] bytes = Base64.decode(result, Base64.DEFAULT);
            String jsonresult = new String(bytes);
            if (!jsonresult.isEmpty() && jsonresult.contains("id") && jsonresult.contains("红包")) {
                String id = jsonresult.substring(jsonresult.indexOf(":") + 2, jsonresult.indexOf(",") - 1);
                Intent intent = new Intent(getActivity(), GrabQRCodeRedActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", true);
                startActivity(intent);
            }
        }
    }

    //给pop子控件设置点击事件
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
                            Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                            intent.putExtra("code", QRCODE);
                            startActivityForResult(intent, 0);
                            mPopupWindow.dismiss();
                            break;
                        case 1:
                            startActivity(new Intent(getActivity(), SendQRCodeRedActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 2:
                            startActivity(new Intent(getActivity(), AddFriendActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 3:
                            startActivity(new Intent(getActivity(), PublicshDynamicActivity.class));
                            mPopupWindow.dismiss();
                            break;
                    }

                }
            });
        }
    }
}
