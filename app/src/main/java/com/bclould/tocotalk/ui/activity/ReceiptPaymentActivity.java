package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.ReceiptPaymentPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReceiptPaymentActivity extends BaseActivity {
    private static final int PAYMENTQRCODE = 0;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_selector_way)
    TextView mTvSelectorWay;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.tv_coin_count)
    TextView mTvCoinCount;
    @Bind(R.id.iv_qr)
    ImageView mIvQr;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_receipt_payment_record)
    RelativeLayout mRlReceiptPaymentRecord;

    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_payment);
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
        ButterKnife.bind(this);
        getPhoneSize();
        moneyIn();
    }

    private void moneyIn() {
        mReceiptPaymentPresenter.generateReceiptQrCode(new ReceiptPaymentPresenter.CallBack() {
            @Override
            public void send(int id) {
                String code = UtilTool.base64PetToJson(Constants.MONEYIN, "redID", id + "", "收付款");
                Bitmap bitmap = UtilTool.createQRImage(code);
                mIvQr.setImageBitmap(bitmap);
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_selector_way, R.id.rl_receipt_payment_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_selector_way:
                initPopWindow();
                break;
            case R.id.rl_receipt_payment_record:
                startActivity(new Intent(this, PayRecordActivity.class));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENTQRCODE && resultCode == RESULT_OK) {
            String url = data.getStringExtra("url");
            String coinName = data.getStringExtra("coinName");
            String count = data.getStringExtra("count");
            Glide.with(this).load(url).into(mIvQr);
            mTvCoinCount.setText(count + " " + coinName);
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
                mTvSelectorWay.setText(payment.getText());
                mPopupWindow.dismiss();
                Intent intent = new Intent(ReceiptPaymentActivity.this, PaymentActivity.class);
                intent.putExtra("type", false);
                startActivityForResult(intent, PAYMENTQRCODE);
            }
        });
    }
}
