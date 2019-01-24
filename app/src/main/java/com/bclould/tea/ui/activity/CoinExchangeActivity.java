package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.ExchangeOrderInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.adapter.CoinExchangeRVAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/4.
 */

public class CoinExchangeActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_exchange)
    TextView mTvExchange;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.ll_coin_selector)
    LinearLayout mLlCoinSelector;
    @Bind(R.id.tv_remain)
    TextView mTvRemain;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.btn_float)
    Button mBtnFloat;
    @Bind(R.id.tv_cny)
    TextView mTvCny;
    @Bind(R.id.btn_exchange)
    Button mBtnExchange;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_exchange)
    LinearLayout mLlExchange;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_data)
    RelativeLayout mRlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private CoinPresenter mCoinPresenter;
    private Dialog mBottomDialog;
    private CoinExchangeRVAdapter mCoinExchangeRVAdapter;

    private String mServiceCharge;
    private PWDDialog pwdDialog;
    private String logo;

    private double cny_rate;
    private double usd_rate;

    List<CoinListInfo.DataBean> mCoinList = new ArrayList<>();
    List<String> mCoin = new ArrayList<>();

    int mPageSize = 10;
    int mPage = 1;
    List<ExchangeOrderInfo.DataBean.ListBean> mExchangeOrderList = new ArrayList<>();

    private String mListCoinName;//请求列表的coinName

    boolean isCheckBox = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_exchange);
        ButterKnife.bind(this);
        setTitle(getString(R.string.lrwmzpm), getString(R.string.question));
        initListener();
        initRecylerView();
        initData();
        boolean aBoolean = MySharedPreferences.getInstance().getBoolean(Constants.EXCHANGE_DISCLAIMER);
        if (!aBoolean)
            showDisclaimerDialog();
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCoinExchangeRVAdapter = new CoinExchangeRVAdapter(this, mExchangeOrderList);
        mRecyclerView.setAdapter(mCoinExchangeRVAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initListData(true);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initListData(false);
            }
        });
        mCoinExchangeRVAdapter = new CoinExchangeRVAdapter(this, mExchangeOrderList);
        mRecyclerView.setAdapter(mCoinExchangeRVAdapter);
    }

    private void initData() {
        mCoinPresenter = new CoinPresenter(this);
        initCoin();
    }

    private void initListData(final boolean isRefresh) {
        int p = 1;
        if (isRefresh) {
            p = 1;
        } else {
            p = mPage + 1;
        }
        mCoinPresenter.exchangeOrder("USDT", mListCoinName, p, mPageSize, new CoinPresenter.CallBack3() {
            @Override
            public void send(ExchangeOrderInfo.DataBean data) {
                if (data==null)return;
                if (ActivityUtil.isActivityOnTop(CoinExchangeActivity.this)) {
                    if (data.getOrders().size() == mPageSize) {
                        mRefreshLayout.setEnableLoadMore(true);
                    } else {
                        mRefreshLayout.setEnableLoadMore(false);
                    }
                    resetRefresh(isRefresh);
                    if (isRefresh) {
                        mPage = 1;
                        mExchangeOrderList.clear();
                    } else {
                        mPage++;
                    }
                    mRlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mExchangeOrderList.addAll(data.getOrders());
                    mCoinExchangeRVAdapter.notifyDataSetChanged();
                    cny_rate = UtilTool.parseDouble(data.getCny_rate());
                    usd_rate = UtilTool.parseDouble(data.getUsd_rate());
                    mBtnFloat.setText("1:" + usd_rate);
                    setRate(0);
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(CoinExchangeActivity.this)) {
                    mRlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                    resetRefresh(isRefresh);
                }
            }
        });
    }

    private void initCoin() {
        mCoinList.clear();
        mCoinPresenter.coinLists("exchange", new CoinPresenter.CallBack() {
            @Override
            public void send(List<CoinListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(CoinExchangeActivity.this)) {
                    if (data.size() != 0) {
                        mCoinList.addAll(data);
                        mCoin.add(data.get(0).getName());
                        mServiceCharge = data.get(0).getOut_exchange();
                        mTvRemain.setText(getString(R.string.dqky) + data.get(0).getCoin_over() + " " + data.get(0).getName());
                        mTvCoin.setText(data.get(0).getName());
                        Glide.with(CoinExchangeActivity.this).load(data.get(0).getLogo()).into(mIvLogo);
                        mTvExchange.setText(data.get(0).getName() + "/" + getString(R.string.usdt));
                        mListCoinName=data.get(0).getName();
                        initListData(true);
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(CoinExchangeActivity.this)) {
                    mRlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_add, R.id.ll_coin_selector, R.id.btn_exchange, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.ll_coin_selector:
                showCoinDialog();
                break;
            case R.id.btn_exchange:
                if (checkEidt()) {
                    showPWDialog();
                }
                break;
            case R.id.ll_error:
                initCoin();
                break;
        }
    }

    private void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    private void setRate(double money) {
        double usdt = money * usd_rate;
        double cny = money * cny_rate;
        mTvPrice.setText(UtilTool.changeMoney(usdt));
        mTvCny.setText(UtilTool.changeMoney(cny));
    }

    private void showPWDialog() {
        pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                exchange(password);
            }
        });
        String coins = mTvCoin.getText().toString();
        String count = mEtCount.getText().toString();
        pwdDialog.showDialog(count, coins, coins + getString(R.string.exchange) + getString(R.string.usdt), logo, null);
    }

    private void exchange(String password) {
        final String count = mEtCount.getText().toString();
        String price = mTvPrice.getText().toString();
        final String coin = mTvCoin.getText().toString();
        mCoinPresenter.exchange(price, count, "USDT", coin, password, new CoinPresenter.CallBack4() {
            @Override
            public void send() {
                if (ActivityUtil.isActivityOnTop(CoinExchangeActivity.this)) {
                    String remain = "";
                    for (CoinListInfo.DataBean info : mCoinList) {
                        if (info.getName().equals(coin)) {
                            remain = Double.parseDouble(info.getCoin_over()) - Double.parseDouble(count) + "";
                            info.setCoin_over(remain);
                        }
                    }
                    mTvRemain.setText(remain);
                    mEtCount.setText("");
                    mListCoinName=mCoin.get(0);
                    initListData(true);
                }
            }

            @Override
            public void error() {

            }
        });
    }

    private boolean checkEidt() {
        if (mEtCount.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
        } else if (mTvPrice.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mTvPrice);
            Toast.makeText(this, getString(R.string.toast_price), Toast.LENGTH_SHORT).show();
        } else if (mTvCoin.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mTvCoin);
            Toast.makeText(this, getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
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
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, mCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CoinExchangeActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.coins));
    }

    public void hideDialog(String name, int id, String logo, String coin_over, String serviceCharge) {
        this.logo = logo;
        mListCoinName=name;
        mCoin.clear();
        mCoin.add(name);
        mBottomDialog.dismiss();
        mTvCoin.setText(name);
        mTvRemain.setText(getString(R.string.dqky) + coin_over + " " + name);
        Glide.with(CoinExchangeActivity.this).load(logo).into(mIvLogo);
        mTvExchange.setText(name + "/" + getString(R.string.usdt));
        mServiceCharge = serviceCharge;
        initListData(true);
    }


    private void showDisclaimerDialog() {
        if (!ActivityUtil.isActivityOnTop(this)) return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_otc_disclaimer, this, R.style.dialog);
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
                MySharedPreferences.getInstance().setBoolean(Constants.EXCHANGE_DISCLAIMER, isCheckBox);
            }
        });
    }

    private void initListener() {
        mEtCount.setFilters(new InputFilter[]{lengthFilter});
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String counts = mEtCount.getText().toString();
                setRate(UtilTool.parseDouble(counts));
            }
        });
    }

    private InputFilter lengthFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // source:当前输入的字符
            // start:输入字符的开始位置
            // end:输入字符的结束位置
            // dest：当前已显示的内容
            // dstart:当前光标开始位置
            // dent:当前光标结束位置
            if (dest.length() == 0 && source.equals(".")) {
                return "0.";
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                if (dotValue.length() == 2) {
                    return "";
                }
            }
            return null;
        }

    };
}
