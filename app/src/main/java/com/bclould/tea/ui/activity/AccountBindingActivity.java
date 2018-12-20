package com.bclould.tea.ui.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.ui.widget.NodePayDialog;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.BIND_FTC;


@RequiresApi(api = Build.VERSION_CODES.N)
public class AccountBindingActivity extends BaseActivity {

    @Bind(R.id.et_email)
    EditText mEtEmail;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_account_binding);
        ButterKnife.bind(this);
        setTitle(getString(R.string.account_binding));
        desc();
    }

    @OnClick({R.id.bark, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_next:
                bind();
                break;
        }
    }

    private void desc() {
        new DistributionPresenter(this).bindDesc(new DistributionPresenter.CallBack() {
            @Override
            public void send(BaseInfo baseInfo) {
                mTvDesc.setText(Html.fromHtml(baseInfo.getData().getDesc()));
            }

            @Override
            public void error() {

            }
        });
    }

    private void bind() {
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();
        if (StringUtils.isEmpty(email)) {
            ToastShow.showToast(this, getString(R.string.toast_email));
        }
        if (StringUtils.isEmpty(password)) {
            ToastShow.showToast(this, getString(R.string.toast_password));
        }

        new DistributionPresenter(this).bindTeam(email, password, new DistributionPresenter.CallBack() {
            @Override
            public void send(BaseInfo baseInfo) {
                MySharedPreferences.getInstance().setBoolean(BIND_FTC, true);
                showDialog();
            }

            @Override
            public void error() {

            }
        });
    }

    private void showDialog() {
        NodePayDialog nodePayDialog = new NodePayDialog(this);
        nodePayDialog.show();
        nodePayDialog.setTvTitle(getString(R.string.binding_succeed));
        nodePayDialog.setIvImage(R.mipmap.icon_node_dui);
        nodePayDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                AccountBindingActivity.this.finish();
            }
        });

    }
}
