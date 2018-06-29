package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.RealNamePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter3;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RealNameC1Activity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.card_type)
    TextView mCardType;
    @Bind(R.id.cv_card_type)
    CardView mCvCardType;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.et_number)
    EditText mEtNumber;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.ll_pass)
    LinearLayout mLlPass;
    @Bind(R.id.ll_no_pass)
    LinearLayout mLlNoPass;
    @Bind(R.id.tv_auth_type)
    TextView mTvAuthType;
    @Bind(R.id.btn_auth)
    Button mBtnAuth;
    @Bind(R.id.iv_auth_type)
    ImageView mIvAuthType;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.iv_jiantou2)
    ImageView mIvJiantou2;
    @Bind(R.id.rl_selector_state)
    RelativeLayout mRlSelectorState;
    @Bind(R.id.iv_jiantou)
    ImageView mIvJiantou;
    @Bind(R.id.tv_cause)
    TextView mTvCause;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.rl_data)
    RelativeLayout mRlData;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;
    private RealNamePresenter mRealNamePresenter;
    private String mType;
    private Dialog mBottomDialog;
    private int mId;
    private String mName_zh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_namec1);
        ButterKnife.bind(this);
        mRealNamePresenter = new RealNamePresenter(this);
        MyApp.getInstance().addActivity(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);//初始化EventBus
        }
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//初始化EventBus
    }

    private void initData() {
        mRealNamePresenter.realNameInfo(new RealNamePresenter.CallBack2() {
            @Override
            public void send(int type, String mark) {
                mLlError.setVisibility(View.GONE);
                mRlData.setVisibility(View.VISIBLE);
                if (type == 3) {
                    mLlNoPass.setVisibility(View.GONE);
                    mBtnAuth.setVisibility(View.GONE);
                    mTvAuthType.setText(getString(R.string.verify_succeed));
                    mTvCause.setVisibility(View.GONE);
                    mLlPass.setVisibility(View.VISIBLE);
                    mIvAuthType.setImageResource(R.mipmap.shenhetongguo);
                } else if (type == 4) {
                    mTvCause.setVisibility(View.VISIBLE);
                    mTvCause.setText(mark);
                    mIvAuthType.setImageResource(R.mipmap.shenheshibai);
                    mLlNoPass.setVisibility(View.GONE);
                    mLlPass.setVisibility(View.VISIBLE);
                    mTvAuthType.setText(getString(R.string.verify_error));
                    mBtnAuth.setVisibility(View.VISIBLE);
                } else if (type == 2) {
                    mTvCause.setVisibility(View.GONE);
                    mIvAuthType.setImageResource(R.mipmap.shenhezhong);
                    mLlNoPass.setVisibility(View.GONE);
                    mBtnAuth.setVisibility(View.GONE);
                    mTvAuthType.setText(getString(R.string.check_pending));
                    mLlPass.setVisibility(View.VISIBLE);
                } else if (type == 1) {
                    mLlNoPass.setVisibility(View.VISIBLE);
                    mLlPass.setVisibility(View.GONE);
                }
            }

            @Override
            public void error() {
                mLlError.setVisibility(View.VISIBLE);
                mRlData.setVisibility(View.GONE);
            }
        });
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.real_name_verify))) {
            initData();
        }
    }

    @OnClick({R.id.ll_error, R.id.bark, R.id.cv_card_type, R.id.btn_next, R.id.btn_auth, R.id.rl_selector_state})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.cv_card_type:
                showPopup();
                break;
            case R.id.rl_selector_state:
                showStateDialog();
                break;
            case R.id.btn_next:
                if (checkEdit()) {
                    submit();
                }
                break;
            case R.id.btn_auth:
                mLlNoPass.setVisibility(View.VISIBLE);
                mLlPass.setVisibility(View.GONE);
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }

    private void showStateDialog() {
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
        tvTitle.setText(getString(R.string.selecotr_state));
        if (MyApp.getInstance().mStateList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            addCoin.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new BottomDialogRVAdapter3(this, MyApp.getInstance().mStateList));
        } else {
            recyclerView.setVisibility(View.GONE);
            addCoin.setVisibility(View.VISIBLE);
        }
    }

    public void hideDialog(String name) {
        mBottomDialog.dismiss();
        mTvState.setText(name);
    }

    private void showPopup() {
        mView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.pop_gong_gao, null);
        mPopupWindow = new PopupWindow(mView, mCvCardType.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAsDropDown(mCvCardType, 0, 10);
        final TextView shenfen = (TextView) mView.findViewById(R.id.tv_shenfen);
        shenfen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mCardType.setText(shenfen.getText());
                mTv.setText(getString(R.string.id_card_number));
            }
        });
        final TextView huzhao = (TextView) mView.findViewById(R.id.tv_huzhao);
        huzhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mCardType.setText(huzhao.getText());
                mTv.setText(R.string.passport_number);
            }
        });
    }

    private void submit() {
        String name = mEtName.getText().toString().trim();
        String cardNumber = mEtNumber.getText().toString().trim();
        String cardType = mCardType.getText().toString().trim();
        mType = "";
        if (cardType.equals(getString(R.string.id_card))) {
            mType = "1";
        } else {
            mType = "2";
        }
        mRealNamePresenter.realNameVerify(name, cardNumber, mId, mType, new RealNamePresenter.CallBack() {
            @Override
            public void send(int message) {
                Intent intent = new Intent(RealNameC1Activity.this, UpIdCardActivity.class);
                intent.putExtra("type", mType);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean checkEdit() {
        if (mEtName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_name), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtName);
        } else if (mEtNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_id_card), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtNumber);
        } else if (mCardType.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_id_card_type), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mCardType);
        } else if (mTvState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_nationality), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorState);
        } else {
            return true;
        }

        return false;
    }

    public void hideDialog(int id, String name_zh) {
        mBottomDialog.dismiss();
        mId = id;
        mName_zh = name_zh;
        mTvState.setText(name_zh);
    }
}
