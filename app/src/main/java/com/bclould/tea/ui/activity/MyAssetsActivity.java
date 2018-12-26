package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bclould.tea.Presenter.SubscribeCoinPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.MyAssetsInfo;
import com.bclould.tea.ui.adapter.MyWalletRVAapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyAssetsActivity extends BaseActivity {

    @Bind(R.id.et_coin_name)
    EditText mEtCoinName;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    List<MyAssetsInfo.DataBean> mDataList = new ArrayList();
    List<MyAssetsInfo.DataBean> mDiltrateData = new ArrayList();
    @Bind(R.id.tv_currency)
    TextView mTvCurrency;
    @Bind(R.id.tv_total)
    TextView mTvTotal;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.cb_search)
    CardView mCbSearch;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    private MyWalletRVAapter mMyWalletRVAapter;
    private ViewGroup mPopupWindowView;
    private PopupWindow mPopupWindow;
    private SubscribeCoinPresenter mSubscribeCoinPresenter;
    private int mCount = 0;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_assets);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        setTitle(getString(R.string.assets),getString(R.string.expect_coin));
        mSubscribeCoinPresenter = new SubscribeCoinPresenter(this);
        MyApp.getInstance().addActivity(this);
        getTotal();
        initRecyclerView();
        initData();
        initEdit();
        initListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                getTotal() ;
                initData();
            }
        });
    }

    private void getTotal() {
        mSubscribeCoinPresenter.getTotal(new SubscribeCoinPresenter.CallBack3() {
            @Override
            public void send(String data) {
                if (data != null && !MyAssetsActivity.this.isDestroyed()) {
                    mCount++;
                    if (mCount == 2) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                    }
                    mTvCurrency.setText(getString(R.string.total_assets_usd));
                    mTvTotal.setText("≈" + data);
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(MyAssetsActivity.this)) {
                    mLlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.transfer))) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    private void initEdit() {
        mEtCoinName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String coinName = mEtCoinName.getText().toString().trim();
                String upperCase = coinName.toUpperCase();
                mDataList.clear();
                for (MyAssetsInfo.DataBean dataBean : mDiltrateData) {
                    if (dataBean.getName().contains(upperCase)) {
                        mDataList.add(dataBean);
                        mMyWalletRVAapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void initData() {
        mSubscribeCoinPresenter.getMyAssets(new SubscribeCoinPresenter.CallBack() {
            @Override
            public void send(List<MyAssetsInfo.DataBean> info) {
                if (!MyAssetsActivity.this.isDestroyed() && info.size() != 0) {
                    mCount++;
                    if (mCount == 2) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                    }
                    mDataList.clear();
                    mDiltrateData.clear();
                    mDataList.addAll(info);
                    mDiltrateData.addAll(info);
                    mMyWalletRVAapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(MyAssetsActivity.this)) {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(15));
        mMyWalletRVAapter = new MyWalletRVAapter(this, mDataList);
        mRecyclerView.setAdapter(mMyWalletRVAapter);
        mMyWalletRVAapter.setOnItemClickListener(new MyWalletRVAapter.OnItemClickListener() {
            @Override
            public void onClick(View view, MyAssetsInfo.DataBean dataBean) {
                initPopupWindow(view, dataBean);
            }
        });
    }

    private void initPopupWindow(View view, final MyAssetsInfo.DataBean dataBean) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPopupWindowView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.pop_assets, null);
        mPopupWindow = new PopupWindow(mPopupWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.AnimationRightFade);
        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, widthPixels - mPopupWindow.getWidth(), location[1] - 15);
        final ImageView back = (ImageView) mPopupWindowView.findViewById(R.id.iv_jiantou);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        final Button inCoin = (Button) mPopupWindowView.findViewById(R.id.btn_in_coin);
        inCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAssetsActivity.this, InCoinActivity.class);
                intent.putExtra("id", dataBean.getId());
                intent.putExtra("coinName", dataBean.getName());
                intent.putExtra("over", dataBean.getOver());
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });
        Button outCoin = (Button) mPopupWindowView.findViewById(R.id.btn_out_coin);
        outCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAssetsActivity.this, OutCoinActivity.class);
                intent.putExtra("id", dataBean.getId());
                intent.putExtra("coinName", dataBean.getName());
                intent.putExtra("over", dataBean.getOver());
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });
        Button transferAccounts = (Button) mPopupWindowView.findViewById(R.id.btn_transfer_accounts);
        transferAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAssetsActivity.this, TransferAccountsActivity.class);
                intent.putExtra("id", dataBean.getId());
                intent.putExtra("coinName", dataBean.getName());
                intent.putExtra("over", dataBean.getOver());
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });
        if (dataBean.getCan_in() == 1) {
            inCoin.setVisibility(View.VISIBLE);
        } else if (dataBean.getCan_in() == 2) {
            inCoin.setVisibility(View.GONE);
        }
        if (dataBean.getCan_out() == 1) {
            outCoin.setVisibility(View.VISIBLE);
        } else if (dataBean.getCan_out() == 2) {
            outCoin.setVisibility(View.GONE);
        }
        if (dataBean.getCan_trans() == 1) {
            transferAccounts.setVisibility(View.VISIBLE);
        } else if (dataBean.getCan_trans() == 2) {
            transferAccounts.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.bark, R.id.tv_add, R.id.et_coin_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, ExpectCoinActivity.class));
                break;
            case R.id.et_coin_name:

                break;
        }
    }
}
