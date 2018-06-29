package com.bclould.tea.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by GIjia on 2018/6/29.
 */

public class LoginBaseActivity extends AppCompatActivity {

    @Override
    public void onResume() {
        super.onResume();
//        UmManage.getInstance().mobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        UmManage.getInstance().mobclickAgent.onPause(this);
    }
}
