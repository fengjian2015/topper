package com.bclould.tocotalk.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.Presenter.OutCoinSitePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.OutCoinSiteInfo;
import com.bclould.tocotalk.ui.adapter.OutCoinSiteRVAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/3.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OutCoinSiteActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_coins)
    TextView mTvCoins;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.hint)
    TextView mHint;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.btn_add_site)
    Button mBtnAddSite;
    @Bind(R.id.rl_add_site)
    RelativeLayout mRlAddSite;
    private int mId;
    private OutCoinSitePresenter mOutCoinSitePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //获取地址
        getSite();
    }
    //初始化界面
    private void initInterface() {
        mId = getIntent().getIntExtra("id", 0);//获取上个界面传递的Id
        getSite();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //获取地址
    public void getSite() {
        mOutCoinSitePresenter = new OutCoinSitePresenter(this);
        mOutCoinSitePresenter.getSite(mId);

    }

    //初始化列表
    private void initRecyclerView(List<OutCoinSiteInfo.MessageBean> siteBeanList) {
        mRecyclerView.setAdapter(new OutCoinSiteRVAdapter(this, siteBeanList, mOutCoinSitePresenter, mId));
    }

    @OnClick({R.id.bark, R.id.btn_add_site})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_add_site:
                //跳转添加地址界面传递id
                Intent intent = new Intent();
                intent.setClass(this, AddOutCoinSiteActivity.class);
                intent.putExtra("id", mId);
                startActivity(intent);
                break;
        }
    }

    //请求的列表数据
    public void setData(List<OutCoinSiteInfo.MessageBean> siteBeanList) {
        initRecyclerView(siteBeanList);
    }

    //把地址和地址id返回上一个界面
    public void setSite(String address, int id) {
        Intent intent = new Intent();
        intent.putExtra("address", address);
        intent.putExtra("siteId", id);
        this.setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
