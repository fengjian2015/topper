package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bclould.tea.Presenter.SubscribeCoinPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.MyAssetsInfo;
import com.bclould.tea.ui.adapter.SubscribeCoinRVAdatper;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SubscribeCoinActivity extends BaseActivity {


    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private SubscribeCoinRVAdatper mAdatper;
    private SubscribeCoinPresenter mSubscribeCoinPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_coin);
        ButterKnife.bind(this);
        setTitle(getString(R.string.add),R.mipmap.icon_bg_btc);
        initData();
    }


    private void initRecyclerView(List<MyAssetsInfo.DataBean> info) {
        mAdatper = new SubscribeCoinRVAdatper(this, info, mSubscribeCoinPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdatper);
    }

    private void initData() {
        mSubscribeCoinPresenter = new SubscribeCoinPresenter(this);
        mSubscribeCoinPresenter.getMyAssets(new SubscribeCoinPresenter.CallBack() {
            @Override
            public void send(List<MyAssetsInfo.DataBean> info) {
                initRecyclerView(info);
            }

            @Override
            public void error() {

            }
        });
    }

    @OnClick({R.id.bark, R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_more:
                break;
        }
    }

}
