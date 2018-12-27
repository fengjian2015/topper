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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.PushBuyingPresenter;
import com.bclould.tea.Presenter.SubscribeCoinPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.ModeOfPaymentInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.CURRENCY;
import static com.bclould.tea.Presenter.LoginPresenter.STATE;
import static com.bclould.tea.Presenter.LoginPresenter.STATE_ID;
import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2017/11/2.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PushBuyingActivity extends BaseActivity {

    private static final int COINSIGN = 0;
    private static final int STATESIGN = 1;
    public static final int PAYSIGN = 2;
    public static final int PAYSIGN2 = 5;
    private static final int TIMESIGN = 4;
    private static final int BUYSELL = 3;
    List<String> mTimeList = new ArrayList<>();
    List<String> mBuySellList = new ArrayList<>();
    List<String> mPayList = new ArrayList<>();
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.xx4)
    TextView mXx4;
    @Bind(R.id.tv_buy_sell)
    TextView mTvBuySell;
    @Bind(R.id.rl_buy_sell)
    RelativeLayout mRlBuySell;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv_currency)
    TextView mTvCurrency;
    @Bind(R.id.rl_selector_currency)
    RelativeLayout mRlSelectorCurrency;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.rl_county)
    RelativeLayout mRlCounty;
    @Bind(R.id.tv8)
    TextView mTv8;
    @Bind(R.id.xx8)
    TextView mXx8;
    @Bind(R.id.tv_payment)
    TextView mTvPayment;
    @Bind(R.id.rl_payment)
    RelativeLayout mRlPayment;
    @Bind(R.id.tv11)
    TextView mTv11;
    @Bind(R.id.xx11)
    TextView mXx11;
    @Bind(R.id.et_phone_number)
    EditText mEtPhoneNumber;
    @Bind(R.id.rl_phone_number)
    RelativeLayout mRlPhoneNumber;
    @Bind(R.id.tv9)
    TextView mTv9;
    @Bind(R.id.xx9)
    TextView mXx9;
    @Bind(R.id.tv_payment_time)
    TextView mTvPaymentTime;
    @Bind(R.id.rl_payment_time)
    RelativeLayout mRlPaymentTime;
    @Bind(R.id.tv5)
    TextView mTv5;
    @Bind(R.id.xx5)
    TextView mXx5;
    @Bind(R.id.et_price)
    TextView mEtPrice;
    @Bind(R.id.tv_units)
    TextView mTvUnits;
    @Bind(R.id.rl_price)
    RelativeLayout mRlPrice;
    @Bind(R.id.tv10)
    TextView mTv10;
    @Bind(R.id.xx10)
    TextView mXx10;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_individual)
    TextView mTvIndividual;
    @Bind(R.id.rl_count)
    RelativeLayout mRlCount;
    @Bind(R.id.tv6)
    TextView mTv6;
    @Bind(R.id.xx6)
    TextView mXx6;
    @Bind(R.id.et_min_limit)
    EditText mEtMinLimit;
    @Bind(R.id.tv_units2)
    TextView mTvUnits2;
    @Bind(R.id.rl_min_limit)
    RelativeLayout mRlMinLimit;
    @Bind(R.id.tv7)
    TextView mTv7;
    @Bind(R.id.xx7)
    TextView mXx7;
    @Bind(R.id.et_max_limit)
    EditText mEtMaxLimit;
    @Bind(R.id.tv_units3)
    TextView mTvUnits3;
    @Bind(R.id.rl_max_limit)
    RelativeLayout mRlMaxLimit;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.btn_pushing)
    Button mBtnPushing;
    @Bind(R.id.rl_bottom)
    RelativeLayout mRlBottom;
    private Dialog mBottomDialog;
    private RecyclerView mRecyclerView;
    private BottomDialogRVAdapter mBottomDialogRVAdapter;
    String mCoinName = "";
    private int mType;
    private CoinPresenter mCoinPresenter;
    private PushBuyingPresenter mPushBuyingPresenter;
    private String mServiceCharge = "";
    private String mServiceCharge2 = "";
    private PWDDialog pwdDialog;
    private String logo;
    private String mState;
    private int mState_id;
    private BaseInfo.DataBean mDataBean;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_buying);
        setTitle(getString(R.string.push_ad),getString(R.string.question));
        mCoinPresenter = new CoinPresenter(this);
        mPushBuyingPresenter = new PushBuyingPresenter(this);/*
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
        }
       getModeOfPayment();
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mServiceCharge = MyApp.getInstance().mOtcCoinList.get(0).getOut_otc();
        }*/
        setData();
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void setData() {
        mTimeList.add(getString(R.string.time_bar));
        mTimeList.add(getString(R.string.time_bar2));
        mTimeList.add(getString(R.string.time_bar3));
        mTimeList.add(getString(R.string.time_bar4));
        mBuySellList.add(getString(R.string.mai));
        mBuySellList.add(getString(R.string.mai2));
        mPayList.add(getString(R.string.bank_card));
        mPayList.add(getString(R.string.alipay));
        mPayList.add(getString(R.string.we_chat));
    }

    Map<String, Boolean> mModeOfPayment = new HashMap<>();

    private void getModeOfPayment() {
        mPushBuyingPresenter.getModeOfPayment(new SubscribeCoinPresenter.CallBack2() {
            @Override
            public void send(ModeOfPaymentInfo.DataBean data) {
                mModeOfPayment.put(getString(R.string.bank_card), data.isBank());
                mModeOfPayment.put(getString(R.string.alipay), data.isAlipay());
                mModeOfPayment.put(getString(R.string.we_chat), data.isWechat());
            }
        });
    }

    private void init() {
        mCoinName = getIntent().getStringExtra("coinName");
        mServiceCharge = getIntent().getStringExtra("serviceCharge");
        mServiceCharge2 = getIntent().getStringExtra("serviceCharge2");
        mState = MySharedPreferences.getInstance().getString(STATE);
        mState_id = MySharedPreferences.getInstance().getInteger(STATE_ID);
        mTvCurrency.setText(mCoinName);
        mTvState.setText(mState);
        if (!mCoinName.isEmpty()) {
            initData(mCoinName);
        }
        /*mEtMinLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtCount.getText().toString() != null && !mEtCount.getText().toString().isEmpty()) {
                    if (mEtMinLimit.getText().toString() != null && !mEtMinLimit.getText().toString().isEmpty()) {
                        int max = Integer.parseInt(mEtMinLimit.getText().toString());
                        double price = Double.parseDouble(mTvPrice.getText().toString());
                        double count = Double.parseDouble(mEtCount.getText().toString());
                        int sum = (int) (price * count);
                        if (max > sum) {
                            mEtMinLimit.setText(sum + "");
                        }
                    } else {
                        mEtMinLimit.setText("");
                    }
                } else {
                    Toast.makeText(PushBuyingActivity.this, "请先输入数量", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mEtMaxLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtCount.getText().toString() != null && !mEtCount.getText().toString().isEmpty()) {
                    if (mEtMaxLimit.getText().toString() != null && !mEtMaxLimit.getText().toString().isEmpty()) {
                        int max = Integer.parseInt(mEtMaxLimit.getText().toString());
                        double price = Double.parseDouble(mTvPrice.getText().toString());
                        double count = Double.parseDouble(mEtCount.getText().toString());
                        int sum = (int) (price * count);
                        if (max > sum) {
                            mEtMaxLimit.setText(sum + "");
                        }
                    } else {
                        mEtMaxLimit.setText("");
                    }
                } else {
                    Toast.makeText(PushBuyingActivity.this, "请先输入数量", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void initData(String name) {
        mCoinPresenter.getCoinPrice(name, new CoinPresenter.CallBack2() {
            @Override
            public void send(BaseInfo.DataBean data) {
                if (ActivityUtil.isActivityOnTop(PushBuyingActivity.this)) {
                    mDataBean=data;
                    mEtPrice.setText("");
                    mTvUnits.setText(data.getCurrency());
                    mTvUnits2.setText(data.getCurrency());
                    mTvUnits3.setText(data.getCurrency());
                }
            }

            @Override
            public void error() {

            }
        });

    }

    @OnClick({R.id.rl_buy_sell, R.id.bark, R.id.tv_add, R.id.rl_selector_currency, R.id.rl_county, R.id.rl_payment, R.id.rl_payment_time, R.id.btn_pushing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_buy_sell:
                showDialog(mModeOfPayment, mBuySellList, BUYSELL, getString(R.string.buysell));
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.rl_selector_currency:
//                showCoinDialog();
                break;
            case R.id.rl_county:
                break;
            case R.id.rl_payment:
                /*String buySell = mTvBuySell.getText().toString();
                if (buySell.isEmpty()) {
                    Toast.makeText(this, "请先选择买卖", Toast.LENGTH_SHORT).show();
                } else {
                    if (buySell.equals("买")) {
                        showDialog(mModeOfPayment, mPayArr, PAYSIGN, getString(R.string.selector_payment));
                    } else {
                        showDialog(mModeOfPayment, mPayArr, PAYSIGN2, getString(R.string.selector_payment));
                    }
                }*/
                break;
            case R.id.btn_pushing:
                if (checkEdit())
                    showPWDialog();
                break;
            case R.id.rl_payment_time:
                showDialog(mModeOfPayment, mTimeList, TIMESIGN, getString(R.string.selector_payment_time));
                break;
        }
    }

   /* private void showCoinDialog() {
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
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        Button addCoin = (Button) mBottomDialog.findViewById(R.id.btn_add_coin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mOtcCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PushBuyingActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
    }*/

    //验证手机号和密码
    private boolean checkEdit() {

        if (mTvBuySell.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_buy_sell), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvBuySell);
        } else if (mTvState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_state), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvState);
        } else if (mTvCurrency.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvCurrency);
        } else if (mEtPhoneNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_phone), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlPhoneNumber);
        } else if (mTvPayment.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_legal_tender), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvPayment);
        } else if (mTvPaymentTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_payment_time), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvPaymentTime);
        } else if (mEtCount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
        } else if (mEtMinLimit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_min_limit), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMinLimit);
        } else if (mEtMaxLimit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_max_limit), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMaxLimit);
        } else if (mEtPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_price), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPrice);
        } /*else if (Double.parseDouble(mEtMinLimit.getText().toString()) < 100) {
            Toast.makeText(this, "最小量不能小于100", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMinLimit);
        } else if (Double.parseDouble(mEtMaxLimit.getText().toString()) < 100) {
            Toast.makeText(this, "最大量不能小于100", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMaxLimit);
        } else if (Double.parseDouble(mEtMaxLimit.getText().toString()) < Double.parseDouble(mEtMinLimit.getText().toString())) {
            Toast.makeText(this, "最大量不能小于最小量", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMaxLimit);
        }*/ else {
            return true;
        }
        return false;
    }

    private void showPWDialog() {
        pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                pushing(password);
            }
        });
        String coins = mTvCurrency.getText().toString();
        String count = mEtCount.getText().toString();
        String desc = "";
        if (mType == 1)
            desc = getString(R.string.push_buy) + coins + getString(R.string.msg);
        else
            desc = getString(R.string.push_sell) + coins + getString(R.string.msg);
        pwdDialog.showDialog(count, coins, desc, logo, null);
    }

    private void pushing(String password) {
        String coin = mTvCurrency.getText().toString();
        String payment = mTvPayment.getText().toString();
        String paymentTime = mTvPaymentTime.getText().toString();
        String price = mEtPrice.getText().toString();
        String maxLimit = mEtMaxLimit.getText().toString();
        String minLimit = mEtMinLimit.getText().toString();
        String remark = mEtRemark.getText().toString();
        String phoneNumber = mEtPhoneNumber.getText().toString();
        if (remark == null) {
            remark = "";
        }
        String count = mEtCount.getText().toString();
        String buySell = mTvBuySell.getText().toString();
        if (buySell.equals(getString(R.string.mai))) {
            mType = 1;
        } else {
            mType = 2;
        }
        mPushBuyingPresenter.pushing(mType, coin, mState_id, price, count, paymentTime, payment, minLimit, maxLimit, remark, password, phoneNumber);
    }

    private Map<String, Integer> mMap = new HashMap<>();

    private void showDialog(Map<String, Boolean> modeOfPayment, List<String> list, final int sign, String title) {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom2, null);
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
        mRecyclerView = (RecyclerView) mBottomDialog.findViewById(R.id.recycler_view);
        final Button button = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        if (sign == PAYSIGN || sign == PAYSIGN2)
            button.setText(getString(R.string.confirm));
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBottomDialogRVAdapter = new BottomDialogRVAdapter(this, modeOfPayment, list, sign, new BottomDialogRVAdapter.CallBack() {
            @Override
            public void send(String name, boolean isChecked, int position) {
                if (isChecked) {
                    mMap.put(name, position);
                } else {
                    mMap.remove(name);
                }
            }
        }, mMap);

        mRecyclerView.setAdapter(mBottomDialogRVAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sign == PAYSIGN || sign == PAYSIGN2) {
                    String payWay = null;
                    for (String key : mMap.keySet()) {
                        if (payWay == null) {
                            payWay = key;
                        } else {
                            payWay = payWay + ", " + key;
                        }
                    }
                    mTvPayment.setText(payWay);
                    mBottomDialog.dismiss();
                } else {
                    mBottomDialog.dismiss();
                }
            }
        });
    }

    public void hideDialog(String name, int sign) {
        mBottomDialog.dismiss();
        switch (sign) {
            case COINSIGN:
                mTvCurrency.setText(name);
                break;
            case BUYSELL:
                mTvBuySell.setText(name);
                if (name.equals(getString(R.string.mai2))) {
                    mEtPrice.setText(mDataBean.getSale_price());
                    mTvHint.setText(getString(R.string.push_ad_hint) + Double.parseDouble(mServiceCharge) * 100 + "%" + getString(R.string.sxf));
                } else {
                    mEtPrice.setText(mDataBean.getBuy_price());
                    mTvHint.setText(getString(R.string.push_ad_hint2) + Double.parseDouble(mServiceCharge2) * 100 + "%" + getString(R.string.sxf));
                }
                /*mTvPayment.setText("");
                mMap.clear();*/
                break;
            case STATESIGN:
                mTvState.setText(name);
                break;
            case TIMESIGN:
                mTvPaymentTime.setText(name);
                break;
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
                startActivity(new Intent(PushBuyingActivity.this, PayPasswordActivity.class));
            }
        });
    }


    public void hideDialog2(String name, int id, String serviceCharge, String logo) {
        mBottomDialog.dismiss();
        this.logo = logo;
        mCoinName = name;
        initData(name);
        UtilTool.Log("手續費", serviceCharge);
        serviceCharge = Double.parseDouble(serviceCharge) * 100 + "";
        mServiceCharge = serviceCharge;
        mTvHint.setText(getString(R.string.push_ad_hint) + serviceCharge + "%" + getString(R.string.sxf));
        mEtMaxLimit.setText("");
        mEtMinLimit.setText("");
        mTvCurrency.setText(name);
    }
}
