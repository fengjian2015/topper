package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.ReviewInfo;
import com.bclould.tea.ui.adapter.ReviewListAdapter;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReviewListActivity extends BaseActivity {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;

    private ArrayList<ReviewInfo.DataBean> mReviewInfos = new ArrayList<>();
    private ReviewListAdapter mReviewListAdapter;
    private String roomId;
    private GroupPresenter mGroupPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        ButterKnife.bind(this);
        setTitle(getString(R.string.initation_list));
        mGroupPresenter = new GroupPresenter(this);
        roomId = getIntent().getStringExtra("roomId");
        initRecyclerView();
        initData();
    }

    private void initData() {
        mGroupPresenter.getReviewList(Integer.parseInt(roomId), new GroupPresenter.CallBack3() {
            @Override
            public void send(ReviewInfo baseInfo) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mLlError.setVisibility(View.GONE);
                mReviewInfos.clear();
                mReviewInfos.addAll(baseInfo.getData());
                mReviewListAdapter.notifyDataSetChanged();
            }

            @Override
            public void error() {
                mRecyclerView.setVisibility(View.GONE);
                mLlError.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewListAdapter = new ReviewListAdapter(this, mReviewInfos,mGroupPresenter );
        mRecyclerView.setAdapter(mReviewListAdapter);
    }

    @OnClick({R.id.bark, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }
}
