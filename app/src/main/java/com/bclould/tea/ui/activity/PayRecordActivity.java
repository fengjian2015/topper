package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.ReceiptPaymentPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.TransferListInfo;
import com.bclould.tea.ui.adapter.PayManageGVAdapter;
import com.bclould.tea.ui.adapter.PayRecordRVAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.TimeSelectUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/3/20.
 */

public class PayRecordActivity extends BaseActivity implements TimeSelectUtil.OnTimeReturnListener {
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.rl_date_selector)
    RelativeLayout mRlDateSelector;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private PayRecordRVAdapter mPayRecordRVAdapter;
    private Dialog mBottomDialog;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage_id = 1;
    private int mPageSize = 10;
    boolean isFinish = true;
    String mTypes = "";
    String mDate = "";
    private Map<String, Integer> mMap = new HashMap<>();
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;
    private List<String> mFiltrateList = new ArrayList<>();

    private TimeSelectUtil mTimeSelectUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
        ButterKnife.bind(this);
        setTitle(getString(R.string.payment_record),getString(R.string.filtrate));
        initTime();
        initIntent();
        initRecycler();
        initMap();
        initData(PULL_DOWN,1);
        initListener();
    }

    private void initTime(){
        mTimeSelectUtil=new TimeSelectUtil(this,3);
        mTimeSelectUtil.setOnTimeReturnListener(this);
        mDate=mTimeSelectUtil.getDate();
        mTvDate.setText(mDate);
    }

    private void initListener() {
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                if (isFinish) {
                    initData(PULL_UP,mPage_id+1);
                }
            }
        });
        mRefreshLayout.setDisableContentWhenLoading(false);
    }

    private void initIntent() {
        mTypes = getIntent().getStringExtra("type");
        mFiltrateList.add(getString(R.string.all));
        mFiltrateList.add(getString(R.string.red_package));
        mFiltrateList.add(getString(R.string.transfer));
        mFiltrateList.add(getString(R.string.receipt_payment));
        mFiltrateList.add(getString(R.string.in_coin));
        mFiltrateList.add(getString(R.string.out_coin));
        mFiltrateList.add(getString(R.string.reward));
        mFiltrateList.add(getString(R.string.exchange));
        mFiltrateList.add(getString(R.string.guess));
        mTvDate.setText(mDate);
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
    }

    private void initMap() {
        mMap.put(getString(R.string.filtrate), Integer.parseInt(mTypes));
    }

    List<TransferListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(final int type,int page) {
        isFinish = false;
        mReceiptPaymentPresenter.transRecord(page, mPageSize, mTypes, mDate, new ReceiptPaymentPresenter.CallBack4() {
            @Override
            public void send(List<TransferListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(PayRecordActivity.this)) {
                    if (mRecyclerView != null) {
                        if (type == PULL_DOWN) {
                            mPage_id=1;
                            mRefreshLayout.finishRefresh();
                        } else {
                            mPage_id++;
                            mRefreshLayout.finishLoadMore();
                        }
                        isFinish = true;
                        if (mDataList.size() != 0 || data.size() != 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mLlError.setVisibility(View.GONE);
                            if (type == PULL_DOWN) {
                                mDataList.clear();
                                if (data.size() == 0) {
                                    mRecyclerView.setVisibility(View.GONE);
                                    mLlNoData.setVisibility(View.VISIBLE);
                                    mLlError.setVisibility(View.GONE);
                                }
                            }
                            mDataList.addAll(data);
                            mPayRecordRVAdapter.notifyDataSetChanged();
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            mLlNoData.setVisibility(View.VISIBLE);
                            mLlError.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(PayRecordActivity.this)) {
                    isFinish = true;
                    if (type == PULL_DOWN) {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void finishRefresh() {
                isFinish = true;
                if (type == PULL_DOWN) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadMore();
                }
            }
        });
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPayRecordRVAdapter = new PayRecordRVAdapter(this, mDataList);
        mRecyclerView.setAdapter(mPayRecordRVAdapter);
    }

    @OnClick({R.id.bark, R.id.rl_date_selector, R.id.tv_add, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_date_selector:
                mTimeSelectUtil.initOptionPicker();
                break;
            case R.id.tv_add:
                showFiltrateDialog();
                break;
            case R.id.ll_error:
                initData(PULL_DOWN,1);
                break;
        }
    }


    //显示账单筛选dialog


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
                    mTypes = "0";
                } else if (typeName.equals(getString(R.string.transfer))) {
                    mTypes = "1";
                } else if (typeName.equals(getString(R.string.red_package))) {
                    mTypes = "2";
                } else if (typeName.equals(getString(R.string.receipt_payment))) {
                    mTypes = "3";
                } else if (typeName.equals(getString(R.string.in_coin))) {
                    mTypes = "4";
                } else if (typeName.equals(getString(R.string.out_coin))) {
                    mTypes = "5";
                } else if (typeName.equals(getString(R.string.reward))) {
                    mTypes = "6";
                } else if (typeName.equals(getString(R.string.exchange))) {
                    mTypes = "7";
                }else if (typeName.equals(getString(R.string.guess))) {
                    mTypes = "8";
                }
                initData(PULL_DOWN,1);
                mMap.put(getString(R.string.filtrate), position);
                mBottomDialog.dismiss();
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBottomDialog != null)
            mBottomDialog.dismiss();
        mTimeSelectUtil.dismiss();
    }

    @Override
    public void getTime(String time) {
        mDate=time;
        mTvDate.setText(mDate);
        initData(PULL_DOWN,1);
    }
}
