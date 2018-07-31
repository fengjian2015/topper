package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.CardInfo;
import com.bclould.tea.ui.activity.BankCardActivity;
import com.bclould.tea.ui.activity.MyAssetsActivity;
import com.bclould.tea.ui.activity.ReceiptPaymentActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/7/31.
 */

public class WalletPVAdapter extends PagerAdapter implements CardAdapter {

    private final Context mContext;
    private final List<CardView> mViews;
    private final List<CardInfo> mDataList;
    private float mBaseElevation;

    public WalletPVAdapter(Context context, List<CardView> views, List<CardInfo> dataList) {
        mContext = context;
        mViews = views;
        mDataList = dataList;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_card, container, false);
        container.addView(view);
        new ViewHolder(view, position, mDataList);
        CardView cardView = (CardView) view.findViewById(R.id.bank_card);
        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);

        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    class ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView mIvIcon;
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.bank_card)
        CardView mBankCard;

        ViewHolder(View view, final int position, List<CardInfo> dataList) {
            ButterKnife.bind(this, view);
            mTvTitle.setText(dataList.get(position).getTitle());
            mIvIcon.setImageResource(dataList.get(position).getIcon());
            mTvCount.setText(position + 1 + "-" + dataList.size());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (position){
                        case 0:
                            mContext.startActivity(new Intent(mContext, ReceiptPaymentActivity.class));
                            break;
                        case 1:
                            mContext.startActivity(new Intent(mContext, MyAssetsActivity.class));
                            break;
                        case 2:
                            mContext.startActivity(new Intent(mContext, BankCardActivity.class));
                            break;
                    }
                }
            });
        }
    }
}
