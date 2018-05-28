package com.bclould.tocotalk.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.CloudMessagePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.ui.adapter.AddFriendAdapter;
import com.bclould.tocotalk.ui.widget.ClearEditText;
import com.bclould.tocotalk.utils.ToastShow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/25.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddFriendActivity extends BaseActivity {


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
    private CloudMessagePresenter mCloudMessagePresenter;
    private List<BaseInfo.DataBean> mDataList = new ArrayList<>();
    private AddFriendAdapter mAddFriendAdapter;
    private DBManager mMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        mCloudMessagePresenter = new CloudMessagePresenter(this);
        mMgr = new DBManager(this);
        MyApp.getInstance().addActivity(this);
        initListener();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddFriendAdapter = new AddFriendAdapter(this, mDataList, mMgr);
        mRecyclerView.setAdapter(mAddFriendAdapter);
    }

    private void initListener() {
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//修改回车键功能
                    // 隐藏键盘
                    ((InputMethodManager) mEtSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String user = mEtSearch.getText().toString().trim();
                    initData(user);
                    return true;
                }
                return false;
            }
        });
    }

    private void initData(String user) {
        mDataList.clear();
        mCloudMessagePresenter.searchUser(user, new CloudMessagePresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                if (data.getName().isEmpty()) {
                    ToastShow.showToast2(AddFriendActivity.this, getString(R.string.no_user));
                } else {
                    mDataList.add(data);
                    mAddFriendAdapter.notifyItemRangeChanged(0, mDataList.size());
                }
            }
        });
    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked() {
        finish();
    }
}
