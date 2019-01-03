package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bclould.tea.Presenter.RedRecordPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.RpRecordInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter;
import com.bclould.tea.ui.adapter.RPCoinsRVAdatper;
import com.bclould.tea.ui.adapter.RPRecordRVAdatper;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.UtilTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/1/3.
 */

@SuppressLint("NewApi")
public class RedPacketRecordActivity extends BaseActivity {

    private static final int TIME = 0;
    private static final int TYPE = 1;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.iv_else)
    ImageView mIvElse;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_who_type)
    TextView mTvWhoType;
    @Bind(R.id.tv_send_count)
    TextView mTvSendCount;
    @Bind(R.id.tv_luck)
    TextView mTvLuck;
    @Bind(R.id.rv_coins)
    RecyclerView mRvCoins;
    @Bind(R.id.rv_red_record)
    RecyclerView mRvRedRecord;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.tv_look_all)
    TextView mTvLookAll;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    private RedRecordPresenter mRedRecordPresenter;
    private Dialog mBottomDialog;
    List<String> mTimeList = new ArrayList<>();
    List<String> mTypeList = new ArrayList<>();
    List<RpRecordInfo.DataBean.LogBean> mDataList = new ArrayList<>();
    List<RpRecordInfo.DataBean.TopBean.CoinNumberBean> mTopList = new ArrayList<>();
    List<RpRecordInfo.DataBean.TopBean.CoinNumberBean> mTopAllList = new ArrayList<>();
    private RPRecordRVAdatper mRpRecordRVAdatper;
    private RPCoinsRVAdatper mRpCoinsRVAdatper;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage = 1;
    private int mPageSize = 10;
    boolean isFinish = true;
    private String mType = "get";
    private String mYear;
    private String mType2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket5));
        }
        setContentView(R.layout.activity_red_packet_record);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTvWhoType.setText(UtilTool.getUser() + getString(R.string.sum_rceive));
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date(System.currentTimeMillis());
        mYear = simpleDateFormat.format(date);
        mTvTime.setText(mYear + getString(R.string.year));
        mRedRecordPresenter = new RedRecordPresenter(this);
        UtilTool.getImage(new DBManager(this), UtilTool.getTocoId(), this, mIvTouxiang);
        mTypeList.add(getString(R.string.sum_rceive));
        mTypeList.add(getString(R.string.sum_send));
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        int year = calendar.get(Calendar.YEAR);
        mTimeList.add(year+"");
        mTimeList.add(year-1+"");
        mTimeList.add(year-2+"");
        initListener();
        initRecyclerView();
        initData(PULL_DOWN,1);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        mRpCoinsRVAdatper = new RPCoinsRVAdatper(this, mTopList);
        mRpRecordRVAdatper = new RPRecordRVAdatper(this, mDataList);
        mRvCoins.setLayoutManager(linearLayoutManager);
        mRvCoins.setAdapter(mRpCoinsRVAdatper);
        mRvRedRecord.setLayoutManager(linearLayoutManager2);
        mRvRedRecord.setAdapter(mRpRecordRVAdatper);
        mRvCoins.setNestedScrollingEnabled(false);
        mRvRedRecord.setNestedScrollingEnabled(false);
    }

    private void initListener() {
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (isFinish) {
                    initData(PULL_UP,mPage+1);
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (isFinish) {
                    initData(PULL_DOWN,1);
                }
            }
        });
    }

    private void initData(final int type,int page) {
        if (mType2 != null) {
            if (!mType2.equals(mType)) {
                mTvLookAll.setText(getString(R.string.look_more_coin_red));
                isLook = false;
                mTopList.clear();
                mTopAllList.clear();
            }
        }
        mType2 = mType;
        isFinish = false;
        mRedRecordPresenter.log(mType, page, mPageSize, mYear, new RedRecordPresenter.CallBack() {

            @Override
            public void send(RpRecordInfo.DataBean data) {
                if (ActivityUtil.isActivityOnTop(RedPacketRecordActivity.this)) {
                    mRpRecordRVAdatper.setType(mType);
                    if (type == PULL_UP) {
                        mRefreshLayout.finishLoadMore();
                    } else {
                        mRefreshLayout.finishRefresh();
                    }
                    mPage=mPageSize;
                    isFinish = true;
                    if (mDataList.size() != 0 || data.getLog().size() != 0) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.GONE);
                        if (type == PULL_DOWN) {
                            if (data.getLog().size() == 0) {
                                mLlData.setVisibility(View.GONE);
                                mLlNoData.setVisibility(View.VISIBLE);
                                mLlError.setVisibility(View.GONE);
                            } else {
                                mDataList.clear();
                                mTopList.clear();
                                mRpCoinsRVAdatper.notifyDataSetChanged();
                                mRpRecordRVAdatper.notifyDataSetChanged();
                            }
                            if (data.getTop().getCoin_number().size() > 3) {
                                mTvLookAll.setVisibility(View.VISIBLE);
                                mTopAllList.addAll(data.getTop().getCoin_number());
                                mTopList.addAll(data.getTop().getCoin_number().subList(0, 3));
                            } else {
                                mTvLookAll.setVisibility(View.GONE);
                                mTopList.addAll(data.getTop().getCoin_number());
                            }
                            if (mType.equals("get")) {
                                mTvLuck.setVisibility(View.VISIBLE);
                                mTvSendCount.setText(getString(R.string.sum_rceive) + data.getTop().getRp_number());
                                mTvLuck.setText(getString(R.string.best_luck) + data.getTop().getBest_luck());
                            } else {
                                mTvLuck.setVisibility(View.GONE);
                                mTvSendCount.setText(getString(R.string.sum_send) + data.getTop().getRp_number());
                            }
                        }
                        mDataList.addAll(data.getLog());
                        if (type == PULL_DOWN) {
                            mRpCoinsRVAdatper.notifyDataSetChanged();
                        }
                        mRpRecordRVAdatper.notifyDataSetChanged();
                    } else {
                        mLlData.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(RedPacketRecordActivity.this)) {
                    isFinish = true;
                    mLlData.setVisibility(View.GONE);
                    if (type == PULL_UP) {
                        mRefreshLayout.finishLoadMore();
                    } else {
                        mRefreshLayout.finishRefresh();
                    }
                    mLlError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void finishRefresh() {
                isFinish = true;
                if (type == PULL_UP) {
                    mRefreshLayout.finishLoadMore();
                } else {
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }

    boolean isLook = false;

    @OnClick({R.id.bark, R.id.iv_else, R.id.tv_who_type, R.id.tv_look_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_else:
                showDialog(mTimeList, TIME, getString(R.string.selector_time));
                break;
            case R.id.tv_who_type:
                showDialog(mTypeList, TYPE, getString(R.string.selector_type));
                break;
            case R.id.tv_look_all:
                isLook = !isLook;
                mTopList.clear();
                if (isLook) {
                    mTopList.addAll(mTopAllList);
                    mRpCoinsRVAdatper.notifyDataSetChanged();
                    mTvLookAll.setText(getString(R.string.shouqi));
                } else {
                    mTopList.addAll(mTopAllList.subList(0, 3));
                    mRpCoinsRVAdatper.notifyDataSetChanged();
                    mTvLookAll.setText(getString(R.string.look_more_coin_red));
                }
                break;
        }
    }

    private void showDialog(List<String> data, int type, String title) {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom2, null);
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
        final Button button = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter(this, data, type));
    }

    public void hideDialog(String name, int sign) {
        mBottomDialog.dismiss();
        if (sign == TYPE) {
            mTvWhoType.setText(UtilTool.getUser() + name);
            if (name.equals(getString(R.string.sum_send))) {
                mType = "send";
            } else {
                mType = "get";
            }
            initData(PULL_DOWN,1);
        } else if (sign == TIME) {
            mTvTime.setText(name + getString(R.string.year));
            mYear = name;
            initData(PULL_DOWN,1);
        }
    }
}
