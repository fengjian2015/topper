package com.bclould.tea.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.DillDataPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.InOutInfo;
import com.bclould.tea.model.MyAssetsInfo;
import com.bclould.tea.model.ProvinceBean;
import com.bclould.tea.ui.adapter.OutDataAdapter;
import com.bclould.tea.ui.widget.ClearEditText;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.TimeSelectUtil;
import com.bclould.tea.utils.UtilTool;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TickRecordActivity extends BaseActivity implements TimeSelectUtil.OnTimeReturnListener {
    @Bind(R.id.tv_add1)
    TextView mTvAdd1;
    @Bind(R.id.tv_add)
    TextView mTvAdd;
    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.rl_date_selector)
    RelativeLayout mRlDateSelector;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.et_coin_name)
    ClearEditText mEtCoinName;

    private String mCoinName;
    private String coinId;

    String mDate = "";
    private String keyWord="";
    private int page=1;

    private DillDataPresenter mDillDataPresenter;
    private OutDataAdapter mOutDataAdapter;
    List<InOutInfo.DataBean> mInOutList = new ArrayList<>();
    private TimeSelectUtil mTimeSelectUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coin2);
        ButterKnife.bind(this);
        setTitle(getString(R.string.out_coin) + getString(R.string.record));
        initData();
        initRecyclerView();
        initEdit();
        initHttp(true,1);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOutDataAdapter = new OutDataAdapter(this, mInOutList, mCoinName);
        mRecyclerView.setAdapter(mOutDataAdapter);
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
                initHttp(false, page+1);
            }
        });
    }

    private void initData() {
        mTimeSelectUtil=new TimeSelectUtil(this,3);
        mTimeSelectUtil.setOnTimeReturnListener(this);
        mDate=mTimeSelectUtil.getDate();
        mTvHint.setText(getString(R.string.no_receipt_out_coin_record));
        coinId = getIntent().getStringExtra("coin_id");
        mCoinName = getIntent().getStringExtra("coin_name");
        mTvDate.setText(mDate);
        mDillDataPresenter = new DillDataPresenter(this);
    }

    private void initEdit() {
        mEtCoinName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//修改回车键功能
                    // 隐藏键盘
                    ((InputMethodManager) mEtCoinName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    keyWord = mEtCoinName.getText().toString().trim();
                    initHttp(true,1);
                    return true;
                }
                return false;
            }
        });
    }


    private void initHttp(final boolean isRefresh, int p) {
        mDillDataPresenter.getInOutData("2", coinId,p,keyWord,mDate, new DillDataPresenter.CallBack2() {
            @Override
            public void send(List<InOutInfo.DataBean> data) {
                UtilTool.Log("充提幣", data.size() + "");
                resetRefresh(isRefresh);
                if (mRecyclerView != null) {

                    if (data.size() == 20) {
                        mRefreshLayout.setEnableLoadMore(true);
                    } else {
                        mRefreshLayout.setEnableLoadMore(false);
                    }
                    if (isRefresh) {
                        page = 1;
                        mInOutList.clear();
                    }else {
                        page++;
                    }
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.GONE);
                        mInOutList.addAll(data);
                        mOutDataAdapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(TickRecordActivity.this)) {
                    resetRefresh(isRefresh);
                    mRecyclerView.setVisibility(View.GONE);
                    mLlNoData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    @OnClick({R.id.bark, R.id.rl_date_selector, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_date_selector:
                mTimeSelectUtil.initOptionPicker();
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }

    @Override
    public void getTime(String time) {
        mDate=time;
        mTvDate.setText(mDate);
        initHttp(true,1);
    }

    @Override
    protected void onDestroy() {
        mTimeSelectUtil.dismiss();
        super.onDestroy();
    }
}
