package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.TransferInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

public class BillDataRVAapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<TransferInfo.DataBean> mData;

    public BillDataRVAapter(Context context, List<TransferInfo.DataBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bill_data, parent, false);

        RecyclerView.ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData.size() == 0)
            return 0;
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.type_name)
        TextView mTypeName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.coin_count)
        TextView mCoinCount;
        private TransferInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

           /* view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BillDetailsActivity.class);
                    if (mDataBean.getDesc().equals("转入")) {
                        intent.putExtra("name", mDataBean.getSend_name());
                        intent.putExtra("type", true);
                    } else {
                        intent.putExtra("name", mDataBean.getReceive_name());
                        intent.putExtra("type", false);
                    }
                    intent.putExtra("time", mDataBean.getCreated_at());
                    intent.putExtra("coin", mDataBean.getCoin_name());
                    intent.putExtra("coinCount", mDataBean.getNumber());
                    intent.putExtra("id", mDataBean.getTo_id());
                    mContext.startActivity(intent);
                }
            });*/

        }

        public void setData(TransferInfo.DataBean dataBean) {
            mDataBean = dataBean;
            if (dataBean.getDesc().equals(mContext.getString(R.string.shift_to))) {
                mTypeName.setText(dataBean.getSend_name());
                mCoinCount.setText("+" + dataBean.getNumber() + " " + dataBean.getCoin_name());
                mCoinCount.setTextColor(mContext.getResources().getColor(R.color.blue2));
            } else {
                mTypeName.setText(dataBean.getReceive_name());
                mCoinCount.setText("-" + dataBean.getNumber() + " " + dataBean.getCoin_name());
                mCoinCount.setTextColor(mContext.getResources().getColor(R.color.red));
            }
            mTime.setText(dataBean.getCreated_at());
        }
    }


}
