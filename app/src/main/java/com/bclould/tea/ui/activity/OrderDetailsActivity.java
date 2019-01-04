package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bclould.tea.Presenter.OrderDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.OrderInfo;
import com.bclould.tea.model.OrderInfo2;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.maning.pswedittextlibrary.MNPasswordEditText;
import org.greenrobot.eventbus.EventBus;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/19.
 */

public class OrderDetailsActivity extends BaseActivity {

    @Bind(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @Bind(R.id.tv_pay_type)
    TextView mTvPayType;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.rl_order_intro)
    RelativeLayout mRlOrderIntro;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv_buysell)
    TextView mTvBuysell;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.iv_bank)
    ImageView mIvBank;
    @Bind(R.id.tv_bank_name)
    TextView mTvBankName;
    @Bind(R.id.tv_bank_site)
    TextView mTvBankSite;
    @Bind(R.id.tv_bank_number)
    TextView mTvBankNumber;
    @Bind(R.id.rl_bank)
    RelativeLayout mRlBank;
    @Bind(R.id.tv_buysell2)
    TextView mTvBuysell2;
    @Bind(R.id.ll_seller)
    LinearLayout mLlSeller;
    @Bind(R.id.ll_buyer)
    LinearLayout mLlBuyer;
    @Bind(R.id.ll_order)
    LinearLayout mLlOrder;
    @Bind(R.id.tv_payment_type)
    TextView mTvPaymentType;
    @Bind(R.id.btn_sell_cancel)
    Button mBtnSellCancel;
    @Bind(R.id.btn_sell_confirm)
    Button mBtnSellConfirm;
    @Bind(R.id.btn_buy_cancel)
    Button mBtnBuyCancel;
    @Bind(R.id.btn_buy_confirm)
    Button mBtnBuyConfirm;
    @Bind(R.id.tv_service_charge)
    TextView mTvServiceCharge;
    @Bind(R.id.tv_shiji)
    TextView mTvShiji;
    @Bind(R.id.tv_email)
    TextView mTvEmail;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.btn_contact)
    Button mBtnContact;
    @Bind(R.id.tv_transfer_hint)
    TextView mTvTransferHint;
    @Bind(R.id.tv_deal_number)
    TextView mTvDealNumber;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private OrderInfo.DataBean mData;
    Timer mTimer = new Timer();
    private int mRecLen = 0;
    private OrderDetailsPresenter mOrderDetailsPresenter;
    TimerTask mTask = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    try {
                        if(mTvTime==null)return;
                        mRecLen--;
                        int second = mRecLen % 60;
                        int minute = mRecLen / 60;
                        mTvTime.setText(minute + getString(R.string.fen) + second + getString(R.string.second));
                        if (mRecLen < 0) {
                            mTimer.cancel();
                            finish();
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.order_timeout), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private String mType;
    private int mType1;
    private String mTo_user_name;
    private String mTo_user_toco_id;
    private String mUser_name;
    private String mUser_toco_id;
    private String mId;
    private PWDDialog pwdDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        setTitle("",getString(R.string.help));
        initInterface();
    }

    private void initInterface() {
        mOrderDetailsPresenter = new OrderDetailsPresenter(this);
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        if (mType.equals(getString(R.string.order))) {
            mId = intent.getStringExtra("id");
            initData();
        } else {
            mScrollView.setVisibility(View.VISIBLE);
            mData = (OrderInfo.DataBean) intent.getSerializableExtra("data");
            mRecLen = mData.getDeadline();
            mType1 = mData.getType();
            mTimer.schedule(mTask, 1000, 1000);
            if (mData.getType() == 1) {
                mTvTitleTop.setText(getString(R.string.buy) + mData.getCoin_name());
                mLlBuyer.setVisibility(View.VISIBLE);
                mLlSeller.setVisibility(View.GONE);
                mTvPaymentType.setText(getString(R.string.payment_method));
                mTvPayType.setText(getString(R.string.dengdai_fk));
            } else {
                mLlBuyer.setVisibility(View.GONE);
                mLlSeller.setVisibility(View.VISIBLE);
                mTvTitleTop.setText(getString(R.string.work_off) + mData.getCoin_name());
                mTvPaymentType.setText(getString(R.string.shoukuan_fs));
                mTvPayType.setText(getString(R.string.dengdai_sk));
            }
            mTo_user_name = mData.getTo_user_name();
            mTo_user_toco_id = mData.getToco_id();
            mUser_name = mData.getUser_name();
            mUser_toco_id = mData.getToco_id();
            if (mData.getType() == 1) {
                mTvTransferHint.setText(getString(R.string.shijidao_transfer));
                mTvBuysell.setText(getString(R.string.seller) + ":" + mData.getTo_user_name());
                mTvBuysell2.setText(getString(R.string.buyer) + ":" + mData.getUser_name());
            } else {
                mBtnSellCancel.setVisibility(View.GONE);
                mTvTransferHint.setText(getString(R.string.shijikou_coin));
                mTvBuysell2.setText(getString(R.string.seller) + ":" + mData.getTo_user_name());
                mTvBuysell.setText(getString(R.string.buyer) + ":" + mData.getUser_name());
            }
            mTvCount.setText(mData.getNumber());
            mTvMoney.setText(mData.getTrans_amount());
            mTvPrice.setText(mData.getPrice());
            mTvEmail.setText(mData.getEmail());
            mTvPhone.setText(mData.getMobile());
            mTvDealNumber.setText(mData.getPayment_no() + "");
            mTvOrderNumber.setText(getString(R.string.order_number) + ":" + mData.getOrder_no());
            mTvServiceCharge.setText(Double.parseDouble(mData.getOtc_free()) * 100 + "%");
            mTvShiji.setText(mData.getActual_number());
            if (mData.getRemark() != null) {
                mTvRemark.setText(getString(R.string.remark) + ":" + mData.getRemark());
            }
            if (mData.getBank() != null) {
                mTvBankName.setText(mData.getBank().getCard_name());
                mTvBankNumber.setText(mData.getBank().getCard_number());
                mTvBankSite.setText(mData.getBank().getBank_name());
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    OrderInfo2 mInfo = new OrderInfo2();

    private void initData() {
        mOrderDetailsPresenter.orderInfo(mId, new OrderDetailsPresenter.CallBack() {
            @Override
            public void send(OrderInfo2.DataBean data) {
                if (ActivityUtil.isActivityOnTop(OrderDetailsActivity.this)) {
                    mScrollView.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mType1 = data.getType();
                    mInfo.setData(data);
                    mTvOrderNumber.setText(getString(R.string.order_number) + ":" + data.getOrder_no());
                    if (data.getType() == 1) {
                        mLlBuyer.setVisibility(View.VISIBLE);
                        mLlSeller.setVisibility(View.GONE);
                        mTvTitleTop.setText(getString(R.string.buy) + data.getCoin_name());
                    } else {
                        mLlBuyer.setVisibility(View.GONE);
                        mLlSeller.setVisibility(View.VISIBLE);
                        mTvTitleTop.setText(getString(R.string.work_off) + data.getCoin_name());
                    }
                    if (data.getStatus() == 0) {
                        mLlBuyer.setVisibility(View.GONE);
                        mLlSeller.setVisibility(View.GONE);
                        /*mTimer.cancel();
                        mTvTime.setText(getString(R.string.no_time));
                        finish();
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.order_cancel), Toast.LENGTH_SHORT).show();*/
                    } else if (data.getStatus() == 1) {
                        mRecLen = data.getDeadline();
                        mTimer.schedule(mTask, 1000, 1000);
                    } else if (data.getStatus() == 2) {
                        mRecLen = data.getDeadline();
                        mTimer.schedule(mTask, 1000, 1000);
                        if (data.getType() == 1) {
                            mBtnBuyCancel.setVisibility(View.GONE);
                            mBtnBuyConfirm.setBackground(getResources().getDrawable(R.drawable.bg_gray_shape));
                            mBtnBuyConfirm.setText(getString(R.string.yi_pay));
                            mBtnBuyConfirm.setEnabled(false);
                        } else {
                            mBtnSellCancel.setVisibility(View.GONE);
                        }
                    } else if (data.getStatus() == 3) {
                        mLlBuyer.setVisibility(View.GONE);
                        mLlSeller.setVisibility(View.GONE);
                    } else if (data.getStatus() == 4) {
                        mLlBuyer.setVisibility(View.GONE);
                        mLlSeller.setVisibility(View.GONE);
                    }
                    mTo_user_name = data.getTo_user_name();
                    mUser_name = data.getUser_name();
                    if (data.getType() == 1) {
                        mTvTransferHint.setText(getString(R.string.shijidao_transfer));
                        mTvBuysell.setText(getString(R.string.seller) + ":" + data.getTo_user_name());
                        mTvBuysell2.setText(getString(R.string.buyer) + ":" + data.getUser_name());
                    } else {
                        mBtnSellCancel.setVisibility(View.GONE);
                        mTvTransferHint.setText(getString(R.string.shijikou_coin));
                        mTvBuysell2.setText(getString(R.string.seller) + ":" + data.getTo_user_name());
                        mTvBuysell.setText(getString(R.string.buyer) + ":" + data.getUser_name());
                    }
                    mTvCount.setText(data.getNumber());
                    mTvMoney.setText(data.getTrans_amount());
                    mTvPrice.setText(data.getPrice());
                    mTvPayType.setText(data.getStatus_name());
                    mTvEmail.setText(data.getEmail());
                    mTvPhone.setText(data.getMobile());
                    mTvDealNumber.setText(data.getPayment_no() + "");
                    mTvServiceCharge.setText(Double.parseDouble(data.getOtc_free()) * 100 + "%");
                    mTvShiji.setText(data.getActual_number());
                    if (data.getRemark() != null) {
                        mTvRemark.setText(getString(R.string.remark) + ":" + data.getRemark());
                    }
                    if (data.getBank() != null) {
                        mTvBankName.setText(data.getBank().getCard_name());
                        mTvBankNumber.setText(data.getBank().getCard_number());
                        mTvBankSite.setText(data.getBank().getBank_name());
                    }
                } else if (data.getStatus() == 3) {
                    mLlBuyer.setVisibility(View.GONE);
                    mLlSeller.setVisibility(View.GONE);
                } else if (data.getStatus() == 4) {
                    mLlBuyer.setVisibility(View.GONE);
                    mLlSeller.setVisibility(View.GONE);
                }
                mTo_user_name = data.getTo_user_name();
                mTo_user_toco_id = data.getToco_id();
                mUser_name = data.getUser_name();
                mUser_toco_id = data.getToco_id();
                if (data.getType() == 1) {
                    mTvTransferHint.setText(getString(R.string.shijidao_transfer));
                    mTvBuysell.setText(getString(R.string.seller) + ":" + data.getTo_user_name());
                    mTvBuysell2.setText(getString(R.string.buyer) + ":" + data.getUser_name());
                } else {
                    mBtnSellCancel.setVisibility(View.GONE);
                    mTvTransferHint.setText(getString(R.string.shijikou_coin));
                    mTvBuysell2.setText(getString(R.string.seller) + ":" + data.getTo_user_name());
                    mTvBuysell.setText(getString(R.string.buyer) + ":" + data.getUser_name());
                }
                mTvCount.setText(data.getNumber());
                mTvMoney.setText(data.getTrans_amount());
                mTvPrice.setText(data.getPrice());
                mTvPayType.setText(data.getStatus_name());
                mTvEmail.setText(data.getEmail());
                mTvPhone.setText(data.getMobile());
                mTvDealNumber.setText(data.getPayment_no() + "");
                mTvServiceCharge.setText(Double.parseDouble(data.getOtc_free()) * 100 + "%");
                mTvShiji.setText(data.getActual_number());
                if (data.getRemark() != null) {
                    mTvRemark.setText(getString(R.string.remark) + ":" + data.getRemark());
                }
                if (data.getBank() != null) {
                    mTvBankName.setText(data.getBank().getCard_name());
                    mTvBankNumber.setText(data.getBank().getCard_number());
                    mTvBankSite.setText(data.getBank().getBank_name());
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(OrderDetailsActivity.this)) {
                    mScrollView.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.ll_error, R.id.btn_contact, R.id.bark, R.id.tv_add, R.id.btn_buy_cancel, R.id.btn_sell_cancel, R.id.btn_buy_confirm, R.id.btn_sell_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_contact:
                if (!mTvBuysell.getText().toString().isEmpty() && !mTvBuysell2.getText().toString().isEmpty()) {
                    if (mType1 == 1) {
                        Intent intent = new Intent(this, ConversationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", mTo_user_name);
                        bundle.putString("user", mTo_user_toco_id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, ConversationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", mUser_name);
                        bundle.putString("user", mUser_toco_id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.btn_buy_cancel:
                showPWDialog(0);
                break;
            case R.id.btn_sell_cancel:
                showPWDialog(0);
                break;
            case R.id.btn_buy_confirm:
                showPWDialog(1);
                break;
            case R.id.btn_sell_confirm:
                showPWDialog(2);
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

    private void showPWDialog(final int type) {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                if (type == 0) {
                    cancel(password);
                } else if (type == 1) {
                    confirmPay(password);
                } else if (type == 2) {
                    confirmGiveCoin(password);
                }
            }
        });
        String content = "";
        if (type == 0) {
            content=getString(R.string.cancel_order_hint);
        } else if (type == 1) {
            content=getString(R.string.fk_hint);
        } else if (type == 2) {
            content=getString(R.string.fb_hint);
        }
        pwdDialog.showDialog(content,null,null,null,null);
    }
    private void confirmGiveCoin(String password) {
        if (mType.equals(getString(R.string.order))) {
            mOrderDetailsPresenter.confirmGiveCoin(mInfo.getData().getTrans_id(), mInfo.getData().getId(), password, new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.confirm_fb));
                    messageEvent.setId(mInfo.getData().getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.deal_finish), Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(OrderDetailsActivity.this, OrderCloseActivity.class);
                    intent.putExtra("id", mId);
                    intent.putExtra("status", 3);
                    startActivity(intent);
                }
            });
        } else {
            mOrderDetailsPresenter.confirmGiveCoin(mData.getTrans_id(), mData.getId(), password, new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.confirm_fb));
                    messageEvent.setId(mData.getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.deal_finish), Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(OrderDetailsActivity.this, OrderCloseActivity.class);
                    intent.putExtra("id", mId);
                    intent.putExtra("status", 3);
                    startActivity(intent);
                }
            });
        }
    }

    private void confirmPay(String password) {
        if (mType.equals(getString(R.string.order))) {
            mOrderDetailsPresenter.confirmPay(mInfo.getData().getTrans_id(), mInfo.getData().getId(), password, new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    mTvPayType.setText(getString(R.string.dengdai_fb));
                    mBtnBuyCancel.setVisibility(View.GONE);
                    mBtnBuyConfirm.setBackground(getResources().getDrawable(R.drawable.bg_gray_shape));
                    mBtnBuyConfirm.setText(getString(R.string.yi_pay));
                    mBtnBuyConfirm.setEnabled(false);
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.confirm_fk));
                    messageEvent.setId(mInfo.getData().getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.fk_succeed), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mOrderDetailsPresenter.confirmPay(mData.getTrans_id(), mData.getId(), password, new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    mTvPayType.setText(getString(R.string.dengdai_fb));
                    mBtnBuyCancel.setVisibility(View.GONE);
                    mBtnBuyConfirm.setBackground(getResources().getDrawable(R.drawable.bg_gray_shape));
                    mBtnBuyConfirm.setText(getString(R.string.yi_pay));
                    mBtnBuyConfirm.setEnabled(false);
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.confirm_fk));
                    messageEvent.setId(mData.getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.fk_succeed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cancel(String password) {
        if (mType.equals(getString(R.string.order))) {
            mOrderDetailsPresenter.cancel(mInfo.getData().getTrans_id(), mInfo.getData().getId(), password, new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.cancel_order));
                    messageEvent.setId(mInfo.getData().getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.cancel_order), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            mOrderDetailsPresenter.cancel(mData.getTrans_id(), mData.getId(), password, new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.cancel_order));
                    messageEvent.setId(mData.getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.cancel_order), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}
