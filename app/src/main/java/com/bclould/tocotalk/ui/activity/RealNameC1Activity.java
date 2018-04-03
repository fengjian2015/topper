package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.Intent;
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

import com.bclould.tocotalk.Presenter.RealNamePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter3;
import com.bclould.tocotalk.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

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
        mRealNamePresenter.realNameInfo(new RealNamePresenter.CallBack() {
            @Override
            public void send(String message) {
                if (message.equals("认证成功")) {
                    mLlNoPass.setVisibility(View.GONE);
                    mBtnAuth.setVisibility(View.GONE);
                    mTvAuthType.setText(message);
                    mLlPass.setVisibility(View.VISIBLE);
                } else if (message.equals("认证失败")) {
                    mIvAuthType.setImageResource(R.mipmap.shenheshibai);
                    mLlNoPass.setVisibility(View.GONE);
                    mLlPass.setVisibility(View.VISIBLE);
                    mTvAuthType.setText(message);
                    mBtnAuth.setVisibility(View.VISIBLE);
                    mTvAuthType.setText(message);
                } else if (message.equals("待审核")) {
                    mIvAuthType.setImageResource(R.mipmap.shenhezhong);
                    mLlNoPass.setVisibility(View.GONE);
                    mBtnAuth.setVisibility(View.GONE);
                    mTvAuthType.setText(message);
                    mLlPass.setVisibility(View.VISIBLE);
                } else if (message.equals("未认证")) {
                    mLlNoPass.setVisibility(View.VISIBLE);
                    mLlPass.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.bark, R.id.cv_card_type, R.id.btn_next, R.id.btn_auth, R.id.rl_selector_state})
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
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RealNameC1Activity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText("选择国家");
        if (MyApp.getInstance().mCoinList.size() != 0) {
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
        mPopupWindow.showAsDropDown(mCvCardType, 0, 10);
        final TextView shenfen = (TextView) mView.findViewById(R.id.tv_shenfen);
        shenfen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mCardType.setText(shenfen.getText());
                mTv.setText("身份证号码");
            }
        });
        final TextView huzhao = (TextView) mView.findViewById(R.id.tv_huzhao);
        huzhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mCardType.setText(huzhao.getText());
                mTv.setText("护照号码");
            }
        });
    }

    private void submit() {
        String name = mEtName.getText().toString().trim();
        String cardNumber = mEtNumber.getText().toString().trim();
        String cardType = mCardType.getText().toString().trim();
        mType = "";
        if (cardType.equals("身份证")) {
            mType = "1";
        } else {
            mType = "2";
        }
        mRealNamePresenter.realNameVerify(name, cardNumber, mId, mType, new RealNamePresenter.CallBack() {
            @Override
            public void send(String message) {
                Intent intent = new Intent(RealNameC1Activity.this, UpIdCardActivity.class);
                intent.putExtra("type", mType);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean checkEdit() {
        if (mEtName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtName);
        } else if (mEtNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "证件号码不能为空", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtNumber);
        } else if (mCardType.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "证件类型不能为空", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mCardType);
        } else if (mTvState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "国籍不能为空", Toast.LENGTH_SHORT).show();
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
