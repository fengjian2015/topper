package com.dashiji.biyun.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dashiji.biyun.Presenter.SubscribeCoinPresenter;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.model.MyAssetsInfo;
import com.dashiji.biyun.utils.MySharedPreferences;
import com.dashiji.biyun.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/8.
 */

public class SubscribeCoinActivity extends BaseActivity {

    private static final String ISADD1 = "isAdd1";
    private static final String ISADD2 = "isAdd2";
    private static final String ISADD3 = "isAdd3";
    private static final String ISADD4 = "isAdd4";
    private static final String ISADD5 = "isAdd5";
    private static final String ISADD6 = "isAdd6";
    private static final String ISADD7 = "isAdd7";
    private static final String ISADD8 = "isAdd8";
    private static final String ISADD9 = "isAdd9";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv_query)
    ImageView mIvQuery;
    @Bind(R.id.iv_coin)
    ImageView mIvCoin;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.btc_add)
    Button mBtcAdd;
    @Bind(R.id.iv_coin2)
    ImageView mIvCoin2;
    @Bind(R.id.tv_coin2)
    TextView mTvCoin2;
    @Bind(R.id.btc_add2)
    Button mBtcAdd2;
    @Bind(R.id.iv_coin3)
    ImageView mIvCoin3;
    @Bind(R.id.tv_coin3)
    TextView mTvCoin3;
    @Bind(R.id.btc_add3)
    Button mBtcAdd3;
    @Bind(R.id.iv_coin4)
    ImageView mIvCoin4;
    @Bind(R.id.tv_coin4)
    TextView mTvCoin4;
    @Bind(R.id.btc_add4)
    Button mBtcAdd4;
    @Bind(R.id.iv_coin5)
    ImageView mIvCoin5;
    @Bind(R.id.tv_coin5)
    TextView mTvCoin5;
    @Bind(R.id.btc_add5)
    Button mBtcAdd5;
    @Bind(R.id.iv_coin6)
    ImageView mIvCoin6;
    @Bind(R.id.tv_coin6)
    TextView mTvCoin6;
    @Bind(R.id.btc_add6)
    Button mBtcAdd6;
    @Bind(R.id.iv_coin7)
    ImageView mIvCoin7;
    @Bind(R.id.tv_coin7)
    TextView mTvCoin7;
    @Bind(R.id.btc_add7)
    Button mBtcAdd7;
    @Bind(R.id.iv_coin8)
    ImageView mIvCoin8;
    @Bind(R.id.tv_coin8)
    TextView mTvCoin8;
    @Bind(R.id.btc_add8)
    Button mBtcAdd8;
    @Bind(R.id.iv_coin9)
    ImageView mIvCoin9;
    @Bind(R.id.tv_coin9)
    TextView mTvCoin9;
    @Bind(R.id.btc_add9)
    Button mBtcAdd9;
    private SubscribeCoinPresenter mSubscribeCoinPresenter;
    private List<MyAssetsInfo.LtcBean> mLtcBeanList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_coin);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //初始化界面
    private void initInterface() {
        getMyAssets();
    }

    private void setLogo(String logo, String name, ImageView ivCoin, TextView tvCoin) {
        tvCoin.setText(name);
        UtilTool.Log("日志", logo);
        Glide.with(this).load(logo).into(ivCoin);
    }


    private void getMyAssets() {
        mSubscribeCoinPresenter = new SubscribeCoinPresenter(this);
        mSubscribeCoinPresenter.getMyAssets();

    }

    public void setData(List<MyAssetsInfo.LtcBean> ltcBeanList) {

        if (ltcBeanList != null) {
            initIsAdd(ltcBeanList);
            setSubscribeAsset(isAdd, mBtcAdd);
            setLogo(ltcBeanList.get(0).getLogo(), ltcBeanList.get(0).getDisplay(), mIvCoin, mTvCoin);
//            isAdd2 = MySharedPreferences.getInstance().getBoolean(ISADD2);
            setSubscribeAsset(isAdd2, mBtcAdd2);
            setLogo(ltcBeanList.get(1).getLogo(), ltcBeanList.get(1).getDisplay(), mIvCoin2, mTvCoin2);
//            isAdd3 = MySharedPreferences.getInstance().getBoolean(ISADD3);
            setSubscribeAsset(isAdd3, mBtcAdd3);
            setLogo(ltcBeanList.get(2).getLogo(), ltcBeanList.get(2).getDisplay(), mIvCoin3, mTvCoin3);
//            isAdd4 = MySharedPreferences.getInstance().getBoolean(ISADD4);
            setSubscribeAsset(isAdd4, mBtcAdd4);
            setLogo(ltcBeanList.get(3).getLogo(), ltcBeanList.get(3).getDisplay(), mIvCoin4, mTvCoin4);
//            isAdd5 = MySharedPreferences.getInstance().getBoolean(ISADD5);
            setSubscribeAsset(isAdd5, mBtcAdd5);
            setLogo(ltcBeanList.get(4).getLogo(), ltcBeanList.get(4).getDisplay(), mIvCoin5, mTvCoin5);
//            isAdd6 = MySharedPreferences.getInstance().getBoolean(ISADD6);
            setSubscribeAsset(isAdd6, mBtcAdd6);
            setLogo(ltcBeanList.get(5).getLogo(), ltcBeanList.get(5).getDisplay(), mIvCoin6, mTvCoin6);
//            isAdd7 = MySharedPreferences.getInstance().getBoolean(ISADD7);
            setSubscribeAsset(isAdd7, mBtcAdd7);
            setLogo(ltcBeanList.get(6).getLogo(), ltcBeanList.get(6).getDisplay(), mIvCoin7, mTvCoin7);
//            isAdd8 = MySharedPreferences.getInstance().getBoolean(ISADD8);
            setSubscribeAsset(isAdd8, mBtcAdd8);
            setLogo(ltcBeanList.get(7).getLogo(), ltcBeanList.get(7).getDisplay(), mIvCoin8, mTvCoin8);

            setSubscribeAsset(isAdd9, mBtcAdd9);
            setLogo(ltcBeanList.get(8).getLogo(), ltcBeanList.get(8).getDisplay(), mIvCoin9, mTvCoin9);
            mLtcBeanList = ltcBeanList;
        } else {

            Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    private void initIsAdd(List<MyAssetsInfo.LtcBean> ltcBeanList) {
        if (ltcBeanList.get(0).getStatus() == 1)
            isAdd = true;
        else
            isAdd = false;
        if (ltcBeanList.get(1).getStatus() == 1)
            isAdd2 = true;
        else
            isAdd2 = false;
        if (ltcBeanList.get(2).getStatus() == 1)
            isAdd3 = true;
        else
            isAdd3 = false;
        if (ltcBeanList.get(3).getStatus() == 1)
            isAdd4 = true;
        else
            isAdd4 = false;
        if (ltcBeanList.get(4).getStatus() == 1)
            isAdd5 = true;
        else
            isAdd5 = false;
        if (ltcBeanList.get(5).getStatus() == 1)
            isAdd6 = true;
        else
            isAdd6 = false;
        if (ltcBeanList.get(6).getStatus() == 1)
            isAdd7 = true;
        else
            isAdd7 = false;
        if (ltcBeanList.get(7).getStatus() == 1)
            isAdd8 = true;
        else
            isAdd8 = false;

        if (ltcBeanList.get(8).getStatus() == 1)
            isAdd9 = true;
        else
            isAdd9 = false;
    }

    boolean isAdd = false;
    boolean isAdd2 = false;
    boolean isAdd3 = false;
    boolean isAdd4 = false;
    boolean isAdd5 = false;
    boolean isAdd6 = false;
    boolean isAdd7 = false;
    boolean isAdd8 = false;
    boolean isAdd9 = false;

    @OnClick({R.id.bark, R.id.iv_query, R.id.btc_add, R.id.btc_add2, R.id.btc_add3, R.id.btc_add4, R.id.btc_add5, R.id.btc_add6, R.id.btc_add7, R.id.btc_add8, R.id.btc_add9})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_query:
                break;
            case R.id.btc_add:
                if (mLtcBeanList != null) {
                    isAdd = !isAdd;
                    onClick(isAdd, mLtcBeanList.get(0).getId(), mBtcAdd, ISADD1);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btc_add2:
                if (mLtcBeanList != null) {
                    isAdd2 = !isAdd2;
                    onClick(isAdd2, mLtcBeanList.get(1).getId(), mBtcAdd2, ISADD2);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btc_add3:
                if (mLtcBeanList != null) {
                    isAdd3 = !isAdd3;
                    onClick(isAdd3, mLtcBeanList.get(2).getId(), mBtcAdd3, ISADD3);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btc_add4:
                if (mLtcBeanList != null) {
                    isAdd4 = !isAdd4;
                    onClick(isAdd4, mLtcBeanList.get(3).getId(), mBtcAdd4, ISADD4);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btc_add5:
                if (mLtcBeanList != null) {
                    isAdd5 = !isAdd5;
                    onClick(isAdd5, mLtcBeanList.get(4).getId(), mBtcAdd5, ISADD5);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btc_add6:
                if (mLtcBeanList != null) {
                    isAdd6 = !isAdd6;
                    onClick(isAdd6, mLtcBeanList.get(5).getId(), mBtcAdd6, ISADD6);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btc_add7:
                if (mLtcBeanList != null) {
                    isAdd7 = !isAdd7;
                    onClick(isAdd7, mLtcBeanList.get(6).getId(), mBtcAdd7, ISADD7);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btc_add8:
                if (mLtcBeanList != null) {
                    isAdd8 = !isAdd8;
                    onClick(isAdd8, mLtcBeanList.get(7).getId(), mBtcAdd8, ISADD8);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
            case R.id.btc_add9:
                if (mLtcBeanList != null) {
                    isAdd9 = !isAdd9;
                    onClick(isAdd9, mLtcBeanList.get(8).getId(), mBtcAdd9, ISADD9);
                } else {
                    Toast.makeText(this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void onClick(boolean isAdd, int id, Button btcAdd, String key) {
        if (isAdd)
            subscribeAsset(id, btcAdd);
        else
            unSubscribeAsset(id, btcAdd);
        MySharedPreferences.getInstance().setBoolean(key, isAdd);
    }

    //取消訂閱幣種
    private void unSubscribeAsset(int id, final Button btcAdd) {
        mSubscribeCoinPresenter.unSubscribeAsset(id, btcAdd);
    }

    //设置订阅和取消效果
    private void setSubscribeAsset(boolean isClick, Button btcAdd) {

        if (isClick) {

            btcAdd.setBackgroundResource(R.drawable.bg_register_shape);

            btcAdd.setText(getString(R.string.cancel));

            btcAdd.setTextColor(getResources().getColor(R.color.black));

        } else {

            btcAdd.setBackgroundResource(R.drawable.bg_buysell_shape);

            btcAdd.setText(getString(R.string.add));

            btcAdd.setTextColor(getResources().getColor(R.color.blue));

        }
    }

    //訂閱幣種
    private void subscribeAsset(int id, final Button btcAdd) {
        mSubscribeCoinPresenter.subscribeAsset(id, btcAdd);
    }

    public void setUi(boolean isClick, Button btcAdd) {
        setSubscribeAsset(isClick, btcAdd);
    }
}
