package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RealNameC1Activity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.rl_card_type)
    RelativeLayout mRlCardType;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.et_number)
    EditText mEtNumber;
    @Bind(R.id.btn_next)
    Button mBtnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_namec1);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.bark, R.id.rl_card_type, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_card_type:
                break;
            case R.id.btn_next:
                if (checkEdit()) {
                    submit();
                }
                break;
        }
    }

    private void submit() {

    }

    private boolean checkEdit() {
        if (mEtName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtName);
        } else if (mEtNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "证件号码不能为空", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtNumber);
        } else {
            return true;
        }

        return false;
    }
}
