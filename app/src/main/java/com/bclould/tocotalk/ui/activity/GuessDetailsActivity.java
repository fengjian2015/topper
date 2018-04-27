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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.BlockchainGuessPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.BetInfo;
import com.bclould.tocotalk.model.GuessInfo;
import com.bclould.tocotalk.ui.adapter.GuessBetRVAdapter;
import com.bclould.tocotalk.ui.widget.VirtualKeyboardView;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GuessDetailsActivity extends BaseActivity {

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
    @Bind(R.id.tv_single_insert_count2)
    TextView mTvSingleInsertCount2;
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
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private MNPasswordEditText mEtPassword;
    private ArrayList<Map<String, String>> valueList;
    private GridView mGridView;
    private int mBet_id;
    private int mPeriod_qty;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;
    private int mCountdown = 0;
    private String mPrize_pool_number;
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
                        Toast.makeText(GuessDetailsActivity.this, getString(R.string.guess_timeout), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };
    private int mCoin_id;
    private String mRandom;
    private String mSingle_coin;
    private int mCurrent_people_number;
    private int mOver_count_num;
    private int mStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_details);
        ButterKnife.bind(this);
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
    }

    List<GuessInfo.DataBean.BetListBean> mDataList = new ArrayList<>();

    private void initData() {
        mBlockchainGuessPresenter.getGuessInfo(mBet_id, mPeriod_qty, new BlockchainGuessPresenter.CallBack3() {
            @Override
            public void send(GuessInfo.DataBean data) {
                UtilTool.Log("次數", data.getOver_count_num() + "");
                mDataList.addAll(data.getBetList());
                mGuessBetRVAdapter.notifyDataSetChanged();
                mTvPresentPeriods.setText(data.getPeriod_qty() + getString(R.string.qi));
                mTvPresentPeriods2.setText(data.getPeriod_qty() + getString(R.string.qi));
                mTvWho.setText(data.getUser_name());
                mTvWho2.setText(data.getUser_name());
                mTvCoin.setText(data.getCoin_name());
                mTvCoin2.setText(data.getCoin_name());
                mTvSingleInsertCount.setText(data.getSingle_coin() + "/" + data.getCoin_name());
                mTvSingleInsertCount2.setText(data.getSingle_coin() + "/" + data.getCoin_name());
                mTvSingleInsertCount3.setText(data.getSingle_coin() + "/" + data.getCoin_name());
                mTvPresentInvestCount.setText(data.getCurrent_people_number() + "");
                mTvPresentInvestCount2.setText(data.getCurrent_people_number() + "");
                mTvStartTime.setText(getString(R.string.fa_qi_time) + data.getCreated_at());
                mTvStartTime2.setText(data.getCreated_at());
                mTvSumCoin.setText(data.getLimit_number() + data.getCoin_name());
                mTvBonusCount.setText(data.getLimit_number() + data.getCoin_name());
                mTvPresentCoinCount.setText(data.getPrize_pool_number() + data.getCoin_name());
                mTvTitle.setText(data.getTitle());
                mTvTitle2.setText(data.getTitle());
                mProgressBar.setMax(Integer.parseInt(data.getLimit_number()));
                mProgressBar.setProgress(Integer.parseInt(data.getPrize_pool_number()));
                mPrize_pool_number = data.getPrize_pool_number();
                mCoin_id = data.getCoin_id();
                mSingle_coin = data.getSingle_coin();
                mCurrent_people_number = data.getCurrent_people_number();
                mOver_count_num = data.getOver_count_num();
                mStatus = data.getStatus();
                if (data.getOver_count_num() == 0) {
                    mBtnBet.setBackground(getDrawable(R.drawable.bg_gray_shape));
                    mBtnRandom.setBackground(getDrawable(R.drawable.bg_grey_shape2));
                }
                if (data.getStatus() == 1 || data.getStatus() == 2) {
                    mLlNo.setVisibility(View.VISIBLE);
                    mLlAlready.setVisibility(View.GONE);
                    if (data.getStatus() == 2) {
                        mBtnBet.setBackground(getDrawable(R.drawable.bg_grey_shape));
                        mBtnRandom.setBackground(getDrawable(R.drawable.bg_grey_shape));
                    } else {
                        mCountdown = data.getCountdown();
                        UtilTool.Log("倒計時", data.getCountdown() + "");
                        mTimer.schedule(mTask, 1000, 1000);
                    }
                } else if (data.getStatus() == 3) {
                    mLlNo.setVisibility(View.GONE);
                    mLlAlready.setVisibility(View.VISIBLE);
                    String[] split = data.getWin_number().split("_");
                    mTvNumber.setText(split[0]);
                    mTvNumber2.setText(split[1]);
                    mTvNumber3.setText(split[2]);
                    mTvNumber4.setText(split[3]);
                }

            }
        });
    }

    private void initIntent() {
        mBlockchainGuessPresenter = new BlockchainGuessPresenter(this);
        Intent intent = getIntent();
        mBet_id = intent.getIntExtra("bet_id", 0);
        mPeriod_qty = intent.getIntExtra("period_qty", 0);
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
        countCoin.setText(mSingle_coin + mTvCoin.getText().toString());
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

        mEtPassword.setOnPasswordChangeListener(new MNPasswordEditText.OnPasswordChangeListener() {
            @Override
            public void onPasswordChange(String password) {
                if (password.length() == 6) {
                    mRedDialog.dismiss();
                    mEtPassword.setText("");
                    bet(password);
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

    @OnClick({R.id.bark, R.id.btn_random, R.id.btn_bet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_random:
                if (mStatus == 1) {
                    if (mOver_count_num != 0) {
                        getRandom();
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
        }
    }

    private boolean checkEdit() {
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
        return false;
    }

    private void getRandom() {
        mBlockchainGuessPresenter.getRandom(new BlockchainGuessPresenter.CallBack4() {
            @Override
            public void send(String data) {
                mOver_count_num--;
                if (mOver_count_num == 0) {
                    mBtnBet.setBackground(getDrawable(R.drawable.bg_gray_shape));
                    mBtnRandom.setBackground(getDrawable(R.drawable.bg_grey_shape2));
                }
                String[] split = data.split(":");
                mEtArray.setText(split[0]);
                mEtArray2.setText(split[1]);
                mEtArray3.setText(split[2]);
                mEtArray4.setText(split[3]);
                mEtArray4.setSelection(2);
                mRandom = data;
            }
        });
    }

    private void bet(String password) {
        mBlockchainGuessPresenter.bet(mBet_id, mPeriod_qty, mCoin_id, mRandom, password, new BlockchainGuessPresenter.CallBack5() {
            @Override
            public void send(BetInfo.DataBean data) {
                mEtArray.requestFocus();
                mEtArray.setText("");
                mEtArray2.setText("");
                mEtArray3.setText("");
                mEtArray4.setText("");
                mPrize_pool_number = Integer.parseInt(mPrize_pool_number) + Integer.parseInt(mSingle_coin) + "";
                mCurrent_people_number = mCurrent_people_number + 1;
                mTvPresentInvestCount.setText(mCurrent_people_number + "");
                mTvPresentCoinCount.setText(mPrize_pool_number + mTvCoin.getText().toString());
                mProgressBar.setProgress(Integer.parseInt(mPrize_pool_number));
                GuessInfo.DataBean.BetListBean betListBean = new GuessInfo.DataBean.BetListBean();
                betListBean.setBet_number(data.getBet_number());
                betListBean.setBonus_number(data.getBonus_number());
                betListBean.setCreated_at(data.getCreated_at());
                betListBean.setStatus(data.getStatus());
                betListBean.setWinning_type(data.getWinning_type());
                mDataList.add(betListBean);
                mGuessBetRVAdapter.notifyDataSetChanged();
            }
        });
    }
}
