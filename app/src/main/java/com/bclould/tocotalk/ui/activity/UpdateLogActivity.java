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

import com.bclould.tocotalk.Presenter.UpdateLogPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.UpdateLogInfo;
import com.bclould.tocotalk.ui.adapter.UpdateLogRVAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/28.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpdateLogActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private UpdateLogRVAdapter mUpdateLogRVAdapter;
    private UpdateLogPresenter mUpdateLogPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_log);
        ButterKnife.bind(this);
        initRecyclerView();
        initData();
    }

    List<UpdateLogInfo.DataBean> mDataList = new ArrayList<>();
    private void initData() {
        mUpdateLogPresenter = new UpdateLogPresenter(this);
        mUpdateLogPresenter.getUpdateLogList(1, new UpdateLogPresenter.CallBack() {
            @Override
            public void send(List<UpdateLogInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mDataList.clear();
                        mDataList.addAll(data);
                        mUpdateLogRVAdapter.notifyItemRangeChanged(0, mDataList.size());
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUpdateLogRVAdapter = new UpdateLogRVAdapter(this, mDataList);
        mRecyclerView.setAdapter(mUpdateLogRVAdapter);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
