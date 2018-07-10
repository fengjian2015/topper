package com.bclould.tea.base;

/**
 * Created by GIjia on 2018/6/29.
 */

public class LoginBaseActivity extends SwipeActivity {

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
