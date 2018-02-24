package com.dashiji.biyun.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashiji.biyun.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/26.
 */

public class BankCardRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;

    //构造方法
    public BankCardRVAdapter(Context context) {
        mContext = context;
    }

    //绑定item
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    //绑定ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.setData(position);

    }

    //条目数量
    @Override
    public int getItemCount() {
        return 6;
    }

    //ViewHolder控制条目
    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView mImage;
        @Bind(R.id.bank_name)
        TextView mBankName;
        @Bind(R.id.card_type)
        TextView mCardType;
        @Bind(R.id.bank_card_number)
        TextView mBankCardNumber;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(int position) {

        }
    }
}
