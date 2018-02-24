package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

public class RealNameActivity extends BaseActivity {

    public static final String REALNAME = "real_name";
    public static final String IDCARDNUMBER = "id_card_number";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.help)
    ImageView mHelp;
    @Bind(R.id.real_name)
    EditText mRealName;
    @Bind(R.id.id_card_number)
    EditText mIdCardNumber;
    @Bind(R.id.btn_next)
    Button mBtnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.help, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.help:

//                startActivity(new Intent(this, UpIdCardActivity.class));

                break;
            case R.id.btn_next:

                String realName = mRealName.getText().toString().trim();

                String idCardNumber = mIdCardNumber.getText().toString().trim();

                if (realName.isEmpty() && idCardNumber.isEmpty()) {

                    Toast.makeText(this, "姓名或者身份证号不能为空", Toast.LENGTH_SHORT).show();

                } else {

                    MySharedPreferences.getInstance().setString(REALNAME, realName);

                    MySharedPreferences.getInstance().setString(IDCARDNUMBER, idCardNumber);

                    startActivity(new Intent(this, UpIdCardActivity.class));

                    finish();

                }

                break;
        }
    }
}
