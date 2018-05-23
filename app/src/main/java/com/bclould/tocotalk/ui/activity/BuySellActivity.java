package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.BuySellPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.DealListInfo;
import com.bclould.tocotalk.model.OrderInfo;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/1/11.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BuySellActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_help)
    TextView mTvHelp;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_reputation)
    TextView mTvReputation;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_quota)
    TextView mTvQuota;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.tv_buysell_count)
    TextView mTvBuysellCount;
    @Bind(R.id.tv_cny_hint)
    TextView mTvCnyHint;
    @Bind(R.id.tv_coin_hint)
    TextView mTvCoinHint;
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.et_cny)
    EditText mEtCny;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.et_coin)
    EditText mEtCoin;
    @Bind(R.id.btn_sell_buy)
    Button mBtnSellBuy;
    private boolean mType;
    private DealListInfo.DataBean mData;
    private double mPrice;
    private int mId;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private MNPasswordEditText mEtPassword;
    private ArrayList<Map<String, String>> valueList;
    private GridView mGridView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_sell);
        ButterKnife.bind(this);
        initInterface();
        setListener();
    }

    private void setListener() {
        mEtCny.setFilters(new InputFilter[]{lengthFilter});
        mEtCoin.setFilters(new InputFilter[]{lengthFilter2});
        mEtCoin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mEtCoin.addTextChangedListener(mTextWatcher);
                } else {
                    mEtCoin.removeTextChangedListener(mTextWatcher);
                    mTvCoinHint.setText("");
                }
            }
        });

        mEtCny.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mEtCny.addTextChangedListener(mTextWatcher2);
                } else {
                    mEtCny.removeTextChangedListener(mTextWatcher2);
                    mTvCnyHint.setText("");
                }
            }
        });
        mEtCoin.requestFocus();
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String s = mEtCoin.getText().toString();
            if (!s.isEmpty()) {
                double count = Double.parseDouble(s);
                double min_amount = mData.getMin_amount();
                double max_amount = mData.getMax_amount();
                double money = count * mPrice;
                if (money < min_amount) {
                    mTvCoinHint.setText(getString(R.string.no_less_than_the_limit));
                } else if (money > max_amount) {
                    mTvCoinHint.setText(getString(R.string.no_exceed_than_the_limit));
                } else {
                    mTvCoinHint.setText("");
                }
                DecimalFormat df = new DecimalFormat("#.##");
                String str = df.format(money);
                mEtCny.setText(str);
            } else {
                mEtCny.setText("0.00");
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String s = mEtCoin.getText().toString();
            if (!s.isEmpty()) {
                double count = Double.parseDouble(s);
                double min_amount = mData.getMin_amount();
                double max_amount = mData.getMax_amount();
                double money = count * mPrice;
                if (money < min_amount) {
                    mTvCoinHint.setText(getString(R.string.no_less_than_the_limit));
                } else if (money > max_amount) {
                    mTvCoinHint.setText(getString(R.string.no_exceed_than_the_limit));
                } else {
                    mTvCoinHint.setText("");
                }
                DecimalFormat df = new DecimalFormat("#.##");
                String str = df.format(money);
                mEtCny.setText(str);
            } else {
                mEtCny.setText("0.00");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher mTextWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String s = mEtCny.getText().toString();
            if (!s.isEmpty()) {
                double money = Double.parseDouble(s);
                double min_amount = mData.getMin_amount();
                double max_amount = mData.getMax_amount();
                if (money < min_amount) {
                    mTvCoinHint.setText(getString(R.string.no_less_than_the_limit));
                } else if (money > max_amount) {
                    mTvCoinHint.setText(getString(R.string.no_exceed_than_the_limit));
                } else {
                    mTvCoinHint.setText("");
                }
                double count = money / mPrice;
                DecimalFormat df = new DecimalFormat("#.######");
                String str = df.format(count);
                mEtCoin.setText(str);
            } else {
                mEtCoin.setText("0.00");
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String s = mEtCny.getText().toString();
            if (!s.isEmpty()) {
                double money = Double.parseDouble(s);
                double min_amount = mData.getMin_amount();
                double max_amount = mData.getMax_amount();
                if (money < min_amount) {
                    mTvCoinHint.setText(getString(R.string.no_less_than_the_limit));
                } else if (money > max_amount) {
                    mTvCoinHint.setText(getString(R.string.no_exceed_than_the_limit));
                } else {
                    mTvCoinHint.setText("");
                }
                double count = money / mPrice;
                DecimalFormat df = new DecimalFormat("#.######");
                String str = df.format(count);
                mEtCoin.setText(str);
            } else {
                mEtCoin.setText("0.00");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private InputFilter lengthFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // source:当前输入的字符
            // start:输入字符的开始位置
            // end:输入字符的结束位置
            // dest：当前已显示的内容
            // dstart:当前光标开始位置
            // dent:当前光标结束位置
            if (dest.length() == 0 && source.equals(".")) {
                return "0.";
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                if (dotValue.length() == 2) {
                    return "";
                }
            }
            return null;
        }

    };

    private InputFilter lengthFilter2 = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // source:当前输入的字符
            // start:输入字符的开始位置
            // end:输入字符的结束位置
            // dest：当前已显示的内容
            // dstart:当前光标开始位置
            // dent:当前光标结束位置
            if (dest.length() == 0 && source.equals(".")) {
                return "0.";
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                if (dotValue.length() == 6) {
                    return "";
                }
            }
            return null;
        }

    };

    private void initInterface() {
        Bundle bundle = getIntent().getExtras();
        mType = bundle.getBoolean("type", false);
        mData = (DealListInfo.DataBean) bundle.getSerializable("data");
        if (mData.getAvatar().isEmpty()) {
            mIvTouxiang.setImageBitmap(UtilTool.setDefaultimage(this));
        } else {
            Glide.with(this).load(mData.getAvatar()).into(mIvTouxiang);
        }
        mPrice = Double.parseDouble(mData.getPrice());
        mId = mData.getId();
        mTvName.setText(mData.getUsername());
        mTvCoin.setText(mData.getCoin_name());
//        mTvId.setText("ID：" + mData.getUser_id() + "");
        mTvQuota.setText(mData.getMin_amount() + "-" + mData.getMax_amount() + " " + mData.getCurrency());
        mTv4.setText(mData.getCurrency());
        mTvRemark.setText(mData.getRemark());
        mTvPrice.setText(mPrice + "");
        mEtCny.setHint(mData.getMin_amount() + "-" + mData.getMax_amount() + mData.getCurrency());
        mTvReputation.setText(getString(R.string.deal) + " " + mData.getCount_trans_number() + " | " + getString(R.string.count) + mData.getNumber() + " " + mData.getCoin_name());
        mTvTitle.setText(getString(R.string.buy) + mData.getCoin_name());
        if (mType) {
            mTvBuysellCount.setText(getString(R.string.sell_count));
            mBtnSellBuy.setText(getString(R.string.work_off));
            mEtCoin.setHint(getString(R.string.sell_count));
            mTvTitle.setText(getString(R.string.work_off) + mData.getCoin_name());
            mEtCoin.setBackground(getResources().getDrawable(R.drawable.bg_blue_border2));
            mEtCny.setBackground(getResources().getDrawable(R.drawable.bg_blue_border2));
            mBtnSellBuy.setBackground(getResources().getDrawable(R.drawable.bg_green_shape2));
        }
    }

    @OnClick({R.id.bark, R.id.tv_help, R.id.btn_sell_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_help:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.btn_sell_buy:
                buySell();
                break;
        }
    }

    private void buySell() {
        String moneys = mEtCny.getText().toString();
        String count = mEtCoin.getText().toString();
        if (!moneys.isEmpty() && !count.isEmpty()) {
            double money = Double.parseDouble(moneys);
            if (money >= mData.getMin_amount() && money <= mData.getMax_amount()) {
                if (!mType) {
                    showDialog();
                } else {
                    showPWDialog();
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_money_count), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.buy_coin_hint));
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder("");
                deleteCacheDialog.dismiss();
            }
        });
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
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
        String count = mEtCoin.getText().toString();
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
        countCoin.setText(count + mData.getCoin_name());
        coin.setText(getString(R.string.work_off) + mData.getCoin_name());
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
                    createOrder(password);
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

    public void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this,R.style.dialog);
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
                startActivity(new Intent(BuySellActivity.this, PayPasswordActivity.class));
            }
        });
    }

    private void createOrder(String password) {
        BuySellPresenter buySellPresenter = new BuySellPresenter(this);
        buySellPresenter.createOrder(mData.getId(), mEtCoin.getText().toString(), mData.getPrice(), mEtCny.getText().toString(), new BuySellPresenter.CallBack2() {
            @Override
            public void send(OrderInfo.DataBean data) {
                Intent intent = new Intent(BuySellActivity.this, OrderDetailsActivity.class);
                intent.putExtra("type", getString(R.string.ad));
                intent.putExtra("data", data);
                EventBus.getDefault().post(new MessageEvent(getString(R.string.create_order)));
                startActivity(intent);
                finish();
            }
        }, password);
    }
}
