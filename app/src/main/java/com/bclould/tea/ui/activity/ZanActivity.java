package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.base.MyApp;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.adapter.CommentRVAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/19.
 */

public class ZanActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.heart_shaped)
    ImageView mHeartShaped;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.previous_page)
    LinearLayout mPreviousPage;
    @Bind(R.id.now_page_count)
    TextView mNowPageCount;
    @Bind(R.id.sum_page_count)
    TextView mSumPageCount;
    @Bind(R.id.next_page)
    LinearLayout mNextPage;
    @Bind(R.id.bottom)
    LinearLayout mBottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zan);
        ButterKnife.bind(this);
        initRecyclerView();
        MyApp.getInstance().addActivity(this);
    }

    private void initRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new CommentRVAdapter(this));

    }

    @OnClick({R.id.bark, R.id.heart_shaped})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.heart_shaped:
                break;
        }
    }
}
