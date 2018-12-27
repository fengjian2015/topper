package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Context;
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

import com.bclould.tea.Presenter.BlockchainGuessPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.GuessListInfo;
import com.bclould.tea.ui.adapter.GuessListRVAdapter;
import com.bclould.tea.ui.adapter.PayManageGVAdapter;
import com.bclould.tea.ui.widget.WinningPopWindow;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyStartActivity extends BaseActivity {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.rl_title)
    View mRlTitle;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;

    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage_id = 0;
    private int mPageSize = 10;
    private GuessListRVAdapter mGuessListRVAdapter;
    private Dialog mBottomDialog;
    private List<String> mFiltrateList = new ArrayList<>();
    private Map<String, Integer> mMap = new HashMap<>();
    private int mType;

    private WinningPopWindow mWinningPopWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_start);
        ButterKnife.bind(this);
        setTitle(getString(R.string.my_start),getString(R.string.filtrate));
        EventBus.getDefault().register(this);//初始化EventBus
        init();
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.winning_show)) {
            show(event.getContent());
        } else if (msg.equals(EventBusUtil.winning_shut_down)) {
            shutDown();
        }
    }

    private void show(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWinningPopWindow = new WinningPopWindow(MyStartActivity.this, content, mRlTitle);
            }
        });
    }

    private void shutDown() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWinningPopWindow != null) {
                    mWinningPopWindow.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//初始化EventBus
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void init() {
        initListener();
        mMap.put(getString(R.string.filtrate), 0);
        setList();
        mBlockchainGuessPresenter = new BlockchainGuessPresenter(this);
        initRecyclerView();
        initData(mType, PULL_DOWN);
    }

    boolean isFinish = true;

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (isFinish)
                    initData(mType, PULL_DOWN);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (isFinish)
                    initData(mType, PULL_UP);
            }
        });
    }

    private void setList() {
        mFiltrateList.add(getString(R.string.all));
        mFiltrateList.add(getString(R.string.touzhuzhong));
        mFiltrateList.add(getString(R.string.dengdai_kj));
        mFiltrateList.add(getString(R.string.yikaijinag));
        mFiltrateList.add(getString(R.string.cancel));
    }

    List<GuessListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(int status, final int type) {
        if (type == PULL_DOWN) {
            mPage_id = 0;
        }
        isFinish = false;
        mBlockchainGuessPresenter.getMyStart(mPage_id, mPageSize, status, new BlockchainGuessPresenter.CallBack() {
            @Override
            public void send(List<GuessListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(MyStartActivity.this)) {
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
                            mGuessListRVAdapter.notifyDataSetChanged();
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
                if (ActivityUtil.isActivityOnTop(MyStartActivity.this)) {
                    isFinish = true;
                    if (type == PULL_DOWN) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
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

    private void initRecyclerView() {
        mGuessListRVAdapter = new GuessListRVAdapter(mDataList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mGuessListRVAdapter);
    }


    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                showDialog();
                break;
        }
    }

    private void showDialog() {
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
                    mType = 0;
                } else if (typeName.equals(getString(R.string.touzhuzhong))) {
                    mType = 1;
                } else if (typeName.equals(getString(R.string.dengdai_kj))) {
                    mType = 2;
                } else if (typeName.equals(getString(R.string.yikaijinag))) {
                    mType = 3;
                } else if (typeName.equals(getString(R.string.cancel))) {
                    mType = 4;
                }
                initData(mType, PULL_DOWN);
                mMap.put(getString(R.string.filtrate), position);
                mBottomDialog.dismiss();
            }
        }));
    }
}
