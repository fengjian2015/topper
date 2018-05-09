package com.bclould.tocotalk.ui.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.InOutInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/4.
 */

public class InOutDataRVAapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<InOutInfo.DataBean> mData;
    private final int mType;
    private final String mCoinName;

    public InOutDataRVAapter(Context context, List<InOutInfo.DataBean> data, int type, String coinName) {
        mContext = context;
        mData = data;
        mType = type;
        mCoinName = coinName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_in_out_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData.size() != 0) {
            return mData.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.type_name)
        TextView mTypeName;
        @Bind(R.id.tv_reailty_income)
        TextView mTvReailtyIncome;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.tv_reality_transfer)
        TextView mTvRealityTransfer;
        @Bind(R.id.text_id)
        TextView mTextId;
        @Bind(R.id.tv_copy)
        TextView mTvCopy;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(InOutInfo.DataBean dataBean) {
            mTypeName.setText(dataBean.getUser_name());
            mTextId.setText(dataBean.getTxid());
            mTvCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(mTextId.getText());
                    Toast.makeText(mContext, mContext.getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
                }
            });
            mTime.setText(dataBean.getCreated_at());
            if (mType == 0) {
                mTvRealityTransfer.setText(mContext.getString(R.string.shiji_in_coin) + " " + dataBean.getNumber() + " " + mCoinName);
                mTvReailtyIncome.setText(mContext.getString(R.string.shiji_out_coin) + " " + dataBean.getNumber_u() + " " + mCoinName);
            } else {

                mTvRealityTransfer.setText(mContext.getString(R.string.shiji_in_coin2) + " " + dataBean.getNumber() + " " + mCoinName);
                mTvReailtyIncome.setText(mContext.getString(R.string.shiji_out_coin) + " " + dataBean.getNumber_u() + " " + mCoinName);
            }
        }
    }
}
