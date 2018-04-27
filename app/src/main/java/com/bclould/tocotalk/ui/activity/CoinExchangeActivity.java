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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.CoinListInfo;
import com.bclould.tocotalk.model.ExchangeOrderInfo;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tocotalk.ui.adapter.CoinExchangeRVAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bumptech.glide.Glide;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.Presenter.LoginPresenter.CURRENCY;
import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;
import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/4.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CoinExchangeActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_question)
    TextView mTvQuestion;
    @Bind(R.id.tv_exchange)
    TextView mTvExchange;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.ll_coin_selector)
    LinearLayout mLlCoinSelector;
    @Bind(R.id.tv_remain)
    TextView mTvRemain;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.btn_float)
    Button mBtnFloat;
    @Bind(R.id.tv_cny)
    TextView mTvCny;
    @Bind(R.id.btn_exchange)
    Button mBtnExchange;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private CoinPresenter mCoinPresenter;
    private Dialog mBottomDialog;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private MNPasswordEditText mEtPassword;
    private ArrayList<Map<String, String>> valueList;
    private GridView mGridView;
    private CoinExchangeRVAdapter mCoinExchangeRVAdapter;
    private String mPrice = "";
    private String mServiceCharge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_exchange);
        ButterKnife.bind(this);
        initListener();
        initRecylerView();
        initData();
        boolean aBoolean = MySharedPreferences.getInstance().getBoolean(Constants.EXCHANGE_DISCLAIMER);
        if (!aBoolean)
            showDisclaimerDialog();
    }

    boolean isCheckBox = false;

    private void showDisclaimerDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_otc_disclaimer, this);
        deleteCacheDialog.show();
        final LinearLayout showHide = (LinearLayout) deleteCacheDialog.findViewById(R.id.ll_show_hide);
        final CheckBox checkBox = (CheckBox) deleteCacheDialog.findViewById(R.id.check_box);
        final Button roger = (Button) deleteCacheDialog.findViewById(R.id.btn_roger);
        roger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        showHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckBox = !isCheckBox;
                checkBox.setChecked(isCheckBox);
                MySharedPreferences.getInstance().setBoolean(Constants.EXCHANGE_DISCLAIMER, isCheckBox);
            }
        });
    }

    private void initListener() {
        mEtCount.setFilters(new InputFilter[]{lengthFilter});
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String counts = mEtCount.getText().toString();
                if (counts != null && !counts.isEmpty() && mPrice != null && !mPrice.isEmpty()) {
                    double count = Double.parseDouble(counts);
                    double price = Double.parseDouble(mPrice);
                    DecimalFormat df = new DecimalFormat("0.000000");
                    String sum = df.format(count * price);
                    mTvPrice.setText(sum + "");
                } else {
                    mTvPrice.setText(mPrice);
                }
            }
        });
    }

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

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCoinExchangeRVAdapter = new CoinExchangeRVAdapter(this, mExchangeOrderList);
        mRecyclerView.setAdapter(mCoinExchangeRVAdapter);
    }

    private void initData() {
        mCoinPresenter = new CoinPresenter(this);
        initCoin();
    }

    private void initPrice(String name) {
        mCoinPresenter.getCoinPrice(name, new CoinPresenter.CallBack2() {
            @Override
            public void send(BaseInfo.DataBean data) {
                try {
                    if (data.getUSDT() != null && data.getRate() != null && data.getTrend() != null) {
                        if (!data.getUSDT().isEmpty() && !data.getRate().isEmpty() && !data.getTrend().isEmpty()) {
                            double usdt = Double.parseDouble(data.getUSDT());
                            double cny = Double.parseDouble(data.getRate());
                            DecimalFormat df = new DecimalFormat("#.00");
                            String price = df.format(cny * usdt);
                            mTvPrice.setText(data.getUSDT());
                            mPrice = mTvPrice.getText().toString();
                            mTvCny.setText("≈ " + price + " " + MySharedPreferences.getInstance().getString(CURRENCY));
                            if (data.getTrend().contains("-")) {
                                mBtnFloat.setText(data.getTrend() + "% ↓");
                            } else {
                                mBtnFloat.setText(data.getTrend() + "% ↑");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    String mPageSize = "100";
    String mPage = "1";
    List<ExchangeOrderInfo.DataBeanX.DataBean> mExchangeOrderList = new ArrayList<>();

    private void initListData(String name) {
        mExchangeOrderList.clear();
        mCoinPresenter.exchangeOrder("USDT", name, mPage, mPageSize, new CoinPresenter.CallBack3() {
            @Override
            public void send(ExchangeOrderInfo.DataBeanX data) {
                mExchangeOrderList.addAll(data.getData());
                mCoinExchangeRVAdapter.notifyDataSetChanged();
            }
        });
    }

    List<CoinListInfo.DataBean> mCoinList = new ArrayList<>();
    List<String> mCoin = new ArrayList<>();

    private void initCoin() {
        mCoinPresenter.coinLists("exchange", new CoinPresenter.CallBack() {
            @Override
            public void send(List<CoinListInfo.DataBean> data) {
                try {
                    mCoinList.addAll(data);
                    mCoin.add(data.get(0).getName());
                    mServiceCharge = data.get(0).getOut_exchange();
                    mTvRemain.setText(getString(R.string.dqky) + data.get(0).getCoin_over() + " " + data.get(0).getName());
                    mTvCoin.setText(data.get(0).getName());
                    Glide.with(CoinExchangeActivity.this).load(data.get(0).getLogo()).into(mIvLogo);
                    mTvExchange.setText(data.get(0).getName() + "/" + getString(R.string.usdt));
                    initPrice(data.get(0).getName());
                    initListData(data.get(0).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_question, R.id.ll_coin_selector, R.id.btn_exchange})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_question:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.ll_coin_selector:
                showCoinDialog();
                break;
            case R.id.btn_exchange:
                if (checkEidt()) {
                    showPWDialog();
                }
                break;
           /* case R.id.tv_up:
                if (mPage.get("page").equals("1")) {
                    Toast.makeText(this, "已经是第一页了", Toast.LENGTH_SHORT).show();
                } else {
                    int page = Integer.parseInt(mPage.get("page"));
                    page--;
                    mPage.put("page", page + "");
                    initListData(mCoin.get(0));
                }
                break;
            case R.id.tv_below:
                if (mPage.get("page").equals(mPageSum.get("pageSum"))) {
                    Toast.makeText(this, "已经是第最后一页了", Toast.LENGTH_SHORT).show();
                } else {
                    int page = Integer.parseInt(mPage.get("page"));
                    page++;
                    mPage.put("page", page + "");
                    initListData(mCoin.get(0));
                }
                break;*/
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
        String coins = mTvCoin.getText().toString();
        String count = mEtCount.getText().toString();
        TextView coin = (TextView) mRedDialog.findViewById(R.id.tv_coin);
        TextView countCoin = (TextView) mRedDialog.findViewById(R.id.tv_count_coin);
        TextView hint = (TextView) mRedDialog.findViewById(R.id.tv_hint);
        hint.setVisibility(View.VISIBLE);
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
        coin.setText(coins + getString(R.string.exchange) + getString(R.string.usdt));
        hint.setText(getString(R.string.start_guess_service_hint) + Double.parseDouble(mServiceCharge) * 100 + "%" + getString(R.string.sxf));
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
                    exchange(password);
                }
            }
        });
    }

    private void exchange(String password) {
        final String count = mEtCount.getText().toString();
        String price = mTvPrice.getText().toString();
        final String coin = mTvCoin.getText().toString();
        mCoinPresenter.exchange(price, count, "USDT", coin, password, new CoinPresenter.CallBack4() {
            @Override
            public void send() {
                String remain = "";
                for (CoinListInfo.DataBean info : mCoinList) {
                    if (info.getName().equals(coin)) {
                        remain = Double.parseDouble(info.getCoin_over()) - Double.parseDouble(count) + "";
                        info.setCoin_over(remain);
                    }
                }
                mTvRemain.setText(remain);
                mEtCount.setText("");
                initListData(mCoin.get(0));
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

    private boolean checkEidt() {
        if (mEtCount.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
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
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, mCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CoinExchangeActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
    }

    public void hideDialog(String name, int id, String logo, String coin_over, String serviceCharge) {
        mCoin.clear();
        mCoin.add(name);
        initListData(name);
        mBottomDialog.dismiss();
        mTvCoin.setText(name);
        mTvRemain.setText(getString(R.string.dqky) + coin_over + " " + name);
        Glide.with(CoinExchangeActivity.this).load(logo).into(mIvLogo);
        mTvExchange.setText(name + "/" + getString(R.string.usdt));
        mServiceCharge = serviceCharge;
        initPrice(name);
    }
}
