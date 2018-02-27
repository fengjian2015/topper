package com.bclould.tocotalk.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CurrencyInOutPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2017/11/3.
 */

public class CurrencyInOutActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_coins)
    TextView mTvCoins;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.iv_receive)
    ImageView mIvReceive;
    @Bind(R.id.tv_xx)
    TextView mTvXx;
    @Bind(R.id.rl_receive)
    RelativeLayout mRlReceive;
    @Bind(R.id.tv_send)
    TextView mTvSend;
    @Bind(R.id.tv_xx2)
    TextView mTvXx2;
    @Bind(R.id.rl_send)
    RelativeLayout mRlSend;
    @Bind(R.id.iv_transfer)
    ImageView mIvTransfer;
    @Bind(R.id.tv_xx3)
    TextView mTvXx3;
    @Bind(R.id.rl_transfer)
    RelativeLayout mRlTransfer;
    @Bind(R.id.tv_coin_site)
    TextView mTvCoinSite;
    @Bind(R.id.tv_site)
    TextView mTvSite;
    @Bind(R.id.iv_copy)
    ImageView mIvCopy;
    @Bind(R.id.iv_qr_code)
    ImageView mIvQrCode;
    @Bind(R.id.tv_all_count)
    TextView mTvAllCount;
    @Bind(R.id.ll_receive)
    LinearLayout mLlReceive;
    @Bind(R.id.et_address)
    EditText mEtAddress;
    @Bind(R.id.btn_selecotr_site)
    Button mBtnSelecotrSite;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.ll_send)
    LinearLayout mLlSend;
    @Bind(R.id.et_username)
    EditText mEtUsername;
    @Bind(R.id.et_count2)
    EditText mEtCount2;
    @Bind(R.id.tv_coin2)
    TextView mTvCoin2;
    @Bind(R.id.et_remark2)
    EditText mEtRemark2;
    @Bind(R.id.btn_transfer)
    Button mBtnTransfer;
    @Bind(R.id.ll_transfer)
    LinearLayout mLlTransfer;
    @Bind(R.id.btn_query)
    Button mBtnQuery;
    @Bind(R.id.btn_confirm_send)
    Button mBtnConfirmSend;
    @Bind(R.id.btn_record)
    Button mBtnRecord;
    private int mId;
    private int mAddressId;
    private String mName;
    private CurrencyInOutPresenter mCurrencyInOutPresenter;
    private Dialog mBottomDialog;
    private Dialog mRedDialog;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private MNPasswordEditText mEtPassword;
    private GridView mGridView;
    private ArrayList<Map<String, String>> valueList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_inout);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //初始化界面
    private void initInterface() {
        Intent intent = getIntent();//获取传递的数据
        mId = intent.getIntExtra("id", 0);//币种id
        //币种名字
        mName = intent.getStringExtra("name");
        mTvCoins.setText(UtilTool.exChange(mName));
        mTvCoinSite.setText(UtilTool.exChange(mName) + "地址：");
        String total = intent.getStringExtra("total");//币种总额
        mTvAllCount.setText(total + " " + UtilTool.exChange(mName));
        mTvCoin.setText(UtilTool.exChange(mName));
        mTvCoin2.setText(UtilTool.exChange(mName));
        mRlReceive.setSelected(true);
        mCurrencyInOutPresenter = new CurrencyInOutPresenter(this);
        mCurrencyInOutPresenter.inCoin(mId, new CurrencyInOutPresenter.CallBack() {
            @Override
            public void send(String address) {
                Bitmap bitmap = UtilTool.createQRImage(address);
                if(address != "")
                mTvSite.setText(address);
                mIvQrCode.setImageBitmap(bitmap);
            }
        });
    }

    //点击事件的处理
    @OnClick({R.id.btn_transfer, R.id.btn_record, R.id.btn_query, R.id.bark, R.id.rl_transfer, R.id.rl_receive, R.id.rl_send, R.id.iv_copy, R.id.btn_selecotr_site, R.id.btn_confirm_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_transfer:
                if (checkEdit2()) {
                    showPWDialog();
                }
                break;
            case R.id.rl_receive:
                selectorInOut(mRlReceive);
                break;
            case R.id.rl_transfer:
                selectorInOut(mRlTransfer);
                break;
            case R.id.rl_send:
                selectorInOut(mRlSend);
                break;
            case R.id.btn_record:
                break;
            case R.id.btn_query:
                showQueryDialog();
                break;/*
            case R.id.iv_eye:
                isEye = !isEye;
                showHidePassword(isEye, mIvEye, mEtPayPassword);
                break;
            case R.id.iv_eye2:
                isEye2 = !isEye2;
                showHidePassword(isEye2, mIvEye2, mEtPayPassword2);
                break;*/
            case R.id.iv_copy:
                copySite();
                break;
            case R.id.btn_selecotr_site:
                //携带币种id跳转提币地址页面
                Intent intent = new Intent();
                intent.setClass(this, OutCoinSiteActivity.class);
                intent.putExtra("id", mId);
                startActivityForResult(intent, 0);
                break;
            case R.id.btn_confirm_send:
                //检测输入框
                if (checkEdit())
                    //提币
                    showGoogleDialog();
                break;
        }
    }

    //显示谷歌验证弹窗
    private void showGoogleDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_google_code, null);
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
        Button confirm = (Button) mBottomDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinOutAction();
            }
        });
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
        String count = mEtCount2.getText().toString();
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
        countCoin.setText(count + mName);
        coin.setText("转账" + mName);
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
                    transfer(password);
                }
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

    //转账
    private void transfer(String password) {
        String userName = mEtUsername.getText().toString().trim();
        String s = mEtCount2.getText().toString().trim();
        double count = Double.parseDouble(s);
        mCurrencyInOutPresenter.transfer(mName, userName, count, password);
    }

    //疑问弹窗
    private void showQueryDialog() {
        Dialog bottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_query, null);
        //获得dialog的window窗口
        Window window = bottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        bottomDialog.setContentView(contentView);
        bottomDialog.show();
    }

    //显示隐藏密码
    boolean isEye = false;
    boolean isEye2 = false;

    private void showHidePassword(boolean isEye, ImageView ivEye, EditText etPayPassword) {
        if (isEye) {
            ivEye.setSelected(true);
            etPayPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPayPassword.setSelection(etPayPassword.length());
        } else {
            etPayPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPayPassword.setSelection(etPayPassword.length());
            ivEye.setSelected(false);
        }
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtAddress.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_address), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtAddress);
        } else if (mEtCount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
        } else {
            return true;
        }
        return false;
    }

    //验证手机号和密码
    private boolean checkEdit2() {
        if (mEtUsername.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtUsername);
        } else if (mEtCount2.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount2);
        } else {
            return true;
        }
        return false;
    }

    //提币
    private void coinOutAction() {
        EditText etGoogleCode = (EditText) mBottomDialog.findViewById(R.id.et_google_code);
        String googleCode = etGoogleCode.getText().toString().trim();
        String s = mEtCount.getText().toString().trim();
        int count = Integer.parseInt(s);
        if (!googleCode.isEmpty()) {
            mCurrencyInOutPresenter.coinOutAction(mId, mAddressId, count, googleCode);
        } else {
            AnimatorTool.getInstance().editTextAnimator(etGoogleCode);
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
        }
    }

    //回调跳转页面数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String address = data.getStringExtra("address");
            mAddressId = data.getIntExtra("id", 0);
            mEtAddress.setText(address);
        }
    }

    //拷贝钱包地址
    private void copySite() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(mTvSite.getText());
        Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
    }

    //页面切换
    private void selectorInOut(RelativeLayout rl) {
        if (rl == mRlReceive) {
            mRlReceive.setSelected(true);
            mRlSend.setSelected(false);
            mRlTransfer.setSelected(false);
            mLlReceive.setVisibility(View.VISIBLE);
            mLlSend.setVisibility(View.GONE);
            mLlTransfer.setVisibility(View.GONE);
            mBtnConfirmSend.setVisibility(View.GONE);
            mTvXx.setBackgroundColor(getResources().getColor(R.color.black));
            mTvXx2.setBackgroundColor(getResources().getColor(R.color.white));
            mTvXx3.setBackgroundColor(getResources().getColor(R.color.white));
        } else if (rl == mRlTransfer) {
            mRlReceive.setSelected(false);
            mRlSend.setSelected(false);
            mRlTransfer.setSelected(true);
            mLlReceive.setVisibility(View.GONE);
            mLlSend.setVisibility(View.GONE);
            mLlTransfer.setVisibility(View.VISIBLE);
            mBtnConfirmSend.setVisibility(View.GONE);
            mTvXx.setBackgroundColor(getResources().getColor(R.color.white));
            mTvXx2.setBackgroundColor(getResources().getColor(R.color.white));
            mTvXx3.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            mRlReceive.setSelected(false);
            mRlSend.setSelected(true);
            mRlTransfer.setSelected(false);
            mLlReceive.setVisibility(View.GONE);
            mLlSend.setVisibility(View.VISIBLE);
            mLlTransfer.setVisibility(View.GONE);
            mBtnConfirmSend.setVisibility(View.VISIBLE);
            mTvXx.setBackgroundColor(getResources().getColor(R.color.white));
            mTvXx2.setBackgroundColor(getResources().getColor(R.color.black));
            mTvXx3.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

}
