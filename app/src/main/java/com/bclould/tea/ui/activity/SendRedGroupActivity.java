package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SendRedGroupActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_currency)
    TextView mTvCurrency;
    @Bind(R.id.et_money)
    EditText mEtMoney;
    @Bind(R.id.tv_change_red)
    TextView mTvChangeRed;
    @Bind(R.id.et_number)
    EditText mEtNumber;
    @Bind(R.id.tv_group_number)
    TextView mTvGroupNumber;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.tv_money_state)
    TextView mTvMoneyState;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.tv_allmoney)
    TextView mTvAllmoney;
    @Bind(R.id.image_logo)
    ImageView mImageLogo;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;

    private Dialog mBottomDialog;
    private PWDDialog pwdDialog;

    private DBManager mMgr;
    private String roomId;
    private boolean isluckyRed = true;
    private String mRemark;
    private double mCount;
    private double singleMoney;
    private String mCoin;
    private DBRoomMember mDBRoomMember;
    private String logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket5));
        setContentView(R.layout.activity_send_red_group);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        mDBRoomMember = new DBRoomMember(this);
        MyApp.getInstance().addActivity(this);
        init();
        initData();
        setOnClick();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void setOnClick() {
        mEtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeAllMoney();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeAllMoney();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void changeAllMoney(){
        if (isluckyRed) {
            mCount =UtilTool.parseDouble(mEtMoney.getText().toString());
        } else {
            mCount =UtilTool.parseDouble(mEtMoney.getText().toString()) *UtilTool.parseDouble(mEtNumber.getText().toString());
        }
        mTvAllmoney.setText(UtilTool.removeZero(mCount + ""));
    }

    private void init() {
        roomId = getIntent().getStringExtra("roomId");
        mTvGroupNumber.setText(getString(R.string.members_of_the_group) + mDBRoomMember.queryAllRequest(roomId).size() + getString(R.string.person1));
        mEtMoney.setSelection(mEtMoney.getText().length());
        mEtNumber.setSelection(mEtNumber.getText().length());
        changeRed();
    }

    private void changeRed() {

        if (isluckyRed) {
            CharSequence str = getText(R.string.lucky_red_common_red);
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    isluckyRed = false;
                    changeRed();
                    changeAllMoney();
                }
            }, str.length() - 6, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue2)), str.length() - 6, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvChangeRed.setText(spannableString);
            mTvChangeRed.setMovementMethod(LinkMovementMethod.getInstance());
            mTvMoneyState.setText(getString(R.string.total_money));
        } else {
            CharSequence str = getText(R.string.common_red_lucky_red);
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    isluckyRed = true;
                    changeRed();
                    changeAllMoney();
                }
            }, str.length() - 7, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue2)), str.length() - 7, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvChangeRed.setText(spannableString);
            mTvChangeRed.setMovementMethod(LinkMovementMethod.getInstance());
            mTvMoneyState.setText(getString(R.string.a_single_amount));
        }

    }

    private void initData() {
        MyApp.getInstance().mRedCoinList.clear();
        if (MyApp.getInstance().mRedCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("red_packet", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (ActivityUtil.isActivityOnTop(SendRedGroupActivity.this)) {
                        mLlError.setVisibility(View.GONE);
                        mLlData.setVisibility(View.VISIBLE);
                        if (MyApp.getInstance().mRedCoinList.size() == 0)
                            MyApp.getInstance().mRedCoinList.addAll(data);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(SendRedGroupActivity.this)) {
                        mLlError.setVisibility(View.VISIBLE);
                        mLlData.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @OnClick({R.id.bark, R.id.rl_selector_currency, R.id.btn_send,R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_selector_currency:
                showDialog();
                break;
            case R.id.btn_send:
                sendGroupRed();
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }

    private void sendGroupRed() {
        int number = UtilTool.parseInt(mEtNumber.getText().toString());
        double money = UtilTool.parseDouble(mEtMoney.getText().toString());
        if (number == 0 || money == 0) {
            Toast.makeText(this, getString(R.string.toast_count_and_money), Toast.LENGTH_SHORT).show();
            return;
        }
        if (number > 100) {
            Toast.makeText(this, getString(R.string.group_red_max_out), Toast.LENGTH_SHORT).show();
            return;
        }
        if (number > mDBRoomMember.queryAllRequest(roomId).size()) {
            Toast.makeText(this, getString(R.string.group_red_max_out_member), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isluckyRed && money > 200) {
            Toast.makeText(this, getString(R.string.group_red_max_money), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isluckyRed && (number * 0.01) > money) {
            Toast.makeText(this, getString(R.string.group_red_min_money), Toast.LENGTH_SHORT).show();
            return;
        }
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                sendRed(password);
            }
        });
        String coins = mTvCurrency.getText().toString();
        if (isluckyRed) {
            mCount = Double.parseDouble(mEtMoney.getText().toString());
        } else {
            mCount = Double.parseDouble(mEtMoney.getText().toString()) * Double.parseDouble(mEtNumber.getText().toString());
        }
        pwdDialog.showDialog(UtilTool.removeZero(mCount + ""),coins,coins+getString(R.string.red_package),logo,null);
    }

    private void sendRed(String password) {
        int type;
        if (isluckyRed) {
            mCount = Double.parseDouble(mEtMoney.getText().toString());
            type = 3;
        } else {
            mCount = Double.parseDouble(mEtMoney.getText().toString()) * Double.parseDouble(mEtNumber.getText().toString());
            singleMoney = Double.parseDouble(mEtMoney.getText().toString());
            type = 2;
        }
        int redCount = Integer.parseInt(mEtNumber.getText().toString());
        mCoin = mTvCurrency.getText().toString();
        mRemark = mEtRemark.getText().toString();
        if (mRemark.isEmpty())
            mRemark = getString(R.string.congratulation);
        new RedPacketPresenter(this).sendRedPacket(roomId, type, mCoin, mRemark, 1, redCount, singleMoney, mCount, password, new RedPacketPresenter.CallBack() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void send(int id) {
                setData(id);
            }
        });
    }

    public void setData(int id) {
        RoomManage.getInstance().addMultiMessageManage(roomId, mMgr.findConversationName(roomId)).sendRed(mRemark, mCoin, mCount, id);
        finish();
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
                startActivity(new Intent(SendRedGroupActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
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
                startActivity(new Intent(SendRedGroupActivity.this, PayPasswordActivity.class));
            }
        });
    }

    public void hideDialog(String name, String logo) {
        mBottomDialog.dismiss();
        mTvCurrency.setText(name);
        mTvCoin.setText(name);
        Glide.with(this).load(logo).into(mImageLogo);
        this.logo=logo;
    }

}
