package com.bclould.tocotalk.ui.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class CloudCircleVPAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public CloudCircleVPAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.add(BuyFragment.getInstance());
        mFragmentList.add(SellFragment.getInstance());
        mFragmentList.add(OrderFormFragment.getInstance());

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
