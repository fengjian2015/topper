package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.RedPacketPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/1/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SendQRCodeRedActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_redpacket_record)
    TextView mTvRedpacketRecord;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_currency)
    TextView mTvCurrency;
    @Bind(R.id.image_logo)
    ImageView mImageLogo;
    @Bind(R.id.et_money_count)
    EditText mEtMoneyCount;
    @Bind(R.id.et_red_count)
    EditText mEtRedCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_allmoney)
    TextView mTvAllmoney;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private Dialog mRedDialog;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private MNPasswordEditText mEtPassword;
    private GridView mGridView;
    private ArrayList<Map<String, String>> valueList;
    private RedPacketPresenter mRedPacketPresenter;
    private Dialog mBottomDialog;
    private String logo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_qr_code_red);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket5));
        mRedPacketPresenter = new RedPacketPresenter(this);
        initData();
        setOnClick();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initData() {
        MyApp.getInstance().mRedCoinList.clear();
        if (MyApp.getInstance().mRedCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("red_packet", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (ActivityUtil.isActivityOnTop(SendQRCodeRedActivity.this)) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                        if (MyApp.getInstance().mRedCoinList.size() == 0)
                            MyApp.getInstance().mRedCoinList.addAll(data);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(SendQRCodeRedActivity.this)) {
                        mLlData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @OnClick({R.id.ll_error, R.id.bark, R.id.tv_redpacket_record, R.id.rl_selector_currency, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.tv_redpacket_record:
                startActivity(new Intent(this, RedPacketRecordActivity.class));
                break;
            case R.id.rl_selector_currency:
                showDialog();
                break;
            case R.id.btn_send:
                if (checkEdit())
                    showPWDialog();
                break;
        }
    }


    private void setOnClick() {
        mEtMoneyCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeAllMoney();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtRedCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeAllMoney();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void changeAllMoney(){
        double mCount =UtilTool.parseDouble(mEtRedCount.getText().toString()) *UtilTool.parseDouble(mEtMoneyCount.getText().toString());
        mTvAllmoney.setText(UtilTool.removeZero(mCount + ""));
    }

    private void showPWDialog() {
        PWDDialog pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                sendRed(password);
            }
        });
        String coins = mTvCurrency.getText().toString();
        double count =UtilTool.parseDouble(mEtRedCount.getText().toString()) *UtilTool.parseDouble(mEtMoneyCount.getText().toString());
        pwdDialog.showDialog(UtilTool.removeZero(count + ""),coins,coins+getString(R.string.red_package),logo,null);
    }

    private void showDialog() {
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
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendQRCodeRedActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
        if (MyApp.getInstance().mRedCoinList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            addCoin.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mRedCoinList));
        } else {
            recyclerView.setVisibility(View.GONE);
            addCoin.setVisibility(View.VISIBLE);
        }
    }

    public void hideDialog(String name, int id, String logo) {
        this.logo=logo;
        mBottomDialog.dismiss();
        mTvCoin.setText(name);
        mTvCurrency.setText(name);
        Glide.with(this).load(logo).into(mImageLogo);
    }

    private void sendRed(String password) {
        String coin = mTvCoin.getText().toString();
        int count = Integer.parseInt(mEtRedCount.getText().toString());
        double money = Double.parseDouble(mEtMoneyCount.getText().toString());
        double single = money / count;
        mRedPacketPresenter.sendRedPacket("code", 2, coin, getString(R.string.congratulation), 2, count, single, money, password, new RedPacketPresenter.CallBack() {
            @Override
            public void send(int id) {
                String code = UtilTool.base64PetToJson(SendQRCodeRedActivity.this, Constants.REDPACKAGE, "redID", id + "", getString(R.string.red_package));
                Intent intent = new Intent(SendQRCodeRedActivity.this, QRCodeRedActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("type", true);
                startActivity(intent);
            }
        });
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtMoneyCount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtMoneyCount);
        } else if (mEtRedCount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtRedCount);
        } else if (mTvCoin.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvCoin);
        } else {
            return true;
        }
        return false;
    }

    public void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this, R.style.dialog);
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
                startActivity(new Intent(SendQRCodeRedActivity.this, PayPasswordActivity.class));
            }
        });
    }

}
