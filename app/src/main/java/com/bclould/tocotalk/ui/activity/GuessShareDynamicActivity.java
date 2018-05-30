package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/30.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GuessShareDynamicActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_share)
    TextView mTvShare;
    @Bind(R.id.text_et)
    EditText mTextEt;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_who)
    TextView mTvWho;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    private String mGuess_pw;
    private String mCoin_name;
    private String mName;
    private String mTitle;
    private DynamicPresenter mDynamicPresenter;
    private int mPeriod_aty;
    private int mGuess_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_share_dynamic);
        ButterKnife.bind(this);
        mDynamicPresenter = new DynamicPresenter(this);
        initIntent();
    }

    private void initIntent() {
        mTitle = getIntent().getStringExtra("title");
        mName = getIntent().getStringExtra("name");
        mCoin_name = getIntent().getStringExtra("coin_name");
        mGuess_pw = getIntent().getStringExtra("guess_pw");
        mPeriod_aty = getIntent().getIntExtra("period_aty", 0);
        mGuess_id = getIntent().getIntExtra("guess_id", 0);
        mTvTitle.setText(mTitle);
        mTvWho.setText(getString(R.string.fa_qi_ren) + ":" + mName);
        mTvCoin.setText(mCoin_name + getString(R.string.guess));
    }

    @OnClick({R.id.bark, R.id.tv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_share:
                if (mTextEt.getText().toString().isEmpty()) {
                    ToastShow.showToast2(this, getString(R.string.et_content));
                } else {
                    publicshDynamic();
                }
                break;
        }
    }

    private void publicshDynamic() {
        String text = mTextEt.getText().toString();
        String content;
        if (mGuess_pw != null) {
            content = text + Constants.GUESS_DYNAMIC_SEPARATOR + mTitle + Constants.GUESS_DYNAMIC_SEPARATOR + mName + Constants.GUESS_DYNAMIC_SEPARATOR + mCoin_name + Constants.GUESS_DYNAMIC_SEPARATOR + mGuess_id + Constants.GUESS_DYNAMIC_SEPARATOR + mPeriod_aty + Constants.GUESS_DYNAMIC_SEPARATOR + mGuess_pw;
        } else {
            content = text + Constants.GUESS_DYNAMIC_SEPARATOR + mTitle + Constants.GUESS_DYNAMIC_SEPARATOR + mName + Constants.GUESS_DYNAMIC_SEPARATOR + mCoin_name + Constants.GUESS_DYNAMIC_SEPARATOR + mGuess_id + Constants.GUESS_DYNAMIC_SEPARATOR + mPeriod_aty;
        }
        UtilTool.Log("競猜分享", content);
        mDynamicPresenter.publicsh(content, 4 + "", "", "", "", new DynamicPresenter.CallBack() {
            @Override
            public void send() {
                finish();
                EventBus.getDefault().post(new MessageEvent(getString(R.string.publish_dynamic)));
            }
        });
    }
}
