package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CoinPresenter;
import com.bclould.tocotalk.Presenter.PushBuyingPresenter;
import com.bclould.tocotalk.Presenter.SubscribeCoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.ModeOfPaymentInfo;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;
import static com.bclould.tocotalk.R.style.BottomDialog;

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
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_question)
    ImageView mIvQuestion;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
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
    EditText mEtPrice;
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
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private MNPasswordEditText mEtPassword;
    private ArrayList<Map<String, String>> valueList;
    private GridView mGridView;
    String mCoinName = "";
    private int mType;
    private CoinPresenter mCoinPresenter;
    private PushBuyingPresenter mPushBuyingPresenter;
    private String mServiceCharge = MyApp.getInstance().mOtcCoinList.get(0).getOut_otc();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_buying);
        mCoinPresenter = new CoinPresenter(this);
        mPushBuyingPresenter = new PushBuyingPresenter(this);
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
        }
        setData();
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        init();
        if (!mCoinName.isEmpty()) {
            initData(mCoinName);
        }
        mTvHint.setText(getString(R.string.push_ad_hint) + mServiceCharge + "%" + getString(R.string.sxf));
//        getModeOfPayment();
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
        mTvCurrency.setText(mCoinName);
        mTvState.setText(MySharedPreferences.getInstance().getString(STATE));
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
                try {
                    double usdt = Double.parseDouble(data.getUSDT());
                    double cny = Double.parseDouble(data.getRate());
                    DecimalFormat df = new DecimalFormat("0.00");
                    String price = df.format(cny * usdt);
                    mEtPrice.setHint(getString(R.string.reference_value) + price);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @OnClick({R.id.rl_buy_sell, R.id.bark, R.id.iv_question, R.id.rl_selector_currency, R.id.rl_county, R.id.rl_payment, R.id.rl_payment_time, R.id.btn_pushing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_buy_sell:
                showDialog(mModeOfPayment, mBuySellList, BUYSELL, getString(R.string.buysell));
                break;
            case R.id.iv_question:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.rl_selector_currency:
                showCoinDialog();
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

    private void showCoinDialog() {
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
    }

    //验证手机号和密码
    private boolean checkEdit() {

        if (mTvCurrency.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvCurrency);
        } else if (mTvState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_state), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvState);
        } else if (mTvBuySell.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_buy_sell), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvBuySell);
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
        }else if (mEtPrice.getText().toString().trim().isEmpty()) {
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
        mEnterAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_enter);
        mExitAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_exit);
        mRedDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_passwrod, null);
        //获得dialog的window窗口
        Window window = mRedDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(BottomDialog);
        mRedDialog.setContentView(contentView);
        mRedDialog.show();
        mRedDialog.setCanceledOnTouchOutside(false);
        initDialog();
    }

    private void initDialog() {
        String coins = mTvCurrency.getText().toString();
        String count = mEtCount.getText().toString();
        TextView coin = (TextView) mRedDialog.findViewById(R.id.tv_coin);
        TextView countCoin = (TextView) mRedDialog.findViewById(R.id.tv_count_coin);
        mEtPassword = (MNPasswordEditText) mRedDialog.findViewById(R.id.et_password);
        // 设置不调用系统键盘
        if (Build.VERSION.SDK_INT <= 10) {
            mEtPassword.setInputType(InputType.TYPE_NULL);
        } else {
            this.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEtPassword, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final VirtualKeyboardView virtualKeyboardView = (VirtualKeyboardView) mRedDialog.findViewById(R.id.virtualKeyboardView);
        ImageView bark = (ImageView) mRedDialog.findViewById(R.id.bark);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRedDialog.dismiss();
                mEtPassword.setText("");
            }
        });
        valueList = virtualKeyboardView.getValueList();
        countCoin.setText(count + coins);
        if (mType == 1)
            coin.setText(getString(R.string.push_buy) + coins + getString(R.string.msg));
        else
            coin.setText(getString(R.string.push_sell) + coins + getString(R.string.msg));
        virtualKeyboardView.getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.startAnimation(mExitAnim);
                virtualKeyboardView.setVisibility(View.GONE);
            }
        });
        mGridView = virtualKeyboardView.getGridView();
        mGridView.setOnItemClickListener(onItemClickListener);
        mEtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.setFocusable(true);
                virtualKeyboardView.setFocusableInTouchMode(true);
                virtualKeyboardView.startAnimation(mEnterAnim);
                virtualKeyboardView.setVisibility(View.VISIBLE);
            }
        });

        mEtPassword.setOnPasswordChangeListener(new MNPasswordEditText.OnPasswordChangeListener() {
            @Override
            public void onPasswordChange(String password) {
                if (password.length() == 6) {
                    mRedDialog.dismiss();
                    mEtPassword.setText("");
                    pushing(password);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            if (position < 11 && position != 9) {    //点击0~9按钮

                String amount = mEtPassword.getText().toString().trim();
                amount = amount + valueList.get(position).get("name");

                mEtPassword.setText(amount);

                Editable ea = mEtPassword.getText();
                mEtPassword.setSelection(ea.length());
            } else {

                if (position == 9) {      //点击退格键
                    String amount = mEtPassword.getText().toString().trim();
                    if (!amount.contains(".")) {
                        amount = amount + valueList.get(position).get("name");
                        mEtPassword.setText(amount);

                        Editable ea = mEtPassword.getText();
                        mEtPassword.setSelection(ea.length());
                    }
                }

                if (position == 11) {      //点击退格键
                    String amount = mEtPassword.getText().toString().trim();
                    if (amount.length() > 0) {
                        amount = amount.substring(0, amount.length() - 1);
                        mEtPassword.setText(amount);

                        Editable ea = mEtPassword.getText();
                        mEtPassword.setSelection(ea.length());
                    }
                }
            }
        }
    };

    private void pushing(String password) {
        String coin = mTvCurrency.getText().toString();
        String state = mTvState.getText().toString();
        String payment = mTvPayment.getText().toString();
        String paymentTime = mTvPaymentTime.getText().toString();
        String price = mEtPrice.getText().toString();
        String maxLimit = mEtMaxLimit.getText().toString();
        String minLimit = mEtMinLimit.getText().toString();
        String remark = mEtRemark.getText().toString();
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

        mPushBuyingPresenter.pushing(mType, coin, state, price, count, paymentTime, payment, minLimit, maxLimit, remark, password);
    }

    private Map<String, Integer> mMap = new HashMap<>();

    private void showDialog(Map<String, Boolean> modeOfPayment, List<String> list, final int sign, String title) {
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
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        TextView retry = (TextView) deleteCacheDialog.findViewById(R.id.tv_retry);
        TextView findPassword = (TextView) deleteCacheDialog.findViewById(R.id.tv_find_password);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mRedDialog.show();
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


    public void hideDialog2(String name, int id) {
        mBottomDialog.dismiss();
        mCoinName = name;
        initData(name);
        mTvHint.setText(getString(R.string.push_ad_hint) + mServiceCharge + "%" + getString(R.string.sxf));
//        mEtMaxLimit.setText("");
//        mEtMinLimit.setText("");
        mTvCurrency.setText(name);
    }
}
