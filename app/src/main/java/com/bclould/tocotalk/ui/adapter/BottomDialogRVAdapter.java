package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.PushBuyingActivity;
import com.bclould.tocotalk.ui.activity.StartGuessActivity;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/11/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BottomDialogRVAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<String> mArr;
    private int mSign;
    private CallBack mCallBack;
    private Map<String, Integer> mMap;
    private Map<String, Boolean> mModeOfPayment;

    public BottomDialogRVAdapter(Context context, Map<String, Boolean> modeOfPayment, List<String> arr, int sign, CallBack callBack, Map<String, Integer> map) {
        mContext = context;
        mSign = sign;
        mArr = arr;
        mCallBack = callBack;
        mMap = map;
        mModeOfPayment = modeOfPayment;
    }

    public BottomDialogRVAdapter(Context context, List<String> timeList, int sign) {
        mContext = context;
        mArr = timeList;
        mSign = sign;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (mSign == 2 || mSign == 5) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_bottom_pay, parent, false);
            viewHolder = new ViewHolder2(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_bottom, parent, false);
            viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mSign == 2 || mSign == 5) {
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.setData(mArr.get(position), position);
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setData(mArr.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return mArr.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_coin_logo)
        ImageView mTvCoinLogo;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_remaining)
        TextView mTvRemaining;
        private String mName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mContext instanceof PushBuyingActivity) {
                        PushBuyingActivity activity = (PushBuyingActivity) mContext;
                        activity.hideDialog(mName, mSign);
                    } else if (mContext instanceof StartGuessActivity) {
                        StartGuessActivity activity = (StartGuessActivity) mContext;
                        activity.hideDialog(mName, mSign);
                    }

                }
            });
        }

        public void setData(String name) {
            mName = name;
            mTvName.setText(name);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.cb_pay)
        CheckBox mCbPay;
        @Bind(R.id.tv_name)
        TextView mTvName;
        boolean isChecked = false;
        private String mName;
        private int mPosition;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mModeOfPayment.get(mName)) {
                        isChecked = !isChecked;
                        if (isChecked) {
                            mCbPay.setChecked(true);
                        } else {
                            mCbPay.setChecked(false);
                        }
                        mCallBack.send(mName, isChecked, mPosition);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.please_bind) + mName, Toast.LENGTH_SHORT).show();
                    }
                }/* else {
                        isChecked = !isChecked;
                        if (isChecked) {
                            mCbPay.setChecked(true);
                        } else {
                            mCbPay.setChecked(false);
                        }
                        mCallBack.send(mName, isChecked, mPosition);
                    }*/

            });
        }

        public void setData(String name, int position) {
            mName = name;
            if (mModeOfPayment.get(name)) {
                mTvName.setText(name);
            } else {
                mTvName.setText(name + "(" + mContext.getString(R.string.not_bound) + ")");
            }
            mPosition = position;
            for (String key : mMap.keySet()) {
                if (key.equals(name)) {
                    mCbPay.setChecked(true);
                    isChecked = true;
                }
            }
        }
    }

    //定义接口
    public interface CallBack {
        void send(String name, boolean isChecked, int position);
    }
}
