package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter2;
import com.bclould.tocotalk.ui.adapter.CloudCircleVPAdapter;
import com.bclould.tocotalk.ui.fragment.BuyFragment;
import com.bclould.tocotalk.ui.fragment.OrderFormFragment;
import com.bclould.tocotalk.ui.fragment.SellFragment;
import com.bclould.tocotalk.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;


/**
 * Created by GA on 2018/3/16.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OtcActivity extends BaseActivity {

    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.rl_selector_state)
    RelativeLayout mRlSelectorState;
    @Bind(R.id.tv_coin_name)
    TextView mTvCoinName;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv_xx)
    TextView mTvXx;
    @Bind(R.id.tv_xx2)
    TextView mTvXx2;
    @Bind(R.id.tv_xx3)
    TextView mTvXx3;
    @Bind(R.id.ll_menu)
    LinearLayout mLlMenu;
    @Bind(R.id.cloud_circle_vp)
    ViewPager mCloudCircleVp;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.btn_selector_coin)
    Button mBtnSelectorCoin;
    @Bind(R.id.btn_push_ad)
    Button mBtnPushAd;
    @Bind(R.id.ll_bottom)
    LinearLayout mLlBottom;
    private Dialog mBottomDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc);
        ButterKnife.bind(this);
        initFragment();
        init();
    }

    List<Fragment> mFragmentList = new ArrayList<>();

    private void initFragment() {
        mFragmentList.add(new BuyFragment());
        mFragmentList.add(new SellFragment());
        mFragmentList.add(new OrderFormFragment());
    }

    private void init() {
        initViewPager();
        initTopMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        for (Fragment fragment : mFragmentList) {
            getSupportFragmentManager().beginTransaction().remove(fragment);
            getSupportFragmentManager().beginTransaction().hide(fragment);
        }
    }

    //初始化ViewPager
    private void initViewPager() {

        CloudCircleVPAdapter cloudCircleVPAdapter = new CloudCircleVPAdapter(getSupportFragmentManager(), mFragmentList);

        mCloudCircleVp.setAdapter(cloudCircleVPAdapter);

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

        for (int i = 0; i < mLlMenu.getChildCount(); i++) {

            final View childAt = mLlMenu.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mLlMenu.indexOfChild(childAt);

                    setSelector(index);

                    mCloudCircleVp.setCurrentItem(index);

                }
            });
        }
    }

    //菜单选项选中处理
    private void setSelector(int index) {
        for (int i = 0; i < mLlMenu.getChildCount(); i++) {
            if (i == index) {
                mLlMenu.getChildAt(i).setSelected(true);
                switch (index) {
                    case 0:
                        mTvXx.setBackgroundColor(getColor(R.color.black));
                        mTvXx2.setBackgroundColor(getColor(R.color.blue2));
                        mTvXx3.setBackgroundColor(getColor(R.color.blue2));
                        break;
                    case 1:
                        mTvXx.setBackgroundColor(getColor(R.color.blue2));
                        mTvXx2.setBackgroundColor(getColor(R.color.black));
                        mTvXx3.setBackgroundColor(getColor(R.color.blue2));
                        break;
                    case 2:
                        mTvXx.setBackgroundColor(getColor(R.color.blue2));
                        mTvXx2.setBackgroundColor(getColor(R.color.blue2));
                        mTvXx3.setBackgroundColor(getColor(R.color.black));
                        break;
                }
            } else {
                mLlMenu.getChildAt(i).setSelected(false);
            }
        }
    }

    @OnClick({R.id.rl_selector_state, R.id.bark, R.id.btn_selector_coin, R.id.btn_push_ad})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_selector_state:
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.btn_selector_coin:
                showCoinDialog();
                break;
            case R.id.btn_push_ad:
                Intent intent = new Intent(this, PushBuyingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showCoinDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        RecyclerView recyclerView = (RecyclerView) mBottomDialog.findViewById(R.id.recycler_view);
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        Button addCoin = (Button) mBottomDialog.findViewById(R.id.btn_add_coin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter2(this, MyApp.getInstance().mDataBeanList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OtcActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText("选择币种");
    }

    public void hideDialog(String name, int id) {
        mBottomDialog.dismiss();
        mTvCoinName.setText(name);
        MessageEvent messageEvent = new MessageEvent("幣種切換");
        messageEvent.setCoinName(name);
        EventBus.getDefault().post(messageEvent);
    }


    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }*/
}
