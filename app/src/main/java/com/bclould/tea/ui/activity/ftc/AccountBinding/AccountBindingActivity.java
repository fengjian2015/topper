package com.bclould.tea.ui.activity.ftc.AccountBinding;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


@RequiresApi(api = Build.VERSION_CODES.N)
public class AccountBindingActivity extends BaseActivity implements AccountBindingContacts.View{

    @Bind(R.id.et_email)
    EditText mEtEmail;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;

    private AccountBindingContacts.Presenter mAuthorizationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_binding);
        ButterKnife.bind(this);
        mAuthorizationPresenter=new AccountBindingPresenter();
        mAuthorizationPresenter.bindView(this);
        mAuthorizationPresenter.start(this);

    }


    @Override
    public void initView() {
        setTitle(getString(R.string.account_binding));
    }

    @OnClick({R.id.bark, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_next:
                mAuthorizationPresenter.bind(mEtEmail.getText().toString(),mEtPassword.getText().toString());
                break;
        }
    }

    @Override
    public void setDesc(String desc) {
        mTvDesc.setText(Html.fromHtml(desc));
    }
}
