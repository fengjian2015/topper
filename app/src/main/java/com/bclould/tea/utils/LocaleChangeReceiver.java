package com.bclould.tea.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import java.util.Locale;
import static com.bclould.tea.Presenter.LoginPresenter.STATE;

/**
 * Created by GA on 2017/11/10.
 */

public class LocaleChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            if (MySharedPreferences.getInstance().getString(context.getString(R.string.language_pref_key)).isEmpty()) {
                Locale locale = context.getResources().getConfiguration().locale;
                String language = (locale.getLanguage() + "-" + locale.getCountry()).toLowerCase();
                UtilTool.Log("語言", "监听到系统语言切换===" + language);
                new LoginPresenter(context).postLanguage(language, new LoginPresenter.CallBack3() {
                    @Override
                    public void send(String currency) {
                        MyApp.getInstance().RestartApp();
                        if (!currency.isEmpty()) {
                            MySharedPreferences.getInstance().setString(STATE, currency);
                        }
                    }
                });
            }
        }
    }
}
