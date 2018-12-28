package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/16.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ExpectCoinActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.et_content)
    EditText mEtContent;
    @Bind(R.id.et_contact)
    EditText mEtContact;
    @Bind(R.id.tv_email)
    TextView mTvEmail;
    private CoinPresenter mCoinPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expect_coin);
        ButterKnife.bind(this);
        setTitle(getString(R.string.expect_coin));
        mTvEmail.setText(getString(R.string.email) + " : " + getString(R.string.official_email));
        mCoinPresenter = new CoinPresenter(this);
    }

    @OnClick({R.id.bark, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_confirm:
                if (checkEdit()) {
                    sumbit();
                }
                break;
        }
    }

    private void sumbit() {
        String content = mEtContent.getText().toString();
        String contact = mEtContact.getText().toString();
        mCoinPresenter.hopeCoin(content, contact);
    }

    private boolean checkEdit() {
        if (mEtContact.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtContact);
            Toast.makeText(this, getString(R.string.toast_contact_information), Toast.LENGTH_SHORT).show();
        } else if (mEtContent.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtContent);
            Toast.makeText(this, getString(R.string.toast_expect_coin), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }
}
