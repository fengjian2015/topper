package com.bclould.tea.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by GA on 2018/7/26.
 */

@TargetApi(Build.VERSION_CODES.N)
public class AppLanguageUtils {
    private static HashMap<String, Locale> mAllLanguages = new HashMap<String, Locale>(3) {{
        put(Constants.SIMPLIFIED_CHINESE, Locale.SIMPLIFIED_CHINESE);
        put(Constants.TRADITIONAL_CHINESE, Locale.TRADITIONAL_CHINESE);
        put(Constants.ENGLISH, Locale.ENGLISH);
    }};

    @SuppressWarnings("deprecation")
    public static void changeAppLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        // app locale
        Locale locale = null;
        if (!newLanguage.isEmpty()) {
            if (newLanguage.equals("zh-cn")) {
                locale = Locale.SIMPLIFIED_CHINESE;
            } else if (newLanguage.equals("zh-hk")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if (newLanguage.equals("en")) {
                locale = Locale.ENGLISH;
            } else {
                locale = Locale.SIMPLIFIED_CHINESE;
            }
        } else {
            locale = new Locale(MySharedPreferences.getInstance().getString(Constants.LANGUAGE), MySharedPreferences.getInstance().getString(Constants.COUNTRY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        // updateConfiguration
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }


    private static boolean isSupportLanguage(String language) {
        return mAllLanguages.containsKey(language);
    }

    public static String getSupportLanguage(String language) {
        if (isSupportLanguage(language)) {
            return language;
        }

        return "";
    }

    /**
     * 获取指定语言的locale信息，如果指定语言不存在{@link #mAllLanguages}，返回本机语言，如果本机语言不是语言集合中的一种{@link #mAllLanguages}，返回英语
     *
     * @param language language
     * @return
     */
    public static Locale getLocaleByLanguage(String language) {
        if (isSupportLanguage(language)) {
            return mAllLanguages.get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : mAllLanguages.keySet()) {
                if (TextUtils.equals(mAllLanguages.get(key).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.CHINESE;
    }


    public static Context attachBaseContext(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }


    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = null;
        if (language.equals("zh-cn")) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals("zh-hk")) {
            locale = Locale.TRADITIONAL_CHINESE;
        } else if (language.equals("en")) {
            locale = Locale.ENGLISH;
        } else {
            locale = new Locale(MySharedPreferences.getInstance().getString(Constants.LANGUAGE), MySharedPreferences.getInstance().getString(Constants.COUNTRY));
        }
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return context;
    }
}
