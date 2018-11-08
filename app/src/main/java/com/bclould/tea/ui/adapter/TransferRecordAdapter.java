package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.TransferRecordInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class TransferRecordAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<TransferRecordInfo.DataBean> mList;
    private OnclickListener mOnclickListener;
    private String coin_name;

    public TransferRecordAdapter(Context context, List<TransferRecordInfo.DataBean> list, String coin_name) {
        mContext = context;
        mList = list;
        this.coin_name=coin_name;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transfer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mList.size() != 0) {
            return mList.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;

        private int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(TransferRecordInfo.DataBean hashMap, int position) {
            this.position = position;
            mTvTime.setText(hashMap.getCreated_at());
            if(hashMap.getType()==1){
                mTvTitle.setText(mContext.getString(R.string.shift_to));
                mTvMoney.setText("+"+hashMap.getNumber()+coin_name);
                mTvMoney.setTextColor(mContext.getResources().getColor(R.color.btn_bg_color));
            }else{
                mTvTitle.setText(mContext.getString(R.string.transfer_out));
                mTvMoney.setText("-"+hashMap.getNumber()+coin_name);
                mTvMoney.setTextColor(mContext.getResources().getColor(R.color.red_color));
            }
        }
    }

    public void setOnClickListener(OnclickListener onClickListener) {
        this.mOnclickListener = onClickListener;
    }

    public interface OnclickListener {
        void onclick(int position);
    }
}
