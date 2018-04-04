package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter2;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/3/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayManageActivity extends BaseActivity {

    String[] mCoinArr = {"TPC", "BTC", "LTC", "DOGO", "ZEC", "LSK", "MAID", "SHC", "ANS"};
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.rl_coin_pay)
    RelativeLayout mRlCoinPay;
    @Bind(R.id.rl_alter_pw)
    RelativeLayout mRlAlterPw;
    @Bind(R.id.rl_fingerprint_pw)
    RelativeLayout mRlFingerprintPw;
    @Bind(R.id.tv_coin_pay)
    TextView mTvCoinPay;
    private Dialog mBottomDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_manage);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bark, R.id.rl_coin_pay, R.id.rl_alter_pw, R.id.rl_fingerprint_pw, R.id.rl_real_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_coin_pay:
                showCoinDialog();
                break;
            case R.id.rl_alter_pw:
                startActivity(new Intent(this, PayPasswordActivity.class));
                break;
            case R.id.rl_fingerprint_pw:
                showFingerDialog();
                break;
            case R.id.rl_real_name:
                startActivity(new Intent(this, RealNameC1Activity.class));
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter2(this, MyApp.getInstance().mCoinList));
        tvTitle.setText("选择币种");
    }

    private void showFingerDialog() {

    }

    public void hideDialog(String name, int id) {
        mBottomDialog.dismiss();
        mTvCoinPay.setText(name + " 支付");
    }
}
