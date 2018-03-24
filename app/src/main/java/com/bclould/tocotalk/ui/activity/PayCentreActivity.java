package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayCentreActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.rl_pay_record)
    RelativeLayout mRlPayRecord;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.rl_pay_manage)
    RelativeLayout mRlPayManage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_centre);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bark, R.id.rl_pay_record, R.id.rl_pay_manage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_pay_record:
                startActivity(new Intent(this, PayRecordActivity.class));
                break;
            case R.id.rl_pay_manage:
                startActivity(new Intent(this, PayManageActivity.class));
                break;
        }
    }
}
