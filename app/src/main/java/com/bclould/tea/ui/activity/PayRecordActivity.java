package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.ProvinceBean;
import com.bclould.tea.model.TransferListInfo;
import com.bclould.tea.ui.adapter.PayManageGVAdapter;
import com.bclould.tea.ui.adapter.PayRecordRVAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.UtilTool;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayRecordActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_filtrate)
    TextView mTvFiltrate;
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
    private int mPage_id = 0;
    private int mPageSize = 10;
    boolean isFinish = true;
    String mTypes = "";
    String mDate = "";
    private Map<String, Integer> mMap = new HashMap<>();
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;
    private List<String> mFiltrateList = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        getOptionData();
        initIntent();
        initRecycler();
        initMap();
        initData(PULL_DOWN);
        initListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void initListener() {
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                if (isFinish) {
                    initData(PULL_UP);
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
        mTvDate.setText(mDate);
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
    }

    private void initMap() {
        mMap.put(getString(R.string.filtrate), Integer.parseInt(mTypes));
    }

    List<TransferListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(final int type) {
        if (type == PULL_DOWN) {
            mPage_id = 0;
        }
        isFinish = false;
        mReceiptPaymentPresenter.transRecord(mPage_id, mPageSize, mTypes, mDate, new ReceiptPaymentPresenter.CallBack4() {
            @Override
            public void send(List<TransferListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(PayRecordActivity.this)) {
                    if (mRecyclerView != null) {
                        if (type == PULL_DOWN) {
                            mRefreshLayout.finishRefresh();
                        } else {
                            mRefreshLayout.finishLoadMore();
                        }
                        isFinish = true;
                        if (mDataList.size() != 0 || data.size() != 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mLlError.setVisibility(View.GONE);
                            if (type == PULL_DOWN) {
                                if (data.size() == 0) {
                                    mRecyclerView.setVisibility(View.GONE);
                                    mLlNoData.setVisibility(View.VISIBLE);
                                    mLlError.setVisibility(View.GONE);
                                } else {
                                    mDataList.clear();
                                }
                            }
                            mDataList.addAll(data);
                            if (mDataList.size() != 0) {
                                mPage_id = mDataList.get(mDataList.size() - 1).getId();
                            }
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
                    if (type == PULL_DOWN) {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void finishRefresh() {
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

    @OnClick({R.id.bark, R.id.rl_date_selector, R.id.tv_filtrate, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_date_selector:
                initOptionPicker();
                break;
            case R.id.tv_filtrate:
                showFiltrateDialog();
                break;
            case R.id.ll_error:
                initData(PULL_DOWN);
                break;
        }
    }

    private OptionsPickerView pvOptions;
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private String[] mDateArr = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    private void initOptionPicker() {
        if (pvOptions == null) {
            pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    if (options2 >= 0) {
                        mDate = options1Items.get(options1).getPickerViewText()
                                + "-" + options2Items.get(options1).get(options2);
                        mTvDate.setText(mDate);
                        initData(PULL_DOWN);
                    }
                }
            })
                    .setContentTextSize(20)//设置滚轮文字大小
                    .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                    .setSelectOptions(0, 0)//默认选中项
                    .setBgColor(getResources().getColor(R.color.white))
                    .setTitleBgColor(getResources().getColor(R.color.gray2))
                    .setCancelColor(Color.BLACK)
                    .setSubmitColor(getResources().getColor(R.color.blue2))
                    .setTextColorCenter(Color.BLACK)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setLabels(getString(R.string.year), getString(R.string.month), getString(R.string.day))
                    .setBackgroundId(0x00000000) //设置外部遮罩颜色
                    .build();
            pvOptions.setPicker(options1Items, options2Items);//二级选择器
        }
        pvOptions.show();
    }

    private void getOptionData() {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = new Date(System.currentTimeMillis());
        mDate = mSimpleDateFormat.format(date);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM");
        Date date2 = new Date(System.currentTimeMillis());
        int month = Integer.parseInt(simpleDateFormat2.format(date2));
        //选项1
        options1Items.add(new ProvinceBean(0, "2018", "描述部分", "其他数据"));
        options1Items.add(new ProvinceBean(1, "2017", "描述部分", "其他数据"));

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        for (String s : mDateArr) {
            if (month >= Integer.parseInt(s)) {
                options2Items_01.add(s);
            }
        }
        Collections.reverse(options2Items_01);
        UtilTool.Log("時間", options2Items_01.size() + "");
        ArrayList<String> options2Items_02 = new ArrayList<>();
        options2Items_02.add("12");
        options2Items_02.add("11");
        options2Items_02.add("10");
        options2Items_02.add("09");
        options2Items_02.add("08");
        options2Items_02.add("07");
        options2Items_02.add("06");
        options2Items_02.add("05");
        options2Items_02.add("04");
        options2Items_02.add("03");
        options2Items_02.add("02");
        options2Items_02.add("01");
        ArrayList<String> options2Items_03 = new ArrayList<>();
        options2Items_02.add("12");
        options2Items_02.add("11");
        options2Items_02.add("10");
        options2Items_02.add("09");
        options2Items_02.add("08");
        options2Items_02.add("07");
        options2Items_02.add("06");
        options2Items_02.add("05");
        options2Items_02.add("04");
        options2Items_02.add("03");
        options2Items_02.add("02");
        options2Items_02.add("01");
        options2Items.add(options2Items_01);
        options2Items.add(options2Items_02);
//        options2Items.add(options2Items_03);

        /*--------数据源添加完毕---------*/
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
                }
                initData(PULL_DOWN);
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
        if (pvOptions != null)
            pvOptions.dismiss();
    }
}
