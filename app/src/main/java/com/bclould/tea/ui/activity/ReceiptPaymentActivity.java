package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.ReceiptPaymentPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReceiptPaymentActivity extends BaseActivity {
    private static final int MONEYIN = 0;
    private static final int MONEYOUT = 1;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_selector_way)
    TextView mTvSelectorWay;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.rl_data)
    RelativeLayout mRlData;
    @Bind(R.id.iv_qr)
    ImageView mIvQr;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv_set)
    TextView mTvSet;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_receipt_payment_record)
    RelativeLayout mRlReceiptPaymentRecord;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;
    private boolean mType;
    private LoadingProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_payment);
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        getPhoneSize();
        moneyIn();
    }

    private void moneyIn() {
        mType = false;
        mTvSet.setText(getString(R.string.set_money_coin));
        mRlData.setVisibility(View.GONE);
        mXx.setVisibility(View.VISIBLE);
        mTvSet.setVisibility(View.VISIBLE);
        mTvHint.setText(getString(R.string.scan_qr_code_pay_hint));
        mReceiptPaymentPresenter.generateReceiptQrCode("", "", "", new ReceiptPaymentPresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                if (ActivityUtil.isActivityOnTop(ReceiptPaymentActivity.this)) {
                    mLlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    String code = UtilTool.base64PetToJson(ReceiptPaymentActivity.this, Constants.MONEYIN, "user_id", data.getId() + "", getString(R.string.receipt_payment));
                    Bitmap bitmap = UtilTool.createQRImage(code);
                    Glide.with(ReceiptPaymentActivity.this).load(bitmap).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(mIvQr);
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(ReceiptPaymentActivity.this)) {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.ll_error, R.id.bark, R.id.tv_selector_way, R.id.rl_receipt_payment_record, R.id.tv_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                moneyIn();
                break;
            case R.id.tv_selector_way:
                initPopWindow();
                break;
            case R.id.tv_set:
                if (mType) {
                    Intent intent = new Intent(ReceiptPaymentActivity.this, PaymentActivity.class);
                    intent.putExtra("type", Constants.MONEYOUT);
                    startActivityForResult(intent, MONEYOUT);
                } else {
                    Intent intent = new Intent(ReceiptPaymentActivity.this, PaymentActivity.class);
                    intent.putExtra("type", Constants.QRMONEYIN);
                    startActivityForResult(intent, MONEYIN);
                }
                break;
            case R.id.rl_receipt_payment_record:
                Intent intent = new Intent(this, PayRecordActivity.class);
                intent.putExtra("type", "3");
                startActivity(intent);
                break;
        }
    }

    //获取屏幕高度
    private void getPhoneSize() {
        mDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDm);
        mHeightPixels = mDm.heightPixels;
    }

    //初始化pop
    private void initPopWindow() {

        int widthPixels = mDm.widthPixels;

        mView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.pop_receipt_payment, null);

        mPopupWindow = new PopupWindow(mView, widthPixels / 100 * 35, mHeightPixels / 6, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.9f;
        getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        mPopupWindow.showAsDropDown(mXx2, (widthPixels - widthPixels / 100 * 35 - 20), 0);
        popChildClick();
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MONEYOUT) {
                mType = true;
                mTvSet.setText(getString(R.string.gg_set));
                mRlData.setVisibility(View.VISIBLE);
                String url = data.getStringExtra("url");
                String coinName = data.getStringExtra("coinName");
                String remark = data.getStringExtra("remark");
                String count = data.getStringExtra("count");
                Glide.with(this).load(url).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(mIvQr);
                hideDialog();
                if (remark == null) {
                    mTv3.setVisibility(View.GONE);
                    mTvRemark.setVisibility(View.GONE);
                } else {
                    mTvRemark.setText(remark);
                }
                mTvCoin.setText(coinName);
                mTvCount.setText(count);
                mTvSelectorWay.setText(getString(R.string.payment));
                mTvHint.setText(getString(R.string.qr_coder_pay));
            } else if (requestCode == MONEYIN) {
                mType = false;
                mRlData.setVisibility(View.VISIBLE);
                mTvSet.setText(getString(R.string.gg_set));
                mTvSelectorWay.setText(getString(R.string.payment));
                String coinId = data.getStringExtra("coinId");
                String coinName = data.getStringExtra("coinName");
                String count = data.getStringExtra("count");
                String id = data.getStringExtra("id");
                String remark = data.getStringExtra("remark");
                mTvCount.setText(count);
                mTvCoin.setText(coinName);
                showDialog();
                String base64PetToJson = UtilTool.base64PetToJson2(Constants.MONEYIN, "user_id", id, "number", count, "coin_id", coinId, "coin_name", coinName, "mark", remark, "user_name", UtilTool.getUser());
                Bitmap bitmap = UtilTool.createQRImage(base64PetToJson);
                Glide.with(ReceiptPaymentActivity.this).load(bitmap).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(mIvQr);
                hideDialog();
                if (remark == null) {
                    mTv3.setVisibility(View.GONE);
                    mTvRemark.setVisibility(View.GONE);
                } else {
                    mTvRemark.setText(remark);
                }
            }
        }
    }

    private void popChildClick() {
        final TextView receipt = (TextView) mView.findViewById(R.id.pop_receipt);
        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvSelectorWay.setText(receipt.getText());
                mPopupWindow.dismiss();
                moneyIn();
            }
        });
        final TextView payment = (TextView) mView.findViewById(R.id.pop_payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                Intent intent = new Intent(ReceiptPaymentActivity.this, PaymentActivity.class);
                intent.putExtra("type", Constants.MONEYOUT);
                startActivityForResult(intent, MONEYOUT);
            }
        });
    }
}
