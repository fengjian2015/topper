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
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.CoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.CoinListInfo;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter3;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tocotalk.ui.adapter.CloudCircleVPAdapter;
import com.bclould.tocotalk.ui.adapter.PayManageGVAdapter;
import com.bclould.tocotalk.ui.fragment.BuyFragment;
import com.bclould.tocotalk.ui.fragment.OrderFormFragment;
import com.bclould.tocotalk.ui.fragment.SellFragment;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;
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
    @Bind(R.id.btn_push_ad)
    Button mBtnPushAd;
    @Bind(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @Bind(R.id.tv_filtrate)
    TextView mTvFiltrate;
    private Dialog mBottomDialog;
    private int mId;
    private String mName_zh;
    private String mCoinName = "";
    private Dialog mStateDialog;
    private String mServiceCharge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otc);
        ButterKnife.bind(this);
        mType = "2";
        mTvState.setText(MySharedPreferences.getInstance().getString(STATE));
        mFiltrateList.add(getString(R.string.all));
        mFiltrateList.add(getString(R.string.canceled_canc));
        mFiltrateList.add(getString(R.string.underway));
        mFiltrateList.add(getString(R.string.off_the_stocks));
        mFiltrateList.add(getString(R.string.exception));
        init();
    }

    List<Fragment> mFragmentList = new ArrayList<>();

    private void initFragment() {
        mFragmentList.add(new BuyFragment());
        mFragmentList.add(new SellFragment());
        mFragmentList.add(new OrderFormFragment());
    }

    private void init() {
        initData();
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
            mTvCoinName.setText(mCoinName);
            mServiceCharge = MyApp.getInstance().mOtcCoinList.get(0).getOut_otc();
        }
        mCloudCircleVp.setCurrentItem(0);
        mLlMenu.getChildAt(0).setSelected(true);
        mTvXx.setVisibility(View.VISIBLE);
        initFragment();
        initViewPager();
        initTopMenu();
        mMap.put(getString(R.string.filtrate), 0);
        boolean aBoolean = MySharedPreferences.getInstance().getBoolean(Constants.OTC_DISCLAIMER);
        if (!aBoolean)
            showDisclaimerDialog();
    }

    boolean isCheckBox = false;

    private void showDisclaimerDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_otc_disclaimer, this);
        deleteCacheDialog.show();
        final LinearLayout showHide = (LinearLayout) deleteCacheDialog.findViewById(R.id.ll_show_hide);
        final CheckBox checkBox = (CheckBox) deleteCacheDialog.findViewById(R.id.check_box);
        final Button roger = (Button) deleteCacheDialog.findViewById(R.id.btn_roger);
        roger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        showHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckBox = !isCheckBox;
                checkBox.setChecked(isCheckBox);
                MySharedPreferences.getInstance().setBoolean(Constants.OTC_DISCLAIMER, isCheckBox);
            }
        });
    }

    private void initData() {
        if (MyApp.getInstance().mOtcCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("otc", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    UtilTool.Log(getString(R.string.coins), data.size() + "");
                    MyApp.getInstance().mOtcCoinList.addAll(data);
                }
            });
        }
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
                if (position == 2) {
                    mTvFiltrate.setVisibility(View.VISIBLE);
                    mRlSelectorState.setVisibility(View.GONE);
                } else {
                    mTvFiltrate.setVisibility(View.GONE);
                    mRlSelectorState.setVisibility(View.VISIBLE);
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
                        mTvXx.setVisibility(View.VISIBLE);
                        mTvXx2.setVisibility(View.GONE);
                        mTvXx3.setVisibility(View.GONE);
                        break;
                    case 1:
                        mTvXx.setVisibility(View.GONE);
                        mTvXx2.setVisibility(View.VISIBLE);
                        mTvXx3.setVisibility(View.GONE);
                        break;
                    case 2:
                        mTvXx.setVisibility(View.GONE);
                        mTvXx2.setVisibility(View.GONE);
                        mTvXx3.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                mLlMenu.getChildAt(i).setSelected(false);
            }
        }
    }

    @OnClick({R.id.tv_coin_name, R.id.rl_selector_state, R.id.bark, R.id.my_publish, R.id.btn_push_ad, R.id.tv_filtrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_selector_state:
                showStateDialog();
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.tv_coin_name:
                showCoinDialog();
                break;
            case R.id.tv_filtrate:
                showFiltrateDialog();
                break;
            case R.id.my_publish:
                Intent intent2 = new Intent(this, MyPushAdActivity.class);
                intent2.putExtra("coinName", mCoinName);
                startActivity(intent2);
                break;
            case R.id.btn_push_ad:
                Intent intent = new Intent(this, PushBuyingActivity.class);
                intent.putExtra("coinName", mCoinName);
                intent.putExtra("serviceCharge", mServiceCharge);
                startActivity(intent);
                break;
        }
    }

    String mType = "";
    List<String> mFiltrateList = new ArrayList<>();
    private Map<String, Integer> mMap = new HashMap<>();

    private void showFiltrateDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bill, null);
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
        GridView gridView = (GridView) mBottomDialog.findViewById(R.id.grid_view);
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        gridView.setAdapter(new PayManageGVAdapter(this, mFiltrateList, mMap, new PayManageGVAdapter.CallBack() {
            //接口回调
            @Override
            public void send(int position, String typeName) {
                if (typeName.equals(getString(R.string.all))) {
                    mType = "2";
                } else if (typeName.equals(getString(R.string.canceled_canc))) {
                    mType = "0";
                } else if (typeName.equals(getString(R.string.off_the_stocks))) {
                    mType = "3";
                } else if (typeName.equals(getString(R.string.underway))) {
                    mType = "1";
                } else if (typeName.equals(getString(R.string.exception))) {
                    mType = "4";
                }
                mMap.put(getString(R.string.filtrate), position);
                mBottomDialog.dismiss();
                MessageEvent messageEvent = new MessageEvent(getString(R.string.deal_order_filtrate));
                messageEvent.setFiltrate(mType);
                EventBus.getDefault().post(messageEvent);
            }
        }));
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
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mOtcCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OtcActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
    }

    private void showStateDialog() {
        mStateDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);
        //获得dialog的window窗口
        Window window = mStateDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mStateDialog.setContentView(contentView);
        mStateDialog.show();
        RecyclerView recyclerView = (RecyclerView) mStateDialog.findViewById(R.id.recycler_view);
        TextView tvTitle = (TextView) mStateDialog.findViewById(R.id.tv_title);
        Button addCoin = (Button) mStateDialog.findViewById(R.id.btn_add_coin);
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OtcActivity.this, MyAssetsActivity.class));
                mStateDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selecotr_state));
        if (MyApp.getInstance().mCoinList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            addCoin.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new BottomDialogRVAdapter3(this, MyApp.getInstance().mStateList));
        } else {
            recyclerView.setVisibility(View.GONE);
            addCoin.setVisibility(View.VISIBLE);
        }
    }

    public void hideDialog(String name, int id, String serviceCharge) {
        mBottomDialog.dismiss();
        mTvCoinName.setText(name);
        mCoinName = name;
        mServiceCharge = serviceCharge;
        MessageEvent messageEvent = new MessageEvent(getString(R.string.coin_switchover));
        messageEvent.setCoinName(name);
        EventBus.getDefault().post(messageEvent);
    }

    public void hideDialog2(int id, String name) {
        mStateDialog.dismiss();
        mId = id;
        mName_zh = name;
        mTvState.setText(name);
        MessageEvent messageEvent = new MessageEvent(getString(R.string.state_switchover));
        messageEvent.setState(name);
        EventBus.getDefault().post(messageEvent);
    }


    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }*/
}
