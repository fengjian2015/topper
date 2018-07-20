package com.bclould.tea.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;

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
        init();
    }

    private void init() {
        mLanguageKind = MySharedPreferences.getInstance().getString(Constants.LANGUAGE);
        if (mLanguageKind.isEmpty()) {
            mCbSystem.setChecked(true);
            mCbSimplified.setChecked(false);
            mCbTraditional.setChecked(false);
        } else if ("zh-cn".equals(mLanguageKind)) {
            mCbSystem.setChecked(false);
            mCbSimplified.setChecked(true);
            mCbTraditional.setChecked(false);
        } else if ("zh-hk".equals(mLanguageKind)) {
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
                changeLanguage();
                break;
            case R.id.rl_follow_system:
                mLanguageKind = "";
                mCbSystem.setChecked(true);
                mCbSimplified.setChecked(false);
                mCbTraditional.setChecked(false);
                break;
            case R.id.rl_simplified_chinese:
                mLanguageKind = "zh-cn";
                mCbSystem.setChecked(false);
                mCbSimplified.setChecked(true);
                mCbTraditional.setChecked(false);
                break;
            case R.id.rl_chinese_traditional:
                mLanguageKind = "zh-hk";
                mCbSystem.setChecked(false);
                mCbSimplified.setChecked(false);
                mCbTraditional.setChecked(true);
                break;
        }
    }


    private void changeLanguage() {
        new LoginPresenter(this).postLanguage(mLanguageKind, new LoginPresenter.CallBack3() {
            @Override
            public void send() {
                finish();
                Resources resources = getResources();
                Configuration configuration = resources.getConfiguration();

                Locale locale = new Locale("values-zh-rHK");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    configuration.setLocale(locale);
                } else {
                    configuration.locale = locale;
                }
                DisplayMetrics dm = resources.getDisplayMetrics();
                resources.updateConfiguration(configuration, dm);
                MySharedPreferences.getInstance().setString(Constants.LANGUAGE, mLanguageKind);
            }
        });
    }
}
