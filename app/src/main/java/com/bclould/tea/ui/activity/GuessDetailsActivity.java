package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.BlockchainGuessPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BetInfo;
import com.bclould.tea.model.GuessInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.GuessBetRVAdapter;
import com.bclould.tea.ui.widget.CurrencyDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_GUESS_MSG;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GuessDetailsActivity extends BaseActivity {

    private static final int PLUS = 0;
    private static final int MINUS = 1;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.tv_day)
    TextView mTvDay;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_hr)
    TextView mTvHr;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.tv_minute)
    TextView mTvMinute;
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.tv_second)
    TextView mTvSecond;
    @Bind(R.id.tv_start_time)
    TextView mTvStartTime;
    @Bind(R.id.tv_present_periods)
    TextView mTvPresentPeriods;
    @Bind(R.id.tv_who)
    TextView mTvWho;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_single_insert_count)
    TextView mTvSingleInsertCount;
    @Bind(R.id.tv_present_invest_count)
    TextView mTvPresentInvestCount;
    @Bind(R.id.tv_present_coin_count)
    TextView mTvPresentCoinCount;
    @Bind(R.id.tv_sum_coin)
    TextView mTvSumCoin;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.et_array)
    EditText mEtArray;
    @Bind(R.id.et_array2)
    EditText mEtArray2;
    @Bind(R.id.et_array3)
    EditText mEtArray3;
    @Bind(R.id.et_array4)
    EditText mEtArray4;
    @Bind(R.id.btn_random)
    Button mBtnRandom;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.btn_bet)
    Button mBtnBet;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.ll_no)
    LinearLayout mLlNo;
    @Bind(R.id.tv_number)
    TextView mTvNumber;
    @Bind(R.id.tv_number2)
    TextView mTvNumber2;
    @Bind(R.id.tv_number3)
    TextView mTvNumber3;
    @Bind(R.id.tv_number4)
    TextView mTvNumber4;
    @Bind(R.id.tv_kaijiang_time)
    TextView mTvKaijiangTime;
    @Bind(R.id.tv_title2)
    TextView mTvTitle2;
    @Bind(R.id.tv_start_time2)
    TextView mTvStartTime2;
    @Bind(R.id.tv_present_periods2)
    TextView mTvPresentPeriods2;
    @Bind(R.id.tv_who2)
    TextView mTvWho2;
    @Bind(R.id.tv_coin2)
    TextView mTvCoin2;
    @Bind(R.id.tv_single_insert_count3)
    TextView mTvSingleInsertCount3;
    @Bind(R.id.tv_present_invest_count2)
    TextView mTvPresentInvestCount2;
    @Bind(R.id.tv_bonus_count)
    TextView mTvBonusCount;
    @Bind(R.id.recycler_view2)
    RecyclerView mRecyclerView2;
    @Bind(R.id.ll_already)
    LinearLayout mLlAlready;
    @Bind(R.id.et_bet_count)
    EditText mEtBetCount;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.ll_array)
    LinearLayout mLlArray;
    @Bind(R.id.et2_array)
    EditText mEt2Array;
    @Bind(R.id.et2_array2)
    EditText mEt2Array2;
    @Bind(R.id.et2_array3)
    EditText mEt2Array3;
    @Bind(R.id.et2_array4)
    EditText mEt2Array4;
    @Bind(R.id.btn_random2)
    Button mBtnRandom2;
    @Bind(R.id.ll_array2)
    LinearLayout mLlArray2;
    @Bind(R.id.et3_array)
    EditText mEt3Array;
    @Bind(R.id.et3_array2)
    EditText mEt3Array2;
    @Bind(R.id.et3_array3)
    EditText mEt3Array3;
    @Bind(R.id.et3_array4)
    EditText mEt3Array4;
    @Bind(R.id.btn_random3)
    Button mBtnRandom3;
    @Bind(R.id.ll_array3)
    LinearLayout mLlArray3;
    @Bind(R.id.et4_array)
    EditText mEt4Array;
    @Bind(R.id.et4_array2)
    EditText mEt4Array2;
    @Bind(R.id.et4_array3)
    EditText mEt4Array3;
    @Bind(R.id.et4_array4)
    EditText mEt4Array4;
    @Bind(R.id.btn_random4)
    Button mBtnRandom4;
    @Bind(R.id.ll_array4)
    LinearLayout mLlArray4;
    @Bind(R.id.et5_array)
    EditText mEt5Array;
    @Bind(R.id.et5_array2)
    EditText mEt5Array2;
    @Bind(R.id.et5_array3)
    EditText mEt5Array3;
    @Bind(R.id.et5_array4)
    EditText mEt5Array4;
    @Bind(R.id.btn_random5)
    Button mBtnRandom5;
    @Bind(R.id.ll_array5)
    LinearLayout mLlArray5;
    @Bind(R.id.scrollView2)
    ScrollView mScrollView2;
    @Bind(R.id.ll_guess_count)
    LinearLayout mLlGuessCount;
    @Bind(R.id.ll_bet)
    LinearLayout mLlBet;
    @Bind(R.id.btn_minus)
    Button mBtnMinus;
    @Bind(R.id.btn_plus)
    Button mBtnPlus;
    @Bind(R.id.cv_time)
    CardView mCvTime;
    @Bind(R.id.ll_hash)
    LinearLayout mLlHash;
    @Bind(R.id.iv_share)
    ImageView mIvShare;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private MNPasswordEditText mEtPassword;
    private ArrayList<Map<String, String>> valueList;
    private GridView mGridView;
    private int mBet_id;
    private int mPeriod_qty;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;
    private int
            mCountdown = 0;
    private double mPrize_pool_number;
    private GuessBetRVAdapter mGuessBetRVAdapter;
    Timer mTimer = new Timer();
    TimerTask mTask = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    mCountdown--;
                    String seconds = "00";
                    String minutes = "00";
                    String hrs = "00";
                    String days = "00";
                    int var = mCountdown;
                    if (mCountdown >= 86400) {
                        days = "0" + mCountdown / 86400;
                        var = mCountdown % 86400;
                    }
                    if (mCountdown >= 3600) {
                        int hr = var / 3600;
                        var = var % 3600;
                        if (hr < 10) {
                            hrs = "0" + hr;
                        } else {
                            hrs = hr + "";
                        }
                    }
                    if (mCountdown >= 60) {
                        int minute = var / 60;
                        var = var % 60;
                        if (minute < 10) {
                            minutes = "0" + minute;
                        } else {
                            minutes = minute + "";
                        }
                    }
                    if (var < 60) {
                        int second = mCountdown % 60;
                        if (second < 10) {
                            seconds = "0" + second;
                        } else {
                            seconds = second + "";
                        }
                    }
                    mTvDay.setText(days);
                    mTvHr.setText(hrs);
                    mTvMinute.setText(minutes);
                    mTvSecond.setText(seconds);
                    if (mCountdown <= 0) {
                        mTimer.cancel();
                        EventBus.getDefault().post(new MessageEvent(getString(R.string.guess_cancel)));
                        initData();
                    }
                }
            });
        }
    };
    private int mCoin_id;
    private String mSingle_coin = "";
    private int mCurrent_people_number;
    private int mOver_count_num;
    private int mStatus;
    private String mRandomArr = "";
    private String mRandomArr2 = "";
    private String mRandomArr3 = "";
    private String mRandomArr4 = "";
    private String mRandomArr5 = "";
    private String mRandomSumArr = "";
    private String[] mHashArr;
    private String[] mIndexArr;
    private double mLimit_number;
    private String[] mUrlArr;
    private int mLimit_people_number;
    private String mGuess_pw = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initIntent();
        initRecylerView();
        initData();
        initEidt();
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGuessBetRVAdapter = new GuessBetRVAdapter(this, mDataList);
        mRecyclerView.setAdapter(mGuessBetRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView2.setAdapter(mGuessBetRVAdapter);
        mRecyclerView2.setNestedScrollingEnabled(false);
    }

    List<GuessInfo.DataBean.BetListBean> mDataList = new ArrayList<>();

    private void initData() {
        mBlockchainGuessPresenter.getGuessInfo(mBet_id, mPeriod_qty, new BlockchainGuessPresenter.CallBack3() {
            @Override
            public void send(GuessInfo.DataBean data) {
                if (!GuessDetailsActivity.this.isDestroyed()) {
                    mLlError.setVisibility(View.GONE);
                    UtilTool.Log("投注列表", data.getBetList().size() + "");
                    mDataList.addAll(data.getBetList());
                    mGuessBetRVAdapter.notifyDataSetChanged();
                    mTvPresentPeriods.setText(data.getPeriod_qty() + getString(R.string.qi));
                    mTvPresentPeriods2.setText(data.getPeriod_qty() + getString(R.string.qi));
                    mTvWho.setText(data.getUser_name());
                    mTvWho2.setText(data.getUser_name());
                    mTvCoin.setText(data.getCoin_name());
                    mTvCoin2.setText(data.getCoin_name());
                    mTvSingleInsertCount.setText(data.getSingle_coin() + "/" + data.getCoin_name());
                    mTvSingleInsertCount3.setText(data.getSingle_coin() + "/" + data.getCoin_name());
                    mTvPresentInvestCount.setText(data.getCurrent_people_number() + "/" + data.getLimit_people_number());
                    mTvPresentInvestCount2.setText(data.getCurrent_people_number() + "");
                    mTvStartTime.setText(getString(R.string.fa_qi_time) + data.getCreated_at());
                    mTvStartTime2.setText(getString(R.string.fa_qi_time) + data.getCreated_at());
                    mTvSumCoin.setText(data.getLimit_number() + data.getCoin_name());
                    mTvBonusCount.setText(data.getLimit_number() + data.getCoin_name());
                    mTvPresentCoinCount.setText(data.getPrize_pool_number() + data.getCoin_name());
                    mTvTitle.setText(data.getTitle());
                    mTvTitle2.setText(data.getTitle());
                    mHashArr = data.getWin_number_hash().split(",");
                    mIndexArr = data.getWin_number_index().split(",");
                    mUrlArr = data.getWin_number_hash_url().split(",");
                    mLimit_people_number = data.getLimit_people_number();
                    mPrize_pool_number = Double.parseDouble(data.getPrize_pool_number());
                    mLimit_number = Double.parseDouble(data.getLimit_number());
                    int progress = (int) (Double.parseDouble(data.getPrize_pool_number()) / Double.parseDouble(data.getLimit_number()) * 100);
                    UtilTool.Log("進度", progress + "");
                    UtilTool.Log("進度", mPrize_pool_number + "");
                    mProgressBar.setMax(100);
                    mProgressBar.setProgress(progress);
                    mCoin_id = data.getCoin_id();
                    mSingle_coin = data.getSingle_coin();
                    mCurrent_people_number = data.getCurrent_people_number();
                    mOver_count_num = data.getOver_count_num();
                    mStatus = data.getStatus();
                    if (data.getStatus() == 1 || data.getStatus() == 2) {
                        mLlNo.setVisibility(View.VISIBLE);
                        mLlAlready.setVisibility(View.GONE);
                        mCountdown = data.getCountdown();
                        UtilTool.Log("倒計時", data.getCountdown() + "");
                        mTimer.schedule(mTask, 1000, 1000);
                        if (data.getStatus() == 2) {
                            mLlGuessCount.setVisibility(View.GONE);
                            mBtnBet.setBackground(getDrawable(R.drawable.bg_gray_shape));
                            mBtnRandom.setBackground(getDrawable(R.drawable.bg_grey_shape2));
                            mIvShare.setVisibility(View.GONE);
                        } else {
                            mIvShare.setVisibility(View.VISIBLE);
                            if (data.getOver_count_num() == 0) {
                                mLlGuessCount.setVisibility(View.GONE);
                                mBtnBet.setBackground(getDrawable(R.drawable.bg_gray_shape));
                                mBtnRandom.setBackground(getDrawable(R.drawable.bg_grey_shape2));
                            } else {
                                mLlGuessCount.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (data.getStatus() == 3) {
                        mIvShare.setVisibility(View.GONE);
                        mLlNo.setVisibility(View.GONE);
                        mLlAlready.setVisibility(View.VISIBLE);
                        String[] split = data.getWin_number().split("_");
                        mTvNumber.setText(split[0]);
                        mTvNumber2.setText(split[1]);
                        mTvNumber3.setText(split[2]);
                        mTvNumber4.setText(split[3]);
                    } else if (data.getStatus() == 4) {
                        mIvShare.setVisibility(View.GONE);
                        mLlNo.setVisibility(View.VISIBLE);
                        mLlAlready.setVisibility(View.GONE);
                        mLlGuessCount.setVisibility(View.GONE);
                        mLlBet.setVisibility(View.GONE);
                        mCvTime.setVisibility(View.GONE);
                        mBtnBet.setBackground(getDrawable(R.drawable.bg_gray_shape));
                        mBtnRandom.setBackground(getDrawable(R.drawable.bg_grey_shape2));
                    }
                }
            }

            @Override
            public void error() {
                mLlError.setVisibility(View.VISIBLE);
                mLlNo.setVisibility(View.GONE);
                mLlAlready.setVisibility(View.GONE);
            }
        });
    }

    private void initIntent() {
        mBlockchainGuessPresenter = new BlockchainGuessPresenter(this);
        Intent intent = getIntent();
        mBet_id = intent.getIntExtra("bet_id", 0);
        mPeriod_qty = intent.getIntExtra("period_qty", 0);
        mGuess_pw = intent.getStringExtra("guess_pw");
    }


    private void initEidt() {
        mEtArray.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mEtArray2.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mEtArray2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mEtArray3.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mEtArray3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mEtArray4.requestFocus();
                    return true;
                }
                return false;
            }
        });
