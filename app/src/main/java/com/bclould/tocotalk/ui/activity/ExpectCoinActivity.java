package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.utils.AnimatorTool;

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
    private CoinPresenter mCoinPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expect_coin);
        ButterKnife.bind(this);
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
            Toast.makeText(this, "联系方式不能为空", Toast.LENGTH_SHORT).show();
        } else if (mEtContent.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mEtContent);
            Toast.makeText(this, "期望币种不能为空", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }
}
