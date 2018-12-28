package com.bclould.tea.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.utils.AppLanguageUtils;

/**
 * Created by GIjia on 2018/6/29.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginBaseActivity extends SwipeActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.getInstance().mActivityList.remove(this);
    }
}
