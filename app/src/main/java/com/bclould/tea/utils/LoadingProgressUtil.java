package com.bclould.tea.utils;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.ui.widget.LoadingProgressDialog;

/**
 * Created by GIjia on 2018/9/11.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class LoadingProgressUtil {
    private LoadingProgressDialog mProgressDialog;
    private Activity mActivity;

    public LoadingProgressUtil(Activity activity){
        mActivity=activity;
    }

    public void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mActivity);
        }
        mProgressDialog.showDialog();
    }

    public void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.hideDialog();
            mProgressDialog = null;
        }
    }
}
