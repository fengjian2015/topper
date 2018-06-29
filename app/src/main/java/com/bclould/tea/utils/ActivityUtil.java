package com.bclould.tea.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;

public class ActivityUtil {
	@SuppressLint("NewApi")
	public static boolean isActivityOnTop(Activity act) {
		if (act != null) {
			if (Build.VERSION.SDK_INT >= 17) {
                if (act.isDestroyed() || act.isFinishing()) {
                    return false;
                }
            } else {
                if (act.isFinishing()) {
                    return false;
                }
            }
		}else {
			return false;
		}
		return true;
	}
}
