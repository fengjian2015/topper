package com.bclould.tea.ui.activity.wallet.exchangefrc;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.adapter.ExchangeFRCAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExchangeFRCActivity extends BaseActivity implements ExchangeFRCContacts.View {

    @Bind(R.id.tv_title_top)
    TextView mTvTitleTop;
    @Bind(R.id.tv_proportion)
    TextView mTvProportion;
    @Bind(R.id.tv_exchange_rate)
    TextView mTvExchangeRate;
    @Bind(R.id.tv_echange_frc_help)
    TextView mTvEchangeFrcHelp;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_balance)
    TextView mTvBalance;
    @Bind(R.id.tv_echange_retuen)
    TextView mTvEchangeRetuen;
    @Bind(R.id.btn_exchange)
    Button mBtnExchange;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_exchange)
    LinearLayout mLlExchange;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.tv_success)
    TextView mTvSuccess;
    @Bind(R.id.rl_success)
    RelativeLayout mRlSuccess;
    @Bind(R.id.tv_remaining)
    TextView mTvRemaining;

    private ExchangeFRCContacts.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_frc);
        ButterKnife.bind(this);
        mPresenter = new ExchangeFRCPresenter();
        mPresenter.bindView(this);
        mPresenter.start(this);
    }

    @Override
    public void initView() {
        setTitle(getString(R.string.exchange_frc));
        initRecyclerView();
        mEtCount.setFilters(new InputFilter[]{mPresenter.getLengthFilter()});
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mTvEchangeRetuen.setText(mPresenter.exchangeMoeny(mEtCount.getText().toString()));
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.initHttp(true);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.initHttp(false);
            }
        });
    }

    @OnClick({R.id.bark, R.id.btn_exchange})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_exchange:
                mPresenter.exchange(mEtCount.getText().toString());
                break;

        }
    }

    @Override
    public void setAdapter(ExchangeFRCAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void setEnableLoadMore(boolean istrue) {
        mRefreshLayout.setEnableLoadMore(istrue);
    }

    @Override
    public void setmRlSuccessShow(int isShow) {
        mRlSuccess.setVisibility(isShow);
    }

    @Override
    public void setmTvSuccess(String content) {
        mTvSuccess.setText(content);
    }

    @Override
    public void setmTvBalance(String content) {
        mTvBalance.setText(content);
    }

    @Override
    public void setmTvEchangeFrcHelp(String content) {
        mTvEchangeFrcHelp.setText(content);
    }

    @Override
    public void setmTvProportion(String content) {
        mTvProportion.setText(content);
    }

    @Override
    public void setmTvExchangeRate(String content) {
        mTvExchangeRate.setText(content);
    }

    @Override
    public void setmTvRemaining(String content) {
        mTvRemaining.setText(content);
    }
}
