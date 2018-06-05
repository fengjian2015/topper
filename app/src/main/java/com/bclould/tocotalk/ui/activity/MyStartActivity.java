package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
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
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.BlockchainGuessPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.GuessListInfo;
import com.bclould.tocotalk.ui.adapter.GuessListRVAdapter;
import com.bclould.tocotalk.ui.adapter.PayManageGVAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyStartActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_filtrate)
    TextView mTvFiltrate;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;

    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int end = 0;
    private int mPage = 1;
    private int mPageSize = 10;
    private GuessListRVAdapter mGuessListRVAdapter;
    private Dialog mBottomDialog;
    private List<String> mFiltrateList = new ArrayList<>();
    private Map<String, Integer> mMap = new HashMap<>();
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_start);
        ButterKnife.bind(this);
        init();
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
                refreshlayout.finishRefresh(2000);
                initData(mType, PULL_DOWN);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
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
            mPage = 1;
            end = 0;
        }
        isFinish = false;
        mBlockchainGuessPresenter.getMyStart(mPage, mPageSize, status, new BlockchainGuessPresenter.CallBack() {
            @Override
            public void send(List<GuessListInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (mDataList.size() != 0 || data.size() != 0) {
                        isFinish =true;
                        if (type == PULL_UP) {
                            if (data.size() == mPageSize) {
                                mPage++;
                                mDataList.addAll(data);
                                mGuessListRVAdapter.notifyDataSetChanged();
                            } else {
                                if (end == 0) {
                                    end++;
                                    mDataList.addAll(data);
                                    mGuessListRVAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (mPage == 1) {
                                mPage++;
                            }
                            mDataList.clear();
                            mDataList.addAll(data);
                            mGuessListRVAdapter.notifyDataSetChanged();
                        }
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        mGuessListRVAdapter = new GuessListRVAdapter(mDataList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mGuessListRVAdapter);
    }


    @OnClick({R.id.bark, R.id.tv_filtrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_filtrate:
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
