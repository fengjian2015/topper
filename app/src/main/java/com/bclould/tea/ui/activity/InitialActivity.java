package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/6/6.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class InitialActivity extends AppCompatActivity {
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    @Bind(R.id.btn_register)
    Button mBtnRegister;
    @Bind(R.id.tv_version)
    TextView mTvVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        String versionCode = UtilTool.getVersionCode(this);
        mTvVersion.setText("V" + versionCode);
        WsConnection.getInstance().logoutService(this);

    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }
}
