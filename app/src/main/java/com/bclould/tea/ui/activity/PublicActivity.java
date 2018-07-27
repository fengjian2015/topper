package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bclould.tea.Presenter.PublicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.PublicInfo;
import com.bclould.tea.ui.adapter.PublicListRVAdapter;
import com.gjiazhe.wavesidebar.WaveSideBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PublicActivity extends BaseActivity implements PublicListRVAdapter.OnclickListener {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.side_bar)
    WaveSideBar mSideBar;
    @Bind(R.id.rl_data)
    RelativeLayout mRlData;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;

    private List<PublicInfo.DataBean> mUsers = new ArrayList<>();
    private PublicListRVAdapter mPublicListRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);
        MyApp.getInstance().addActivity(this);
        ButterKnife.bind(this);
        initRecylerView();
        setListener();
        initData();
    }

    private void initData() {
        new PublicPresenter(this).publicList(new PublicPresenter.CallBack() {
            @Override
            public void send(PublicInfo dataBean) {
                if(dataBean.getData().size()==0){
                    mLlNoData.setVisibility(View.VISIBLE);
                    mRlData.setVisibility(View.GONE);
                }else{
                    mLlNoData.setVisibility(View.GONE);
                    mRlData.setVisibility(View.VISIBLE);
                }
                updateData(dataBean.getData());
            }

            @Override
            public void error() {
                mLlNoData.setVisibility(View.GONE);
                mRlData.setVisibility(View.GONE);
                mLlError.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPublicListRVAdapter = new PublicListRVAdapter(this, mUsers);
        mPublicListRVAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mPublicListRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    Map<String, Integer> mMap = new HashMap<>();

    private void updateData(List<PublicInfo.DataBean> dataBeanList) {
        if (mPublicListRVAdapter == null) {
            return;
        }
        mUsers.clear();
        mMap.clear();
        mUsers.addAll(dataBeanList);
        Collections.sort(mUsers);
        try {
            for (int i = 0; i < mUsers.size(); i++) {
                if (!mUsers.get(i).getFirstLetter().equals("#")) {
                    mMap.put(mUsers.get(i).getFirstLetter(), i);
                }
            }
            String[] arr = mMap.keySet().toArray(new String[mMap.keySet().size() + 1]);
            arr[arr.length - 1] = "#";
            Arrays.sort(arr, String.CASE_INSENSITIVE_ORDER);
            for (int i = 0; i < arr.length; i++) {
                if (i < arr.length - 1)
                    arr[i] = arr[i + 1];
                if (i == arr.length - 1) {
                    arr[i] = "#";
                }
            }
            mSideBar.setIndexItems(arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPublicListRVAdapter.notifyDataSetChanged();
    }

    private void setListener() {
        mSideBar.bringToFront();
        mSideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                for (int i = 0; i < mUsers.size(); i++) {
                    if (mUsers.get(i).getFirstLetter().equals(index)) {
                        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
    }

    @OnClick({R.id.bark,R.id.ll_error,R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.tv_search:
                startActivity(new Intent(PublicActivity.this,SearchPublicActivity.class));
                break;
        }
    }

    @Override
    public void onclick(int position) {

    }

    @Override
    public void onLongClick(int position) {

    }
}
