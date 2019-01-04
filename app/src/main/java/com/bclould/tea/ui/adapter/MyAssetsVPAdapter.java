package com.bclould.tea.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tea.ui.fragment.BillDataFragment;
import com.bclould.tea.ui.fragment.InOutDataFragment;
import com.bclould.tea.ui.fragment.MyWalletFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2017/9/22.
 */

public class MyAssetsVPAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public MyAssetsVPAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.add(MyWalletFragment.getInstance());
        mFragmentList.add(BillDataFragment.getInstance());
        mFragmentList.add(InOutDataFragment.getInstance());
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
