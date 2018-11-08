package com.bclould.tea.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bclould.tea.R;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/7/30.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ManagementFundsDialog extends Dialog {


    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.et_money)
    EditText mEtMoney;
    @Bind(R.id.tv_conin)
    TextView mTvConin;
    @Bind(R.id.tv_balance)
    TextView mTvBalance;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    private OnClickListener onClickListener;

    private Context mContext;
    private String balance;

    public ManagementFundsDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mContext=context;
    }

    public ManagementFundsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext=context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_management_funds);
        ButterKnife.bind(this);
        init();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(String number);
    }


    private void init() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String number = mEtMoney.getText().toString();
                if(StringUtils.isEmpty(number)){
                    ToastShow.showToast((Activity) mContext,mContext.getString(R.string.pless_input_money));
                    return;
                }
                if(UtilTool.parseDouble(number)>UtilTool.parseDouble(balance)){
                    ToastShow.showToast((Activity) mContext,mContext.getString(R.string.insufficient_balance_available));
                    return;
                }
                if(onClickListener!=null){
                    dismiss();
                    onClickListener.onClick(mEtMoney.getText().toString());
                }
            }
        });
    }

    /**
     *
     * @param type 1转入 2转出
     * @param coinName
     * @param image
     * @param balance
     */
    public void setContent(int type,String coinName,String image,String balance){
        this.balance=balance;
        Glide.with(mContext).load(image).into(mImage);
        if(type==1){
            mTvTitle.setText(coinName+mContext.getString(R.string.shift_to));
        }else{
            mTvTitle.setText(coinName+mContext.getString(R.string.transfer_out));
        }
        mTvConin.setText(coinName);
        mTvBalance.setText(balance+coinName);
    }
}
