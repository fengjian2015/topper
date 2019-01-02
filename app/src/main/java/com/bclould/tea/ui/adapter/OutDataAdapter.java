package com.bclould.tea.ui.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.InOutInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fengjian on 2019/1/2.
 */

public class OutDataAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<InOutInfo.DataBean> mData;
    private final String mCoinName;

    public OutDataAdapter(Context context, List<InOutInfo.DataBean> data, String coinName) {
        mContext = context;
        mData = data;
        mCoinName = coinName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_out_record, parent, false);
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
        @Bind(R.id.tv_state)
        TextView mTvState;
        @Bind(R.id.tv_out_money)
        TextView mTvOutMoney;
        @Bind(R.id.tv_miner)
        TextView mTvMiner;
        @Bind(R.id.tv_block)
        TextView mTvBlock;
        @Bind(R.id.tv_address)
        TextView mTvAddress;
        @Bind(R.id.tv_time)
        TextView mTvTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(InOutInfo.DataBean dataBean) {
            String copy = mContext.getResources().getString(R.string.copy);
            String block = dataBean.getTxid()+" "+ copy;
            String address=dataBean.getAddress()+" "+copy;
            SpannableString spanblock = new SpannableString(block);
            ClickableSpan clickblock = new Copy(dataBean.getTxid());
            spanblock.setSpan(clickblock, block.length() - copy.length(), block.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvBlock.setText(spanblock);
            mTvBlock.setMovementMethod(LinkMovementMethod.getInstance());

            SpannableString spanaddress= new SpannableString(address);
            ClickableSpan clickaddress = new Copy(dataBean.getAddress());
            spanaddress.setSpan(clickaddress, spanaddress.length() - copy.length(), spanaddress.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvAddress.setText(spanaddress);
            mTvAddress.setMovementMethod(LinkMovementMethod.getInstance());

            mTvTime.setText(dataBean.getCreated_at());
            mTvState.setText(dataBean.getStatus());
            mTvMiner.setText(dataBean.getFee());
            mTvOutMoney.setText("-"+dataBean.getNumber()+" "+mCoinName);
        }
    }

    class Copy extends ClickableSpan {
        private String copyContent;
        public Copy(String content){
            copyContent=content;
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(mContext.getResources().getColor(R.color.btn_bg_color));
        }

        @Override
        public void onClick(View widget) {
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(copyContent);
            Toast.makeText(mContext, mContext.getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
        }
    }
}
