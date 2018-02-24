package com.dashiji.biyun.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dashiji.biyun.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/12.
 */

public class DealRecordRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;

    public DealRecordRVAdapter(Context context) {

        mContext = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_deal_record, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 15;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_transfer)
        TextView mCurrencyTransfer;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.money)
        TextView mMoney;
        @Bind(R.id.type)
        TextView mType;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
