package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

import com.bclould.tea.Presenter.BlockchainGuessPresenter;
import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.ui.widget.WinningPopWindow;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class StartGuessActivity extends BaseActivity {

    private static final int DEADLINE = 0;
    private static final int GUESS_TYPE = 1;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.tv_type)
    TextView mTvType;
    @Bind(R.id.rl_guess_type)
    RelativeLayout mRlGuessType;
    @Bind(R.id.et_command)
    EditText mEtCommand;
    @Bind(R.id.rl_pw)
    RelativeLayout mRlPw;
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
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.rl_title)
    View mRlTitle;

    private Dialog mBottomDialog;
    private int mId;
    private PWDDialog pwdDialog;

    private int timePosition = 0;

    private WinningPopWindow mWinningPopWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_guess);
        ButterKnife.bind(this);
        setTitle(getString(R.string.start_guess));
        mEtSingleInsertCount.setKeyListener(null);
        EventBus.getDefault().register(this);//初始化EventBus
        setData();
        initData();
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.winning_show)) {
            show(event.getContent());
        } else if (msg.equals(EventBusUtil.winning_shut_down)) {
            shutDown();
        }
    }

    private void show(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWinningPopWindow = new WinningPopWindow(StartGuessActivity.this, content, mRlTitle);
            }
        });
    }

    private void shutDown() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWinningPopWindow != null) {
                    mWinningPopWindow.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//初始化EventBus
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initData() {
        MyApp.getInstance().mBetCoinList.clear();
        if (MyApp.getInstance().mBetCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("bet", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    mLlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    UtilTool.Log(getString(R.string.coins), data.size() + "");
                    MyApp.getInstance().mBetCoinList.addAll(data);
                }

                @Override
                public void error() {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void setData() {
        mTimeList.add(getString(R.string.time_bar2));
        mTimeList.add(getString(R.string.time_bar4));
        mTimeList.add(getString(R.string.time_deadline));
        mTimeList.add(getString(R.string.time_deadline2));
        mTimeList.add(getString(R.string.time_deadline3));
        mTimeList.add(getString(R.string.time_deadline4));
        mTimeListInt.add(10);
        mTimeListInt.add(30);
        mTimeListInt.add(3 * 60);
        mTimeListInt.add(6 * 60);
        mTimeListInt.add(12 * 60);
        mTimeListInt.add(24 * 60);
        mTypeList.add(getString(R.string.start_guess_type));
        mTypeList.add(getString(R.string.start_guess_type2));
    }

    @OnClick({R.id.bark, R.id.rl_selector_coin, R.id.rl_selector_deadline, R.id.btn_confirm, R.id.rl_guess_type, R.id.ll_error})
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
            case R.id.ll_error:
                initData();
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
        } else if (mEtSingleInsertCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_single_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtSingleInsertCount);
        } else if (mRlPw.getVisibility() == View.VISIBLE) {
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
    List<Integer> mTimeListInt = new ArrayList<>();
    List<String> mTypeList = new ArrayList<>();

    private void showBottomDialog(int type, List<String> list) {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom2, null);
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
        tvTitle.setText(getString(R.string.coins));
    }

    public void hideDialog(String name, int sign, int position) {
        timePosition = position;
        mBottomDialog.dismiss();
        if (sign == DEADLINE) {
            mTvDeadline.setText(name);
        } else if (sign == GUESS_TYPE) {
            mTvType.setText(name);
            if (name.equals(getString(R.string.start_guess_type))) {
                mRlPw.setVisibility(View.GONE);
            } else {
                mRlPw.setVisibility(View.VISIBLE);
            }
        }
    }


    private void showPWDialog() {
        pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                pushing(password);
            }
        });
        String coins = mTvCoin.getText().toString();
        pwdDialog.showDialog(getString(R.string.push_guess) + coins + getString(R.string.msg), null, null, null, null);
    }

    private void pushing(String password) {
        String title = mEtGuessTitle.getText().toString();
        String count = mEtBetCount.getText().toString();
        String singleCount = mEtSingleInsertCount.getText().toString();
        String command = mEtCommand.getText().toString();
//        String deadline = mTvDeadline.getText().toString();
        String timeMinute = mTimeListInt.get(timePosition) + "";
//        if (deadline.contains(getString(R.string.fen))) {
//            timeMinute = Integer.parseInt(deadline.substring(0, 2)) + "";
//        } else {
//            timeMinute = Integer.parseInt(deadline.substring(0, 2)) * 60 + "";
//        }
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
        mEtSingleInsertCount.setText(data.getSingle_coin());
        mTvHint.setText(getString(R.string.start_guess_service_hint) + Double.parseDouble(data.getBet_fee()) * 100 + "%" + getString(R.string.sxf));
    }
}
