package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.ReceiptPaymentPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/3/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReceiptPaymentActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_selector_way)
    TextView mTvSelectorWay;
    @Bind(R.id.iv_qr)
    ImageView mIvQr;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv_selector)
    TextView mTvSelector;
    @Bind(R.id.tv_set_money)
    TextView mTvSetMoney;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_receipt_payment_record)
    RelativeLayout mRlReceiptPaymentRecord;
    String[] mCoinArr = {"TPC", "BTC", "LTC", "DOGO", "ZEC", "LSK", "MAID", "SHC", "ANS"};
    @Bind(R.id.xx2)
    TextView mXx2;
    private Dialog mBottomDialog;
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

    /*private void moneyOut() {
        mReceiptPaymentPresenter.generatePaymentQrCode(new ReceiptPaymentPresenter.CallBack() {
            @Override
            public void send(int id) {
                String code = UtilTool.base64PetToJson(Constants.MONEYIN, "redID", id + "", "收付款");
                Bitmap bitmap = UtilTool.createQRImage(code);
                mIvQr.setImageBitmap(bitmap);
            }
        });
    }*/

    @OnClick({R.id.bark, R.id.tv_selector_way, R.id.tv_selector, R.id.tv_set_money, R.id.rl_receipt_payment_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_selector_way:
                initPopWindow();
                break;
            case R.id.tv_selector:
                showCoinDialog();
                break;
            case R.id.tv_set_money:
                startActivity(new Intent(this, SetMoneyActivity.class));
                break;
            case R.id.rl_receipt_payment_record:
                startActivity(new Intent(this, PayRecordActivity.class));
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
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, mCoinArr));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReceiptPaymentActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText("选择币种");
    }

    public void hideDialog(String name) {
        mBottomDialog.dismiss();
        mTvSelector.setText(name + " 支付");
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

    private void popChildClick() {
        final TextView receipt = (TextView) mView.findViewById(R.id.pop_receipt);
        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvSelectorWay.setText(receipt.getText());
                mPopupWindow.dismiss();
            }
        });
        final TextView payment = (TextView) mView.findViewById(R.id.pop_payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvSelectorWay.setText(payment.getText());
                mPopupWindow.dismiss();
            }
        });
    }
}
