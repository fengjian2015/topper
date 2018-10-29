package com.bclould.tea.ui.activity;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.BankCardPresenter;
import com.bclould.tea.Presenter.CurrencyInOutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.CardListInfo;
import com.bclould.tea.ui.adapter.BankCardRVAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BankCardActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.add_bank_card)
    Button mAddBankCard;
    @Bind(R.id.tv_delete)
    TextView mTvDelete;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    private PWDDialog pwdDialog;
    private BankCardPresenter mBankCardPresenter;
    List<CardListInfo.DataBean> mCardList = new ArrayList<>();
    private BankCardRVAdapter mBankCardRVAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);//初始化EventBus
        mBankCardPresenter = new BankCardPresenter(this);
        initRecyclerView();
        initData();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initData() {
        mCardList.clear();
        mBankCardPresenter.bankCardList(new BankCardPresenter.CallBack2() {
            @Override
            public void send(List<CardListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(BankCardActivity.this)) {
                    mScrollView.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mCardList.addAll(data);
                    mBankCardRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(BankCardActivity.this)) {
                    mScrollView.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (getString(R.string.bank_binding_unbinding).equals(msg)) {
            initData();
        } else if (getString(R.string.set_the_default_bank_card).equals(msg)) {
            initData();
        }
    }

    //初始化条目
    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBankCardRVAdapter = new BankCardRVAdapter(this, mCardList, mBankCardPresenter);
        mRecyclerView.setAdapter(mBankCardRVAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(40));
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    boolean isDelete = false;

    //点击事件的处理
    @OnClick({R.id.bark, R.id.add_bank_card, R.id.tv_delete, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.add_bank_card:
                showPWDialog();
                break;
            case R.id.tv_delete:
                isDelete = !isDelete;
                if (mRecyclerView.getChildCount() != 0) {
                    for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                        View childAt = mRecyclerView.getChildAt(i);
                        ImageView delete = (ImageView) childAt.findViewById(R.id.iv_delete);
                        if (isDelete) {
                            delete.setVisibility(View.VISIBLE);
                        } else {
                            delete.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_binding_bank), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showPWDialog() {
        pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                check(password);
            }
        });
        pwdDialog.showDialog(getString(R.string.verify_pay_pw), null, null, null, null);
    }

    private void check(String password) {
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.check(password);
    }

    public void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        TextView retry = (TextView) deleteCacheDialog.findViewById(R.id.tv_retry);
        TextView findPassword = (TextView) deleteCacheDialog.findViewById(R.id.tv_find_password);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                pwdDialog.show();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(BankCardActivity.this, PayPasswordActivity.class));
            }
        });
    }
}
