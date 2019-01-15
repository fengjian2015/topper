package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.ReceiptPaymentPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow2;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/21.
 */

public class ReceiptPaymentActivity extends BaseActivity {
    private static final int MONEYIN = 0;
    private static final int MONEYOUT = 1;
    @Bind(R.id.rl_title)
    View mXx2;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.iv_qr)
    ImageView mIvQr;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.rl_coin_count)
    RelativeLayout mRlCoinCount;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.rl_data)
    RelativeLayout mRlData;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv_set)
    TextView mTvSet;
    @Bind(R.id.rl_backgound)
    RelativeLayout mRlBackgound;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_receipt_payment_record)
    RelativeLayout mRlReceiptPaymentRecord;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private DisplayMetrics mDm;
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;
    private boolean mType;
    private LoadingProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        setContentView(R.layout.activity_receipt_payment);
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
        ButterKnife.bind(this);
        setTitle(getString(R.string.receipt_payment),getString(R.string.receipt));
        mTvAdd.setCompoundDrawablePadding(20);
        mTvAdd.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.iocn_two_c), null);
        getPhoneSize();
        moneyIn();
    }

    private void initIntent() {
        String action = getIntent().getAction();
        if (action != null && action.equals("android.intent.action.receiving") && WsConnection.getInstance().getOutConnection()) {
            finish();
            startActivity(new Intent(this, InitialActivity.class));
        }
    }

    private void moneyIn() {
        mType = false;
        mTvSet.setText(getString(R.string.set_money_coin));
        mRlData.setVisibility(View.GONE);
        mXx.setVisibility(View.VISIBLE);
        mTvSet.setVisibility(View.VISIBLE);
        mTvHint.setText(getString(R.string.scan_qr_code_pay_hint));
        mRlBackgound.setBackgroundResource(R.drawable.bg_get_shape);
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

    @OnClick({R.id.ll_error, R.id.bark, R.id.tv_add, R.id.rl_receipt_payment_record, R.id.tv_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                moneyIn();
                break;
            case R.id.tv_add:
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
    }

    //初始化pop
    private void initPopWindow() {
        int widthPixels = mDm.widthPixels;
        List<HashMap> list=new ArrayList<>();
        list.add(MenuListPopWindow2.setHashMapData(this,R.string.receipt,R.mipmap.icon_pay_c));
        list.add(MenuListPopWindow2.setHashMapData(this,R.string.payment,R.mipmap.icon_get_c));
        final MenuListPopWindow2 menuListPopWindow2=new MenuListPopWindow2(this,(int) (getResources().getDimension(R.dimen.y200)),list);
        if(!ActivityUtil.isActivityOnTop(this))return;
        menuListPopWindow2.showAsDropDown(mXx2,(widthPixels - menuListPopWindow2.getPopupWidth()), 0);
        menuListPopWindow2.setListOnClick(new MenuListPopWindow2.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        mTvAdd.setText(getResources().getString(R.string.receipt));
                        mTvAdd.setTextColor(Color.rgb(124, 161, 229));
                        moneyIn();
                        menuListPopWindow2.dismiss();
                        break;
                    case 1:
                        Intent intent = new Intent(ReceiptPaymentActivity.this, PaymentActivity.class);
                        intent.putExtra("type", Constants.MONEYOUT);
                        startActivityForResult(intent, MONEYOUT);
                        menuListPopWindow2.dismiss();
                        break;
                }
            }
        });
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
                Bitmap bitmap = UtilTool.createQRImage(url);
                Glide.with(ReceiptPaymentActivity.this).load(bitmap).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(mIvQr);
//                Glide.with(this).load(url).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(mIvQr);
                hideDialog();
                if (remark.isEmpty()) {
                    mTvRemark.setVisibility(View.GONE);
                } else {
                    mTvRemark.setVisibility(View.VISIBLE);
                    mTvRemark.setText(getString(R.string.remark) + "：" + remark);
                }
                mTvCoin.setText(coinName);
                mTvCount.setText(count);
                mTvAdd.setText(getString(R.string.payment));
                mTvAdd.setTextColor(Color.rgb(73, 187, 100));
                mTvHint.setText(getString(R.string.qr_coder_pay));
                mRlBackgound.setBackgroundResource(R.drawable.bg_pay_shape);
            } else if (requestCode == MONEYIN) {
                mType = false;
                mRlData.setVisibility(View.VISIBLE);
                mTvSet.setText(getString(R.string.gg_set));
                mTvHint.setText(getString(R.string.receipt_payment_hint));
                mTvAdd.setText(getString(R.string.receipt));
                mTvAdd.setTextColor(Color.rgb(124, 161, 229));
                mRlBackgound.setBackgroundResource(R.drawable.bg_get_shape);
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
                if (remark.isEmpty()) {
                    mTvRemark.setVisibility(View.GONE);
                } else {
                    mTvRemark.setVisibility(View.VISIBLE);
                    mTvRemark.setText(getString(R.string.remark) + "：" + remark);
                }
            }
        }
    }
}
