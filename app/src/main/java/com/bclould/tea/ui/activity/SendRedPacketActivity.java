package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.RedPacketPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.xmpp.RoomManage;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2017/12/28.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SendRedPacketActivity extends AppCompatActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_currency)
    TextView mTvCurrency;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.rl_selector_currency)
    RelativeLayout mRlSelectorCurrency;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    private Dialog mBottomDialog;
    private DBManager mMgr;
    private String mUser;
    private Dialog mRedDialog;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private MNPasswordEditText mEtPassword;
    private GridView mGridView;
    private RedPacketPresenter mRedPacketPresenter;
    private String mRemark;
    private double mCount;
    private String mCoin;
//    List<CoinInfo.DataBean> mCoinList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket4));
        mMgr = new DBManager(this);
        setContentView(R.layout.activity_send_red_packet);
        mUser = getIntent().getStringExtra("user");
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        MyApp.getInstance().mCoinList.clear();
        if (MyApp.getInstance().mCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("trans", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (MyApp.getInstance().mCoinList.size() == 0)
                        MyApp.getInstance().mCoinList.addAll(data);
                }
            });
        }
    }

    @OnClick({R.id.bark, R.id.rl_selector_currency, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_selector_currency:
                showDialog();
                break;
            case R.id.btn_send:
                if (mEtCount.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
                    AnimatorTool.getInstance().editTextAnimator(mEtCount);
                } else {
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
        String coins = mTvCurrency.getText().toString();
        String count = mEtCount.getText().toString();
        String remark = mEtRemark.getText().toString();
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
        countCoin.setText(count + coins);
        coin.setText(coins + getString(R.string.red_package));
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
                    sendRed(password);
                }
            }
        });
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
                startActivity(new Intent(SendRedPacketActivity.this, PayPasswordActivity.class));
            }
        });
    }

    private void sendRed(String password) {
        mRedPacketPresenter = new RedPacketPresenter(this);
        mCoin = mTvCurrency.getText().toString();
        mCount = Double.parseDouble(mEtCount.getText().toString());
        mRemark = mEtRemark.getText().toString();
        if (mRemark.isEmpty())
            mRemark = getString(R.string.congratulation);
        int type = 1;
        int redCount = 1;
        double redSum = 0;
        mRedPacketPresenter.sendRedPacket(mUser, type, mCoin, mRemark, 1, redCount, redSum, mCount, password, new RedPacketPresenter.CallBack() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void send(int id) {
                setData(id);
            }
        });

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
                startActivity(new Intent(SendRedPacketActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
        if (MyApp.getInstance().mCoinList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            addCoin.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mCoinList));
        } else {
            recyclerView.setVisibility(View.GONE);
            addCoin.setVisibility(View.VISIBLE);
        }

    }

    public void hideDialog(String name) {
        mBottomDialog.dismiss();
        mTvCurrency.setText(name);
        mTvCoin.setText(name);
    }

    private ArrayList<Map<String, String>> valueList;
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

    public void setData(int id) {
        RoomManage.getInstance().addSingleMessageManage(mUser, mMgr.findConversationName(mUser)).sendRed(mRemark, mCoin, mCount, id);
        finish();
    }
}