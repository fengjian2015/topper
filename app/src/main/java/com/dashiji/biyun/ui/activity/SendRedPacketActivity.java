package com.dashiji.biyun.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.dashiji.biyun.Presenter.RedPacketPresenter;
import com.dashiji.biyun.R;
import com.dashiji.biyun.history.DBManager;
import com.dashiji.biyun.model.ConversationInfo;
import com.dashiji.biyun.model.MessageInfo;
import com.dashiji.biyun.ui.adapter.BottomDialogRVAdapter2;
import com.dashiji.biyun.ui.widget.DeleteCacheDialog;
import com.dashiji.biyun.ui.widget.VirtualKeyboardView;
import com.dashiji.biyun.utils.AnimatorTool;
import com.dashiji.biyun.utils.Constants;
import com.dashiji.biyun.utils.MessageEvent;
import com.dashiji.biyun.xmpp.XmppConnection;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jxmpp.jid.impl.JidCreate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dashiji.biyun.R.style.BottomDialog;

/**
 * Created by GA on 2017/12/28.
 */

public class SendRedPacketActivity extends AppCompatActivity {


    String[] mCoinArr = {"TPC", "BTC", "LTC", "DOGO", "ZEC", "LSK", "MAID", "SHC", "ANS"};
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.redpacket3));
        mMgr = new DBManager(this);
        setContentView(R.layout.activity_send_red_packet);
        mUser = getIntent().getStringExtra("user");
        ButterKnife.bind(this);
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
        coin.setText(coins + "红包");
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
            mRemark = Constants.REDDEFAULT;
        int type = 1;
        int redCount = 1;
        double redSum = 0;
        mRedPacketPresenter.sendRedPacket(mUser, type, mCoin, mRemark, 1, redCount, redSum, mCount, password, new RedPacketPresenter.CallBack() {
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
        tvTitle.setText("选择币种");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter2(this, mCoinArr));

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
        String message = Constants.REDBAG + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount + Constants.CHUANCODE + id;
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(message);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setRemark(mRemark);
            messageInfo.setCoin(mCoin);
            messageInfo.setCount(mCount + "");
            messageInfo.setState(0);
            messageInfo.setRedId(id);
            mMgr.addMessage(messageInfo);
            String hint = "[" + mCoin + "]" + mRemark;
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, hint, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mUser.substring(0, mUser.indexOf("@")));
                info.setUser(mUser);
                info.setMessage(hint);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent("发红包了"));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
