package com.bclould.tea.ui.activity;

import android.app.Activity;
import android.content.Context;
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

import com.bclould.tea.Presenter.OutCoinSitePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.OutCoinSiteInfo;
import com.bclould.tea.ui.adapter.OutCoinSiteRVAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.btn_add_site)
    Button mBtnAddSite;
    @Bind(R.id.rl_add_site)
    RelativeLayout mRlAddSite;
    private int mId;
    private OutCoinSitePresenter mOutCoinSitePresenter;
    private String mCoinName;
    private OutCoinSiteRVAdapter mOutCoinSiteRVAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);
        ButterKnife.bind(this);
        setTitle(getString(R.string.all_address));
        mOutCoinSitePresenter = new OutCoinSitePresenter(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initInterface();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.add_site))) {
            getSite();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    //初始化界面
    private void initInterface() {
        Bundle bundle = getIntent().getExtras();
        mCoinName = bundle.getString("coinName");
        mId = bundle.getInt("id", 0);//获取上个界面传递的Id
        UtilTool.Log("地址", mCoinName);
        UtilTool.Log("地址", mId + "");
        //获取上个界面传递的Id
        initRecyclerView();
        getSite();
    }

    List<OutCoinSiteInfo.MessageBean> mDataList = new ArrayList<>();

    //获取地址
    public void getSite() {
        mOutCoinSitePresenter.getSite(mId, new OutCoinSitePresenter.CallBack() {
            @Override
            public void send(List<OutCoinSiteInfo.MessageBean> data) {
                if (mRecyclerView != null) {
                    if (data.size() != 0) {
                        mDataList.clear();
                        mDataList.addAll(data);
                        mOutCoinSiteRVAdapter.notifyItemRangeChanged(0, mDataList.size());
                    }
                }
            }
        });

    }

    //初始化列表
    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOutCoinSiteRVAdapter = new OutCoinSiteRVAdapter(this, mDataList, mOutCoinSitePresenter, mId);
        mRecyclerView.setAdapter(mOutCoinSiteRVAdapter);
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
                intent.putExtra("coinName", mCoinName);
                startActivity(intent);
                break;
        }
    }

    //把地址和地址id返回上一个界面
    public void setSite(String address, int id) {
        Intent intent = new Intent();
        intent.putExtra("address", address);
        intent.putExtra("siteId", id);
        this.setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }
}
