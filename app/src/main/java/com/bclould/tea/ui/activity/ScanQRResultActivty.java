package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ScanQRResultActivty extends AppCompatActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrresult_activty);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
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
