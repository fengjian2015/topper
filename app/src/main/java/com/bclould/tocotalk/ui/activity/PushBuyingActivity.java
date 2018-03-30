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

import com.bclould.tocotalk.Presenter.PushBuyingPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter2;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2017/11/2.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PushBuyingActivity extends BaseActivity {

    private static final int COINSIGN = 0;
    private static final int STATESIGN = 1;
    private static final int PAYSIGN = 2;
    private static final int TIMESIGN = 4;
    private static final int BUYSELL = 3;

    String[] mCoinArr = {"TPC", "BTC", "LTC", "DOGO", "ZEC", "LSK", "MAID", "SHC", "ANS"};
    String[] mStateArr = {"中國", "美國", "日本", "瑞士", "澳洲", "馬來西亞", "韓國"};
    String[] mPayArr = {"支付寶", "微信", "銀聯", "現金"};
    String[] mTimeArr = {"5分钟", "10分钟", "20分钟", "30分钟"};
    String[] mBuySellArr = {"买", "卖"};
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_question)
    ImageView mIvQuestion;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
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
    @Bind(R.id.tv_price)
    TextView mTvPrice;
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
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.xx4)
    TextView mXx4;
    @Bind(R.id.tv_buy_sell)
    TextView mTvBuySell;
    @Bind(R.id.rl_buy_sell)
    RelativeLayout mRlBuySell;

    private Dialog mBottomDialog;
    private RecyclerView mRecyclerView;
    private BottomDialogRVAdapter mBottomDialogRVAdapter;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private MNPasswordEditText mEtPassword;
    private ArrayList<Map<String, String>> valueList;
    private GridView mGridView;
    private PushBuyingPresenter mPushBuyingPresenter;
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_buying);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.rl_buy_sell, R.id.bark, R.id.iv_question, R.id.rl_selector_currency, R.id.rl_county, R.id.rl_payment, R.id.rl_payment_time, R.id.btn_pushing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_buy_sell:
                showDialog(mBuySellArr, BUYSELL, "买卖");
                break;
            case R.id.iv_question:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.rl_selector_currency:
//                showDialog(mCoinArr, COINSIGN, getString(R.string.selected_currency));
                showCoinDialog();
                break;
            case R.id.rl_county:
                showDialog(mStateArr, STATESIGN, getString(R.string.selector_state));
                break;

            case R.id.rl_payment:
                showDialog(mPayArr, PAYSIGN, getString(R.string.selector_payment));
                break;
            case R.id.btn_pushing:
                if (checkEdit())
                    showPWDialog();
                break;
            case R.id.rl_payment_time:
                showDialog(mTimeArr, TIMESIGN, getString(R.string.selector_payment_time));
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
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        Button addCoin = (Button) mBottomDialog.findViewById(R.id.btn_add_coin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter2(this, MyApp.getInstance().mDataBeanList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PushBuyingActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText("选择币种");
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
            Toast.makeText(this, "买卖不能为空", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvBuySell);
        } else if (mTvPayment.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_legal_tender), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvPayment);
        } else if (mTvPaymentTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_payment_time), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvPaymentTime);
        } else if (mEtMinLimit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_min_limit), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMaxLimit);
        } else if (mEtMaxLimit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_max_limit), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMaxLimit);
        } else if (mEtCount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
        } else {
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
            coin.setText("发布买入" + coins + "信息");
        else
            coin.setText("发布卖出" + coins + "信息");
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
        String price = mTvPrice.getText().toString();
        String maxLimit = mEtMaxLimit.getText().toString();
        String minLimit = mEtMinLimit.getText().toString();
        String remark = mEtRemark.getText().toString();
        String count = mEtCount.getText().toString();
        String buySell = mTvBuySell.getText().toString();
        if (buySell.equals("买")) {
            mType = 1;
        } else {
            mType = 2;
        }
        mPushBuyingPresenter = new PushBuyingPresenter(this);
        mPushBuyingPresenter.pushing(mType, coin, state, price, count, paymentTime, payment, minLimit, maxLimit, remark, password);
    }

    private Map<String, Integer> mMap = new HashMap<>();

    private void showDialog(String[] arr, final int sign, String title) {
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
        if (sign == PAYSIGN)
            button.setText("确定");
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBottomDialogRVAdapter = new BottomDialogRVAdapter(this, arr, sign, new BottomDialogRVAdapter.CallBack() {
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
                if (sign == PAYSIGN) {
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
                break;
            case STATESIGN:
                mTvState.setText(name);
                break;
            case TIMESIGN:
                mTvPaymentTime.setText(name);
                break;
        }
    }

    private void setListener() {
        /*mTvCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String coin = mTvCurrency.getText().toString();
                String legalTender = mTvExchangeCurrency.getText().toString();
                if (legalTender.isEmpty()) {
                    getCoinPrice(coin, "CNY");
                } else {
                    getCoinPrice(coin, legalTender);
                }
            }
        });
        mTvExchangeCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String coin = mTvCurrency.getText().toString();
                String legalTender = mTvExchangeCurrency.getText().toString();
                if (coin.isEmpty()) {
                    getCoinPrice(coin, "CNY");
                } else {
                    getCoinPrice(coin, legalTender);
                }
            }
        });*/
    }

    public void hideDialog2(String name, int id) {
        mBottomDialog.dismiss();
        mTvCurrency.setText(name);
    }
}
