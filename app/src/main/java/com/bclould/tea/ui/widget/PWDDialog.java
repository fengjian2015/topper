package com.bclould.tea.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GIjia on 2018/7/20.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class PWDDialog {
    private Activity context;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private MNPasswordEditText mEtPassword;
    private Dialog mRedDialog;
    private GridView mGridView;
    private OnPWDresult onPWDresult;

    public PWDDialog(Activity context) {
        this.context=context;
    }

    public void showDialog(String count,String coins,String desc,String imageurl,String hint){
        mEnterAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_enter);
        mExitAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_exit);
        mRedDialog = new Dialog(context, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_passwrod, null);
        //获得dialog的window窗口
        Window window = mRedDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(BottomDialog);
        mRedDialog.setContentView(contentView);
        mRedDialog.show();
        mRedDialog.setCanceledOnTouchOutside(false);
        initDialog(count,coins,desc,imageurl,hint);
    }

    public void show(){
        mRedDialog.show();
    }


    private void initDialog(String count,String coins,String desc,String imageurl,String hint) {
        TextView coin = (TextView) mRedDialog.findViewById(R.id.tv_coin);
        TextView tvcount = (TextView) mRedDialog.findViewById(R.id.tv_count);
        TextView tvDesc=mRedDialog.findViewById(R.id.tv_desc);
        TextView tvHint=mRedDialog.findViewById(R.id.tv_hint);
        ImageView image_logo=mRedDialog.findViewById(R.id.image_logo);
        RelativeLayout rl_coin=mRedDialog.findViewById(R.id.rl_coin);
        mEtPassword = (MNPasswordEditText) mRedDialog.findViewById(R.id.et_password);

        if(!StringUtils.isEmpty(hint)){
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(hint);
        }
        if(StringUtils.isEmpty(coins)){
            rl_coin.setVisibility(View.GONE);
        }
        if(StringUtils.isEmpty(desc)){
            tvDesc.setVisibility(View.GONE);
        }

        Glide.with(context).load(imageurl).into(image_logo);
        tvcount.setText(UtilTool.removeZero(count + ""));
        coin.setText(coins);
        tvDesc.setText(desc);

        // 设置不调用系统键盘
        if (Build.VERSION.SDK_INT <= 10) {
            mEtPassword.setInputType(InputType.TYPE_NULL);
        } else {
            context.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEtPassword, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final VirtualKeyboardView virtualKeyboardView = (VirtualKeyboardView) mRedDialog.findViewById(R.id.virtualKeyboardView);
        ImageView bark = (ImageView) mRedDialog.findViewById(R.id.bark);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRedDialog.dismiss();
                mEtPassword.setText("");
            }
        });
        valueList = virtualKeyboardView.getValueList();

        virtualKeyboardView.getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.startAnimation(mExitAnim);
                virtualKeyboardView.setVisibility(View.GONE);
            }
        });
        mGridView = virtualKeyboardView.getGridView();
        mGridView.setOnItemClickListener(onItemClickListener);
        mEtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.setFocusable(true);
                virtualKeyboardView.setFocusableInTouchMode(true);
                virtualKeyboardView.startAnimation(mEnterAnim);
                virtualKeyboardView.setVisibility(View.VISIBLE);
            }
        });

        mEtPassword.setOnPasswordChangeListener(new MNPasswordEditText.OnPasswordChangeListener() {
            @Override
            public void onPasswordChange(String password) {
                if (password.length() == 6) {
                    mRedDialog.dismiss();
                    mEtPassword.setText("");
                    if(onPWDresult!=null){
                        onPWDresult.success(password);
                    }
                }
            }
        });
    }

    private ArrayList<Map<String, String>> valueList;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            if (position < 11 && position != 9) {    //点击0~9按钮

                String amount = mEtPassword.getText().toString().trim();
                amount = amount + valueList.get(position).get("name");

                mEtPassword.setText(amount);

                Editable ea = mEtPassword.getText();
                mEtPassword.setSelection(ea.length());
            } else {

                if (position == 9) {      //点击退格键
                    String amount = mEtPassword.getText().toString().trim();
                    if (!amount.contains(".")) {
                        amount = amount + valueList.get(position).get("name");
                        mEtPassword.setText(amount);

                        Editable ea = mEtPassword.getText();
                        mEtPassword.setSelection(ea.length());
                    }
                }

                if (position == 11) {      //点击退格键
                    String amount = mEtPassword.getText().toString().trim();
                    if (amount.length() > 0) {
                        amount = amount.substring(0, amount.length() - 1);
                        mEtPassword.setText(amount);

                        Editable ea = mEtPassword.getText();
                        mEtPassword.setSelection(ea.length());
                    }
                }
            }
        }
    };

    public void setOnPWDresult(OnPWDresult onPWDresult){
        this.onPWDresult=onPWDresult;
    }

    public interface OnPWDresult{
        void success(String password);
    }
}
