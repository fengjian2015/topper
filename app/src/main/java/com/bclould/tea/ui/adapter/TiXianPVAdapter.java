package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.ui.activity.TiXianActivity;
import com.bclould.tea.model.CardBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static butterknife.ButterKnife.bind;

/**
 * Created by GA on 2017/10/16.
 */
public class TiXianPVAdapter extends PagerAdapter implements CardAdapter {

    private TiXianActivity mTiXianActivity;
    private Context mContext;
    private List<CardView> mViews;
    private List<CardBean> mData;
    private float mBaseElevation;

    public TiXianPVAdapter(TiXianActivity tiXianActivity) {
        mContext = tiXianActivity;
        mTiXianActivity = tiXianActivity;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardBean item) {
        mViews.add(null);
        mData.add(item);
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
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_ti_xian, container, false);
        container.addView(view);
        new ViewHolder(view, position, mData);
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
        @Bind(R.id.image)
        ImageView mImage;
        @Bind(R.id.bank_name)
        TextView mBankName;
        @Bind(R.id.bank_number)
        TextView mBankNumber;
        @Bind(R.id.bank_card)
        CardView mBankCard;

        ViewHolder(View view, int position, List<CardBean> data) {
            bind(this, view);

            if (position % 2 == 1) {

                mBankCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.red));

            }

            mTiXianActivity.setData(mData);

            mBankName.setText(data.get(position).getBackName());

            mBankNumber.setText(data.get(position).getBanckNumber());

        }
    }
}
