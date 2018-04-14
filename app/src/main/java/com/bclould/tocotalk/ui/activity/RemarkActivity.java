package com.bclould.tocotalk.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/28.
 */

public class RemarkActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.save)
    TextView mSave;
    @Bind(R.id.et_remark)
    EditText mEtRemark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }



    @OnClick({R.id.bark, R.id.save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.save:

                preserve();

                break;
        }
    }

    private void preserve() {

        String remarkName = mEtRemark.getText().toString().trim();

        if(remarkName.isEmpty()){

            Toast.makeText(this, getString(R.string.toast_remark), Toast.LENGTH_SHORT).show();

        }

        finish();

    }
}
