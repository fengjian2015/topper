package com.dashiji.biyun.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.ui.activity.SendQRCodeRedActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/28.
 */

public class BottomDialogRVAdapter3 extends RecyclerView.Adapter {

    private final SendQRCodeRedActivity mSendRedPacketActivity;
    private final String[] mCoinArr;

    public BottomDialogRVAdapter3(SendQRCodeRedActivity sendRedPacketActivity, String[] coinArr) {
        mSendRedPacketActivity = sendRedPacketActivity;
        mCoinArr = coinArr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mSendRedPacketActivity).inflate(R.layout.item_dialog_bottom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mCoinArr[position]);
    }

    @Override
    public int getItemCount() {
        return mCoinArr.length;
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
                    mSendRedPacketActivity.hideDialog(mName);
                }
            });
        }

        public void setData(String name) {
            mName = name;
            mTvName.setText(name);
        }
    }
}
