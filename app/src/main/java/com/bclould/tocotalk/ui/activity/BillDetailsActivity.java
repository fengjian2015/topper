package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
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

import com.bclould.tocotalk.Presenter.DillDataPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.InOutInfo;
import com.bclould.tocotalk.model.TransferInfo;
import com.bclould.tocotalk.ui.adapter.BillDataRVAapter;
import com.bclould.tocotalk.ui.adapter.InOutDataRVAapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BillDetailsActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_record)
    TextView mTvRecord;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    private int mType;
    private DillDataPresenter mDillDataPresenter;
    List<TransferInfo.DataBean> mTransferList = new ArrayList<>();
    List<InOutInfo.DataBean> mInOutList = new ArrayList<>();
    private BillDataRVAapter mBillDataRVAapter;
    private InOutDataRVAapter mInOutDataRVAapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mDillDataPresenter = new DillDataPresenter(this);
        initIntent();
        initRecyclerView();
        initData();
    }

    private void initRecyclerView() {
        if (mType == 2) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mBillDataRVAapter = new BillDataRVAapter(this, mTransferList);
            mRecyclerView.setAdapter(mBillDataRVAapter);
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mInOutDataRVAapter = new InOutDataRVAapter(this, mInOutList);
            mRecyclerView.setAdapter(mInOutDataRVAapter);
        }
    }

    private void initData() {
        if (mType == 0) {
            mDillDataPresenter.getInOutData("充币", new DillDataPresenter.CallBack2() {
                @Override
                public void send(List<InOutInfo.DataBean> data) {
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mInOutList.addAll(data);
                        mInOutDataRVAapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else if (mType == 1) {
            mDillDataPresenter.getInOutData("提币", new DillDataPresenter.CallBack2() {
                @Override
                public void send(List<InOutInfo.DataBean> data) {
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mInOutList.addAll(data);
                        mInOutDataRVAapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            mDillDataPresenter.getTransfer(new DillDataPresenter.CallBack() {
                @Override
                public void send(List<TransferInfo.DataBean> data) {
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mTransferList.addAll(data);
                        mBillDataRVAapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", 0);
        if (mType == 0) {
            mTvTitle.setText("充币记录");
        } else if (mType == 1) {
            mTvTitle.setText("提币记录");
        } else {
            mTvTitle.setText("转账记录");
        }
    }

    @OnClick({R.id.bark, R.id.tv_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_record:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
        }
    }
}
