package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.TransferListInfo;
import com.bclould.tocotalk.ui.activity.PayDetailsActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayRecordRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<TransferListInfo.DataBean> mDataBeanList;

    public PayRecordRVAdapter(Context context, List<TransferListInfo.DataBean> dataBeanList) {
        mContext = context;
        mDataBeanList = dataBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pay_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataBeanList.size() != 0) {
            return mDataBeanList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_photo)
        ImageView mIvPhoto;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        private TransferListInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayDetailsActivity.class);
                    intent.putExtra("id", mDataBean.getLog_id() + "");
                    intent.putExtra("type_number", mDataBean.getType_number() + "");
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(TransferListInfo.DataBean dataBean) {
            mDataBean = dataBean;
            mTvName.setText(dataBean.getType());
            mTvType.setText(dataBean.getType_desc());
            mTvTime.setText(dataBean.getCreated_at());
            if (dataBean.getNumber().contains("-")) {
                mTvMoney.setText(dataBean.getNumber() + " " + dataBean.getCoin_name());
                mTvMoney.setTextColor(mContext.getColor(R.color.green));
            } else {
                mTvMoney.setText("+" + dataBean.getNumber() + " " + dataBean.getCoin_name());
                mTvMoney.setTextColor(mContext.getColor(R.color.red));
            }
        }
    }
}
