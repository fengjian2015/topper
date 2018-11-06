package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bclould.tea.Presenter.FinanciaPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.FinancialCoinInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter5;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FinancialDetailActivity extends BaseActivity {

    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.tv_available)
    TextView mTvAvailable;
    @Bind(R.id.tv_rate)
    TextView mTvRate;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.rl_huo)
    RelativeLayout mRlHuo;
    @Bind(R.id.et_money)
    EditText mEtMoney;
    @Bind(R.id.tv_expected)
    TextView mTvExpected;

    private String coinName;
    private String coinOver;
    private String coinLogo;
    private Dialog mBottomDialog;
    private FinancialCoinInfo.DataBean mDataBean = new FinancialCoinInfo.DataBean();
    private List<FinancialCoinInfo.DataBean> mCoinList = new ArrayList<>();
    private int lock_day;
    private int product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_detail);
        ButterKnife.bind(this);
        init();
        setOnClick();
        initHttpCoin();
    }

    private void setOnClick() {
        mEtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double money=UtilTool.parseDouble(mEtMoney.getText().toString());
                mTvExpected.setText(UtilTool.expectedReturn(money,lock_day,mDataBean.getRate_value()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void init() {
        coinName = getIntent().getStringExtra("coinName");
        coinOver = getIntent().getStringExtra("coinOver");
        coinLogo = getIntent().getStringExtra("coinLogo");
        lock_day = getIntent().getIntExtra("lock_day", 0);
        product_id = getIntent().getIntExtra("product_id", 0);
        int coin_id = getIntent().getIntExtra("coin_id", 0);
        String income_rate = getIntent().getStringExtra("income_rate");
        double rate_value = getIntent().getDoubleExtra("rate_value", 0);
        mDataBean.setId(coin_id);
        mDataBean.setCoin_over(UtilTool.parseDouble(coinOver));
        mDataBean.setName(coinName);
        mDataBean.setLogo(coinLogo);
        mDataBean.setRate(income_rate);
        mDataBean.setRate_value(rate_value);
        setCoinName();
    }

    private void setCoinName() {
        Glide.with(this).load(mDataBean.getLogo()).into(mImage);
        mTvCoin.setText(mDataBean.getName());
        mTvAvailable.setText(UtilTool.removeZero(mDataBean.getCoin_over() + "") + mDataBean.getName());

        mTvRate.setText(mDataBean.getRate());

        double money=UtilTool.parseDouble(mEtMoney.getText().toString());
        mTvExpected.setText(UtilTool.expectedReturn(money,lock_day,mDataBean.getRate_value()));

        if (lock_day == 0) {
            mRlHuo.setVisibility(View.GONE);
        } else {
            mRlHuo.setVisibility(View.VISIBLE);
            mTvTime.setText((lock_day / 30) + getString(R.string.month1));
        }
    }

    private void initHttpCoin() {
        new FinanciaPresenter(this).coinList(product_id + "", "financial", new FinanciaPresenter.CallBack2() {
            @Override
            public void send(FinancialCoinInfo baseInfo) {
                mCoinList.clear();
                mCoinList.addAll(baseInfo.getData());
            }

            @Override
            public void error() {

            }
        });
    }


    @OnClick({R.id.bark, R.id.btn_next, R.id.tv_coin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_next:
                pay();
                break;
            case R.id.tv_coin:
                showCoinDialog();
                break;

        }
    }

    private void pay() {
        final String number = mEtMoney.getText().toString();
        if (StringUtils.isEmpty(number)) {
            ToastShow.showToast(this, getString(R.string.pless_input_money));
            return;
        }
        if (UtilTool.parseDouble(number) > mDataBean.getCoin_over()) {
            ToastShow.showToast(this, getString(R.string.insufficient_balance_available));
            return;
        }
        PWDDialog pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                //密码输入成功调用支付
                payHttp(password, number);
            }
        });
        pwdDialog.showDialog(UtilTool.removeZero(number + ""), mDataBean.getName(), getString(R.string.transfer_assets), null, null);
    }

    private void payHttp(String password, String number) {
        new FinanciaPresenter(this).financialBuy(mDataBean.getId(), number, product_id + "", password, new FinanciaPresenter.CallBack1() {
            @Override
            public void send(BaseInfo baseInfo) {
                finish();
            }

            @Override
            public void error() {

            }
        });
    }

    private void showCoinDialog() {
        if (mCoinList.size() == 0) {
            initHttpCoin();
            return;
        }
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
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        recyclerView.setAdapter(new BottomDialogRVAdapter5(this, mCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinancialDetailActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.coins));
    }

    public void hideDialog(FinancialCoinInfo.DataBean data) {
        mBottomDialog.dismiss();
        mDataBean = data;
        setCoinName();
        MessageEvent messageEvent = new MessageEvent(getString(R.string.coin_switchover));
        messageEvent.setCoinName(data.getName());
        EventBus.getDefault().post(messageEvent);
    }
}
