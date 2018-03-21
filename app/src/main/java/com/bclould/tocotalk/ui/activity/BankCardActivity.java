package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.bclould.tocotalk.Presenter.BankCardPresenter;
import com.bclould.tocotalk.Presenter.CurrencyInOutPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.CardListInfo;
import com.bclould.tocotalk.ui.adapter.BankCardRVAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.MessageEvent;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BankCardActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.add_bank_card)
    Button mAddBankCard;
    private ArrayList<Map<String, String>> valueList;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private GridView mGridView;
    private MNPasswordEditText mEtPassword;
    private BankCardPresenter mBankCardPresenter;
    List<CardListInfo.DataBean> mCardList = new ArrayList<>();
    private BankCardRVAdapter mBankCardRVAdapter;
    private OnItemDeleteListener mOnItemDeleteListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);//初始化EventBus
        mBankCardPresenter = new BankCardPresenter(this);
        initRecyclerView();
        initData();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    private void initData() {
        mBankCardPresenter.bankCardList(new BankCardPresenter.CallBack2() {
            @Override
            public void send(List<CardListInfo.DataBean> data) {
                mCardList.addAll(data);
                mBankCardRVAdapter.notifyDataSetChanged();
            }
        });
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if ("银行卡绑定解绑".equals(msg)) {
            initData();
        }
    }

    //初始化条目
    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBankCardRVAdapter = new BankCardRVAdapter(this, mCardList, mBankCardPresenter);
        mRecyclerView.setAdapter(mBankCardRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    boolean isDelete = false;

    //点击事件的处理
    @OnClick({R.id.bark, R.id.add_bank_card, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.add_bank_card:
                showPWDialog();
                break;
            case R.id.tv_delete:
                isDelete = !isDelete;
                try {
                    mOnItemDeleteListener.onDelete(isDelete);
                } catch (Exception e) {
                    Toast.makeText(this, "您还没有添加银行卡", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
        }
    }

    public interface OnItemDeleteListener {
        void onDelete(boolean isDelete);
    }

    public void setOnItemDeleteListener(BankCardActivity.OnItemDeleteListener onItemDeleteListener) {
        mOnItemDeleteListener = onItemDeleteListener;
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
        countCoin.setText("请输入支付密码，以验证身份");
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
                    check(password);
                }
            }
        });
    }

    private void check(String password) {
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.check(password);
    }

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
                startActivity(new Intent(BankCardActivity.this, PayPasswordActivity.class));
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
}
