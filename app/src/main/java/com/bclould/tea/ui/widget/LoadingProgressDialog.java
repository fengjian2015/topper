package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;

import com.bclould.tea.R;

/**
 * Created by GA on 2017/11/15.
 */

public class LoadingProgressDialog extends Dialog {
    private Context context = null;
    private static LoadingProgressDialog sLoadingProgressDialog = null;

    public LoadingProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static LoadingProgressDialog createDialog(Context context) {
        sLoadingProgressDialog = new LoadingProgressDialog(context, R.style.LoadingProgressDialog2);
        sLoadingProgressDialog.setContentView(R.layout.dialog_loading_progress);
        sLoadingProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return sLoadingProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        if (sLoadingProgressDialog == null) {
            return;
        }

        ImageView imageView = (ImageView) sLoadingProgressDialog.findViewById(R.id.iv_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    /**
     * [Summary]
     * setTitile 标题
     *
     * @param strTitle
     * @return
     */
    public LoadingProgressDialog setTitile(String strTitle) {
        return sLoadingProgressDialog;
    }

    /**
     * [Summary]
     * setMessage 提示内容
     *
     * @param strMessage
     * @return
     */
    public LoadingProgressDialog setMessage(String strMessage) {
        /*TextView tvMsg = (TextView) sLoadingProgressDialog.findViewById(R.id.tv_loading);

        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
*/
        return sLoadingProgressDialog;
    }
}