/*
        mEtArray2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String array2 = mEtArray2.getText().toString();
                if (array2.isEmpty()) {
                    String array = mEtArray.getText().toString();
                    mEtArray.requestFocus();
                    if (!array.isEmpty()) {
                        mEtArray.setSelection(mEtArray2.getText().length());
                        int index = mEtArray.getSelectionStart();
                        Editable text = mEtArray.getText();
                        text.delete(index - 1, index);
                    } else {
                        mEtArray.setSelection(0);
                    }
                }
                return false;
            }
        });
        mEtArray3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String array3 = mEtArray3.getText().toString();
                if (array3.isEmpty()) {
                    String array2 = mEtArray2.getText().toString();
                    if (!array2.isEmpty()) {
                        mEtArray2.requestFocus();
                        mEtArray2.setSelection(mEtArray2.getText().length());
                        int index = mEtArray2.getSelectionStart();
                        Editable text = mEtArray2.getText();
                        text.delete(index - 1, index);
                    } else {
                        String array = mEtArray.getText().toString();
                        mEtArray.requestFocus();
                        if (!array.isEmpty()) {
                            mEtArray.setSelection(mEtArray2.getText().length());
                            int index = mEtArray.getSelectionStart();
                            Editable text = mEtArray.getText();
                            text.delete(index - 1, index);
                        } else {
                            mEtArray.setSelection(0);
                        }
                    }
                }
                return false;
            }
        });
        mEtArray4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String array4 = mEtArray4.getText().toString();
                if (array4.isEmpty()) {
                    String array3 = mEtArray3.getText().toString();
                    if (!array3.isEmpty()) {
                        mEtArray3.requestFocus();
                        mEtArray3.setSelection(mEtArray2.getText().length());
                        int index = mEtArray3.getSelectionStart();
                        Editable text = mEtArray3.getText();
                        text.delete(index - 1, index);
                    } else {
                        String array2 = mEtArray2.getText().toString();
                        if (!array2.isEmpty()) {
                            mEtArray2.requestFocus();
                            mEtArray2.setSelection(mEtArray2.getText().length());
                            int index = mEtArray2.getSelectionStart();
                            Editable text = mEtArray2.getText();
                            text.delete(index - 1, index);
                        } else {
                            String array = mEtArray.getText().toString();
                            mEtArray.requestFocus();
                            if (!array.isEmpty()) {
                                mEtArray.setSelection(mEtArray2.getText().length());
                                int index = mEtArray.getSelectionStart();
                                Editable text = mEtArray.getText();
                                text.delete(index - 1, index);
                            } else {
                                mEtArray.setSelection(0);
                            }
                        }
                    }
                }
                return false;
            }
        });
        mEtArray.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String array = mEtArray.getText().toString();
                if (charSequence.length() == 2) {
                    mEtArray2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtArray2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String array = mEtArray2.getText().toString();
                if (array.length() == 0) {
                    mEtArray.requestFocus();
                    mEtArray.setSelection(mEtArray.getText().length());
                }
                if (array.length() == 2) {
                    mEtArray3.requestFocus();
                }
            }
        });
        mEtArray3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String array = mEtArray3.getText().toString();
                if (array.length() == 0) {
                    mEtArray2.requestFocus();
                    mEtArray2.setSelection(mEtArray.getText().length());
                }
                if (array.length() == 2) {
                    mEtArray4.requestFocus();
                }
            }
        });
        mEtArray4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String array = mEtArray4.getText().toString();
                if (array.length() == 0) {
                    mEtArray3.requestFocus();
                    mEtArray3.setSelection(mEtArray.getText().length());
                }
                if (array.length() == 2) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
                    if (isOpen) {
                        imm.hideSoftInputFromWindow(mEtArray4.getWindowToken(), 0);
                    }
                }
            }
        });*/
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
        int count = 0;
        if (mLlArray5.getVisibility() == View.VISIBLE) {
            count = 5;
            mRandomSumArr = mRandomArr + "," + mRandomArr2 + "," + mRandomArr3 + "," + mRandomArr4 + "," + mRandomArr5;
        } else if (mLlArray4.getVisibility() == View.VISIBLE) {
            count = 4;
            mRandomSumArr = mRandomArr + "," + mRandomArr2 + "," + mRandomArr3 + "," + mRandomArr4;
        } else if (mLlArray3.getVisibility() == View.VISIBLE) {
            count = 3;
            mRandomSumArr = mRandomArr + "," + mRandomArr2 + "," + mRandomArr3;
        } else if (mLlArray2.getVisibility() == View.VISIBLE) {
            count = 2;
            mRandomSumArr = mRandomArr + "," + mRandomArr2;
        } else if (mLlArray.getVisibility() == View.VISIBLE) {
            count = 1;
            mRandomSumArr = mRandomArr;
        }
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
        countCoin.setText(UtilTool.doubleMultiply(count, Double.parseDouble(mSingle_coin)) + mTvCoin.getText().toString());
        coin.setText(getString(R.string.bet) + mTvCoin.getText().toString() + getString(R.string.guess));
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

        final int finalCount = count;
        mEtPassword.setOnPasswordChangeListener(new MNPasswordEditText.OnPasswordChangeListener() {
            @Override
            public void onPasswordChange(String password) {
                if (password.length() == 6) {
                    mRedDialog.dismiss();
                    mEtPassword.setText("");
                    bet(password, finalCount);
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

    @OnClick({R.id.ll_error, R.id.iv_share, R.id.ll_hash, R.id.btn_plus, R.id.btn_minus, R.id.bark, R.id.btn_random, R.id.btn_random2, R.id.btn_random3, R.id.btn_random4, R.id.btn_random5, R.id.btn_bet, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
            case R.id.iv_share:
                showShareDialog();
                break;
            case R.id.ll_hash:
                showHashDialog();
                break;
            case R.id.btn_plus:
                PlusMinus(PLUS);
                break;
            case R.id.btn_minus:
                PlusMinus(MINUS);
                break;
            case R.id.btn_confirm:
                if (!mEtBetCount.getText().toString().isEmpty()) {
                    if (Integer.parseInt(mEtBetCount.getText().toString()) <= 5) {
                        setBetCount();
                    } else {
                        Toast.makeText(this, getString(R.string.bet_hint2), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.toast_guess_count_null), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_random:
                if (mStatus == 1) {
                    if (mOver_count_num != 0) {
                        getRandom(1);
                    } else {
                        Toast.makeText(this, getString(R.string.bet_zuiduo_cishu), Toast.LENGTH_SHORT).show();
                    }
                } else if (mStatus == 2) {
                    Toast.makeText(this, getString(R.string.sum_jiangjin_chi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_random2:
                if (mStatus == 1) {
                    if (mOver_count_num != 0) {
                        getRandom(2);
                    } else {
                        Toast.makeText(this, getString(R.string.bet_zuiduo_cishu), Toast.LENGTH_SHORT).show();
                    }
                } else if (mStatus == 2) {
                    Toast.makeText(this, getString(R.string.sum_jiangjin_chi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_random3:
                if (mStatus == 1) {
                    if (mOver_count_num != 0) {
                        getRandom(3);
                    } else {
                        Toast.makeText(this, getString(R.string.bet_zuiduo_cishu), Toast.LENGTH_SHORT).show();
                    }
                } else if (mStatus == 2) {
                    Toast.makeText(this, getString(R.string.sum_jiangjin_chi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_random4:
                if (mStatus == 1) {
                    if (mOver_count_num != 0) {
                        getRandom(4);
                    } else {
                        Toast.makeText(this, getString(R.string.bet_zuiduo_cishu), Toast.LENGTH_SHORT).show();
                    }
                } else if (mStatus == 2) {
                    Toast.makeText(this, getString(R.string.sum_jiangjin_chi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_random5:
                if (mStatus == 1) {
                    if (mOver_count_num != 0) {
                        getRandom(5);
                    } else {
                        Toast.makeText(this, getString(R.string.bet_zuiduo_cishu), Toast.LENGTH_SHORT).show();
                    }
                } else if (mStatus == 2) {
                    Toast.makeText(this, getString(R.string.sum_jiangjin_chi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_bet:
                if (mStatus == 1) {
                    if (mOver_count_num != 0) {
                        if (checkEdit()) {
                            showPWDialog();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.bet_zuiduo_cishu), Toast.LENGTH_SHORT).show();
                    }
                } else if (mStatus == 2) {
                    Toast.makeText(this, getString(R.string.sum_jiangjin_chi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }

    private void showShareDialog() {
        List<String> list = Arrays.asList(new String[]{getString(R.string.share_friend), getString(R.string.share_dynamic)});
        final MenuListPopWindow menu = new MenuListPopWindow(this, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                Intent intent;
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        intent = new Intent(GuessDetailsActivity.this, SelectFriendActivity.class);
                        intent.putExtra("type", 2);
                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setTitle(mTvTitle.getText().toString());
                        messageInfo.setInitiator(mTvWho.getText().toString());
                        messageInfo.setCoin(mTvCoin.getText().toString());
                        messageInfo.setGuessPw(mGuess_pw);
                        messageInfo.setPeriodQty(mPeriod_qty + "");
                        messageInfo.setBetId(mBet_id + "");
                        messageInfo.setMessage(mTvTitle.getText().toString());
                        intent.putExtra("msgType", TO_GUESS_MSG);
                        intent.putExtra("messageInfo", messageInfo);
                        startActivity(intent);
                        break;
                    case 2:
                        menu.dismiss();
                        Intent intent2 = new Intent(GuessDetailsActivity.this, GuessShareDynamicActivity.class);
                        intent2.putExtra("title", mTvTitle.getText().toString());
                        intent2.putExtra("name", mTvWho.getText().toString());
                        intent2.putExtra("coin_name", mTvCoin.getText().toString());
                        intent2.putExtra("guess_id", mBet_id);
                        intent2.putExtra("period_aty", mPeriod_qty);
                        intent2.putExtra("guess_pw", mGuess_pw);
                        startActivity(intent2);
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void showHashDialog() {
        final CurrencyDialog dialog = new CurrencyDialog(R.layout.dialog_hash, this, R.style.dialog);
        dialog.show();
        final TextView hash = (TextView) dialog.findViewById(R.id.tv_hash);
        TextView number = (TextView) dialog.findViewById(R.id.tv_number);
        final TextView hash2 = (TextView) dialog.findViewById(R.id.tv_hash2);
        TextView number2 = (TextView) dialog.findViewById(R.id.tv_number2);
        final TextView hash3 = (TextView) dialog.findViewById(R.id.tv_hash3);
        TextView number3 = (TextView) dialog.findViewById(R.id.tv_number3);
        final TextView hash4 = (TextView) dialog.findViewById(R.id.tv_hash4);
        TextView number4 = (TextView) dialog.findViewById(R.id.tv_number4);
        RelativeLayout rlHash = (RelativeLayout) dialog.findViewById(R.id.rl_hash);
        RelativeLayout rlHash2 = (RelativeLayout) dialog.findViewById(R.id.rl_hash2);
        RelativeLayout rlHash3 = (RelativeLayout) dialog.findViewById(R.id.rl_hash3);
        RelativeLayout rlHash4 = (RelativeLayout) dialog.findViewById(R.id.rl_hash4);
        TextView copy = (TextView) dialog.findViewById(R.id.tv_copy);
        TextView copy2 = (TextView) dialog.findViewById(R.id.tv_copy2);
        TextView copy3 = (TextView) dialog.findViewById(R.id.tv_copy3);
        TextView copy4 = (TextView) dialog.findViewById(R.id.tv_copy4);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(hash.getText());
                Toast.makeText(GuessDetailsActivity.this, getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
            }
        });
        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(hash2.getText());
                Toast.makeText(GuessDetailsActivity.this, getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
            }
        });
        copy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(hash3.getText());
                Toast.makeText(GuessDetailsActivity.this, getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
            }
        });
        copy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(hash4.getText());
                Toast.makeText(GuessDetailsActivity.this, getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
            }
        });
        rlHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(GuessDetailsActivity.this, WebViewActivity.class);
                intent.putExtra("url", mUrlArr[0]);
                intent.putExtra("title", getString(R.string.hash_details));
                startActivity(intent);
            }
        });
        rlHash2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(GuessDetailsActivity.this, WebViewActivity.class);
                intent.putExtra("url", mUrlArr[1]);
                intent.putExtra("title", getString(R.string.hash_details));
                startActivity(intent);
            }
        });
        rlHash3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(GuessDetailsActivity.this, WebViewActivity.class);
                intent.putExtra("url", mUrlArr[2]);
                intent.putExtra("title", getString(R.string.hash_details));
                startActivity(intent);

            }
        });
        rlHash4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(GuessDetailsActivity.this, WebViewActivity.class);
                intent.putExtra("url", mUrlArr[3]);
                intent.putExtra("title", getString(R.string.hash_details));
                startActivity(intent);

            }
        });
        number.setText(mTvNumber.getText().toString() + " = " + mTvCoin.getText().toString() + getString(R.string.qukuai) + mIndexArr[0] + getString(R.string.hash_value));
        number2.setText(mTvNumber2.getText().toString() + " = " + mTvCoin.getText().toString() + getString(R.string.qukuai) + mIndexArr[1] + getString(R.string.hash_value));
        number3.setText(mTvNumber3.getText().toString() + " = " + mTvCoin.getText().toString() + getString(R.string.qukuai) + mIndexArr[2] + getString(R.string.hash_value));
        number4.setText(mTvNumber4.getText().toString() + " = " + mTvCoin.getText().toString() + getString(R.string.qukuai) + mIndexArr[3] + getString(R.string.hash_value));
        hash.setText(mHashArr[0]);
        hash2.setText(mHashArr[1]);
        hash3.setText(mHashArr[2]);
        hash4.setText(mHashArr[3]);
    }

    private void PlusMinus(int type) {
        String count = mEtBetCount.getText().toString();
        if (count.isEmpty()) {
            if (type == MINUS) {
                Toast.makeText(this, getString(R.string.toast_count_ling), Toast.LENGTH_SHORT).show();
            } else {
                mEtBetCount.setText("1");
                mEtBetCount.setSelection(mEtBetCount.getText().length());
            }
        } else {
            int counti = Integer.parseInt(count);
            if (type == MINUS) {
                if (counti == 1) {
                    Toast.makeText(this, getString(R.string.toast_count_ling), Toast.LENGTH_SHORT).show();
                } else {
                    counti--;
                    mEtBetCount.setText(counti + "");
                    mEtBetCount.setSelection(mEtBetCount.getText().length());
                }
            } else {
                if (counti == mOver_count_num) {
                    Toast.makeText(this, getString(R.string.toast_count_max), Toast.LENGTH_SHORT).show();
                } else {
                    counti++;
                    mEtBetCount.setText(counti + "");
                    mEtBetCount.setSelection(mEtBetCount.getText().length());
                }
            }
        }
    }

    private void setBetCount() {
        String etBetCount = mEtBetCount.getText().toString();
        if (!etBetCount.isEmpty()) {
            int count = Integer.parseInt(etBetCount);
            if (count > 0) {
                switch (count) {
                    case 1:
                        mLlArray.setVisibility(View.VISIBLE);
                        mLlArray2.setVisibility(View.GONE);
                        mLlArray3.setVisibility(View.GONE);
                        mLlArray4.setVisibility(View.GONE);
                        mLlArray5.setVisibility(View.GONE);
                        break;
                    case 2:
                        mLlArray.setVisibility(View.VISIBLE);
                        mLlArray2.setVisibility(View.VISIBLE);
                        mLlArray3.setVisibility(View.GONE);
                        mLlArray4.setVisibility(View.GONE);
                        mLlArray5.setVisibility(View.GONE);
                        break;
                    case 3:
                        mLlArray.setVisibility(View.VISIBLE);
                        mLlArray2.setVisibility(View.VISIBLE);
                        mLlArray3.setVisibility(View.VISIBLE);
                        mLlArray4.setVisibility(View.GONE);
                        mLlArray5.setVisibility(View.GONE);
                        break;
                    case 4:
                        mLlArray.setVisibility(View.VISIBLE);
                        mLlArray2.setVisibility(View.VISIBLE);
                        mLlArray3.setVisibility(View.VISIBLE);
                        mLlArray4.setVisibility(View.VISIBLE);
                        mLlArray5.setVisibility(View.GONE);
                        break;
                    case 5:
                        mLlArray.setVisibility(View.VISIBLE);
                        mLlArray2.setVisibility(View.VISIBLE);
                        mLlArray3.setVisibility(View.VISIBLE);
                        mLlArray4.setVisibility(View.VISIBLE);
                        mLlArray5.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_count_ling), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_guess_count_null), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkEdit() {
        if (mLlArray.getVisibility() == View.VISIBLE) {
            if (mEtArray.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEtArray);
            } else if (mEtArray2.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEtArray2);
            } else if (mEtArray3.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEtArray3);
            } else if (mEtArray4.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEtArray4);
            } else {
                return true;
            }
        }
        if (mLlArray2.getVisibility() == View.VISIBLE) {
            if (mEt2Array.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt2Array);
            } else if (mEt2Array2.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEtArray2);
            } else if (mEt2Array3.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt2Array3);
            } else if (mEt2Array4.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt2Array4);
            } else {
                return true;
            }
        }
        if (mLlArray3.getVisibility() == View.VISIBLE) {
            if (mEt3Array.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt3Array);
            } else if (mEt3Array2.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt3Array2);
            } else if (mEt3Array3.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt3Array3);
            } else if (mEt3Array4.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt3Array4);
            } else {
                return true;
            }
        }
        if (mLlArray4.getVisibility() == View.VISIBLE) {
            if (mEt4Array.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt4Array);
            } else if (mEt4Array2.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt4Array2);
            } else if (mEt4Array3.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt4Array3);
            } else if (mEt4Array4.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt4Array4);
            } else {
                return true;
            }
        }
        if (mLlArray5.getVisibility() == View.VISIBLE) {
            if (mEt5Array.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt5Array);
            } else if (mEt5Array2.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt5Array2);
            } else if (mEt5Array3.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt5Array3);
            } else if (mEt5Array4.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_array), Toast.LENGTH_SHORT).show();
                AnimatorTool.getInstance().editTextAnimator(mEt5Array4);
            } else {
                return true;
            }
        }
        return false;
    }

    private void getRandom(int status) {
        String[] randomArr = UtilTool.getRandomArr(Constants.BET_ARR_COUNT);
        switch (status) {
            case 1:
                for (int i = 0; i < randomArr.length; i++) {
                    if (i != 0) {
                        mRandomArr = mRandomArr + ":" + randomArr[i];
                    } else {
                        mRandomArr = randomArr[i];
                    }
                }
                mEtArray.setText(randomArr[0]);
                mEtArray2.setText(randomArr[1]);
                mEtArray3.setText(randomArr[2]);
                mEtArray4.setText(randomArr[3]);
                mEtArray4.setSelection(2);
                break;
            case 2:
                for (int i = 0; i < randomArr.length; i++) {
                    if (i != 0) {
                        mRandomArr2 = mRandomArr2 + ":" + randomArr[i];
                    } else {
                        mRandomArr2 = randomArr[i];
                    }
                }
                mEt2Array.setText(randomArr[0]);
                mEt2Array2.setText(randomArr[1]);
                mEt2Array3.setText(randomArr[2]);
                mEt2Array4.setText(randomArr[3]);
                mEt2Array4.setSelection(2);
                break;
            case 3:
                for (int i = 0; i < randomArr.length; i++) {
                    if (i != 0) {
                        mRandomArr3 = mRandomArr3 + ":" + randomArr[i];
                    } else {
                        mRandomArr3 = randomArr[i];
                    }
                }
                mEt3Array.setText(randomArr[0]);
                mEt3Array2.setText(randomArr[1]);
                mEt3Array3.setText(randomArr[2]);
                mEt3Array4.setText(randomArr[3]);
                mEt3Array4.setSelection(2);
                break;
            case 4:
                for (int i = 0; i < randomArr.length; i++) {
                    if (i != 0) {
                        mRandomArr4 = mRandomArr4 + ":" + randomArr[i];
                    } else {
                        mRandomArr4 = randomArr[i];
                    }
                }
                mEt4Array.setText(randomArr[0]);
                mEt4Array2.setText(randomArr[1]);
                mEt4Array3.setText(randomArr[2]);
                mEt4Array4.setText(randomArr[3]);
                mEt4Array4.setSelection(2);
                break;
            case 5:
                for (int i = 0; i < randomArr.length; i++) {
                    if (i != 0) {
                        mRandomArr5 = mRandomArr5 + ":" + randomArr[i];
                    } else {
                        mRandomArr5 = randomArr[i];
                    }
                }
                mEt5Array.setText(randomArr[0]);
                mEt5Array2.setText(randomArr[1]);
                mEt5Array3.setText(randomArr[2]);
                mEt5Array4.setText(randomArr[3]);
                mEt5Array4.setSelection(2);
                break;
        }

        /*mBlockchainGuessPresenter.getRandom(new BlockchainGuessPresenter.CallBack4() {
            @Override
            public void send(String data) {
                String[] split = data.split(":");
                mRandom = data;
            }
        });*/
    }

    private void bet(String password, final int count) {

        UtilTool.Log("數組", mRandomSumArr);
        mBlockchainGuessPresenter.bet(mBet_id, mPeriod_qty, mCoin_id, mRandomSumArr, password, new BlockchainGuessPresenter.CallBack5() {
            @Override
            public void send(BetInfo.DataBean data) {
                mOver_count_num -= count;
                if (mOver_count_num == 0) {
                    mLlGuessCount.setVisibility(View.GONE);
                    mBtnBet.setBackground(getDrawable(R.drawable.bg_gray_shape));
                    mBtnRandom.setBackground(getDrawable(R.drawable.bg_grey_shape2));
                }
                EventBus.getDefault().post(new MessageEvent(getString(R.string.bet)));
                switch (count) {
                    case 1:
                        mEtArray.requestFocus();
                        mEtArray.setText("");
                        mEtArray2.setText("");
                        mEtArray3.setText("");
                        mEtArray4.setText("");
                        break;
                    case 2:
                        mEtArray.requestFocus();
                        mEtArray.setText("");
                        mEtArray2.setText("");
                        mEtArray3.setText("");
                        mEtArray4.setText("");
                        mEt2Array.setText("");
                        mEt2Array2.setText("");
                        mEt2Array3.setText("");
                        mEt2Array4.setText("");
                        break;
                    case 3:
                        mEtArray.requestFocus();
                        mEtArray.setText("");
                        mEtArray2.setText("");
                        mEtArray3.setText("");
                        mEtArray4.setText("");
                        mEt2Array.setText("");
                        mEt2Array2.setText("");
                        mEt2Array3.setText("");
                        mEt2Array4.setText("");
                        mEt3Array.setText("");
                        mEt3Array2.setText("");
                        mEt3Array3.setText("");
                        mEt3Array4.setText("");
                        break;
                    case 4:
                        mEtArray.requestFocus();
                        mEtArray.setText("");
                        mEtArray2.setText("");
                        mEtArray3.setText("");
                        mEtArray4.setText("");
                        mEt2Array.setText("");
                        mEt2Array2.setText("");
                        mEt2Array3.setText("");
                        mEt2Array4.setText("");
                        mEt3Array.setText("");
                        mEt3Array2.setText("");
                        mEt3Array3.setText("");
                        mEt3Array4.setText("");
                        mEt4Array.setText("");
                        mEt4Array2.setText("");
                        mEt4Array3.setText("");
                        mEt4Array4.setText("");
                        break;
                    case 5:
                        mEtArray.requestFocus();
                        mEtArray.setText("");
                        mEtArray2.setText("");
                        mEtArray3.setText("");
                        mEtArray4.setText("");
                        mEt2Array.setText("");
                        mEt2Array2.setText("");
                        mEt2Array3.setText("");
                        mEt2Array4.setText("");
                        mEt3Array.setText("");
                        mEt3Array2.setText("");
                        mEt3Array3.setText("");
                        mEt3Array4.setText("");
                        mEt4Array.setText("");
                        mEt4Array2.setText("");
                        mEt4Array3.setText("");
                        mEt4Array4.setText("");
                        mEt5Array.setText("");
                        mEt5Array2.setText("");
                        mEt5Array3.setText("");
                        mEt5Array4.setText("");
                        break;
                }
                mLlArray2.setVisibility(View.GONE);
                mLlArray3.setVisibility(View.GONE);
                mLlArray4.setVisibility(View.GONE);
                mLlArray5.setVisibility(View.GONE);

               /* mPrize_pool_number = mPrize_pool_number + Double.parseDouble(mSingle_coin) * count;
                mCurrent_people_number = mCurrent_people_number + count;
                int progress = (int) (mPrize_pool_number / mLimit_number * 100);
                mTvPresentInvestCount.setText(mCurrent_people_number + "");
                mTvPresentCoinCount.setText(mPrize_pool_number + mTvCoin.getText().toString());
                mProgressBar.setProgress(progress);*/

                mTvPresentCoinCount.setText(data.getPrize_pool_number() + mTvCoin.getText().toString());
                mTvPresentInvestCount.setText(data.getCoin_number() + "/" + mLimit_people_number);
                int progress = (int) (Double.parseDouble(data.getPrize_pool_number()) / mLimit_number * 100);
                mProgressBar.setProgress(progress);
                for (BetInfo.DataBean.ListBean info : data.getList()) {
                    GuessInfo.DataBean.BetListBean betListBean = new GuessInfo.DataBean.BetListBean();
                    betListBean.setBet_number(info.getBet_number());
                    betListBean.setBonus_number(info.getBonus_number());
                    betListBean.setCreated_at(info.getCreated_at());
                    betListBean.setStatus(info.getStatus());
                    betListBean.setWinning_type(info.getWinning_type());
                    mDataList.add(betListBean);
                }
                mGuessBetRVAdapter.notifyDataSetChanged();
            }
        });
    }
}
