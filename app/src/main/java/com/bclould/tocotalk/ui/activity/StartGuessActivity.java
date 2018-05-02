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
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.et_guess_title)
    EditText mEtGuessTitle;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.rl_selector_coin)
    RelativeLayout mRlSelectorCoin;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_deadline)
    TextView mTvDeadline;
    @Bind(R.id.rl_selector_deadline)
    RelativeLayout mRlSelectorDeadline;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.tv_single_insert_count)
    TextView mTvSingleInsertCount;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_single_insert_count)
    RelativeLayout mRlSingleInsertCount;
    @Bind(R.id.et_command)
    EditText mEtCommand;
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
    }

    @OnClick({R.id.bark, R.id.rl_selector_coin, R.id.rl_selector_deadline, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_selector_coin:
                showCoinDialog();
                break;
            case R.id.rl_selector_deadline:
                showDialog();
                break;
            case R.id.btn_confirm:
                if (checkEdit()) {
                    showPWDialog();
                }
                break;
        }
    }

    private boolean checkEdit() {
        if (mEtGuessTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_guess_title), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtGuessTitle);
        } else if (mTvCoin.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_coin), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorCoin);
        } else if (mEtCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
        } else if (mTvDeadline.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_dealine), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorDeadline);
        } else if (mTvSingleInsertCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_dealine), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlSingleInsertCount);
        } else if (mEtCommand.getText().toString().length() < 4) {
            Toast.makeText(this, getString(R.string.toast_command_min), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCommand);
        } else {
            return true;
        }
        return false;
    }

    List<String> mTimeList = new ArrayList<>();

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
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BottomDialogRVAdapter(this, mTimeList, 4));
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

    public void hideDialog(String name) {
        mBottomDialog.dismiss();
        mTvDeadline.setText(name);
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
        String count = mEtCount.getText().toString();
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
        coin.setText(getString(R.string.push_guess) + coins + getString(R.string.msg));
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
        String count = mEtCount.getText().toString();
        String singleCount = mTvSingleInsertCount.getText().toString();
        String command = mEtCommand.getText().toString();
        String singleInsertCount = mTvSingleInsertCount.getText().toString();
        int sum = (int) (Double.parseDouble(count) / Double.parseDouble(singleInsertCount));
        String deadline = mTvDeadline.getText().toString();
        String timeMinute = Integer.parseInt(deadline.substring(0, deadline.lastIndexOf(getString(R.string.hr)))) * 60 + "";
        UtilTool.Log("時間", timeMinute);
        BlockchainGuessPresenter blockchainGuessPresenter = new BlockchainGuessPresenter(this);
        blockchainGuessPresenter.pushingGuess(mId, title, count, timeMinute, sum + "", password, singleCount, command, new BlockchainGuessPresenter.CallBack2() {
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
        mTvSingleInsertCount.setText(data.getSingle_coin());
        mTvHint.setText(getString(R.string.start_guess_service_hint) + Double.parseDouble(data.getOut_exchange()) * 100 + "%" + getString(R.string.sxf));
    }
}
