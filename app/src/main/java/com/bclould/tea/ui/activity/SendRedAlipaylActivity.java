package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/9/3.
 */

public class SendRedAlipaylActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.tv_money_state)
    TextView mTvMoneyState;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv1)
    TextView mTv1;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_allmoney)
    TextView mTvAllmoney;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    private String mRoomId;
    private DeleteCacheDialog mDeleteCacheDialog;
    private PWDDialog mPwdDialog;
    private double mCount;
    private String mRemark;
    private RedPacketPresenter mRedPacketPresenter;
    private DBManager mMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket5));
        }
        setContentView(R.layout.activity_send_red_alipay);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        mRoomId = getIntent().getStringExtra("roomId");
        mRedPacketPresenter = new RedPacketPresenter(this);
        setOnClick();
    }

    private void setOnClick() {
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTvAllmoney.setText(mEtCount.getText().toString() + "CNY");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                startActivity(new Intent(SendRedAlipaylActivity .this, PayPasswordActivity.class));
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
        mCount = Double.parseDouble(mEtCount.getText().toString());
        mPwdDialog.showDialog(UtilTool.removeZero(mCount + ""), coins, coins + getString(R.string.red_package), "", null);
    }

    private void sendRed(String password) {
        mCount = Double.parseDouble(mEtCount.getText().toString());
        int type = 1;
        int redCount = 1;
        mRemark = mEtRemark.getText().toString();
        if (mRemark.isEmpty())
            mRemark = getString(R.string.congratulation);
        double redSum = 0;
        mRedPacketPresenter.sendRedPacket(mRoomId, type, "CNY", mRemark, 3, redCount, redSum, mCount, password, new RedPacketPresenter.CallBack() {
            @Override
            public void send(final int id, String response) {
                AlipayClient.getInstance().payV2(SendRedAlipaylActivity.this, response, new PersonalDetailsPresenter.CallBack7() {
                    @Override
                    public void send(String userId) {
                        setData(id);
                    }
                });
            }
        });
    }

    public void setData(int id) {
        RoomManage.getInstance().addSingleMessageManage(mRoomId, mMgr.findConversationName(mRoomId)).sendRed(mRemark, "CNY", mCount, id);
        finish();
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
                startActivity(new Intent(SendRedAlipaylActivity.this, PersonalDetailsActivity.class));
            }
        });
    }

    private boolean checkEdit() {
        if (mEtCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            return false;
        } else if (Double.parseDouble(mEtCount.getText().toString()) > 200) {
            Toast.makeText(this, getString(R.string.group_red_max_money), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            return false;
        } else if (Double.parseDouble(mEtCount.getText().toString()) < 0.01) {
            Toast.makeText(this, getString(R.string.group_red_min_money), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            return false;
        }
        return true;
    }
}
