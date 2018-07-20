package com.bclould.tea.ui.activity;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/5/30.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RewardActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.rl_selector_coin)
    RelativeLayout mRlSelectorCoin;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_record)
    TextView mTvRecord;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private Dialog mBottomDialog;
    private int mId;
    private String mCoinName;
    private DynamicPresenter mDynamicPresenter;
    private String mUrl;
    private String mName;
    private int mDynamic_id;
    private PWDDialog pwdDialog;
    private String logo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mDynamicPresenter = new DynamicPresenter(this);
        initData();
        initIntent();
    }

    private void initData() {
        MyApp.getInstance().mRewardCoinList.clear();
        if (MyApp.getInstance().mRewardCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("reward", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (ActivityUtil.isActivityOnTop(RewardActivity.this)) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                        if (MyApp.getInstance().mRewardCoinList.size() == 0)
                            MyApp.getInstance().mRewardCoinList.addAll(data);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(RewardActivity.this)) {
                        mLlData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mName = intent.getStringExtra("name");
        mDynamic_id = intent.getIntExtra("dynamic_id", 0);
        if (mUrl != null) {
            if (mUrl.isEmpty()) {
                UtilTool.setCircleImg(this, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            } else {
                UtilTool.setCircleImg(this, mUrl, mIvTouxiang);
            }
        } else {
            UtilTool.setCircleImg(this, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
        }
        mTvName.setText(getString(R.string.user) + " : " + mName);
    }

    @OnClick({R.id.ll_error, R.id.tv_record, R.id.bark, R.id.rl_selector_coin, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.tv_record:
                Intent intent = new Intent(this, PayRecordActivity.class);
                intent.putExtra("type", "6");
                startActivity(intent);
                break;
            case R.id.rl_selector_coin:
                showCoinDialog();
                break;
            case R.id.btn_confirm:
                if (checkEidt())
                    showPWDialog();
                break;
        }
    }

    private boolean checkEidt() {
        if (mTvCoin.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mTvCoin);
            ToastShow.showToast2(this, getString(R.string.toast_coin));
        } else if (mEtCount.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            ToastShow.showToast2(this, getString(R.string.toast_count));
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
                reward(password);
            }
        });
        String count = mEtCount.getText().toString();
        pwdDialog.showDialog(count,mCoinName,mCoinName + getString(R.string.reward),logo,null);
    }

    private void reward(String password) {
        String count = mEtCount.getText().toString();
        String remark = mEtRemark.getText().toString();
        mDynamicPresenter.reward(mDynamic_id, count, mId, password, new DynamicPresenter.CallBack() {
            @Override
            public void send() {
                MessageEvent messageEvent = new MessageEvent(getString(R.string.reward_succeed));
                messageEvent.setId(mDynamic_id + "");
                EventBus.getDefault().post(messageEvent);
                finish();
            }
        });
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
        Button addCoin = (Button) mBottomDialog.findViewById(R.id.btn_add_coin);
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mRewardCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RewardActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
    }

    public void hideDialog(String name, int id, String logo) {
        this.logo=logo;
        mId = id;
        mCoinName = name;
        mBottomDialog.dismiss();
        mTvCoin.setText(name);
    }
}
