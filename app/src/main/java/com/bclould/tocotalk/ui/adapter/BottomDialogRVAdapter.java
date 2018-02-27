package com.bclould.tocotalk.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.PushBuyingActivity;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/11/22.
 */

public class BottomDialogRVAdapter extends RecyclerView.Adapter {

    private final PushBuyingActivity mPushBuyingActivity;
    private final String[] mArr;
    private final int mSign;
    private final CallBack mCallBack;
    private final Map<String, Integer> mMap;

    public BottomDialogRVAdapter(PushBuyingActivity pushBuyingActivity, String[] arr, int sign, CallBack callBack, Map<String, Integer> map) {
        mPushBuyingActivity = pushBuyingActivity;
        mSign = sign;
        mArr = arr;
        mCallBack = callBack;
        mMap = map;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (mSign == 2) {
            view = LayoutInflater.from(mPushBuyingActivity).inflate(R.layout.item_dialog_bottom_pay, parent, false);
            viewHolder = new ViewHolder2(view);
        } else {
            view = LayoutInflater.from(mPushBuyingActivity).inflate(R.layout.item_dialog_bottom, parent, false);
            viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mSign == 2) {
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.setData(mArr[position], position);
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setData(mArr[position]);
        }

    }

    @Override
    public int getItemCount() {
        return mArr.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        private String mName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPushBuyingActivity.hideDialog(mName, mSign);

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
                    isChecked = !isChecked;
                    if (isChecked) {
                        mCbPay.setChecked(true);
                    } else {
                        mCbPay.setChecked(false);
                    }
                    mCallBack.send(mName, isChecked, mPosition);
                }
            });
        }

        public void setData(String name, int position) {
            mName = name;
            mTvName.setText(name);
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
