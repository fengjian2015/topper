package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.adapter.SearchAdapter;
import com.bclould.tea.ui.widget.ClearEditText;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SearchActivity extends BaseActivity {

    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.et_search)
    ClearEditText mEtSearch;
    @Bind(R.id.cb_search)
    CardView mCbSearch;
    @Bind(R.id.tv_cancel)
    TextView mTvCancel;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private DBManager mMgr;
    List<UserInfo> mUserList = new ArrayList<>();
    List<UserInfo> mBackupsList = new ArrayList<>();
    private SearchAdapter mSearchAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mMgr = new DBManager(this);
        ButterKnife.bind(this);
        initListener();
        initRecyclerView();
        initData();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter = new SearchAdapter(this, mUserList, mMgr);
        mRecyclerView.setAdapter(mSearchAdapter);
    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked() {
        finish();
    }

    private void initListener() {
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String user = mEtSearch.getText().toString().trim();
                String upperCase = user.toUpperCase();
                mUserList.clear();
                for (UserInfo info : mBackupsList) {
                    if (info.getUser().toUpperCase().contains(upperCase) || info.getRemark().toUpperCase().contains(upperCase)) {
                        mUserList.add(info);
                        mSearchAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void initData() {
        mUserList.clear();
        List<UserInfo> userInfos = mMgr.queryAllUser();
        UserInfo userInfo = null;
        UserInfo userInfo2 = null;
        for (UserInfo info : userInfos) {
            if (info.getUser().equals(UtilTool.getTocoId())) {
                userInfo = info;
            } else if (info.getUser().isEmpty()) {
                userInfo2 = info;
            }
        }
        userInfos.remove(userInfo);
        if (userInfo2 != null)
            userInfos.remove(userInfo2);
        mUserList.addAll(userInfos);
        mBackupsList.addAll(userInfos);
        Collections.sort(mUserList);
    }
}
