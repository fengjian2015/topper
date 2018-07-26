package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.Presenter.ReceiptPaymentPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.ReceiptInfo;
import com.bclould.tea.model.UserDataInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

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
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.tv_give)
    TextView mTvGive;
    private String mUserId;
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;
    private Dialog mBottomDialog;
    private int mId;
    private String mType;
    private String mCoinName;
    private String mCoinId;
    private String mCoinNames;
    private String mNumber;
    private String mMark;
    private PersonalDetailsPresenter mPersonalDetailsPresenter;
    private String mCode;
    private String mAvatar;
    private PWDDialog pwdDialog;
    private String logo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mPersonalDetailsPresenter = new PersonalDetailsPresenter(this);
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
                    if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                        mLlNoSteadfast.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                        if (MyApp.getInstance().mPayCoinList.size() == 0)
                            MyApp.getInstance().mPayCoinList.addAll(data);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                        mLlNoSteadfast.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void initIntent() {
        mType = getIntent().getStringExtra("type");
        if (mType.equals(Constants.MONEYIN)) {//無數據用戶支付
            initData();
            mCvWho.setVisibility(View.VISIBLE);
            mUserId = getIntent().getStringExtra("userId");
            final String username = getIntent().getStringExtra("username");
            mPersonalDetailsPresenter.getUserData(Integer.parseInt(mUserId), new PersonalDetailsPresenter.CallBack4() {
                @Override
                public void send(UserDataInfo.DataBean dataBean) {
                    if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                        UtilTool.setCircleImg(PaymentActivity.this, dataBean.getAvatar(), mIvTouxiang);
                        mTvName.setText(username);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                        UtilTool.setCircleImg(PaymentActivity.this, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
                    }
                }
            });
        } else if (mType.equals(Constants.MONEYOUT)) {//生成付款碼
            mCvWho.setVisibility(View.GONE);
            initData();
            mTvTitle.setText(getString(R.string.create_fk_code));
            mBtnPayment.setText(getString(R.string.confirm));
        } else if (mType.equals(Constants.QRMONEYIN)) {//生成收款碼
            mCvWho.setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.create_sk_code));
            initData();
            mBtnPayment.setText(getString(R.string.confirm));
        } else if (mType.equals(Constants.COMMERCIAL_TENANT_RECOGNITION_SYMBOL)) {//商戶支付
            mLlNoSteadfast.setVisibility(View.VISIBLE);
            mCode = getIntent().getStringExtra("username");
            mLlError.setVisibility(View.GONE);
            mTvGive.setText(getString(R.string.payment_give) + getString(R.string.merchant));
            mCvWho.setVisibility(View.VISIBLE);
            mRlSelectorCoin.setEnabled(false);
            getMerchantUser();
        } else {//有數據用戶支付
            mLlNoSteadfast.setVisibility(View.VISIBLE);
            mLlError.setVisibility(View.GONE);
            mCvWho.setVisibility(View.VISIBLE);
            mUserId = getIntent().getStringExtra("userId");
            mCoinId = getIntent().getStringExtra("coinId");
            mCoinNames = getIntent().getStringExtra("coinName");
            mNumber = getIntent().getStringExtra("number");
            mMark = getIntent().getStringExtra("mark");
            final String username = getIntent().getStringExtra("username");
            mPersonalDetailsPresenter.getUserData(Integer.parseInt(mUserId), new PersonalDetailsPresenter.CallBack4() {
                @Override
                public void send(UserDataInfo.DataBean dataBean) {
                    if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                        UtilTool.setCircleImg(PaymentActivity.this, dataBean.getAvatar(), mIvTouxiang);
                        mTvName.setText(username);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                        UtilTool.setCircleImg(PaymentActivity.this, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
                    }
                }
            });
            mTvCoin.setText(mCoinNames);
            mRlSelectorCoin.setEnabled(false);
            mEtCount.setText(mNumber);
            mEtCount.setKeyListener(null);
            mEtRemark.setText(mMark);
            mEtRemark.setKeyListener(null);
        }
    }

    private void getMerchantUser() {
        mPersonalDetailsPresenter.getMerchantUser(mCode, new PersonalDetailsPresenter.CallBack4() {
            @Override
            public void send(UserDataInfo.DataBean dataBean) {
                if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                    mAvatar = dataBean.getAvatar();
                    if (!mAvatar.isEmpty()) {
                        UtilTool.setCircleImg(PaymentActivity.this, mAvatar, mIvTouxiang);
                    } else {
                        UtilTool.setCircleImg(PaymentActivity.this, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
                    }
                    mTvName.setText(dataBean.getMerchant_name());
                    mTvCoin.setText(dataBean.getCoin_name());
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(PaymentActivity.this)) {
                    mLlNoSteadfast.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.ll_error, R.id.bark, R.id.btn_payment, R.id.rl_selector_coin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                if (mType.equals(Constants.COMMERCIAL_TENANT_RECOGNITION_SYMBOL)) {
                    getMerchantUser();
                } else {
                    initData();
                }
                break;
            case R.id.btn_payment:
                if (mType.equals(Constants.DATAMONEYIN)) {
                    showPWDialog();
                } else if (mType.equals(Constants.MONEYIN) || mType.equals(Constants.MONEYOUT)) {
                    if (checkEidt()) {
                        showPWDialog();
                    }
                } else if (mType.equals(Constants.COMMERCIAL_TENANT_RECOGNITION_SYMBOL)) {
                    if (checkEidt()) {
                        showPWDialog();
                    }
                } else {
                    createReceiptQrCode();
                }
                break;
            case R.id.rl_selector_coin:
                showCoinDialog();
                break;
        }
    }

    private void showPWDialog() {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                if (mType.equals(Constants.MONEYIN)) {
                    payment(password);
                } else if (mType.equals(Constants.MONEYOUT)) {
                    createQrCode(password);
                } else if (mType.equals(Constants.DATAMONEYIN)) {
                    payment2(password);
                } else if (mType.equals(Constants.COMMERCIAL_TENANT_RECOGNITION_SYMBOL)) {
                    paymentMerchant(password);
                }
            }
        });
        String desc="";
        if (mType.equals(Constants.MONEYIN)) {
            desc=getString(R.string.scan_qr_code_pay);
        } else if (mType.equals(Constants.MONEYOUT)) {
            desc=getString(R.string.create_fk_code);
        } else if (mType.equals(Constants.QRMONEYIN)) {
            desc=getString(R.string.create_sk_code);
        } else {
            desc=getString(R.string.scan_qr_code_pay);
        }
        String count = mEtCount.getText().toString();
        String coinName = mTvCoin.getText().toString();
        pwdDialog.showDialog(count,coinName,desc,logo,null);
    }

    //支付給商家
    private void paymentMerchant(String password) {
        final String number = mEtCount.getText().toString().trim();
        String remark = mEtRemark.getText().toString();
        final String coin = mTvCoin.getText().toString().trim();
        final String name = mTvName.getText().toString().trim();
        mReceiptPaymentPresenter.payMerchant(mCode, number, remark, password, new PersonalDetailsPresenter.CallBack() {
            @Override
            public void send() {
                Intent intent = new Intent(PaymentActivity.this, PayReceiptResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("coinName", coin);
                bundle.putString("name", name);
                bundle.putString("number", number);
                bundle.putString("type", Constants.MONEYIN);
                bundle.putString("avatar", mAvatar);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(PaymentActivity.this, getString(R.string.fk_succeed), Toast.LENGTH_SHORT).show();
                finish();
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

            @Override
            public void error() {

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
                pwdDialog.show();
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

    public void hideDialog(String name, int id, String logo) {
        mId = id;
        this.logo=logo;
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
