package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.ReceiptPaymentPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.ReceiptInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/3/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PaymentActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.iv_jiantou)
    ImageView mIvJiantou;
    @Bind(R.id.rl_selector_coin)
    RelativeLayout mRlSelectorCoin;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.ll_no_steadfast)
    LinearLayout mLlNoSteadfast;
    @Bind(R.id.btn_payment)
    Button mBtnPayment;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.cv_who)
    CardView mCvWho;
    private String mUserId;
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;
    private Dialog mBottomDialog;
    private int mId;
    private String mType;
    private String mCoinName;
    private ArrayList<Map<String, String>> valueList;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private GridView mGridView;
    private MNPasswordEditText mEtPassword;
    private String mCoinId;
    private String mCoinNames;
    private String mNumber;
    private String mMark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initData();
        initIntent();
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
    }

    private void initData() {
        MyApp.getInstance().mPayCoinList.clear();
        if (MyApp.getInstance().mPayCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("pay", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (MyApp.getInstance().mPayCoinList.size() == 0)
                        MyApp.getInstance().mPayCoinList.addAll(data);
                }
            });
        }
    }


    private void initIntent() {
        DBManager mgr = new DBManager(this);
        mType = getIntent().getStringExtra("type");
        if (mType.equals(Constants.MONEYIN)) {
            mCvWho.setVisibility(View.VISIBLE);
            mUserId = getIntent().getStringExtra("userId");
            String username = getIntent().getStringExtra("username");
            UtilTool.getImage(mgr, username, MyApp.getInstance().app(), mIvTouxiang);
            mTvName.setText(username);
        } else if (mType.equals(Constants.MONEYOUT)) {
            mCvWho.setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.create_fk_code));
            mBtnPayment.setText(getString(R.string.confirm));
        } else if (mType.equals(Constants.QRMONEYIN)) {
            mCvWho.setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.create_sk_code));
            mBtnPayment.setText(getString(R.string.confirm));
        } else {
            mCvWho.setVisibility(View.VISIBLE);
            mUserId = getIntent().getStringExtra("userId");
            mCoinId = getIntent().getStringExtra("coinId");
            mCoinNames = getIntent().getStringExtra("coinName");
            mNumber = getIntent().getStringExtra("number");
            mMark = getIntent().getStringExtra("mark");
            String username = getIntent().getStringExtra("username");
            mTvName.setText(username);
            mTvCoin.setText(mCoinNames);
            mEtCount.setText(mNumber);
            mEtCount.setKeyListener(null);
            mEtRemark.setText(mMark);
            mEtRemark.setKeyListener(null);
            UtilTool.getImage(mgr, username, MyApp.getInstance().app(), mIvTouxiang);
        }
    }

    @OnClick({R.id.bark, R.id.btn_payment, R.id.rl_selector_coin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_payment:
                if (mType.equals(Constants.DATAMONEYIN)) {
                    showPWDialog();
                } else if (checkEidt()) {
                    if (mType.equals(Constants.MONEYIN) || mType.equals(Constants.MONEYOUT)) {
                        showPWDialog();
                    } else {
                        createReceiptQrCode();
                    }
                }
                break;
            case R.id.rl_selector_coin:
                showCoinDialog();
                break;
        }
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
        if (mType.equals(Constants.MONEYIN)) {
            coin.setText(getString(R.string.scan_qr_code_pay));
        } else if (mType.equals(Constants.MONEYOUT)) {
            coin.setText(getString(R.string.create_fk_code));
        } else if (mType.equals(Constants.QRMONEYIN)) {
            coin.setText(getString(R.string.create_sk_code));
        } else {
            coin.setText(getString(R.string.scan_qr_code_pay));
        }
        String count = mEtCount.getText().toString();
        String coinName = mTvCoin.getText().toString();
        countCoin.setText(count + coinName);
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
                    if (mType.equals(Constants.MONEYIN)) {
                        payment(password);
                    } else if (mType.equals(Constants.MONEYOUT)) {
                        createQrCode(password);
                    } else if (mType.equals(Constants.DATAMONEYIN)) {
                        payment2(password);
                    }
                }
            }
        });
    }

    private void payment2(String password) {
        mReceiptPaymentPresenter.payment(mUserId, mNumber, Integer.parseInt(mCoinId), password, new ReceiptPaymentPresenter.CallBack2() {
            @Override
            public void send(ReceiptInfo.DataBean data) {
                Intent intent = new Intent(PaymentActivity.this, PayReceiptResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("coinName", data.getCoin_name());
                bundle.putString("date", data.getDate());
                bundle.putString("name", data.getName());
                bundle.putString("number", data.getNumber());
                bundle.putString("type", Constants.MONEYIN);
                bundle.putString("avatar", data.getAvatar());
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(PaymentActivity.this, getString(R.string.fk_succeed), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void createReceiptQrCode() {
        String count = mEtCount.getText().toString();
        String remark = mEtRemark.getText().toString();
        mReceiptPaymentPresenter.generateReceiptQrCode(mId + "", count, remark, new ReceiptPaymentPresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                Intent intent = new Intent(PaymentActivity.this, ReceiptPaymentActivity.class);
                intent.putExtra("coinId", data.getCoin_id());
                intent.putExtra("coinName", data.getCoin_name());
                intent.putExtra("count", data.getNumber());
                intent.putExtra("id", data.getId() + "");
                intent.putExtra("remark", data.getRemark());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
                mRedDialog.show();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(PaymentActivity.this, PayPasswordActivity.class));
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

    private void createQrCode(String password) {
        final String count = mEtCount.getText().toString();
        final String remark = mEtRemark.getText().toString();
        mReceiptPaymentPresenter.generatePaymentQrCode(count, mId, password, new ReceiptPaymentPresenter.CallBack3() {
            @Override
            public void send(String url) {
                Intent intent = new Intent(PaymentActivity.this, ReceiptPaymentActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("coinName", mCoinName);
                intent.putExtra("remark", remark);
                intent.putExtra("count", count);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mPayCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
    }

    public void hideDialog(String name, int id) {
        mId = id;
        mCoinName = name;
        mBottomDialog.dismiss();
        mTvCoin.setText(name);
    }

    private boolean checkEidt() {
        if (mEtCount.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
        } else if (mTvCoin.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorCoin);
            Toast.makeText(this, getString(R.string.selector_coin), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void payment(String password) {
        String count = mEtCount.getText().toString();
        mReceiptPaymentPresenter.payment(mUserId, count, mId, password, new ReceiptPaymentPresenter.CallBack2() {
            @Override
            public void send(ReceiptInfo.DataBean data) {
                Intent intent = new Intent(PaymentActivity.this, PayReceiptResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("coinName", data.getCoin_name());
                bundle.putString("date", data.getDate());
                bundle.putString("name", data.getName());
                bundle.putString("number", data.getNumber());
                bundle.putString("type", Constants.MONEYIN);
                bundle.putString("avatar", data.getAvatar());
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(PaymentActivity.this, getString(R.string.fk_succeed), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
