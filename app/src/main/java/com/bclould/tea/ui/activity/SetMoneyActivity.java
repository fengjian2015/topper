package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/21.
 */

public class SetMoneyActivity extends BaseActivity {
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_money);
        ButterKnife.bind(this);
        setTitle(getString(R.string.set_money));
    }


    @OnClick({R.id.bark, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                break;
            case R.id.btn_finish:
                break;
        }
    }
}
