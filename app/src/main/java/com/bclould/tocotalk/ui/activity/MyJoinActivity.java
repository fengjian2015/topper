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
import com.bclould.tocotalk.ui.adapter.MyJoinRVAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyJoinActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.xx3)
    TextView mXx3;
    @Bind(R.id.xx4)
    TextView mXx4;
    @Bind(R.id.ll_top_menu)
    LinearLayout mLlTopMenu;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private MyJoinRVAdapter mMyJoinRVAdapter;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_join);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initTopMenu();
        setSelector(0);
        mBlockchainGuessPresenter = new BlockchainGuessPresenter(this);
        initData();
        initRecyclerView();
    }

    private void initTopMenu() {
        for (int i = 0; i < mLlTopMenu.getChildCount(); i++) {
            View childAt = mLlTopMenu.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = mLlTopMenu.indexOfChild(view);
                    setSelector(index);
                    initData();
                }
            });
        }
    }

    private void setSelector(int index) {
        switch (index) {
            case 0:
                mXx.setVisibility(View.VISIBLE);
                mXx2.setVisibility(View.INVISIBLE);
                mXx3.setVisibility(View.INVISIBLE);
                mXx4.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mXx.setVisibility(View.INVISIBLE);
                mXx2.setVisibility(View.VISIBLE);
                mXx3.setVisibility(View.INVISIBLE);
                mXx4.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mXx.setVisibility(View.INVISIBLE);
                mXx2.setVisibility(View.INVISIBLE);
                mXx3.setVisibility(View.VISIBLE);
                mXx4.setVisibility(View.INVISIBLE);
                break;
            case 3:
                mXx.setVisibility(View.INVISIBLE);
                mXx2.setVisibility(View.INVISIBLE);
                mXx3.setVisibility(View.INVISIBLE);
                mXx4.setVisibility(View.VISIBLE);
                break;
        }
    }

    List<GuessListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData() {

    }

    private void initRecyclerView() {
        mMyJoinRVAdapter = new MyJoinRVAdapter(this, mDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMyJoinRVAdapter);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
