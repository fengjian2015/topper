package com.bclould.tocotalk.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tocotalk.ui.fragment.OrderFormFragment;
import com.bclould.tocotalk.ui.fragment.SellFragment;
import com.bclould.tocotalk.ui.fragment.BuyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2017/9/20.
 */

public class CloudCircleVPAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public CloudCircleVPAdapter(FragmentManager fm) {
        super(fm);

        mFragmentList.add(new BuyFragment());
        mFragmentList.add(new SellFragment());
        mFragmentList.add(new OrderFormFragment());

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
