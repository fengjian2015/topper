package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.service.IMService;
import com.bclould.tea.topperchat.SocketListener;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.xmpp.RoomManage;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/7/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectorLanguageActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.cb_system)
    CheckBox mCbSystem;
    @Bind(R.id.rl_follow_system)
    RelativeLayout mRlFollowSystem;
    @Bind(R.id.cb_simplified)
    CheckBox mCbSimplified;
    @Bind(R.id.rl_simplified_chinese)
    RelativeLayout mRlSimplifiedChinese;
    @Bind(R.id.cb_traditional)
    CheckBox mCbTraditional;
    @Bind(R.id.rl_chinese_traditional)
    RelativeLayout mRlChineseTraditional;
    private String mLanguageKind = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_language);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        MySharedPreferences.getInstance().getSp().registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        init();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void init() {

        mLanguageKind = MySharedPreferences.getInstance().getString(getString(R.string.language_pref_key));
        if (mLanguageKind.isEmpty()) {
            mCbSystem.setChecked(true);
            mCbSimplified.setChecked(false);
            mCbTraditional.setChecked(false);
        } else if ("zh".equals(mLanguageKind)) {
            mCbSystem.setChecked(false);
            mCbSimplified.setChecked(true);
            mCbTraditional.setChecked(false);
        } else if ("zh-hant".equals(mLanguageKind)) {
            mCbSystem.setChecked(false);
            mCbSimplified.setChecked(false);
            mCbTraditional.setChecked(true);
        }
    }


    @OnClick({R.id.bark, R.id.rl_follow_system, R.id.rl_simplified_chinese, R.id.rl_chinese_traditional, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_save:
                saveSettings();
                break;
            case R.id.rl_follow_system:
                mLanguageKind = "";
                mCbSystem.setChecked(true);
                mCbSimplified.setChecked(false);
                mCbTraditional.setChecked(false);
                break;
            case R.id.rl_simplified_chinese:
                mLanguageKind = "zh";
                mCbSystem.setChecked(false);
                mCbSimplified.setChecked(true);
                mCbTraditional.setChecked(false);
                break;
            case R.id.rl_chinese_traditional:
                mLanguageKind = "zh-hant";
                mCbSystem.setChecked(false);
                mCbSimplified.setChecked(false);
                mCbTraditional.setChecked(true);
                break;
        }
    }

    private void saveSettings() {
        String language = null;
        if (mLanguageKind.equals("zh-hant")) {
            language = "zh-hk";
        } else if (mLanguageKind.equals("zh")) {
            language = "zh-cn";
        } else if (mLanguageKind.equals("")) {
            Locale locale = getResources().getConfiguration().locale;
            language = (locale.getLanguage() + "-" + locale.getCountry()).toLowerCase();
        }
        new LoginPresenter(this).postLanguage(language, new LoginPresenter.CallBack3() {
            @Override
            public void send() {
                MySharedPreferences.getInstance().setString(getString(R.string.language_pref_key), mLanguageKind);
            }
        });
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (getString(R.string.language_pref_key).equals(key)) {
                String language = MySharedPreferences.getInstance().getString(getString(R.string.language_pref_key));
                onChangeAppLanguage(language.toString());
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private void onChangeAppLanguage(String newLanguage) {
        AppLanguageUtils.changeAppLanguage(this, newLanguage);
        AppLanguageUtils.changeAppLanguage(MyApp.getInstance().app(), newLanguage);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        /*new Handler() {
            public void handleMessage(Message msg) {
                Intent intent = getPackageManager()
                        .getLaunchIntentForPackage(getApplication().getPackageName());
                PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 200, restartIntent);
                System.exit(0);
            }
        }.sendEmptyMessageDelayed(0, 3000);*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySharedPreferences.getInstance().getSp().unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }
}
