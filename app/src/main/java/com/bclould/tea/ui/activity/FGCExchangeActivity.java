package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.FinanciaPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.FGCInfo;
import com.bclould.tea.ui.adapter.CoinExchangeRVAdapter;
import com.bclould.tea.ui.adapter.FGCAdapter;
import com.bclould.tea.ui.adapter.FinancialAdapter;
import com.bclould.tea.ui.adapter.FinancialGridAdapter;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.UtilTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FGCExchangeActivity extends BaseActivity {

    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.btn_float)
    Button mBtnFloat;
    @Bind(R.id.tv_available)
    TextView mTvAvailable;
    @Bind(R.id.btn_exchange)
    Button mBtnExchange;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_exchange)
    LinearLayout mLlExchange;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private int page=1;
    private LinearLayoutManager mLayoutManager;
    private FGCAdapter mFGCAdapter;
    private List<FGCInfo.DataBean.RecordBean> mHashMapList=new ArrayList<>();
    private double maxMoney=0;
    private double rate;
    private PWDDialog pwdDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgcexchange);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initRecylerView();
        setOnClick();
        initHttp(true,1);
    }


    private void initRecylerView() {
        mRecyclerView.setFocusable(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFGCAdapter = new FGCAdapter(this, mHashMapList);
        mRecyclerView.setAdapter(mFGCAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHttp(true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initHttp(false,page+1);
            }
        });
    }

    private void initHttp(final boolean isRefresh, int p) {
        new FinanciaPresenter(this).exchangeFGC(p, new FinanciaPresenter.CallBack6() {
            @Override
            public void send(FGCInfo baseInfo) {
                resetRefresh(isRefresh);
                if(baseInfo.getData().getRecord().size()==10){
                    mRefreshLayout.setEnableLoadMore(true);
                }else{
                    mRefreshLayout.setEnableLoadMore(false);
                }
                setViewData(baseInfo,isRefresh);
            }

            @Override
            public void error() {
                resetRefresh(isRefresh);
            }
        });
    }

    private void setViewData(FGCInfo baseInfo,boolean isRefresh){
        if(isRefresh) {
            page=1;
            mBtnFloat.setText(baseInfo.getData().getRate() + ":1");
            mTvAvailable.setText(getString(R.string.available_fgc)+ baseInfo.getData().getFgc_num());
            mEtCount.setHint(baseInfo.getData().getUsd_num() + "");
            maxMoney=baseInfo.getData().getUsd_num();
            rate=baseInfo.getData().getRate();
            mHashMapList.clear();
        }else{
            page++;
        }
        mHashMapList.addAll(baseInfo.getData().getRecord());
        mFGCAdapter.notifyDataSetChanged();
    }


    private void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }


    private void setOnClick() {
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
                double counts = UtilTool.parseDouble(mEtCount.getText().toString());
                if(counts>maxMoney){
                    mEtCount.setText(maxMoney+"");
                    mEtCount.setSelection(mEtCount.getText().length());
                }
                setTvPrice(mEtCount.getText().toString());
            }
        });
    }

    private void setTvPrice(String money){
       String m= UtilTool.changeMoney1(UtilTool.parseDouble(money)/rate);
        mTvPrice.setText(m);
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
                if (dotValue.length() == 8) {
                    return "";
                }
            }
            return null;
        }

    };

    @OnClick({R.id.bark, R.id.btn_exchange})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_exchange:
                if (checkEidt()) {
                    showPWDialog();
                }
                break;

        }
    }

    private void showPWDialog() {
        pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                exchange(password);
            }
        });
        String count = mEtCount.getText().toString();
        pwdDialog.showDialog(count, "USDT", getString(R.string.exchange) + "FGC", null, null);
    }

    private void exchange(String password){
        new FinanciaPresenter(this).exchange(mEtCount.getText().toString(), password, new FinanciaPresenter.CallBack1() {
            @Override
            public void send(BaseInfo baseInfo) {
                initHttp(true,1);
            }

            @Override
            public void error() {

            }
        });
    }

    private boolean checkEidt() {
        if (UtilTool.parseDouble(mEtCount.getText().toString())<=0) {
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
        }else{
            return true;
        }
        return false;
    }
}
