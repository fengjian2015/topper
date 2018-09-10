package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.RedPacketPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.xmpp.RoomManage;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2017/12/28.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SendRedPacketActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_currency)
    TextView mTvCurrency;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.rl_selector_currency)
    RelativeLayout mRlSelectorCurrency;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.tv_allmoney)
    TextView mTvAllmoney;
    @Bind(R.id.image_logo)
    ImageView mImageLogo;
    private Dialog mBottomDialog;
    private DBManager mMgr;
    private String mUser;
    private RedPacketPresenter mRedPacketPresenter;
    private String mRemark;
    private double mCount;
    private String mCoin;
    private PWDDialog pwdDialog;
    private String logo;
//    List<CoinInfo.DataBean> mCoinList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket5));
        mMgr = new DBManager(this);
        setContentView(R.layout.activity_send_red_packet);
        mUser = getIntent().getStringExtra("user");
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initData();
        setOnClick();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initData() {
        MyApp.getInstance().mRedCoinList.clear();
        if (MyApp.getInstance().mRedCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("red_packet", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (ActivityUtil.isActivityOnTop(SendRedPacketActivity.this)) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                        if (MyApp.getInstance().mRedCoinList.size() == 0)
                            MyApp.getInstance().mRedCoinList.addAll(data);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(SendRedPacketActivity.this)) {
                        mLlData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    private void setOnClick() {
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTvAllmoney.setText(mEtCount.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @OnClick({R.id.ll_error, R.id.bark, R.id.rl_selector_currency, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.rl_selector_currency:
                showDialog();
                break;
            case R.id.btn_send:
                if (mEtCount.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
                    AnimatorTool.getInstance().editTextAnimator(mEtCount);
                }else  if (getString(R.string.please_choose).equals(mTvCurrency.getText().toString())){
                    Toast.makeText(this, getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    showPWDialog();
                }
                break;
        }
    }

    private void showPWDialog() {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                sendRed(password);
            }
        });
        String coins = mTvCurrency.getText().toString();
        String count = mEtCount.getText().toString();
        pwdDialog.showDialog(count,coins,coins + getString(R.string.red_package),logo,null);
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
                startActivity(new Intent(SendRedPacketActivity.this, PayPasswordActivity.class));
            }
        });
    }

    private void sendRed(String password) {
        mRedPacketPresenter = new RedPacketPresenter(this);
        mCoin = mTvCurrency.getText().toString();
        mCount = Double.parseDouble(mEtCount.getText().toString());
        mRemark = mEtRemark.getText().toString();
        if (mRemark.isEmpty())
            mRemark = getString(R.string.congratulation);
        int type = 1;
        int redCount = 1;
        double redSum = 0;
        mRedPacketPresenter.sendRedPacket(mUser, type, mCoin, mRemark, 1, redCount, redSum, mCount, password, new RedPacketPresenter.CallBack() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void send(int id) {
                setData(id);
            }
        });

    }

    private void showDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        RecyclerView recyclerView = (RecyclerView) mBottomDialog.findViewById(R.id.recycler_view);
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        Button addCoin = (Button) mBottomDialog.findViewById(R.id.btn_add_coin);
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendRedPacketActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.coins));
        if (MyApp.getInstance().mRedCoinList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            addCoin.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mRedCoinList));
        } else {
            recyclerView.setVisibility(View.GONE);
            addCoin.setVisibility(View.VISIBLE);
        }

    }

    public void hideDialog(String name, String logo) {
        mBottomDialog.dismiss();
        this.logo=logo;
        mTvCurrency.setText(name);
        mTvCoin.setText(name);
        Glide.with(this).load(logo).into(mImageLogo);
    }
    public void setData(int id) {
        RoomManage.getInstance().addSingleMessageManage(mUser, mMgr.findConversationName(mUser)).sendRed(mRemark, mCoin, mCount, id);
        finish();
    }
}
