package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.Presenter.RedPacketPresenter;
import com.bclould.tea.R;
import com.bclould.tea.alipay.AlipayClient;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendRedGroupAlipaylActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_money_state)
    TextView mTvMoneyState;
    @Bind(R.id.et_money)
    EditText mEtMoney;
    @Bind(R.id.tv_change_red)
    TextView mTvChangeRed;
    @Bind(R.id.et_number)
    EditText mEtNumber;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_group_number)
    TextView mTvGroupNumber;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_allmoney)
    TextView mTvAllmoney;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    private String roomId;
    private boolean isluckyRed;
    private DBManager mMgr;
    private DBRoomMember mDBRoomMember;
    private double mCount;
    private PWDDialog mPwdDialog;
    private double singleMoney;
    private String mRemark;
    private DeleteCacheDialog mDeleteCacheDialog;
    private RedPacketPresenter mRedPacketPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket5));
        }
        setContentView(R.layout.activity_send_red_alipay_group);
        ButterKnife.bind(this);
        init();
        setOnClick();
    }

    private void setOnClick() {
        mEtMoney.addTextChangedListener(new TextWatcher() {
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
        mEtNumber.addTextChangedListener(new TextWatcher() {
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

    private void init() {
        mMgr = new DBManager(this);
        mRedPacketPresenter = new RedPacketPresenter(this);
        mDBRoomMember = new DBRoomMember(this);
        roomId = getIntent().getStringExtra("roomId");
        mTvGroupNumber.setText(getString(R.string.members_of_the_group) + mDBRoomMember.queryAllRequest(roomId).size() + getString(R.string.person1));
        mEtMoney.setSelection(mEtMoney.getText().length());
        mEtNumber.setSelection(mEtNumber.getText().length());
        changeRed();
    }

    private void changeRed() {
        if (isluckyRed) {
            CharSequence str = getText(R.string.lucky_red_common_red);
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    isluckyRed = false;
                    changeRed();
                    changeAllMoney();
                }
            }, str.length() - 6, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue2)), str.length() - 6, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvChangeRed.setText(spannableString);
            mTvChangeRed.setMovementMethod(LinkMovementMethod.getInstance());
            mTvMoneyState.setText(getString(R.string.total_money));
        } else {
            CharSequence str = getText(R.string.common_red_lucky_red);
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    isluckyRed = true;
                    changeRed();
                    changeAllMoney();
                }
            }, str.length() - 7, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue2)), str.length() - 7, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvChangeRed.setText(spannableString);
            mTvChangeRed.setMovementMethod(LinkMovementMethod.getInstance());
            mTvMoneyState.setText(getString(R.string.a_single_amount));
        }
    }

    private void changeAllMoney() {
        if (isluckyRed) {
            mCount = UtilTool.parseDouble(mEtMoney.getText().toString());
        } else {
            mCount = UtilTool.parseDouble(mEtMoney.getText().toString()) * UtilTool.parseDouble(mEtNumber.getText().toString());
        }
        mTvAllmoney.setText(UtilTool.removeZero(mCount + "CNY"));
    }

    @OnClick({R.id.bark, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_send:
                if (checkEdit()) {
                    if (UtilTool.getUUID().isEmpty()) {
                        showBindDialog();
                    } else {
                        showPWDialog();
                    }
                }
                break;
        }
    }

    private void showBindDialog() {
        if (mDeleteCacheDialog == null) {
            mDeleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        }
        mDeleteCacheDialog.show();
        mDeleteCacheDialog.setTitle(getString(R.string.alipay_red_bind_hint));
        Button cancel = (Button) mDeleteCacheDialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteCacheDialog.dismiss();

            }
        });

        Button confirm = (Button) mDeleteCacheDialog.findViewById(R.id.btn_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteCacheDialog.dismiss();
                startActivity(new Intent(SendRedGroupAlipaylActivity.this, PersonalDetailsActivity.class));
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
                mPwdDialog.show();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(SendRedGroupAlipaylActivity.this, PayPasswordActivity.class));
            }
        });
    }

    private void showPWDialog() {
        String coins = "CNY";
        if (mPwdDialog == null) {
            mPwdDialog = new PWDDialog(this);
        }
        mPwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                sendRed(password);
            }
        });
        if (isluckyRed) {
            mCount = Double.parseDouble(mEtMoney.getText().toString());
        } else {
            mCount = Double.parseDouble(mEtMoney.getText().toString()) * Double.parseDouble(mEtNumber.getText().toString());
        }
        mPwdDialog.showDialog(UtilTool.removeZero(mCount + ""), coins, coins + getString(R.string.red_package), "", null);
    }

    private void sendRed(String password) {
        int type;
        if (isluckyRed) {
            mCount = Double.parseDouble(mEtMoney.getText().toString());
            type = 3;
        } else {
            mCount = Double.parseDouble(mEtMoney.getText().toString()) * Double.parseDouble(mEtNumber.getText().toString());
            singleMoney = Double.parseDouble(mEtMoney.getText().toString());
            type = 2;
        }
        int redCount = Integer.parseInt(mEtNumber.getText().toString());
        mRemark = mEtRemark.getText().toString();
        if (mRemark.isEmpty())
            mRemark = getString(R.string.congratulation);
        mRedPacketPresenter.sendRedPacket(roomId, type, "CNY", mRemark, 3, redCount, singleMoney, mCount, password, new RedPacketPresenter.CallBack() {
            @Override
            public void send(final int id, String response) {
                AlipayClient.getInstance().payV2(SendRedGroupAlipaylActivity.this, response, new PersonalDetailsPresenter.CallBack7() {
                    @Override
                    public void send(String userId) {
                        setData(id);
                    }
                });
            }
        });
    }

    public void setData(int id) {
        RoomManage.getInstance().addMultiMessageManage(roomId, mMgr.findConversationName(roomId)).sendRed(mRemark, "CNY", mCount, id);
        finish();
    }

    private boolean checkEdit() {
        int number = UtilTool.parseInt(mEtNumber.getText().toString());
        double money = UtilTool.parseDouble(mEtMoney.getText().toString());
        if (number == 0 || money == 0) {
            Toast.makeText(this, getString(R.string.toast_count_and_money), Toast.LENGTH_SHORT).show();
            return false;
        } else if (number > 100) {
            Toast.makeText(this, getString(R.string.group_red_max_out), Toast.LENGTH_SHORT).show();
            return false;
        } else if (number > mDBRoomMember.queryAllRequest(roomId).size()) {
            Toast.makeText(this, getString(R.string.group_red_max_out_member), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isluckyRed && money > 200) {
            Toast.makeText(this, getString(R.string.group_red_max_money), Toast.LENGTH_SHORT).show();
            return false;
        } else if (isluckyRed && (number * 0.01) > money) {
            Toast.makeText(this, getString(R.string.group_red_min_money), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
