package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.CloudMessagePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.ui.adapter.AddFriendAdapter;
import com.bclould.tea.ui.widget.ClearEditText;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.utils.permissions.AuthorizationUserTools;

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
    @Bind(R.id.qr_code_iv)
    ImageView mQrCodeIv;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.ll_qr)
    LinearLayout mLlQr;
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
        initListener();
        initRecyclerView();
        initQR();
    }


    private void initQR() {
        try {
            DBManager mgr = new DBManager(this);
            String user = UtilTool.getTocoId();
            UtilTool.getImage(mgr, user, this, mTouxiang);
//            mTouxiang.setImageBitmap(UtilTool.getImage(mgr,user, this));
            Bitmap bitmap = UtilTool.createQRImage(UtilTool.base64PetToJson(this, Constants.BUSINESSCARD, "name", user, "名片"));
            mQrCodeIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        mLlQr.setVisibility(View.VISIBLE);
        mCloudMessagePresenter.searchUser(user, new CloudMessagePresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                if (data.getName().isEmpty()) {
                    ToastShow.showToast2(AddFriendActivity.this, getString(R.string.no_user));
                } else {
                    mLlQr.setVisibility(View.GONE);
                    mDataList.add(data);
                    mAddFriendAdapter.notifyItemRangeChanged(0, mDataList.size());
                }
            }
        });
    }

    @OnClick({R.id.iv_qr_code, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_qr_code:
                if (!AuthorizationUserTools.isCameraCanUse(this))
                    return;
                Intent intent = new Intent(this, ScanQRCodeActivity.class);
                intent.putExtra("code", 1);
                startActivity(intent);
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }
}
