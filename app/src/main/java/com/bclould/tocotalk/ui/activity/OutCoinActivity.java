package com.bclould.tocotalk.ui.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CurrencyInOutPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.AnimatorTool;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class OutCoinActivity extends BaseActivity {

    private static final int SELECTORSITE = 1;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_record)
    TextView mTvRecord;
    @Bind(R.id.tv_out_coin_site)
    TextView mTvOutCoinSite;
    @Bind(R.id.btn_selector_site)
    Button mBtnSelectorSite;
    @Bind(R.id.et_coin_count)
    EditText mEtCoinCount;
    @Bind(R.id.tv_yu_e)
    TextView mTvYuE;
    @Bind(R.id.tv_coin_count)
    TextView mTvCoinCount;
    @Bind(R.id.tv_coin_name)
    TextView mTvCoinName;
    @Bind(R.id.et_google_code)
    EditText mEtGoogleCode;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;

    private int mId;
    private int mSiteId;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private GridView mGridView;
    private MNPasswordEditText mEtPassword;
    private String mCoinName;
    private String mOver;
    private ArrayList<Map<String, String>> valueList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_coin);
        ButterKnife.bind(this);
        initIntent();
        initData();
    }

    private void initData() {
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.outCoinDesc(mId, new CurrencyInOutPresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                if (data.getDesc() != null) {
                    String desc = data.getDesc().replace("\\n", "\n");
                    mTvDesc.setText(desc);
                }
            }
        });
    }

    private void initIntent() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);
        mCoinName = intent.getStringExtra("coinName");
        mOver = intent.getStringExtra("over");
        mTvCoinName.setText(mCoinName);
        mTvCoinCount.setText(mOver);
    }

    @OnClick({R.id.bark, R.id.tv_record, R.id.btn_selector_site, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_record:
                Intent intent2 = new Intent(this, BillDetailsActivity.class);
                intent2.putExtra("type", 1);
                intent2.putExtra("coin_id", mId + "");
                intent2.putExtra("coin_name", mCoinName);
                startActivity(intent2);
                break;
            case R.id.btn_selector_site:
                Intent intent = new Intent(this, OutCoinSiteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", mId);
                bundle.putString("coinName", mCoinName);
                intent.putExtras(bundle);
                startActivityForResult(intent, SELECTORSITE);
                break;
            case R.id.btn_confirm:
                if (editCheck()) {
                    showPWDialog();
                }
                break;
        }
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
        String count = mEtCoinCount.getText().toString();
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
        countCoin.setText(count + mCoinName);
        coin.setText(mCoinName + "提币");
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
                    outCoin(password);
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

    public void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this);
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
                startActivity(new Intent(OutCoinActivity.this, PayPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTORSITE) {
                mSiteId = data.getIntExtra("siteId", 0);
                String site = data.getStringExtra("address");
                mTvOutCoinSite.setText(site);
            }
        }
    }

    private void outCoin(String password) {
        String site = mTvOutCoinSite.getText().toString().trim();
        String count = mEtCoinCount.getText().toString().trim();
        String googleCode = mEtGoogleCode.getText().toString().trim();
        String remark = mEtRemark.getText().toString().trim();
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.coinOutAction(mId + "", mSiteId + "", count, googleCode, password, remark);
    }

    private boolean editCheck() {
        if (mTvOutCoinSite.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_out_coin_site), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvOutCoinSite);
        } else if (mEtCoinCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCoinCount);
        } else if (mEtGoogleCode.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtGoogleCode);
        } else {
            return true;
        }
        return false;
    }
}
