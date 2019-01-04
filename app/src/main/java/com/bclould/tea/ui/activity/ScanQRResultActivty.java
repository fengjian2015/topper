package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanQRResultActivty extends BaseActivity {
    @Bind(R.id.tv_content)
    TextView mTvContent;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrresult_activty);
        ButterKnife.bind(this);
        setTitle(getString(R.string.qr_scan_result));
        initIntent();
    }

    private void initIntent() {
        result = getIntent().getStringExtra("result");
        mTvContent.setText(result);
    }


    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;

        }
    }
}
