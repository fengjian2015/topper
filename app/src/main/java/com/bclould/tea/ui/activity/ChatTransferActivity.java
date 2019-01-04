package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/2.
 */

public class ChatTransferActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.rl_selector_coin)
    RelativeLayout mRlSelectorCoin;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private Dialog mBottomDialog;
    private String mUser;
    private RedPacketPresenter mRedPacketPresenter;
    private DBManager mMgr;
    private String mCount;
    private String mRemark;
    private String mName;
    private String mCoin;
    private DBRoomManage mdbRoomManage;
    private PWDDialog pwdDialog;
    private String logo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_transfer);
        ButterKnife.bind(this);
        setTitle(getString(R.string.transfer_accounts),getString(R.string.transfer_record));
        mMgr = new DBManager(this);
        mdbRoomManage = new DBRoomManage(this);
        mRedPacketPresenter = new RedPacketPresenter(this);
        initData();
        initIntent();
    }

    private void initData() {
        MyApp.getInstance().mCoinList.clear();
        if (MyApp.getInstance().mCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("trans", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (ActivityUtil.isActivityOnTop(ChatTransferActivity.this)) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                        if (MyApp.getInstance().mCoinList.size() == 0)
                            MyApp.getInstance().mCoinList.addAll(data);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(ChatTransferActivity.this)) {
                        mLlData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        mUser = intent.getStringExtra("user");
        mName = mMgr.findUserName(mUser);
        if (StringUtils.isEmpty(mName)) {
            mName = mdbRoomManage.findRoomName(mUser);
        }
        mTvName.setText(mName);
        String remark = mMgr.queryRemark(mUser);
        if (remark.isEmpty()) {
            mTvName.setText(mName);
        } else {
            mTvName.setText(remark);
        }
//        mIvTouxiang.setImageBitmap(UtilTool.getImage(mMgr, jid, ChatTransferActivity.this));
        UtilTool.getImage(mMgr, mUser, ChatTransferActivity.this, mIvTouxiang);
    }

    @OnClick({R.id.ll_error, R.id.bark, R.id.tv_add, R.id.rl_selector_coin, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.tv_add:
                Intent intent = new Intent(this, BillDetailsActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            case R.id.rl_selector_coin:
                showDialog();
                break;
            case R.id.btn_confirm:
                if (checkEidt()) {
                    showPWDialog();
                }
                break;
        }
    }

    private boolean checkEidt() {
        if (mEtCount.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
        } else if (mTvCoin.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorCoin);
            Toast.makeText(this, getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void showPWDialog() {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                transfer(password);
            }
        });
        String coins = mTvCoin.getText().toString();
        String count = mEtCount.getText().toString();
        pwdDialog.showDialog(count,coins,coins + getString(R.string.transfer),logo,null);
    }

    private void transfer(String password) {
        mCount = mEtCount.getText().toString();
        mRemark = mEtRemark.getText().toString();
        if (mRemark.isEmpty()) {
            mRemark = getString(R.string.transfer_give) + mTvName.getText().toString();
        }
        mCoin = mTvCoin.getText().toString();
        mRedPacketPresenter.transgerfriend(mCoin, mUser, Double.parseDouble(mCount), password, mRemark);
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
                pwdDialog.show();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(ChatTransferActivity.this, PayPasswordActivity.class));
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
                startActivity(new Intent(ChatTransferActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.coins));
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

    public void hideDialog(String name, int id, String logo) {
        mBottomDialog.dismiss();
        mTvCoin.setText(name);
        this.logo=logo;
    }

    public void sendMessage() {
        RoomManage.getInstance().getRoom(mUser).sendTransfer(mRemark, mCoin, mCount);
        finish();
    }
}
