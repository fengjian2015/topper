package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.BlockchainGuessPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.GuessListInfo;
import com.bclould.tocotalk.ui.adapter.GuessListRVAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GuessRecordActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    private GuessListRVAdapter mGuessListRVAdapter;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;
    private int mPage = 1;
    private int mPage_size = 1000;
    private int mType = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_record);
        ButterKnife.bind(this);
        mBlockchainGuessPresenter = new BlockchainGuessPresenter(this);
        initRecylerView();
        initData();
    }

    private void initData() {
        mDataList.clear();
        mBlockchainGuessPresenter.getGuessList(mPage, mPage_size, mType, new BlockchainGuessPresenter.CallBack() {
            @Override
            public void send(List<GuessListInfo.DataBean> data) {
                if (data.size() != 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlNoData.setVisibility(View.GONE);
                    mDataList.addAll(data);
                    mGuessListRVAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mLlNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    List<GuessListInfo.DataBean> mDataList = new ArrayList<>();

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGuessListRVAdapter = new GuessListRVAdapter(mDataList, this);
        mRecyclerView.setAdapter(mGuessListRVAdapter);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
