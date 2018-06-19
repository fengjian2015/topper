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

import com.bclould.tocotalk.Presenter.BlockchainGuessPresenter;
import com.bclould.tocotalk.Presenter.CoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.CoinListInfo;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter;
import com.bclould.tocotalk.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class StartGuessActivity extends BaseActivity {

    private static final int DEADLINE = 0;
    private static final int GUESS_TYPE = 1;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.tv_type)
    TextView mTvType;
    @Bind(R.id.rl_guess_type)
    RelativeLayout mRlGuessType;
    @Bind(R.id.et_command)
    EditText mEtCommand;
    @Bind(R.id.cv_password)
    CardView mCvPassword;
    @Bind(R.id.et_guess_title)
    EditText mEtGuessTitle;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.rl_selector_coin)
    RelativeLayout mRlSelectorCoin;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.et_single_insert_count)
    EditText mEtSingleInsertCount;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_single_insert_count)
    RelativeLayout mRlSingleInsertCount;
    @Bind(R.id.et_bet_count)
    EditText mEtBetCount;
    @Bind(R.id.tv_deadline)
    TextView mTvDeadline;
    @Bind(R.id.rl_selector_deadline)
    RelativeLayout mRlSelectorDeadline;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    private Dialog mBottomDialog;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private MNPasswordEditText mEtPassword;
    private ArrayList<Map<String, String>> valueList;
    private GridView mGridView;
    private int mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_guess);
        ButterKnife.bind(this);
        setData();
        initData();
    }

    private void initData() {
        MyApp.getInstance().mBetCoinList.clear();
        if (MyApp.getInstance().mBetCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("bet", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    UtilTool.Log(getString(R.string.coins), data.size() + "");
                    MyApp.getInstance().mBetCoinList.addAll(data);
                }
            });
        }
    }

    private void setData() {
        mTimeList.add(getString(R.string.time_deadline));
        mTimeList.add(getString(R.string.time_deadline2));
        mTimeList.add(getString(R.string.time_deadline3));
        mTimeList.add(getString(R.string.time_deadline4));
        mTypeList.add(getString(R.string.start_guess_type));
        mTypeList.add(getString(R.string.start_guess_type2));
    }

    @OnClick({R.id.bark, R.id.rl_selector_coin, R.id.rl_selector_deadline, R.id.btn_confirm, R.id.rl_guess_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_selector_coin:
                showCoinDialog();
                break;
            case R.id.rl_selector_deadline:
                showBottomDialog(DEADLINE, mTimeList);
                break;
            case R.id.rl_guess_type:
                showBottomDialog(GUESS_TYPE, mTypeList);
                break;
            case R.id.btn_confirm:
                if (checkEdit()) {
                    showPWDialog();
                }
                break;
        }
    }

    private boolean checkEdit() {
        if (mTvType.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_guess_type), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlGuessType);
        } else if (mEtGuessTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_guess_title), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtGuessTitle);
        } else if (mTvCoin.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorCoin);
        } else if (mEtBetCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_bet_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtBetCount);
        } else if (mTvDeadline.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_dealine), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorDeadline);
        }else if (mEtSingleInsertCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_single_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtSingleInsertCount);
        } else if (mCvPassword.getVisibility() == View.VISIBLE) {
            if (mEtCommand.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_guess_pw), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEtCommand);
            } else if (mEtCommand.getText().length() < 4) {
                Toast.makeText(this, getString(R.string.toast_command_min), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEtCommand);
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    List<String> mTimeList = new ArrayList<>();
    List<String> mTypeList = new ArrayList<>();

    private void showBottomDialog(int type, List<String> list) {
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
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter(this, list, type));
        tvTitle.setText(getString(R.string.selector_deadline));
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mBetCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartGuessActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selector_coin));
    }

    public void hideDialog(String name, int sign) {
        mBottomDialog.dismiss();
        if (sign == DEADLINE) {
            mTvDeadline.setText(name);
        } else if (sign == GUESS_TYPE) {
            mTvType.setText(name);
            if (name.equals(getString(R.string.start_guess_type))) {
                mCvPassword.setVisibility(View.GONE);
            } else {
                mCvPassword.setVisibility(View.VISIBLE);
            }
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
        String coins = mTvCoin.getText().toString();
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
//        countCoin.setText(str + coins);
        countCoin.setText(getString(R.string.push_guess) + coins + getString(R.string.msg));
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
                    pushing(password);
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

    private void pushing(String password) {
        String title = mEtGuessTitle.getText().toString();
        String count = mEtBetCount.getText().toString();
        String singleCount = mEtSingleInsertCount.getText().toString();
        String command = mEtCommand.getText().toString();
        String deadline = mTvDeadline.getText().toString();
        String timeMinute = Integer.parseInt(deadline.substring(0, deadline.lastIndexOf(getString(R.string.hr)))) * 60 + "";
        UtilTool.Log("時間", timeMinute);
        BlockchainGuessPresenter blockchainGuessPresenter = new BlockchainGuessPresenter(this);
        blockchainGuessPresenter.pushingGuess(mId, title, count, timeMinute, count, password, singleCount, command, new BlockchainGuessPresenter.CallBack2() {
            @Override
            public void send() {
                Toast.makeText(StartGuessActivity.this, getString(R.string.publish_succeed), Toast.LENGTH_SHORT).show();
                finish();
                EventBus.getDefault().post(new MessageEvent(getString(R.string.push_guess)));
            }
        });
    }

    public void hideDialog2(CoinListInfo.DataBean data) {
        mBottomDialog.dismiss();
        mTvCoin.setText(data.getName());
        mId = data.getId();
        mEtSingleInsertCount.setHint(data.getSingle_coin());
        mTvHint.setText(getString(R.string.start_guess_service_hint) + Double.parseDouble(data.getBet_fee()) * 100 + "%" + getString(R.string.sxf));
    }
}
