package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.OrderDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.OrderInfo2;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/10.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OrderCloseActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_question)
    TextView mTvQuestion;
    @Bind(R.id.tv_order_type)
    TextView mTvOrderType;
    @Bind(R.id.tv_who)
    TextView mTvWho;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_hint_limit)
    TextView mTvHintLimit;
    @Bind(R.id.tv_limit)
    TextView mTvLimit;
    @Bind(R.id.tv_hint_money)
    TextView mTvHintMoney;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_hint_count)
    TextView mTvHintCount;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_hint_price)
    TextView mTvHintPrice;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.ll_finish)
    LinearLayout mLlFinish;
    @Bind(R.id.ll_exception_buy)
    LinearLayout mLlExceptionBuy;
    @Bind(R.id.ll_exception_sell)
    LinearLayout mLlExceptionSell;
    @Bind(R.id.tv_service_charge_hint)
    TextView mTvServiceChargeHint;
    @Bind(R.id.tv_service_charge)
    TextView mTvServiceCharge;
    @Bind(R.id.tv_shiji_hint)
    TextView mTvShijiHint;
    @Bind(R.id.tv_shiji)
    TextView mTvShiji;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private String mId;
    private int mStatus;
    private OrderDetailsPresenter mOrderDetailsPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_close);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mOrderDetailsPresenter = new OrderDetailsPresenter(this);
        initIntent();
        initData();
    }

    private void initData() {
        mOrderDetailsPresenter.orderInfo(mId, new OrderDetailsPresenter.CallBack() {
            @Override
            public void send(OrderInfo2.DataBean data) {
                if (ActivityUtil.isActivityOnTop(OrderCloseActivity.this)) {
                    mLlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mInfo.setData(data);
                    if (data.getTo_user_name().equals(UtilTool.getUser())) {
                        mTvWho.setText(getString(R.string.buyer));
                        mTvName.setText(data.getUser_name());
                    } else {
                        mTvWho.setText(getString(R.string.seller));
                        mTvName.setText(data.getTo_user_name());
                    }
                    if (data.getType() == 1) {
                        if (data.getStatus() == 0) {
                            mTvOrderType.setText(getString(R.string.order_cancel));
                            mLlFinish.setVisibility(View.VISIBLE);
                        } else if (data.getStatus() == 3) {
                            mTvOrderType.setText(getString(R.string.order_finish));
                            mLlFinish.setVisibility(View.VISIBLE);
                        } else if (data.getStatus() == 4) {
                            mTvOrderType.setText(getString(R.string.order_exception));
                            mLlExceptionBuy.setVisibility(View.VISIBLE);
                            mLlFinish.setVisibility(View.VISIBLE);
                            mTvOrderType.setTextColor(getResources().getColor(R.color.color_orange));
                        }
                        mTvTitle.setText(getString(R.string.buy) + data.getCoin_name());
                        mTvShijiHint.setText(getString(R.string.shiji_out_coin));
                    } else {
                        if (data.getStatus() == 0) {
                            mTvOrderType.setText(getString(R.string.order_cancel));
                            mLlFinish.setVisibility(View.VISIBLE);
                        } else if (data.getStatus() == 3) {
                            mTvOrderType.setText(getString(R.string.order_finish));
                            mLlFinish.setVisibility(View.VISIBLE);
                        } else if (data.getStatus() == 4) {
                            mTvOrderType.setText(getString(R.string.order_exception));
                            mLlExceptionSell.setVisibility(View.VISIBLE);
                            mLlFinish.setVisibility(View.VISIBLE);
                            mTvOrderType.setTextColor(getResources().getColor(R.color.color_orange));
                        }
                        mTvTitle.setText(getString(R.string.work_off) + data.getCoin_name());
                        mTvShijiHint.setText(getString(R.string.shijikouchu));
                    }
                    mTvMoney.setText(data.getTrans_amount());
                    mTvPrice.setText(data.getPrice());
                    mTvHintCount.setText(getString(R.string.deal_count) + "(" + data.getCoin_name() + ")");
                    mTvCount.setText(data.getNumber());
                    mTvOrderNumber.setText(data.getOrder_no());
                    mTvServiceCharge.setText(Double.parseDouble(data.getOtc_free()) * 100 + "%");
                    mTvShiji.setText(data.getActual_number());
                    mTvTime.setText(data.getCreated_at());
                    mTvLimit.setText(data.getMin_amount() + " - " + data.getMax_amount());
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(OrderCloseActivity.this)) {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initIntent() {
        mStatus = getIntent().getIntExtra("status", 0);
        mId = getIntent().getStringExtra("id");
        if (mStatus == 0) {
            mTvOrderType.setText(getString(R.string.order_cancel));
        } else if (mStatus == 3) {
            mTvOrderType.setText(getString(R.string.order_finish));
            mLlFinish.setVisibility(View.VISIBLE);
        } else if (mStatus == 4) {
            mTvOrderType.setText(getString(R.string.order_exception));
            mTvOrderType.setTextColor(getResources().getColor(R.color.color_orange));
        }
    }

    @OnClick({R.id.bark, R.id.tv_question, R.id.btn_confirm, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_question:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.btn_confirm:
                showPWDialog();
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }

    private Dialog mRedDialog;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private MNPasswordEditText mEtPassword;
    private GridView mGridView;

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
        coin.setVisibility(View.GONE);
        countCoin.setGravity(Gravity.CENTER);
        countCoin.setText(getString(R.string.fb_hint));
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
                    confirm(password);
                }
            }
        });
    }

    private ArrayList<Map<String, String>> valueList;
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

    OrderInfo2 mInfo = new OrderInfo2();

    private void confirm(String password) {
        mOrderDetailsPresenter.confirmGiveCoin(mInfo.getData().getTrans_id(), mInfo.getData().getId(), password, new OrderDetailsPresenter.CallBack2() {
            @Override
            public void send() {
                MessageEvent messageEvent = new MessageEvent(getString(R.string.confirm_fb));
                messageEvent.setId(mInfo.getData().getId() + "");
                EventBus.getDefault().post(messageEvent);
                Toast.makeText(OrderCloseActivity.this, getString(R.string.deal_finish), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
