package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.UnclaimedRedInfo;
import com.bclould.tea.ui.adapter.UnclaimedRedListAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UnclaimedRedActivity extends BaseActivity {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;

    private UnclaimedRedListAdapter mUnclaimedRedListAdapter;
    private String roomId;
    private ArrayList<UnclaimedRedInfo.DataBean> mArrayList = new ArrayList<>();
    private DBRoomMember mDBRoomMember;
    private DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unclaimed_red);
        MyApp.getInstance().addActivity(this);
        ButterKnife.bind(this);
        setTitle(getString(R.string.unclaimed_red));
        roomId = getIntent().getStringExtra("roomId");
        mDBRoomMember = new DBRoomMember(this);
        mDBManager = new DBManager(this);
        initRecyclerView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        new GroupPresenter(this).getUnclaimedRed(Integer.parseInt(roomId), new GroupPresenter.CallBack4() {
            @Override
            public void send(UnclaimedRedInfo baseInfo) {
                mLlError.setVisibility(View.GONE);
                mArrayList.clear();
                mArrayList.addAll(baseInfo.getData());
                if(mArrayList.size()==0){
                    mLlNoData.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }else{
                    mLlNoData.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                mUnclaimedRedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void error() {
                mLlNoData.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mLlError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUnclaimedRedListAdapter = new UnclaimedRedListAdapter(this, mArrayList, mDBManager, mDBRoomMember, roomId);
        mRecyclerView.setAdapter(mUnclaimedRedListAdapter);
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
